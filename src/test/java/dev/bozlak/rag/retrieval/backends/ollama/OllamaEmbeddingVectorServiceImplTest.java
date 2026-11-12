package dev.bozlak.rag.retrieval.backends.ollama;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dev.bozlak.rag.retrieval.EmbeddingModel;

/**
 * Tests the service against a fake Ollama server on localhost.
 * Ollama does not need to run for these tests.
 */
class OllamaEmbeddingVectorServiceImplTest {

    private static final String OK_BODY = "{\"model\":\"bge-m3:567m\",\"embeddings\":[[0.1,-0.2,0.3]]}";
    private static final EmbeddingModel MODEL = EmbeddingModel.BGE_M3_567M;

    private HttpServer server;
    private HttpClient httpClient;
    private OllamaEmbeddingVectorServiceImpl service;
    private String apiUrl;

    // The answer of the fake server
    private volatile int responseStatus;
    private volatile String responseBody;

    // The request that the fake server got
    private volatile String requestMethod;
    private volatile String requestContentType;
    private volatile String requestBody;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(OllamaDefaultPropertiesForRetrieval.API_ENDPOINT, this::handle);
        server.start();

        apiUrl = "http://127.0.0.1:" + server.getAddress().getPort() + OllamaDefaultPropertiesForRetrieval.API_ENDPOINT;
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        service = new OllamaEmbeddingVectorServiceImpl(httpClient, new Gson());
    }

    @AfterEach
    void tearDown() {
        httpClient.close();
        server.stop(0);
    }

    @Test
    void getGeneratedEmbeddingVector_status200_returnsFirstVector() throws Exception {
        respond(200, OK_BODY);

        float[] vector = service.getGeneratedEmbeddingVector(MODEL, "hello", apiUrl);

        assertArrayEquals(new float[] {0.1f, -0.2f, 0.3f}, vector);
    }

    @Test
    void getGeneratedEmbeddingVector_sendsJsonPostWithModelAndSentence() throws Exception {
        respond(200, OK_BODY);

        service.getGeneratedEmbeddingVector(MODEL, "hello world", apiUrl);

        assertEquals("POST", requestMethod);
        assertEquals("application/json", requestContentType);

        JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
        assertEquals(MODEL.toString(), json.get("model").getAsString());
        assertEquals("hello world", json.getAsJsonArray("input").get(0).getAsString());
    }

    @Test
    void getGeneratedEmbeddingVector_withoutModel_usesDefaultModel() throws Exception {
        respond(200, OK_BODY);

        service.getGeneratedEmbeddingVector("hello", apiUrl);

        String sentModel = JsonParser.parseString(requestBody).getAsJsonObject().get("model").getAsString();
        assertEquals(EmbeddingModel.getDefaultEmbeddingModel().toString(), sentModel);
    }

    static Stream<Arguments> errorResponses() {
        return Stream.of(
                Arguments.of(404, "{\"error\":\"model \\\"bge-m3:567m\\\" not found, try pulling it first\"}"),
                Arguments.of(400, "{\"error\":\"input length exceeds the context length\"}"),
                Arguments.of(404, "404 page not found"),
                Arguments.of(502, "<html>Bad Gateway</html>"),
                Arguments.of(500, "")
        );
    }

    // The status code must be checked before the body is read as JSON.
    // If not, a body that is not JSON gives a Gson error and the real reason is lost.
    @ParameterizedTest
    @MethodSource("errorResponses")
    void getGeneratedEmbeddingVector_statusNot200_throwsWithStatusAndBody(int status, String body) {
        respond(status, body);

        EmbeddingVectorDidNotGenerateException exception = assertThrows(EmbeddingVectorDidNotGenerateException.class,
                () -> service.getGeneratedEmbeddingVector(MODEL, "hello", apiUrl));

        assertTrue(exception.getMessage().contains(String.valueOf(status)), exception.getMessage());
        assertTrue(exception.getMessage().contains(body), exception.getMessage());
    }

    @Test
    void getGeneratedEmbeddingVector_emptyEmbeddingsArray_throws() {
        respond(200, "{\"model\":\"bge-m3:567m\",\"embeddings\":[]}");

        assertThrows(EmbeddingVectorDidNotGenerateException.class,
                () -> service.getGeneratedEmbeddingVector(MODEL, "hello", apiUrl));
    }

    @Test
    void getGeneratedEmbeddingVector_noEmbeddingsField_throws() {
        respond(200, "{\"model\":\"bge-m3:567m\"}");

        assertThrows(EmbeddingVectorDidNotGenerateException.class,
                () -> service.getGeneratedEmbeddingVector(MODEL, "hello", apiUrl));
    }

    // The URL must be full, like http://localhost:11434/api/embed
    @Test
    void getGeneratedEmbeddingVector_onlyPathAsUrl_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getGeneratedEmbeddingVector(MODEL, "hello", OllamaDefaultPropertiesForRetrieval.API_ENDPOINT));
    }

    @Test
    void getGeneratedEmbeddingVector_nothingListensOnPort_throwsIOException() throws IOException {
        int freePort;
        try (ServerSocket socket = new ServerSocket(0)) {
            freePort = socket.getLocalPort();
        }
        String urlWithoutServer = "http://127.0.0.1:" + freePort + OllamaDefaultPropertiesForRetrieval.API_ENDPOINT;

        assertThrows(IOException.class, () -> service.getGeneratedEmbeddingVector(MODEL, "hello", urlWithoutServer));
    }

    private void respond(int status, String body) {
        this.responseStatus = status;
        this.responseBody = body;
    }

    private void handle(HttpExchange exchange) throws IOException {
        requestMethod = exchange.getRequestMethod();
        requestContentType = exchange.getRequestHeaders().getFirst("Content-Type");
        requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        if (bytes.length == 0) {
            exchange.sendResponseHeaders(responseStatus, -1);
        } else {
            exchange.sendResponseHeaders(responseStatus, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
        exchange.close();
    }
}

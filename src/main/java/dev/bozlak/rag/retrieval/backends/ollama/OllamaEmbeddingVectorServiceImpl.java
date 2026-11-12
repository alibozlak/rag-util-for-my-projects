package dev.bozlak.rag.retrieval.backends.ollama;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.bozlak.rag.retrieval.EmbeddingModel;

public class OllamaEmbeddingVectorServiceImpl implements OllamaEmbeddingVectorService {

    private final HttpClient httpClient;
    private final Gson gson;

    /**
     * Creates the Ollama embedding service.
     * <p>
     * Ollama must be running, and the model must be on your machine.
     * For the default model, run {@code ollama pull bge-m3:567m} first.
     * <p>
     * {@link HttpClient} and {@link Gson} are thread-safe.
     * Create the service one time and use it again and again.
     *
     * @param httpClient the client that sends the requests, for example {@code HttpClient.newHttpClient()}
     * @param gson the object that reads and writes JSON, for example {@code new Gson()}
     */
    public OllamaEmbeddingVectorServiceImpl(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Ollama does not cut long text, because {@code truncate} is {@code false}.
     * Too long text gives an error.
     * The request waits for an answer for up to 100 seconds.
     *
     * @throws EmbeddingVectorDidNotGenerateException if the Ollama status code is not 200
     *         (for example, the model is not pulled or the text is too long),
     *         or if the answer has no vector
     */
    @Override
    public float[] getGeneratedEmbeddingVector(EmbeddingModel embeddingModel, String sentence, String fullApiEndpoint)
            throws IOException, InterruptedException 
    {
        OllamaEmbeddingModelRequestDto ollamaEmbeddingModelRequestDto 
            = new OllamaEmbeddingModelRequestDto(embeddingModel, sentence);
        String jsonRequestBody = this.gson.toJson(ollamaEmbeddingModelRequestDto);

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(fullApiEndpoint))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
            .timeout(Duration.ofSeconds(100))
            .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200)
            throw new EmbeddingVectorDidNotGenerateException(httpResponse.statusCode(), httpResponse.body());

        JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("embeddings");

        if (jsonArray == null || jsonArray.isEmpty())
            throw new EmbeddingVectorDidNotGenerateException("Embedding backend returned a null or empty vector array");

        float[] embeddingVector = this.gson.fromJson(jsonArray.get(0), float[].class);
        return embeddingVector;
    }

    @Override
    public float[] getGeneratedEmbeddingVector(String sentence, String fullApiEndpoint) throws IOException, InterruptedException {
        return this.getGeneratedEmbeddingVector(EmbeddingModel.getDefaultEmbeddingModel(), sentence, fullApiEndpoint);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default URL is {@value OllamaDefaultPropertiesForRetrieval#API_FULL_ENDPOINT}.
     *
     * @throws EmbeddingVectorDidNotGenerateException if the Ollama status code is not 200,
     *         or if the answer has no vector
     */
    @Override
    public float[] getGeneratedEmbeddingVector(String sentence) throws IOException, InterruptedException {
        String apiDefaultEndpoint = OllamaDefaultPropertiesForRetrieval.API_FULL_ENDPOINT;
        return this.getGeneratedEmbeddingVector(sentence, apiDefaultEndpoint);
    }

}

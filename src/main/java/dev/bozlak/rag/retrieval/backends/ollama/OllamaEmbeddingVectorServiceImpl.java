package dev.bozlak.rag.retrieval.backends.ollama;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OllamaEmbeddingVectorServiceImpl implements OllamaEmbeddingVectorService {

    private final HttpClient httpClient;
    private final Gson gson;

    public OllamaEmbeddingVectorServiceImpl(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    @Override
    public float[] getGeneratedEmbeddingVector(String sentence, String apiEndpoint) throws IOException, InterruptedException {
        OllamaEmbeddingModelRequestDto ollamaEmbeddingModelRequestDto = new OllamaEmbeddingModelRequestDto(sentence);
        String jsonRequestBody = this.gson.toJson(ollamaEmbeddingModelRequestDto);

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiEndpoint))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
            .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("embeddings");

        if (jsonArray == null || jsonArray.isEmpty())
            throw new EmbeddingVectorDidNotGenerateException();

        float[] embeddingVector = this.gson.fromJson(jsonArray.get(0), float[].class);
        return embeddingVector;
    }

    @Override
    public float[] getGeneratedEmbeddingVector(String sentence) throws IOException, InterruptedException {
        String apiDefaultEndpoint = OllamaDefaultPropertiesForRetrieval.API_FULL_ENDPOINT;
        return this.getGeneratedEmbeddingVector(sentence, apiDefaultEndpoint);
    }
}

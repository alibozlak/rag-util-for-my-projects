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

    public OllamaEmbeddingVectorServiceImpl(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

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
            .timeout(Duration.ofSeconds(40))
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

    @Override
    public float[] getGeneratedEmbeddingVector(String sentence) throws IOException, InterruptedException {
        String apiDefaultEndpoint = OllamaDefaultPropertiesForRetrieval.API_FULL_ENDPOINT;
        return this.getGeneratedEmbeddingVector(sentence, apiDefaultEndpoint);
    }

}

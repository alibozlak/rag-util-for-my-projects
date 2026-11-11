package dev.bozlak.rag.retrieval.backends;

import dev.bozlak.rag.backends.OllamaDefaultProperties;
import dev.bozlak.rag.retrieval.EmbeddingModel;

public class OllamaDefaultPropertiesForRetrieval {

    public static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModel.BGE_M3_567M;
    public static final String API_ENDPOINT = "/api/embed";
    public static final String API_FULL_ENDPOINT = OllamaDefaultProperties.BASE_URL + API_ENDPOINT;

    public static final String NUM_CTX_NAME = "num_ctx";
}
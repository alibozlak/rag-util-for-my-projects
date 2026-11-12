package dev.bozlak.rag.retrieval.backends.ollama;

import dev.bozlak.rag.backends.ollama.OllamaDefaultProperties;

public class OllamaDefaultPropertiesForRetrieval {

    public static final boolean TRUNCATE = false;
    
    public static final String API_ENDPOINT = "/api/embed";
    public static final String API_FULL_ENDPOINT = OllamaDefaultProperties.BASE_URL + API_ENDPOINT;

    public static final String NUM_CTX_NAME = "num_ctx";
}
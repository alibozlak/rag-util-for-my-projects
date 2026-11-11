package dev.bozlak.rag.retrieval.backends;

import java.io.IOException;

public interface EmbeddingVectorService {
    
    float[] getGeneratedEmbeddingVector(String sentence, String apiEndpoint) throws IOException, InterruptedException;
    float[] getGeneratedEmbeddingVector(String sentence) throws IOException, InterruptedException;
}

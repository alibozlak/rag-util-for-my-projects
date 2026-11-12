package dev.bozlak.rag.retrieval.backends;

import java.io.IOException;

import dev.bozlak.rag.retrieval.EmbeddingModel;

public interface EmbeddingVectorService {
    
    float[] getGeneratedEmbeddingVector(EmbeddingModel embeddingModel, String sentence, String fullApiEndpoint) 
        throws IOException, InterruptedException;
    float[] getGeneratedEmbeddingVector(String sentence, String fullApiEndpoint) 
        throws IOException, InterruptedException;
    float[] getGeneratedEmbeddingVector(String sentence) 
        throws IOException, InterruptedException;
}

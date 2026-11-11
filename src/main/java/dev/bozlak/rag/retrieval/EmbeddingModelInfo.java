package dev.bozlak.rag.retrieval;

public class EmbeddingModelInfo {

    public final String modelName;
    public final int maxTokenContextLength;
    public final int embeddingLength ;
    public final String quantization;

    public EmbeddingModelInfo(
            String modelName, int maxTokenContextLength, int embeddingLength, String quantization
    ) {
        this.modelName = modelName;
        this.maxTokenContextLength = maxTokenContextLength;
        this.embeddingLength = embeddingLength;
        this.quantization = quantization;
    }
}

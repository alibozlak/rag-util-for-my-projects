package dev.bozlak.rag.retrieval.backends.ollama;

public class EmbeddingVectorDidNotGenerateException extends RuntimeException {
    public EmbeddingVectorDidNotGenerateException() {
        super("Embedding vector did not generate !!");
    }
}

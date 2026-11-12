package dev.bozlak.rag.retrieval.backends.ollama;

public class EmbeddingVectorDidNotGenerateException extends RuntimeException {
    public EmbeddingVectorDidNotGenerateException(String message) {
        super("Embedding vector did not generate !!\nMessage : " + message);
    }

    public EmbeddingVectorDidNotGenerateException(int statusCode, String body) {
        super("Embedding vector did not generate !!\nEmbedding backend status code : " + statusCode + "\nBody :" + body);
    }
}

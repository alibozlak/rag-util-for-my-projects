package dev.bozlak.rag.retrieval.backends;

import java.io.IOException;

import dev.bozlak.rag.retrieval.EmbeddingModel;
import dev.bozlak.rag.retrieval.TokenService;

public interface EmbeddingVectorService {

    /**
     * Creates an embedding vector for the sentence.
     * <p>
     * This method does not check the token count.
     * Call {@link TokenService#validateTokenCount(EmbeddingModel, String)} first.
     *
     * @param embeddingModel the model that creates the vector
     * @param sentence the text for the vector
     * @param fullApiEndpoint the full URL of the backend API,
     *                        for example {@code http://localhost:11434/api/embed}.
     *                        Only a path, like {@code /api/embed}, does not work.
     * @return the embedding vector
     * @throws IOException if there is a network problem or the backend does not answer in time
     * @throws InterruptedException if the thread is interrupted while it waits for the answer
     */
    float[] getGeneratedEmbeddingVector(EmbeddingModel embeddingModel, String sentence, String fullApiEndpoint)
        throws IOException, InterruptedException;

    /**
     * Creates an embedding vector for the sentence with the default model.
     * <p>
     * This is the same as {@link #getGeneratedEmbeddingVector(EmbeddingModel, String, String)},
     * but it uses {@link EmbeddingModel#getDefaultEmbeddingModel()}.
     *
     * @param sentence the text for the vector
     * @param fullApiEndpoint the full URL of the backend API
     * @return the embedding vector
     * @throws IOException if there is a network problem or the backend does not answer in time
     * @throws InterruptedException if the thread is interrupted while it waits for the answer
     */
    float[] getGeneratedEmbeddingVector(String sentence, String fullApiEndpoint)
        throws IOException, InterruptedException;

    /**
     * Creates an embedding vector for the sentence with the default model and the default URL.
     * <p>
     * This is the same as {@link #getGeneratedEmbeddingVector(EmbeddingModel, String, String)},
     * but it uses {@link EmbeddingModel#getDefaultEmbeddingModel()} and the default URL of the backend.
     *
     * @param sentence the text for the vector
     * @return the embedding vector
     * @throws IOException if there is a network problem or the backend does not answer in time
     * @throws InterruptedException if the thread is interrupted while it waits for the answer
     */
    float[] getGeneratedEmbeddingVector(String sentence)
        throws IOException, InterruptedException;
}

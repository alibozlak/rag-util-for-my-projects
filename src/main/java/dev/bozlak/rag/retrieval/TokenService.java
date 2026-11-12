package dev.bozlak.rag.retrieval;

import ai.djl.modality.nlp.preprocess.Tokenizer;
import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

public interface TokenService extends AutoCloseable {

    /**
     * Checks that the sentence is not too long for the embedding model.
     * <p>
     * Call this method before you create an embedding vector.
     * The embedding service does not check the token count for you.
     * <p>
     * The limit is the max token count of the model minus
     * {@link TokenizerDefaultConfig#TOKEN_SAFETY_MARGIN}.
     * <p>
     * The tokenizer of this service must be the tokenizer of the same model.
     * If it is not, the token count is wrong.
     *
     * @param embeddingModel the model that you will use for the embedding vector
     * @param sentence the text to check
     * @throws TokenCountTooMuchException if the sentence has too many tokens
     */
    void validateTokenCount(EmbeddingModel embeddingModel, String sentence);

    /**
     * Checks that the sentence is not too long for the default embedding model.
     * <p>
     * This is the same as {@link #validateTokenCount(EmbeddingModel, String)},
     * but it uses {@link EmbeddingModel#getDefaultEmbeddingModel()}.
     *
     * @param sentence the text to check
     * @throws TokenCountTooMuchException if the sentence has too many tokens
     */
    void validateTokenCount(String sentence);

    /**
     * Shared check for the classes that implement {@code TokenService}.
     * <p>
     * You do not need to call this method yourself.
     * Use {@link #validateTokenCount(EmbeddingModel, String)} instead.
     *
     * @param embeddingModel the model that gives the max token count
     * @param sentence the text to check
     * @param tokenizer the tokenizer that counts the tokens
     * @throws TokenCountTooMuchException if the sentence has too many tokens
     */
    static void validateTokenCount(EmbeddingModel embeddingModel, String sentence, Tokenizer tokenizer){
        int tokenCount = tokenizer.tokenize(sentence).size();
        int maxTokenCount = embeddingModel.getModelInfos().maxTokenContextLength - TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;

        if (tokenCount > maxTokenCount)
            throw new TokenCountTooMuchException(maxTokenCount, tokenCount);
    }

}

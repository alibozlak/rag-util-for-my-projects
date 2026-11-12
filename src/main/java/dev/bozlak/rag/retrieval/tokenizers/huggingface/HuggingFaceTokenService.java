package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.EmbeddingModel;
import dev.bozlak.rag.retrieval.TokenService;

public class HuggingFaceTokenService implements TokenService {

    private final HuggingFaceTokenizer huggingFaceTokenizer;

    /**
     * Creates the service with a Hugging Face tokenizer.
     * <p>
     * You can create the tokenizer with
     * {@link HuggingFaceTokenizerDefaultConfigForRetrieval#huggingFaceTokenizer()}.
     * The tokenizer must be the tokenizer of the embedding model that you use.
     * <p>
     * This service closes the tokenizer in {@link #close()}.
     * Do not use the tokenizer after you close the service.
     *
     * @param huggingFaceTokenizer the tokenizer of your embedding model
     */
    public HuggingFaceTokenService(HuggingFaceTokenizer huggingFaceTokenizer) {
        this.huggingFaceTokenizer = huggingFaceTokenizer;
    }

    @Override
    public void validateTokenCount(EmbeddingModel embeddingModel, String sentence) {
        TokenService.validateTokenCount(embeddingModel, sentence, this.huggingFaceTokenizer);
    }

    @Override
    public void validateTokenCount(String sentence) {
        this.validateTokenCount(EmbeddingModel.getDefaultEmbeddingModel(), sentence);
    }

    /**
     * Closes the tokenizer and frees its native memory.
     * <p>
     * Call this method when you do not need the service anymore.
     * You can also use the service in a try-with-resources block.
     * Do not use the service after this call.
     */
    @Override
    public void close() {
        this.huggingFaceTokenizer.close();
    }
}

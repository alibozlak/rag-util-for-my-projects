package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.EmbeddingModel;
import dev.bozlak.rag.retrieval.TokenService;

public class HuggingFaceTokenService implements TokenService, AutoCloseable {

    private final HuggingFaceTokenizer huggingFaceTokenizer;

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

    @Override
    public void close() throws Exception {
        this.huggingFaceTokenizer.close();
    }
}

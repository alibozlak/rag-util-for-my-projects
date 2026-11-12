package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.bozlak.rag.retrieval.EmbeddingModel;
import dev.bozlak.rag.retrieval.TestData;
import dev.bozlak.rag.retrieval.TokenCountTooMuchException;
import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

class HuggingFaceTokenServiceTest {

    private static final EmbeddingModel MODEL = EmbeddingModel.BGE_M3_567M;
    private static final int LIMIT =
            MODEL.getModelInfos().maxTokenContextLength - TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;

    private HuggingFaceTokenService tokenService;

    @BeforeEach
    void setUp() throws IOException {
        tokenService = new HuggingFaceTokenService(
                HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer(TestData.testTokenizerPath()));
    }

    @AfterEach
    void tearDown() {
        tokenService.close();
    }

    @Test
    void validateTokenCount_exactlyAtLimit_doesNotThrow() {
        assertDoesNotThrow(() -> tokenService.validateTokenCount(MODEL, TestData.sentenceWithTokens(LIMIT)));
    }

    @Test
    void validateTokenCount_oneTokenOverLimit_throws() {
        String sentence = TestData.sentenceWithTokens(LIMIT + 1);

        assertThrows(TokenCountTooMuchException.class, () -> tokenService.validateTokenCount(MODEL, sentence));
    }

    @Test
    void validateTokenCount_withoutModel_usesDefaultModelLimit() {
        int defaultLimit = EmbeddingModel.getDefaultEmbeddingModel().getModelInfos().maxTokenContextLength
                - TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;
        String sentenceAtLimit = TestData.sentenceWithTokens(defaultLimit);
        String sentenceOverLimit = TestData.sentenceWithTokens(defaultLimit + 1);

        assertDoesNotThrow(() -> tokenService.validateTokenCount(sentenceAtLimit));
        assertThrows(TokenCountTooMuchException.class, () -> tokenService.validateTokenCount(sentenceOverLimit));
    }
}

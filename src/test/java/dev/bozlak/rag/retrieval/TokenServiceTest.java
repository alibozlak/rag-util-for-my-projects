package dev.bozlak.rag.retrieval;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ai.djl.modality.nlp.preprocess.SimpleTokenizer;
import ai.djl.modality.nlp.preprocess.Tokenizer;
import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

class TokenServiceTest {

    private static final EmbeddingModel MODEL = EmbeddingModel.BGE_M3_567M;
    private static final int LIMIT =
            MODEL.getModelInfos().maxTokenContextLength - TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;

    // Splits on spaces: one word is one token.
    private final Tokenizer tokenizer = new SimpleTokenizer();

    @Test
    void validateTokenCount_shortSentence_doesNotThrow() {
        assertDoesNotThrow(() -> TokenService.validateTokenCount(MODEL, "hello world", tokenizer));
    }

    @Test
    void validateTokenCount_exactlyAtLimit_doesNotThrow() {
        assertDoesNotThrow(() -> TokenService.validateTokenCount(MODEL, TestData.sentenceWithTokens(LIMIT), tokenizer));
    }

    @Test
    void validateTokenCount_oneTokenOverLimit_throwsWithMaxAndActualCount() {
        String sentence = TestData.sentenceWithTokens(LIMIT + 1);

        TokenCountTooMuchException exception = assertThrows(TokenCountTooMuchException.class,
                () -> TokenService.validateTokenCount(MODEL, sentence, tokenizer));

        assertTrue(exception.getMessage().contains("Max: " + LIMIT), exception.getMessage());
        assertTrue(exception.getMessage().contains("Actual: " + (LIMIT + 1)), exception.getMessage());
    }
}

package dev.bozlak.rag.retrieval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

class EmbeddingModelTest {

    @Test
    void toString_returnsOllamaModelName() {
        assertEquals("bge-m3:567m", EmbeddingModel.BGE_M3_567M.toString());
    }

    @Test
    void getModelInfos_bgeM3_hasExpectedValues() {
        EmbeddingModelInfo info = EmbeddingModel.BGE_M3_567M.getModelInfos();

        assertEquals("bge-m3:567m", info.modelName);
        assertEquals(8_192, info.maxTokenContextLength);
        assertEquals(1_024, info.embeddingLength);
        assertEquals("F16", info.quantization);
    }

    @Test
    void getDefaultEmbeddingModel_isBgeM3() {
        assertEquals(EmbeddingModel.BGE_M3_567M, EmbeddingModel.getDefaultEmbeddingModel());
    }

    // The constructor fills the model info with a switch on the model name.
    // If a new model has no case in the switch, its info is null. This test finds that.
    @ParameterizedTest
    @EnumSource(EmbeddingModel.class)
    void everyModel_hasValidModelInfo(EmbeddingModel embeddingModel) {
        EmbeddingModelInfo info = embeddingModel.getModelInfos();

        assertNotNull(info, () -> embeddingModel.name() + " has no model info. Add a case to the switch in the constructor.");
        assertEquals(embeddingModel.toString(), info.modelName);
        assertTrue(info.maxTokenContextLength > TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN,
                "Max token count must be bigger than the safety margin");
        assertTrue(info.embeddingLength > 0, "Embedding length must be positive");
    }
}

package dev.bozlak.rag.retrieval.backends.ollama;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.bozlak.rag.retrieval.EmbeddingModel;

class OllamaEmbeddingModelRequestDtoTest {

    private final Gson gson = new Gson();

    // Ollama reads these field names. If a field name changes, the request breaks.
    @Test
    void toJson_hasTheFieldsThatOllamaExpects() {
        EmbeddingModel model = EmbeddingModel.BGE_M3_567M;
        OllamaEmbeddingModelRequestDto dto = new OllamaEmbeddingModelRequestDto(model, "hello world");

        JsonObject json = JsonParser.parseString(gson.toJson(dto)).getAsJsonObject();

        assertEquals(model.toString(), json.get("model").getAsString());

        JsonArray input = json.getAsJsonArray("input");
        assertEquals(1, input.size());
        assertEquals("hello world", input.get(0).getAsString());

        assertFalse(json.get("truncate").getAsBoolean());
        assertEquals(model.getModelInfos().maxTokenContextLength,
                json.getAsJsonObject("options").get("num_ctx").getAsInt());
    }

    @Test
    void constructorWithOnlySentence_usesDefaultModel() {
        OllamaEmbeddingModelRequestDto dto = new OllamaEmbeddingModelRequestDto("hello");

        assertEquals(EmbeddingModel.getDefaultEmbeddingModel().toString(), dto.model);
        assertEquals(List.of("hello"), dto.input);
    }
}

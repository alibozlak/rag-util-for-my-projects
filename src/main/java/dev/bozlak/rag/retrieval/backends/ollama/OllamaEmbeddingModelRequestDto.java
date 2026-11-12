package dev.bozlak.rag.retrieval.backends.ollama;

import java.util.List;
import java.util.Map;

import dev.bozlak.rag.retrieval.EmbeddingModel;

/**
 * Request DTO for generating embedding vectors using the Ollama API.
 */
public class OllamaEmbeddingModelRequestDto {
    
    public final String model;
    public final List<String> input;
    public final boolean truncate;
    public final Map<String, Object> options; 

    public OllamaEmbeddingModelRequestDto(String sentence) {
        this(EmbeddingModel.getDefaultEmbeddingModel(), sentence);
    }

    public OllamaEmbeddingModelRequestDto(EmbeddingModel embeddingModel, String sentence) {
        this(
            embeddingModel.getInfos().modelName, 
            List.of(sentence), 
            OllamaDefaultPropertiesForRetrieval.TRUNCATE, 
            Map.of(OllamaDefaultPropertiesForRetrieval.NUM_CTX_NAME, embeddingModel.getInfos().maxTokenContextLength)
        );
    }

    public OllamaEmbeddingModelRequestDto(
        String embeddingModelName, List<String> sentences, boolean truncate, Map<String, Object> options
    ) {
        this.model = embeddingModelName;
        this.input = sentences;
        this.truncate = truncate;
        this.options = options;
    }
}

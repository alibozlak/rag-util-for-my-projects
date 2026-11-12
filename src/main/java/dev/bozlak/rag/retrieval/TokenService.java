package dev.bozlak.rag.retrieval;

import ai.djl.modality.nlp.preprocess.Tokenizer;
import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

public interface TokenService {

    void validateTokenCount(EmbeddingModel embeddingModel, String sentence);
    void validateTokenCount(String sentence);

    static void validateTokenCount(EmbeddingModel embeddingModel, String sentence, Tokenizer tokenizer){
        int tokenCount = tokenizer.tokenize(sentence).size();
        int maxTokenCount = EmbeddingModel.getModelInfos(embeddingModel).maxTokenContextLength 
            -TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;

        if (tokenCount > maxTokenCount)
            throw new TokenCountTooMuchException(maxTokenCount, tokenCount);
    }

}

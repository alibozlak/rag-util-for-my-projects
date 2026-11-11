package dev.bozlak.rag.retrieval;

import ai.djl.modality.nlp.preprocess.Tokenizer;
import dev.bozlak.rag.retrieval.tokenizers.HuggingFaceTokenizerDefaultConfigForRetrieval;

public interface TokenService {

    static void validTokenCount(EmbeddingModel embeddingModel, String sentence, Tokenizer tokenizer){
        int tokenCount = tokenizer.tokenize(sentence).size();
        int maxTokenCount = embeddingModel.getInfos().maxTokenContextLength -
                HuggingFaceTokenizerDefaultConfigForRetrieval.TOKEN_SAFETY_MARGIN;

        if (tokenCount > maxTokenCount)
            throw new TokenCountTooMuchException(maxTokenCount, tokenCount);
    }

}

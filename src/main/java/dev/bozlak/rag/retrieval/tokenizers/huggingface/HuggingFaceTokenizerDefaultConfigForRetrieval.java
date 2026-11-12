package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.TokenizerJsonFileNotFoundException;
import dev.bozlak.rag.retrieval.tokenizers.TokenizerDefaultConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HuggingFaceTokenizerDefaultConfigForRetrieval {

    public static final boolean TRUNCATION = false;
    public static final boolean ADD_SPECIAL_TOKENS = true;
    public static final String TOKENIZER_PATH = "models/embedding/tokenizer.json";
    public static final int TOKEN_SAFETY_MARGIN = TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN;

    /**
     * Default config for Hugging Face Tokenizer. <br>
     * Tokenizer Path: <b>models/embedding/tokenizer.json</b> <br>
     * Add Special Tokens: <b>true</b> <br>
     * Truncation: <b>false</b> <br>
     *
     * @return ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
     * @throws IOException
     */
    public static HuggingFaceTokenizer huggingFaceTokenizer() throws IOException {
        return huggingFaceTokenizer(TOKENIZER_PATH);
    }

    /**
     * Default config for Hugging Face Tokenizer. <br>
     * Add Special Tokens: <b>true</b> <br>
     * Truncation: <b>false</b> <br>
     *
     * @return ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
     * @throws IOException
     */
    public static HuggingFaceTokenizer huggingFaceTokenizer(String tokenizerJsonPath) throws IOException {
        Path path = getTokenizerJsonAbsolutePath(tokenizerJsonPath);

        return HuggingFaceTokenizer.builder()
                .optTokenizerPath(path)
                .optAddSpecialTokens(ADD_SPECIAL_TOKENS)
                .optTruncation(TRUNCATION)
                .build();
    }

    public static Path getTokenizerJsonAbsolutePath(String tokenizerJsonPath) {
        Path path = Path.of(tokenizerJsonPath).toAbsolutePath().normalize();
        if (!Files.isRegularFile(path))
            throw new TokenizerJsonFileNotFoundException(tokenizerJsonPath);

        return path;
    }
}

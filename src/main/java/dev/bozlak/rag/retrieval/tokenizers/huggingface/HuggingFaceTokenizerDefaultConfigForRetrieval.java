package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.TokenizerJsonFileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HuggingFaceTokenizerDefaultConfigForRetrieval {

    public static final boolean TRUNCATION = false;
    public static final boolean ADD_SPECIAL_TOKENS = true;
    public static final String TOKENIZER_PATH = "models/embedding/tokenizer.json";

    /**
     * Default config for Hugging Face Tokenizer. <br>
     * Tokenizer Path: <b>models/embedding/tokenizer.json</b> <br>
     * Add Special Tokens: <b>true</b> <br>
     * Truncation: <b>false</b> <br>
     * <p>
     * The path starts from the working directory, not from the classpath.
     * Run your project from the folder that has your {@code pom.xml}.
     * <p>
     * Put the {@code tokenizer.json} file of your embedding model in this path.
     * You can find the download link in the Javadoc of each
     * {@link dev.bozlak.rag.retrieval.EmbeddingModel EmbeddingModel} value.
     * <p>
     * Close the tokenizer when you finish. If you give it to
     * {@link HuggingFaceTokenService}, the service closes it for you.
     *
     * @return a new tokenizer
     * @throws TokenizerJsonFileNotFoundException if there is no file in the path
     * @throws IOException if the tokenizer file cannot be read
     */
    public static HuggingFaceTokenizer huggingFaceTokenizer() throws IOException {
        return huggingFaceTokenizer(TOKENIZER_PATH);
    }

    /**
     * Default config for Hugging Face Tokenizer. <br>
     * Add Special Tokens: <b>true</b> <br>
     * Truncation: <b>false</b> <br>
     * <p>
     * Use this method when your tokenizer file is not in the default path.
     * A relative path starts from the working directory.
     * <p>
     * Close the tokenizer when you finish. If you give it to
     * {@link HuggingFaceTokenService}, the service closes it for you.
     *
     * @param tokenizerJsonPath the path of the {@code tokenizer.json} file
     * @return a new tokenizer
     * @throws TokenizerJsonFileNotFoundException if there is no file in the path
     * @throws IOException if the tokenizer file cannot be read
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

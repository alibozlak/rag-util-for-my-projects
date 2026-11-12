package dev.bozlak.rag.retrieval;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Shared data for the tests.
 */
public final class TestData {

    private static final String TEST_TOKENIZER_RESOURCE = "/tokenizer/test-tokenizer.json";

    private TestData() {
    }

    /**
     * Returns a sentence with the given number of words.
     * <p>
     * One word is one token for {@code SimpleTokenizer} and for {@code test-tokenizer.json}.
     */
    public static String sentenceWithTokens(int tokenCount) {
        return String.join(" ", Collections.nCopies(tokenCount, "word"));
    }

    /**
     * Returns the absolute path of {@code src/test/resources/tokenizer/test-tokenizer.json}.
     * <p>
     * This is a small WordLevel tokenizer. It splits the text on spaces and adds no special tokens.
     */
    public static String testTokenizerPath() {
        URL url = TestData.class.getResource(TEST_TOKENIZER_RESOURCE);
        if (url == null)
            throw new IllegalStateException("Test resource not found: " + TEST_TOKENIZER_RESOURCE);

        try {
            return Path.of(url.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}

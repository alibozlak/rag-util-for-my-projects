package dev.bozlak.rag.retrieval.tokenizers.huggingface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.TestData;
import dev.bozlak.rag.retrieval.TokenizerJsonFileNotFoundException;

class HuggingFaceTokenizerDefaultConfigForRetrievalTest {

    @TempDir
    Path tempDir;

    @Test
    void getTokenizerJsonAbsolutePath_existingFile_returnsAbsolutePath() throws Exception {
        Path file = Files.createFile(tempDir.resolve("tokenizer.json"));

        Path result = HuggingFaceTokenizerDefaultConfigForRetrieval.getTokenizerJsonAbsolutePath(file.toString());

        assertTrue(result.isAbsolute());
        assertEquals(file.toAbsolutePath().normalize(), result);
    }

    @Test
    void getTokenizerJsonAbsolutePath_relativePath_startsFromWorkingDirectory() {
        String relativePath = "src/test/resources/tokenizer/test-tokenizer.json";

        Path result = HuggingFaceTokenizerDefaultConfigForRetrieval.getTokenizerJsonAbsolutePath(relativePath);

        Path expected = Path.of(System.getProperty("user.dir")).resolve(relativePath).normalize();
        assertEquals(expected, result);
    }

    @Test
    void getTokenizerJsonAbsolutePath_missingFile_throws() {
        String missingFile = tempDir.resolve("missing.json").toString();

        assertThrows(TokenizerJsonFileNotFoundException.class,
                () -> HuggingFaceTokenizerDefaultConfigForRetrieval.getTokenizerJsonAbsolutePath(missingFile));
    }

    @Test
    void getTokenizerJsonAbsolutePath_directory_throws() {
        String directory = tempDir.toString();

        assertThrows(TokenizerJsonFileNotFoundException.class,
                () -> HuggingFaceTokenizerDefaultConfigForRetrieval.getTokenizerJsonAbsolutePath(directory));
    }

    @Test
    void huggingFaceTokenizer_validFile_createsWorkingTokenizer() throws Exception {
        try (HuggingFaceTokenizer tokenizer =
                     HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer(TestData.testTokenizerPath())) {

            assertEquals(List.of("hello", "world"), tokenizer.tokenize("hello world"));
        }
    }

    // If the tokenizer cuts long text, the token count is wrong and the check never fails.
    @Test
    void huggingFaceTokenizer_doesNotTruncateLongText() throws Exception {
        try (HuggingFaceTokenizer tokenizer =
                     HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer(TestData.testTokenizerPath())) {

            assertEquals(10_000, tokenizer.tokenize(TestData.sentenceWithTokens(10_000)).size());
        }
    }

    @Test
    void huggingFaceTokenizer_missingFile_throws() {
        String missingFile = tempDir.resolve("missing.json").toString();

        assertThrows(TokenizerJsonFileNotFoundException.class,
                () -> HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer(missingFile));
    }

    // This library has no models/ folder. The projects that use the library have it.
    @Test
    void huggingFaceTokenizer_defaultPathWithoutFile_throws() {
        Path defaultPath = Path.of(HuggingFaceTokenizerDefaultConfigForRetrieval.TOKENIZER_PATH);
        assumeFalse(Files.exists(defaultPath), "A tokenizer file is in the default path, so this test is skipped");

        assertThrows(TokenizerJsonFileNotFoundException.class,
                () -> HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer());
    }
}

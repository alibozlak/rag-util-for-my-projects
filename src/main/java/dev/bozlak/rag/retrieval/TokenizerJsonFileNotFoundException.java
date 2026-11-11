package dev.bozlak.rag.retrieval;

public class TokenizerJsonFileNotFoundException extends RuntimeException {
    public TokenizerJsonFileNotFoundException(String tokenizerJsonPath) {
        super("Tokenizer json file not found: " + tokenizerJsonPath + " !!");
    }
}

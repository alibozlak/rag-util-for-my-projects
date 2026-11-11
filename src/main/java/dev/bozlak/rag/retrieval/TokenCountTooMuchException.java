package dev.bozlak.rag.retrieval;

public class TokenCountTooMuchException extends RuntimeException {
    public TokenCountTooMuchException(int maxCount, int actualCount) {
        super("Token count is too much. Max: " + maxCount + ", Actual: " + actualCount + " !!");
    }
}

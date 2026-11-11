package dev.bozlak.rag.retrieval;

public enum EmbeddingModel {

    /**
     * Hugging Face Tokenizer Path: <a href="https://huggingface.co/BAAI/bge-m3/blob/main/tokenizer.json">link</a> <br>
     * Architecture: Bert <br>
     * Total parameters: 566.70 Million <br>
     */
    BGE_M3_567M("bge-m3:567m");

    private final String modelName;

    EmbeddingModel(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return this.modelName;
    }

    public EmbeddingModelInfo getInfos() {
        return switch (this) {
            case BGE_M3_567M -> new EmbeddingModelInfo(
                    "bge-m3:567m", 8_192, 1_024, "F16"
            );
        };
    }

}

package dev.bozlak.rag.retrieval;

public enum EmbeddingModel {

    /**
     * Hugging Face Tokenizer Path: <a href="https://huggingface.co/BAAI/bge-m3/blob/main/tokenizer.json">link</a> <br>
     * Architecture: Bert <br>
     * Total parameters: 566.70 Million <br>
     */
    BGE_M3_567M("bge-m3:567m");

    private final String modelName;
    private EmbeddingModelInfo embeddingModelInfo;

    EmbeddingModel(String modelName) {
        this.modelName = modelName;

        switch (modelName) {
            case "bge-m3:567m": this.embeddingModelInfo 
                = new EmbeddingModelInfo(modelName, 8_192, 1_024, "F16"); break;
        }
    }

    @Override
    public String toString() {
        return this.modelName;
    }

    public EmbeddingModelInfo getModelInfos() {
        return this.embeddingModelInfo;
    }

    public static EmbeddingModel getDefaultEmbeddingModel() {
        return EmbeddingModel.BGE_M3_567M;
    }

}

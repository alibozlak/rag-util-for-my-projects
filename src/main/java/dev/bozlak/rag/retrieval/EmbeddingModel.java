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

    /**
     * Returns the model name for the backend, for example {@code "bge-m3:567m"}.
     * <p>
     * The backend uses this name in the request.
     *
     * @return the model name
     */
    @Override
    public String toString() {
        return this.modelName;
    }

    /**
     * Returns the details of this model:
     * max token count, vector length and quantization.
     *
     * @return the model details
     */
    public EmbeddingModelInfo getModelInfos() {
        return this.embeddingModelInfo;
    }

    /**
     * Returns the default model: {@link #BGE_M3_567M}.
     * <p>
     * Methods without an {@code EmbeddingModel} parameter use this model.
     *
     * @return the default model
     */
    public static EmbeddingModel getDefaultEmbeddingModel() {
        return EmbeddingModel.BGE_M3_567M;
    }

}

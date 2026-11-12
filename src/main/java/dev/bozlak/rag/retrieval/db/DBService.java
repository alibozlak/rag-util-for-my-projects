package dev.bozlak.rag.retrieval.db;

import java.util.function.Consumer;

public interface DBService {
    
    void insertRawToDB(AddToDbRequestBaseModel addToDbRequestBaseModel);

    static void insertRawToDB(Consumer<AddToDbRequestBaseModel> consumer, AddToDbRequestBaseModel addToDbRequestBaseModel) {
        consumer.accept(addToDbRequestBaseModel);
    }
}

package Integration.MongoDB;

import RadiumEditor.Console;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class MongoDB {

    private MongoClient client;

    public void Connect(String connectionString) {
        ConnectionString connection = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connection)
                .build();

        client = MongoClients.create(settings);

        Console.Log("Connected to database.");
    }

    public void Disconnect() {
        client.close();
    }

    public MongoDatabase GetDatabase(String database) {
        return client.getDatabase(database);
    }

    public List<Document> GetDocuments(String database, String collectionName) {
        MongoDatabase db = GetDatabase(database);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        FindIterable<Document> documents = collection.find();
        MongoCursor<Document> iterator = documents.iterator();
        List<Document> docs = new ArrayList<>();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            docs.add(doc);
        }

        return docs;
    }

}

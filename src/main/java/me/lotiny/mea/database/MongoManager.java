package me.lotiny.mea.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.lotiny.mea.Mea;
import org.bson.Document;

@Getter
public class MongoManager {

    private final Mea plugin;

    @Getter
    private MongoClient client;
    @Getter
    private MongoDatabase database;
    @Getter
    private boolean connected = true;

    private MongoCollection<Document> collection;

    public MongoManager(Mea plugin) {
        this.plugin = plugin;
        connect();
    }

    public void connect() {
        try {
            this.client = MongoClients.create(plugin.getDatabaseFile().getString("MONGODB.URI"));
            this.database = this.client.getDatabase(plugin.getDatabaseFile().getString("MONGODB.DATABASE"));
        } catch (Exception e) {
            this.connected = false;
        }

        this.collection = this.database.getCollection("players");
    }

    public void disconnect() {
        if (this.client != null) {
            this.client.close();
        }
    }
}

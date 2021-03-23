

package mapreducenosql;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class mongodbconf {
    static final String host = "localhost";
    static final int port = 27017;
    static final String dbName = "test";
   
    final MongoClient mongoClient;
    final MongoDatabase mongoDatabase;
    
    
    public mongodbconf(){
        mongoClient = new MongoClient(host, port);
        mongoDatabase = mongoClient.getDatabase(dbName);
    }
}

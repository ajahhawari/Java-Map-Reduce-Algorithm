package mapreducenosql;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

public class MapReduceNoSQL {

public final MongoCollection<Document> rawCollection;
    //public final MongoCollection&ltDocument&gt resultCollection;
    
    public MapReduceNoSQL(mongodbconf config){
        rawCollection = config.mongoDatabase.getCollection("friendlist");
    }

    private ArrayList<Document> map(ArrayList<Document> friendList){
        ArrayList<Document> mapResult = new ArrayList<>();
        
        for(Document personFriend:friendList){
            ArrayList<Document> mapFriend = new ArrayList<>();
            String name = personFriend.getString("name");
            ArrayList friends = (ArrayList)personFriend.get("friend");
            
            for(int i=0; i<friends.size(); i++){
                ArrayList key = new ArrayList();
                key.add(name);
                key.add(friends.get(i));
                Document kv = new Document();
                kv.append("key", key);
                kv.append("value", friends);
                Collections.sort(key);
                mapFriend.add(kv);
            }
            
            mapResult.addAll(mapFriend);
            
        }
        
        return mapResult;
    }
    
    private Map<ArrayList, ArrayList<ArrayList>> group(ArrayList<Document> mapResult){
        Map<ArrayList, ArrayList<ArrayList>> groupResult = new HashMap<>();
        
        for(Document kv:mapResult){
            groupResult.put((ArrayList)kv.get("key"), new ArrayList<ArrayList>());
        }
        
        for(Document kv:mapResult){
            groupResult.get((ArrayList)kv.get("key")).add((ArrayList)kv.get("value"));
        }
        
        return groupResult;
    }
    
    private Map<ArrayList, ArrayList> reduce(Map<ArrayList, ArrayList<ArrayList>> groupResult){
        Map<ArrayList, ArrayList> reduceResult = new HashMap<>();
        
        for(Map.Entry kv:groupResult.entrySet()){
            ArrayList key = (ArrayList)kv.getKey();
            ArrayList<ArrayList> value = (ArrayList)kv.getValue();
            
            ArrayList<String> result = value.get(0);
            for(int i=1; i<value.size(); i++){
                result = intersection(value.get(i), result);
            }
            reduceResult.put(key, result);
        }
        
        return reduceResult;
    }
    
    private ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2){
        ArrayList<String> result = new ArrayList<>(list1);
        result.retainAll(list2);
        
        return result;
    }
    public static void main(String[] args) {
        
        mongodbconf config = new mongodbconf();
        MapReduceNoSQL mapReduce = new MapReduceNoSQL(config);
        FindIterable<Document> friendList = mapReduce.rawCollection.find();
        ArrayList<Document> friends = new ArrayList<>();
        for(Document friend:friendList){
            friends.add(friend);
        }
        
        ArrayList<Document> mapResult = mapReduce.map(friends);
        Map<ArrayList, ArrayList<ArrayList>> groupResult = mapReduce.group(mapResult);
        Map<ArrayList, ArrayList> reduceResult = mapReduce.reduce(groupResult);
        
        
        System.out.println("------Friend List------------------");
        for(Document f:friends){
            String name = f.getString("name");
            ArrayList<String> friend = (ArrayList)f.get("friend");
            System.out.println(name+":"+friend.toString());
        }
        
        System.out.println("------Map Result------------------");
        for(Document kv:mapResult){
            ArrayList key = (ArrayList)kv.get("key");
            ArrayList value = (ArrayList)kv.get("value");
            System.out.println(key.toString()+":"+value.toString());
            
        }
        
        System.out.println("------Group Result------------------");
        for(Map.Entry kv:groupResult.entrySet()){
            ArrayList key = (ArrayList)kv.getKey();
            ArrayList<ArrayList> value = (ArrayList)kv.getValue();
            System.out.println(key.toString()+":"+value.toString());
        }
        
        System.out.println("------Reduce Result------------------");
        for(Map.Entry kv:reduceResult.entrySet()){
            ArrayList key = (ArrayList)kv.getKey();
            ArrayList value = (ArrayList)kv.getValue();
            System.out.println(key.toString()+":"+value.toString());
        }
        
    }
    
}

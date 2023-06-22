import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.schemafields.NumericField;
import redis.clients.jedis.search.schemafields.TextField;

import java.util.HashMap;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        JedisPooled jedisPool = new JedisPooled("localhost", 6379);
//        Employee employee = new Employee("Trong Beo", "Free", 1);
//        Employee employee1 = new Employee("John", "Free", 1);
//        Employee employee2 = new Employee("Paul", "Free", 1);
//        jedisPool.ftCreate("idx:employee",
//                FTCreateParams.createParams()
//                        .on(IndexDataType.JSON)
//                        .addPrefix("emp:"),
//                TextField.of("$.name").as("name"),
//                TextField.of("$.company").as("company"),
//                NumericField.of("$.age").as("age")
//        );
//        jedisPool.jsonSetWithEscape("emp:1", employee);
//        jedisPool.jsonSetWithEscape("emp:2", employee1);
//        jedisPool.jsonSetWithEscape("emp:3", employee2);
        var query = new Query("John");
        var result = jedisPool.ftSearch("idx:employee", query).getDocuments();
        System.out.println(result);
    }
}

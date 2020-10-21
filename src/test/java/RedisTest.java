import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class RedisTest {

    @Test
   public void testRedisHash(){
        Jedis jedis = new Jedis("127.0.0.1",6379);
        Map<String,String> map = new HashMap<>();
        map.put("name", "Herry");
        map.put("age", "10");

        jedis.hmset("redisTest:u", map);
        System.out.println( jedis.hmget("redisTest::u", "age")
);
    }
}

package creative.design.carrotbow.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return (String) values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }


    //Set
    public void addSets(String key, String data){
        SetOperations<String, Object> sets = redisTemplate.opsForSet();
        sets.add(key, data);
    }

    public void addSets(String key, String data, Duration duration) {
        SetOperations<String, Object> sets = redisTemplate.opsForSet();
        sets.add(key, data, duration);
    }

    public Set<Object> getSets(String key){
        SetOperations<String, Object> sets = redisTemplate.opsForSet();
        return sets.members(key);
    }

    public void deleteSets(String key, String data){
        SetOperations<String, Object> sets = redisTemplate.opsForSet();
        sets.remove(key, data);
    }

}

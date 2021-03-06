package login.repository;

import com.sun.org.apache.xpath.internal.operations.Bool;
import login.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    @Cacheable(value = "findOneByUsername", keyGenerator = "customKeyGenerator")
	User findOneByUsername(String username);

    User findOneByUsername(String username, Boolean nocache);

}

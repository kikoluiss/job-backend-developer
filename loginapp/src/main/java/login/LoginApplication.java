package login;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import login.model.User;
import login.repository.UserRepository;
import login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableResourceServer
@EnableCircuitBreaker
public class LoginApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(LoginApplication.class, args);

        String mongoHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        Integer mongoPort = Integer.parseInt(ctx.getEnvironment().getProperty("spring.data.mongodb.port"));
        String mongoDatabase = ctx.getEnvironment().getProperty("spring.data.mongodb.database");
        String mongoUsername = ctx.getEnvironment().getProperty("spring.data.mongodb.username");
        String mongoPassword = ctx.getEnvironment().getProperty("spring.data.mongodb.password");

        MongoClient mongoClient = null;
        if (mongoUsername != null && !mongoUsername.isEmpty() && mongoPassword != null && !mongoPassword.isEmpty()) {
            MongoCredential credentials = MongoCredential.createMongoCRCredential(
                    mongoUsername,
                    mongoDatabase,
                    mongoPassword.toCharArray()
            );
            mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort), credentials, null);
        }
        else {
            mongoClient = new MongoClient(mongoHost, mongoPort);
        }

        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, mongoDatabase);

        List<User> usersList = mongoTemplate.findAll(User.class, "users");
        if (usersList.size() == 0) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword("$2a$10$8aBm3dlbLr33F7rj3n7vYuU7AxPVnR54QGBWpVF7sCLC7pfmY6EFa");
            user.setEnabled(true);
            mongoTemplate.save(user, "users");
        }
    }

}

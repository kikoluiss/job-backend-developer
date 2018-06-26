package login.endpoits;

import login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String home() {
        return "Home page according to user's profile";
    }
}

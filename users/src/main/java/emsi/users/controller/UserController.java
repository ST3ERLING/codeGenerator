package emsi.users.controller;

import emsi.users.entity.User;
import emsi.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email) {
        try {
            userService.signup(username, password, email);
            return "User registered successfully!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        try {
            User user = userService.login(username, password);
            return "Welcome, " + user.getUsername() + "!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

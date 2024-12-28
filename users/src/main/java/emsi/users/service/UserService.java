package emsi.users.service;

import emsi.users.entity.User;
import emsi.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User signup(String username, String password, String email) throws Exception {
        // Check if the username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username is already taken");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email is already registered");
        }

        // Save the new user
        User user = new User(username, password, email);
        return userRepository.save(user);
    }

    public User login(String username, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = optionalUser.get();

        // Basic password validation
        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password");
        }

        return user;
    }
}

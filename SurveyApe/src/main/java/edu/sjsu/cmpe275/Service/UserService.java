package edu.sjsu.cmpe275.Service;

import edu.sjsu.cmpe275.Domain.Role;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Repository.RoleRepository;
import edu.sjsu.cmpe275.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByConfirmationToken(String confirmationToken) {
        return userRepository.findByConfirmationToken(confirmationToken);
    }

    public List<User> findAll(){
        List<User> users = new ArrayList<>();
        Iterable<User> itr = userRepository.findAll();
        for(User user:itr){
            users.add(user);
        }
        return users;
    }

    public void saveUser(User user) {
        Role userRole;
        if(!user.isEnabled()){
            userRole = roleRepository.findByRole("USER");
        }
        else{
            userRole = roleRepository.findByRole("ADMIN");
        }
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

}
package com.udemy.demo.user;

import com.udemy.demo.configuration.MyUserDetailService;
import com.udemy.demo.jwt.Jwtutils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private Jwtutils jwtUtils;

    @PostMapping(value = "/users")
    public ResponseEntity addUSer(@RequestBody @Valid User user, HttpServletResponse response) {

        List<User> users = userRepository.findByEmail(user.getEmail());

        if (!users.isEmpty()) {
            return new ResponseEntity("User already existing", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setLastName(StringUtils.capitalize(user.getLastName()));
        user.setFirstName(StringUtils.capitalize(user.getFirstName()));

        userRepository.save(user);

        String token = jwtUtils.generateToken(new MyUserDetailService.UserPrincipal(user));
        Cookie cookie = new Cookie("token", token);
        response.addCookie(cookie);

        return new ResponseEntity(user, HttpStatus.CREATED);
    }

}

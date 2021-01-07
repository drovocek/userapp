package edu.volkov.userapp.web;

import edu.volkov.userapp.model.Greeting;
import edu.volkov.userapp.model.HIMessage;
import edu.volkov.userapp.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@AllArgsConstructor
@Slf4j
public class UserSocketController extends AbstractUserController {

//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HIMessage message) throws Exception {
//        log.info("\n <<greeting: {} >>", message);
//        Thread.sleep(1000); // simulated delay
//        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//    }
//
//    @MessageMapping("/users/get")
//    @SendTo("/topic/greetings")
//    public User get(User user) {
//        log.info("get by id: {}", user.getId());
//        User userFromDb = userRepository.findById(user.getId()).orElse(null);
//        if (userFromDb == null) throw new NoSuchElementException("no user by id: " + user.getId());
//        return user;
//    }

    @MessageMapping("/users/getAll")
    @SendTo("/topic/greetings")
    public List<User> getAll() {
        return StreamSupport.stream(getAllUsers().spliterator(), false)
                .collect(Collectors.toList());
    }

    @MessageMapping("/users/getFiltered")
    @SendTo("/topic/greetings")
    public Page<User> getFiltered(
            Integer pageNumber,
            Integer pageSize,
            String email,
            String phoneNumber,
            String firstName,
            String lastName
    ) {
        return getFilteredUsers(pageNumber, pageSize, email, phoneNumber, firstName, lastName);
    }

    @MessageMapping("/users/get")
    @SendTo("/topic/greetings")
    public User get(int id) {
        return getUser(id);
    }

    @MessageMapping("/users/create")
    @SendTo("/topic/greetings")
    public User create(User user) {
        return createUser(user);
    }

    @MessageMapping("/users/delete")
    @SendTo("/topic/greetings")
    public Integer delete(int id) {
        deleteUser(id);
        return id;
    }

    @MessageMapping("/users/update")
    @SendTo("/topic/greetings")
    public User update(User user) throws BindException {
        return updateUser(user, user.getId());
    }

    @MessageMapping("/users/getByMail")
    @SendTo("/topic/greetings")
    public User getByMail(String email) {
        return getUserByEmail(email);
    }
}

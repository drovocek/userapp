package edu.volkov.userapp.web;

import edu.volkov.userapp.model.Greeting;
import edu.volkov.userapp.model.HIMessage;
import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.NoSuchElementException;

@Controller
@AllArgsConstructor
@Slf4j
public class UserSocketController {

    private final UserRepository userRepository;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HIMessage message) throws Exception {
        log.info("\n <<greeting: {} >>", message);
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/users/get")
    @SendTo("/topic/greetings")
    public User get(User user) {
        log.info("get by id: {}", user.getId());
        User userFromDb = userRepository.findById(user.getId()).orElse(null);
        if (userFromDb == null) throw new NoSuchElementException("no user by id: " + user.getId());
        return user;
    }

    @MessageMapping("/users/delete")
    @SendTo("/topic/greetings")
    public User delete(User user) throws Exception {
        log.info("\n <<delete by id: {} >>", user.getId());
        userRepository.deleteById(user.getId());
        return user;
    }

//    @MessageMapping
//    @SendTo("/topic/greetings")
//    public Greeting greeting() {
//        log.info("\n <<deleteMess by id: {} >>", 1);
//        return new Greeting("delete by id: " + 1);
//    }
//
//    @PostMapping(value = "/register", consumes = MediaTypes.HAL_JSON_VALUE)
//    @ResponseStatus(value = HttpStatus.CREATED)
//    public ResponseEntity<EntityModel<User>> register(@Valid @RequestBody User user) {
//        log.info("register {}", user);
//        ValidationUtil.checkNew(user);
//        user.setRoles(Set.of(Role.USER));
//        user = userRepository.save(user);
//        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/account")
//                .build().toUri();
//        return ResponseEntity.created(uriOfNewResource).body(ASSEMBLER.toModel(user));
//    }
//
//    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(@Valid @RequestBody User user, @AuthenticationPrincipal AuthUser authUser) {
//        log.info("update {} to {}", authUser, user);
//        User oldUser = authUser.getUser();
//        ValidationUtil.assureIdConsistent(user, oldUser.id());
//        user.setRoles(oldUser.getRoles());
//        if (user.getPassword() == null) {
//            user.setPassword(oldUser.getPassword());
//        }
//        userRepository.save(user);
//    }

}

package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import edu.volkov.userapp.to.UserPackage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.NoSuchElementException;

import static edu.volkov.userapp.util.PackageType.*;
import static edu.volkov.userapp.util.UserUtil.iterableToArray;
import static edu.volkov.userapp.util.UserUtil.packUp;

@Controller
@AllArgsConstructor
@Slf4j
public class UserSocketController {

    private final UserRepository repository;

    @MessageMapping("/users/getAll")
    @SendTo("/topic/users/init")
    public UserPackage getAll() {
        log.info("\n << getAll >>");
        return packUp(GET_ALL, iterableToArray(repository.findAll()));
    }

    @MessageMapping("/users/get")
    @SendTo("/topic/users")
    public UserPackage get(Integer id) {
        log.info("\n << get by id: {} >>", id);
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new NoSuchElementException("no user by id: " + id);
        }
        return packUp(GET, user);
    }

    @MessageMapping("/users/create")
    @SendTo("/topic/users")
    public UserPackage create(User user) {
        log.info("\n << create: {} >>", user);
        return packUp(CREATE, repository.save(user));
    }

    @MessageMapping("/users/delete")
    @SendTo("/topic/users")
    public UserPackage delete(Integer id) {
        log.info("\n << delete by id: {} >>", id);
        if (repository.delete(id) == 0) {
            throw new NoSuchElementException("no user by id: " + id);
        }
        User deleted = new User(id, "", "", "", "");
        return packUp(DELETE, deleted);
    }

    @MessageMapping("/users/update")
    @SendTo("/topic/users")
    public UserPackage update(User user) {
        log.info("\n << update: {} >>", user);
        User checked = repository.findById(user.getId()).orElse(null);
        if (checked == null) {
            throw new NoSuchElementException("update failed, no user by id: " + user.getId());
        }
        return packUp(UPDATE, repository.save(user));
    }
}

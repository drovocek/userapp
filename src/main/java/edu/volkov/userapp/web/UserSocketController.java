package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import edu.volkov.userapp.to.UserPackage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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

    @MessageMapping("/users/get/{id}")
    @SendTo("/topic/users")
    public UserPackage get(@DestinationVariable Integer id) {
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
        if (!user.isNew()) {
            throw new RuntimeException("user has id, he is not new");
        }
        User created = repository.save(user);
        log.info("\n << created: {} >>", created);
        return packUp(CREATE, created);
    }

    @MessageMapping("/users/delete/{id}")
    @SendTo("/topic/users")
    public UserPackage delete(@DestinationVariable Integer id) {
        log.info("\n << delete by id: {} >>", id);
        if (repository.delete(id) == 0) {
            throw new NoSuchElementException("no user by id: " + id);
        }
        User deleted = new User(id, "", "", "", "");
        return packUp(DELETE, deleted);
    }

    @MessageMapping("/users/update/{id}")
    @SendTo("/topic/users")
    public UserPackage update(@DestinationVariable Integer id, User user) {
        log.info("\n << update: {} >>", user);
        if (!user.getId().equals(id)) {
            throw new RuntimeException("id is not the user id");
        }
        User checked = repository.findById(id).orElse(null);
        if (checked == null) {
            throw new NoSuchElementException("update failed, no user by id: " + user.getId());
        }
        User updated = repository.save(user);
        log.info("\n << updated: {} >>", updated);
        return packUp(UPDATE, updated);
    }

    @SubscribeMapping("/users")
    public UserPackage getAll() {
        log.info("\n << getAll >>");
        return packUp(GET_ALL, iterableToArray(repository.findAll()));
    }
}

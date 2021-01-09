package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import edu.volkov.userapp.to.UserPackage;
import edu.volkov.userapp.util.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;

import static edu.volkov.userapp.to.PackageType.*;
import static edu.volkov.userapp.util.UserUtil.iterableToArray;
import static edu.volkov.userapp.util.UserUtil.packUp;

@Controller
@AllArgsConstructor
@Slf4j
public class UserSocketController {

    private final UserRepository repository;
    private final ValidationUtil validationUtil;

    @MessageMapping("/users/create")
    @SendTo("/topic/users")
    public UserPackage create(User user) throws BindException {
        log.info("\n << create: {} >>", user);
        validationUtil.validateBeforeCreate(user);
        User created = repository.save(user);
        return packUp(CREATE, created);
    }

    @MessageMapping("/users/delete/{id}")
    @SendTo("/topic/users")
    public UserPackage delete(@DestinationVariable Integer id) throws NotFoundException {
        log.info("\n << delete by id: {} >>", id);
        validationUtil.checkNotFoundWithId(repository.delete(id) != 0, id);
        return packUp(DELETE, new User(id, "", "", "", ""));
    }

    @MessageMapping("/users/update/{id}")
    @SendTo("/topic/users")
    public UserPackage update(@DestinationVariable Integer id, User user) throws NotFoundException, BindException {
        log.info("\n << update: {} >>", user);
        validationUtil.validateBeforeUpdate(user, id);
        User updated = repository.save(user);
        return packUp(UPDATE, updated);
    }

    @MessageMapping("/users/getAll")
    @SendToUser("/queue/users")
    public UserPackage getAll() {
        log.info("\n << getAll >>");
        return packUp(GET_ALL, iterableToArray(repository.findAll()));
    }

    @MessageMapping("/users/get/{id}")
    @SendToUser("/queue/users")
    public UserPackage get(@DestinationVariable Integer id) throws NotFoundException {
        log.info("\n << get by id: {} >>", id);
        User user = validationUtil.checkNotFoundWithId(repository.findById(id).orElse(null), id);
        return packUp(GET, user);
    }
}

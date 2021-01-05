package edu.volkov.userapp.repository;

import edu.volkov.userapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@RepositoryEventHandler(User.class)
@Slf4j
public class UserRepositoryEventHandler {

    @HandleBeforeSave
    public void handleUserSave(@Valid User user) {
        log.info("\n<<handleUserSave>>");
    }

    @HandleBeforeCreate
    public void handleUserCreate(@Valid User user) {
        log.info("\n<<handleUserCreate>>");
    }
}
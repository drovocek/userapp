package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.validation.BindException;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    private UserRepository repository;

    public Iterable<User> getAllUsers() {
        log.info("\n << getAll >>");
        return repository.findAll();
    }

    public Page<User> getFilteredUsers(
            Integer pageNumber,
            Integer pageSize,
            String email,
            String phoneNumber,
            String firstName,
            String lastName
    ) {
        log.info("\n << getFiltered for pageNumber: {} and pageSize: {} >>", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return repository.getFiltered(email, phoneNumber, firstName, lastName, pageable);
    }

    public User getUser(int id) {
        log.info("\n << get by id: {} >>", id);
        User user = repository.findById(id).orElse(null);
        if (user == null) throw new NoSuchElementException("no user by id: " + id);
        return user;
    }

    public User createUser(User user) {
        log.info("\n << create: {} >>", user);
        return repository.save(user);
    }

    public void deleteUser(int id) {
        log.info("\n << get by id: {} >>", id);
        if (repository.delete(id) != 0) {
            throw new NoSuchElementException("no user by id: " + id);
        }
    }

    public User updateUser(User user, int id) throws BindException {
        log.info("\n << update {} with id={} >>", user, id);
        return repository.save(user);
    }

    public User getUserByEmail(String email) {
        log.info("\n << get by email: {} >>", email);
        User user = repository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            throw new NoSuchElementException("no user by email: " + email);
        }
        return user;
    }
}

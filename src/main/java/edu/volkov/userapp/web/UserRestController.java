package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = UserRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserRestController extends AbstractUserController {

    static final String REST_URL = "/api/users";

    @GetMapping
    public List<User> getAll() {
        return getAllUsers();
    }

    @GetMapping("/filter")
    public Page<User> getFiltered(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        return getFilteredUsers(pageNumber, pageSize, email, phoneNumber, firstName, lastName);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return getUser(id);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = super.createUser(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        deleteUser(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping
    public ResponseEntity<User> update(@RequestBody User user, @PathVariable int id) throws BindException {
        User updated = super.createUser(user);
        URI uriOfUpdateResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .buildAndExpand(updated.getId()).toUri();
        return ResponseEntity.created(uriOfUpdateResource).body(updated);
    }

    @GetMapping("/by")
    public User getByMail(@RequestParam String email) {
        User user = super.getUserByEmail(email);
        return user;
    }
}
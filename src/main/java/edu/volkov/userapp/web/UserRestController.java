package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = UserRestController.REST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class UserRestController extends AbstractUserController implements RepresentationModelProcessor<RepositoryLinksResource> {

    @SuppressWarnings("unchecked")
    public static final RepresentationModelAssemblerSupport<User, EntityModel<User>> ASSEMBLER =
            new RepresentationModelAssemblerSupport<User, EntityModel<User>>(UserRestController.class, (Class<EntityModel<User>>) (Class<?>) EntityModel.class) {
                @Override
                public EntityModel<User> toModel(User user) {
                    return EntityModel.of(user, linkTo(UserRestController.class).withSelfRel());
                }
            };

    static final String REST_URL = "/api/users";

    @GetMapping
    public CollectionModel<EntityModel<User>> getAll() {
        return ASSEMBLER.toCollectionModel(findAll());
    }

    @GetMapping("/filter")
    public CollectionModel<EntityModel<User>> getFiltered(
            Integer pageNumber,
            Integer pageSize,
            String email,
            String phoneNumber,
            String firstName,
            String lastName
    ) {
        log.info("\n << getFiltered for pageNumber: {} and pageSize: {} >>", pageNumber, pageSize);
        return ASSEMBLER.toCollectionModel(getFilteredBy(pageNumber, pageSize, email, phoneNumber, firstName, lastName));
    }

    @GetMapping("/{id}")
    public EntityModel<User> get(@PathVariable int id) {
        return ASSEMBLER.toModel(findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<User>> create(@RequestBody User user) {
        User created = createNew(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(ASSEMBLER.toModel(created));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody User user, @PathVariable int id) throws BindException {
        super.update(user, id);
    }

    @GetMapping("/by")
    public EntityModel<User> getByMail(@RequestParam String email) {
        User user = super.findByEmailIgnoreCase(email);
        return ASSEMBLER.toModel(user);
    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(UserRestController.class).withRel("users"));
        return resource;
    }
}
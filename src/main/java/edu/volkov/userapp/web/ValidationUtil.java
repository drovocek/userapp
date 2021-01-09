package edu.volkov.userapp.web;

import edu.volkov.userapp.View;
import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import edu.volkov.userapp.util.exception.IllegalRequestDataException;
import edu.volkov.userapp.util.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Component
@Slf4j
public class ValidationUtil {

    private final Validator validator;
    private final UniqueMailValidator emailValidator = new UniqueMailValidator();
    private final HasUserIdValidator hasIdValidator = new HasUserIdValidator();

    private final UserRepository repository;

    public ValidationUtil(UserRepository repository, @Qualifier("defaultValidator") Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    public void validateBeforeUpdate(User user, int id) throws BindException {
        assureIdConsistent(user, id);
        log.info("<< validateBeforeUpdate() >>");
        DataBinder binder = new DataBinder(user);
        binder.addValidators(emailValidator, hasIdValidator, validator);
        binder.validate(View.Web.class);
        if (binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }
    }

    public void validateBeforeCreate(User user) throws BindException {
        checkNew(user);
        log.info("<< validateBeforeCreate() >>");
        DataBinder binder = new DataBinder(user);
        binder.addValidators(emailValidator, validator);
        binder.validate(View.Web.class);
        if (binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }
    }

    private void assureIdConsistent(User user, int id) {
        log.info("<< assureIdConsistent() >>");
        if (user.isNew()) {
            user.setId(id);
        } else if (user.id() != id) {
            throw new IllegalRequestDataException(user + " must be with id=" + id);
        }
    }

    private void checkNew(User user) {
        log.info("<< checkNew() >>");
        if (!user.isNew()) {
            throw new IllegalRequestDataException(user + " must be new (id=null)");
        }
    }

    public User checkNotFoundWithId(User user, int id) {
        log.info("<< checkNotFoundWithId() >>");
        checkNotFoundWithId(user != null, id);
        return user;
    }

    public void checkNotFoundWithId(boolean found, int id) {
        log.info("<< checkNotFoundWithId() >>");
        checkNotFound(found, "id=" + id);
    }

    public static void checkNotFound(boolean found, String msg) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + msg);
        }
    }

    @Component("uniqueEmail")
    public class UniqueMailValidator implements org.springframework.validation.Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return User.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            log.info("<< UniqueMailValidator validate() >>");
            User user = ((User) target);
            if (StringUtils.hasText(user.getEmail())) {
                User dbUser = repository.getByEmail(user.getEmail().toLowerCase());
                if (dbUser != null && !dbUser.getId().equals(user.getId())) {
                    errors.rejectValue("email", "User with this email already exists");
                }
            }
        }
    }

    @Component("hasId")
    public class HasUserIdValidator implements org.springframework.validation.Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return User.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            log.info("<< HasUserIdValidator validate() >>");
            User user = ((User) target);
            if (StringUtils.hasText(user.getEmail())) {
                User dbUser = repository.findById(user.getId()).orElse(null);
                if (dbUser == null) {
                    errors.rejectValue("id", "User with this id is not exist");
                }
            }
        }
    }
}

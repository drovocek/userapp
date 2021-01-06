package edu.volkov.userapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import static edu.volkov.userapp.testdata.UserTestData.*;
import static edu.volkov.userapp.util.exception.ErrorType.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
class UserApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper mapper;

    private static final String BASE_PATH = "http://localhost/api/users";
    private static final Pageable FIRST_PAGE_WITH_TWO_USERS = PageRequest.of(0, 2);

    private void verifyJsonWithOneUser(final ResultActions action, User user, Integer userId) throws Exception {
        action
                .andExpect(jsonPath("firstName", is(user.getFirstName())))
                .andExpect(jsonPath("lastName", is(user.getLastName())))
                .andExpect(jsonPath("phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + userId)))
                .andExpect(jsonPath("_links.user.href", is(BASE_PATH + "/" + userId)));
    }

    private void verifyJsonWithManyUsers(final ResultActions action, Map<Integer, User> usersById) throws Exception {
        usersById.forEach((userId, user) -> {
            try {
                int i = userId - 1;
                action
                        .andExpect(jsonPath("_embedded.users[" + i + "].firstName", is(user.getFirstName())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].lastName", is(user.getLastName())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].phoneNumber", is(user.getPhoneNumber())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].email", is(user.getEmail())))
                        .andExpect(jsonPath("_embedded.users[" + i + "]._links.self.href", is(BASE_PATH + "/" + userId)))
                        .andExpect(jsonPath("_embedded.users[" + i + "]._links.user.href", is(BASE_PATH + "/" + userId)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void getOne() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/" + USER1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE));

        verifyJsonWithOneUser(result, USER1, USER1_ID);

        USER_MATCHER.assertMatch(repository.findById(USER1_ID).get(), USER1);
    }

    @Test
    void getNotFound() throws Exception {
        this.mockMvc.perform(get(BASE_PATH + "/" + USER_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(DATA_NOT_FOUND.name()));

        assertThrows(NoSuchElementException.class, () -> repository.findById(USER_NOT_FOUND_ID).get());
    }

    @Test
    void getByEmail() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/by-email")
                .param("email", USER1.getEmail()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE));

        verifyJsonWithOneUser(result, USER1, USER1_ID);

        USER_MATCHER.assertMatch(repository.findByEmailIgnoreCase(USER1.getEmail()).get(), USER1);
    }

    @Test
    public void getAll() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH)))
                .andExpect(jsonPath("_links.profile.href", is("http://localhost/api/profile/users")))
                .andExpect(jsonPath("_links.search.href", is(BASE_PATH + "/search")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(USERS_MAP.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, USERS_MAP);

        USER_MATCHER.assertMatch(repository.findAll(), USERS_LIST);
    }

    @Test
    public void getFilteredOne() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", USER1.getPhoneNumber())
                .param("email", USER1.getEmail())
                .param("firstName", USER1.getFirstName())
                .param("lastName", USER1.getLastName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(ONE_USER_MAP.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(
                        USER1.getPhoneNumber(), USER1.getEmail(),
                        USER1.getFirstName(), USER1.getLastName(),
                        FIRST_PAGE_WITH_TWO_USERS
                ),
                Arrays.asList(USER1)
        );
    }

    @Test
    public void getFilteredAllWithBlankParam() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", "")
                .param("email", "")
                .param("firstName", "")
                .param("lastName", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(USERS_LIST.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, USERS_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered("", "", "", "", FIRST_PAGE_WITH_TWO_USERS),
                USERS_LIST
        );
    }

    @Test
    public void getFilteredOneWithUpperParam() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", USER1.getPhoneNumber().toUpperCase())
                .param("email", USER1.getEmail().toUpperCase())
                .param("firstName", USER1.getFirstName().toUpperCase())
                .param("lastName", USER1.getLastName().toUpperCase()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(ONE_USER_MAP.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(
                        USER1.getPhoneNumber(), USER1.getEmail(),
                        USER1.getFirstName(), USER1.getLastName(),
                        FIRST_PAGE_WITH_TWO_USERS
                ),
                Arrays.asList(USER1)
        );
    }

    @Test
    public void getFilteredOneWithHalfStringParam() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", "(111)")
                .param("email", "asily")
                .param("firstName", "as")
                .param("lastName", "van"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(ONE_USER_MAP.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(
                        USER1.getPhoneNumber(), USER1.getEmail(),
                        USER1.getFirstName(), USER1.getLastName(),
                        FIRST_PAGE_WITH_TWO_USERS
                ),
                Arrays.asList(USER1)
        );
    }

    @Test
    public void getFilteredAllWithoutParam() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(USERS_LIST.size())))
                .andExpect(jsonPath("page.totalPages", is(1)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, USERS_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(null, null, null, null, FIRST_PAGE_WITH_TWO_USERS),
                USERS_LIST
        );
    }

    @Test
    public void getFilteredEmptyList() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", "1234")
                .param("email", "")
                .param("firstName", "")
                .param("lastName", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter?page=0&size=20")))
                .andExpect(jsonPath("page.size", is(20)))
                .andExpect(jsonPath("page.totalElements", is(0)))
                .andExpect(jsonPath("page.totalPages", is(0)))
                .andExpect(jsonPath("page.number", is(0)));

        verifyJsonWithManyUsers(result, Collections.emptyMap());

        USER_MATCHER.assertMatch(
                repository.getFiltered("1234", "", "", "", FIRST_PAGE_WITH_TWO_USERS),
                Collections.emptyList()
        );
    }

    @Test
    void deleteGood() throws Exception {
        this.mockMvc.perform(delete(BASE_PATH + "/" + USER1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(repository.findById(USER1_ID).isPresent());
    }

    @Test
    void deleteNotFound() throws Exception {
        this.mockMvc.perform(delete(BASE_PATH + "/" + USER_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(DATA_NOT_FOUND.name()));

        assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(USER_NOT_FOUND_ID));
    }

    @Test
    void update() throws Exception {
        User expected = getNew();

        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(expected)))
                .andDo(print())
                .andExpect(status().isNoContent());

        User actual = repository.findById(USER1_ID).get();

        USER_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateDuplicateEmail() throws Exception {
        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(USER_WITH_DUPLICATE_EMAIL)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(DATA_ERROR.name()));

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(USER_WITH_DUPLICATE_EMAIL));
    }

    @Test
    void updateInvalid() throws Exception {
        User invalid = new User(USER1);
        invalid.setFirstName("");
        invalid.setLastName("");
        invalid.setPhoneNumber("");
        invalid.setEmail("");

        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()));

        assertThrows(ConstraintViolationException.class, () -> repository.save(invalid));
    }

//    @Test
//    public void updateHtmlUnsafe() throws Exception {
//        this.mockMvc.perform(put("/api/users/1")
//                .contentType(MediaTypes.HAL_JSON_VALUE)
//                .content(USER_HTML_UNSAFE_HAL_JSON))
//                .andExpect(status().isConflict());
//    }

    @Test
    void create() throws Exception {
        User newUser = getNew();

        ResultActions action = this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(newUser)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = mapper.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
        User createdFromDb = repository.findById(NEW_USER_ID).get();

        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(createdFromDb, newUser);
    }

    @Test
    void createDuplicateEmail() throws Exception {
        this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(USER_WITH_DUPLICATE_EMAIL)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(DATA_ERROR.name()));

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(USER_WITH_DUPLICATE_EMAIL));
    }

    @Test
    void createInvalid() throws Exception {
        User invalid = new User(USER1);
        invalid.setFirstName("");
        invalid.setLastName("");
        invalid.setPhoneNumber("");
        invalid.setEmail("");

        this.mockMvc.perform(post(BASE_PATH)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()));

        assertThrows(ConstraintViolationException.class, () -> repository.save(invalid));
    }
}

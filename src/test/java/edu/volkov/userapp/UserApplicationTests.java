package edu.volkov.userapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static edu.volkov.userapp.testdata.UserTestData.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper mapper;

    private static final String BASE_PATH = "http://localhost/api/users";

    @Test
    void getReturnsCorrectResponse() throws Exception {
        USER_MATCHER.assertMatch(repository.findById(USER1_ID).get(), USER1);
        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + USER1_ID));
        result.andDo(print()).andExpect(status().isOk());
        verifyJsonWithOneUser(result, USER1, USER1_ID);
    }

    void verifyJsonWithOneUser(final ResultActions action, User user, Integer userId) throws Exception {
        action
                .andExpect(jsonPath("phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("firstName", is(user.getFirstName())))
                .andExpect(jsonPath("lastName", is(user.getLastName())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + userId)))
                .andExpect(jsonPath("_links.user.href", is(BASE_PATH + "/" + userId)));
    }

    void verifyJsonWithManyUsers(final ResultActions action, Map<Integer, User> usersById) throws Exception {
        usersById.forEach((userId, user) -> {
            try {
                int i = userId - 1;
                action
                        .andExpect(jsonPath("_embedded.users[" + i + "].phoneNumber", is(user.getPhoneNumber())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].email", is(user.getEmail())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].firstName", is(user.getFirstName())))
                        .andExpect(jsonPath("_embedded.users[" + i + "].lastName", is(user.getLastName())))
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
                .andExpect(status().isNotFound());
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
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(USER1.getPhoneNumber(), USER1.getEmail(), USER1.getFirstName(), USER1.getLastName()),
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
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, USERS_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered("", "", "", ""),
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
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(USER1.getPhoneNumber(), USER1.getEmail(), USER1.getFirstName(), USER1.getLastName()),
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
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, ONE_USER_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(USER1.getPhoneNumber(), USER1.getEmail(), USER1.getFirstName(), USER1.getLastName()),
                Arrays.asList(USER1)
        );
    }

    @Test
    public void getFilteredAllWithoutParam() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, USERS_MAP);

        USER_MATCHER.assertMatch(
                repository.getFiltered(null, null, null, null),
                USERS_LIST
        );
    }

    @Test
    public void getFilteredNotFound() throws Exception {
        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/search/filter")
                .param("phoneNumber", "1234")
                .param("email", "")
                .param("firstName", "")
                .param("lastName", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/search/filter")));

        verifyJsonWithManyUsers(result, Collections.emptyMap());

        USER_MATCHER.assertMatch(
                repository.getFiltered("1234","","",""),
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
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        User expected = getNew();

        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(expected)))
                .andExpect(status().isNoContent());

        User actual = repository.findById(USER1_ID).get();

        USER_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateDuplicateEmail() throws Exception {
        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(mapper.writeValueAsString(USER_WITH_DUPLICATE_EMAIL)))
                .andExpect(status().isConflict());
    }

//    @Test
//    void updateInvalid() throws Exception {
//        this.mockMvc.perform(put("/api/users/1")
//                .contentType(MediaTypes.HAL_JSON_VALUE)
//                .content(USER_BAD_HAL_JSON))
//                .andExpect(status().isConflict());
//    }

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
                .andExpect(status().isConflict());
    }

//    @Test
//    void createInvalid() throws Exception {
//        this.mockMvc.perform(post("/api/users")
//                .contentType(MediaTypes.HAL_JSON_VALUE)
//                .content(USER_BAD_HAL_JSON))
//                .andExpect(status().isConflict());
//    }
}

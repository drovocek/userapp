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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static edu.volkov.userapp.testdata.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
class UserApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getOne() throws Exception {
        this.mockMvc.perform(get("/api/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(USER1_HAL_JSON));
    }

    @Test
    void getNotFound() throws Exception {
        this.mockMvc.perform(get("/api/users/11"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEmail() throws Exception {
        this.mockMvc.perform(get("/api/users/search/by-email?email=mail1@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(USER1_HAL_JSON));
    }

    @Test
    public void getAll() throws Exception {
        this.mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(ALL_USERS_HAL_JSON));
    }

    @Test
    public void getFilteredOne() throws Exception {
        this.mockMvc.perform(get("/api/users/search/filter?" +
                "phoneNumber=+1 (111) 111-11-11&" +
                "email=mail1@gmail.com&" +
                "firstName=First_name1&" +
                "lastName=Last_Name1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(FILTERED_USERS_HAL_JSON));
    }

    @Test
    public void getFilteredAllWithEmptyParam() throws Exception {
        this.mockMvc.perform(get("/api/users/search/filter?" +
                "phoneNumber=&"+
                "email=&" +
                "firstName=&" +
                "lastName="))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(FILTERED_USERS_HAL_JSON));
    }

    @Test
    public void getFilteredAllWithNullParam() throws Exception {
        this.mockMvc.perform(get("/api/users/search/filter?"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(content().json(FILTERED_USERS_HAL_JSON));
    }

    @Test
    void deleteGood() throws Exception {
        this.mockMvc.perform(delete("/api/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(repository.findById(1).isPresent());
    }

    @Test
    void deleteNotFound() throws Exception {
        this.mockMvc.perform(delete("/api/users/11"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        User expected = mapper.readValue(getUpdatedUser1HalJson(), User.class);

        this.mockMvc.perform(put("/api/users/1")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(getUpdatedUser1HalJson()))
                .andExpect(status().isNoContent());

        User actual = repository.findById(1).get();

        USER_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateDuplicateEmail() throws Exception {
        this.mockMvc.perform(put("/api/users/1")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(USER1_HAL_JSON.replaceAll("mail1@gmail.com", "mail2@gmail.com")))
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
    void createWithLocation() throws Exception {
        User newUser = mapper.readValue(getNewUserHalJson(), User.class);

        ResultActions action = this.mockMvc.perform(post("/api/users")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(getNewUserHalJson()))
                .andExpect(status().isCreated());

        User created = mapper.readValue(action.andReturn().getResponse().getContentAsString(), User.class);

        User createdFromDb = repository.findById(3).get();

        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(createdFromDb, newUser);
    }

    @Test
    void createWithLocationDuplicateEmail() throws Exception {
        this.mockMvc.perform(post("/api/users")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(getNewUserHalJson().replaceAll("mail13@gmail.com", "mail1@gmail.com")))
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

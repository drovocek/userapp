package edu.volkov.userapp;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import edu.volkov.userapp.to.UserPackage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static edu.volkov.userapp.testdata.UserTestData.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:testData.sql", config = @SqlConfig(encoding = "UTF-8"))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UserApplicationTests {

    @Autowired
    UserRepository repository;

    @Value("${local.server.port}")
    private int port;
    private String URL;

    private static final String SEND_CREATE_ENDPOINT = "/app/users/create";
    private static final String SEND_UPDATE_ENDPOINT = "/app/users/update/";
    private static final String SEND_DELETE_ENDPOINT = "/app/users/delete/";
    private static final String SUBSCRIBE_ENDPOINT = "/topic/users";
    private static final String SUBSCRIBE_GET_ENDPOINT = "/app/users/get/";
    private static final String SUBSCRIBE_GET_ALL_ENDPOINT = "/app/users/getAll";

    private CompletableFuture<UserPackage> completableUserPackageFuture;

    @Before
    public void setup() {
        completableUserPackageFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/websocket";
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class CreateUserPackageFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            log.info("<< getPayloadType >>");
            return UserPackage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            log.info("<< handleFrame >>");
            completableUserPackageFuture.complete((UserPackage) o);
        }
    }


    @Test
    public void create() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_CREATE_ENDPOINT, getNew());

        User created = completableUserPackageFuture.get(10, SECONDS).getUsers()[0];
        assertNotNull(created);

        User createdFromDb = repository.findById(NEW_USER_ID).get();
        assertNotNull(createdFromDb);

        USER_MATCHER.assertMatch(created, createdFromDb);
    }

    @Test
    public void update() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_UPDATE_ENDPOINT + USER1_ID, getUpdated());

        User updated = completableUserPackageFuture.get(2, SECONDS).getUsers()[0];
        assertNotNull(updated);

        User updatedFromDb = repository.findById(USER1_ID).get();
        assertNotNull(updatedFromDb);

        USER_MATCHER.assertMatch(updated, updatedFromDb);
    }

    @Test
    public void delete() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_DELETE_ENDPOINT + USER1_ID, getUpdated());

        Integer deletedId = completableUserPackageFuture.get(2, SECONDS).getId();
        assertNotNull(deletedId);

        User deleted = repository.findById(USER1_ID).orElse(null);
        assertNull(deleted);

        assertEquals(deletedId, USER1_ID);
    }

    @Test
    public void get() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_GET_ENDPOINT + USER1_ID, new CreateUserPackageFrameHandler());

        User user = completableUserPackageFuture.get(2, SECONDS).getUsers()[0];
        assertNotNull(user);

        USER_MATCHER.assertMatch(user, USER1);
    }

    @Test
    public void getNotFound() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_GET_ENDPOINT + USER_NOT_FOUND_ID, new CreateUserPackageFrameHandler());

//        Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
    }

    @Test
    public void getAll() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_GET_ALL_ENDPOINT, new CreateUserPackageFrameHandler());

        User[] users = completableUserPackageFuture.get(2, SECONDS).getUsers();

        USER_MATCHER.assertMatch(Arrays.asList(users), USERS_LIST);
    }


//
//    @Test
//    void getNotFound() throws Exception {
//        this.mockMvc.perform(get(BASE_PATH + "/" + USER_NOT_FOUND_ID))
//                .andDo(print())
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.type").value(DATA_NOT_FOUND.name()));
//
//        assertThrows(NoSuchElementException.class, () -> repository.findById(USER_NOT_FOUND_ID).get());
//    }
//
//    @Test
//    void getByEmail() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/by")
//                .param("email", USER1.getEmail()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
//
//        verifyJsonWithOneUser(result, USER1, USER1_ID);
//
//        USER_MATCHER.assertMatch(repository.findByEmailIgnoreCase(USER1.getEmail()).get(), USER1);
//    }
//
//    @Test
//    public void getAll() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
//
//        verifyJsonWithManyUsers(result, USERS_MAP);
//
//        USER_MATCHER.assertMatch(repository.findAll(), USERS_LIST);
//    }
//
//    @Test
//    public void getFilteredOne() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter")
//                .param("pageNumber", "0")
//                .param("pageSize", "20")
//                .param("phoneNumber", USER1.getPhoneNumber())
//                .param("email", USER1.getEmail())
//                .param("firstName", USER1.getFirstName())
//                .param("lastName", USER1.getLastName()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(ONE_USER_MAP.size())))
//                .andExpect(jsonPath("totalPages", is(1)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    public void getFilteredAllWithBlankParam() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter")
//                .param("pageNumber", "0")
//                .param("pageSize", "20")
//                .param("phoneNumber", "")
//                .param("email", "")
//                .param("firstName", "")
//                .param("lastName", ""))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(USERS_LIST.size())))
//                .andExpect(jsonPath("totalPages", is(1)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    public void getFilteredOneWithUpperParam() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter")
//                .param("pageNumber", "0")
//                .param("pageSize", "20")
//                .param("phoneNumber", USER1.getPhoneNumber().toUpperCase())
//                .param("email", USER1.getEmail().toUpperCase())
//                .param("firstName", USER1.getFirstName().toUpperCase())
//                .param("lastName", USER1.getLastName().toUpperCase()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(ONE_USER_MAP.size())))
//                .andExpect(jsonPath("totalPages", is(1)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    public void getFilteredOneWithHalfStringParam() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter")
//                .param("pageNumber", "0")
//                .param("pageSize", "20")
//                .param("phoneNumber", "(111)")
//                .param("email", "asily")
//                .param("firstName", "as")
//                .param("lastName", "van"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(ONE_USER_MAP.size())))
//                .andExpect(jsonPath("totalPages", is(1)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    public void getFilteredAllWithoutParam() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(USERS_LIST.size())))
//                .andExpect(jsonPath("totalPages", is(1)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    public void getFilteredEmptyList() throws Exception {
//        final ResultActions result = this.mockMvc.perform(get(BASE_PATH + "/filter")
//                .param("pageNumber", "0")
//                .param("pageSize", "20")
//                .param("phoneNumber", "1234")
//                .param("email", "")
//                .param("firstName", "")
//                .param("lastName", ""))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("size", is(20)))
//                .andExpect(jsonPath("totalElements", is(0)))
//                .andExpect(jsonPath("totalPages", is(0)))
//                .andExpect(jsonPath("number", is(0)));
//    }
//
//    @Test
//    void deleteGood() throws Exception {
//        this.mockMvc.perform(delete(BASE_PATH + "/" + USER1_ID))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//
//        assertFalse(repository.findById(USER1_ID).isPresent());
//    }
//
//    @Test
//    void deleteNotFound() throws Exception {
//        this.mockMvc.perform(delete(BASE_PATH + "/" + USER_NOT_FOUND_ID))
//                .andDo(print())
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.type").value(DATA_NOT_FOUND.name()));
//
//        assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(USER_NOT_FOUND_ID));
//    }
//
//    @Test
//    void update() throws Exception {
//        User expected = getUpdated();
//
//        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(expected)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        User actual = repository.findById(USER1_ID).get();
//
//        USER_MATCHER.assertMatch(actual, expected);
//    }
//
//    @Test
//    void updateDuplicateEmail() throws Exception {
//        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(USER_WITH_DUPLICATE_EMAIL)))
//                .andDo(print())
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.type").value(DATA_ERROR.name()));
//
//        assertThrows(DataIntegrityViolationException.class, () -> repository.save(USER_WITH_DUPLICATE_EMAIL));
//    }
//
//    @Test
//    void updateInvalid() throws Exception {
//        User invalid = new User(USER1);
//        invalid.setFirstName("");
//        invalid.setLastName("");
//        invalid.setPhoneNumber("");
//        invalid.setEmail("");
//
//        this.mockMvc.perform(put(BASE_PATH + "/" + USER1_ID)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(invalid)))
//                .andDo(print())
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()));
//
//        assertThrows(TransactionSystemException.class, () -> repository.save(invalid));
//    }
//
////    @Test
////    public void updateHtmlUnsafe() throws Exception {
////        this.mockMvc.perform(put("/api/users/1")
////                .contentType(MediaTypes.HAL_JSON_VALUE)
////                .content(USER_HTML_UNSAFE_HAL_JSON))
////                .andExpect(status().isConflict());
////    }
//
//    @Test
//    void create() throws Exception {
//        User newUser = getNew();
//
//        ResultActions action = this.mockMvc.perform(post(BASE_PATH)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(newUser)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        User created = mapper.readValue(action.andReturn().getResponse().getContentAsString(), User.class);
//        User createdFromDb = repository.findById(NEW_USER_ID).get();
//
//        USER_MATCHER.assertMatch(created, newUser);
//        USER_MATCHER.assertMatch(createdFromDb, newUser);
//    }
//
//    @Test
//    void createDuplicateEmail() throws Exception {
//        this.mockMvc.perform(post(BASE_PATH)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(USER_WITH_DUPLICATE_EMAIL)))
//                .andDo(print())
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.type").value(DATA_ERROR.name()));
//
//        assertThrows(DataIntegrityViolationException.class, () -> repository.save(USER_WITH_DUPLICATE_EMAIL));
//    }
//
//    @Test
//    void createInvalid() throws Exception {
//        User invalid = getNew();
//        invalid.setFirstName("");
//        invalid.setLastName("");
//        invalid.setPhoneNumber("");
//        invalid.setEmail("");
//
//        this.mockMvc.perform(post(BASE_PATH)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(mapper.writeValueAsString(invalid)))
//                .andDo(print())
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()));
//
//        assertThrows(ConstraintViolationException.class, () -> repository.save(invalid));
//    }
}

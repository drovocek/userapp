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
    public void createDuplicateEmail() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        User newUserWithDuplicateEmail = getNew();
        newUserWithDuplicateEmail.setEmail(USER1.getEmail());

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_CREATE_ENDPOINT, newUserWithDuplicateEmail);

        //Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
    }

    @Test
    public void createInvalid() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        User newInvalidUser = new User(null, "", "", "", "");

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_CREATE_ENDPOINT, newInvalidUser);

        //Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
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
    public void updateDuplicateEmail() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        User updatedUserWithDuplicateEmail = getUpdated();
        updatedUserWithDuplicateEmail.setEmail(USER_2.getEmail());

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_UPDATE_ENDPOINT + USER1_ID, updatedUserWithDuplicateEmail);

        //Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
    }

    @Test
    public void updateInvalid() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        User updatedInvalidUser = new User(USER1_ID, "", "", "", "");

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_UPDATE_ENDPOINT + USER1_ID, updatedInvalidUser);

        //Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
    }


    @Test
    public void delete() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_DELETE_ENDPOINT + USER1_ID, null);

        Integer deletedId = completableUserPackageFuture.get(2, SECONDS).getId();
        assertNotNull(deletedId);

        User deleted = repository.findById(USER1_ID).orElse(null);
        assertNull(deleted);

        assertEquals(deletedId, USER1_ID);
    }

    @Test
    public void deleteNotFound() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new CreateUserPackageFrameHandler());
        stompSession.send(SEND_DELETE_ENDPOINT + USER_NOT_FOUND_ID, null);

        //Exception exception = completableUserPackageFuture.get(2, SECONDS).getExceptions()[0];
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

////    @Test
////    public void updateHtmlUnsafe() throws Exception {
////        this.mockMvc.perform(put("/api/users/1")
////                .contentType(MediaTypes.HAL_JSON_VALUE)
////                .content(USER_HTML_UNSAFE_HAL_JSON))
////                .andExpect(status().isConflict());
////    }
}

package edu.volkov.userapp.testdata;

import edu.volkov.userapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTestData {

    public static final UserTestMatcher USER_MATCHER = new UserTestMatcher();

    public final static Integer USER1_ID = 1;
    public final static Integer USER2_ID = 2;
    public final static Integer NEW_USER_ID = 3;
    public final static Integer USER_NOT_FOUND_ID = 100;
    public final static User USER1 = new User(USER1_ID, "Vasily", "Ivanov", "+1 (111) 111-11-11", "vasily@gmail.com");
    public final static User USER_2 = new User(USER2_ID, "Ivan", "Vasiliev", "+2 (222) 222-22-22", "ivan@gmail.com");
    public final static User USER_WITH_DUPLICATE_EMAIL = new User(null, "Kiril", "Kirilov", "+4 (444) 444-44-44", "ivan@gmail.com");
    public final static Map<Integer, User> ONE_USER_MAP = new HashMap();
    public final static Map<Integer, User> USERS_MAP = new HashMap();
    public final static List<User> USERS_LIST = new ArrayList();

    static {
        ONE_USER_MAP.put(USER1_ID, USER1);
        USERS_MAP.put(USER1_ID, USER1);
        USERS_MAP.put(USER2_ID, USER_2);
        USERS_LIST.add(USER1);
        USERS_LIST.add(USER_2);
    }

    public static User getNew() {
        return new User(null, "Marina", "Ivanova", "+3 (333) 333-33-33", "marina@gmail.com");
    }

    public static User getUpdated() {
        User updated = new User(USER1);
        updated.setFirstName("updated");
        updated.setLastName("updated");
        updated.setPhoneNumber("updated");
        updated.setEmail("updated@gmail.com");
        return updated;
    }

}




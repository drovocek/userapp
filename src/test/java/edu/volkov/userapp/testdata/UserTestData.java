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
    public final static User USER1 = new User("+1 (111) 111-11-11", "vasily@gmail.com", "Vasily", "Ivanov");
    public final static User USER_2 = new User("+2 (222) 222-22-22", "ivan@gmail.com", "Ivan", "Vasiliev");
    public final static User USER_WITH_DUPLICATE_EMAIL = new User("+4 (444) 444-44-44", "ivan@gmail.com", "Kiril", "Kirilov");
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

    public static User getNew(){
        return new User("+3 (333) 333-33-33", "marina@gmail.com", "Marina", "Ivanova");
    }
}




package edu.volkov.userapp.testdata;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserTestData {

    public static final UserTestMatcher USER_MATCHER = new UserTestMatcher();

    private static final String GET_ALL_FOOTER = "," +
            "\"_links\" : " +
            "{" +
            "\"self\" : {" +
            "\"href\" : \"http://localhost/api/users\"" +
            "}," +
            "\"profile\" : { " +
            "\"href\" : \"http://localhost/api/profile/users\"" +
            "}," +
            "\"search\" : {" +
            "\"href\" : \"http://localhost/api/users/search\" " +
            "}" +
            "}," +
            "\"page\" : " +
            "{" +
            "\"size\" : 20," +
            "\"totalElements\" : 2," +
            "\"totalPages\" : 1," +
            "\"number\" : 0" +
            "}" +
            "}";

    private static final String GET_FILTERED_FOOTER = "," +
            "\"_links\" : " +
            "{" +
            "\"self\" : {" +
            "\"href\" : \"http://localhost/api/users/search/filter?phoneNumber=+1%20(111)%20111-11-11&email=mail1@gmail.com&firstName=First_name1&lastName=Last_Name1\"" +
            "}}}";

    private final static String USER_HAL_JSON_STRING_LAYOUT =
            "{\"phoneNumber\": \"PN\"," +
                    "\"email\": \"mailID@gmail.com\"," +
                    "\"firstName\": \"First_nameID\"," +
                    "\"lastName\": \"Last_NameID\"," +
                    "\"_links\": {" +
                    "\"self\":" +
                    "{\"href\": \"http://localhost/api/users/ID\"}," +
                    "\"user\": {" +
                    "\"href\": \"http://localhost/api/users/ID\"}}}";

    public final static String USER_HTML_UNSAFE_HAL_JSON =
            "{\"phoneNumber\": \"PN\"," +
                    "\"email\": \"mailID@gmail.com\"," +
                    "\"firstName\": \"<script>alert(456)</script>\"," +
                    "\"lastName\": \"<script>alert(789)</script>\"," +
                    "\"_links\": {" +
                    "\"self\":" +
                    "{\"href\": \"http://localhost/api/users/ID\"}," +
                    "\"user\": {" +
                    "\"href\": \"http://localhost/api/users/ID\"}}}";

    public final static String USER_BAD_HAL_JSON =
            "{\"phoneNumber\": \"\"," +
                    "\"email\": \"\"," +
                    "\"firstName\": \"\"," +
                    "\"lastName\": \"\"," +
                    "\"_links\": {" +
                    "\"self\":" +
                    "{\"href\": \"http://localhost/api/users/ID\"}," +
                    "\"user\": {" +
                    "\"href\": \"http://localhost/api/users/ID\"}}}";

    public final static String USER1_HAL_JSON = createUserHalJson(1);
    public final static String ALL_USERS_HAL_JSON;
    public final static String FILTERED_USERS_HAL_JSON;

    static {
        ALL_USERS_HAL_JSON = getUsersHalJsonString(createUserHalJsonStringsList(2), GET_ALL_FOOTER);
        FILTERED_USERS_HAL_JSON = getUsersHalJsonString(createUserHalJsonStringsList(1), GET_FILTERED_FOOTER);
    }

    private static String createUserHalJson(int userId) {
        String phoneNumber = createPhoneNumber(userId);
        return USER_HAL_JSON_STRING_LAYOUT.replaceAll("PN", phoneNumber).replaceAll("ID", String.valueOf(userId));
    }

    private static String createPhoneNumber(int userId) {
        if (userId < 0) userId = -userId;
        if (userId > 9) userId = normalizeId(userId);

        String phoneLayout = "+x (xxx) xxx-xx-xx";
        return phoneLayout.replaceAll("x", String.valueOf(userId));
    }

    private static int normalizeId(int userId) {
        while (userId > 9) {
            userId = userId / 10;
        }
        return userId;
    }

    public static String getUpdatedUser1HalJson() {
        String updated = createUserHalJson(3);
        return updated.replaceAll("/3", "1");
    }

    public static String getNewUserHalJson() {
        return createUserHalJson(4);
    }

    private static List<String> createUserHalJsonStringsList(int count) {
        return Stream.iterate(1, n -> n + 1).limit(count).map(UserTestData::createUserHalJson).collect(Collectors.toList());
    }

    private static String getUsersHalJsonString(List<String> usersStringHalJson, String footer) {
        StringBuilder sb = new StringBuilder("{\"_embedded\" : { \"users\" : [ ");
        sb.append(String.join(",", usersStringHalJson));
        sb.append(" ] }");
        return sb.append(footer).toString();
    }
}




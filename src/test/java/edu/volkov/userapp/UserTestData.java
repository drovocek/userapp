package edu.volkov.userapp;

public class UserTestData {

    public final static String layout =
            "{\"phoneNumber\": \"PN\"," +
                    "\"email\": \"mailID@gmail.com\"," +
                    "\"firstName\": \"First_nameID\"," +
                    "\"lastName\": \"Last_NameID\"," +
                    "\"_links\": {" +
                    "\"self\":" +
                    "{\"href\": \"http://localhost/api/users/ID\"}," +
                    "\"user\": {" +
                    "\"href\": \"http://localhost/api/users/ID\"}}}";

    public final static String user1 = createUserHalJson(1);
    public final static String user2 = createUserHalJson(2);
    public final static String user3 = createUserHalJson(3);
    public final static String user4 = createUserHalJson(4);
    public final static String user5 = createUserHalJson(5);
    public final static String user6 = createUserHalJson(6);
    public final static String user7 = createUserHalJson(7);
    public final static String user8 = createUserHalJson(8);
    public final static String user9 = createUserHalJson(9);
    public final static String user10 = createUserHalJson(10);

    private static String createUserHalJson(int userId) {
        String phoneNumber = createPhoneNumber(userId);
        return layout.replaceAll("PN",phoneNumber).replaceAll("ID",String.valueOf(userId));
    }

    private static String createPhoneNumber(int userId) {
        String layout = "+x (xxx) xxx-xx-xx";
        return layout.replaceAll("x", String.valueOf(userId));
    }
}

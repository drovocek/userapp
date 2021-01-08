package edu.volkov.userapp.util;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.to.UserPackage;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserUtil {
    public static UserPackage packUp(PackageType type, User... user) {
        return new UserPackage(
                user.length == 1 ? user[0].id() : null,
                user,
                type.name()
        );
    }

    public static User[] iterableToArray(Iterable<User> users) {
        User[] usersArr = new User[0];
        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList()).toArray(usersArr);
    }
}

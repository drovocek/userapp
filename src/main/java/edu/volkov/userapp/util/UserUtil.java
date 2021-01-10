package edu.volkov.userapp.util;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.to.PackageType;
import edu.volkov.userapp.to.UserPackage;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserUtil {

    public static User[] iterableToArray(Iterable<User> users) {
        User[] usersArr = new User[0];
        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList()).toArray(usersArr);
    }
}

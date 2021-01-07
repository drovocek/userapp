package edu.volkov.userapp.to;

import edu.volkov.userapp.model.User;

public class UserUtil {
    public static UserTo asTo(User user, PackageType type) {
        return new UserTo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmail(),
                type.name()
        );
    }
}

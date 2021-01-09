package edu.volkov.userapp.to;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.util.exception.ApiError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPackage implements Serializable {

    private Integer id;

    private String sessionIdRegex;

    private User[] users;

    private String packageType;

    private ApiError apiError;

    public UserPackage(Integer id, User[] users, String packageType) {
        this.id = id;
        this.users = users;
        this.packageType = packageType;
    }

    public UserPackage(Integer id, String sessionIdRegex, User[] users, String packageType) {
        this.id = id;
        this.sessionIdRegex = sessionIdRegex;
        this.users = users;
        this.packageType = packageType;
    }
}

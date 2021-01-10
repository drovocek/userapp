package edu.volkov.userapp.to;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.util.exception.ApiError;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPackage implements Serializable {

    private PackageType packageType;

    private String sessionIdRegex;

    private Integer[] deletedIds;

    private User[] users;

    private ApiError apiError;
}

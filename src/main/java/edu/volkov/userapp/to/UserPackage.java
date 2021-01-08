package edu.volkov.userapp.to;

import edu.volkov.userapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserPackage {

    private Integer id;

    private User[] users;

    private String packageType;
}

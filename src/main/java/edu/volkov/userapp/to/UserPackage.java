package edu.volkov.userapp.to;

import edu.volkov.userapp.model.User;
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

    private User[] users;

    private String packageType;
}

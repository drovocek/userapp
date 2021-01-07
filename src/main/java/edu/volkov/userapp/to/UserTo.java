package edu.volkov.userapp.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTo {

    private Integer id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String packageType;
}

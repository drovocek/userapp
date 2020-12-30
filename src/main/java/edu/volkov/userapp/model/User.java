package edu.volkov.userapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Access(AccessType.FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractPersistable<Integer> {

    @NotBlank
    @Size(max = 128)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @Size(max = 128)
    private String email;

    @NotBlank
    @Size(max = 128)
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Size(max = 128)
    @Column(name = "last_name")
    private String lastName;

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=' " + getId() + '\'' +
                "phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
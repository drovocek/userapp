package edu.volkov.userapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Access(AccessType.FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractPersistable<Integer> {

    @Column(name = "phone_number")
    @Size(max = 128)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @Size(max = 128)
    private String email;

    @Column(name = "first_name")
    @Size(max = 128)
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 128)
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
package edu.volkov.userapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Access(AccessType.FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractPersistable<Integer> {

    @NotBlank(message = "First name must not be empty")
    @Size(max = 100, message = "First name size must be between 0 and 100")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    @Size(max = 100, message = "Last name size must be between 0 and 100")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Phone number must not be empty")
    @Size(max = 30, message = "Phone number size must be between 0 and 30")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid format of Email")
    @Size(max = 100, message = "Email size must be between 0 and 100")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }

    public User(User user) {
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
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
package edu.volkov.userapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Access(AccessType.FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Persistable<Integer>, Serializable {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @NotBlank(message = "First name must not be empty")
    @Size(max = 100, message = "First name size must be between 0 and 100")
    @Column(name = "first_name")
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last name must not be empty")
    @Size(max = 100, message = "Last name size must be between 0 and 100")
    @Column(name = "last_name")
    private String lastName;

    @Getter
    @Setter
    @NotBlank(message = "Phone number must not be empty")
    @Size(max = 30, message = "Phone number size must be between 0 and 30")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Getter
    @Setter
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid format of Email")
    @Size(max = 100, message = "Email size must be between 0 and 100")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public User(User user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public int id() {
        Assert.notNull(id, "Entity must have id");
        return id;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return id == null;
    }

    //    https://stackoverflow.com/questions/1638723
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(ProxyUtils.getUserClass(o))) {
            return false;
        }
        return id != null;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id;
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
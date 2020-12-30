package edu.volkov.userapp.repository;

import edu.volkov.userapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {

    @RestResource(rel = "by-email", path = "by-email")
    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    @RestResource(rel = "by-lastname", path = "by-lastname")
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    @RestResource(rel = "filter", path = "filter")
//    @Query("SELECT u FROM User u WHERE " +
//            "u.phoneNumber = :phoneNumber AND " +
//            "u.email = LOWER(:email) AND " +
//            "u.firstName =:firstName AND " +
//            "u.lastName = :lastName")
    @Query("SELECT u FROM User u WHERE " +
            "(:phoneNumber is null or u.phoneNumber LIKE :phoneNumber) AND " +
            "(:email is null or u.email LIKE :email) AND " +
            "(:firstName is null or u.firstName LIKE :firstName) AND " +
            "(:lastName is null or u.lastName LIKE :lastName)")
    List<User> getFiltered(
            String phoneNumber,
            String email,
            String firstName,
            String lastName
    );
}

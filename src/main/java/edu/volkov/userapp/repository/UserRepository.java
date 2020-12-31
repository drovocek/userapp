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
    @Query("SELECT u FROM User u WHERE " +
            "(u.phoneNumber LIKE COALESCE(CONCAT('%',:phoneNumber),'%')) AND " +
            "(u.email LIKE COALESCE(CONCAT('%',:email),'%')) AND " +
            "(u.firstName LIKE COALESCE(CONCAT('%',:firstName),'%')) AND " +
            "(u.lastName LIKE COALESCE(CONCAT('%',:lastName),'%'))")
    List<User> getFiltered(
            String phoneNumber,
            String email,
            String firstName,
            String lastName
    );
}

package edu.volkov.userapp.repository;

import edu.volkov.userapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(@Param("id") int id);

    @RestResource(rel = "by-email", path = "by-email")
    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    @RestResource(rel = "filter", path = "filter")
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%',COALESCE(:email,'%'),'%'))) AND " +
            "(LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%',COALESCE(:phoneNumber,'%'),'%'))) AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%',COALESCE(:firstName,'%'),'%'))) AND " +
            "(LOWER(u.lastName) LIKE LOWER(CONCAT('%',COALESCE(:lastName,'%'),'%')))")
    Page<User> getFiltered(
            String email,
            String phoneNumber,
            String firstName,
            String lastName,
            Pageable pageable
    );
}

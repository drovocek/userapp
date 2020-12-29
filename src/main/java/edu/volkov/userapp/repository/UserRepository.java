package edu.volkov.userapp.repository;

import edu.volkov.userapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}

package edu.volkov.userapp;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class UserApplication implements ApplicationRunner {

    private final UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        repository.save(new User("+1 (111) 111-11-11","user1@gmail.com","User","First"));
        repository.save(new User("+2 (222) 222-22-22","user2@gmail.com","User","Second"));
        repository.findAll().forEach(System.out::println);
    }
}

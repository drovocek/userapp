package edu.volkov.userapp.web;

import edu.volkov.userapp.model.User;
import edu.volkov.userapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class UserController {

    UserRepository repository;
    private static final String template = "Hello, %s!";

//   // private final AtomicLong counter = new AtomicLong();
//    //@CrossOrigin(origins = "http://localhost:8080")
//    @GetMapping("/users")
//    public Greeting greeting(@RequestParam(required = false, defaultValue = "World") String name) {
//        System.out.println("==== get greeting ====");
//        return new Greeting(counter.incrementAndGet(), String.format(template, name));
//    }

//    @ModelAttribute("users")
//    public Page<User> users(@PageableDefault(size = 5) Pageable pageable) {
//        return repository.findAll(pageable);
//    }

    @GetMapping("/")
    public String getUsers() {
        return "users2";
    }
}

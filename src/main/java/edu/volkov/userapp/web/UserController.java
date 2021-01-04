package edu.volkov.userapp.web;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class UserController {

    @GetMapping("/")
    public String getUsers() {
        return "users";
    }
}

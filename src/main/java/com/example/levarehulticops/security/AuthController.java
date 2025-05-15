package com.example.levarehulticops.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Неверный логин или пароль.");
        }
        if (logout != null) {
            model.addAttribute("msg", "Вы успешно вышли.");
        }
        return "login";
    }

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        return "welcome";  // src/main/resources/templates/welcome.html
    }
}

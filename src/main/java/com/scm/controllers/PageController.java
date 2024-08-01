package com.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Smart Contact Manager");
        model.addAttribute("gmail", "hoangkhang16112003@gmail.com");
        model.addAttribute("githubRepo", "https://github.com/HoangKhang5207/");
        return "home";
    }

    // about
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        return "about";
    }

    // services
    @GetMapping("/services")
    public String servicesPage(Model model) {
        return "services";
    }

    // contact page
    @GetMapping("/contact")
    public String contactPage(Model model) {
        return "contact";
    }

    // login page
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    // signup page
    @GetMapping("/register")
    public String registerPage(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "register";
    }

    // processing register
    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult,
            HttpSession session) {
        // fetch form data
        // UserForm
        System.out.println(userForm);

        // validate form data
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // save to database
        // userService
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhone(userForm.getPhoneNumber());
        user.setAbout(userForm.getAbout());
        user.setProfilePic("https://upload.wikimedia.org/wikipedia/commons/2/2c/Default_pfp.svg");

        userService.saveUser(user);
        System.out.println("User saved successfully");

        // add the message
        Message message = new Message("Registration successful!", MessageType.green);
        session.setAttribute("message", message);

        return "redirect:/register";
    }
}

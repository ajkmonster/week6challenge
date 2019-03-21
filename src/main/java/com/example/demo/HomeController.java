package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CloudinaryConfig cloudc;
    @GetMapping("/"+"/login")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user", new User());
//        if (result.hasErrors()){
//            return "login";
//        }
//        else {
        userService.saveUser(user);
        model.addAttribute("message","User Account Created");
//        }
        return "login";
    }
    @RequestMapping("/index")
    public String secure(Principal principal, Model model){
        User myuser = ((CustomUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getUser();
        model.addAttribute("myuser",myuser);
        return "index";
    }
    @PostMapping("/search")
    public String searchword(Model model, @RequestParam String search){
        ArrayList<Car> results =(ArrayList<Car>)
                carRepository.findByModelOrManufacturerContainingIgnoreCase(search,search);
        model.addAttribute("cars", results);
        return "carlist";
    }
    @RequestMapping("/profile")
    public String profile(Model model){
        if(userService.getUser() != null) {
            model.addAttribute("user", userService.getUser());
        }
        return "profile";
    }
    @RequestMapping("/carlist")
    public String getAuthorList(Model model) {
        model.addAttribute("cars",carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        if(userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "carlist";
    }

    @RequestMapping("/categorylist")
    public String getCategoryList(Model model) {
        model.addAttribute("cars",carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "categorylist";
    }
    @GetMapping("/addcategory")
    public String bookForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("cars",carRepository.findAll());
        return "categoryform";
    }
    @GetMapping("/addcar")
    public String authorForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }
    @PostMapping("/processcategory")
    public String processForm(@ModelAttribute("category") @Valid Category category, BindingResult result
                             ) {
        if (result.hasErrors()){
            return "categoryform";
        }
        categoryRepository.save(category);
        return "redirect:/carlist";
    }
    @PostMapping("/processcar")
    public String processCarForm(@ModelAttribute("car") @Valid Car car, BindingResult result, @RequestParam("file") MultipartFile file, @RequestParam("hiddenphoto") String picture) {
        if (result.hasErrors()){
            return "carform";
        }
        if(!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                String url = uploadResult.get("url").toString();
                int i = url.lastIndexOf('/');
                url = url.substring(i + 1);
                url = "http://res.cloudinary.com/ajkmonster/image/upload/w_150,h_150/" + url;
                car.setPicture(url);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/carform";
            }
        }
        else {
            if(!picture.isEmpty()) {
                car.setPicture(picture);
            }
            else {
                car.setPicture("");
            }
        }
        car.setUser(userService.getUser());
        carRepository.save(car);
        return "redirect:/carlist";
    }

    @RequestMapping("/detailcar/{id}")
    public String detailsOfAuthor(@PathVariable("id") long id, Model model){
        model.addAttribute("car",carRepository.findById(id).get());
        if(userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "detailcar";
    }
    @RequestMapping("/updatecar/{id}")
    public String updateAuthorList (@PathVariable("id") long id, Model model){
        model.addAttribute("car",carRepository.findById(id).get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }
    @RequestMapping("/delete/{id}")
    public String deleteAuthorList (@PathVariable("id") long id){
       carRepository.deleteById(id);
        return "redirect:/carlist";
    }




}

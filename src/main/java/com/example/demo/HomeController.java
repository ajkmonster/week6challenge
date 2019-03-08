package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }
    @RequestMapping("/admin")
    public String admin(){
        return "admin";
    }
    @RequestMapping("/carlist")
    public String getAuthorList(Model model) {
        model.addAttribute("cars",carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
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
    public String processCarForm(@ModelAttribute("car") @Valid Car car, BindingResult result, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()){
            return "carform";
        }

        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String url = uploadResult.get("url").toString();
            int i = url.lastIndexOf('/');
            url=url.substring(i+1);
            url="http://res.cloudinary.com/ajkmonster/image/upload/w_150,h_150/"+url;
            car.setPicture(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/carform";
        }
        carRepository.save(car);
        return "redirect:/carlist";
    }

    @RequestMapping("/detailcar/{id}")
    public String detailsOfAuthor(@PathVariable("id") long id, Model model){
        model.addAttribute("car",carRepository.findById(id).get());
        return "detailcar";
    }
    @RequestMapping("/updatecar/{id}")
    public String updateAuthorList (@PathVariable("id") long id, Model model){
        model.addAttribute("car",carRepository.findById(id).get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }
    @RequestMapping("/deletecar/{id}")
    public String deleteAuthorList (@PathVariable("id") long id, Model model){
       carRepository.deleteById(id);
        return "redirect:/carlist";
    }




}

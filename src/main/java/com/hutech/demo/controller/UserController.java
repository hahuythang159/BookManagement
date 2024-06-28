package com.hutech.demo.controller;

import com.hutech.demo.model.User;
import com.hutech.demo.service.ExcelExportService;
import com.hutech.demo.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private ExcelExportService excelExportService;



    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/userList";
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
                           @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
                           Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho người dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }
    @PostMapping("/users/enable/{id}")
    public String enableUser(@PathVariable("id") Long userId) {
        userService.enableUser(userId);
        return "redirect:/users";
    }

    @PostMapping("/users/disable/{id}")
    public String disableUser(@PathVariable("id") Long userId) {
        userService.disableUser(userId);
        return "redirect:/users";
    }
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportUserListToExcel() {
        List<User> users = userService.getAllUsers();
        ByteArrayInputStream in = excelExportService.exportUserListToExcel(users);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
}
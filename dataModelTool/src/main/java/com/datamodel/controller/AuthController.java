package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.ChangePasswordDTO;
import com.datamodel.dto.LoginDTO;
import com.datamodel.dto.RegisterDTO;
import com.datamodel.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.login(loginDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", loginDTO.getUsername());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        try {
            authService.register(registerDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authService.changePassword(changePasswordDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/unauthorized")
    public Result<Void> unauthorized() {
        return Result.error(401, "未登录或登录已过期");
    }
}

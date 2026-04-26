package com.datamodel.service;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.dto.ChangePasswordDTO;
import com.datamodel.dto.LoginDTO;
import com.datamodel.dto.RegisterDTO;
import com.datamodel.entity.UserEntity;
import com.datamodel.repository.UserRepository;
import com.datamodel.util.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(LoginDTO loginDTO) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );
        subject.login(token);
        return (String) subject.getSession().getId();
    }

    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
    }

    @Transactional
    public void register(RegisterDTO registerDTO) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            if (userRepository.existsByUsername(registerDTO.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }

            UserEntity user = new UserEntity();
            user.setUsername(registerDTO.getUsername());
            
            String salt = PasswordUtil.generateSalt();
            String encryptedPassword = PasswordUtil.encryptPassword(registerDTO.getPassword(), salt);
            
            user.setPassword(encryptedPassword);
            user.setSalt(salt);
            user.setNickname(registerDTO.getNickname());
            user.setEmail(registerDTO.getEmail());
            user.setStatus(1);

            userRepository.save(user);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            UserEntity user = userRepository.findByUsername(changePasswordDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            if (!PasswordUtil.verifyPassword(
                    changePasswordDTO.getOldPassword(),
                    user.getSalt(),
                    user.getPassword()
            )) {
                throw new RuntimeException("原密码错误");
            }

            String newSalt = PasswordUtil.generateSalt();
            String newEncryptedPassword = PasswordUtil.encryptPassword(
                    changePasswordDTO.getNewPassword(),
                    newSalt
            );

            user.setPassword(newEncryptedPassword);
            user.setSalt(newSalt);

            userRepository.save(user);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }
}

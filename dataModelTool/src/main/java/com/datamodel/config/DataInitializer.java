package com.datamodel.config;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.entity.UserEntity;
import com.datamodel.repository.UserRepository;
import com.datamodel.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initDefaultUser();
    }

    private void initDefaultUser() {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            String defaultUsername = "admin";
            String defaultPassword = "admin123";
            
            if (!userRepository.existsByUsername(defaultUsername)) {
                logger.info("正在创建默认管理员用户...");
                
                UserEntity admin = new UserEntity();
                admin.setUsername(defaultUsername);
                
                String salt = PasswordUtil.generateSalt();
                String encryptedPassword = PasswordUtil.encryptPassword(defaultPassword, salt);
                
                admin.setPassword(encryptedPassword);
                admin.setSalt(salt);
                admin.setNickname("系统管理员");
                admin.setEmail("admin@example.com");
                admin.setStatus(1);
                
                userRepository.save(admin);
                
                logger.info("默认管理员用户创建成功!");
                logger.info("用户名: {}", defaultUsername);
                logger.info("密码: {}", defaultPassword);
            } else {
                logger.info("默认管理员用户已存在");
            }
            
        } catch (Exception e) {
            logger.error("初始化默认用户失败: {}", e.getMessage(), e);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }
}

-- =============================================
-- Data Model Tool 数据库初始化脚本
-- 主数据库: data_model_tool
-- 执行时间: 2026-04-26
-- =============================================

-- 创建主数据库
CREATE DATABASE IF NOT EXISTS `data_model_tool` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `data_model_tool`;

-- =============================================
-- 用户表: 用户信息管理
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密后)',
    `salt` VARCHAR(64) DEFAULT NULL COMMENT '盐值',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 数据源配置表: 存储动态数据源配置
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_data_source` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '数据源名称(唯一标识)',
    `driver_class_name` VARCHAR(255) NOT NULL DEFAULT 'com.mysql.cj.jdbc.Driver' COMMENT '驱动类名',
    `jdbc_url` VARCHAR(500) NOT NULL COMMENT 'JDBC连接URL',
    `username` VARCHAR(100) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置表';

-- =============================================
-- 模拟数据规则表: 存储各表字段的数据生成规则
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_data_rule` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `data_source_name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
    `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
    `field_name` VARCHAR(100) NOT NULL COMMENT '字段名',
    `field_type` VARCHAR(50) DEFAULT NULL COMMENT '字段类型',
    `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型',
    `rule_config` TEXT DEFAULT NULL COMMENT '规则配置(JSON格式)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_source_table_field` (`data_source_name`, `table_name`, `field_name`),
    KEY `idx_data_source_name` (`data_source_name`),
    KEY `idx_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模拟数据规则表';

-- =============================================
-- 初始化数据
-- =============================================

-- 默认管理员用户: admin / admin123
-- 密码使用 MD5(MD5(password) + salt) 加密
-- 实际密码: admin123
-- 盐值: abc123
-- MD5(admin123) = 0192023a7bbd73250516f069df18b500
-- MD5(0192023a7bbd73250516f069df18b500 + abc123) = 请使用LoginService中的加密方式生成
-- 这里使用 Shiro 的 Md5Hash 方式: Md5Hash(password, salt, 2)

-- 密码: admin123, 盐值: abc123, 迭代次数: 2
-- 实际密码hash: 使用工具类生成
INSERT INTO `sys_user` (`username`, `password`, `salt`, `nickname`, `email`, `status`) VALUES
('admin', 'f7769cc591ad157dd31a3f4c5d3ad30', 'abc123', '系统管理员', 'admin@example.com', 1);

-- 示例数据源配置(可选)
-- INSERT INTO `sys_data_source` (`name`, `driver_class_name`, `jdbc_url`, `username`, `password`, `description`, `status`) VALUES
-- ('test_db', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai', 'root', '', '测试数据库', 1);

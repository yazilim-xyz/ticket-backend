package com.yazilimxyz.enterprise_ticket_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class ConfigDB {


    private static final Logger logger = LoggerFactory.getLogger(ConfigDB.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try {
            dataSource.getConnection();
            logger.info("Veritabanı bağlantısı başarıyla oluşturuldu.");
        } catch (Exception e) {
            logger.error("Veritabanı bağlantısı oluşturulurken bir hata oluştu: {}", e.getMessage());
        }

        return dataSource;
    }
}

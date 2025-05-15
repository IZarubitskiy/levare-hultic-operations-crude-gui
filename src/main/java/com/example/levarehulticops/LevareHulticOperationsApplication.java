package com.example.levarehulticops;

import com.example.levarehulticops.users.entity.AccessLevel;
import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LevareHulticOperationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LevareHulticOperationsApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepo) {
        return args -> {
            if (userRepo.count() == 0) {
                // фиксированная соль (29 символов для BCrypt): версия, cost, сами 22-символьная соль
                String fixedSalt = "$2a$10$abcdefghijklmnopqrstuv";
                // формируем хэш для пароля "admin"
                String hash = BCrypt.hashpw("admin", fixedSalt);

                userRepo.save(new User(
                        null,
                        "System Administrator",
                        "Admin role",
                        "admin",
                        hash,
                        AccessLevel.ADMIN
                ));

                userRepo.save(new User(
                        null,
                        "sales",
                        "sales",
                        "sales",
                        hash,
                        AccessLevel.SALES
                ));
                userRepo.save(new User(
                        null,
                        "manager",
                        "manager",
                        "manager",
                        hash,
                        AccessLevel.MANAGER
                ));

                userRepo.save(new User(
                        null,
                        "engineer",
                        "engineer",
                        "engineer",
                        hash,
                        AccessLevel.ENGINEER
                ));

                userRepo.save(new User(
                        null,
                        "supervisor",
                        "supervisor",
                        "supervisor",
                        hash,
                        AccessLevel.SUPERVISOR
                ));
            }
        };
    }
}
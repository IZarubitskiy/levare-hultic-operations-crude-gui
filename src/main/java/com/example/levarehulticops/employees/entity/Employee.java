package com.example.levarehulticops.employees.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полное имя сотрудника
     */
    @Column(nullable = false)
    private String name;

    /**
     * Должность или описание роли в рамках бизнеса
     */
    @Column(nullable = false)
    private String position;

    /**
     * Логин для аутентификации (уникальный)
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Хэш пароля для аутентификации
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Системная роль (только одна) — определяет уровень доступа
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccessLevel role;
}

package com.example.levarehulticops.users.entity;

import com.example.levarehulticops.workorders.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the employee
     */
    @Column(nullable = false)
    private String name;

    /**
     * Position or business role description
     */
    @Column(nullable = false)
    private String position;

    /**
     * Login for authentication (unique)
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Password hash for authentication
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * System role (single) — defines access level
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessLevel role;

    /**
     * Clients that this user services (client codes defined by enum).
     * If the user services no clients, this set remains empty.
     */
    @ElementCollection(targetClass = Client.class)
    @CollectionTable(
            name = "user_clients",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "client")
    @Enumerated(EnumType.STRING)
    private Set<Client> servicedClients = new HashSet<>();
}

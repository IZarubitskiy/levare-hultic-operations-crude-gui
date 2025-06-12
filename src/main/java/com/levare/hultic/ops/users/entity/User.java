package com.levare.hultic.ops.users.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a basic employee user with name and position only.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    /**
     * Full name of the employee
     */
    private String name;

    /**
     * Position or business role description
     */
    private String position;
}

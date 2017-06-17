package com.highplume.service;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Hugh on 1/2/2017.
 */
@Entity

@NamedQueries({
        @NamedQuery(name = Role.FIND_BY_ID, query = "SELECT b FROM Role b WHERE b.id = :id"),
        @NamedQuery(name = Role.FIND_BY_NAME, query = "SELECT b FROM Role b WHERE b.name = :name")
})

public class Role {

    public static final String FIND_BY_ID = "Role.findByID";
    public static final String FIND_BY_NAME = "Role.findByName";

    @Id
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(length = 255)
    private String description;

    public Role(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Role() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

package com.highplume.service;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Hugh on 3/17/2017.
 */

@Entity

@NamedQueries({
        @NamedQuery(name = TUType.FIND_ALL, query = "SELECT b FROM TUType b ORDER BY b.name"),
        @NamedQuery(name = TUType.FIND_GENERAL, query = "SELECT b FROM TUType b WHERE b.name = 'General'")

})

public class TUType {
    public static final String FIND_ALL = "TUType.FIND_ALL";
    public static final String FIND_GENERAL = "TUType.FIND_GENERALS";

    @Id
    @GeneratedValue
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(length = 255)
    private String description;

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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TUType() {
    }

    public TUType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TUType)) return false;
        TUType tuType = (TUType) o;
        return Objects.equals(id, tuType.id) &&
                Objects.equals(name, tuType.name) &&
                Objects.equals(description, tuType.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}

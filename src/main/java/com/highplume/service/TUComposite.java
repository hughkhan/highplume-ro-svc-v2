package com.highplume.service;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Hugh on 3/17/2017.
 */
@Entity

@NamedQueries({
        @NamedQuery(name = TUComposite.FIND_ALL_BY_CORPID, query = "SELECT b FROM TUComposite b WHERE b.corpid = :corpID ORDER BY b.name"),
        @NamedQuery(name = TUComposite.FIND_ACTIVE_BY_CORPID, query = "SELECT b FROM TUComposite b WHERE b.corpid = :corpID AND b.active = TRUE")
})

public class TUComposite {
    public static final String FIND_ALL_BY_CORPID = "TUComposite.FIND_ALL_BY_CORPID";
    public static final String FIND_ACTIVE_BY_CORPID = "TUComposite.FIND_ACTIVE_BY_CORPID";

    @Id
    @GeneratedValue
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 25)
    private String corpid;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false)
    private boolean active;

    public TUComposite() {
    }

    public TUComposite(String id) {
        this.id = id;
    }

    public TUComposite(String corpid, String name, boolean active) {
        this.corpid = corpid;
        this.name = name;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {return active; }

    public void setActive(boolean active) {this.active = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TUComposite)) return false;
        TUComposite that = (TUComposite) o;
        return Objects.equals(corpid, that.corpid) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corpid, name);
    }
}

package com.highplume.service;

import javax.persistence.*;
import java.util.Objects;
import com.highplume.service.TUType;

/**
 * Created by Hugh on 3/17/2017.
 */

@Entity

@NamedQueries({
        @NamedQuery(name = TU.FIND_ALL_BY_TUCOMPOSITEID, query = "SELECT b FROM TU b WHERE b.tuCompositeId = :tucompositeid ORDER BY b.ratio DESC"),
        @NamedQuery(name = TU.FIND_ALL_BY_TUCOMPOSITEID_JOIN, query
                = "SELECT b.id, b.tutypeId, b.ratio, p.name, p.description FROM TU b INNER JOIN TUType p ON b.tutypeId = p.id where b.tuCompositeId = :tucompositeid ORDER BY b.ratio DESC")
        })

public class TU {
    public static final String FIND_ALL_BY_TUCOMPOSITEID = "TU.FIND_ALL_BY_TUCOMPOSITEID";
    public static final String FIND_ALL_BY_TUCOMPOSITEID_JOIN = "TU.FIND_ALL_BY_TUCOMPOSITEID_JOIN";


    @Id
    @GeneratedValue
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 25)
    private String tuCompositeId;
    @Column(nullable = false, length = 25)
    private String tutypeId;
    private float ratio;

    public TU() {
    }

    public TU(String tuCompositeId, String tutypeId, float ratio) {
        this.tuCompositeId = tuCompositeId;
        this.tutypeId = tutypeId;
        this.ratio = ratio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTuCompositeId() {
        return tuCompositeId;
    }

    public void setTuCompositeId(String tuCompositeId) {
        this.tuCompositeId = tuCompositeId;
    }

    public String getTutypeId() {
        return tutypeId;
    }

    public void setTutypeId(String tutypeId) {
        this.tutypeId = tutypeId;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TU)) return false;
        TU tu = (TU) o;
        return Objects.equals(tuCompositeId, tu.tuCompositeId) &&
                Objects.equals(tutypeId, tu.tutypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuCompositeId, tutypeId);
    }
}

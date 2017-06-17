package com.highplume.service;

//import org.eclipse.persistence.annotations.*;
import org.eclipse.persistence.config.CacheIsolationType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hugh on 7/13/2016.
 */
@Entity
//@Cacheable(false)
@org.eclipse.persistence.annotations.Cache(isolation=CacheIsolationType.ISOLATED)
public class StarGiven {

    @Id
    @GeneratedValue
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 25)
    private String givingMemberID;
    @Column(nullable = false, length = 25)
    private String receivingMemberID;
    @Column(nullable = false, length = 25)
    private String tuTypeID;
    @Temporal(TemporalType.TIMESTAMP)
    private Date givenDate;

    public StarGiven() {
    }

    public StarGiven(String givingMemberID, String receivingMemberID, String tuTypeID, Date givenDate) {

        this.givingMemberID = givingMemberID;
        this.receivingMemberID = receivingMemberID;
        this.tuTypeID = tuTypeID;
        this.givenDate = givenDate;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGivingMemberID() {
        return givingMemberID;
    }

    public void setGivingMemberID(String givingMemberID) {
        this.givingMemberID = givingMemberID;
    }

    public String getReceivingMemberID() {
        return receivingMemberID;
    }

    public void setReceivingMemberID(String receivingMemberID) {
        this.receivingMemberID = receivingMemberID;
    }

    public String getTuTypeID() {
        return tuTypeID;
    }

    public void setTuTypeID(String tuTypeID) {
        this.tuTypeID = tuTypeID;
    }	
	
    public Date getGivenDate() {
        return givenDate;
    }

    public void setGivenDate(Date givenDate) {
        this.givenDate = givenDate;
    }
}
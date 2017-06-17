package com.highplume.service;

import javax.persistence.Embeddable;

/**
 * Class to create primary key to keep unique users per corporation.
 *         --
 * Created by Hugh on 12/24/2016.
 */
//@Embeddable
public class CorpUserPK {

  // ======================================
  // =             Attributes             =
  // ======================================

  private String corpID;
  private String userID;

  // ======================================
  // =            Constructors            =
  // ======================================

  public CorpUserPK() {
  }

  public CorpUserPK(String corpID, String userID) {
    this.corpID = corpID;
    this.userID = userID;
  }

  // ======================================
  // =          Getters & Setters         =
  // ======================================

  public String getCorpID() {
    return corpID;
  }

  public void setCorpID(String corpID) {
    this.corpID = corpID;
  }

  public String getLanguage() {
    return userID;
  }

  public void setLanguage(String userID) {
    this.userID = userID;
  }

  // ======================================
  // =         hash, equals, toString     =
  // ======================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CorpUserPK corpUserPK = (CorpUserPK) o;

    if (!userID.equals(corpUserPK.userID)) return false;
    if (!corpID.equals(corpUserPK.corpID)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = corpID.hashCode();
    result = 31 * result + userID.hashCode();
    return result;
  }
}
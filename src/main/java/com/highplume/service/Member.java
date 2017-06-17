package com.highplume.service;

import org.eclipse.persistence.config.CacheIsolationType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;


@Entity
//@Cacheable(false)
@org.eclipse.persistence.annotations.Cache(isolation= CacheIsolationType.ISOLATED)
@IdClass(CorpUserPK.class)
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = Member.FIND_ALL, query = "SELECT b FROM Member b"),
        @NamedQuery(name = Member.FIND_ALL_BY_CORPID, query = "SELECT b FROM Member b WHERE b.corpID = :corpID ORDER BY b.nameFirst ASC"),
        @NamedQuery(name = Member.FIND_ALL_BY_CORPID_DEPTID, query = "SELECT b FROM Member b WHERE b.corpID = :corpID AND b.departmentID = :deptID ORDER BY b.nameFirst ASC"),
        @NamedQuery(name = Member.COUNT, query = "SELECT COUNT(b) FROM Member b"),
        @NamedQuery(name = Member.FIND_BY_UID, query = "SELECT b FROM Member b WHERE b.userID = :uid"),
        @NamedQuery(name = Member.FIND_BY_ID, query = "SELECT b FROM Member b WHERE b.id = :id"),
        @NamedQuery(name = Member.FIND_BY_PWD, query = "SELECT b FROM Member b WHERE b.PWD = :pwd")

})
public class Member {

  // ======================================
  // =             Attributes             =
  // ======================================

  public static final String FIND_ALL = "Member.findAll";
  public static final String FIND_ALL_BY_CORPID = "Member.findAllByCorpID";
  public static final String FIND_ALL_BY_CORPID_DEPTID = "Member.findAllByCorpIDDeptID";
//  public static final String FIND_ALL_BY_DEPTID = "Member.findAllByDeptID";
  public static final String COUNT = "Member.count";
  public static final String FIND_BY_UID = "Member.findByUID";
  public static final String FIND_BY_ID = "Member.findByID";
  public static final String FIND_BY_PWD = "Member.findByPWD";



  @GeneratedValue
  @Column(nullable = false, length = 25)
  private String id;
  @Column(nullable = false, length = 50)
  private String nameFirst;
  @Column(nullable = false, length = 50)
  private String nameMiddle;
  @Column(nullable = false, length = 50)
  private String nameLast;
  @Id
  @Column(nullable = false, length = 25)
  private String corpID;
  @Id
  @Column(nullable = false, length = 25)
  private String userID;
  @Column(nullable = false, length = 50)
  private String PWD;
  @Column(nullable = false, length = 50)
  private String hash;
  @Column(nullable = false, length = 50)
  private String email;
  @Column(nullable = false, length = 25)
  private String departmentID;
  @Column(nullable = false, length = 25)
  private String roleID;
  @Column(nullable = false)
  private Boolean active;
  @Column(length = 50)
  private String activationCode;


  // ======================================
  // =            Constructors            =
  // ======================================

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Member() {
  }

/*  public Member(String id, String nameFirst, String nameMiddle, String nameLast, String userID, String corpID, String PWD, String hash) {
    this.id = id;
    this.nameFirst = nameFirst;
    this.nameMiddle = nameMiddle;
    this.nameLast = nameLast;
    this.userID = userID;
    this.corpID = corpID;
    this.PWD = PWD;
    this.hash = hash;
  }*/

  public Member(String nameFirst, String nameMiddle, String nameLast, String corpID, String userID,
                String PWD, String hash, String email, String departmentID, String roleID, Boolean active, String activationCode) {
    this.nameFirst = nameFirst;
    this.nameMiddle = nameMiddle;
    this.nameLast = nameLast;
    this.corpID = corpID;
    this.userID = userID;
    this.PWD = PWD;
    this.hash = hash;
    this.email = email;
    this.departmentID = departmentID;
    this.roleID = roleID;
    this.active = active;
    this.activationCode = activationCode;
  }

    public String getUserID() {
        return userID;
    }

    public String getCorpID() {
        return corpID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setCorpID(String corpID) {
        this.corpID = corpID;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


// ======================================
  // =          Getters & Setters         =
  // ======================================

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getnameFirst() {
    return nameFirst;
  }

  public void setnameFirst(String nameFirst) {
    this.nameFirst = nameFirst;
  }

  public String getnameMiddle() {
    return nameMiddle;
  }

  public void setnameMiddle(String nameMiddle) {
    this.nameMiddle = nameMiddle;
  }

  public String getnameLast() {
    return nameLast;
  }

  public void setnameLast(String nameLast) {
    this.nameLast = nameLast;
  }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID( String departmentID) {
        this.departmentID = departmentID;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) &&
                Objects.equals(nameFirst, member.nameFirst) &&
                Objects.equals(nameMiddle, member.nameMiddle) &&
                Objects.equals(nameLast, member.nameLast) &&
                Objects.equals(corpID, member.corpID) &&
                Objects.equals(userID, member.userID) &&
                Objects.equals(PWD, member.PWD) &&
                Objects.equals(hash, member.hash) &&
                Objects.equals(email, member.email) &&
                Objects.equals(departmentID, member.departmentID) &&
                Objects.equals(roleID, member.roleID) &&
                Objects.equals(active, member.active) &&
                Objects.equals(activationCode, member.activationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameFirst, nameMiddle, nameLast, corpID, userID, PWD, hash, email, departmentID, roleID, active, activationCode);
    }


}

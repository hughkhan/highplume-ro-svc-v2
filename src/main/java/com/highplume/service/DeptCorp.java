package com.highplume.service;

import javax.persistence.*;
//import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;



@Entity
@NamedQueries({
        @NamedQuery(name = DeptCorp.FIND_ALL_BY_CORPID, query = "SELECT b FROM DeptCorp b WHERE b.corpID = :corpID"),
        @NamedQuery(name = DeptCorp.FIND_BY_ID, query = "SELECT b FROM DeptCorp b WHERE b.id = :id")
})
/**
 * Created by Hugh on 12/8/2016.
 */
public class DeptCorp {
	
  // ======================================
  // =             Attributes             =
  // ======================================

  public static final String FIND_ALL_BY_CORPID = "DeptCorp.findAllByCorpID";
  public static final String FIND_BY_ID = "DeptCorp.findByID";
  
//@formatter:off	
	
	
  @Id
  @GeneratedValue
  @Column(nullable = false, length = 25)
  private String id;
  @Column(nullable = false, length = 50)
  private String DeptName;
  @Column(nullable = false, length = 25)
  private String corpID;

  public DeptCorp() {}


public DeptCorp(String deptName, String corpID) {
    this.DeptName = deptName;
    this.corpID = corpID;
}
public String getId() {
    return id;
}
public void setId(String id) {
    this.id = id;
}
public String getDeptName() {
    return DeptName;
}
public void setDeptName(String deptName) {
    DeptName = deptName;
}
public String getCorpID() {
    return corpID;
}
public void setCorpID(String corpID) {
    this.corpID = corpID;
}

@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeptCorp deptCorp = (DeptCorp) o;
    return Objects.equals(id, deptCorp.id) &&
            Objects.equals(corpID, deptCorp.corpID);
}@Override
public int hashCode() {
    return Objects.hash(id, corpID);
}}



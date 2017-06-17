package com.highplume.service;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

//@formatter:off

@Entity

@NamedQueries({
        @NamedQuery(name = Corp.FIND_BY_ID, query = "SELECT b FROM Corp b WHERE b.id = :id")
})
/**
 * Created by Hugh on 12/8/2016.
 */
public class Corp {

  public static final String FIND_BY_ID = "Corp.findByID";

  @Id
  @GeneratedValue
  @Column(nullable = false, length = 25)
  private String id;
  @Column(nullable = false, length = 50)
  private String Name;
  @Column(nullable = false, length = 50)
  private String Website;


  public Corp() {
  }
  public Corp(String name, String website) {
    Name = name;
    Website = website;
  }

  public String getName() {
    return Name;
}
public void setName(String name) {
    Name = name;
}
public String getWebsite() {
    return Website;
}
public void setWebsite(String website) {
    Website = website;
}
public String getId() {
    return id;
}@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Corp corp = (Corp) o;
    return Objects.equals(id, corp.id) &&
            Objects.equals(Name, corp.Name) &&
            Objects.equals(Website, corp.Website);
}@Override
public int hashCode() {
    return Objects.hash(id, Name, Website);
}}
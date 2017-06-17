package com.highplume.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Antonio Goncalves
 *         APress Book - Beginning Java EE 7 with Glassfish 4
 *         http://www.apress.com/
 *         http://www.antoniogoncalves.org
 *         --
 */
@XmlRootElement
@XmlSeeAlso(Member.class)
public class Members extends ArrayList<Member> {

  // ======================================
  // =            Constructors            =
  // ======================================

  public Members() {
    super();
  }

  public Members(Collection<? extends Member> c) {
    super(c);
  }

  // ======================================
  // =          Getters & Setters         =
  // ======================================

  @XmlElement(name = "member")
  public List<Member> getMembers() {
    return this;
  }

  public void setMembers(List<Member> members) {
    this.addAll(members);
  }
}
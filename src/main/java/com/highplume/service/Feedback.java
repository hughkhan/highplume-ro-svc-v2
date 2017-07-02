package com.highplume.service;

import javax.persistence.*;
import java.util.Objects;
import java.util.Date;

//@formatter:off

@Entity

@NamedQueries({
        @NamedQuery(name = Feedback.FIND_BY_ID, query = "SELECT b FROM Feedback b WHERE b.id = :id")
})
/**
 * Created by Hugh on 12/8/2016.
 */
public class Feedback {

  public static final String FIND_BY_ID = "Feedback.findByID";

  @Id
  @GeneratedValue
  @Column(nullable = false, length = 25)
  private String id;
  @Column(nullable = false, length = 25)
  private String corpID;
  @Column(nullable = false, length = 2)
  private String type;
  @Column(nullable = true, length = 10)
  private String cat;
  @Column(nullable = false, length = 512)
  private String info;
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;


  public Feedback() {
  }

  public Feedback(String corpID, String type, String cat, String info, Date date) {
    this.corpID = corpID;
    this.type = type;
    this.cat = cat;
    this.info = info;
    this.date = date;    
  }
	public String getId() {
		return id;
	}

    public String getCorpID() {return corpID;}
    public void setCorpID(String corpID) {this.corpID = corpID;}

    public String getType() {
		return this.type;
	}
	public void setType(String type){
		this.type = type;
	}
	
	public String getCat() {
		return this.cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getInfo() {
		return this.info;
	}
	public void setInfo(String info) {
		this.info = info;
	}

	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Feedback feedback = (Feedback) o;
    return Objects.equals(id, feedback.id) &&
            Objects.equals(type, feedback.type) &&
            Objects.equals(cat, feedback.cat);
}
@Override
public int hashCode() {
    return Objects.hash(id, type, cat);
}
}
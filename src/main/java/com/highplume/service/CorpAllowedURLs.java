package com.highplume.service;

import javax.persistence.*;
//import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;



@Entity
@NamedQueries({
        @NamedQuery(name = CorpAllowedURLs.FIND_ALL_BY_CORPID, query = "SELECT b FROM CorpAllowedURLs b WHERE b.corpID = :corpID"),
        @NamedQuery(name = CorpAllowedURLs.FIND_CORPID_BY_URL, query = "SELECT b FROM CorpAllowedURLs b WHERE b.allowedURL = :allowedURL"),
})
/**
 * Created by Hugh on 12/8/2016.
 */
public class CorpAllowedURLs {
	
  public static final String FIND_ALL_BY_CORPID = "CorpAllowedURLs.findAllByCorpID";
  public static final String FIND_CORPID_BY_URL = "CorpAllowedURLs.findCorpIDByURL";
  
//@formatter:off	

    @Id
    @GeneratedValue
    @Column(nullable = false, length = 25)
    private String id;
    @Column(nullable = false, length = 25)
    private String corpID;
    @Column(nullable = false, length = 50)
    private String allowedURL;

	public CorpAllowedURLs() {}

	public CorpAllowedURLs(String corpID, String allowedURL) {
		this.corpID = corpID;
		this.allowedURL = allowedURL;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCorpID() {
		return corpID;
	}
	public void setCorpID(String corpID) {
		this.corpID = corpID;
	}
	public String getAllowedURL() {
		return allowedURL;
	}
	public void setAllowedURL(String allowedURL) {
		this.allowedURL = allowedURL;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CorpAllowedURLs corpAllowedURLs = (CorpAllowedURLs) o;
		return Objects.equals(id, corpAllowedURLs.id) &&
				Objects.equals(corpID, corpAllowedURLs.corpID) &&
				Objects.equals(allowedURL, corpAllowedURLs.allowedURL);
	}@Override
	public int hashCode() {
		return Objects.hash(id, corpID, allowedURL);
}}



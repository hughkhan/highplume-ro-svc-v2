package com.highplume.service;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;


import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
//import java.net.URI;
//import java.net.URLDecoder;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;

//import java.util.*;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.activation.*;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//@formatter:off

@Path("/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class ROService {

  // ======================================
  // =             Attributes             =
  // ======================================

  @PersistenceContext(unitName = "chapter15PU")
  private EntityManager em;
  @Context
  private UriInfo uriInfo;

  String prefix = "RO-";    //logFile = "RO-highplumeROService.log";
    String logFile = "highplumeService.log";
    String logTrigger = "highplumelog.trigger";
  // ======================================
  // =           Public Methods           =
  // ======================================

    /*-----------------------------*/

	public void log (String output){
		utilLog(prefix, output);
	}

    /*-----------------------------*/

	public void log (String output, int logLevel){
		utilLog(prefix, output, logLevel);
	}

    /*-----------------------------*/


    public void utilLog (String prefix, String output){
		utilLog(prefix, output, 0);
	}


    public void utilLog (String prefix, String output, int logLevel){
//        String path = ROService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File f1 = new File("."); 				// f is the current directory; where the JVM was launched  => C:\Users\Latitude Owner\Documents\payara41\glassfish\domains\domain1\config\.
		String path = f1.getAbsolutePath();
        String decodedPath = "";
        try{
            decodedPath = URLDecoder.decode(path, "UTF-8");
        }catch (UnsupportedEncodingException e){
             e.printStackTrace();
        }

		decodedPath = decodedPath.substring(0, decodedPath.indexOf("config")) + "logs\\";

        File f = new File(decodedPath+logTrigger);				//if logTrigger file exists, log everything.  Otherwise only log level 0 stuff.
        if(f.exists() || logLevel == 0) {
            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(decodedPath+prefix+logFile, true))) {
                bw.write(now.toString() + ":" + output);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /*------------------------------*/

  /**
   * JSON : curl -X GET -H "Accept: application/json" http://localhost:8080/chapter15-service-1.0/rs/members -v
   * XML  : curl -X GET -H "Accept: application/xml" http://localhost:8080/chapter15-service-1.0/rs/members -v
   */
  @GET
  @Path("members/{corpID}/{userToken}")
  public Response getAllMembers(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {

    if (!validUserAndLevel(corpID, userToken, null,"201"))
    {
        return Response.status(500)
                        .entity("<html lang=\"en\"><body><h1>  ERROR:  Unauthorized  </h1></body></html>\"")
                        .build();
    }
    TypedQuery<Member> query = em.createNamedQuery(Member.FIND_ALL, Member.class);
    Members members = new Members(query.getResultList());
    return Response.ok(members).build();
  }
/*------------------------*/
  @GET
  @Path("users/{corpID}/{userToken}")
  @Produces("application/json")
  public String getAllUsers(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {
    TypedQuery<Member> memQuery = em.createNamedQuery(Member.FIND_ALL, Member.class);
    List<Member> members = memQuery.getResultList();

    if (!validUserAndLevel(corpID, userToken, null,"201"))
		return "{\"users\": []}";
		
    String retStr="{\"users\": [";
		
    for (int i=0; i < members.size(); i++) {
      retStr += "{\"ID\": \"" + members.get(i).getId()+
                "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                "\", \"nameLast\": \"" + members.get(i).getnameLast() +
                "\", \"userID\": \"" + members.get(i).getUserID() +
                "\", \"corpID\": \"" + members.get(i).getCorpID() +
                "\", \"PWD\": \"" + members.get(i).getPWD() +
                "\", \"hash\": \"" + members.get(i).getHash() +
                "\", \"email\": \"" + members.get(i).getEmail() +
                "\", \"department\": \"" + members.get(i).getDepartmentID() +
                "\", \"roleID\": \"" + members.get(i).getRoleID() +
                "\", \"active\": \"" + members.get(i).getActive() +
                "\", \"activationCode\": \"" + members.get(i).getActivationCode() +
                "\"},";
    }
    if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
    retStr += "]}";

    return retStr;
  }

    /*------------------------*/
	
/*
-------Case insensitive index--------------

 CREATE INDEX idx_groups_name ON groups lower(name);

-----Stored Procedure-------
CREATE OR REPLACE FUNCTION get_avg(param_corpID text)
RETURNS real AS $$
DECLARE
cnt real;

BEGIN
create temporary table if not exists temp_gids (gid int not null) on commit drop;

insert into temp_gids (gid) SELECT count(RECEIVINGMEMBERID) AS NUMOFSTARSREC
FROM STARGIVEN
INNER JOIN MEMBER ON RECEIVINGMEMBERID = MEMBER.ID
WHERE MEMBER.CORPID = param_corpID
GROUP BY MEMBER.NAMEFIRST, MEMBER.NAMELAST, MEMBER.ID
ORDER BY NUMOFSTARSREC DESC;

select into cnt avg(gid) from temp_gids;

return cnt;

END; $$
LANGUAGE plpgsql;

------stored procedure------------

select get_avg('1');
------run stored procedure------------

getAllUsersByCorp
_usersByDept
getAllUsersByDept
getdDeptUserData
getAvgGiving
getGivers
getReceivers
msds
_receiverTotalsPerDept
_giverTotalsPerDept
IdUserNameValue
_getUserDecile
_getPercentRank
_giverValuesRanking
_ReceiverValuesRanking
getCorpvalues
getInfluence
_deptGiverTotals
_deptRecTotals
getDeptTotals
getStars
test
addQualities()
addQualities
initDB
getQualities
getQualitiesComposite
qualityProfile
quality
giveStar
addMember
changePwd
inactiveUser
changeUserDept
userInfo
loginMember
_deptByCorp
getDeptByCorp
GetCorpID
validateEmailP
validateEmail
sendMailTLS




members
users
usersbycorp/{corpID}
usersbydept/{corpID}/{departmentID}
deptuserdata/{corpID}
giver/{corpID}/{receiverID}
receivers/{corpID}/{giverID}
    _msds (double[] x, int n)
     _receiverTotalsPerDept(String corpID, String UserID)
     _giverTotalsPerDept(String corpID, String UserID)
     IdUserNameValue _getUserDecile(java.util.ArrayList<IdUserNameValue> userArray, String userID)

influence/{corpID}/{gr}/{wl}/{userID: .*}
influence/{corpID}/{wl}/{userID: .*}
    _deptGiverTotals(String corpID)
    _deptRecTotals(String corpID)
depttotals/{corpID}
getstars/{corpID}/{givingOrReceiving}
test
initdb
givestar/{givingMemberID}/{receivingMemberID}
addmember
changepwd
userinfo
login -POST
    _deptByCorp(String corpID)
getdeptbycorp
getcorpid
validateemailp
validateemail
sendmailtls
*/

    /*------------------------*/

/*  @GET
  @Path("usersbycorp/{corpID}/{userToken}")
  @Produces("application/json")
  public String getAllUsersByCorp(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {

    if (!validUserAndLevel(corpID, userToken, null, "201"))
		return "{\"users\": []}";
	
    List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();

    String retStr="{\"users\": [";

    for (int i=0; i < members.size(); i++) {
      retStr += "{\"ID\": \"" + members.get(i).getId()+
                "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                "\", \"nameLast\": \"" + members.get(i).getnameLast() +
                "\", \"userID\": \"" + members.get(i).getUserID() +
//                "\", \"PWD\": \"" + members.get(i).getPWD() +
//                "\", \"hash\": \"" + members.get(i).getHash() +
//                "\", \"email\": \"" + members.get(i).getEmail() +
//                "\", \"department\": \"" + members.get(i).getDepartmentID() +
                "\"},";
    }
    if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
    retStr += "]}";

    return retStr;
  }*/

    /*------------------------*/

  public List<Member> _usersByDept(String corpID, String departmentID) {

    return em.createNamedQuery(Member.FIND_ALL_BY_CORPID_DEPTID, Member.class)
												.setParameter("corpID", corpID)
												.setParameter("deptID", departmentID)
												.getResultList();
  }
    /*------------------------*/


  @GET
  @Path("usersbydept/{corpID}/{userToken}/{departmentID}")
  @Produces("application/json")
  public String getAllUsersByDept(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, @PathParam("departmentID") String departmentID) {

    if (!validUserAndLevel(corpID, userToken, null, "401"))
		return "{\"users\": []}";

	List<Member> members = _usersByDept(corpID, departmentID);

    String retStr="{\"users\": [";

    for (int i=0; i < members.size(); i++) {
      retStr += "{\"ID\": \"" + members.get(i).getId()+
                "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                "\", \"nameLast\": \"" + members.get(i).getnameLast() +
//                "\", \"userID\": \"" + members.get(i).getUserID() +
                "\", \"active\": \"" + members.get(i).getActive().toString() +
//                "\", \"PWD\": \"" + members.get(i).getPWD() +
//                "\", \"hash\": \"" + members.get(i).getHash() +
//                "\", \"email\": \"" + members.get(i).getEmail() +
//                "\", \"department\": \"" + members.get(i).getDepartmentID() +
                "\"},";
    }
    if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
    retStr += "]}";

    return retStr;
  }

    /*------------------------*/
    @GET
    @Path("deptuserdata/{corpID}/{userToken}")
    @Produces("application/json")
    public String getdDeptUserData(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {    //Data about each department
        DecimalFormat formatter = new DecimalFormat("#0.00");
        String retStr="{\"data\": {\"giver\": [";

        if (!validUserAndLevel(corpID, userToken, null, "201"))
		    return "{\"data\": {\"giver\": []}}";

  		List<DeptCorp> departments = _deptByCorp(corpID);
//        List<Member>[] membersPerDept = (List<Member>[])new Object[departments.size()];
        List<List<Member>> membersPerDept = new java.util.ArrayList<>();	//List of list of user records per department

		for (int i = 0; i < departments.size(); i++)	//Load the List with a list of users per department
		{
            List<Member> tempMem = _usersByDept(corpID, departments.get(i).getId());
			if (tempMem.size() != 0) membersPerDept.add(tempMem);
		}

        List<Object[]> totalsByDept = _deptGiverTotals(corpID);	//Total TUs given by each department

        for (int i = 0; i < totalsByDept.size(); i++) {
            int j = 0; double deptAvgPerGiver=0.0;

            while ((j < membersPerDept.size()) && (!totalsByDept.get(i)[2].equals(membersPerDept.get(j).get(0).getDepartmentID())) ){ //giverTotal[0]=deptname,[1]=total,[2]=deptID
                j++;
            }
            deptAvgPerGiver = Double.parseDouble(totalsByDept.get(i)[1].toString())/(membersPerDept.get(j).size());

            retStr += "{\"Dept\": \"" + totalsByDept.get(i)[0] + "\", \"Average\": \"" + Math.round(deptAvgPerGiver) + "\"},";
        }
        if (totalsByDept.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "], \"receiver\": [";

        totalsByDept = _deptRecTotals(corpID);		//Total TUs received by each department

        for (int i=0; i < totalsByDept.size(); i++) {
            int j=0;
            while ((j < membersPerDept.size()) && (!totalsByDept.get(i)[2].equals(membersPerDept.get(j).get(0).getDepartmentID())) ){ //giverTotal[0]=deptname,[1]=total,[2]=deptID
                j++;
            }

            double deptAvgPerGiver = Double.parseDouble(totalsByDept.get(i)[1].toString())/(membersPerDept.get(j).size());
            retStr += "{\"Dept\": \"" + totalsByDept.get(i)[0] + "\", \"Average\": \"" + Math.round(deptAvgPerGiver) + "\"},";
        }
        if (totalsByDept.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}}";

        return retStr;
	}

    /*------------------------*/

    public Double getAvgGiving(String corpID){

        String queryStr =
            "SELECT COUNT(GIVINGMEMBERID) AS NUMOFSTARSGIVEN " +
            "FROM STARGIVEN " +
            "INNER JOIN MEMBER ON GIVINGMEMBERID = MEMBER.ID " +
            "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
            "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
            "WHERE MEMBER.CORPID = '"+corpID+"' " +
            "AND TUCOMPOSITE.CORPID = '"+corpID+"' " +
            "AND TUCOMPOSITE.ACTIVE = true " +
            "GROUP BY MEMBER.ID " +
            "ORDER BY NUMOFSTARSGIVEN DESC";

          List<Object> results = em.createNativeQuery(queryStr).getResultList();
          double total = 0.0;
          for (int i=0; i<results.size(); i++){
              total += Double.parseDouble(results.get(i).toString());
          }
          return new Double(total/results.size());
    }

    /*------------------------*/

    @GET
    @Path("giver/{corpID}/{userToken}/{receiverID}")
    @Produces("application/json")
    public String getGivers(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, @PathParam("receiverID") String receiverID) {
        String queryStr, retStr="{\"data\": [";

        if (!validUserAndLevel(corpID, userToken, null,"301"))            //Make sure the user is at least a dept admin
            return "{\"data\": []}";

        String deptRestrictionClause = "";
        if (getUserRoleID(userToken).equals("301"))
            deptRestrictionClause = "AND RECEIVINGMEMBER.DEPARTMENTID = '" + getUserDeptID(userToken) + "' ";

        queryStr = "SELECT MEMBER.ID, MEMBER.NAMEFIRST, MEMBER.NAMEMIDDLE, MEMBER.NAMELAST, COUNT(GIVINGMEMBERID) AS NUMOFSTARSGIVEN " +
                "FROM STARGIVEN " +
                "INNER JOIN MEMBER ON GIVINGMEMBERID = MEMBER.ID " +
                "INNER JOIN MEMBER AS RECEIVINGMEMBER ON STARGIVEN.RECEIVINGMEMBERID = RECEIVINGMEMBER.ID " +
                "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                "WHERE STARGIVEN.receivingmemberid = '" + receiverID + "' "  +
                "AND MEMBER.CORPID = '" + corpID + "' "  +
                deptRestrictionClause +
                "AND TUCOMPOSITE.CORPID = '" + corpID + "' "  +
                "AND TUCOMPOSITE.ACTIVE = true " +
                "GROUP BY MEMBER.ID, MEMBER.NAMEFIRST, MEMBER.NAMELAST, MEMBER.NAMEMIDDLE " +
                "ORDER BY NUMOFSTARSGIVEN DESC";

        List<Object[]> results = em.createNativeQuery(queryStr).getResultList();

        for (int i=0; i < results.size(); i++) {
            retStr += "{\"ID\": \"" + results.get(i)[0] + "\",\"Given To\": \"" + results.get(i)[1]+" "+ results.get(i)[3] + "\",\"Number of Stars\": " + (results.get(i)[4]).toString() + "},";
        }
        if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";

        return retStr;
    }

    /*------------------------*/

    @GET
    @Path("receivers/{corpID}/{userToken}/{giverID}")
    @Produces("application/json")
    public String getReceivers(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, @PathParam("giverID") String giverID) {
        String queryStr, retStr="{\"data\": [";

        if (!validUserAndLevel(corpID, userToken, null,"301"))            //Make sure the user is at least a dept admin
            return "{\"data\": []}";

        String deptRestrictionClause = "";
        if (getUserRoleID(userToken).equals("301"))
            deptRestrictionClause = "AND GIVINGMEMBER.DEPARTMENTID = '" + getUserDeptID(userToken) + "' ";

        queryStr = "SELECT MEMBER.ID, MEMBER.NAMEFIRST, MEMBER.NAMEMIDDLE, MEMBER.NAMELAST, COUNT(RECEIVINGMEMBERID) AS NUMOFSTARSRECEIVED " +
                "FROM STARGIVEN " +
                "INNER JOIN MEMBER ON RECEIVINGMEMBERID = MEMBER.ID " +
                "INNER JOIN MEMBER AS GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
                "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                "WHERE STARGIVEN.givingmemberid = '" + giverID + "' "  +
                "AND MEMBER.CORPID = '" + corpID + "' "  +
                deptRestrictionClause +
                "AND TUCOMPOSITE.CORPID = '" + corpID + "' "  +
                "AND TUCOMPOSITE.ACTIVE = true " +
                "GROUP BY MEMBER.ID, MEMBER.NAMEFIRST, MEMBER.NAMELAST, MEMBER.NAMEMIDDLE " +
                "ORDER BY NUMOFSTARSRECEIVED DESC";

        List<Object[]> results = em.createNativeQuery(queryStr).getResultList();


        for (int i=0; i < results.size(); i++) {
            retStr += "{\"ID\": \"" + results.get(i)[0] + "\",\"Receiver\": \"" + (String) results.get(i)[1]+" "+ results.get(i)[3] + "\",\"Number of Stars\": " + (results.get(i)[4]).toString() + "},";
        }
        if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";

        return retStr;
    }


    /*------------------------*/

/*    @GET
    @Path("reach/{corpID}/{memberID}")
    @Produces("application/json")
    public String getReach(@PathParam("corpID") String corpID, @PathParam("memberID") String memberID) {
        String queryStr, retStr="{\"data\": [";

        queryStr = "SELECT GIVINGMEMBERDEPT.DEPTNAME, COUNT(GIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTALS " +
                   "FROM STARGIVEN " +
                   "INNER JOIN MEMBER GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
                   "INNER JOIN DEPTCORP GIVINGMEMBERDEPT ON GIVINGMEMBER.DEPARTMENTID = GIVINGMEMBERDEPT.ID " +
                   "WHERE STARGIVEN.RECEIVINGMEMBERID = '" + memberID + "' "  +
                   "AND GIVINGMEMBER.CORPID = '" + corpID + "' "  +
                   "GROUP BY GIVINGMEMBERDEPT.DEPTNAME " +
                   "ORDER BY DEPTTOTALS DESC";

        List<Object[]> results = em.createNativeQuery(queryStr).getResultList();

        for (int i=0; i < results.size(); i++) {
            retStr += "{\"Dept\": \"" + results.get(i)[0] + "\",\"Total\": \"" + (String) (results.get(i)[1]).toString()+ "\"},";
        }
        if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";

        return retStr;
    }*/

    /*------------------------*/

    static double[] _msds (double[] x, int n) {
        if (x.length==0) return new double[]{0,0,0};
        if (x.length==1) return new double[]{x[0],0,x[0]};
        double summ = 0.0;
        double mull = 1.0;
        for (int j=0; j<x.length; j++){
            summ += x[j];
            mull *= x[j];
        }
        double theMean = summ / n;
        double summSquared = 0.0;
        for (int k=0; k<x.length; k++){
            double squaredDiff = Math.pow((theMean - x[k]),2.0);
            summSquared += squaredDiff;
        }
        double std = Math.sqrt(summSquared / (n - 1));

        double[] ans= {theMean, std, summ};
        return ans;
    }

    /*------------------------*/

    public List<Object[]> _receiverTotalsPerDept(String corpID, String UserID, String deptID) {  //totals that the user received from each department
        String queryStr =
			"SELECT COUNT(GIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTAL, GIVINGMEMBERDEPT.ID " +
			"FROM STARGIVEN " +
			"INNER JOIN MEMBER GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
			"INNER JOIN DEPTCORP GIVINGMEMBERDEPT ON GIVINGMEMBER.DEPARTMENTID = GIVINGMEMBERDEPT.ID " +
			"INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
			"INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
			"WHERE STARGIVEN.RECEIVINGMEMBERID = '" + UserID + "' "  +
			"AND GIVINGMEMBER.CORPID = '" + corpID + "' " +
//        if (deptID != null) queryStr += "AND GIVINGMEMBER.DEPARTMENTID = '" + deptID + "' ";
            "AND TUCOMPOSITE.CORPID = '" + corpID + "' "  +
			"AND TUCOMPOSITE.ACTIVE = true "  +
			"GROUP BY GIVINGMEMBERDEPT.DEPTNAME, GIVINGMEMBERDEPT.ID " +
            "ORDER BY DEPTTOTAL DESC";

        return em.createNativeQuery(queryStr).getResultList();
	}

    /*------------------------*/

    public List<Object[]> _giverTotalsPerDept(String corpID, String UserID, String deptID) { //totals that the user gave to each department
        String queryStr =
			"SELECT COUNT(RECEIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTAL, RECEIVINGMEMBERDEPT.ID " +
			"FROM STARGIVEN " +
			"INNER JOIN MEMBER RECEIVINGMEMBER ON STARGIVEN.RECEIVINGMEMBERID = RECEIVINGMEMBER.ID " +
			"INNER JOIN DEPTCORP RECEIVINGMEMBERDEPT ON RECEIVINGMEMBER.DEPARTMENTID = RECEIVINGMEMBERDEPT.ID " +
			"INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
			"INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
			"WHERE STARGIVEN.GIVINGMEMBERID = '" + UserID + "' "  +
			"AND RECEIVINGMEMBER.CORPID = '" + corpID + "' " +
//        if (deptID != null) queryStr += "AND RECEIVINGMEMBER.DEPARTMENTID = '" + deptID + "' ";
            "AND TUCOMPOSITE.CORPID = '" + corpID + "' "  +
		    "AND TUCOMPOSITE.ACTIVE = true "  +
			"GROUP BY RECEIVINGMEMBERDEPT.DEPTNAME, RECEIVINGMEMBERDEPT.ID " +
            "ORDER BY DEPTTOTAL DESC";

        return em.createNativeQuery(queryStr).getResultList();
	}

    /*------------------------*/

    public IdUserNameValue _getUserDecile(java.util.ArrayList<IdUserNameValue> userArray, String userID) {

        double decile_width = (double)userArray.size()/10.00d;
        int i = 0;

        for (i=0; i<userArray.size(); i++) {
            if (userID.equals(userArray.get(i).getId())){
                break;
            }
        }

        double decile = 10.0-((double)i/decile_width);
        if (i==userArray.size())    //if didn't find the userID in userArray. Bad ID.
            return (new IdUserNameValue("", "", "", "",0.0));
        else
            return (new IdUserNameValue(userArray.get(i).getId(), userArray.get(i).getNameFirst(), userArray.get(i).getNameMiddle(), userArray.get(i).getNameLast(),decile));
	}

    /*------------------------*/

    public IdUserNameValue _getPercentRank(java.util.ArrayList<IdUserNameValue> userArray, String userID) {
        int i = 0;

        for (i=0; i<userArray.size(); i++) {
            if (userID.equals(userArray.get(i).getId())){
                break;
            }
        }

        if (i==userArray.size())    //if didn't find the userID in userArray.
            return (new IdUserNameValue("", "", "", "",0.0));
        else{
            if (userArray.get(i).getValue() == 0.0)                                                         //if 0 then shouldn't place them anywhere
                return (new IdUserNameValue(userArray.get(i).getId(), userArray.get(i).getNameFirst(),
                            userArray.get(i).getNameMiddle(), userArray.get(i).getNameLast(),0.0));

            double rank = 100.0 - (double)i/(double)userArray.size()*100.0;
            return (new IdUserNameValue(userArray.get(i).getId(), userArray.get(i).getNameFirst(),
                        userArray.get(i).getNameMiddle(), userArray.get(i).getNameLast(),rank));
            }
	}

    /*------------------------*/

    public double _giverValuesRanking(String corpID, String userID, String valueID) {

/*        String queryStr =
                "SELECT QUALITY.ID, QUALITY.NAME, COUNT(STARGIVEN.TUTYPEID) " +
                "FROM STARGIVEN " +
                "INNER JOIN MEMBER RECEIVINGMEMBER ON STARGIVEN.RECEIVINGMEMBERID = RECEIVINGMEMBER.ID " +
                "INNER JOIN TUTYPE QUALITY ON QUALITY.ID = STARGIVEN.TUTYPEID " +
                "WHERE STARGIVEN.GIVINGMEMBERID = '" + userID + "' " +
                "AND RECEIVINGMEMBER.CORPID = '" + corpID + "' " +
                "GROUP BY QUALITY.ID, QUALITY.NAME, STARGIVEN.TUTYPEID";*/
				
		String byValueClause = "";
		if (!valueID.equals("-1")){
			byValueClause = "AND TUTYPE.ID = '" + valueID + "' ";
		}
        String queryStr =
                "SELECT TU.RATIO, COUNT(STARGIVEN.TUTYPEID), TUTYPE.NAME " +
                "FROM STARGIVEN " +
                "INNER JOIN MEMBER RECEIVINGMEMBER ON STARGIVEN.RECEIVINGMEMBERID = RECEIVINGMEMBER.ID " +
                "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                "INNER JOIN TUTYPE ON TUTYPE.ID = TU.TUTYPEID " +
                "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                "WHERE STARGIVEN.GIVINGMEMBERID = '" + userID + "' " +
                "AND RECEIVINGMEMBER.CORPID = '" + corpID + "' " +
                "AND TUCOMPOSITE.CORPID = '" + corpID + "' " +
                "AND TUCOMPOSITE.ACTIVE = true " +
				byValueClause +
                "GROUP BY TU.RATIO, STARGIVEN.TUTYPEID, TUTYPE.NAME";

        List<Object[]> valueTotals = em.createNativeQuery(queryStr).getResultList();

        double rank = 0.0;
        for (int i=0; i < valueTotals.size(); i++){
//            rank += Double.parseDouble(corpValues.get(valueTotals.get(i)[0]).toString()) * ((Long)valueTotals.get(i)[2]).doubleValue();
//            rank += ((Float)corpValues.get(valueTotals.get(i)[0])).doubleValue() * ((Long)valueTotals.get(i)[2]).doubleValue();

            if (valueTotals.get(i)[2].equals("General"))
                rank += getGeneralsAvg(corpID) * ((Long)valueTotals.get(i)[1]).doubleValue();
            else
                rank += ((Double)valueTotals.get(i)[0]) * ((Long)valueTotals.get(i)[1]).doubleValue();
/*            Object valObj = corpValues.get(valueTotals.get(i)[0]);                            //Ratio from tu table as Value in Hashmap looked up by tutypeid as Key
            if (valObj != null){                                                              //if a tutype is not used in the active valuesprofile it will be null
                valueRatio = ((Float)valObj).doubleValue();
                tuCount =  ((Long)valueTotals.get(i)[2]).doubleValue();                       //Count from the stargiven table of all tu's of tytype given to the receiver
                rank = valueRatio * tuCount;
            }*/
        }
        return rank;       //Multiply General with the average off other values.
    }

    /*------------------------*/

    public double _ReceiverValuesRanking(String corpID, String userID, String valueID) {

		String byValueClause = "";
		if (!valueID.equals("-1")){
			byValueClause = "AND TUTYPE.ID = '" + valueID + "' ";
		}
        String queryStr =
                "SELECT TU.RATIO, COUNT(STARGIVEN.TUTYPEID), TUTYPE.NAME " +
                "FROM STARGIVEN " +
                "INNER JOIN MEMBER GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
                "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                "INNER JOIN TUTYPE ON TUTYPE.ID = TU.TUTYPEID " +
                "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                "WHERE STARGIVEN.RECEIVINGMEMBERID = '" + userID + "' " +
                "AND GIVINGMEMBER.CORPID = '" + corpID + "' " +
                "AND TUCOMPOSITE.CORPID = '" + corpID + "' " +
                "AND TUCOMPOSITE.ACTIVE = true " +
				byValueClause +
                "GROUP BY TU.RATIO, STARGIVEN.TUTYPEID, TUTYPE.NAME ";

        List<Object[]> valueTotals = em.createNativeQuery(queryStr).getResultList();

        double rank = 0.0;
        for (int i=0; i < valueTotals.size(); i++){
//            rank += Double.parseDouble(corpValues.get(valueTotals.get(i)[0]).toString()) * ((Long)valueTotals.get(i)[2]).doubleValue();
            if (valueTotals.get(i)[2].equals("General"))
                rank += getGeneralsAvg(corpID) * ((Long)valueTotals.get(i)[1]).doubleValue();
            else
                rank += ((Double)valueTotals.get(i)[0]) * ((Long)valueTotals.get(i)[1]).doubleValue();
/*
            Object valObj = corpValues.get(valueTotals.get(i)[0]);                            //Ratio from tu table as Value in Hashmap looked up by tutypeid as Key
            if (valObj != null){                                                              //if a tutype is not used in the active valuesprofile it will be null
                valueRatio = ((Float)valObj).doubleValue();
                tuCount =  ((Long)valueTotals.get(i)[2]).doubleValue();                       //Count from the stargiven table of all tu's of tytype given to the receiver
                rank = valueRatio * tuCount;
            }*/
        }
        return rank;
    }

    /*------------------------*/

    public double getGeneralsAvg(String corpID) {
    double total = 0.0;

       	TUComposite activeProfile = em.createNamedQuery(TUComposite.FIND_ACTIVE_BY_CORPID, TUComposite.class).setParameter("corpID", corpID).getSingleResult();
        List<TU> profileTUs = em.createNamedQuery(TU.FIND_ALL_BY_TUCOMPOSITEID, TU.class).setParameter("tucompositeid", activeProfile.getId()).getResultList();

        if (profileTUs.size() <= 1)     //If no results or only "General" present
            return 0.0;

        for (TU tu: profileTUs){
            total += tu.getRatio();
        }
        return total/(profileTUs.size()-1);         //Don't add "General" to the total

/*        profileTUs.forEach((tu) -> {
            avg += ;
        });*/
    }
/*    *//*------------------------*//*

    @GET
    @Path("corpvaluesdept/{corpID}/{userToken}/{gr}/{deptID}") //corpID -- Giver or Receiver -- user, if present, for whom to send back % ranking only
    @Produces("application/json")
    public String getCorpValuesDept(@PathParam("corpID") String corpID, @PathParam("corpID") String userToken, @PathParam("gr") String gr, @PathParam("deptID") String deptID) {
        return _getCorpValues (corpID, gr, null, false, deptID);
    }*/
	
	
	
    /*------------------------*/
	public String _getCorpValuesForIndividual(String corpID, String gr, String userID){
		try{
			TUComposite activeTuComposite = em.createNamedQuery(TUComposite.FIND_ACTIVE_BY_CORPID, TUComposite.class).setParameter("corpID", corpID).getSingleResult();

			List<Object[]> tus = em.createNamedQuery(TU.FIND_ALL_BY_TUCOMPOSITEID_JOIN)
												.setParameter("tucompositeid", activeTuComposite.getId())
												.getResultList();	 
			
			java.util.ArrayList<IdUserNameValue> valueRanking = new java.util.ArrayList<>();
			
			for (int i=0; i<tus.size(); i++){
				String rank = _getCorpValues(corpID, gr, tus.get(i)[1].toString(), userID, true, null);
				valueRanking.add(new IdUserNameValue(tus.get(i)[1].toString(), tus.get(i)[3].toString(), "", "", Double.parseDouble(rank)));				
			}

	        String retStr="{\"ranking\": [";

            for (IdUserNameValue valueRank: valueRanking){
				retStr += "{\"ID\": \"" + valueRank.getId() +
					   "\", \"valueName\": \"" + valueRank.getNameFirst() +
					   "\", \"rank\": \"" + Math.round(valueRank.getValue()) + "\"},\n";
            }
            if (valueRanking.size() != 0) retStr = retStr.substring(0, retStr.length()-2); //remove the last extra comma and lf
            retStr += "]}";
			
			return retStr;
		
		} catch (NoResultException pe) {
			return "{\"ranking\": []}";
		} catch (PersistenceException pe) {
			return "FAIL: " + pe.getMessage();
		}
		
	}
	
    /*------------------------*/

    @GET
    @Path("corpvalues/{corpID}/{userToken}/{gr}/{valueID}/{userID: .*}") //corpID -- Giver or Receiver -- user, if present, for whom to send back % ranking only
    @Produces("application/json")
    public String getCorpValues(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, 
								@PathParam("gr") String gr, @PathParam("valueID") String valueID, @PathParam("userID") String userID) {
        boolean forIndividualUser = (userID != null && !userID.isEmpty());
		String userRoleID = getUserRoleID(userToken);

		if (userRoleID.substring(0,2).equalsIgnoreCase("Er")) //Record not found in db.  Possible hack.
    		return "{\"ranking\": []}";

		if (forIndividualUser){
			if (!validUserAndLevel(corpID, userToken, userID,"401"))            //Validate 'userID'.  Make sure the user is not hacking in another person's ID by sending in userID
				return "{\"ranking\": []}";
		    else
				return _getCorpValuesForIndividual(corpID, gr, userID);
        }
		else if (userRoleID.equals("301")){
            return _getCorpValues (corpID, gr, valueID, null, false, getUserDeptID(userToken));			//Restrict to own department only
		}
		else{
			if (!validUserAndLevel(corpID, userToken, null,"201"))
				return "{\"ranking\": []}";
		    else
                return _getCorpValues(corpID, gr, valueID, userID, false, null);
		}
    }

    /*------------------------*/

/*    @GET
    @Path("corpvalues/{corpID}/{gr}/{userID: .*}") //corpID -- Giver or Receiver -- user, if present, for whom to send back % ranking only
    @Produces("application/json")
    public String getCorpvalues(@PathParam("corpID") String corpID, @PathParam("gr") String gr, @PathParam("userID") String userID) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        boolean forIndividualUser = (userID != null && !userID.isEmpty());*/

    public String _getCorpValues(String corpID, String gr, String valueID, String userID, boolean forIndividualUser, String deptID){
		
        //Get the list of all the employees in the corporation
/*        List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();*/
/*        List<Member> members;
        if (deptID == null)     //Individual against the company or corp admin
            members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();
        else
            members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID_DEPTID, Member.class)                           //restricted to department.  dept admin
												.setParameter("corpID", corpID).setParameter("deptID", deptID)
												.getResultList();*/

        List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();

        java.util.ArrayList<String> deptUserIds = new java.util.ArrayList<>();
        if (deptID != null){
            List<Member> deptMembers = em.createNamedQuery(Member.FIND_ALL_BY_CORPID_DEPTID, Member.class)                           //restricted to department.  dept admin
												.setParameter("corpID", corpID).setParameter("deptID", deptID)
												.getResultList();
            for (Member member: deptMembers){
                deptUserIds.add(member.getId());
            }
        }

        String retStr="{\"ranking\": [";
        double rank = 0;

/*        //Get corporate Values Profile
    	TUComposite activeProfile = em.createNamedQuery(TUComposite.FIND_ACTIVE_BY_CORPID, TUComposite.class).setParameter("corpID", corpID).getSingleResult();
        List<TU> profileDef = em.createNamedQuery(TU.FIND_ALL_BY_TUCOMPOSITEID, TU.class).setParameter("tucompositeid", activeProfile.getId()).getResultList();

        HashMap<String, Float> values = new HashMap<String, Float>();
        for (int i=0; i<profileDef.size(); i++){
            values.put(profileDef.get(i).getTutypeId(),profileDef.get(i).getRatio());
        }*/

        java.util.ArrayList<IdUserNameValue> users = new java.util.ArrayList<>();

        for (int i=0; i < members.size(); i++) {
            if (gr.equals("giv")){
                rank = _giverValuesRanking(corpID, members.get(i).getId(), valueID);
                }
            else if (gr.equals("rcv")){
//                rank = _ReceiverValuesRanking(corpID, members.get(i).getId(), values);
                rank = _ReceiverValuesRanking(corpID, members.get(i).getId(), valueID);
            }

            users.add(new IdUserNameValue(members.get(i).getId(), members.get(i).getnameFirst(), members.get(i).getnameMiddle(),
                        members.get(i).getnameLast(),rank));
        }

        Collections.sort(users, Collections.reverseOrder());  //List has to be sorted for ranking algorithm to work

        if (forIndividualUser){
        IdUserNameValue user = _getPercentRank(users, userID);
				retStr = String.valueOf(Math.round(user.getValue()));
/*             retStr += "{\"ID\": \"" + user.getId() +
                       "\", \"nameFirst\": \"" + user.getNameFirst() +
                       "\", \"nameMiddle\": \"" + user.getNameMiddle() +
                       "\", \"nameLast\": \"" + user.getNameLast() +
                       "\", \"rank\": \"" + Math.round(user.getValue()) + "\"}";
            retStr += "]}"; */
        }
        else {
			double corpGivingAvg = getAvgGiving(corpID).doubleValue();
			DecimalFormat formatter = new DecimalFormat("#0.00");
//            for (int i=0; i<users.size(); i++){
            for (IdUserNameValue user: users){
                if (deptUserIds.contains(user.getId()) || deptID == null){         //if deptID != null then restricted by dept(dept admin).  if null then return all(corp admin).
//                    IdUserNameValue user = _getPercentRank(users, users.get(i).getId());   //Have to do it this way instead of in via db joins since measuring dept members against all corp employees
                    retStr += "{\"ID\": \"" + user.getId() +
                           "\", \"nameFirst\": \"" + user.getNameFirst() +
                           "\", \"nameMiddle\": \"" + user.getNameMiddle() +
                           "\", \"nameLast\": \"" + user.getNameLast() +
                           "\", \"rank\": \"" + formatter.format(user.getValue()/corpGivingAvg) + "\"},\n";
//                           "\", \"rank\": \"" + Math.round(users.get(i).getValue()) + "\"},\n";
//                if (i+1 != users.size())
//                    retStr += ", \n";
                }
            }
            if (users.size() != 0) retStr = retStr.substring(0, retStr.length()-2); //remove the last extra comma and lf
            retStr += "]}";
        }

        return retStr;
    }
    /*------------------------*/
/*    @GET
    @Path("influencedept/{corpID}/{gr}/{wl}/{deptID}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back % ranking only
//    @Path("influence/{corpID}/{gr}/{wl}/{userID:(/[^/]+?)?}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back decile only
    @Produces("application/json")
    public String getInfluenceDept(@PathParam("corpID") String corpID, @PathParam("gr") String gr, @PathParam("wl") String wl, @PathParam("deptID") String deptID) {
        return _getInfluence (corpID, gr, wl, null, false, false, deptID);
        }*/
    /*------------------------*/
    @GET
    @Path("influence/{corpID}/{userToken}/{gr}/{wl}/{userID: .*}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back % ranking only
//    @Path("influence/{corpID}/{gr}/{wl}/{userID:(/[^/]+?)?}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back decile only
    @Produces("application/json")
    public String getInfluence(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, @PathParam("gr") String gr, @PathParam("wl") String wl, @PathParam("userID") String userID) {
        boolean diag = (userID != null && !userID.isEmpty() && userID.equalsIgnoreCase("diagnostics"));
        boolean forIndividualUser = (userID != null && !userID.isEmpty());      //value was passed in.  Presumably userID.
//        return _getInfluence (corpID, gr, wl, userID, diag, forIndividualUser, null);

		String userRoleID = getUserRoleID(userToken);
		if (userRoleID.substring(0,2).equalsIgnoreCase("Er")) //Record not found in db.  Possible hack.
    		return "{\"data\": []}";
			
		if (diag){
			if (!validUserAndLevel(corpID, userToken, null, "101"))
				return "{\"data\": []}";
		    else
				return  _getInfluence (corpID, gr, wl, null, true, false, null);
		}

		if (forIndividualUser){
			if (!validUserAndLevel(corpID, userToken, userID, "401"))            //Make sure the user is not hacking in another person's ID by sending in supplied userID
				return "{\"data\": []}";
		    else
				return  _getInfluence (corpID, gr, wl, userID, false, true, null);
        }
		else if (userRoleID.equals("301")){
			return _getInfluence (corpID, gr, wl, null, false, false, getUserDeptID(userToken));
		}
		else{
			if (!validUserAndLevel(corpID, userToken, null,"201"))
				return "{\"data\": []}";
		    else
				return _getInfluence (corpID, gr, wl, null, false, false, null);
		}

        }

    /*------------------------*/
/*    @GET
    @Path("influence/{corpID}/{gr}/{wl}/{userID: .*}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back % ranking only
//    @Path("influence/{corpID}/{gr}/{wl}/{userID:(/[^/]+?)?}") //corpID -- Giver or Receiver -- Wider appeal or Local appeal -- user for whom to send back decile only
    @Produces("application/json")
    public String getInfluence(@PathParam("corpID") String corpID, @PathParam("gr") String gr, @PathParam("wl") String wl, @PathParam("userID") String userID) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        boolean diag = (userID != null && !userID.isEmpty() && userID.equalsIgnoreCase("diagnostics"));
        boolean forIndividualUser = (userID != null && !userID.isEmpty());      //value was passed in.  Presumably userID.*/

    public String _getInfluence (String corpID, String gr, String wl, String userID, boolean diag, boolean forIndividualUser, String deptID){
        DecimalFormat formatter = new DecimalFormat("#0.00");
        //Get the total number of registered departments at the corp
		List<DeptCorp> deptcorp = em.createNamedQuery(DeptCorp.FIND_ALL_BY_CORPID, DeptCorp.class).setParameter("corpID",corpID).getResultList();
		double numOfDepts = deptcorp.size();

        //Get the average receiving and giving in the corporation (Closed system so receiving should equal giving therefore only need average of one in the stored proc)
//        String queryStr = "select get_avg('" + corpID + "')";
//        double corpReceivingAvg = ((Number)em.createNativeQuery(queryStr).getSingleResult()).doubleValue();
        double corpReceivingAvg = getAvgGiving(corpID).doubleValue();

        //Get the list of all the employees in the corporation
        List<Member> members;
        if (deptID == null)     //Individual against the company or corp admin
            members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();
        else
            members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID_DEPTID, Member.class)                           //restricted to department.  dept admin
												.setParameter("corpID", corpID).setParameter("deptID", deptID)
												.getResultList();

        String retStr="{\"data\": [";
        java.util.ArrayList<IdUserNameValue> retArray = new java.util.ArrayList<>();
        int ownDepIdx = 0;
        double rank = 0;

        for (int i=0; i < members.size(); i++) {
            List<Object[]> results = new java.util.ArrayList<Object[]>();
            StringBuilder diagInfo = new StringBuilder();

            if (gr.equals("giv")){
                results = _giverTotalsPerDept(corpID, members.get(i).getId(), deptID);      //totals that the user gave to each department
                }
            else if (gr.equals("rcv")){
                results = _receiverTotalsPerDept(corpID, members.get(i).getId(), deptID);   //totals that the user received from each department
            }

            if (gr.equals("giv") || gr.equals("rcv")){
                ownDepIdx = -1;
                for (int j=0; j < results.size(); j++){                              //find user's own department
                    if (results.get(j)[1].equals(members.get(i).getDepartmentID())){
                        ownDepIdx = j;
                        break;
                    }
                }

                if (wl.equals("wide")){
                    rank = Analysis.COIScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                }else if (wl.equals("local")){
                    rank = Analysis.COTScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                } else return "FAIL";
            }
            else if (gr.equals("both")) {
                double recRank, givRank = 0.0;

                //Get ranking for giving
                results = _giverTotalsPerDept(corpID, members.get(i).getId(), deptID);      //totals that the user gave to each department
                ownDepIdx = -1;
                for (int j=0; j < results.size(); j++){                              //find user's own department
                    if (results.get(j)[1].equals(members.get(i).getDepartmentID())){
                        ownDepIdx = j;
                        break;
                    }
                }
                if (wl.equals("wide")){
                    givRank = Analysis.COIScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                }else if (wl.equals("local")){
                    givRank = Analysis.COTScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                } else return "FAIL";

                //Now get ranking for receiving
                results = _receiverTotalsPerDept(corpID, members.get(i).getId(), deptID);   //totals that the user received from each department
                ownDepIdx = -1;
                for (int j=0; j < results.size(); j++){                              //find user's own department
                    if (results.get(j)[1].equals(members.get(i).getDepartmentID())){
                        ownDepIdx = j;
                        break;
                    }
                }
                if (wl.equals("wide")){
                    recRank = Analysis.COIScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                }else if (wl.equals("local")){
                    recRank = Analysis.COTScore(results, numOfDepts, ownDepIdx, corpReceivingAvg, diag, diagInfo);
                } else return "FAIL";

                rank = givRank + recRank;
            }
            else {
                return "FAIL";
            }

            if (diag){
                retArray.add(new IdUserNameValue(members.get(i).getId(), members.get(i).getnameFirst(), members.get(i).getnameMiddle(),
                        members.get(i).getnameLast(),rank, diagInfo.toString()));
            }
            else{
                retArray.add(new IdUserNameValue(members.get(i).getId(), members.get(i).getnameFirst(), members.get(i).getnameMiddle(),
                        members.get(i).getnameLast(),rank));
            }
        }

//            Collections.sort(retArray);
        Collections.sort(retArray, Collections.reverseOrder()) ;

        if (diag){
//            return retArray.get(0).getDiagnostics();
/*              for (int i=0; i < retArray.size(); i++){
                  retStr += retArray.get(i).getDiagnostics() + "," + retArray.get(i).getNameFirst() + retArray.get(i).getNameLast() + "," + retArray.get(i).getValue() + i + "\n";
              }
              return retStr;*/

            for (int i=0; i < retArray.size(); i++){
                String[] diagnostics = retArray.get(i).getDiagnostics().split(",");
                retStr += "{\"ID\": \"" + retArray.get(i).getId() +
                           "\", \"nameFirst\": \"" + retArray.get(i).getNameFirst() +
                           "\", \"nameMiddle\": \"" + retArray.get(i).getNameMiddle() +
                           "\", \"nameLast\": \"" + retArray.get(i).getNameLast() +
                           "\", \"Rank\": \"" + formatter.format(retArray.get(i).getValue()) +
                           "\", \"Mean\": \"" + diagnostics[1] +
                           "\", \"Std Dev\": \"" + diagnostics[2] +
                           "\", \"Sum\": \"" + diagnostics[3] +
                           "\", \"GRDeptRatio\": \"" + diagnostics[4] +
                           "\", \"GRDepts\": \"" + diagnostics[5] +
                           "\", \"TotalDepts\": \"" + diagnostics[6];

                for (int k=7; k<diagnostics.length-1; k++){
                    retStr += "\", \"Dept"+k+"\": \"" + diagnostics[k];
                }
                retStr += "\", \"OwnDeptIdx\": \"" + diagnostics[diagnostics.length-1];
                retStr += "\"},\n";
            }
            if (retArray.size() != 0) retStr = retStr.substring(0, retStr.length()-2); //remove the last extra comma and lf
            retStr += "]}";
            return retStr;
        }
        else if (forIndividualUser){
                IdUserNameValue user = _getPercentRank(retArray, userID);
                retStr += "{\"ID\": \"" + user.getId() +
                           "\", \"nameFirst\": \"" + user.getNameFirst() +
                           "\", \"nameMiddle\": \"" + user.getNameMiddle() +
                           "\", \"nameLast\": \"" + user.getNameLast() +
                           "\", \"Index\": \"" + Math.round(user.getValue()) + "\"}";
                retStr += "]}";
                return retStr;
        }
        else{

            for (int i=0; i < retArray.size(); i++){
//                IdUserNameValue user = _getPercentRank(retArray, userID);

/*            for (int i=0; i < retArray.size(); i++){
                IdUserNameValue user = _getPercentRank(retArray, retArray.get(i).getId());      //Could get percentage rank but for now raw index numbers
                retStr += "{\"ID\": \"" + user.getId() +
                           "\", \"nameFirst\": \"" + user.getNameFirst() +
                           "\", \"nameMiddle\": \"" + user.getNameMiddle() +
                           "\", \"nameLast\": \"" + user.getNameLast() +
                           "\", \"Index\": \"" + Math.round(user.getValue()) + "\"},";*/

                retStr += "{\"ID\": \"" + retArray.get(i).getId() +
                           "\", \"nameFirst\": \"" + retArray.get(i).getNameFirst() +
                           "\", \"nameMiddle\": \"" + retArray.get(i).getNameMiddle() +
                           "\", \"nameLast\": \"" + retArray.get(i).getNameLast() +
                           "\", \"Index\": \"" + formatter.format(retArray.get(i).getValue()) + "\"},";

            }
        }
        if (retArray.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";
        return retStr;
    }

    /*------------------------*/

    /*    @GET
    @Path("influencercv/{corpID}")
    @Produces("application/json")
    public String getInfluenceRcv(@PathParam("corpID") String corpID) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();

        String queryStr, retStr="{\"data\": [";

        for (int i=0; i < members.size(); i++) {
            List<Object> results = _giverTotalsPerDept(corpID, members.get(i).getId());
            double[] userTotalsPerDept = new double[results.size()];

            for (int j=0; j < results.size(); j++) {
                userTotalsPerDept[j] = Double.parseDouble(results.get(j).toString());;
            }
            double[] ans = _msds (userTotalsPerDept, results.size()); //mean=ans[0], stddev=ans[1], sum=ans[2]
            double rank = ans[2]*(1-ans[1]*2/100);
            if (Double.isNaN(rank)) rank = 0;
                retStr += "{\"ID\": \"" + members.get(i).getId() +
                            "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                            "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                            "\", \"nameLast\": \"" + members.get(i).getnameLast() +
                            "\", \"Index\": \"" + formatter.format(rank) + "\"},";
        }
        if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";
        return retStr;
    }*/

    /*------------------------*/

/*    @GET
    @Path("influencegiv/{corpID}")
    @Produces("application/json")
    public String getInfluenceGiv(@PathParam("corpID") String corpID) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();

        String queryStr, retStr="{\"data\": [";

        for (int i=0; i < members.size(); i++) {
            List<Object> results = _receiverTotalsPerDept(corpID, members.get(i).getId());
            double[] userTotalsPerDept = new double[results.size()];

            for (int j=0; j < results.size(); j++) {
                userTotalsPerDept[j] = Double.parseDouble(results.get(j).toString());;
            }
            double[] ans = _msds (userTotalsPerDept, results.size()); //mean=ans[0], stddev=ans[1], sum=ans[2]
            double rank = ans[2]*(1-ans[1]*2/100);
            if (Double.isNaN(rank)) rank = 0;
                retStr += "{\"ID\": \"" + members.get(i).getId() +
                            "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                            "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                            "\", \"nameLast\": \"" + members.get(i).getnameLast() +
                            "\", \"Index\": \"" + formatter.format(rank) + "\"},";
        }
        if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";
        return retStr;
    }*/

    /*------------------------*/

/*    @GET
    @Path("reachrcv/{corpID}")
    @Produces("application/json")
    public String getReachRcv(@PathParam("corpID") String corpID) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        List<Member> members = em.createNamedQuery(Member.FIND_ALL_BY_CORPID, Member.class)
												.setParameter("corpID", corpID)
												.getResultList();

        String queryStr, retStr="{\"data\": [";

        for (int i=0; i < members.size(); i++) {

            queryStr = "SELECT COUNT(GIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTAL " +
                        "FROM STARGIVEN " +
                        "INNER JOIN MEMBER GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
                        "INNER JOIN DEPTCORP GIVINGMEMBERDEPT ON GIVINGMEMBER.DEPARTMENTID = GIVINGMEMBERDEPT.ID " +
                        "WHERE STARGIVEN.RECEIVINGMEMBERID = '" + members.get(i).getId() + "' "  +
                        "AND GIVINGMEMBER.CORPID = '" + corpID + "' "  +
                        "GROUP BY GIVINGMEMBERDEPT.DEPTNAME " +
                        "ORDER BY DEPTTOTAL DESC";

            List<Object> results = em.createNativeQuery(queryStr).getResultList();

            double summ = 0;
            for (int j=0; j < results.size(); j++) {
                 summ += Double.parseDouble(results.get(j).toString());
            }
            retStr += "{\"ID\": \"" + members.get(i).getId() +
                        "\", \"nameFirst\": \"" + members.get(i).getnameFirst() +
                        "\", \"nameMiddle\": \"" + members.get(i).getnameMiddle() +
                        "\", \"nameLast\": \"" + members.get(i).getnameLast() +
                        "\", \"Index\": \"" + formatter.format(summ) + "\"},";
//                        "\", \"Index\": \"" + String.format("%.2f", summ) + "\"},";
        }
        if (members.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}";
        return retStr;
    }*/

    /*------------------------*/

    public List<Object[]> _deptGiverTotals(String corpID) {
        String queryStr =
			"SELECT GIVINGMEMBERDEPT.DEPTNAME, COUNT(GIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTALS, GIVINGMEMBERDEPT.id " +
            "FROM STARGIVEN " +
            "INNER JOIN MEMBER GIVINGMEMBER ON STARGIVEN.GIVINGMEMBERID = GIVINGMEMBER.ID " +
            "INNER JOIN DEPTCORP GIVINGMEMBERDEPT ON GIVINGMEMBER.DEPARTMENTID = GIVINGMEMBERDEPT.ID " +
            "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
            "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
            "WHERE GIVINGMEMBER.CORPID = '" + corpID + "' " +
            "AND TUCOMPOSITE.CORPID = '" + corpID + "' " +
            "AND TUCOMPOSITE.ACTIVE = true " +
            "GROUP BY GIVINGMEMBERDEPT.DEPTNAME, GIVINGMEMBERDEPT.id " +
            "ORDER BY DEPTTOTALS DESC";

        return em.createNativeQuery(queryStr).getResultList();
	}

    /*------------------------*/

    public List<Object[]> _deptRecTotals(String corpID) {
        String queryStr =
			"SELECT RECEIVINGMEMBERDEPT.DEPTNAME, COUNT(RECEIVINGMEMBERDEPT.DEPTNAME) AS DEPTTOTALS, RECEIVINGMEMBERDEPT.id " +
            "FROM STARGIVEN " +
            "INNER JOIN MEMBER RECEIVINGMEMBER ON STARGIVEN.RECEIVINGMEMBERID = RECEIVINGMEMBER.ID " +
            "INNER JOIN DEPTCORP RECEIVINGMEMBERDEPT ON RECEIVINGMEMBER.DEPARTMENTID = RECEIVINGMEMBERDEPT.ID " +
            "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
            "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
            "WHERE RECEIVINGMEMBER.CORPID = '" + corpID + "' " +
            "AND TUCOMPOSITE.CORPID = '" + corpID + "' " +
            "AND TUCOMPOSITE.ACTIVE = true " +
            "GROUP BY RECEIVINGMEMBERDEPT.DEPTNAME, RECEIVINGMEMBERDEPT.id " +
            "ORDER BY DEPTTOTALS DESC";

        return em.createNativeQuery(queryStr).getResultList();
	}

    /*------------------------*/

    @GET
    @Path("depttotals/{corpID}/{userToken}")
    @Produces("application/json")
    public String getDeptTotals(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {
        String retStr="{\"data\": {\"giver\": [";

    	if (!validUserAndLevel(corpID, userToken, null,"201"))            //Make sure the user is at least a dept admin
	    	return "{\"data\": {\"giver\": []}}";

        List<Object[]> results = _deptGiverTotals(corpID);

        for (int i=0; i < results.size(); i++) {
            retStr += "{\"Dept\": \"" + results.get(i)[0] + "\",\"Total\": \"" + (String) (results.get(i)[1]).toString()+ "\"},";
        }
        if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "], \"receiver\": [";

//        results = em.createNativeQuery(queryStr).getResultList();
        results = _deptRecTotals(corpID);

        for (int i=0; i < results.size(); i++) {
            retStr += "{\"Dept\": \"" + results.get(i)[0] + "\",\"Total\": \"" + (String) (results.get(i)[1]).toString()+ "\"},";
        }
        if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
        retStr += "]}}";

        return retStr;
    }

    /*----------------------------*/

  @GET
  @Path("getstars/{corpID}/{userToken}/{givingOrReceiving}")
  @Produces("application/json")
//  public Response getStars() {
  public String getStars(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken, @PathParam("givingOrReceiving") String givingOrReceiving) {
    String queryStr,deptID="",retStr="{\"data\": [";

	if (!validUserAndLevel(corpID, userToken, null,"301"))            //Make sure the user is at least a dept admin
		return retStr + "]}";

	String userRoleID = getUserRoleID(userToken);
	boolean deptRestricted = false;
	if (userRoleID.equals("301"))
    {
	    deptRestricted = true;
	    deptID = getUserDeptID(userToken);
    }

    try{
        if (givingOrReceiving.equalsIgnoreCase("R")) {
          queryStr = "SELECT MEMBER.NAMEFIRST, MEMBER.NAMELAST, COUNT(STARGIVEN.RECEIVINGMEMBERID) AS NUMOFSTARSREC, MEMBER.ID " +
                     "FROM STARGIVEN " +
                     "INNER JOIN MEMBER ON STARGIVEN.RECEIVINGMEMBERID = MEMBER.ID " +
                     "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                     "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                     "WHERE MEMBER.CORPID = '"+corpID+"' ";

          if (deptRestricted) queryStr += "AND MEMBER.DEPARTMENTID = '" + deptID + "' ";

          queryStr += "AND TUCOMPOSITE.CORPID = '" + corpID + "' " +
                      "AND TUCOMPOSITE.ACTIVE = true " +
                      "GROUP BY MEMBER.NAMEFIRST, MEMBER.NAMELAST, MEMBER.ID " +
                      "ORDER BY NUMOFSTARSREC DESC";

          List<Object[]> results = em.createNativeQuery(queryStr).getResultList();

          for (int i=0; i < results.size(); i++) {
              retStr += "{\"ID\": \"" + results.get(i)[3] + "\", \"Receiver\": \"" + (String) results.get(i)[0]+" "+ results.get(i)[1] + "\", \"Number of Stars\": " + (results.get(i)[2]).toString() +"},";
          }
          if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
          retStr += "]}";
        }
        else if (givingOrReceiving.equalsIgnoreCase("G")) {
          queryStr = "SELECT MEMBER.NAMEFIRST, MEMBER.NAMELAST, COUNT(STARGIVEN.GIVINGMEMBERID) AS NUMOFSTARSGIVEN, MEMBER.ID " +"" +
                     "FROM STARGIVEN " +
                     "INNER JOIN MEMBER ON STARGIVEN.GIVINGMEMBERID = MEMBER.ID " +
                     "INNER JOIN TU ON STARGIVEN.TUTYPEID = TU.TUTYPEID " +
                     "INNER JOIN TUCOMPOSITE ON TUCOMPOSITE.ID = TU.TUCOMPOSITEID " +
                     "WHERE MEMBER.CORPID = '"+corpID+"' ";

          if (deptRestricted) queryStr += "AND MEMBER.DEPARTMENTID = '" + deptID + "' ";

          queryStr += "AND TUCOMPOSITE.CORPID = '"+corpID+"' " +
                      "AND TUCOMPOSITE.ACTIVE = true " +
                      "GROUP BY MEMBER.NAMEFIRST, MEMBER.NAMELAST, MEMBER.ID " +
                      "ORDER BY NUMOFSTARSGIVEN DESC";

          List<Object[]> results = em.createNativeQuery(queryStr).getResultList();

          for (int i=0; i < results.size(); i++) {
              retStr += "{\"ID\": \"" + results.get(i)[3] + "\", \"Giver\": \"" + (String) results.get(i)[0]+" "+ results.get(i)[1] + "\", \"Number of Stars\": " + (results.get(i)[2]).toString() +"},";
          }
          if (results.size() != 0) retStr = retStr.substring(0, retStr.length()-1); //remove the last extra comma
          retStr += "]}";
        }
        else {
          return "<html lang=\"en\"><body>Error:  Use /R or /G </body></html>";
        }
        return retStr;

    } catch (javax.persistence.NoResultException pe) {
//            return "NO RESULT" + "loggingMember.getDepartmentID()"+loggingMember.getDepartmentID()+"deptcorp.getDeptName()"+deptcorp.getDeptName();
        return "NO RESULT";
    } catch  (javax.persistence.PersistenceException pe){
        return "ERROR: " + pe.getMessage();
    } catch (Exception e){
        return "ERROR: " + e.getMessage();
    }
  }

/*-------------------------*/

    @GET
    @Path("test")
    @Produces("application/json")
    public String test() {
        String retStr = "";

        double avg = getAvgGiving("1").doubleValue();
        retStr = "getAvgGiving = " + Double.toString(avg);
        avg = getGeneralsAvg("1");
        retStr += "\n getGeneralAvg = " + Double.toString(avg);

		byte[] encodedBytes = Base64.getEncoder().encode("Test".getBytes());
		retStr += "\n encodedBytes(\"Test\") = " + (new String(encodedBytes));

/*
    if (validUserAndLevel("1","L0tra3RsRkVhVlJMM0hMV0QyY1JIZko0", "401"))
		retStr = "True";
	else
		retStr = "False";
*/
//retStr = validUserAndLevel("1","L0tra3RsRkVhVlJMM0hMV0QyY1JIZko0", "401")
		
    return retStr;
	}
/*
IdUserNameValue test = new IdUserNameValue();

test.setId("1");


        IdUserNameValue[] Sorted = new IdUserNameValue[3];

            Sorted[0] = new IdUserNameValue();
            Sorted[0].setId("1");
            Sorted[0].setNameFirst("Henry");
            Sorted[0].setNameMiddle("David");
            Sorted[0].setNameLast("Thoreau");
            Sorted[0].setValue(1.1);

            Sorted[1] = new IdUserNameValue();
            Sorted[1].setId("2");
            Sorted[1].setNameFirst("Henry2");
            Sorted[1].setNameMiddle("David2");
            Sorted[1].setNameLast("Thoreau2");
            Sorted[1].setValue(1.2);

            Sorted[2] = new IdUserNameValue();
            Sorted[2].setId("3");
            Sorted[2].setNameFirst("Henry3");
            Sorted[2].setNameMiddle("David3");
            Sorted[2].setNameLast("Thoreau3");
            Sorted[2].setValue(0.2);

            Arrays.sort(Sorted);

            return Sorted[0].getNameFirst()+
                    Sorted[0].getValue()+

                    Sorted[1].getNameFirst()+
                    Sorted[1].getValue()+

                    Sorted[2].getNameFirst()+
                    Sorted[2].getValue();
*/

        /*
        //Add Departments and Corporations
        Corp corp = new Corp("Sierra Club", "http://www.sierraclub.org/");
        em.persist(corp);
        return ("corp="+corp.getName()+corp.getId()+corp.getWebsite());
*/





/*        return "{\"data\":  [" +
        "        {\"a\": \"1The Lord of the Rings\", \"b\": \"J. R. R. Tolkien\"}," +
        "        {\"a\": \"2Le Petit Prince (The Little Prince)\", \"b\": \"Antoine de Saint-Exupéry\"}," +
        "        {\"a\": \"3Harry Potter and the Philosopher's Stone\", \"b\": \"J. K. Rowling\"}," +
        "        {\"a\": \"4And Then There Were None\", \"b\": \"Agatha Christie\"}," +
        "        {\"a\": \"5Dream of the Red Chamber\", \"b\": \"Cao Xueqin\"}," +
        "        {\"a\": \"6The Hobbit\", \"b\": \"J. R. R. Tolkien\"}," +
        "        {\"a\": \"7She: A History of Adventure\", \"b\": \"H. Rider Haggard\"}" +
        "      ]}";
    }*/


  /*--------------------------*/

  @GET
  @Path("getqualities/{corpID}/{userToken}")
  @Produces("application/json")
  public String getQualities(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {
/*
{
	"qualities": [{
		"id": "221",
		"name": "Nike1",
		"description": "blah blah"
	}, {
		"id": "221",
		"name": "Nike1",
		"description": "blah blah"
	}]
}
 */
    if (!validUserAndLevel(corpID, userToken, null, "301"))
		return "{\"qualities\": [{}]}";

	List<TUType> tut = em.createNamedQuery(TUType.FIND_ALL, TUType.class).getResultList();

    String retStr = "{\"qualities\": [{";

    for (int i=0; i<tut.size(); i++){
        retStr += "\"id\": \"" + tut.get(i).getId() + "\",";
        retStr += "\"name\": \"" + tut.get(i).getName() + "\",";
        retStr += "\"description\": \"" + tut.get(i).getDescription() + "\"";
        if (i+1 != tut.size() )
            retStr += "}, {";
    }

    retStr += "}]}";
    return retStr;
  }


  /*--------------------------*/

  @GET
  @Path("getactivequalities/{corpID}/{userToken}")
  @Produces("application/json")
  public String getActiveQualities(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {
/*
{
	"qualities": [{
		"id": "218",
		"name": "Positivity",
		"description": "Optimistic in attitude"
	}, {
		"id": "217",
		"name": "Judgement",
		"description": "Wise decision maker"
	}, {
		"id": "216",
		"name": "Intelligence",
		"description": "Exhibits smarts and self-awareness"
	}, {
		"id": "220",
		"name": "Openness",
		"description": "Naturally collaborative"
	}]
}
 */

    if (!validUserAndLevel(corpID, userToken, null, "401"))
		return "{\"qualities\": [{}]}";

	String retStr = "{\"qualities\": [{";
	
	try{
		TUComposite activeTuComposite = em.createNamedQuery(TUComposite.FIND_ACTIVE_BY_CORPID, TUComposite.class).setParameter("corpID", corpID).getSingleResult();

        List<Object[]> tus = em.createNamedQuery(TU.FIND_ALL_BY_TUCOMPOSITEID_JOIN)
                                            .setParameter("tucompositeid", activeTuComposite.getId())
                                            .getResultList();	 

		for (int i=0; i<tus.size(); i++){
			retStr += "\"id\": \"" +  tus.get(i)[1] + "\",";
			retStr += "\"name\": \"" +  tus.get(i)[3] + "\",";
			retStr += "\"description\": \"" +  tus.get(i)[4] + "\"";
			if (i+1 != tus.size() )
				retStr += "}, {";
		}
    } catch (NoResultException pe) {
		return "{\"qualities\": [{}]}";
	} catch (PersistenceException pe) {
		return "FAIL: " + pe.getMessage();
	}
    retStr += "}]}";
    return retStr;
  }
  
  
  /*--------------------------*/

  @GET
  @Path("getqualitiescomposite/{corpID}/{userToken}")
  @Produces("application/json")
  public String getQualitiesComposite(@PathParam("corpID") String corpID, @PathParam("userToken") String userToken) {
/*
{
   "qualities":{
      "activecompid":"221",
      "activecompname":"Nike1",
      "composites":[
         {
            "id":"221",
            "name":"Nike1",
            "values":[  
               {  
                  "tuid":"223",
                  "tutypeid":"216",
                  "tutypename":"Intelligence",
                  "ratio":25.0
               },
               {  
                  "tuid":"226",
                  "tutypeid":"220",
                  "tutypename":"Openness",
                  "ratio":25.0
               },
               {  
                  "tuid":"224",
                  "tutypeid":"218",
                  "tutypename":"Positivity",
                  "ratio":25.0
               },
               {  
                  "tuid":"225",
                  "tutypeid":"217",
                  "tutypename":"Good Judgement",
                  "ratio":25.0
               }
            ]
         },
         {  
            "id":"222",
            "name":"Nike2",
            "values":[  
               {  

               }
            ]
         }
      ]
   }
}
*/
/*    String retStr = "{\"qualities\": {" +
                        "\"activecompid\": \"201\", " +
                        "\"composites\": [{" +
                                "\"id\": \"224\"," +
                                "\"values\": [{" +
                                        "\"tuid\": \"228\", \"tutypeid\": \"222\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"225\", \"tutypeid\": \"218\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"226\", \"tutypeid\": \"220\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"227\", \"tutypeid\": \"219\", \"ratio\": 25" +
                                "}] " +
                            "}, {" +
                                "\"id\": \"224\"," +
                                "\"values\": [{" +
                                        "\"tuid\": \"228\", \"tutypeid\": \"222\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"225\", \"tutypeid\": \"218\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"226\", \"tutypeid\": \"220\", \"ratio\": 25" +
                                "}, {" +
                                        "\"id\": \"227\", \"tutypeid\": \"219\", \"ratio\": 25" +

                                "}]" +
                            "}]" +
                        "}" +
                    "}";*/


    if (!validUserAndLevel(corpID, userToken, null, "201"))
		return "{\"qualities\": {}}";

    String retStr = "{\"qualities\": {" +
                        "\"activecompid\": ";
	try{
	TUComposite activeTuComposite = em.createNamedQuery(TUComposite.FIND_ACTIVE_BY_CORPID, TUComposite.class).setParameter("corpID", corpID)
                                                                                                             .getSingleResult();
    retStr += "\"" + activeTuComposite.getId() + "\", ";

    retStr +=  "\"activecompname\": " + "\"" + activeTuComposite.getName() + "\", ";

    retStr +=           "\"composites\": [{";

	List<TUComposite> tuComposites = em.createNamedQuery(TUComposite.FIND_ALL_BY_CORPID, TUComposite.class).setParameter("corpID", corpID)
                                                                                                           .getResultList();
    for (int i=0; i<tuComposites.size(); i++){
        retStr +=               "\"id\": \"" + tuComposites.get(i).getId() + "\",";
        retStr +=               "\"name\": \"" + tuComposites.get(i).getName() + "\",";

        List<Object[]> tus = em.createNamedQuery(TU.FIND_ALL_BY_TUCOMPOSITEID_JOIN)
                                            .setParameter("tucompositeid", tuComposites.get(i).getId())
                                            .getResultList();
/*		Query query = em.createQuery("SELECT b.id, b.tutypeId, b.ratio, p.name FROM TU b INNER JOIN TUType p ON b.tutypeId = p.id where b.tuCompositeId = :tucompositeid");
		query.setParameter("tucompositeid", tuComposites.get(i).getId());
		List<Object[]> tus = query.getResultList();*/

        retStr +=                   "\"values\": [{";

        for (int j=0; j<tus.size(); j++){

            retStr +=                   "\"tuid\": \"" + tus.get(j)[0] + "\", \"tutypeid\": \"" + tus.get(j)[1] + "\", \"tutypename\": \"" + tus.get(j)[3] + "\", \"ratio\": " + (tus.get(j)[2]).toString();

            if (j+1 != tus.size() ){
                retStr +=       "}, {";}
        }

        retStr +=               "}] ";
        if (i+1 != tuComposites.size() ){
        retStr +=          "}, {";}
    }
    retStr +=               "}]";
    retStr +=           "}";
    retStr +=        "}";
    } catch (NoResultException pe) {
		return "{\"qualities\": [{}]}";
	} catch (PersistenceException pe) {
		return "FAIL: " + pe.getMessage();
	}
    return retStr;
  }

  /*--------------------------*/

  public List<DeptCorp> _deptByCorp(String corpID) {
	TypedQuery<DeptCorp> dpquery = em.createNamedQuery(DeptCorp.FIND_ALL_BY_CORPID, DeptCorp.class).setParameter("corpID",corpID);
	return dpquery.getResultList();
  }

	/*--------------------------*/

  public String _getCorpIDFromUID(String userID){

    String[] _messageChunks = userID.split("@");

	try{
		TypedQuery<CorpAllowedURLs> query = em.createNamedQuery(CorpAllowedURLs.FIND_CORPID_BY_URL, CorpAllowedURLs.class)
                .setParameter("allowedURL",_messageChunks[_messageChunks.length-1].toLowerCase());  //only compare the part after the @ sign
		CorpAllowedURLs corpAllowedURLs = query.getSingleResult();
		return corpAllowedURLs.getCorpID();

	} catch (NoResultException pe) {
            return "NO RESULT";
    } catch  (PersistenceException pe){
            return "ERROR: " + pe.getMessage();
    } catch (Exception e){
            return "ERROR: " + e.getMessage();
    }
  }

	/*--------------------------*/

  @POST
  @Path("getdeptbycorp")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String getDeptByCorp(String message) {

    String[] msgChunk = message.split(",");                                     //0=corpID,1=userToken
    String  corpID      = msgChunk[0],
            userTokenB64   = msgChunk[1];
	try{
        if (!userTokenB64.equals("NOTOKENYET")){                                        //If registering
            if (!validUserAndLevel(corpID, userTokenB64, null, "401"))
  	    	    return "ERROR: Not Authorized";
        }
        else{
            corpID = _getCorpIDFromUID(msgChunk[0]);
            if (corpID.equalsIgnoreCase("NO RESULT"))
                return "{\"departments\": []}";
        }

		List<DeptCorp> deptcorp = _deptByCorp(corpID);

		String retStr="{\"departments\": [";

		for (int i=0; i < deptcorp.size(); i++) {
		  retStr += "{\"ID\": \"" + deptcorp.get(i).getId()+
					"\", \"department\": \"" + deptcorp.get(i).getDeptName() +
					"\"},";
		}
        if (deptcorp.size() != 0) retStr = retStr.substring(0, retStr.length()-1);  //remove the last extra comma
        retStr += "]}";
		return retStr;
	} catch (NoResultException pe) {
            return "{\"departments\": []}";
    } catch  (PersistenceException pe){
            return "ERROR: " + pe.getMessage();
    } catch (Exception e){
            return "ERROR: " + e.getMessage();
    }
  }

    /*--------------------------*/


  @POST
  @Path("corpregistered")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String GetCorpID(String message) {

    String result = _getCorpIDFromUID(message);
    if (result.equalsIgnoreCase("NO RESULT"))
        return result;
    else
        return "REGISTERED";
  }

    /*--------------------------*/

  public boolean validUserAndLevel(String CorpID, String userTokenBase64, String userID, String minLevel) {  //userID='id' in member table
	try{
        byte[] decodedPWD = Base64.getDecoder().decode(userTokenBase64.getBytes());
        String userToken = new String(decodedPWD); log("validUserAndLevel:userToken: " + userToken,1);
		
		Member member = em.createNamedQuery(Member.FIND_BY_PWD, Member.class).setParameter("pwd",userToken).getSingleResult();
		Integer minLevelIntValue = Integer.valueOf(minLevel);
		Integer userRoleIntValue = Integer.valueOf(member.getRoleID());

		if (userID != null)
		    if (!(member.getId().equals(userID))){
				log("validUserAndLevel:userID != member.Id");
                return false;                           //ID did not match record pulled up using userToken.  Possible hack.
			}
		if (CorpID.equals(member.getCorpID())){
			if (userRoleIntValue <= minLevelIntValue)
				return true;
			else{
				log("validUserAndLevel:minLevel fail");
				return false;
			}
		}
		else
		{
			log("validUserAndLevel:corpID != member.corpID");
			return false;
		}
 	} catch (NoResultException pe) {
			log("validUserAndLevel:NoResultException: " + pe.getMessage());
            return false;
    } catch  (PersistenceException pe){
			log("validUserAndLevel:PersistenceException: " + pe.getMessage());
            return false;
    } catch (Exception e){
			log("validUserAndLevel:Exception: " + e.getMessage());
            return false;
    }
  }

    /*--------------------------*/
/*
public boolean validDeptAndLevel(String CorpID, String userTokenBase64, String deptID, String minLevel) {
	try{
        byte[] decodedPWD = Base64.getDecoder().decode(userTokenBase64.getBytes());
        String userToken = new String(decodedPWD);

		Member member = em.createNamedQuery(Member.FIND_BY_PWD, Member.class).setParameter("pwd",userToken).getSingleResult();
		Integer minLevelIntValue = Integer.valueOf(minLevel);
		Integer userRoleIntValue = Integer.valueOf(member.getRoleID());

        if (member.getRoleID().equals("301"))
            if (!member.getDepartmentID().equals(deptID))       //Dept admin hacking to see another dept
                return false;

		if (CorpID.equals(member.getCorpID())){
			if (userRoleIntValue <= minLevelIntValue)
				return true;
			else
				return false;
		}
		else
		{
			return false;
		}
 	} catch (NoResultException pe) {
            return false;
    } catch  (PersistenceException pe){
            return false;
    } catch (Exception e){
            return false;
    }
  }*/


    /*--------------------------*/


  public String getRoleValue (String roleName){

	switch(roleName) {
		case "USER" : return "401";
		case "DEPT-ADMIN" : return "301";
		case "CORP-ADMIN" : return "201";
		case "SUPER" : return "101";
		default : return "001";
	}
  }
	
    /*--------------------------*/
  
  public String getRoleName (String roleValue){

	switch(roleValue) {
		case "401" : return "USER";
		case "301" : return "DEPT-ADMIN";
		case "201" : return "CORP-ADMIN";
		case "101" : return "SUPER";
		default : return "ERROR";
	}
  }
	
    /*--------------------------*/

  public String getUserRoleID(String userTokenBase64) {
	try{
        byte[] decodedPWD = Base64.getDecoder().decode(userTokenBase64.getBytes());
        String userToken = new String(decodedPWD);

		Member member = em.createNamedQuery(Member.FIND_BY_PWD, Member.class).setParameter("pwd",userToken).getSingleResult();
		return member.getRoleID();

 	} catch (NoResultException pe) {
            return "Error:No Record Found. " + pe.getMessage();
    } catch  (PersistenceException pe){
            return "Error:" + pe.getMessage();
    } catch (Exception e){
            return "Error: " + e.getMessage();
    }
  }

    /*--------------------------*/

  public String getUserDeptID(String userTokenBase64) {
	try{
        byte[] decodedPWD = Base64.getDecoder().decode(userTokenBase64.getBytes());
        String userToken = new String(decodedPWD);

		Member member = em.createNamedQuery(Member.FIND_BY_PWD, Member.class).setParameter("pwd",userToken).getSingleResult();
		return member.getDepartmentID();

 	} catch (NoResultException pe) {
            return "Error:No Record Found. " + pe.getMessage();
    } catch  (PersistenceException pe){
            return "Error:" + pe.getMessage();
    } catch (Exception e){
            return "Error: " + e.getMessage();
    }
  }

    /*--------------------------*/

}

/*

package com.mkyong.common;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("username","password");
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from@no-spam.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("to@no-spam.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}*/


/*


  @POST
  @Path("sendmail")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String sendMail(String message) {

      String to = "sonoojaiswal1988@gmail.com";//change accordingly
      String from = "sonoojaiswal1987@gmail.com";//change accordingly
      String host = "localhost";//or IP address
      String[] _msgChunks = message.split(",");
     //Get the session object
      Properties properties = System.getProperties();
      properties.setProperty("mail.smtp.host", host);
      Session session = Session.getDefaultInstance(properties);

     //compose the message
      try{
         MimeMessage mailMessage = new MimeMessage(session);
         mailMessage.setFrom(new InternetAddress(from));
         mailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
         mailMessage.setSubject("Ping");
         mailMessage.setText("Hello, this is example of sending email  ");

         // Send message
         Transport.send(mailMessage);
         return "message sent successfully....";

      }catch (MessagingException mex) {mex.printStackTrace(); return "Error";}
  }

 */
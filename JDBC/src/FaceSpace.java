import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author Gabriel Wells
 * Main facespace jdbc driver, term project cs 1555
 * Gabriel Wells, Mike McAlpin, Jordan Britton
 */
public class FaceSpace {
	
	//global, static variables
	private static Connection dbconn;
	private static Statement statement; //used to create an instance of the connection
    private static PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private static ResultSet resultSet; //used to hold the result of queries
    private static String query;  //this will hold the query we are using
    /*private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of queries
    private String query;  //this will hold the query we are using*/
    
    //Mike - injection checked
    public static boolean createUser() {
    	Scanner inScan=new Scanner(System.in);
    	System.out.print("\nEnter the first name: ");
    	String fname = inScan.next();
    	while(!checkInput(fname)) {
        	System.out.print("\nEnter the first name: ");
        	fname = inScan.next();
    	}
    	System.out.print("\nEnter the last name: ");
    	String lname = inScan.next();
    	while(!checkInput(lname)) {
        	System.out.print("\nEnter the last name: ");
        	lname = inScan.next();
    	}
    	System.out.print("\nEnter the email address: ");
    	String email = inScan.next();
    	while(!checkInput(email)) {
        	System.out.print("\nEnter the email address: ");
        	email = inScan.next();
    	}
    	int dobYear = -1, dobMonth = -1, dobDay = -1; //initialize these to negative so the while loops run the first time at least
    	while(dobYear < 0) {
    		System.out.print("\nEnter the year of birth (4 digits): ");
    		dobYear = inScan.nextInt();
    	}
    	while(dobMonth < 1 || dobMonth > 12) {
        	System.out.print("\nEnter the month of birth (numeric): ");
        	dobMonth = inScan.nextInt();
    	}
    	while(dobDay < 1 || dobDay > 31) {
        	System.out.print("\nEnter the day of birth (numeric): ");
        	dobDay = inScan.nextInt();
    	}
 	   	try {
 	   		//System.out.println("Getting number of rows...");
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = dbconn.createStatement();
			//first count the current number of users
			query = "SELECT MAX(userId) as maxId "
					+ "FROM Profiles";
			//query += " FROM Profiles";
			resultSet = statement.executeQuery(query);
		    int max=0;
		    if(resultSet.next()) {
				max = resultSet.getInt("maxId");
		    }
		    //System.out.println("Number of rows: "+max);
		    
		    //now do the actual insert
		    query = "INSERT INTO Profiles VALUES (?,?,?,?,?,?,?,NULL)";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,max+1);
		    prepStatement.setString(2,fname);
		    prepStatement.setString(3,lname);
		    prepStatement.setString(4,email);
		    prepStatement.setInt(5,dobDay);
		    prepStatement.setInt(6,dobMonth);
		    prepStatement.setInt(7,dobYear);
			
		    System.out.println("Adding user...");
		    int result = prepStatement.executeUpdate();
		    //System.out.println("After statement execution");
		    if (result == 1) 
		    	System.out.println("Update is successful!");
		    	//System.out.println("Update is successful, result=" + result);
		    else 
		    	System.out.println("No rows were updated");
		    
		    dbconn.commit();
		    resultSet.close();
	    }
		catch(SQLException Ex)  
		{
			//System.out.println("Machine Error: " +Ex.toString());
			printSQLException(Ex);
			return false;
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
				return false;
			}
		}    	
    	return true;
    }
    
    //Jordan
    public static boolean initiateFriendship() {
    	
    	return true;
    }
    
    //Jordan
    public static boolean establishFriendship() {
    	
    	return true;
    }
    
    //Mike - injection tested
    public static boolean displayFriends() {
    	Scanner inScan=new Scanner(System.in);
    	int userId = -1; //initialize this to negative so the while loops run the first time at least
    	while(userId < 1) {
    		System.out.print("\nEnter the userId: ");
    		userId = inScan.nextInt();
    	}
    	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
			//first get all the friendships with the senderId==input and receiverId is the friend
			String subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			String subQueryFriendships = "SELECT senderId,receiverId FROM Friendships WHERE senderId=? AND approved=1";
		    query = "SELECT P.userId AS UserId,P.fname AS First,P.lname AS Last "
		    		+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryFriendships+") F "
		    		+"ON P.userId=F.receiverId";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			System.out.println("\nFriends:");
			int friendId;
			String name;
			while(resultSet.next()) {
				name = resultSet.getString("First")+" "+resultSet.getString("Last");
				friendId = resultSet.getInt("UserId");
				System.out.println("Id: "+friendId+"\tName: "+name);
		    }
			
			//now get all the friendships with the receiverId==input and senderId is the friend
			subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			subQueryFriendships = "SELECT senderId,receiverId FROM Friendships WHERE receiverId=? AND approved=1";
		    query = "SELECT P.userId AS UserId,P.fname AS First,P.lname AS Last "
		    		+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryFriendships+") F "
		    		+"ON P.userId=F.senderId";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			while(resultSet.next()) {
				name = resultSet.getString("First")+" "+resultSet.getString("Last");
				friendId = resultSet.getInt("UserId");
				System.out.println("Id: "+friendId+"\tName: "+name);
		    }
			
			System.out.println("\nEnd of Friends!");
	    }
		catch(SQLException Ex)  
		{
			printSQLException(Ex);
			return false;
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
				return false;
			}
		}	
    	return true;
    }
    
    private static boolean checkInput(String in){
    	//common sql injection attack characters that have no business being in input
    	if (in.contains("--") || in.contains(";") || in.contains("=") || in.contains(")")){
    		System.out.println("Error. Your input is suspicious. Please try again");
    		return false;
    	}
    	
    	//other sql keywords that may be used
    	if (in.contains("DROP") || in.contains("SELECT") || in.contains("UPDATE")){
    		System.out.println("Error. No SQL keywords allowed in input");
    		return false;
    	}
    	return true;
    }
    
    //Gabe
    public static boolean createGroup() {
    	Scanner in = new Scanner(System.in);
    	try {
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); //because counting number groups
			Statement stmt = dbconn.createStatement();
			int numGroups = 0;
			String query = "SELECT COUNT(*) AS count FROM Groups";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				numGroups = rs.getInt("count");
			}
			
            rs = stmt.executeQuery("SELECT * FROM Groups");
			
			while(rs.next()){
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getInt(4));
			}
			
			System.out.println("What is the name of your group?");
			String groupName = in.nextLine();
			while(!checkInput(groupName)){
				groupName = in.nextLine();
			}
			System.out.println("What is it's description?");
			String groupDesc = in.nextLine();
			while(!checkInput(groupDesc)){
				groupDesc = in.nextLine();
			}
			System.out.println("What is the membership limit?");
			int membership = in.nextInt();
			
			query = "INSERT INTO Groups VALUES (?,?,?,?)";
			PreparedStatement pstmt = dbconn.prepareStatement(query);
			pstmt.setInt(1,(numGroups+1));
			pstmt.setString(2,groupName);
			pstmt.setString(3,groupDesc);
			pstmt.setInt(4,membership);
			
			pstmt.executeUpdate();
			
			
			System.out.println("Group created!");
	    	try {
    			if (stmt !=null) stmt.close();
    		} catch (SQLException e) {
    			System.out.println("Cannot close Statement. Machine error: "+e.toString());
    		}
			
		} catch (SQLException e) {
			printSQLException(e);
		}
    	return true;
    }
    
    //Gabe
    //need to check membership limit of group
    public static boolean addToGroup() {
    	try {
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);//to prevent two users being added at same time, potentially overflowing group membership
	    	Scanner in = new Scanner(System.in);
	    	int userId = -1;
	    	int count = -1;
	    	int groupId = -1;
	    	
	    	Statement stmt = dbconn.createStatement();
	    	
	    	String countquery = "SELECT COUNT(*) as cnt FROM Profiles WHERE fname = ? AND lname = ?";
	    	String query = "SELECT * FROM Profiles WHERE fname = ? AND lname = ?";
	    	String groupQuery = "SELECT groupId FROM Groups WHERE name = ?";
	    	
	    	System.out.println("What is the first name?");
	    	String fname = in.nextLine();
	    	while(!checkInput(fname)){
	    		fname = in.nextLine();
	    	}
	    	System.out.println("What is the last name?");
	    	String lname = in.nextLine();
	    	while(!checkInput(lname)){
	    		lname = in.nextLine();
	    	}
	    	System.out.println("What group?");
	    	String group = in.nextLine();
	    	while(!checkInput(group)){
	    		group = in.nextLine();
	    	}
    	
			PreparedStatement pstmt = dbconn.prepareStatement(query);
			pstmt.setString(1, fname);
			pstmt.setString(2, lname);
			
			PreparedStatement pstmt2 = dbconn.prepareStatement(countquery);
			pstmt2.setString(1, fname);
			pstmt2.setString(2, lname);
			
			PreparedStatement pstmt3 = dbconn.prepareStatement(groupQuery);
			pstmt3.setString(1, group);
			
			ResultSet rs = pstmt.executeQuery();
			ResultSet rs2 = pstmt2.executeQuery();
			ResultSet rs3 = pstmt3.executeQuery();
			
			while(rs3.next()){
				groupId = rs3.getInt(1);
			}
			
			while(rs2.next()){
				count = rs2.getInt("cnt");
			}
			
			while(rs.next()){
				if(count == -1){
					System.out.println("No results recieved");
				}
				if(count == 0){
					System.out.println("No facespace user by that name");
				}
				if(count > 1){
					System.out.println("More than 1 user has that name!");
				}
				userId = rs.getInt(1);
			}
			
			if(groupId == -1 || userId == -1){
				System.out.println("Error. Either user or group id was not found");
				return false;
			}
			
			String insertQuery = "INSERT INTO Members VALUES(?, ?)";
			
			PreparedStatement finalStatement = dbconn.prepareStatement(insertQuery);
			finalStatement.setInt(1, groupId);
			finalStatement.setInt(2, userId);
			
			finalStatement.executeUpdate();
			
			System.out.println("Group membership updated.");
	    	try {
			if (stmt !=null) stmt.close();
		} catch (SQLException e) {
			System.out.println("Cannot close Statement. Machine error: "+e.toString());
		}
			
		} catch (SQLException e) {
			printSQLException(e);
		}
    	
    	return true;
    }
    
    //Jordan
    public static boolean sendMessageToUser() {
    	
    	return true;
    }
    
    //Jordan
    public static boolean sendMessageToGroup() {
    	
    	return true;
    }
    
    //Mike
    public static boolean displayMessages() {
    	Scanner inScan=new Scanner(System.in);
    	int userId = -1; //initialize these to negative so the while loops run the first time at least
    	while(userId < 1) {
    		System.out.print("\nEnter the userId: ");
    		userId = inScan.nextInt();
    	}
    	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
			//first get all the friendships with the senderId==input and receiverId is the friend
			String subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			String subQueryMessages1 = "SELECT messageId,senderId FROM Messages";
			String subQuerySenders = "SELECT M.messageId as messageId,P.fname AS fname,P.lname AS lname "
					+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryMessages1+") M "
					+"ON P.userId=M.senderId";
			String subQueryMessages2 = "SELECT M.messageId as messageId,M.senderId AS senderId,M.subject AS subject,M.messageText AS messageText,M.dateSent as dateSent,S.fname AS fname,S.lname AS lname "
					+"FROM Messages M JOIN ("+subQuerySenders+") S "
					+"ON M.messageId=S.messageId";
			String subQueryRecipients = "SELECT messageId,userId FROM Recipients WHERE userId=?";
		    query = "SELECT M.messageId AS mId,M.senderId AS sId,M.subject AS subject,M.messageText AS text,M.dateSent AS dateSent,M.fname AS fname,M.lname AS lname "
		    		+"FROM ("+subQueryMessages2+") M JOIN ("+subQueryRecipients+") R "
		    		+"ON M.messageId=R.messageId";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			System.out.println("\nMessages:");
			int messageId,senderId,groupId;
			String subject,text,senderName;
			Timestamp date;
			while(resultSet.next()) {
				messageId = resultSet.getInt("mId");
				senderId = resultSet.getInt("sId");
				senderName = resultSet.getString("fname")+" "+resultSet.getString("lname");
				subject = resultSet.getString("subject");
				text = resultSet.getString("text");
				date = resultSet.getTimestamp("dateSent");
				System.out.println("\nMessageId: "+messageId+"\tFrom: "+senderName+"\tDate/Time: "+date);
				System.out.println("Subject: "+subject);
				System.out.println(text);
		    }
			
			System.out.println("\nEnd of Messages!");
	    }
		catch(SQLException Ex)  
		{
			printSQLException(Ex);
			return false;
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
				return false;
			}
		}
    	return true;
    }
    
    //Mike
    public static boolean displayNewMessages() {
    	Scanner inScan=new Scanner(System.in);
    	int userId = -1; //initialize these to negative so the while loops run the first time at least
    	while(userId < 1) {
    		System.out.print("\nEnter the userId: ");
    		userId = inScan.nextInt();
    	}
    	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
			//first get the last login time
			String userQuery = "SELECT userId,lastLogin FROM Profiles WHERE userId=?";
		    prepStatement = dbconn.prepareStatement(userQuery);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			Timestamp lastLogin=null;
			if(resultSet.next()) {
				lastLogin = resultSet.getTimestamp("lastLogin");
				if(lastLogin==null) {
					System.out.println("User has never logged in!");
				}
				else
					System.out.println("Last login time: "+lastLogin);
			}
			else {
				System.out.println("User not found.");
			}
			//then get all the friendships with the senderId==input and receiverId is the friend
			String subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			String subQueryMessages1 = "SELECT messageId,senderId FROM Messages";
			String subQuerySenders = "SELECT M.messageId as messageId,P.fname AS fname,P.lname AS lname "
					+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryMessages1+") M "
					+"ON P.userId=M.senderId";
			String subQueryMessages2 = "SELECT M.messageId as messageId,M.senderId AS senderId,M.subject AS subject,M.messageText AS messageText,M.dateSent as dateSent,S.fname AS fname,S.lname AS lname "
					+"FROM Messages M JOIN ("+subQuerySenders+") S "
					+"ON M.messageId=S.messageId";
			String subQueryRecipients = "SELECT messageId,userId FROM Recipients WHERE userId=?";
		    query = "SELECT M.messageId AS mId,M.senderId AS sId,M.subject AS subject,M.messageText AS text,M.dateSent AS dateSent,M.fname AS fname,M.lname AS lname "
		    		+"FROM ("+subQueryMessages2+") M JOIN ("+subQueryRecipients+") R "
		    		+"ON M.messageId=R.messageId";
		    if(lastLogin != null)
		    	query += " WHERE dateSent>?";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
		    if(lastLogin != null)
		    	prepStatement.setTimestamp(2,lastLogin);
			resultSet = prepStatement.executeQuery();
			System.out.println("\nMessages:");
			int messageId,senderId,groupId;
			String subject,text,senderName;
			Timestamp date;
			while(resultSet.next()) {
				messageId = resultSet.getInt("mId");
				senderId = resultSet.getInt("sId");
				senderName = resultSet.getString("fname")+" "+resultSet.getString("lname");
				subject = resultSet.getString("subject");
				text = resultSet.getString("text");
				date = resultSet.getTimestamp("dateSent");
				System.out.println("\nMessageId: "+messageId+"\tFrom: "+senderName+"\tDate/Time: "+date);
				System.out.println("Subject: "+subject);
				System.out.println(text);
		    }
			
			System.out.println("\nEnd of Messages!");
	    }
		catch(SQLException Ex)  
		{
			printSQLException(Ex);
			return false;
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
				return false;
			}
		}
    	return true;
    }
    
    //Gabe
    public static boolean searchForUser() {
    	try {
    		//serializable because nothing else should occur when dropUser is taking place
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    		Statement stmt = dbconn.createStatement();
    		
	    	Scanner in = new Scanner(System.in);
	    	System.out.print("Please enter your search string: ");
	    	String searchString = in.nextLine();
	    	while(!checkInput(searchString)){
	    		searchString = in.nextLine();
	    	}
	    	
	    	String searchQuery = "SELECT * FROM Profiles WHERE UPPER(fname) LIKE ? OR UPPER(lname) LIKE ?";
	    	
	    	String[] partials = searchString.split(" ");
	    	ResultSet rs;
	    	PreparedStatement pstmt;
	    	int count = 0;
	    	
	    	for (int i = 0; i < partials.length; i++){
	    		System.out.println("All users matching " + partials[i] + ":");
	    		pstmt = dbconn.prepareStatement(searchQuery);
	    		pstmt.setString(1, "%" + partials[i].toUpperCase() + "%");
	    		pstmt.setString(2, "%" + partials[i].toUpperCase() + "%");
	    		rs = pstmt.executeQuery();
	    		while(rs.next()){
	    			count++;
	    			System.out.println("(" + rs.getInt(1) + ") " + rs.getString(2) + " " + rs.getString(3));
	    		}
	    		if(count == 0)
	    			System.out.println("No results found!");
	    		else 
	    			count = 0;
	    		System.out.println("");
	    	}
	    	
	    	try {
	    		if (stmt !=null) stmt.close();
	    	} catch (SQLException e) {
	    		System.out.println("Cannot close Statement. Machine error: "+e.toString());
	    	}
			
		} catch (SQLException e) {
			printSQLException(e);
		}
    	return true;
    }
    
    //Mike
    public static boolean threeDegrees() {
    	Scanner inScan=new Scanner(System.in);
    	int userId_A = -1,userId_B = -1; //initialize these to negative so the while loops run the first time at least
    	while(userId_A < 1) {
    		System.out.print("\nEnter the userId for A: ");
    		userId_A = inScan.nextInt();
    	}
    	while(userId_B < 1) {
    		System.out.print("\nEnter the userId for B: ");
    		userId_B = inScan.nextInt();
    	}
		String pathStart="Start: User "+userId_A,currPath1,currPath2,currPath3;
		ArrayList<Integer> friends_1,friends_2,friends_3;
		
		friends_1 = getFriends(userId_A);
		//first level
		for(int i=0; i<friends_1.size(); i++) {
			currPath1 = " --> User "+friends_1.get(i);
			if(friends_1.get(i) == userId_B) {
				System.out.println("Path found! (1 hop)");
				System.out.println(pathStart+currPath1);
				try {
					if (statement!=null) statement.close();
				} catch (SQLException e) {
					System.out.println("Cannot close Statement. Machine error: "+e.toString());
					return false;
				}
				return true;
			}
			else {
				friends_2 = getFriends(friends_1.get(i));
    			//second level
				for(int j=0; j<friends_2.size(); j++) {
					currPath2 = " --> User "+friends_2.get(j);
	    			if(friends_2.get(j) == userId_B) {
	    				System.out.println("Path found! (2 hops)");
	    				System.out.println(pathStart+currPath1+currPath2);
	    				try {
	    					if (statement!=null) statement.close();
	    				} catch (SQLException e) {
	    					System.out.println("Cannot close Statement. Machine error: "+e.toString());
	    					return false;
	    				}
	    				return true;
	    			}
	    			else {
	    				friends_3 = getFriends(friends_2.get(j));
	        			//third level
	    				for(int k=0; k<friends_3.size(); k++) {
	    					currPath3 = " --> User "+friends_3.get(k);
	    	    			if(friends_3.get(k) == userId_B) {
	    	    				System.out.println("Path found! (3 hops)");
	    	    				System.out.println(pathStart+currPath1+currPath2+currPath3);
	    	    				try {
	    	    					if (statement!=null) statement.close();
	    	    				} catch (SQLException e) {
	    	    					System.out.println("Cannot close Statement. Machine error: "+e.toString());
	    	    					return false;
	    	    				}
	    	    				return true;
	    	    			}
	        			}
	    			}
    			}
			}
		}
		System.out.printf("No path found...");
    	return true;
    }
    
    private static ArrayList<Integer> getFriends(int userId) {
    	ArrayList<Integer> friends = new ArrayList<Integer>();
    	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
			//first get all the friendships with the senderId==input and receiverId is the friend
			String subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			String subQueryFriendships = "SELECT senderId,receiverId FROM Friendships WHERE senderId=? AND approved=1";
		    query = "SELECT P.userId AS UserId,P.fname AS First,P.lname AS Last "
		    		+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryFriendships+") F "
		    		+"ON P.userId=F.receiverId";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			int friendId;
			while(resultSet.next()) {
				friendId = resultSet.getInt("UserId");
				friends.add(friendId);
		    }
			
			//now get all the friendships with the receiverId==input and senderId is the friend
			subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			subQueryFriendships = "SELECT senderId,receiverId FROM Friendships WHERE receiverId=? AND approved=1";
		    query = "SELECT P.userId AS UserId,P.fname AS First,P.lname AS Last "
		    		+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryFriendships+") F "
		    		+"ON P.userId=F.senderId";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,userId);
			resultSet = prepStatement.executeQuery();
			while(resultSet.next()) {
				friendId = resultSet.getInt("UserId");
				friends.add(friendId);
		    }
	    }
		catch(SQLException Ex)  
		{
			printSQLException(Ex);
			return null;
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
				return null;
			}
		}	
    	return friends;
    }
    
    //Jordan
    public static boolean topMessagers() {
    	
    	return true;
    }
    
    //Gabe
    public static boolean dropUser() {
    	
    	try {
    		//serializable because nothing else should occur when dropUser is taking place
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		
	    	Scanner in = new Scanner(System.in);
	    	
	    	Statement stmt = dbconn.createStatement();
	    	
	    	System.out.println("What is userId?");
	    	int id = in.nextInt();
	    	
	    	String delQuery = "DELETE FROM Profiles WHERE userId = ?";
	    	
	    	PreparedStatement pstmt = dbconn.prepareStatement(delQuery);
	    	pstmt.setInt(1, id);
	    	pstmt.executeUpdate();
	    	
	    	String messagesQuery = "SELECT messageId FROM Messages WHERE senderId IS NULL";
	    	String recipientsQuery = "SELECT * FROM Recipients WHERE messageId = ?";
	    	String deleteQuery = "DELETE FROM Messages WHERE messageId = ?";
	    	
	    	ResultSet nullMsgs = stmt.executeQuery(messagesQuery);
	    	int msgId = -1;
	    	int counter = 0;
	    	
	    	//loops through null messages. For each message, run another query
	    	//that determines whether that message still has existing recipients
	    	//if it has no recipients, it is safe to drop that message from db
	    	while(nullMsgs.next()){
	    		msgId = nullMsgs.getInt(1);
	    		System.out.println(msgId);
	    		PreparedStatement pstmt2 = dbconn.prepareStatement(recipientsQuery);
	    		pstmt2.setInt(1,  msgId);
	    		ResultSet rs = pstmt2.executeQuery();
	    		while(rs.next()){
	    			counter++;
	    		}
	    		if(counter == 0){
	    			System.out.println("That message has no recipients, it can be deleted entirely");
	    			PreparedStatement pstmt3 = dbconn.prepareStatement(deleteQuery);
	    			pstmt3.setInt(1, msgId);
	    			pstmt3.executeUpdate();
	    			System.out.println("Message deleted successfully");
	    		}
	    		else{
	    			counter = 0;
	    		}
	    	}
    	
			System.out.println("Profile deleted successfully");
	    	
	    	try {
	    		if (stmt !=null) stmt.close();
	    	} catch (SQLException e) {
	    		System.out.println("Cannot close Statement. Machine error: "+e.toString());
	    	}
			
		} catch (SQLException e) {
			printSQLException(e);
		}
    	
    	return true;
    }    

    public static int showMenu(){
    	Scanner in = new Scanner(System.in);
    	System.out.println("\n--FaceSpace Menu--");
    	System.out.println("1. Create a new user");
    	System.out.println("2. Initiate a friendship");
    	System.out.println("3. Establish a pending friendship");
    	System.out.println("4. Display your friends");
    	System.out.println("5. Create a new group");
    	System.out.println("6. Add a user to a group");
    	System.out.println("7. Send a message to a user");
    	System.out.println("8. Send a message to a group");
    	System.out.println("9. Display all messages");
    	System.out.println("10. Display new messages");
    	System.out.println("11. Search for user");
    	System.out.println("12. Three degrees search");
    	System.out.println("13. Display top messagers");
    	System.out.println("14. Drop user");
    	System.out.println("0. Quit");
    	
    	int choice = in.nextInt();
    	return choice;
    }
    
    /*
    private static int lookupUser() {
    	Scanner inScan=new Scanner(System.in);
    	int userId = -1;
    	System.out.println("Lookup user by email or name? (E/N)");
    	String response = inScan.next();
    	if(response.toUpperCase().equals("E")) {
    		System.out.print("Enter email: ");
    		String email = inScan.next();
    		userId = lookupByEmail(email);
    	}
    	else {
    		System.out.print("Enter first name: ");
    		String fname = inScan.next();
    		System.out.print("Enter last name: ");
    		String lname = inScan.next();
    		userId = lookupByName(fname,lname);
    	}    	
    	return userId;
    }
    
    private static int lookupByEmail(String email) {
    	Scanner inScan = new Scanner(System.in);
    	int userId = -1;
    	try {
    		ResultSet resultSet2;
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
		    query = "SELECT userId, fname, lname, email "
		    		+"FROM Profiles "
		    		+"WHERE email=?";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setString(1,email);
			resultSet = prepStatement.executeQuery();
			resultSet2 = prepStatement.executeQuery();
			int count = 0;
			while(resultSet.next()) {
				count++;
		    }
			if(count == 0) {
				System.out.println(count);
				return -1;
			}
			else if(count == 1) {
				resultSet2.next();
				return resultSet2.getInt("userId");
			}
			else {
				int index = 0;
				ArrayList<Integer> ids = new ArrayList<Integer>();
				while(resultSet2.next()) {
					index++;
					ids.add(resultSet2.getInt("userId"));
					System.out.println(index+".\t"+resultSet2.getString("fname")+" "+resultSet2.getString("lname")+"\t"+resultSet2.getString("email"));
				}
				System.out.println("\nChoose a user (enter the first number on the line)(enter -1 if none): ");
				return inScan.nextInt();
			}
	    }
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
    	return -1;
    }
    
    private static int lookupByName(String fname, String lname) {
    	Scanner inScan = new Scanner(System.in);
    	int userId = -1;
    	try {
    		ResultSet resultSet2;
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
		    query = "SELECT userId,fname,lname,email "
		    		+"FROM Profiles "
		    		+"WHERE fname=? AND lname=?";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setString(1,fname);
		    prepStatement.setString(2,lname);
			resultSet = prepStatement.executeQuery();
			resultSet2 = prepStatement.executeQuery();
			int count = 0;
			while(resultSet.next()) {
				count++;
		    }
			if(count == 0) {
				return -1;
			}
			else if(count == 1) {
				resultSet2.next();
				return resultSet2.getInt("userId");
			}
			else {
				int index = 0;
				ArrayList<Integer> ids = new ArrayList<Integer>();
				while(resultSet2.next()) {
					index++;
					ids.add(resultSet2.getInt("userId"));
					System.out.println(index+".\t"+resultSet2.getString("fname")+" "+resultSet2.getString("lname")+"\t"+resultSet2.getString("email"));
				}
				System.out.println("\nChoose a user (enter the first number on the line)(enter -1 if none): ");
				return inScan.nextInt();
			}
			
	    }
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
		}
		finally{
			try {
				if (statement!=null) statement.close();
			} catch (SQLException e) {
				System.out.println("Cannot close Statement. Machine error: "+e.toString());
			}
		}
    	
    	return -1;
    }
    */
    
    //these bottom two methods were taken from oracle java docs and are used to easily print out any sql exceptions
    //https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (ignoreSQLException(
                    ((SQLException)e).
                    getSQLState()) == false) {

                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " +
                        ((SQLException)e).getSQLState());

                    System.err.println("Error Code: " +
                        ((SQLException)e).getErrorCode());

                    System.err.println("Message: " + e.getMessage());

                    Throwable t = ex.getCause();
                    while(t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }
    public static boolean ignoreSQLException(String sqlState) {

        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }

        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;

        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55"))
            return true;

        return false;
    }
    
	public static void main(String args[]) throws SQLException{		
		String username = "gmw24";
		String password = "3858457";
		
		System.out.println("Welcome to facespace");

		try{
		    // Register the oracle driver.  
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    
		    //This is the location of the database.  This is the database in oracle
		    //provided to the class
		    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"; 
		    
		    //create a connection to DB on class3.cs.pitt.edu
		    dbconn = DriverManager.getConnection(url, username, password); 
		    //TranDemo1 demo = new TranDemo1(Integer.parseInt(args[0]));
		    
		    System.out.println("Connection to database successful.");
		    
		    //System.out.println("User Id: "+lookupUser());
		    
		    int choice = showMenu();
		    
		    while(choice < 0 && choice > 14){
		    	System.out.println("Sorry, that choice is not recognized.");
		    	choice = showMenu();
		    }
		    
		    while(choice > 0 && choice < 14){
		    
			    switch(choice){
				    case 1:
				    	createUser();
				    	break;
				    case 4:
				    	displayFriends();
				    	break;
			    	case 5:
			    		createGroup();
			    		break;
			    	case 6:
			    		addToGroup();
			    		break;
				    case 9:
				    	displayMessages();
				    	break;
				    case 10:
				    	displayNewMessages();
				    	break;
			    	case 11:
			    		searchForUser();
			    		break;
				    case 12:
				    	threeDegrees();
				    	break;
			    	case 14:
			    		dropUser();
			    		break;
				    default:
				    	break;
			    }
			    choice = showMenu();
			    System.out.println("");
		    }
		}
		catch(Exception Ex)  {
		    System.out.println("Error connecting to database.  Machine Error: " +
				       Ex.toString());
		    Ex.printStackTrace();
		}
		finally
		{
		 	try {
    			if (statement !=null) statement.close();
    		} catch (SQLException e) {
    			System.out.println("Cannot close Statement. Machine error: "+e.toString());
    		}
			dbconn.close();
		}
	}
}


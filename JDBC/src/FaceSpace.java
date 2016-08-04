import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.Date;

/**
 * Main facespace jdbc driver, term project cs 1555
 * Gabriel Wells, Mike McAlpin, Jordan Britton
 * 
 * PLEASE NOTE: To ensure database is in a consistent state at the start of testing,
 * please run the following three sql files in this order:
 * 1. facespace_db.sql (sets up schemas)
 * 2. facespace_integrity.sql (sets up triggers and constraints)
 * 3. facespace_data.sql (populates database)
 * 
 * Please do this in the instance of sqlplus which corresponds to the username and password below.
 * Alternatively, change the username and password to fit yours and run it in your instance of sql plus.
 */
public class FaceSpace {
	
	//global, static variables
	private static Connection dbconn;
	private static Statement statement; //used to create an instance of the connection
    private static PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private static ResultSet resultSet; //used to hold the result of queries
    private static String query;  //this will hold the query we are using
    
    //Mike - injection checked
    public static boolean createUser() {
    	Scanner inScan=new Scanner(System.in);
    	//first get all the required data from the user, ensuring proper input
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
    	//now connect to the db
 	   	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = dbconn.createStatement();
			//first count the current number of users
			query = "SELECT MAX(userId) as maxId "
					+ "FROM Profiles";
			resultSet = statement.executeQuery(query);
		    int max=0;
		    if(resultSet.next()) {
				max = resultSet.getInt("maxId");
		    }
		    
		    //now we have the max id, so do the actual insert
		    query = "INSERT INTO Profiles VALUES (?,?,?,?,?,?,?,NULL)";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setInt(1,max+1); //+1 to increment properly
		    prepStatement.setString(2,fname);
		    prepStatement.setString(3,lname);
		    prepStatement.setString(4,email);
		    prepStatement.setInt(5,dobDay);
		    prepStatement.setInt(6,dobMonth);
		    prepStatement.setInt(7,dobYear);
			
		    System.out.println("Adding user...");
		    int result = prepStatement.executeUpdate();
		    //now just check the result to ensure success
		    if (result == 1) 
		    	System.out.println("Update is successful!");
		    else 
		    	System.out.println("No rows were updated");
		    //commit, then close
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
    	Scanner nums = new Scanner(System.in);
    	
    	System.out.println("Please enter your name or email: ");
    	int senderID = lookupUser();
    	if(senderID <= 0) {
    		System.out.println("Sender not found.");
    		return false;
    	}
    	System.out.println("And the person who you would like to send a request: ");
    	int rID = lookupUser();
    	if(rID <= 0) {
    		System.out.println("Receiver not found.");
    		return false;
    	}
    	
    	if(senderID == rID){
    		System.out.println("Users may not send friend requests to themselves.");
    		return false;
    	}
    	try{
    		if(!getUser(rID)){
    			System.out.println("No one in our database has that ID number.");
    			return false;
    		}
    		//checks if this friend relation already exists
    		if(friends(senderID,rID)){
    			System.out.println("The users are already friends!");
    			return false;
    		}
    	
    	
    		statement = dbconn.createStatement();
    		String checkPendingRequests = "SELECT dateEstablished FROM Friendships " +
                 "WHERE receiverId='"+rID+"' AND senderId='"+senderID+"' AND dateEstablished IS NULL";
    		ResultSet rs= statement.executeQuery(checkPendingRequests);


    		if (rs.next()) {// establishes a friendship if a request was already sent from the other user
             System.out.println("\nThe person with ID"+rID +" already sent you a friend request; establishing friendship now...\n");
             makeFriends(senderID, rID);
         	}
         	else{
         		Statement stmt = dbconn.createStatement();
    			int numFriendship = 0;
    			String query = "SELECT MAX(friendshipId) AS count FROM Friendships";
    			ResultSet res = stmt.executeQuery(query);
    			
    			while(res.next()){
    				numFriendship = res.getInt("count");
    			}
    			
    			//System.out.println(numFriendship);
    			
                res = stmt.executeQuery("SELECT * FROM Friendships");
        	 	String Query = "INSERT INTO Friendships VALUES (?,?,?,?,?)";
        	 	PreparedStatement pstm= dbconn.prepareStatement(Query);
        	 	pstm.setInt(1, (numFriendship+1));
        	 	pstm.setInt(2, senderID);
        	 	pstm.setInt(3, rID);
        	 	pstm.setInt(4, 0);
        	 	pstm.setTimestamp(5,null);
        	 	pstm.executeUpdate();
        	 	pstm.close();
        	 	dbconn.commit();
        	 	System.out.println("Friend request sent!");
         	}        
         }catch (SQLException e){
        	 e.printStackTrace();
        	 System.out.println("ERROR CREATING PENDING FRIEND");
        	 return false;
         }
    	return true;
    }
    
    //Jordan
    public static boolean establishFriendship() throws SQLException {
    	Scanner scan = new Scanner(System.in);
    	
    	System.out.println("Please enter your name or email: ");
    	int userId = lookupUser();
    	if(userId <= 0) {
    		System.out.println("User not found.");
    		return false;
    	}
    
    	try{
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); //because counting number groups
    		System.out.println("Printing pending friendships...");
    		Statement st = dbconn.createStatement();
    		String query = "SELECT fname,lname,friendshipId FROM Friendships JOIN Profiles "
    		+"ON Profiles.userId = Friendships.senderId " +
    		"WHERE receiverId ='" + userId + "'AND approved = 0";
    		
    		prepStatement = dbconn.prepareStatement(query);
    		resultSet = st.executeQuery(query);
    		
    		
    		System.out.println("First Name\tLast Name\tfID");
			System.out.println("--------------------------------------------");
    		while(resultSet.next()){
    			System.out.println(resultSet.getString(1)+"\t"+resultSet.getString(2)+"\t"+resultSet.getInt(3));
    		}
    		System.out.println("Please enter the fID of the request you'd\nlike to accept:");
    		int fID = scan.nextInt();
    		
    		fIDCheck(userId,fID);
    		if(!fIDCheck(userId,fID)){// makes sue the user doesn't enter a bad friendshipID and mess with the wrong data
    			return false;
//    			fID = scan.nextInt();
//    			fIDCheck(userId,fID);
    		}
    		
    		statement = dbconn.createStatement();
    		String sql ="UPDATE Friendships "+
    		"SET approved = 1 "+
    		"WHERE friendshipId = "+fID;
    		statement.executeUpdate(sql);
    		
    		java.util.Date date= new java.util.Date();
    		Timestamp now = new Timestamp(date.getTime());
    		
    		statement = dbconn.createStatement();
    		query = "UPDATE Friendships "+
    	    		"SET dateEstablished = TIMESTAMP '"+ now+
    	    		"' WHERE friendshipId = "+ fID;
    	    //System.out.println(query);
    		statement.executeUpdate(query);
    	    dbconn.commit();
    	    System.out.println("Friendship established. Send 'em a message!");
    		
    	}catch (SQLException e){
    		System.out.println("Cannot establish friendship");
    		dbconn.rollback();
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    private static boolean fIDCheck(int uID,int fID){
    	try {
    		Statement st = dbconn.createStatement();
    		String query = "SELECT fname,lname,friendshipId FROM Friendships JOIN Profiles "
    		+"ON Profiles.userId = Friendships.senderId " +
    		"WHERE receiverId ='" + uID + "'AND approved = 0";
    		
    		prepStatement = dbconn.prepareStatement(query);
    		resultSet = st.executeQuery(query);
			while(resultSet.next()){
				if(fID == resultSet.getInt(3)){
					return true;
				}
			}
		} catch (SQLException e) {
			System.out.println("Check of fID failed. WHAT DID U ENTER??");
			e.printStackTrace();
		}
    	System.out.println("That ID isn't in your friend requests. Please try again.");
    	return false;
    }
    
    //Mike
    public static boolean displayFriends() {
    	Scanner inScan=new Scanner(System.in);
    	
    	System.out.println("Please enter your name or email: ");
    	int userId = lookupUser(); //use this to look up by name or email (then you dont need the id)
    	//ensure proper id was returned
    	if(userId <= 0) {
    		System.out.println("User not found.");
    		return false;
    	}
    	
    	try {
			dbconn.setAutoCommit(true); //the default is true and every statement executed is considered a transaction.
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
			//now print the results
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
			//now print the results
			while(resultSet.next()) {
				name = resultSet.getString("First")+" "+resultSet.getString("Last");
				friendId = resultSet.getInt("UserId");
				System.out.println("Id: "+friendId+"\tName: "+name);
		    }
			//nothing will be printed 
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
    		//because accessing and updating groups table, need to be serializable
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		
    		//create a statement
			Statement stmt = dbconn.createStatement();
			int numGroups = -1;
			
			//get the maximum id to use for new id
			String query = "SELECT MAX(groupId) AS count FROM Groups";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				numGroups = rs.getInt("count");
			}
			
			if(numGroups == -1){
				System.out.println("Error with retrieving maximum group id");
				return false;
			}
			
            rs = stmt.executeQuery("SELECT * FROM Groups");
			System.out.println("Here are all the current groups:");
			
			while(rs.next()){
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getInt(4));
			}
			
			//receive new group information
			System.out.println("\nWhat is the name of your group?");
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
			
			dbconn.commit();
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
    public static boolean addToGroup() {
    	try {
    		//to prevent two users being added at same time, potentially overflowing group membership
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    	Scanner in = new Scanner(System.in);
	    	int userId = -1;
	    	int count = -1;
	    	int groupId = -1;
	    	
	    	Statement stmt = dbconn.createStatement();
	    	
	    	//various queries needed for this function
	    	String countquery = "SELECT COUNT(*) as cnt FROM Profiles WHERE fname = ? AND lname = ?";
	    	String query = "SELECT * FROM Profiles WHERE fname = ? AND lname = ?";
	    	String groupQuery = "SELECT groupId FROM Groups WHERE name = ?";
	    	
	    	//get fname, lname, and group
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
			
			//get group id
			while(rs3.next()){
				groupId = rs3.getInt(1);
			}
			
			//get number of users with fname and lname
			while(rs2.next()){
				count = rs2.getInt("cnt");
			}
			
			//get the user id
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
			
			//insert into group
			//if membership limit is reached, the database will throw an error
			String insertQuery = "INSERT INTO Members VALUES(?, ?)";
			PreparedStatement finalStatement = dbconn.prepareStatement(insertQuery);
			finalStatement.setInt(1, groupId);
			finalStatement.setInt(2, userId);
			
			finalStatement.executeUpdate();
			dbconn.commit();
			
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
    	Scanner in = new Scanner(System.in);
    	
    	/*System.out.println("Please enter you userId:");
    	int Id1 = in.nextInt();
    	System.out.println("Please enter the userId of the person to recieve this message:");
    	int Id2 = in.nextInt();*/
    	System.out.println("Please enter your name or email: ");
    	int Id1 = lookupUser();
    	if(Id1 <= 0) {
    		System.out.println("Sender not found.");
    		return false;
    	}
    	System.out.println("And the person who you would like to send a message: ");
    	int Id2 = lookupUser();
    	if(Id2 <= 0) {
    		System.out.println("Receiver not found.");
    		return false;
    	}
    	
    	try{
    		if(!getUser(Id2)){
    			System.out.println("That recipient doesn't exist.");
    			return false;
    		}else{
    			dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); //because counting number groups
    			//Date date = new Date();
    			Statement stmt = dbconn.createStatement();
    			int numMessages = 0;
    			String query = "SELECT MAX(messageId) AS count FROM Messages";
    			ResultSet rs = stmt.executeQuery(query);
    			
    			while(rs.next()){
    				numMessages = rs.getInt("count");
    			}
    			
    			Scanner text = new Scanner(System.in);
    			
    			System.out.println("Message subject:");
    			String sub = text.nextLine();
    			
    			System.out.println("What is the message (Max 140 characters)?");
    			String mess = text.nextLine();
    			
    			java.util.Date date= new java.util.Date();
    			Timestamp now = new Timestamp(date.getTime());
    			
    			query = "INSERT INTO Messages VALUES (?,?,?,?,?,NULL)";
    			prepStatement = dbconn.prepareStatement(query);
    			prepStatement.setInt(1, (numMessages+1));
    			prepStatement.setInt(2, Id1);
    			prepStatement.setString(3, sub);
    			prepStatement.setString(4, mess);
    			prepStatement.setTimestamp(5, now);
    			
    			System.out.println("Sending the message...");
    			
    			int sent = prepStatement.executeUpdate();
    			
    			query = "INSERT INTO Recipients VALUES (?,?)";
    			prepStatement = dbconn.prepareStatement(query);
    			prepStatement.setInt(1, (numMessages+1));
    			prepStatement.setInt(2, Id2);
    			
    			int recieved = prepStatement.executeUpdate();
    			
    			if(sent==1 && recieved ==1){
    				System.out.println("Message Sent.");
    			}else{
    				System.out.println("Message not sent");
    			}
    			
    			dbconn.commit();
    			rs.close();
    		}
    		
    	}catch(SQLException e){
    		System.out.println("Could not send message.");
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    //Jordan
    //TODO: make this by lookup
    public static boolean sendMessageToGroup() {
    	Scanner sc = new Scanner (System.in);
    	/*System.out.println("Plz enter your userId");
    	int userID = sc.nextInt();*/
    	System.out.println("Please enter the name or email of the sender: ");
    	int userID = lookupUser();
    	if(userID <= 0) {
    		System.out.println("User not found.");
    		return false;
    	}
    	/*System.out.println("Please enter the Id of the group you wish\nto send a message to");
    	int groupID = sc.nextInt();*/
    	try{
    		//first get the group id
	    	System.out.println("What group?");
	    	String group = sc.nextLine();
	    	while(!checkInput(group)){
	    		group = sc.nextLine();
	    	}
	    	//Statement stmt = dbconn.createStatement();
	    	String groupQuery = "SELECT groupId FROM Groups WHERE name = ?";
			PreparedStatement pstmt3 = dbconn.prepareStatement(groupQuery);
			pstmt3.setString(1, group);
    		ResultSet rs3 = pstmt3.executeQuery();
    		int groupID = -1;
			while(rs3.next()){
				groupID = rs3.getInt(1);
			}
    		
			
    		Statement stmt = dbconn.createStatement();
    		String query = "SELECT * FROM Members WHERE userId = "
    				+ userID + " AND groupId = " + groupID;
    		PreparedStatement ps = dbconn.prepareStatement(query);
    		ResultSet r = ps.executeQuery();
    		
    		if(!r.next()){
    			System.out.println("You cannot send a message to this group.\n Either you aren't a member of it or\n it does not exist.");
    			return false;
    		}
    		
    		Scanner groupText = new Scanner(System.in);
    		
    		System.out.println("What is the subject of the message?");
    		String subject = groupText.nextLine();
    		
    		System.out.println("Message text(140 chars):");
    		String message = groupText.nextLine();
    		
    		String query2 = "SELECT P.userId "
    		+"FROM (Profiles) P JOIN (Members) M "
    		+"ON P.userId = M.userId "
    		+"WHERE M.groupId="+groupID+ " AND P.userId != " + userID;
    		
    		ResultSet res = stmt.executeQuery(query2);
    		
    		while(res.next()){
    			Statement stment = dbconn.createStatement();
    			int numMessages = 0;
    			String newQuery = "SELECT MAX(messageId) AS count FROM Messages";
    			ResultSet rs = stment.executeQuery(newQuery);
    			
    			while(rs.next()){
    				numMessages = rs.getInt("count");
    			}
    			
    			java.util.Date date= new java.util.Date();
    			Timestamp now = new Timestamp(date.getTime());
    			
    			query = "INSERT INTO Messages VALUES (?,?,?,?,?,NULL)";
    			prepStatement = dbconn.prepareStatement(query);
    			prepStatement.setInt(1, (numMessages+1));
    			prepStatement.setInt(2, userID);
    			prepStatement.setString(3, subject);
    			prepStatement.setString(4, message);
    			prepStatement.setTimestamp(5, now);
    			
    			//System.out.println("Sending the message...");
    			
    			int sent = prepStatement.executeUpdate();
    			
    			query = "INSERT INTO Recipients VALUES (?,?)";
    			prepStatement = dbconn.prepareStatement(query);
    			prepStatement.setInt(1, (numMessages+1));
    			prepStatement.setInt(2, res.getInt(1));
    			
    			int recieved = prepStatement.executeUpdate();
    			//System.out.println(res.getInt(1));
    			dbconn.commit();
    		}
    		
    		
    	}catch(SQLException e){
    		System.out.println("Error encountered: Cannot send the message.");
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    //Mike
    public static boolean displayMessages() {
    	Scanner inScan=new Scanner(System.in);
    	//first use lookupUser to get the userId required
    	System.out.println("Please enter your name or email: ");
    	int userId = lookupUser();
    	if(userId <= 0) {
    		System.out.println("User not found.");
    		return false;
    	}

    	try {
			dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
			//first get all the friendships with the senderId==input and receiverId is the friend
			//get users
			String subQueryProfiles = "SELECT userId,fname,lname FROM Profiles";
			//get messages
			String subQueryMessages1 = "SELECT messageId,senderId FROM Messages";
			//get senders
			String subQuerySenders = "SELECT M.messageId as messageId,P.fname AS fname,P.lname AS lname "
					+"FROM ("+subQueryProfiles+") P JOIN ("+subQueryMessages1+") M "
					+"ON P.userId=M.senderId";
			//join messages and senders
			String subQueryMessages2 = "SELECT M.messageId as messageId,M.senderId AS senderId,M.subject AS subject,M.messageText AS messageText,M.dateSent as dateSent,S.fname AS fname,S.lname AS lname "
					+"FROM Messages M JOIN ("+subQuerySenders+") S "
					+"ON M.messageId=S.messageId";
			//get recipients, join the above query with the recipients
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
			//now get and print the message data
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
    	//first get the receiver id of the messages
    	System.out.println("Please enter your name or email: ");
    	int userId = lookupUser();
    	if(userId <= 0) {
    		System.out.println("User not found.");
    		return false;
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
		    //here is where we filter by last login time
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
			//now just print the results
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
    	//first get the 2 ids of the users that we are looking for a connection between
    	Scanner inScan=new Scanner(System.in);
    	System.out.println("Please enter the name or email of user A: ");
    	int userId_A = lookupUser();
    	if(userId_A <= 0) {
    		System.out.println("User A not found.");
    		return false;
    	}
    	System.out.println("Please enter the name or email of user B: ");
    	int userId_B = lookupUser();
    	if(userId_B <= 0) {
    		System.out.println("User B not found.");
    		return false;
    	}
    	//now begin building the string to output with the results
		String pathStart="Start: User "+userId_A,currPath1,currPath2,currPath3;
		ArrayList<Integer> friends_1,friends_2,friends_3;
		
		//get all the 1st level friends for user A
		friends_1 = getFriends(userId_A);
		//the following utilizes depth first search, so a connection 3 deep may be found before a direct friend connection
		//first level - loop through all the direct friends first
		for(int i=0; i<friends_1.size(); i++) {
			//update the current path
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
				//return if a path is found
				return true;
			}
			else {
				friends_2 = getFriends(friends_1.get(i));
    			//second level - loop through friends of friends
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
	        			//third level - loop through friends of friends of friends
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
		//if not returned by now, all options were checked and nothing was found
		System.out.printf("No path found...");
    	return true;
    }
    
    //this function simply gets the friend ids of the userId parameter and returns an arraylist of these ids
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
    	Scanner cs = new Scanner(System.in);
    	System.out.println("How many of the top senders would\n you like to see?");
    	int top = cs.nextInt();
    	System.out.println("And how many months back would you like to go?");
    	int months = cs.nextInt();
    	try {
			dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = dbconn.createStatement();
			String query = "SELECT senderId, COUNT(*) "+
			"FROM Messages JOIN Recipients "+
			"ON Messages.messageId = Recipients.messageId "+
			"WHERE Messages.dateSent > "+
			"(SELECT add_months(current_date,-"+months+") FROM DUAL) "+
			"GROUP BY senderId "+
			"ORDER BY COUNT(*) DESC ";
			//"FETCH FIRST "+top+" ROWS ONLY";
			
			resultSet = statement.executeQuery(query);
			
			System.out.println("sID:\n-------------------------------");
			for(int i = 0; i < top; i++){
				resultSet.next();
				System.out.println(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Could not get top messagers for you");
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    //Gabe
    public static boolean dropUser() {
    	try {
    		//serializable because nothing else should occur when dropUser is taking place
    		dbconn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		
	    	Scanner in = new Scanner(System.in);
	    	
	    	Statement stmt = dbconn.createStatement();
	    	
	    	/*System.out.println("What is userId?");
	    	int id = in.nextInt();*/
	    	System.out.println("Please enter the name or email of the user to drop: ");
	    	int id = lookupUser();
	    	if(id <= 0) {
	    		System.out.println("User not found.");
	    		return false;
	    	}
	    	
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
	    		PreparedStatement pstmt2 = dbconn.prepareStatement(recipientsQuery);
	    		pstmt2.setInt(1,  msgId);
	    		ResultSet rs = pstmt2.executeQuery();
	    		while(rs.next()){
	    			counter++;
	    		}
	    		if(counter == 0){
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
			dbconn.commit();
	    	
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
    
  //helper functions for establishing/initializing friends
  	private static boolean friends(int S, int R){
  		try{
  			statement = dbconn.createStatement();
  			String isFriends = "SELECT dateEstablished FROM Friendships " +
                      "WHERE (senderId='"+S+"' AND receiverId='"+R+"' AND dateEstablished IS NOT NULL) OR"+
                      "(senderId='"+R+"' AND receiverId='"+S+"' AND dateEstablished IS NOT NULL)";
              ResultSet resultSet = statement.executeQuery(isFriends);
              if(resultSet.next()) return true;
              else return false;
  		}catch (SQLException e){
              e.printStackTrace();
          }
  		return false;
  	}
  	
  	public static boolean getUser(int ID){
  		try{
  			statement = dbconn.createStatement();
  			String userSearch = "SELECT fname FROM Profiles WHERE userId = '" +ID+"'";
  			ResultSet r = statement.executeQuery(userSearch);
  			if(r.next()){
  				return true;
  			}
  			
  		}catch (SQLException e){
  			e.printStackTrace();
  			System.out.println("Could not check for existence of user.");
  			return false;
  		}
  		return false;
  	}
  	
  	//establishes friendship between two users. different from establishFriendship b/c it's only used under special circumstances
  	public static boolean makeFriends(int sID, int rID) {
    	try{
    		statement = dbconn.createStatement();
    		String sql ="UPDATE Friendships "+
    		"SET approved = 1 "+
    		"WHERE senderId = "+sID +" AND recieverId = "+ rID +" AND approved = 0";
    		statement.executeUpdate(sql);
    		
    		java.util.Date date= new java.util.Date();
    		Timestamp now = new Timestamp(date.getTime());
    		
    		statement = dbconn.createStatement();
    		String query = "UPDATE Friendships "+
    	    		"SET dateEstablished = TIMESTAMP '"+ now+
    	    		"' WHERE senderId = "+sID +" AND recieverId = "+ rID +" AND approved = 0";
    	    //System.out.println(query);
    		statement.executeUpdate(query);
    	}catch(SQLException e){
    		System.out.println("AY. STOP WHAT UR DOING.");
    		e.printStackTrace();
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
    
    private static int lookupUser() {
    	Scanner inScan=new Scanner(System.in);
    	int userId = -1;
    	System.out.println("Lookup user by email or name? (E/N)");
    	String response = inScan.next();
    	if(response.toUpperCase().equals("E")) {
    		System.out.print("Enter exact email: ");
    		String email = inScan.next();
    		userId = lookupByEmail(email);
    	}
    	else {
    		System.out.print("Enter exact first name: ");
    		String fname = inScan.next();
    		System.out.print("Enter exact last name: ");
    		String lname = inScan.next();
    		userId = lookupByName(fname,lname);
    	}    	
    	return userId;
    }
    
    private static int lookupByEmail(String email) {
    	Scanner inScan = new Scanner(System.in);
    	int userId = -1;
    	try {
    		PreparedStatement prepStatement2;
    		ResultSet resultSet2;
			//dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
		    query = "SELECT userId, fname, lname, email "
		    		+"FROM Profiles "
		    		+"WHERE email=?";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement2 = dbconn.prepareStatement(query);
		    prepStatement.setString(1,email);
		    prepStatement2.setString(1,email);
			resultSet = prepStatement.executeQuery();
			resultSet2 = prepStatement2.executeQuery();
			int count = 0;
			while(resultSet.next()) {
				count++;
		    }
			System.out.println(count);
			if(count == 0) {
				System.out.println("User not found.");
				userId = -1;
			}
			else if(count == 1) {
				System.out.println("User found!");
				resultSet2.next();
				userId = resultSet2.getInt("userId");
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
				userId = ids.get(inScan.nextInt()-1);
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
    	return userId;
    }
    
    private static int lookupByName(String fname, String lname) {
    	Scanner inScan = new Scanner(System.in);
    	int userId = -1;
    	try {
    		PreparedStatement prepStatement2;
    		ResultSet resultSet2;
			//dbconn.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
			dbconn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
			statement = dbconn.createStatement();
			
		    query = "SELECT userId,fname,lname,email "
		    		+"FROM Profiles "
		    		+"WHERE fname=? AND lname=?";
		    prepStatement = dbconn.prepareStatement(query);
		    prepStatement.setString(1,fname);
		    prepStatement.setString(2,lname);
		    prepStatement2 = dbconn.prepareStatement(query);
		    prepStatement2.setString(1,fname);
		    prepStatement2.setString(2,lname);
			resultSet = prepStatement.executeQuery();
			resultSet2 = prepStatement2.executeQuery();
			int count = 0;
			while(resultSet.next()) {
				count++;
		    }
			if(count == 0) {
				System.out.println("User not found.");
				userId = -1;
			}
			else if(count == 1) {
				System.out.println("User found!");
				resultSet2.next();
				userId = resultSet2.getInt("userId");
			}
			else {
				int index = 0;
				ArrayList<Integer> ids = new ArrayList<Integer>();
				while(resultSet2.next()) {
					index++;
					ids.add(resultSet2.getInt("userId"));
					System.out.println(index+".\t"+resultSet2.getString("fname")+" "+resultSet2.getString("lname")+"\t"+resultSet2.getString("email"));
				}
				System.out.println("\nChoose a user (enter the first number on the line)(enter 0 if none): ");
				userId = ids.get(inScan.nextInt()-1);
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
    	
    	return userId;
    }
    
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
		String username = "mjm275";
		String password = "3844041";
		
		System.out.println("Welcome to facespace");

		try{
		    // Register the oracle driver.  
		    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		    
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
		    


		    while(choice > 0 && choice < 15){
		    
			    switch(choice){
				    case 1:
				    	createUser();
				    	break;
				    case 2:
				    	initiateFriendship();
				    	break;
				    case 3:
				    	establishFriendship();
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
			    	case 7:
			    		sendMessageToUser();
			    		break;
			    	case 8:
			    		sendMessageToGroup();
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
				    case 13:
				    	topMessagers();
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
}

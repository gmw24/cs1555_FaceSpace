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
    
    //Mike
    public static boolean createUser() {
    	Scanner inScan=new Scanner(System.in);
    	System.out.print("\nEnter the first name: ");
    	String fname = inScan.next();
    	System.out.print("\nEnter the last name: ");
    	String lname = inScan.next();
    	System.out.print("\nEnter the email address: ");
    	String email = inScan.next();
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
 	   		System.out.println("Getting number of rows...");
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
		    System.out.println("Number of rows: "+max);
		    
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
		    	System.out.println("Update is successful, result=" + result);
		    else 
		    	System.out.println("No rows were updated");
		    
		    dbconn.commit();
		    resultSet.close();
	    }
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
    
    //Mike
    public static boolean displayFriends() {
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
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
    public static boolean createGroup() {
    	
    	return true;
    }
    
    //Gabe
    //need to check membership limit of group
    public static boolean addToGroup() {
    	
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
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
    	try {
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
	    }
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
		catch(Exception Ex)  
		{
			System.out.println("Machine Error: " +
					   Ex.toString());
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
    
	public static void main(String args[]) throws SQLException{		
		String username = "mjm275";
		String password = "3844041";

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
		    
		    int choice = showMenu();
		    
		    while(choice < 0 && choice > 14){
		    	System.out.println("Sorry, that choice is not recognized.");
		    	choice = showMenu();
		    }
		    
		    switch(choice){
			    case 1:
			    	createUser();
			    	break;
			    case 4:
			    	displayFriends();
			    	break;
			    case 9:
			    	displayMessages();
			    	break;
			    case 10:
			    	displayNewMessages();
			    	break;
			    case 12:
			    	threeDegrees();
			    	break;
			    default:
			    	break;
		    }
		}
		catch(Exception Ex)  {
		    System.out.println("Error connecting to database.  Machine Error: " +
				       Ex.toString());
		}
		finally
		{
			/*
			 * NOTE: the connection should be created once and used through out the whole project;
			 * Is very expensive to open a connection therefore you should not close it after every operation on database
			 */
			dbconn.close();
		}
	}
}


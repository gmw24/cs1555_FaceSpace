import java.sql.*;
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
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of queries
    private String query;  //this will hold the query we are using
    
    //Mike
    public static boolean createUser() {
    	
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
    	
    	return true;
    }
    
    //Gabe
    public static boolean createGroup() {
    	System.out.println("--Create group--");
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
			System.out.println("What is it's description?");
			String groupDesc = in.nextLine();
			System.out.println("What is the membership limit?");
			int membership = in.nextInt();
			
			query = "INSERT INTO Groups VALUES (?,?,?,?)";
			PreparedStatement pstmt = dbconn.prepareStatement(query);
			pstmt.setInt(1,(numGroups+1));
			pstmt.setString(2,groupName);
			pstmt.setString(3,groupDesc);
			pstmt.setInt(4,membership);
			
			System.out.println("Executing prepared statement");
			pstmt.executeUpdate();
			
			
			System.out.println("Insert complete!");
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
//	    	String qry = "SELECT * FROM Profiles";
//	    	String qry2 = "SELECT * FROM Groups";
//	    	ResultSet resSet = stmt.executeQuery(qry);
//	    	ResultSet resSet2 = stmt.executeQuery(qry2);
//	    	
//	    	while(resSet.next()){
//	    		System.out.println(resSet.getInt(1) + " " + resSet.getString(2) + " " + resSet.getString(3));
//	    	}
//	    	
//	    	while(resSet2.next()){
//	    		System.out.println(resSet2.getInt(1) + " " + resSet2.getString(2) + " " + resSet2.getString(3));
//	    	}
	    	
	    	String countquery = "SELECT COUNT(*) as cnt FROM Profiles WHERE fname = ? AND lname = ?";
	    	String query = "SELECT * FROM Profiles WHERE fname = ? AND lname = ?";
	    	String groupQuery = "SELECT groupId FROM Groups WHERE name = ?";
	    	
	    	System.out.println("What is the first name?");
	    	String fname = in.nextLine();
	    	System.out.println("What is the last name?");
	    	String lname = in.nextLine();
	    	System.out.println("What group?");
	    	String group = in.nextLine();
    	
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
				System.out.println(count);
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
			System.out.println(userId);
			
			if(groupId == -1 || userId == -1){
				System.out.println("userId:" + userId);
				System.out.println("groupId:" + groupId);
				System.out.println("Error. Either user or group id was not found");
				return false;
			}
			
			String insertQuery = "INSERT INTO Members VALUES(?, ?)";
			
			PreparedStatement finalStatement = dbconn.prepareStatement(insertQuery);
			finalStatement.setInt(1, groupId);
			finalStatement.setInt(2, userId);
			
			finalStatement.executeUpdate();
			
			System.out.println("Row added successfully");
	    	
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
    	
    	return true;
    }
    
    //Mike
    public static boolean displayNewMessages() {
    	
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
	    	
	    	String searchQuery = "SELECT * FROM Profiles WHERE UPPER(fname) LIKE ? OR UPPER(lname) LIKE ?";
	    	
	    	String[] partials = searchString.split(" ");
	    	ResultSet rs;
	    	PreparedStatement pstmt;
	    	int count = 0;
	    	
	    	for (int i = 0; i < partials.length; i++){
	    		System.out.println("All users matching " + partials[i] + ":");
	    		pstmt = dbconn.prepareStatement(searchQuery);
	    		pstmt.setString(1, "%" + partials[i] + "%");
	    		pstmt.setString(2, "%" + partials[i] + "%");
	    		rs = pstmt.executeQuery();
	    		while(rs.next()){
	    			count++;
	    			System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getInt(5) + "/" +
	    					rs.getInt(6) + "/" + rs.getString(7) + " " + rs.getTimestamp(8));
	    		}
	    		if(count == 0)
	    			System.out.println("No results found!");
	    		else 
	    			count = 0;
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
    	
    	return true;
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
		    
		    int choice = showMenu();
		    
		    while(choice < 0 && choice > 14){
		    	System.out.println("Sorry, that choice is not recognized.");
		    	choice = showMenu();
		    }
		    
		    switch(choice){
		    	case 5:
		    		createGroup();
		    		break;
		    	case 6:
		    		addToGroup();
		    		break;
		    	case 11:
		    		searchForUser();
		    		break;
		    	case 14:
		    		dropUser();
		    		break;
		    	default:
		    		break;
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


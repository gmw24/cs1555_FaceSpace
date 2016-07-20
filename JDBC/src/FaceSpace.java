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
			
	    	try {
    			if (stmt !=null) stmt.close();
    		} catch (SQLException e) {
    			System.out.println("Cannot close Statement. Machine error: "+e.toString());
    		}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return true;
    }
    
    //Gabe
    //need to check membership limit of group
    public static boolean addToGroup() {
    	
    	try {
	    	Scanner in = new Scanner(System.in);
	    	int userId = -1;
	    	int count = -1;
	    	int groupId = -1;
	    	
	    	Statement stmt = dbconn.createStatement();
	    	String qry = "SELECT * FROM Profiles";
	    	ResultSet resSet = stmt.executeQuery(qry);
	    	
	    	while(resSet.next()){
	    		System.out.println(resSet.getInt(1) + " " + resSet.getString(2) + " " + resSet.getString(3));
	    	}
	    	
	    	String countquery = "SELECT COUNT(*) as cnt FROM Profiles WHERE fname = ? AND lname = ?";
	    	String query = "SELECT * FROM Profiles WHERE fname = ? AND lname = ?";
	    	String groupQuery = "SELECT groupId FROM Groups WHERE name = ?";
	    	
	    	System.out.println("What is the first name?");
	    	String fname = in.next();
	    	System.out.println("What is the last name?");
	    	String lname = in.next();
	    	System.out.println("What group?");
	    	String group = in.next();
    	
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
				groupId = rs.getInt(1);
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
			
			String insertQuery = "INSERT INTO Members VALUES(?, ?)";
			
			PreparedStatement finalStatement = dbconn.prepareStatement(insertQuery);
			finalStatement.setInt(1, groupId);
			finalStatement.setInt(2, userId);
			
			finalStatement.executeUpdate();
	    	
	    	try {
			if (stmt !=null) stmt.close();
		} catch (SQLException e) {
			System.out.println("Cannot close Statement. Machine error: "+e.toString());
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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


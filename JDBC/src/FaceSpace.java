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
	private Statement statement; //used to create an instance of the connection
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of queries
    private String query;  //this will hold the query we are using
    
    public static boolean createUser() {
    	
    	return true;
    }

    public static boolean initiateFriendship() {
    	
    	return true;
    }

    public static boolean establishFriendship() {
    	
    	return true;
    }

    public static boolean displayFriends() {
    	
    	return true;
    }

    public static boolean createGroup() {
    	
    	return true;
    }
    
    public static boolean addToGroup() {
    	
    	return true;
    }
    
    public static boolean sendMessageToUser() {
    	
    	return true;
    }
    
    public static boolean sendMessageToGroup() {
    	
    	return true;
    }
    
    public static boolean displayMessages() {
    	
    	return true;
    }
    
    public static boolean displayNewMessages() {
    	
    	return true;
    }
    
    public static boolean searchForUser() {
    	
    	return true;
    }
    
    public static boolean threeDegrees() {
    	
    	return true;
    }
    
    public static boolean topMessagers() {
    	
    	return true;
    }
    
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


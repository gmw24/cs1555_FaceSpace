/*
 * This is the driver program for FaceSpace. It runs all the functions and displays working functionality.
 * Please note: before running this, execute the 3 SQL files present in the repository to make sure the db 
 * is in a consistent state. Run them in this order:
 * facespace_db.sql
 * facespace_data.sql
 * facespace_integrity.sql
 */

import java.sql.SQLException;
import java.util.Scanner;


public class driver_FS {
	
	public static void main(String[] args) throws SQLException {
		
		Scanner inScan=new Scanner(System.in);
		FaceSpace theFS = new FaceSpace();
		
		System.out.println("First we initiate the connection.");
		theFS.initConnection();
		
		System.out.println("Demostrating our FaceSpace functionality!");
		System.out.print("Press enter to begin:");
		inScan.next();
		
		System.out.println("Will begin by proving createUser() and searchForUser() work...");
		System.out.println("Calling searchForUser(\"Jim Thorpe\"");
		theFS.searchForUser("Thorp");
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling createUser(\"Jim\",\"Thorpe\",\"jthorpe@gmail.com\",1995,1,1)");
		theFS.createUser("Jim","Thorpe","jthorpe@gmail.com",1995,1,1);
		System.out.println("Calling searchForUser(\"Thorp\"");
		theFS.searchForUser("Thorp");
		System.out.print("Done with this part! Press enter to continue:");
		inScan.next();
		
		System.out.println("Will now prove initiateFriendship(), establishFriendship(), and displayFriends() work...");
		System.out.println("Calling displayFriends(1)");
		theFS.displayFriends(1);
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling initiateFriendship(1,6)");
		int fNum = theFS.initiateFriendship(1,6);
		System.out.println("Calling displayFriends(1)");
		theFS.displayFriends(1);
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling establishFriendship(1,6)");
		theFS.establishFriendship(6,fNum);
		System.out.println("Calling displayFriends(1)");
		theFS.displayFriends(1);
		System.out.print("Done with this part! Press enter to continue:");
		inScan.next();
		
		System.out.println("Will now prove displayMessages() and displayNewMessages() work...");
		System.out.println("Calling displayMessages(1)");
		theFS.displayMessages(1);
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling displayNewMessages(1)");
		theFS.displayNewMessages(1);
		System.out.print("Done with this part! Press enter to continue:");
		inScan.next();
		
		//drop user
		System.out.println("Dropping user with id of 2 (Mike McAlpin)");
		theFS.dropUser(2);
		System.out.println("Searching for user \"Mike McAlpin\"");
		theFS.searchForUser("Mike");
		System.out.println("Press enter to continue:");
		inScan.next();
		
		
		System.out.println("Displaying the top 3 messagers for the past 2 months");
		theFS.topMessagers(3, 6);
		System.out.println("Press enter to continue:");
		inScan.next();
		System.out.println("Displaying the top 10 messages for the past 6 months");
		theFS.topMessagers(10, 6);
		System.out.println("Press enter to continue:");
		inScan.next();
		
		System.out.println("Running the three degrees query.");
		System.out.println("Searching for three degrees between userIds 1 and 69");
		theFS.threeDegrees(1, 69);
		System.out.println("Searching for three degrees between userIds 69 and 75");
		theFS.threeDegrees(69, 75);
		
	}
}

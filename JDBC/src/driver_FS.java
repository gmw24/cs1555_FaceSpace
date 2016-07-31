import java.sql.SQLException;
import java.util.Scanner;


public class driver_FS {
	
		
	
	public static void main(String[] args) throws SQLException {
		
		Scanner inScan=new Scanner(System.in);
		FaceSpace theFS = new FaceSpace();
		
		System.out.println("Demostrating our FaceSpace functionality!");
		System.out.print("Press enter to begin:");
		inScan.next();
		
		System.out.println("Will begin by proving createUser() works with searchForUser()...");
		System.out.println("Calling searchForUser(\"Jim Thorpe\"");
		theFS.searchForUser("Jim Thorpe");
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling createUser(\"Jim\",\"Thorpe\",\"jthorpe@gmail.com\",1995,1,1)");
		theFS.createUser("Jim","Thorpe","jthorpe@gmail.com",1995,1,1);
		System.out.println("Calling searchForUser(\"Jim Thorpe\"");
		theFS.searchForUser("Jim Thorpe");
		System.out.println("Press enter to continue:");
		inScan.next();

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
		
		System.out.println("Will now prove displayMessages() and displayNewMessages() work...");
		System.out.println("Calling displayMessages(1)");
		theFS.displayMessages(1);
		System.out.print("Press enter to continue:");
		inScan.next();
		System.out.println("Calling displayNewMessages(1)");
		theFS.displayNewMessages(1);
		System.out.print("Done with this part! Press enter to continue:");
		inScan.next();
		
		System.out.println("Proving that createGroup() and addToGroup() work together...");
		System.out.println("Calling createGroup()...");
		theFS.createGroup("Goofy Goobers","We're all Goofy Goobers, yeah",6);
		System.out.println("Adding a user to this group...");
		theFS.addToGroup("Britton","Jordan","Goofy Goobers");
		System.out.print("Done with this part! Press enter to continue:");
		inScan.next();

		System.out.println("Proving that sendMessageToUser() works...");
		theFS.sendMessageToUser(1,47,"I h8 U","You sold me a bad hairdo");
		System.out.println("And sendMessageToGroup()...");
		theFS.sendMessageToGroup(61,5,"We Win","All your base are belong to us");
		System.out.print("Done with sending messages. Press enter to continue");
		inScan.next();
		
		theFS.closeConnection();
	}

}

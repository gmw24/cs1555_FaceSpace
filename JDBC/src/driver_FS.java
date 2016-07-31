import java.sql.SQLException;
import java.util.Scanner;


public class driver_FS {
	
		
	
	public static void main(String[] args) throws SQLException {
		
		Scanner inScan=new Scanner(System.in);
		FaceSpace theFS = new FaceSpace();
		
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
		
		
		
	}

}

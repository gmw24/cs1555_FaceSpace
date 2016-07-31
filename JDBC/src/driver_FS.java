import java.util.Scanner;


public class driver_FS {
	
		
	
	public static void main(String[] args) {
		
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
		
		
		
	}

}

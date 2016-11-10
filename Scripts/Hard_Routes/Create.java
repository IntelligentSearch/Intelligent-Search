import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Create {
				public static Scanner scanner;
				public static void main(String[] args) {
								File file = new File("./Silver.txt");
								try {
												scanner = new Scanner(file);
												int order = 1;
												while (scanner.hasNextLine()) { 
																String line = scanner.nextLine();
																line = line.trim();
																line = line.substring(1, line.length()-2);
																String [] coords = line.split(",");
													
																System.out.println("INSERT INTO Hard_Route VALUES (\"Silver Loop\", " + coords[0] + "," + coords[1] + "," + order++ + ");");
												}
								} catch (Exception e) {

								}
				}
}

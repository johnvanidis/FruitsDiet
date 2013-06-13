import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*Αναδρομική εξίσωση:
 * Α: ο "στόχος" για την συνολική βιταμίνη ( 0<=i<=A)
 * τα φρούτα (k στο σύνολο)  (0<=j<=k-1) με περιεκτικότητα σε βιταμίνη (βιταμίνη(j))
 * 
 * 
 * C[j][i]= C[j+1][i]                            Aν το i είναι μικρότερο από την βιταμίνη που περιέχει το φρούτο j
 *          min(C[j+1][i],1+C[j][i-βιταμίνη(j)]  Διαφορετικά
 *  
 *  Σημείωση: Αναλόγως με τα διαθέσιμα φρούτα πολλοί αριθμοί-στόχοι δεν μπορούν να σχηματιστούν και γίνονται
 *  κατάλληλοι έλεγχοι 
 *  
 *  */
public class Fruits {


  static int min(int a,int b){
		if(a<b)
			return a;
		return b;
	}

	//Φρούτα
	private static class fruit{

		String name;//όνομα
		int vitamin;//περιεκτικότητα

		public fruit(String nam, int vita) {
			vitamin = vita;
			name = nam;
		}


		@Override
		public String toString() {
			return name + " " + vitamin;
		}
	}

	//Ανάγνωση του αρχείου
	public static int readFile(ArrayList<fruit> fruits) {
		DataInputStream in;
		int target = 0;
		int temp=0;
		try {
			in = new DataInputStream(new FileInputStream("fruits.txt"));

			BufferedReader line = new BufferedReader(new InputStreamReader(in));

			String strline;
			Pattern p1 = Pattern.compile("\\d+");
			Pattern p2 = Pattern.compile("[a-zA-Z]+");
			strline = line.readLine();

			Matcher m1 = p1.matcher(strline);
			Matcher m2;
			m1.find();
			target = Integer.parseInt(m1.group());
			while ((strline = line.readLine()) != null) {
				if (strline.equals(""))
					continue;
				m1 = p1.matcher(strline);
				m2 = p2.matcher(strline);
				m1.find();
				m2.find();
				temp=Integer.parseInt(m1.group());
				if(temp>0)
					fruits.add(new fruit(m2.group(), temp));
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("File error");
			System.exit(-1);
		}
		return target;
	}

	
	static void dynamicProgramming(ArrayList<fruit> fruits,int[][] c, int target){
		
		int temp;//βοηθητικός ακέραιος
		
		int amount;//βοηθητικός ακέραιος
		int j = fruits.size() - 1;//δείκτης που ξεκινά από το τελευταίο φρούτο ( αν είναι ταξινομιμένα με φθήνουσα σειρά αυτό με την μικρότερη περιεκτικότητα σε βιταμίνη)
		
		//βρόχος για αρχικοποίηση της τελευταίας γραμμής, δηλαδή για τα υποπροβλήματα που είναι διαθέσιμο μόνο το τελευταίο φρούτο
		for (int i = 0; i < target + 1; i++) {
			//ο δείκτης i μεταβάλλεται από 0 μέχρι τον στόχο και αντιπροσωπεύει ποιος είναι ο στόχος βιταμήνης
			//σε κάθε υποπρόβλημα
			
			
			temp = i % fruits.get(j).vitamin;//αν με το διαθέσιμο φρούτο δεν μπορεί να σχηματιστεί ο στόχος τότε αυτή η μεταβλητή δεν είναι 0
			
			amount = i / fruits.get(j).vitamin;//πόσα φρούτα χρειάζονται για τον στόχο
			
			//αν με το διαθέσιμο φρούτο ο αριθμός στόχος δεν μπορεί να επιτευχθεί 
			//(δλδ ο στόχος δεν είναι πολλαπλάσιο της περιεκτικότητας του φρούτου)
			if (temp != 0) {
				c[j][i] = Integer.MAX_VALUE; //στο κελί μπαίνει η τιμή MAX_VALUE που συμβολίζει την αδυναμία
				//να "φτιαχτεί" αυτός ο αριθμός
			} else {
				//διαφορετικά τοποθετείται ο αριθμός των φρούτων που χρειάζονται
				c[j][i]=amount;
			}
		}
		
		//ενημέρωση και των υπόλοιπων κελιών
		//κάθε φορά που μειώνεται ο δείκτης j λύνονται υποπροβλήματα στα οποία επιτρέπεται να χρησιμοποιηθεί ακόμη ένα είδος φρούτου
		for (j--; j >= 0; j--) {
			
			int newvit = fruits.get(j).vitamin;//η περιεκτικότητα του νέου φρούτου
			
				//αφού φτάσει ο δείκτης την περιεκτικότητα:
				//συγκρύνονται τα κελιά που περιγράφει η αναδρομική εξίσωση
			for (int i = 0; i < target + 1; i++)	{
				if(i<newvit){
					//Όπως περιγράφει και η αναδρομική εξίσωση, όσο ο στόχος είναι μικρότερος της
					//περιεκτικότητας του νέου φρούτου, αντιγράφεται η τιμή του "από κάτω" υποπρολήματος
					c[j][i] = c[j + 1][i];
					}
				else{
						if (c[j][i - newvit] == Integer.MAX_VALUE){
							c[j][i] = c[j + 1][i];//στην περίπτωση που βρεθεί κελί με MAX_VALUE, αυτόματα επιλέγεται
							//αυτό που βρίσκεται από κάτω
						}else{
							int tempCell=c[j][i - newvit]+1;
							c[j][i] = min(c[j + 1][i], tempCell);
					}	
				}
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<fruit> fruits = new ArrayList<fruit>();
		int target = readFile(fruits);
		int[][] c = new int[fruits.size()][target + 1];
		dynamicProgramming(fruits, c, target);

		//Η λύση βρίσκεται στο τελευταίο κελί της πρώτης σειράς
		if (c[0][target] == Integer.MAX_VALUE) {//αν έχει τιμή MAX_VALUE τότε το πρόβλημα δεν λύνεται
			System.out.println("No solution found");
		}else{
			System.out.println(" Target: "+target);
			System.out.println("#Fruits: "+c[0][target]);//Εκτύπωση της λύσης
			int j=target;
			int i=0;
			int count=0;
			while(j>=0 && i<fruits.size()-1){
				if (c[i][j]>=c[i+1][j]){
					if (count>0)
						System.out.println(fruits.get(i).name+"*"+count);
					count=0;
					i++;
					continue;
				}
				count++;
				j-=fruits.get(i).vitamin;
			}
			if(c[i][j]>0 && c[i][j]!=Integer.MAX_VALUE)
				System.out.println(fruits.get(i).name+"*"+c[i][j]	);
		}
	}

}

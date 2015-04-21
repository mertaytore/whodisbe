import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author      cagdas	boyinankara@gmail.com
 * @version     1.0                 
 * @since       2015-04-15
 * Instructor class to hold information about the instructors, ease the job when it comes to 
 * compare their names to determine such instructor exists teaching other classes
 */

public class Instructor implements Comparator {
	
	String name;
	/*following 2d arrays hold data for date/time
	 * there are 5 arrays in the first dimension to determine the day of the week
	 * and there are 8 arrays in the second dimension to determine the class hour belonging to that day
	 */
	boolean[][] haveClass = new boolean[5][8];
	// location of the classroom
	String[][] location = new String[5][8];
	// the department and course codes of the class that is taught
	String[][] clase = new String[5][8];
	
	public Instructor(String name){
		this.name = name;
		//set all classes to false initially before adding classes
		for(int i = 0; i < haveClass.length; i++)
		{
			for(int j = 0; j < haveClass[i].length; j++)
			{
				haveClass[i][j] = false;
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * for the next 3 methods!
	 * @param first		class day
	 * @param second	class hour
	 * @param location	class location (classroom)
	 * @param clase		department&course code and title?
	 */
	public void addClass(int first, int second, String location){
		haveClass[first][second] = true;
		this.location[first][second] = location;
	}
	public void addClassDescription(int first, int second, String clase){
		haveClass[first][second] = true;
		this.clase[first][second] = clase;
	}
	public void removeClass(int first, int second, String location){
		haveClass[first][second] = false;
		this.location[first][second] = null;
	}

	// compare teacher's names according to the turkish alphabetical order, 
	// not super necessary but looks better when they're sorted
	/**
	 * @see Java.text.Collator
	 */
	@Override
	public int compare(Object i1, Object i2) {
		Locale trLocale = new Locale("tr-TR");
		Collator collator = Collator.getInstance(trLocale);
		/* strength is set to SECONDARY to distinguish between turkish letters such as
		 * ç, ğ, ş and their equivalents in the english alphabet c, g, s
		 */
		collator.setStrength(Collator.SECONDARY);
		String first = (((Instructor) i1).getName());
		String second = (((Instructor) i2).getName());
		return collator.compare(first, second);
	}
}

import java.text.Collator;
import java.util.ArrayList;
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
	
	private String name;
	/*following 2d arrays hold data for date/time
	 * there are 5 arrays in the first dimension to determine the day of the week
	 * and there are 8 arrays in the second dimension to determine the class hour belonging to that day
	 */
	private ArrayList<Course> courses;
	
	public Instructor(String name){
		this.name = name;
		this.courses = new ArrayList<Course>();
	}
	
	public String getName(){
		return name;
	}
	
	public void addCourse(Course course){
		courses.add(course);
	}
	
	public Course getCourse(int index){
		if(index < courses.size()){
			return courses.get(index);
		}
		return null;
	}

	// compare teacher's names according to the turkish alphabetical order, 
	// not super necessary but looks better when they're sorted
	/**
	 * @see Java.text.Collator
	 */
	@Override
	public int compare(Object i1, Object i2) {
		Locale trLocale = Locale.forLanguageTag("tr-TR");
		Collator collator = Collator.getInstance(trLocale);
		String first = (((Instructor) i1).getName());
		String second = (((Instructor) i2).getName());
		return collator.compare(first, second);
	}
	
	public String toString(){
		String list = "";
		for(int i = 0; i < courses.size(); i++){
			list += name + " " + courses.get(i).toString() + "\n";
		}
		return list;
	}
}


public class Course {
	String day;
	int dayInt;
	int time;
	String title;
	String deptCode;
	int courseCode;
	int section;
	int status;
	String location;
	
	public Course(String deptCode, int courseCode, int section, int dayInt, int time, int status, String location){
//		this.title = title;
		this.deptCode = deptCode;
		this.courseCode = courseCode;
		this.section = section;
		this.dayInt = dayInt;
		this.time = time;
		this.status = status;
		this.location = location;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}
	
	public String getStatusStr(){
		switch(status){
		case 0:
			return "Regular";
		case 1:
			return "Make-Up";
		case 2:
			return "Lab";
		}
		return "";
	}
	
	public String toString(){
		return deptCode+courseCode+"-"+section+" "+dayInt+" "+time+" "+getStatusStr()+" "+location;
	}

}

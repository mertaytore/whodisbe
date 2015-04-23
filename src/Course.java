
public class Course {
	private String day;
	private int dayInt;
	private int time;
	private String title;
	private String deptCode;
	private int courseCode;
	private int section;
	private int status;
	private String location;
	
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
	
	public String getDeptCode(){
		return deptCode;
	}
	
	public int getCourseCode(){
		return courseCode;
	}
	
	public int getSection(){
		return section;
	}
	
	public int getDayInt(){
		return dayInt;
	}
	
	public int getTime(){
		return time;
	}
	
	public int getStatus(){
		return status;
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

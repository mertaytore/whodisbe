import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.Collator;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

/**
 * @author      cagdas	boyinankara@gmail.com
 * @version     1.0                 
 * @since       2015-04-14
 * Read data about all the courses taught at Bilkent and parse the data and insert into a database
 */


/**
 * TODO: add insertion queries possibly write a class for that
 * @author dunkucoder
 *
 */

public class main {
	public static void main(String[] args) throws IOException {
		Locale trLocale = Locale.forLanguageTag("tr-TR");
		Trust trust = new Trust();
		trust.trustTheSiteGoddammit();
		ArrayList<String> courses = new ArrayList<String>();
		ArrayList<Instructor> instructors = new ArrayList<Instructor>();
		File file = new File("/Users/dunkucoder/Desktop/input.txt");
		File outFile = new File("/Users/dunkucoder/Desktop/output.txt");
		
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(scan.hasNextLine())
		{
			String curline = scan.nextLine();
			if( curline.length() < 6 && curline.length() > 1){
				courses.add(curline);
			}
		}
		
		ArrayList<String> matched = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		// read html code for the time tables of courses offered by all departments
		for(int i = 0; i < courses.size(); i++){
			String url = "https://stars.bilkent.edu.tr/homepage/print/plainOfferings.php?COURSE_CODE=" + 
					courses.get(i) + "&SEMESTER=20142";
			urls.add(url);
		}
			
			
		String first = "<td";
		String second = "</td>";
		for(String url : urls){
			getBetween(getUrlSource(url),first,second,matched);
		}
		
		
		addInstructors(matched, instructors);
		addCourses(matched, instructors);
		Collections.sort(instructors, new Instructor(""));
		
		BufferedWriter output = new BufferedWriter(new FileWriter(outFile));
		for(int i = 0; i < instructors.size(); i++)
		{
			String cur = instructors.get(i).toString();
			try {
	            output.write(cur);
//	            output.close();
	        } catch ( IOException e ) {
	            e.printStackTrace();
	        }
		}
		output.close();
		
		
		
		Collator collator = Collator.getInstance(trLocale);
		collator.setStrength(Collator.SECONDARY);
	}
	
	public static String getUrlSource(String url) throws IOException 
	{
        URL link = new URL(url);
        URLConnection yc = link.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }
	
	public static ArrayList<String> getBetween(String str, String first, String second, ArrayList<String> matches)
	{
		int count = 0;
		for(int i = 0; i + first.length() < str.length(); i++)
		{
			String subFirst = str.substring(i, i+first.length());
			if(subFirst.equals(first)){
				
				for(int j = i+first.length(); j + second.length() < str.length(); j++){
					String subSecond = str.substring(j, j+second.length());
					if(subSecond.equals(second)){
						String toAdd = str.substring(i+first.length(), j);
						int curLength = toAdd.length();
						for(int k = 0; k < curLength; k++)
						{
							if(toAdd.charAt(k) == '<'){
								for(int p = k; p < curLength; p++)
								{
									if(toAdd.charAt(p) == '>'){
										toAdd = toAdd.substring(0, k) + toAdd.substring(p, curLength);
										curLength = toAdd.length();
										break;
									}
									
								}
							}
						}
						toAdd.replace("<", "");
						toAdd.replace(">", "");
						count++;
						if(count > 17){
							int countMod = (count - 17) % 13;
							if(countMod == 1){
								matches.add(toAdd.substring(1));
							}
							else if(countMod == 2){
								matches.add(toAdd.substring(11));
							}
							else if(countMod == 3){
								matches.add(toAdd.substring(2, toAdd.length()-1));
							}
							else if(countMod == 6){
								matches.add(toAdd.substring(1));
							}
						}
						break;
					}
				}
			}
		}
		
		return matches;
	}
	
	public static Instructor getInstructor(String name, ArrayList<Instructor> instructors){
		Locale trLocale = Locale.forLanguageTag("tr-TR");
		Collator collator = Collator.getInstance(trLocale);
		boolean found = false;
		int first = 0;
		int second = instructors.size();
		int pivot = (first+second)/2;
		while(second > first){
			if(instructors.get(pivot).getName().equals(name)){
				return instructors.get(pivot);
			}
			else if(collator.compare(name,instructors.get(pivot).getName()) > 0){
				int cur = pivot;
				first = pivot;
				pivot = (first+second)/2;
				if(pivot == cur){
					return null;
				}
			}
			else{
				int cur = pivot;
				second = pivot;
				pivot = (first+second)/2;
				if(pivot == cur){
					return null;
				}
			}
		}
		return null;
	}
	
	public static void addCourses(ArrayList<String> matches, ArrayList<Instructor> instructors){
		for(int i = 0; i < matches.size(); i += 4){
			String course = matches.get(i);
			String title = matches.get(i+1);
			String instructor = matches.get(i+2);
			if(instructor.equals("taf")){
				i+=4;
			}
			else{
				
				String cur = instructor;
				boolean stillHas = cur.contains(">>");
				boolean wentIn = false;
				ArrayList<String> curs = new ArrayList<String>();
				
				while(stillHas){
					wentIn = true;
					int index = cur.indexOf('>');
					curs.add(cur.substring(0, index));
					cur = cur.substring(index+2, cur.length());
					stillHas = cur.contains(">>");
				}
				
				if(!wentIn){
					Instructor toAdd = getInstructor(instructor, instructors);
					if(toAdd != null){
						String schedule = matches.get(i+3);
//						System.out.println(schedule);
						String[] locs = getSchedule(schedule);
						
						for(int j = 0; j < locs.length; j++){
							if(locs.length > 1){
								Course curCourse = getCourse(course, locs[j]);
								if(curCourse != null){
									toAdd.addCourse(curCourse);
								}
							}

						}
					}
					
				}
				else{
					for(int j = 0; j < curs.size(); j++){
						Instructor toAdd = getInstructor(curs.get(j), instructors);
						if(toAdd != null){
							String schedule = matches.get(i+3);
//							System.out.println(schedule);
							String[] locs = getSchedule(schedule);
							
							for(int k = 0; k < locs.length; k++){
								if(locs.length > 1){
									Course curCourse = getCourse(course, locs[k]);
									if(curCourse != null){
										toAdd.addCourse(curCourse);
									}
								}

							}
						}
					}
				}
				
//				String schedule = matches.get(i+3);
//				String[] locs = getSchedule(schedule);
//				
//				for(int j = 0; j < locs.length; j++){
//					if(locs[j].length() > 0){
//						
//						Course curCourse = getCourse(course, locs[j]);
//					}
//					
//				}
			}	
		}
	}
	
	public static void addInstructors(ArrayList<String> matches, ArrayList<Instructor> instructors){
		
		for(int i = 0; i < matches.size(); i += 4){
			String course = matches.get(i);
			String title = matches.get(i+1);
			String instructor = matches.get(i+2);
			if(instructor.equals("taf")){
				i+=4;
			}
			else{
				
				String cur = instructor;
				boolean stillHas = cur.contains(">>");
				boolean wentIn = false;
				ArrayList<String> curs = new ArrayList<String>();
				
				while(stillHas){
					wentIn = true;
					int index = cur.indexOf('>');
					curs.add(cur.substring(0, index));
					cur = cur.substring(index+2, cur.length());
					stillHas = cur.contains(">>");
				}
				
				if(!instructorExists(instructor, instructors) && !wentIn){
					
					Instructor toAdd = new Instructor(instructor);
					String schedule = matches.get(i+3);
					String[] locs = getSchedule(schedule);
					
					for(int j = 0; j < locs.length; j++){
						if(locs.length > 0){
							Course curCourse = getCourse(course, locs[j]);
						}

					}
					
					instructors.add(toAdd);
					Collections.sort(instructors, new Instructor(""));
				}
				else if(wentIn){
					for(int j = 0; j < curs.size(); j++){
						if(!instructorExists(curs.get(j), instructors)){
							Instructor toAdd = new Instructor(curs.get(j));
							instructors.add(toAdd);
							Collections.sort(instructors, toAdd);
						}
					}
				}
			}	
		}
	}
	
	public static Course getCourse(String course, String line){
		final int REGULAR = 0;
		final int MAKE_UP = 1;
		final int LAB = 2;
		if(line.length() < 3){
			return null;
		}
		String[] res = course.split(" ");
		String deptCode = res[0];
		int courseCode = Integer.parseInt(res[1].substring(0,3));
		int section = Integer.parseInt(res[1].substring(4,res[1].length()));
		String statusStr = "";
		int status = -1;
		String day = getDay(line.substring(0,3));
		int dayInt = getDayInt(line.substring(0,3));
		int hoursInt = getHours(line.substring(4,15));
		String classroom = "";
		if(line.replace("[", "").length() != line.length()){
			if(16 < line.length()){
				classroom = line.substring(16,line.length()-3);
				statusStr = line.substring(line.length()-3, line.length());
			}
				
				
		}
		else{
			if(16 < line.length()){
				 classroom = line.substring(16, line.length());
			}
		}
		
		status = (getStatus(statusStr));
		
		return new Course(deptCode, courseCode, section, dayInt, hoursInt, status, classroom);
	}
	
	public static int getStatus(String str){
		switch(str){
		case "[L]":
			return 2;
		case "[S]":
			return 1;
		}
		return 0;
	}
		
	public static String getStatusStr(int status){
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
	
	public static String getDay(String str){
		switch(str){
		case "Mon":
			return "Monday";
		case "Tue":
			return "Tuesday";
		case "Wed":
			return "Wednesday";
		case "Thu":
			return "Thursday";
		case "Fri":
			return "Friday";
		}
		return "";
	}
	
	public static int getDayInt(String str){
		switch(str){
		case "Mon":
			return 0;
		case "Tue":
			return 1;
		case "Wed":
			return 2;
		case "Thu":
			return 3;
		case "Fri":
			return 4;
		}
		return -1;
	}
	
	public static int getHours(String str){
		int sum;
		int firstInt = Integer.parseInt(str.substring(0,2));
		int secondInt = Integer.parseInt(str.substring(6,8));
		firstInt *= 100;
		return secondInt + firstInt;
	}
	
	public static String[] getSchedule(String schedule){
		String[] list = schedule.split(">");
		return list;	
	}
	
	public static boolean instructorExists(String name, ArrayList<Instructor> instructors){
		Locale trLocale = Locale.forLanguageTag("tr-TR");
		Collator collator = Collator.getInstance(trLocale);
		collator.setStrength(Collator.SECONDARY);
		boolean found = false;
		int first = 0;
		int second = instructors.size();
		int pivot = (first+second)/2;
//		name = prepareToCompare(name);
		while(second > first){
			if(instructors.get(pivot).getName().equals(name)){
				return true;
			}
			else if(collator.compare(name,instructors.get(pivot).getName()) > 0){
//				System.out.println(name + " " + instructors.get(pivot).getName() + " " + pivot);
				int cur = pivot;
				first = pivot;
				pivot = (first+second)/2;
				if(pivot == cur){
//					System.out.println("asd");
					return false;
				}
			}
			else{
				int cur = pivot;
				second = pivot;
				pivot = (first+second)/2;
				if(pivot == cur){
					return false;
				}
			}
		}
		return false;
	}
}
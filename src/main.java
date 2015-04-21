import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
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


public class main {
	public static void main(String[] args) throws IOException {
		Locale trLocale = new Locale("tr-TR");
		Trust trust = new Trust();
		trust.trustTheSiteGoddammit();
		ArrayList<String> courses = new ArrayList<String>();
		ArrayList<Instructor> instructors = new ArrayList<Instructor>();
		File file = new File("/Users/dunkucoder/Desktop/input.txt");
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(scan.hasNextLine())
		{
			String curline = scan.nextLine();
//			System.out.println(curline);
			if( curline.length() < 6 && curline.length() > 1){
				courses.add(curline);
			}
		}
		
		ArrayList<String> matched = new ArrayList<String>();
		
//		for(int i = 0; i < courses.size(); i++){
			String url = "https://stars.bilkent.edu.tr/homepage/print/plainOfferings.php?COURSE_CODE=" + 
					"CS" + "&SEMESTER=20142";
//		}
			
			
		String first = "<td";
		String second = "</td>";
		
		getBetween(getUrlSource(url),first,second,matched);
		
		
		addInstructors(matched, instructors);
		Collections.sort(instructors, new Instructor(""));
		
		for(int i = 0; i < instructors.size(); i++)
		{
			String cur = instructors.get(i).getName();
//			System.out.println(cur);
		}
		
		System.out.println(instructors.size());
		
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
//									System.out.println("p: " + p);
									if(toAdd.charAt(p) == '>'){
//										System.out.println("something");
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
//								String cur = toAdd.substring(2, toAdd.length()-1);
//								boolean still = cur.contains("<<");
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
	
	public static void addInstructors(ArrayList<String> matches, ArrayList<Instructor> instructors){
		for(int i = 0; i < matches.size(); i += 4){
			String course = matches.get(i);
			String title = matches.get(i+1);
			String instructor = matches.get(i+2);
			String schedule = matches.get(i+3);
			System.out.println(instructor + " " + course);
			String[] locs = getSchedule(schedule);
			for(int j = 0; j < locs.length; j++){
//				System.out.println(locs[j]);
				if(locs[j].length() > 0)
					getCourse(locs[j]);
			}
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
				instructors.add(new Instructor(instructor));
				Collections.sort(instructors, new Instructor(""));
			}
			else if(wentIn){
				for(int j = 0; j < curs.size(); j++){
					if(!instructorExists(curs.get(j), instructors)){
						instructors.add(new Instructor(curs.get(j)));
						Collections.sort(instructors, new Instructor(""));
					}
				}
			}	
		}
	}
	
	public static Course getCourse(String line){
		final int REGULAR = 0;
		final int MAKE_UP = 1;
		final int LAB = 2;
		
		String statusStr = "";
		int status = -1;
		String day = getDay(line.substring(0,3));
		int dayInt = getDayInt(line.substring(0,3));
		System.out.println(day + " " + dayInt);
		int hoursInt = getHours(line.substring(4,15));
		System.out.println(hoursInt);
		String classroom = "";
		if(line.replace("[", "").length() != line.length()){
			if(16 < line.length()){
				classroom = line.substring(16,line.length()-3);
				statusStr = line.substring(line.length()-3, line.length());
				System.out.println(getStatusStr(getStatus(statusStr)));
			}
				
				
		}
		else{
			if(16 < line.length()){
				 line.substring(16, line.length());
				 System.out.println(getStatusStr(getStatus(statusStr)));
			}
		}
		System.out.println(classroom);
		
		return null;
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
		Locale trLocale = new Locale("tr-TR");
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
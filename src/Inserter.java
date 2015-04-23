import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

public class Inserter {
	Connection con = null;
    java.sql.Statement stmt = null;
	String url;
	String username;
	String password;
	String port;
	String creationQuery = "CREATE TABLE instructor(name VARCHAR(50) NOT NULL, day INTEGER, time INTEGER, deptCode VARCHAR(7),"
			+ " courseCode INTEGER, section INTEGER, location VARCHAR(20), status INTEGER) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	// short for insertion query! see below for reason why.
	String[] iq = {"INSERT INTO instructor VALUES('"
			, "',"
			, ","
			, ",'"
			, "',"
			,","
			,",'"
			,"',"
			,");"};
	public Inserter(String url, String username, String password) throws ClassNotFoundException, SQLException{
		this.url = url;
		this.username = username;
		this.password = password;
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url, username, password);
		
		// I only have a basic idea on how these classes work
		stmt = con.createStatement();
		java.sql.DatabaseMetaData md = con.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		
		// to delete tables that have foreign key conflicts, disable foreign key checks
		stmt.execute("SET FOREIGN_KEY_CHECKS = 0;"); 
		
		// while resultset still has results this will drop tables if there were any tables to begin with
		while (rs.next()) {
		    String curTable = rs.getString("TABLE_NAME");
		    if(curTable.equals("instructor")){
		    	stmt.execute("DROP TABLE instructor");
			}
		}
		
		stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
		
		stmt.execute(creationQuery);
	}
	
	public void insertInstructor(Instructor instructor) throws SQLException{
		for(int i = 0; i < instructor.numOfCourses(); i++){
			String query = iq[0] + instructor.getName() + iq[1] + instructor.getCourse(i).getDayInt() + iq[2] +
					instructor.getCourse(i).getTime() + iq[3] + instructor.getCourse(i).getDeptCode() + iq[4] +
					instructor.getCourse(i).getCourseCode() + iq[5] + instructor.getCourse(i).getSection() + iq[6] +
					instructor.getCourse(i).getLocation() + iq[7] + instructor.getCourse(i).getStatus() + iq[8];
			System.out.println(query);
			stmt.execute(query);
		}
	}
}

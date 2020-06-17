package gradesdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.JTable;

public class DBManagement {
	static Logger logger = Logger.getLogger(DBManagement.class);
	public DBManagement() {
		PropertyConfigurator.configure("log4j.properties");
	}
	
public static JTable getData(Connection conn, String[] names, String query) {
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			
			Vector<Object> colNames = new Vector<Object>();
			for(int i=0;i<names.length;i++) {
				colNames.add(names[i]);
			}
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		    while (result.next()) {
		        Vector<Object> vector = new Vector<Object>();
		        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
		            vector.add(result.getObject(columnIndex));
		        }
		        data.add(vector);
		    }	
			System.out.println("ALL records selected.");
			JTable table = new JTable(data,colNames);
			logger.trace("Retrieving data table from database.");
			return table;
		}catch(Exception e){
			logger.error(e);
			return null;
		}
	}
	public static String getValue(Connection conn, String query){
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();		
			if(result.next()) {
				logger.trace("Retrieving data from database.");
				return result.getString(1);
			}
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		return null;
	}
	public static String[] getList(Connection conn, String query){
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			int count =0;
			while(result.next()) {
				count++;
			}
			PreparedStatement statement2 = conn.prepareStatement(query);
			ResultSet result2 = statement2.executeQuery();
			int i = 0;
			String[] list = new String[count];
			while (result2.next()) {
				list[i] = result2.getString(1);
				i++;
			}
			logger.trace("Retrieving data list from database.");
			return list;
		}catch(Exception e){
			logger.error(e);
			return null;
		}
	}
	public static void postGrade(Connection conn,String login, List<String> list,String comment, String date) {
		try {
			String sid = "SELECT S_id FROM (SELECT S_id,CONCAT(Name,' ',Surname) student FROM Students having student='"+list.get(0)+"')tmp;";
			String cname = "SELECT Name FROM Courses where login='"+login+"';";
			PreparedStatement posted = conn.prepareStatement("INSERT INTO Grades VALUES ('"+getValue(conn,sid)+"','"+getValue(conn,cname)+"', '"+ list.get(1)+"', '"+list.get(2)+"', '"+comment+"', '"+date+"');");
			posted.executeUpdate();
			logger.trace("Database updated");
			logger.info("Grade inserted successfully.");
		}catch(Exception e){
			logger.error(e);	
		}
	}
	public static Connection Connect() {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://mysql.agh.edu.pl:3306/macbono1";
			String username = "macbono1";
			String password = "kvtvEQyDH1WmVMuf";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url,username, password);
			logger.info("Conncected to: "+url);
			return conn;
		} catch(Exception e){
			logger.error(e);
			return null;
		}
	}
	public static String checkLogin(Connection conn, String login, String password) {
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT login,password,type FROM Users WHERE login='"+login+"';");
			
			ResultSet result = statement.executeQuery();
			if(result.next()) {
			if(result!=null) {
				if(password.equals( result.getString("password"))) {
					logger.trace("Login successful.");
					return result.getString("type");
				}
			}
		}
		}catch(Exception e){
			logger.error(e);
			return null;
		}
		logger.trace("Unsuccessful login try. Invalid data.");
		return "bad";
	}
}

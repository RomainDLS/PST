package ESIEA.PST.Abracadabra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportToDb {
	public ImportToDb() {
		String url = "jdbc:mysql://localhost:3306/mydb";
		String user = "root";
		String passwd = "user";
		Connection cn = null;
		Statement st = null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver O.K.");
			cn = DriverManager.getConnection(url, user, passwd);
			st = cn.createStatement();
			System.out.println("Connection O.K.");
		} catch (Exception e){
			e.printStackTrace();
		}
		
		SaveMusic(st, "titre","album","artiste","genre",2007,"commentaire");
		AddSignature(st, "titre", "artiste", 299);
		
		try {
			cn.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void SaveMusic(Statement st, String title, String album, String artist, String type, int year, String comment){

		try{
			String sql = "INSERT INTO music_database VALUES (NULL, '"+ title +"','"+ artist +"','"+ year +"','"+ album +"','" + type + "',' "+ comment + "');";
			System.out.println(sql);
			st.executeUpdate(sql);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void AddSignature(Statement st, String title, String artist, int j){

		try{
			String sql;
			System.out.println("Adding values !\n");
			try{
				sql = "INSERT INTO signature VALUES (" + j + ")";
				System.out.println(sql);
				st.executeUpdate(sql);
			} catch (Exception e){
				System.out.println("Values :" + j + "\t already exist in the table : signature");
			}

				ResultSet rs= st.executeQuery("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
				rs.next();
			try{
				sql = "INSERT INTO signature_match VALUES (" + j + "," + rs.getString("idmusic_database") + ")";
				System.out.println(sql);
				st.executeUpdate(sql);
			} catch (Exception e){
				System.out.println("Values :" + j + "\t already exist in the table : signature");
			}
				
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}

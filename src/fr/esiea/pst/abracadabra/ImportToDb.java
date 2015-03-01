package fr.esiea.pst.abracadabra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ImportToDb {
	Statement st = null;
	
	public ImportToDb() {
		String url = "jdbc:mysql://sd-36718.dedibox.fr:3306/abracadabra";
		String user = "root";
		String passwd = "user"; //Passwrd à aller chercher par mail
		Connection cn = null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver O.K.");
			cn = DriverManager.getConnection(url, user, passwd);
			st = cn.createStatement();
			System.out.println("Connection O.K.");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void SaveMusic(String title, String album, String artist, String type, int year, String comment){
		
		title = title.replace("'", "\"");
		album = album.replace("'", "\"");
		artist = artist.replace("'", "\"");
		type = type.replace("'", "\"");
		comment = comment.replace("'", "\"");
		

		try{
			String sql = "INSERT INTO music_database VALUES (NULL, '"+ title +"','"+ artist +"','"+ year +"','"+ album +"','" + type + "',' "+ comment + "');";
			System.out.println(sql);
			st.executeUpdate(sql);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public int GetIdMusic(String title, String artist) {
		title = title.replace("'", "\"");
		artist = artist.replace("'", "\"");
		try{
			ResultSet rs= st.executeQuery("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
			rs.next();
			System.out.println("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
			return rs.getInt(1);
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public void AddSignature(int id, int j){
		String idmusic = "" + id;
		
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

			try{
				sql = "INSERT INTO signature_match VALUES (" + j + "," + idmusic + ")";
				System.out.println(sql);
				st.executeUpdate(sql);
			} catch (Exception e){
				System.out.println("Values :" + j + "\t already exist in the table : signature_match");
			}
				
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}

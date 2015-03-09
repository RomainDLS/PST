package fr.esiea.pst.abracadabra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

public class ImportToDb {
	Statement st = null;
	Connection cn = null;
	
	public ImportToDb() {
	//	String url = "jdbc:mysql://sd-36718.dedibox.fr:3306/abracadabra";
		String url = "jdbc:mysql://localhost:3306/mydb";
		String user = "root"; //abracadabra
		String passwd = "user"; //Passwrd à aller chercher par mail
				
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
		//	System.out.println(sql);
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
	//		System.out.println("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
			return rs.getInt(1);
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public void AddSignature(int id, int[] hash, int[] time){
		
		try{
			String sql = null;
	//		System.out.println("Adding values !\n");
			try{
				for(int i=0;i<hash.length;i++){
				sql = ("INSERT INTO signature VALUES (" + id + "," + hash[i] + "," + time[i] + ");\n");
			//	System.out.println(sql);
				st.addBatch(sql);
				}
				st.executeBatch();
			} catch (Exception e){
				System.out.println("Values :" + hash + "\t already exist in the table : signature");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public int musicMatched(int hash[]){
		
		ResultSet rs = null;
				
		String request = "SELECT distinct time from signature where hash = ";
		try{
			//for(int h : hash){
				rs= st.executeQuery(request + 1083837565);
				while(!rs.isAfterLast()){
					rs.next();
					
				}
			//}
				//		System.out.println("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
			return rs.getRow();
		} catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public static void main(String[] args) {
		ImportToDb Data = new ImportToDb();
		int hash[] = new int [16];
		System.out.println( "test ->" + Data.musicMatched(hash));
	}
}

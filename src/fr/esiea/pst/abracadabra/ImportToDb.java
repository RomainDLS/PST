package fr.esiea.pst.abracadabra;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.UnsupportedAudioFileException;

public class ImportToDb {
	PreparedStatement insertHashesStatement;
	Statement st = null;
	Connection cn = null;
	
	public ImportToDb() {
	//	String url = "jdbc:mysql://sd-36718.dedibox.fr:3306/abracadabra";
		String url = "jdbc:mysql://localhost:3306/mydb";
		String user = "root"; //abracadabra
		String passwd = "user"; //NE PAS COMMITER LE PASSWORD. FOURNI PAR MAIL PAR LE SUIVEUR.
				
		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver O.K.");
			cn = DriverManager.getConnection(url, user, passwd);
			st = cn.createStatement();
			insertHashesStatement = cn.prepareStatement("INSERT INTO signature VALUES (?,?,?);");
			System.out.println("Connection O.K.");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public int SaveMusic(String title, String album, String artist, String type, int year, String comment){
		
		title = title.replace("'", "\"");
		album = album.replace("'", "\"");
		artist = artist.replace("'", "\"");
		type = type.replace("'", "\"");
		comment = comment.replace("'", "\"");
		

		try{
			String sql = "INSERT INTO music_database VALUES (NULL, '"+ title +"','"+ artist +"','"+ year +"','"+ album +"','" + type + "',' "+ comment + "');";
		//	System.out.println(sql);
			st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
			
		} catch (Exception e){
			throw new RuntimeException(e);
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
	
	public String getMusicById(int id){
		if(id!=0)
		try{
			ResultSet rs= st.executeQuery("Select title,artiste FROM music_database WHERE idmusic_database = "+id);
			rs.next();
	//		System.out.println("Select * FROM music_database WHERE title = '"+title+"' AND artiste = '" + artist +"'");
			return rs.getString(1) + "\n   " + rs.getString(2);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		else
			return "Recherche ineffective...";
	}
	
	public void AddSignatures(int id, Hash hash){

		//String sql = null;
		
		try{
			long start = System.currentTimeMillis();
			byte[] bytes = new byte[]{ 23,47, 0x6F};
			
			
			for(Entry<Integer, Integer> h : hash.getHash().entrySet()){
				insertHashesStatement.setInt(1, id);
				insertHashesStatement.setInt(2, h.getValue()); //FIXME use setBytes to insert the sha1 hash directly
				insertHashesStatement.setInt(3, h.getKey());
				//sql = ("INSERT INTO signature VALUES (" + id + "," + h.getValue() + "," + h.getKey() + ");");
				//st.addBatch(sql);
				insertHashesStatement.addBatch();
			}
			insertHashesStatement.executeBatch();
			long end = System.currentTimeMillis();
			System.out.println("Hashs Added ! Time : " + (end - start));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public String musicMatched(Hash hash){
		
		ResultSet rs = null;
		HashMap<Integer, Integer> idList = new HashMap<>(); //{music_id, match_count}
				
		String request = "SELECT distinct music from signature where hash = ";
		
		try{
			for(Entry<Integer, Integer> h : hash.getHash().entrySet()){
				rs= st.executeQuery(request + h.getValue());
				while(rs.next()){
					int musicId = (Integer)rs.getInt("music");
				//	int time = (Integer)rs.getInt("time");
					Integer count = idList.get(musicId);
					if(count == null) {
						count = 0;
					}
					idList.put(musicId, count+1);
				}
			}
			
			System.out.println(idList);
		
			int MaxValue = 0;
			int id = 0;
			for(Entry<Integer, Integer> list : idList.entrySet()){
				if(list.getValue() > MaxValue){
					MaxValue = list.getValue();
					id = list.getKey();
				}
			}
			
			return getMusicById(id);
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String Recognize(String fileName) throws UnsupportedAudioFileException, IOException{
		File file;
		ImportToDb Import = new ImportToDb();
		String[] name = fileName.split("\\."); 
		long t0 = System.currentTimeMillis();
		long t1;
		if(name[name.length-1].equals("mp3")){
		  file = Audio.convertMP3toWAV(new File(fileName));
		  t1 = System.currentTimeMillis();
		  System.out.println("Conversion to wav:" + (double)(t1-t0)/1000 + "s");
		}
		else
		  file = new File(fileName);
		t1 = System.currentTimeMillis();
		FftProcessor fftProcessor = new FftProcessor();
		Complex[][] fftSlices = fftProcessor.fft(file);
		long t2 = System.currentTimeMillis();
		System.out.println("FFT: " + (double)(t2-t1)/1000 + "s");
		Hash hashes = fftProcessor.hash(file, fftSlices);
		long t3 = System.currentTimeMillis();
		System.out.println("Hashing: " + (double)(t3-t2)/1000 + "s");
		String matched = Import.musicMatched(hashes);
		System.out.println(matched);
		long t4 = System.currentTimeMillis();
		System.out.println("Time for identification : " + (double)(t4-t3) /1000 + "s");
		
		return matched;
	}
	
}

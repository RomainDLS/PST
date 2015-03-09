package fr.esiea.pst.abracadabra;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

	public static void main(String[] args) {
		
		ImportToDb Import = new ImportToDb();
		
		Import.SaveMusic("If 6'was\\9","ELdl","JH","Rock",1967,"NULL");
		int id = Import.GetIdMusic("If 6'was\\9", "JH");
	//	Import.AddSignature(id, 1, 2);
	/*	
//		File audiofile = new File("01.mp3");
		File audiofile = new File("02.wav");
		try {
		//	new FftProcessor().fft(Audio.convertMP3toWAV(audiofile));
			new FftProcessor().fft(audiofile);
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}

package fr.esiea.pst.abracadabra;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

	public static void main(String[] args) {
		
/*		ImportToDb Import = new ImportToDb();
		
		Import.SaveMusic("If 6'was\\9","ELdl","JH","Rock",1967,"NULL");
		int id = Import.GetIdMusic("If 6'was\\9", "JH");
		for(int i = 6; i<78;i++)
			Import.AddSignature(id, i);*/
		
		File audiofile = new File("échantillon.wav");
		try {
			new FftProcessor().fft(audiofile);
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

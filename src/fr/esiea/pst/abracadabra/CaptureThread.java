package fr.esiea.pst.abracadabra;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class CaptureThread extends Thread {
	
	private File audioFile;
  private TargetDataLine targetDataLine;

	public CaptureThread(File audioFile) {
		this.audioFile = audioFile;
	}
	
  @Override
  public void run() {
	  
  	AudioFormat audioFormat = Audio.WAVFormat();	
    System.out.println(audioFormat);  
  	DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat); 
  	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
  
  	try {
        targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);	
        targetDataLine.open(audioFormat);
        targetDataLine.start();
  	}
  	catch(LineUnavailableException e) {
      e.printStackTrace();
  	}
    
    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
      
    try {
      AudioSystem.write(audioInputStream,fileType,audioFile);
    } catch (IOException e) {
      halt();
      e.printStackTrace();
	  }
  }
  
  public void halt() {
    if(targetDataLine != null) {
  	  targetDataLine.stop();
  	  targetDataLine.close();
    }
  }
}
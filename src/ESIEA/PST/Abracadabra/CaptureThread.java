package ESIEA.PST.Abracadabra;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;


public class CaptureThread extends Thread{
	
	private Audio audio;
	private File audioFile;

	public CaptureThread(Audio audio, File audioFile) {
		this.audio = audio;
		this.audioFile = audioFile;
	}
	
  public void run(){
	  
	audio.audioFormat = audio.WAVFormat();	
    System.out.println(audio.audioFormat);  
	DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audio.audioFormat); 
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
   
    try{
      audio.targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);	
      audio.targetDataLine.open(audio.audioFormat);
      audio.targetDataLine.start();
    }catch (Exception e){
      e.printStackTrace();
    }
    
    audio.audioInputStream = new AudioInputStream(audio.targetDataLine);
    
    try {
		AudioSystem.write(audio.audioInputStream,fileType,audioFile);
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
  
  public void halt() {
	  audio.targetDataLine.stop();
	  audio.targetDataLine.close();
  }
}
package fr.esiea.pst.abracadabra;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;


/*
/***** Fonctions utiles pour la lecture d'autre fichiers ******/
/*
public class Audio extends Thread {

	AudioInputStream audioInputStream = null;
	SourceDataLine line;
	

public static long bytesToLongSignedLSB(byte[] frameBuffer, int start, int len) {


    long l = 0;

    if(frameBuffer.length > 0) {

      int last = start+len-1;
      l = frameBuffer[last] << (8 * last); //last byte remains signed
      for(int r = len-2; r > 0; r--) {
    	  
        l += (frameBuffer[start + r] & 0xffL) << (8 * r);
      }

    }

    return l;


  }
	

/**
   * LSB / Little endian case (WAV/RIFF)
   */

/*
  public static long bytesToLongUnsignedLSB(byte[] frameBuffer, int start, int len) {


    long l = 0;
    for(int r = len-1; r >= 0; r--) {
      l += (frameBuffer[start + r] & 0xffL) << (8 * r);
    }
    return l;


  }
	

public static AudioFormat toSignedMonoPCM(AudioFormat sourceFormat) {


    return new AudioFormat(Encoding.PCM_SIGNED, //tells whether samples are signed, forcing to PCM SIGNED for decoding (like most 16b PCM wavs, whereas 8bit PCM wavs are unsigned, and there can also be Float or µLaw wav's)

                           sourceFormat.getSampleRate(),
                           16, //bits per sample
                           1, //mono
                           2, //frame size (16/8)

                           sourceFormat.getSampleRate(), //frame rate == sample rate in pcm
                           false, //little endian in pcm
                           sourceFormat.properties());
  }


  /**
   * Useful when only one channel has to be analyzed

  public static AudioInputStream getMonoInputStream(AudioInputStream ais) {

    AudioFormat targetFormat = AudioUtils.toSignedMonoPCM(ais.getFormat());
    return AudioSystem.getAudioInputStream(targetFormat, ais); // will return ais itself if the the in/out formats are the same


  }
   */

//Version lecture d'un enregistrement audio
// le programme enregistre dans un fichier wave un son, grâce à une interface graphique.
// On click sur le boutton "start" pour commencer la capture et "stop" pour l'arrêter.
// Une fois que le boutton "stop" à été actionné, le programme enregistre le fichier audio dans tableau (ici Byte)
//On utilisera enfin Byte pour toutes sorte de transformation notamment grâces aux fonctions ci-dessus.



public class Audio extends JFrame{
	private static final long serialVersionUID = 1L;
	AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;

  final JButton captureBtn = new JButton("Capture");
  final JButton stopBtn = new JButton("Stop");

  public static void main( String args[]){
    new Audio();
    
  }

  public Audio(){
    captureBtn.setEnabled(true);
    stopBtn.setEnabled(false);

    captureBtn.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          captureBtn.setEnabled(false);
          stopBtn.setEnabled(true);
          captureAudio();
        }
      }
    );

    stopBtn.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          captureBtn.setEnabled(true);
          stopBtn.setEnabled(false);
          targetDataLine.stop();
          targetDataLine.close();
        }
      }
    );
    
    getContentPane().add(captureBtn);
    getContentPane().add(stopBtn);

    getContentPane().setLayout(new FlowLayout());
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(300,120);
    setVisible(true);
  }
  
  private void captureAudio(){
    try{
      audioFormat = getAudioFormat();	
      System.out.println(audioFormat);
      DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
      targetDataLine = (TargetDataLine)
      AudioSystem.getLine(dataLineInfo);

      new CaptureThread().start();
    }catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  private AudioFormat getAudioFormat(){
    float sampleRate = 8000.0F;
    int sampleSizeInBits = 16;
    int channels = 1;  
    boolean signed = true;    
    boolean bigEndian = false;
   
    return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
  }
  
class CaptureThread extends Thread{
	
	SourceDataLine line;
	
	
  public void run(){
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    File audioFile = new File("échantillon.wav");
   
    try{
      targetDataLine.open(audioFormat);
      targetDataLine.start();
      AudioSystem.write(new AudioInputStream(targetDataLine),fileType,audioFile);
      audioInputStream = AudioSystem.getAudioInputStream(audioFile);
      System.out.println(audioInputStream);
    }catch (Exception e){
      e.printStackTrace();
    }
    

	AudioFormat audioFormat = audioInputStream.getFormat();
	DataLine.Info info = new DataLine.Info(SourceDataLine.class,
			audioFormat);
    
    try {
		line = (SourceDataLine) AudioSystem.getLine(info);

	} catch (LineUnavailableException e) {
		e.printStackTrace();
		return;
	}

	try {
		line.open(audioFormat);
	} catch (LineUnavailableException e) {
		e.printStackTrace();
		return;
	}

	line.start();
    
    try {
		byte bytes[] = new byte[1024];
		int bytesRead = 0;
		int i = 0;

		while ((bytesRead = audioInputStream.read(bytes, 0, bytes.length)) != -1) {
			line.write(bytes, 0, bytesRead);
			System.out.println("Byte =" + bytes[i]);

		}
	} catch (IOException io) {
		io.printStackTrace();
		return;
	}
    
  }
}

}

 // Version lecture du fichier
  
  /*
	public void run() {
		File fichier = new File("échantillon.wav");
		try {
			AudioFileFormat format = AudioSystem.getAudioFileFormat(fichier);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			audioInputStream = AudioSystem.getAudioInputStream(fichier);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		AudioFormat audioFormat = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);

		try {
			line = (SourceDataLine) AudioSystem.getLine(info);

		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}

		try {
			line.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}

		line.start();

		try {
			byte bytes[] = new byte[1024];
			int bytesRead = 0;
			int i = 0;

			while ((bytesRead = audioInputStream.read(bytes, 0, bytes.length)) != -1) {
				line.write(bytes, 0, bytesRead);
				System.out.println("Byte =" + bytes[i]);

			}
		} catch (IOException io) {
			io.printStackTrace();
			return;
		}

	}
}*/
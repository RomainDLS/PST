package ESIEA.PST.Abracadabra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;
import org.jtransforms.fft.*;

public class Window extends JFrame{
	private static final long serialVersionUID = 1L;
    
  final JButton captureBtn = new JButton("Capture");
  final JButton stopBtn = new JButton("Stop");

  Audio audio;
  File audioFile;
  float data[][];
  private CaptureThread ct;
  
  public static void main( String args[]){
    new Window();
    
  }

  public Window(){
	audio = new Audio();
	audioFile = new File("échantillon.wav");
	ct = new CaptureThread(audio, audioFile);
	//File mp3File = new File("Klingande.mp3");
	//audio.convertMP3toWAV(mp3File);
	
    captureBtn.setEnabled(true);
    stopBtn.setEnabled(false);

    captureBtn.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
          captureBtn.setEnabled(false);
          stopBtn.setEnabled(true);
          try{
              ct.start();
            }catch (Exception ex) {
              ex.printStackTrace();
              System.exit(0);
            }
        }
      }
    );

    stopBtn.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
        	captureBtn.setEnabled(true);
        	stopBtn.setEnabled(false);
        	ct.halt();
        	try{
        		fft(audioFile);
        	}catch(Exception ex1){
        		ex1.printStackTrace();
        	}
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
   


	public void fft(File audioFile) {
		 byte bytes[] = new byte[4096];
	
		 try {
			audio.audioInputStream = AudioSystem.getAudioInputStream(audioFile);
			long dataLength = audio.audioInputStream.getFrameLength() * audio.audioFormat.getFrameSize();
			System.out.println(dataLength);
			
			int nbView = (int) dataLength/bytes.length;
			System.out.println(nbView);
			
			data = new float [nbView][4096*2];
			FloatFFT_1D fft = new FloatFFT_1D(bytes.length); 
			
			FileWriter fw1 = new FileWriter("data.txt");
			FileWriter fw2 = new FileWriter("fft.txt");	
			
			for(int i = 0; i < nbView; i++){
				int read = readWindow(bytes);

				for(int j = 0; j < read; j++){
					data[i][j] = bytes[j];
					fw1.write(bytes[j]+ "\n");
				}
				fft.complexForward(data[i]);
				System.out.println(fft);
				
				for(int j=0; j < data[i].length; j++){
					fw2.write(data[i][j]+ "   ");
					j++;
					fw2.write(data[i][j]+ "\n");
				}
			}
		 	} catch (UnsupportedAudioFileException | IOException e) {
		 		e.printStackTrace();
		 	}
		 //Analyseur analyseur = new Analyseur(data);
	}

	private int readWindow(byte[] bytes) throws IOException {
		int read = 0;
		while(read < bytes.length) {
			int r = audio.audioInputStream.read(bytes,read,bytes.length-read);
			if(r != -1) {
				read += r;
			}
			else {
				break;
			}
		}
		return read;
	}
}

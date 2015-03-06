package fr.esiea.pst.abracadabra;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jtransforms.fft.DoubleFFT_1D;

public class FftProcessor {

  public void fft(File audioFile) throws UnsupportedAudioFileException, IOException {

    try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
      AudioFormat audioFormat = audioInputStream.getFormat();
      long dataLength = audioInputStream.getFrameLength() * audioFormat.getSampleSizeInBits() / 8;
      System.out.println("dataLength : "+dataLength);

      byte bytes[] = new byte[4096];
      int nbView = (int) dataLength / bytes.length;
  
      System.out.println("nbView : " + nbView);
      System.out.println("frequences Ranges \t 20-250 Hz\t\t250-2000 Hz\t\t2000-6000 Hz\t\t6000-22050");
      
      double data[][] = new double[nbView][];
      DoubleFFT_1D fft = new DoubleFFT_1D(bytes.length); //CAUTION, only works when frame size = 1 byte
  
      try (BufferedInputStream bis = new BufferedInputStream(audioInputStream);
           BufferedWriter fw1 = new BufferedWriter(new FileWriter("data.txt"));
           BufferedWriter fw2 = new BufferedWriter(new FileWriter("fft.txt"));
           BufferedWriter fw3 = new BufferedWriter(new FileWriter("Magnitudes.txt"))) {
        
        for (int i = 0; i < nbView; i++) {
          int read = readSlidingWindow(bis, bytes);
  
          double[] block = convertToDoubles(fw1, bytes, read);
          data[i] = block; 
          
          fft.realForward(block);
      //    System.out.println(fft);
  
          double[] magnitudes = computeBlockMagnitudes(fw2, block);
          computeHiScores(fw3, magnitudes);
        }
      }
    }
    //autoclose
  }

  protected double[] convertToDoubles(BufferedWriter fw, byte[] bytes, int read) throws IOException {
    double[] block = new double[bytes.length];
    for (int j = 0, k=0; j < read; j++) {
      double value = bytes[j] & 0xFFL; //CAUTION, only works with frames made of unsigned bytes
      block[k] = value;
      fw.write(Double.toString(value));
      fw.newLine();
    }
    return block;
  }

  protected double[] computeBlockMagnitudes(BufferedWriter fw, double[] block) throws IOException {
    double magnitudes[] = new double[block.length/2];
    for (int j = 0,k=0; j < block.length/2; k++, j++) {
      fw.append(Double.toString(block[j])).append("\t\t");
      j++;
      fw.append(Double.toString(block[j]));
      if(k<magnitudes.length){
    	  magnitudes[k] = Math.sqrt(Math.pow(block[j-1],2)+Math.pow(block[j],2));
    	  fw.write("\t\t" + k*44100/4096 + " Hz\t\t Magn :" + magnitudes[k]);
      }
      fw.newLine();
    }
    return magnitudes;
  }

  protected void computeHiScores(BufferedWriter fw, double[] Magnitudes) throws IOException {
    fw.write("20-250 Hz\t\t250-2000 Hz\t\t2000-6000 Hz\t\t6000-22050\n");

    int[] Frequences = new int[4];
    Frequences[0] = GetMaxFreq(Magnitudes, 2, 24);
    Frequences[1] = GetMaxFreq(Magnitudes, 25, 187);
    Frequences[2] = GetMaxFreq(Magnitudes, 188, 558);
    Frequences[3] = GetMaxFreq(Magnitudes, 559, 2048);
    fw.write(GetMaxFreq(Magnitudes, 2, 24) + "   \t");
    fw.write(GetMaxFreq(Magnitudes, 25, 187) + "  \t");
    fw.write(GetMaxFreq(Magnitudes, 188, 558) + "  \t");
    fw.write(GetMaxFreq(Magnitudes, 559, 2048) + "\t");
    fw.append("" + getHash(Frequences));
    fw.newLine();
  }

  private int readWindow(InputStream inputStream, byte[] bytes) throws IOException {
    int read = 0;
    while (read < bytes.length) {
      int r = inputStream.read(bytes, read, bytes.length - read);
      if (r != -1) {
        read += r;
      }
      else {
        break;
      }
    }
    return read;
  }
  
  private int readSlidingWindow(InputStream inputStream, byte[] bytes) throws IOException {
	    if(inputStream.markSupported()) {
	      int skipValue = bytes.length/2; //50%
	      inputStream.mark(skipValue);
	      int read = readWindow(inputStream, bytes);
	      inputStream.reset();
	      inputStream.skip(skipValue);
	      return read;
	    }
	    else {
	      return readWindow(inputStream, bytes);
	    }
	  }
  
  private int GetMaxFreq(double[] magnitudes, int RangeLow, int RangeHigh){
	  int Freq = 0;
	  int Magnitude = 0;
	  
	  for(int i=RangeLow; i<RangeHigh; i++){
		  if(magnitudes[i]>Magnitude){
			Magnitude = (int) magnitudes[i];
		  	Freq = i*44100/4096;
		  }
	  }
	  
	  return Freq;
  }
  
  private float GetMaxMagn(double[] magnitudes, int RangeLow, int RangeHigh){
	  int Magnitude = 0;
	  
	  for(int i=RangeLow; i<RangeHigh; i++){
		  if(magnitudes[i]>Magnitude){
			Magnitude = (int) magnitudes[i];
		  }
	  }
	  
	  return Magnitude;
  }
  
  
  private int getHash(int[] Frequences){
	  return (int)Frequences[0]/3 + (int)100*Frequences[1]/3 + (int)100000*Frequences[2]/3;
  }
  
  public static void main(String... args) throws UnsupportedAudioFileException, IOException {
    new FftProcessor().fft(new File("D:/java/workspace/ESIEA/PST/échantillon.wav"));
  }
}

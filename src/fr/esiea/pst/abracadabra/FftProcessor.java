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

  private static final int WINDOW_SIZE = 4096;

  public void fft(File audioFile) throws UnsupportedAudioFileException, IOException {

    try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
      AudioFormat audioFormat = audioInputStream.getFormat();
      int bytesPerFrame = audioFormat.getSampleSizeInBits() / 8;
      
      //half a second to skip due to some noise from the line opening on some PC's
      long toSkip = (long)(bytesPerFrame * audioFormat.getFrameRate()) / 2;
      audioInputStream.skip(toSkip);
      
      long dataLength = audioInputStream.getFrameLength() * audioFormat.getSampleSizeInBits() / 8 - toSkip;
      System.out.println("dataLength : "+dataLength);

      byte bytes[] = new byte[WINDOW_SIZE];
      int nbView = (int) dataLength / WINDOW_SIZE; //FIXME recompute depending on window overlapping
  
      System.out.println("nbView : " + nbView);
      System.out.println("frequences Ranges \t 20-250 Hz\t\t250-2000 Hz\t\t2000-6000 Hz\t\t6000-22050");
      
      double data[][] = new double[nbView][];
      DoubleFFT_1D fft = new DoubleFFT_1D(WINDOW_SIZE); //CAUTION, only works when frame size = 1 byte
  
      try (BufferedInputStream bis = new BufferedInputStream(audioInputStream);
           BufferedWriter fw1 = new BufferedWriter(new FileWriter("data.txt"));
           BufferedWriter fw2 = new BufferedWriter(new FileWriter("fft.txt"));
           BufferedWriter fw3 = new BufferedWriter(new FileWriter("Magnitudes.txt"))) {

        fw3.write("20-250 Hz\t\t250-2000 Hz\t\t2000-6000 Hz\t\t6000-22050\n");

        for (int i = 0; i < nbView; i++) {
          int read = readSlidingWindow(bis, bytes, 0f); //TODO experiment with overlapping (maybe 50% or 30%)
  
          double[] block = convertToDoubles(fw1, bytes, read);
          data[i] = block; 
          
          fft.realForward(block);
      //    System.out.println(fft);
  
          double[] magnitudes = computeBlockMagnitudes(fw2, block);
          HighScore[] highScores = computeHiScores(fw3, magnitudes);
          for (HighScore hi : highScores) {
            fw3.write(hi + "   \t");
          }
          //fw3.append("" + getHash(highScores)); //TODO
          fw3.newLine();
        }
      }
    }
    //autoclose
  }

  protected double[] convertToDoubles(BufferedWriter fw, byte[] bytes, int read) throws IOException {
    double[] block = new double[bytes.length];
    for (int j = 0, k=0; j < read; j++, k++) {
      double value = bytes[j] & 0xFFL; //CAUTION, only works with frames made of unsigned bytes
      block[k] = value;
      fw.write(Double.toString(value));
      fw.newLine();
    }
    return block;
  }

  protected double[] computeBlockMagnitudes(BufferedWriter fw, double[] block) throws IOException {
    int magSize = block.length/2;
    double magnitudes[] = new double[magSize];
    for (int j = 0, k = 0; k < magSize; k++) {
      Complex c = new Complex(block[j++], block[j++]);
      fw.append(c.toString());
  	  double abs = c.abs();
  	  magnitudes[k] = abs;
  	  fw.write("\t\t" + k*44100/WINDOW_SIZE + " Hz\t\t Magn :" + abs);
      fw.newLine();
    }
    return magnitudes;
  }

  protected HighScore[] computeHiScores(BufferedWriter fw, double[] magnitudes) {
    HighScore[] highScores = new HighScore[4];
    highScores[0] = getMaxFreq(magnitudes, 2, 24);
    highScores[1] = getMaxFreq(magnitudes, 25, 187);
    highScores[2] = getMaxFreq(magnitudes, 188, 558);
    highScores[3] = getMaxFreq(magnitudes, 559, 2048);
    return highScores;
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
  
  private int readSlidingWindow(InputStream inputStream, byte[] bytes, float overflowRate) throws IOException {
	    if(overflowRate != 0 && inputStream.markSupported()) {
	      int skipValue = bytes.length - (int)(overflowRate * bytes.length);
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

  private HighScore getMaxFreq(double[] magnitudes, int rangeLow, int rangeHigh){
	  HighScore HS = new HighScore();
	  
	  for(int i = rangeLow; i < rangeHigh; i++) {
		  if(magnitudes[i] > HS.getMagn()) {
			  HS.setMagn(magnitudes[i]);
			  HS.setFreq(i*44100/WINDOW_SIZE);
		  }
	  }
	  
	  return HS;
  }
  
  public static void main(String... args) throws UnsupportedAudioFileException, IOException {
    //new FftProcessor().fft(new File("D:/java/workspace/ESIEA/PST/échantillon.wav"));
    new FftProcessor().fft(new File("G:/échantillon.wav"));
  }
}

package fr.esiea.pst.abracadabra;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jtransforms.fft.DoubleFFT_1D;

public class FftProcessor {

  private static final int WINDOW_SIZE = 4096;

  private MessageDigest hash;

  public FftProcessor() {
	 try {
		hash = MessageDigest.getInstance("SHA-1");
	} catch (NoSuchAlgorithmException e) {
		throw new RuntimeException(e);
	}
  }
  
  public Hash fft(File audioFile) throws UnsupportedAudioFileException, IOException {
	  Hash hashList = new Hash();
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

      DoubleFFT_1D fft = new DoubleFFT_1D(WINDOW_SIZE); //CAUTION, only works when frame size = 1 byte
        
      try (BufferedInputStream bis = new BufferedInputStream(audioInputStream);
    	BufferedWriter fw3 = new BufferedWriter(new FileWriter("Magnitudes"+audioFile.getName()+".txt"))) {  	  
	        for (int i = 0; i < nbView*2; i++) {
	          int read = readSlidingWindow(bis, bytes, 0.5f); //TODO experiment with overlapping (maybe 50% or 30%)
	          double[] block = convertToDoubles(bytes, read);
	          hanning(block, 0, block.length);
	          fft.realForward(block);
	  
	          double[] magnitudes = computeBlockMagnitudes(block);
	          HighScore[] highScores = computeHiScores(magnitudes);
	          HighScore.removeNegligible(highScores);
	          hash.reset();
	          for (HighScore hi : highScores) {
	        	fw3.write(hi.getFreq()+"("+hi.getMagn() + ")\t");
	            hash.update(intToBytes(hi.getFreq()));
	          }
	          fw3.newLine();
	  		  
	          hashList.setHash(i, fromByteArray(hash.digest()));	    	  
	        }
	        
      }
    }
    //autoclose
    return hashList;
  }
  
  public void hanning(double[] audioBuffer, int start, int len) {
	    int N = len - 1;
	    for (int i = start, n = 0; i < start + len; i++, n++) {
	      audioBuffer[i] = audioBuffer[i] * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * n / N));
	    }
  }
  
  //can't compute an nto out of a 20byte sha-1
  @Deprecated
  int fromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
  

  protected double[] convertToDoubles(byte[] bytes, int read) throws IOException {
    double[] block = new double[bytes.length];
    for (int j = 0, k=0; j < read; j++, k++) {
      double value = bytes[j] & 0xFFL; //CAUTION, only works with frames made of unsigned bytes
      block[k] = value;
    }
    return block;
  }


  protected double[] computeBlockMagnitudes(double[] block) throws IOException {
    int magSize = block.length/2;
    double magnitudes[] = new double[magSize];
    for (int j = 0, k = 0; k < magSize; k++) {
      Complex c = new Complex(block[j++], block[j++]);
  	  double abs = c.abs();
  	  magnitudes[k] = abs;
    }
    return magnitudes;
  }


  protected HighScore[] computeHiScores(double[] magnitudes) {
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
  

  private static final byte[] intToBytes(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
  }
  

  public static void main(String... args) throws UnsupportedAudioFileException, IOException {
	  ImportToDb Import = new ImportToDb();
    //new FftProcessor().fft(new File("D:/java/workspace/ESIEA/PST/échantillon.wav"));
//    Import.SaveMusic("Every Struggle","Life After Death","Notorious BIG","Rap",1970,"NULL");	  
//	  ImportToDb Import = new ImportToDb();
//	  Import.SaveMusic("Every Day Struggle","Live After Death","Notorious B.I.G","Rap",0,"NULL");
//	  Import.SaveMusic("Achy Breaky Heart","Some Gave All","Billy Ray Cyrus","Rock",1970,"NULL");
			  
/*    Hash hash = new FftProcessor().fft(Audio.convertMP3toWAV(new File("C:/Users/Romain/PST-Abracadabra/Achy Breaky Heart.mp3")));
    Import.AddSignatures(Import.GetIdMusic("Achy Breaky Heart", "Billy Ray Cyrus"), hash);
    hash = new FftProcessor().fft(Audio.convertMP3toWAV(new File("C:/Users/Romain/PST-Abracadabra/EveryDayStruggle.mp3")));
    Import.AddSignatures(Import.GetIdMusic("Every Struggle", "Notorious BIG"), hash);
*/    
	  Hash hash = new FftProcessor().fft(Audio.convertMP3toWAV(new File("C:/Users/Romain/PST-Abracadabra/AchyBreakyHeart.mp3")));
    System.out.println(Import.musicMatched(hash));
  }
}
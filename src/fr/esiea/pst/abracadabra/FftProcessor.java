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
import java.sql.Savepoint;

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

  public Complex[][] fft(File audioFile) throws UnsupportedAudioFileException, IOException {
    return fft(audioFile, 500, -1); //by default half a second to skip due to some noise from the line opening on some PC's
  }

  public Complex[][] fft(File audioFile, long msToSkip, long msToKeep) throws UnsupportedAudioFileException, IOException {
    AudioFormat monoFormat = Audio.WAVFormat(); //44100/8/1
    try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(monoFormat, AudioSystem.getAudioInputStream(audioFile))) {
      AudioFormat audioFormat = audioInputStream.getFormat();

      int bytesPerFrame = audioFormat.getSampleSizeInBits() / 8;
      long oneSecondBytes = (long)(bytesPerFrame * audioFormat.getFrameRate());
      long toSkip = oneSecondBytes * msToSkip / 1000L; 
      audioInputStream.skip(toSkip); 
      
      long dataLength;
   //   System.out.println("msToKeep : "+ msToKeep + " - audioFormat : " + audioFormat + " - audioInputStream.getFrameLength() : " + audioInputStream.getFrameLength());
      if(msToKeep < 0) {
        dataLength = audioInputStream.getFrameLength() * audioFormat.getSampleSizeInBits() / 8 - toSkip;
      }
      else {
        long frames = (long)((msToKeep * audioFormat.getSampleRate()) / 1000);
        dataLength = frames * audioFormat.getSampleSizeInBits() / 8;
      }
      System.out.println("dataLength : "+dataLength);

      byte bytes[] = new byte[WINDOW_SIZE];
      float overlapFactor = 0.5f; //experimenting with overlapping: 50%
      int nbView = (int)(((float) dataLength / WINDOW_SIZE) / overlapFactor); //recompute windows count depending on window overlapping
      Complex[][] fftSlices = new Complex[nbView][];
  
      System.out.println("nbView : " + nbView);

      DoubleFFT_1D fft = new DoubleFFT_1D(WINDOW_SIZE); //CAUTION, only works when frame size = 1 byte
        
      try (BufferedInputStream bis = new BufferedInputStream(audioInputStream)) {  	  
	        for (int i = 0; i < nbView; i++) {
	          int read = readSlidingWindow(bis, bytes, overlapFactor);
	          double[] block = convertToDoubles(bytes, read);
	          hanning(block, 0, block.length);
	          fft.realForward(block);
	          fftSlices[i] = toComplex(block);
	        }
	        return fftSlices;
      }
    }
  }

  public Hash fftAndHash(File audioFile) throws IOException, UnsupportedAudioFileException {
    Complex[][] fftSlices = fft(audioFile);
    return hash(audioFile, fftSlices);
  }

  public Hash hash(File audioFile, Complex[][] fftSlices) throws IOException {
    Hash hashList = new Hash();
    
    try(BufferedWriter fw3 = new BufferedWriter(new FileWriter("Magnitudes"+audioFile.getName()+".txt"));
    		BufferedWriter fw2 = new BufferedWriter(new FileWriter("Data"+audioFile.getName()+".txt"))) {
      int windows = fftSlices.length;
      for(int i = 0; i < windows; i++) {
        Complex[] fftSlice = fftSlices[i];
        double[] magnitudes = computeBlockMagnitudes(fw2, fftSlice);
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
  

  protected double[] convertToDoubles(byte[] bytes, int read) {
    double[] block = new double[bytes.length];
    for (int j = 0, k=0; j < read; j++, k++) {
      double value = bytes[j] & 0xFFL; //CAUTION, only works with frames made of unsigned bytes
      block[k] = value;
    }
    return block;
  }

  
  protected Complex[] toComplex(double[] fftBlock) {
    int size = fftBlock.length/2;
    Complex result[] = new Complex[size];
    for (int j = 0, k = 0; k < size; k++) {
      result[k] = new Complex(fftBlock[j++], fftBlock[j++]);
      
    }
    return result;
  }

  protected double[] computeBlockMagnitudes(BufferedWriter fw, Complex[] fftSlice) throws IOException {
    int size = fftSlice.length;
    double magnitudes[] = new double[size];
    for (int i = 0; i < size; i++) {
       // fw.append(fftSlice.toString());
  	  magnitudes[i] = fftSlice[i].abs();
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
  
  public static void importFile(String fileName, ImportToDb Import) throws UnsupportedAudioFileException, IOException{
	  long t0 = System.currentTimeMillis();
	  File file;
	  String[] name = fileName.split("\\."); 
	  long t1;
	  if(name[name.length-1].equals("mp3")){
		  file = Audio.convertMP3toWAV(new File(fileName));
		  t1 = System.currentTimeMillis();
		  System.out.println("Conversion to wav:" + (t1-t0)/1000 + "s");
	  }
	  else
		  file = new File(fileName);
	  t1 = System.currentTimeMillis();
	  int id = Import.SaveMusic(name[0].split("/")[name[0].split("/").length-1], "", "", "", 2000, "");
	  FftProcessor fftProcessor = new FftProcessor();
	  Complex[][] fftSlices = fftProcessor.fft(file);
	  long t2 = System.currentTimeMillis();
	  System.out.println("FFT: " + (t2-t1)/1000 + "s");
	  Hash hashes = fftProcessor.hash(file, fftSlices);
	  long t3 = System.currentTimeMillis();
	  System.out.println("Hashing: " + (t3-t2)/1000 + "s");
	  Import.AddSignatures(id, hashes);
	  long t4 = System.currentTimeMillis();
	  System.out.println("Add signatures:" + (t4-t3)/1000 + "s");
	  
  }
  
  public static void importMusicFromDirectory(String directoryName) throws UnsupportedAudioFileException, IOException{
	  ImportToDb Import = new ImportToDb();
	  String[] fileList = new File(directoryName).list();
	  for(String fileName : fileList){
		  System.out.println("Importing : " + fileName);
		  importFile(directoryName + "/" + fileName, Import);
	  }
	  
  }
  

  public static void main(String... args) throws UnsupportedAudioFileException, IOException {	  	  
//	  importFile("MusicsToImport/Achy Breaky Heart.mp3", new ImportToDb());
//	  importMusicFromDirectory("C:/Users/Romain/PST-Abracadabra/MusicsToImport");
	  
	  ImportToDb.Recognize("Biggy.mp3");
	  
  }
}

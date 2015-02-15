package fr.esiea.pst.abracadabra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jtransforms.fft.FloatFFT_1D;

public class FftProcessor {

  public void fft(File audioFile) throws UnsupportedAudioFileException, IOException {
    byte bytes[] = new byte[4096];

    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
    AudioFormat audioFormat = audioInputStream.getFormat();
    long dataLength = audioInputStream.getFrameLength() * audioFormat.getFrameSize();
    System.out.println(dataLength);

    int nbView = (int) dataLength / bytes.length;
    System.out.println(nbView);

    float data[][] = new float[nbView][4096 * 2];
    FloatFFT_1D fft = new FloatFFT_1D(bytes.length);

    try (BufferedWriter fw1 = new BufferedWriter(new FileWriter("data.txt"));
         BufferedWriter fw2 = new BufferedWriter(new FileWriter("fft.txt"))) {
      for (int i = 0; i < nbView; i++) {
        int read = readWindow(audioInputStream, bytes);

        for (int j = 0; j < read; j++) {
          data[i][j] = bytes[j];
          fw1.write(bytes[j]);
          fw1.newLine();
        }
        fft.complexForward(data[i]);
        System.out.println(fft);

        for (int j = 0; j < data[i].length; j++) {
          fw2.append(Float.toString(data[i][j])).append("   ");
          j++;
          fw2.append(Float.toString(data[i][j]));
          fw2.newLine();
        }
      }
    }
    //autoclose
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
}

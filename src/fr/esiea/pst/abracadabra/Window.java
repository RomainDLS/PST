package fr.esiea.pst.abracadabra;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;

import org.jtransforms.fft.*;

@SuppressWarnings("synthetic-access")
public class Window extends JFrame {
  private static final String SAMPLE_FILE_NAME = "échantillon.wav";

  private static final long serialVersionUID = 1L;

  private final JButton captureBtn = new JButton("Capture");
  private final JButton stopBtn = new JButton("Stop");

  private CaptureThread ct;

  public static void main(String args[]) {
    new Window();// .fft(new File("D:/java/workspace/PST/échantillon.wav"));
  }

  public Window() {
    File audioFile = new File(SAMPLE_FILE_NAME);
    // File mp3File = new File("Klingande.mp3");
    // audio.convertMP3toWAV(mp3File);

    switchStopped();

    captureBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        switchStarted();
        try {
          ct = new CaptureThread(audioFile); //renewing at each capture, else we'll be in an invalid state
          ct.start();
        }
        catch (Exception ex) {
          showError(ex);
        }
      }
    });

    stopBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        switchStopped();

        ct.halt();
        try {
          fft(audioFile);
        }
        catch (Exception ex) {
          showError(ex);
        }
      }
    });

    add(captureBtn);
    add(stopBtn);

    setLayout(new FlowLayout());
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(300, 120);
    setVisible(true);
  }
  
  private void switchStarted() {
    captureBtn.setEnabled(false);
    stopBtn.setEnabled(true);
  }

  private void switchStopped() {
    captureBtn.setEnabled(true);
    stopBtn.setEnabled(false);
  }
  
  
  private void showError(Exception ex) throws HeadlessException {
    JOptionPane.showMessageDialog(null, ex.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE, null);
  }

  
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

    try (BufferedWriter fw1 = new BufferedWriter(new FileWriter("data.txt")); BufferedWriter fw2 = new BufferedWriter(new FileWriter("fft.txt"))) {

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

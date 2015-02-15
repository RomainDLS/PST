package fr.esiea.pst.abracadabra;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
          new FftProcessor().fft(audioFile);
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
}

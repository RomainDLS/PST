package fr.esiea.pst.abracadabra.spectrum;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import fr.esiea.pst.abracadabra.Complex;

public class SpectrumVisualizer extends JFrame {

  private static final long serialVersionUID = 1L;
  
  public SpectrumVisualizer(Complex[][] fftBuffer) {
    this(fftBuffer, true);
  }
  
  public SpectrumVisualizer(Complex[][] fftBuffer, boolean logMode) {
    this(fftBuffer, 800, 600, logMode);
  }
  
  public SpectrumVisualizer(Complex[][] fftBuffer, int width, int height, boolean logMode) {

    add( new SpectrumPanel(fftBuffer, logMode));
    
    addKeyListener( new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          dispose();
        }
      }
    });
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(width, height);
    setVisible(true);
  }
}

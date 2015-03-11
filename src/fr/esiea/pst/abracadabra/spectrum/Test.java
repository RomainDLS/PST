package fr.esiea.pst.abracadabra.spectrum;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.esiea.pst.abracadabra.Complex;
import fr.esiea.pst.abracadabra.FftProcessor;

public class Test {


  public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
    File audioFile = new File("D:/Temp/02 - Mi-1 = -aSn=1NDi[n] [Sj C{i}Fij[n - 1] + [Fexti[[n-1]].wav"); //the famous Aphex Twin demon face

    FftProcessor fp = new FftProcessor();
    Complex[][] fftpSlices = fp.fft(audioFile, (5 * 60 + 27)*1000, 10000); //analyze from 5:27 during 10 seconds.
    new SpectrumVisualizer(fftpSlices, true);
  }

}

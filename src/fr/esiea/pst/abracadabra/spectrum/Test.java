package fr.esiea.pst.abracadabra.spectrum;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.esiea.pst.abracadabra.Audio;
import fr.esiea.pst.abracadabra.Complex;
import fr.esiea.pst.abracadabra.FftProcessor;

public class Test {


  public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
    File audioFile = (Audio.convertMP3toWAV(new File("C:/Users/Romain/aphex twin - Windowlicker (Promo) [warp WAP 105 P]/Aphex Twin - 02 - (A Complex Mathematical Equation).mp3"))); //the famous Aphex Twin demon face

    FftProcessor fp = new FftProcessor();
    Complex[][] fftpSlices = fp.fft(audioFile, (5 * 60 + 27)*1000, 10000); //analyze from 5:27 during 10 seconds.
    new SpectrumVisualizer(fftpSlices, true);
  }

}

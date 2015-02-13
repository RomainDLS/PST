package ESIEA.PST.Abracadabra;
import java.io.*;
import javax.sound.sampled.*;
import it.sauronsoftware.jave.*;

public class Audio{
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;

    public Audio(){
    	this.audioFormat = null;
    	this.targetDataLine = null;
    	this.audioInputStream = null;
    }
    
    public AudioFormat WAVFormat(){
        float sampleRate = 44100;
        int sampleSizeInBits = 8;
        boolean signed = true;
        int channels = 1;      
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
    
    public File convertMP3toWAV(File mp3File){
    	File wavFile = new File("Klingande.wav");
    	AudioAttributes audio = new AudioAttributes();
    	audio.setCodec("pcm_s16le");
    	EncodingAttributes attrs = new EncodingAttributes();
    	attrs.setFormat("wav");
    	attrs.setAudioAttributes(audio);
    	Encoder encoder = new Encoder();
    	try {
			encoder.encode(mp3File, wavFile, attrs);
		} catch (IllegalArgumentException | EncoderException e) {
			e.printStackTrace();
		}
    	return wavFile;
    }
}

package fr.esiea.pst.abracadabra;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

import javax.sound.sampled.AudioFormat;

public class Audio{

    public static AudioFormat WAVFormat(){
        float sampleRate = 44100;
        int sampleSizeInBits = 8;
        boolean signed = true;
        int channels = 1;      
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
    
    public static File convertMP3toWAV(File mp3File){
    	File wavFile = new File("tmp.wav");
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

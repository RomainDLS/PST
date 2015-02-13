package fr.esiea.pst.abracadabra;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

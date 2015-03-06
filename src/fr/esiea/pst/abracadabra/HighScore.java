package fr.esiea.pst.abracadabra;

public class HighScore {
	private int freq;
	private double magn;
	
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public double getMagn() {
		return magn;
	}
	public void setMagn(double magn) {
		this.magn = magn;
	}
	@Override
	public String toString() {
		return "Freq :" + freq + "Hz, Magn :" + magn;
	}

}

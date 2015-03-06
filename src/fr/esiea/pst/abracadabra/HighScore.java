package fr.esiea.pst.abracadabra;

public class HighScore {
	private int Freq;
	private double Magn;
	
	public HighScore(){
	}
	
	public int getFreq() {
		return Freq;
	}
	public void setFreq(int freq) {
		Freq = freq;
	}
	public double getMagn() {
		return Magn;
	}
	public void setMagn(double magn) {
		Magn = magn;
	}
	@Override
	public String toString() {
		return "Freq :" + Freq + "Hz, Magn :" + Magn;
	}

}

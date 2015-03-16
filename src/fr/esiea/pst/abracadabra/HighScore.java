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
		return ""+ freq;
	}
	
	static void removeNegligible(HighScore[] list){
		double MagnMax = 0;
		
		for(HighScore hi: list){
			if(hi.getMagn() > MagnMax)
				MagnMax = hi.getMagn();
			if(hi.getMagn() < MagnMax/7)
				hi.setFreq(0);			
		}
	}

}

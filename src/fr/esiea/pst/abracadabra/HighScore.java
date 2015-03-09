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
		return ""+ Freq;
	}
	
	static void removeNegligible(HighScore[] list){
		double MagnMax = 0;
		
		for(HighScore hi: list){
			if(hi.getMagn() > MagnMax)
				MagnMax = hi.getMagn();
			if(hi.getMagn() < MagnMax)
				hi.setFreq(0);			
		}
	}

}

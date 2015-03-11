package fr.esiea.pst.abracadabra;

import java.util.HashMap;

public class Hash {
	
	HashMap<Integer, Integer> Hash;
	
	public Hash(){
		Hash = new HashMap<>();
	}

	public HashMap<Integer, Integer> getHash() {
		return Hash;
	}

	public void setHash(int time, int Hash) {
		this.Hash.put(time,Hash);
	}

	@Override
	public String toString() {
		return "Hash [Hash=" + Hash + "]";
	}

	
}

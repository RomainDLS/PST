package fr.esiea.pst.abracadabra;

import java.util.HashMap;

//FIXME maybe ust use a list of a tuple object {int time, byte[] hash}
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

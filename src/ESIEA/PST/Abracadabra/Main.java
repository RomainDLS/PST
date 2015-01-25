package ESIEA.PST.Abracadabra;

public class Main {

	public static void main(String[] args) {
		
		ImportToDb Import = new ImportToDb();
	//	Audio son = new Audio();
		
		Import.SaveMusic("If 6'was\\9","ELdl","JH","Rock",1967,"NULL");
		int id = Import.GetIdMusic("If 6'was\\9", "JH");
		for(int i = 6; i<78;i++)
			Import.AddSignature(id, i);
	}

}

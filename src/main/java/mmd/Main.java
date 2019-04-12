package mmd;

import java.io.IOException;

import mmd.model.MMDAlgo;

public class Main {
	
	public static void main(String[] args) {
		MMDAlgo algo = new MMDAlgo("example");
		try {
			algo.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

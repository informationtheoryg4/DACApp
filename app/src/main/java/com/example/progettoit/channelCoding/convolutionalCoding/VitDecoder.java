/*Alessandro Corbetta
 * corbisoft@gmail.com
 * Conv Encoder simulator 1/02/11
 * 
 */

package com.example.progettoit.channelCoding.convolutionalCoding;



//import encMechanicsConv.DecodingTrallisSupport.DecTrellisFrame;

public class VitDecoder {
	
	private int totStatesLog;
	private int codeWordBit;
	
	private Trellis myTrellis;
	private State myState;
	private DecodingTrallisSupport myDecSupp;

	public VitDecoder(String path){
		this.myTrellis = new Trellis(path);
		this.myState = new State(new Integer(0));
		this.totStatesLog = myTrellis.getTotStatesLog();
		this.codeWordBit = myTrellis.getTotCodeBit();
		this.myDecSupp = new DecodingTrallisSupport(1<<totStatesLog,myTrellis);
	}

	public String addTransmittedWord(String word){
		
		int codeWordNum = Integer.parseInt(word, 2);
		return new String( this.myDecSupp.addSection(codeWordNum));
		
	}

	public int getCodeWordBit() {
		return codeWordBit;
	}

	public void reset(){
		this.myState = new State(new Integer(0));
		this.myDecSupp = new DecodingTrallisSupport(1<<totStatesLog,myTrellis);
	}
	
}

/*Alessandro Corbetta
 * corbisoft@gmail.com
 * Conv Encoder simulator 1/02/11
 * 
 */

package com.example.progettoit.channelCoding.convolutionalCoding;



//import encMechanicsConv.Trellis.TrellisFrame;

import java.util.ArrayList;

public class DecodingTrallisSupport {
	private ArrayList<DecTrallisColumn> myColumns;
	private int totStates;
	private Integer myTime;
	private int codewordBit;
	private Trellis refTrallis;
	
	
	public DecodingTrallisSupport(int totStates, Trellis refTrallis){
		this.totStates = totStates;
				
		this.refTrallis = refTrallis;
		this.codewordBit = this.refTrallis.getTotCodeBit();

		//this.myDecTrellisWindow = new DecTrellisWindow();
		
		this.reset();

	}

	private void reset(){
		this.myColumns = new ArrayList<DecTrallisColumn>();
		this.myColumns.add(new DecTrallisColumn(totStates, null,refTrallis,codewordBit));
		myTime = 0;
	}
	synchronized public String addSection(int codeWord){
		//System.out.println("qui!");
		DecTrallisColumn newCol = new DecTrallisColumn(totStates, this.myColumns.get(myTime),refTrallis,codewordBit);
		this.myColumns.add(++myTime,newCol);
		DecOutputInterface decOutput = newCol.createWordSection(codeWord);

		/*if(decOutput.toFlushOut()){
			
			this.reset();
			return decOutput.getInfowordsIcarry();			
			
		}else
			return "";*/
		return decOutput.getInfowordsIcarry();	
	}
	


		

}

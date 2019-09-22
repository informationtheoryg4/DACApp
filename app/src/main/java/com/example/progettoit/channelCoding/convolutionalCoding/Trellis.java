/*Alessandro Corbetta
 * corbisoft@gmail.com
 * Conv Encoder simulator 1/02/11
 * 
 */

package com.example.progettoit.channelCoding.convolutionalCoding;


import android.content.res.AssetManager;

import java.io.*;
import java.util.*;




public class Trellis {
	private int totStatesLog;
	private int totCodeBit;
	private TreeMap<StateAndInfoBit,StateWithInflow> myTrellis;
	private TreeMap<StateAndInfoBit,StateAndInfoBit> codingCorresp;
	private ArrayList<StateWithInflow> orderedFinalStates;
	public ArrayList<StateWithInflow> getOrderedFinalStates() {
		return orderedFinalStates;
	}
	public Trellis(){
		this.myTrellis = new TreeMap<StateAndInfoBit,StateWithInflow>();
		this.codingCorresp = new TreeMap<StateAndInfoBit,StateAndInfoBit>();
		 
	}

	public Trellis(String path){
		this();
		if(this.ReadTrellis(path) == false) System.exit(0);
	}
	
	public CodeWordAndFinalState codedOut(State myState, Integer myInfoBit){
		StateAndInfoBit stWithInfoTmp = new StateAndInfoBit(myState,myInfoBit);
		stWithInfoTmp = this.codingCorresp.get(stWithInfoTmp);
		return new CodeWordAndFinalState(stWithInfoTmp.getMyCodeWordStr(),this.myTrellis.get(stWithInfoTmp));
		
	}

	public int getTotCodeBit() {
		return totCodeBit;
	}
	private StringTokenizer readTrellisFileLine(BufferedReader stFile){
		String line;
		boolean flag = true;
		try {
			do{ //leggo la prima riga non commentata
				line = stFile.readLine();
				if( line==null)
					return null;
				else{			
					if(! line.startsWith("#"))
						flag =false;
				}
			}while(flag);
			return new StringTokenizer(line, " ");
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	public int getTotStatesLog() {
		return totStatesLog;
	}
	
	private boolean ReadTrellis(String path){

		BufferedReader stFile;
		try {
			stFile = new BufferedReader(new StringReader(
					"# Dimensione dello spazio degli stati i.e. log_2 (states) | bit parola di codice\n" +
					"2 3\n" +
					"# stato | bit di info | parola di codice | stato in out \n" +
					"0 0 000 0\n" +
					"0 1 111 2\n" +
					"1 0 001 0\n" +
					"1 1 110 2\n" +
					"2 0 011 1 \n" +
					"3 0 010 1\n" +
					"3 1 101 3\n" +
					"2 1 100 3"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		StringTokenizer st;
		this.totStatesLog = Integer.parseInt((st =readTrellisFileLine(stFile)).nextToken());
		this.totCodeBit = Integer.parseInt(st.nextToken());

		StateWithInflow[] ouputAlreadyConsid = new StateWithInflow[(1 << totStatesLog )];
		
		for(int i = 0 ; i < (1 << totStatesLog); i++ ) ouputAlreadyConsid[i] = null;

		for(int i = 0 ; i < (1 << totStatesLog + 1); i++ ){
			st = readTrellisFileLine(stFile);
			int state = Integer.parseInt(st.nextToken());
			int infoBit = Integer.parseInt(st.nextToken());
			int codeword = Integer.parseInt(st.nextToken(),2);
			int outState = Integer.parseInt(st.nextToken());
			StateAndInfoBit from = new StateAndInfoBit(new Integer(state), new Integer(infoBit), new Integer(codeword),totCodeBit);
			StateWithInflow to;
			if(ouputAlreadyConsid[outState] == null){			
				to = new StateWithInflow(new Integer(outState));
				ouputAlreadyConsid[outState] = to;			
			}else
				to = ouputAlreadyConsid[outState];
			this.myTrellis.put(from, to);
			this.codingCorresp.put(from, from);
		}

		//backward link 4 decoding
		
		Iterator<StateAndInfoBit> itOnKeys =  this.myTrellis.keySet().iterator();		
		while(itOnKeys.hasNext()){
			StateAndInfoBit key = itOnKeys.next();
			this.myTrellis.get(key).setInFlow(key);
			//System.out.println(this.myTrellis.get(key));
		}
		Set<StateWithInflow> tempSet = new TreeSet<StateWithInflow>(this.myTrellis.values());
		this.orderedFinalStates = new ArrayList<StateWithInflow>(tempSet);
		Collections.sort(this.orderedFinalStates);

		return true;
	}
	
	}

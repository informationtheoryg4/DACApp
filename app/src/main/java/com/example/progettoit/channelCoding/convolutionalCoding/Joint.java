/*Alessandro Corbetta
 * corbisoft@gmail.com
 * Conv Encoder simulator 1/02/11
 * 
 */

package com.example.progettoit.channelCoding.convolutionalCoding;


public class Joint {
	private Encoder myEncoder;
	private BSChannel myCh;
	private VitDecoder myDec;
	private int totInfoBit;
	private int totCodedBit;
	
	public int getTotCodedBit() {
		return totCodedBit;
	}

	public Joint(String path,double probab){
		this.myEncoder = new Encoder(path);
		this.myCh = new BSChannel(probab);
		this.myDec = new VitDecoder(path);
	}
	
	
	
	
	public int getTotalInfobit(){
		return this.totInfoBit;
	}
	
	public String CodeSeq(String seq){
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < seq.length(); i++){
			sb.append(this.myEncoder.encode(Integer.parseInt(Character.toString( seq.charAt(i)))));
		}
		return new String(sb);
		
	}

	public String CodeSeqAndSend(String seq){
		StringBuffer sb = new StringBuffer();
		StringBuffer cw = new StringBuffer();
		String codedWord;

		for(int i = 0 ; i < seq.length(); i++){
			codedWord = this.myEncoder.encode(Integer.parseInt(Character.toString( seq.charAt(i))));
			cw.append(codedWord);
			for(int j = 0 ; j <codedWord.length();j++){
				sb.append(this.myCh.transition(Integer.parseInt((Character.toString(codedWord.charAt(j))))));
			}

		}
		//System.out.println("Information sequence: \t" + seq);
		//System.out.println("Coded word: \t\t" + new String(cw));
		//System.out.println("Received word: \t\t" + new String(sb));
		return new String(sb);
		//ci serve la coded word cw
	}
	private int hammingWeight(String a, String b){
		
		int weight =0;
		/*for(int i=0;i<this.myDec.getCodeWordBit();i++){
			weight += c%2;
			c >>=1;
		}*/
		
		for(int i = 0; i<a.length();i++){
			if(a.charAt(i) != b.charAt(i)) weight++;
		}
		
		return weight;
	}
	public String CodeTransfDecode(String seq){


		String trans = this.CodeSeqAndSend(seq);

		//System.out.println("Received word: " + trans);

		StringBuffer sb = new StringBuffer("Decoded...\n");

		for(int i = 0; i< trans.length()/this.myDec.getCodeWordBit()-1;i++){
			this.myDec.addTransmittedWord(trans.substring(this.myDec.getCodeWordBit()*i,this.myDec.getCodeWordBit()*(i+1)));
		}

		int i = trans.length()/this.myDec.getCodeWordBit()-1;
		String decOutput = this.myDec.addTransmittedWord(trans.substring(this.myDec.getCodeWordBit()*i,this.myDec.getCodeWordBit()*(i+1)));
		String output = new String("Decoded: "+ decOutput  +"\n");// +
			//"Transmission Errors: " + Integer.toString(hammingWeight(Integer.parseInt(this.CodeSeq(seq),2),Integer.parseInt(decOutput,2))));

		return output;
	}


	public String Decode(String seq){
		for(int i = 0; i< seq.length()/this.myDec.getCodeWordBit()-1;i++){
			this.myDec.addTransmittedWord(seq.substring(this.myDec.getCodeWordBit()*i,
					this.myDec.getCodeWordBit()*(i+1)));
		}
		int i = seq.length()/this.myDec.getCodeWordBit()-1;
		String decOutput = this.myDec.addTransmittedWord(seq.substring(this.myDec.getCodeWordBit()*i,
				this.myDec.getCodeWordBit()*(i+1)));
		return decOutput;
	}
	
	public InterexchangeSeq CodeTransfDecode4Win(String seq){

		this.totInfoBit = seq.length();
		StringBuffer sb = new StringBuffer();
		StringBuffer cw = new StringBuffer();
		String codedWord;
		for(int i = 0 ; i < seq.length(); i++){
			codedWord = this.myEncoder.encode(Integer.parseInt(Character.toString( seq.charAt(i))));
			cw.append(codedWord);
			for(int j = 0 ; j <codedWord.length();j++){
				sb.append(this.myCh.transition(Integer.parseInt((Character.toString(codedWord.charAt(j))))));
			}

		}
		String codeString = new String(cw);
		this.totCodedBit = codeString.length();


		String trans = new String(sb);

		sb = new StringBuffer("Decoded...\n");

		for(int i = 0; i< trans.length()/this.myDec.getCodeWordBit()-1;i++){
			this.myDec.addTransmittedWord(trans.substring(this.myDec.getCodeWordBit()*i,this.myDec.getCodeWordBit()*(i+1)));
		}

		int i = trans.length()/this.myDec.getCodeWordBit()-1;
		String decOutput = this.myDec.addTransmittedWord(trans.substring(this.myDec.getCodeWordBit()*i,this.myDec.getCodeWordBit()*(i+1)));
		String output = new String("Decoded: "+ decOutput  +"\n");// +
			//"Transmission Errors: " + Integer.toString(hammingWeight(Integer.parseInt(this.CodeSeq(seq),2),Integer.parseInt(decOutput,2))));


		int codingErr, bitErr;

		codingErr = this.hammingWeight(codeString, trans);
		bitErr = this.hammingWeight(seq, decOutput);


		return new InterexchangeSeq(codeString,trans,decOutput, codingErr,bitErr);


	}
	
	public void resetEnc(){
		this.myEncoder.reset();
		this.myDec.reset();
	}
	
	public static void main(String[]args) {
		Joint jt = new Joint("CodifEsempioLibro540.txt", 0.2);
		char [] c1 = new char[32];
		for(int i = 0; i<32; i++){
		    c1[i] = i%2==0?'0':'1';
        }
		String seq = "100011100011111110001010000111100011010";
		String coded = "111011001000111110101010001000111100101101101101101010001000111011110011001001000111100101101010001000110100010110011";
		//char [] chararray = coded.toCharArray();
		//chararray[3] = '0';
		//String coded1 = new String(chararray);
		
		System.out.println(seq+"\n"+coded);
		System.out.println(coded.length());
		jt.resetEnc();
		
		String decOutput = jt.Decode(coded);
		System.out.println("output: "+decOutput+"\ninput: "+seq);

		System.out.println((int)34.543);
		
	}

}

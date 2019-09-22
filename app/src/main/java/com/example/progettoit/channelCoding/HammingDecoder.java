package com.example.progettoit.channelCoding;

import static com.example.progettoit.channelCoding.HammingCoder.xor;

public class HammingDecoder {

    public static String hDecode(String c) {

        if(c.length()%12!=0)
            throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i+12 <= c.length(); i+=12) {
            sb.append(hamming_12_8_Decode(c.substring(i, i+12)));
        }

        return sb.toString();

    }

    public static String parityCheck(String c) {
        String[] HT = {
                "1000",
                "0100",
                "1100",
                "0010",
                "1010",
                "0110",
                "1110",
                "0001",
                "1001",
                "0101",
                "1101",
                "0011"
        };
        String tmp = "0000";

        char [] syn = tmp.toCharArray();
        for (int j = 0; j < HT[0].length(); j++) {
            for (int i = 0; i < c.length(); i++) {
                syn[j] = xor((Character.getNumericValue(HT[i].charAt(j))*Character.getNumericValue(c.charAt(i))),
                        Character.getNumericValue(syn[j]));
            }
        }

        if (String.valueOf(syn).equals("0000")) //nessun errore
            return c;
        ConcatenatedDecoder.errorCount++;
        System.out.println("Bit corrected");
        char [] res = c.toCharArray();
        int pos = 0;
        for(int i = 0; i<syn.length;i++){
            if(syn[i]=='1')
                pos+=Math.pow(2,i);
        }
        if(pos>12)//ERRORE NON CORRETTO
            return c;
        System.out.println(new String(syn));
        res[pos-1]= (res[pos-1]=='1')?'0':'1';

        return new String(res);
    }

    private static String hamming_12_8_Decode(String c) {

        if(c.length()!=12)
            throw new IllegalArgumentException();

        String c1 = parityCheck(c);

        return c1.charAt(2)+c1.substring(4,7)+c1.substring(8);

    }

    private static String hamming_7_4_Decode(String c) {
        return c.charAt(2)+c.substring(4);
    }

}

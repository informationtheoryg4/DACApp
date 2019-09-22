package com.example.progettoit.channelCoding;


public class R3Decoder {

    public static String r3decode(String enc){
        char [] decoded = new char[enc.length()/3];
        for(int i = 0; i<enc.length(); i+=3){
            decoded[i/3] = enc.charAt(i);
        }
        return new String(decoded);
    }

    public static String r3errorCheck(String enc){
        if(enc.length()%3!=0)
            throw new IllegalArgumentException();
        char[] c = enc.toCharArray();
        for(int i = 0; i<c.length; i+=3){
            if(c[i]==c[i+1]&& c[i+1]==c[i+2]);
            else{
                ConcatenatedDecoder.errorCount++;
                System.out.println("Bit corrected");
                if(c[i]==c[i+1])
                    c[i+2] = c[i];
                else if(c[i]==c[i+2])
                    c[i+1]=c[i];
                else
                    c[i]=c[i+1];
            }
        }
        System.out.println("");
        return new String(c);
    }

}

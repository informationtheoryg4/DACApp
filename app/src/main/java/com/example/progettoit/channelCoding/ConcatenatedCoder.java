package com.example.progettoit.channelCoding;

import static com.example.progettoit.channelCoding.HammingCoder.hEncode;
import static com.example.progettoit.channelCoding.R3Coder.r3encode;

public class ConcatenatedCoder {

    private static final double ERROR_PROBABILITY = 0.001;

    public static String concatEncode(String s){
        if(s.length()%8!=0)
            throw new IllegalArgumentException();
        String hamming = hEncode(s);
        String res = r3encode(hamming);



        return res;
    }

}

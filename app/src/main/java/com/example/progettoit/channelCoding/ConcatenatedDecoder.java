package com.example.progettoit.channelCoding;

import static com.example.progettoit.channelCoding.ConcatenatedCoder.concatEncode;
import static com.example.progettoit.channelCoding.HammingDecoder.hDecode;
import static com.example.progettoit.channelCoding.R3Decoder.r3decode;
import static com.example.progettoit.channelCoding.R3Decoder.r3errorCheck;

public class ConcatenatedDecoder {

    public static int errorCount;

    public static String concatDecode(String s){
        String correct = r3errorCheck(s);
        String r3 = r3decode(correct);
        String res = hDecode(r3);
        return res;
    }

    public static void main(String[]args){
        String s = "0100111001011110";
        String c = concatEncode(s);
        System.out.println(c);
        System.out.println(s+"\n"+concatDecode(c));
    }

}

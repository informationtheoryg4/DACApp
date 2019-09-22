package com.example.progettoit.channelCoding;


import static com.example.progettoit.channelCoding.R3Decoder.r3decode;
import static com.example.progettoit.channelCoding.R3Decoder.r3errorCheck;

public class R3Coder {

    public static String r3encode(String s){
        char [] encoded = new char[s.length()*3];
        for(int i = 0, j = 0; i<s.length(); i++, j+=3){
            encoded[j] = s.charAt(i);
            encoded[j+1] = s.charAt(i);
            encoded[j+2] = s.charAt(i);
        }
        return new String(encoded);
    }




    public static void main(String[]args){
        String s = "101011001";
        String e = r3encode(s);
        char[] ch = e.toCharArray();
        ch[2]='0';
        ch[3]='1';
        ch[11]='1';
        String corr = r3errorCheck(new String(ch));
        System.out.println(new String(ch));
        System.out.println(corr);
        System.out.print(r3decode(e));

    }
}

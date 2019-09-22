package com.example.progettoit.utils;

import java.util.Arrays;

public class Utils {

    public static String byteArrayToString(byte [] byteArray){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < byteArray.length; i++){
            sb.append(byteArray[i]+" ");
        }
        return sb.toString();
    }

    public static byte[] stringToByteArray(String string){
        String [] s = string.split(" ");
        byte [] byteArray = new byte[s.length];
        for(int i = 0; i < s.length; i++){
            byteArray[i] = Byte.parseByte(s[i]);
        }
        return byteArray;
    }

    public static String byteArrayToBinary(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        int res = 0;
        if(byteArray.length>5 && byteArray[0] == (byte)12 && byteArray[1] == (byte)34 && byteArray[2] == (byte)56 &&
                byteArray[3] == (byte)78 && byteArray[4]>0){
            //System.out.println("aaaa");
            res = byteArray[4];
            byte [] tmp = Arrays.copyOfRange(byteArray, 5, byteArray.length);
            byteArray = tmp;
        }
        for (byte b : byteArray){
            int val = b;
            for (int i = 0; i < 8; i++){
                sb.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return sb.toString().substring(res);
    }

    public static byte[] binaryToByteArray(String bin) {
        StringBuilder sb = new StringBuilder();
        int res = 8-bin.length()%8;
        //System.out.println(res);
        if(res!=8){
            for(int i = 0; i<res; i++){
                sb.append('0');
            }
        }
        sb.append(bin);
        String binary = sb.toString();
        byte [] byteArray;
        //System.out.println(binary);
        int o;
        if(res==8) {
            byteArray = new byte[binary.length() / 8];
            o = 0;
        }
        else {
            byteArray = new byte[binary.length() / 8 + 5];
            o = 5;
            byteArray[0]= (byte)12;
            byteArray[1]= (byte)34;
            byteArray[2]= (byte)56;
            byteArray[3]= (byte)78;
            byteArray[4]=(byte)res;
        }
        //System.out.println(byteArray.length);

        for (; o < byteArray.length; o++) {
            byteArray[o] = 0;
        }
        int index;
        if(res==8) {
            for (int i = 0; i < binary.length(); i += 8) {
                for (int j = 0; j < 8; j++) {
                    index = i + j;
                    if (binary.charAt(index) == '1')
                        byteArray[i / 8] += (byte) Math.pow(2, 7 - j);
                }
            }
        }else{
            for (int i = 0; i < binary.length(); i += 8) {
                for (int j = 0; j < 8; j++) {
                    index = i + j;
                    if (binary.charAt(index) == '1')
                        byteArray[i / 8+5] += (byte) Math.pow(2, 7 - j);
                }
            }
        }
        return byteArray;
    }

    public static void main (String []args){
        byte[] ba = {34,10,56};
        String s = byteArrayToBinary(ba);
        String s1 = "011010010101110010011000100000101000111000";
        System.out.println(s+"\n"+s1);
        byte[] ba1 =binaryToByteArray(s1);
        System.out.println(byteArrayToBinary(ba1));
        for(byte b : ba1)
            System.out.print(b+" ");
    }

}

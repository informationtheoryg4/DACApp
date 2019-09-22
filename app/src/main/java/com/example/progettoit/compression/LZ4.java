package com.example.progettoit.compression;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LZ4 {

    public static byte[] lz4compress(byte[] data, int bufferSize){
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, bufferSize, compressor);
        try {
            compressedOutput.write(data);
            compressedOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteOutput.toByteArray();
    }

    public static byte[] lz4decompress(byte[] data, int bufferSize){
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LZ4FastDecompressor decompresser = factory.fastDecompressor();
        LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(data), decompresser);
        int count;
        byte[] buffer = new byte[bufferSize];
        try {
            while ((count = lzis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            lzis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

}

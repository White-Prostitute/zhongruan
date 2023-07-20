package edu.scu.zhongruan.utils;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UsuUtil {

    //获取文件后缀名
    public static String getFileSuffix(String fileName){
        if(fileName == null)return "";
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i+1);
    }

    //转移流数据
    public static void transferStreamData(InputStream fis, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = fis.read(bytes))!=-1){
            outputStream.write(bytes, 0, len);
        }
    }

    public static String readStrFromInputStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
        int len;
        try{
            while ((len = stream.read(bytes))!=-1){
                sb.append(new String(bytes, 0, len));
            }
        }finally {
            stream.close();
        }
        return sb.toString();
    }

    public static String transferSteamToBase64(InputStream stream) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();
        return encoder.encode(bytes);
    }
}

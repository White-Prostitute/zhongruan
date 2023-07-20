package edu.scu.zhongruan.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class HttpUtil {

    /**
     * 上传单个文件
     * @param file 使用的文件
     * @param urlStr 访问的url
     * @return 响应字符串
     * @throws IOException IO
     */
    public static String uploadFile(FileHolder file, String urlStr) throws IOException {
        FileHolder[] files = new FileHolder[1];
        files[0] = file;
        return uploadFiles(files, urlStr);
    }

    /**
     * 上传多个文件
     * @param files 文件数组
     * @param urlStr 访问的url
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public static String uploadFiles(FileHolder[] files, String urlStr) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("charset", "UTF-8");
        con.setRequestProperty("accept", "application/json");
//        con.setRequestProperty("Content-length", String.valueOf(file.length()));
        con.setDoOutput(true);
        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ BOUNDARY);
        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        String body = writeBody(files, BOUNDARY);
        out.write(body.getBytes(StandardCharsets.UTF_8));
        out.close();

        //获取响应数据
        StringBuilder res = new StringBuilder();
        byte[] bytess = new byte[1024];
        int len = 0;
        InputStream inputStream = con.getInputStream();
        while ((len = inputStream.read(bytess))!=-1){
            res.append(new String(bytess, 0, len));
        }
        inputStream.close();
        return res.toString();
    }

    /**
     * 书写文件传输的请求体
     * @param files 上传的文件数组
     * @param boundary 边界值
     * @return 响应字符串
     * @throws IOException IO异常
     */
    public static String writeBody(FileHolder[] files, String boundary) throws IOException {
        StringBuilder res = new StringBuilder();
        for (FileHolder file : files) {
            InputStream stream = file.getStream();
            String fileName = file.getFileName();
            String sb = "--" + // 必须多两道线
                    boundary +
                    "\r\n" +
                    "Content-Disposition: form-data;name=\"files\";filename=\"" +fileName + "\"\r\n" +
                    "Content-Type:application/octet-stream\r\n\r\n";
            res.append(sb);

            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            DataInputStream in = new DataInputStream(stream);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                res.append(new String(bufferOut, 0, bytes));
            }
            in.close();
            res.append("\r\n");
        }

        // 结尾部分
        res.append("--").append(boundary).append("--\r\n");
        return res.toString();
    }

    /**
     * MultipartFile对象转化为FileHolder对象
     * @param files 文件对象数组
     * @return FileHolder对象数组
     * @throws IOException IO异常
     */
    public static FileHolder[] transferToFileHolder(MultipartFile[] files, String[] fileNames) throws IOException {
        int len = files.length;
        FileHolder[] res = new FileHolder[len];
        for (int i = 0; i < len; i++) {
            InputStream stream = files[i].getInputStream();
            String suffix = Objects.requireNonNull(files[i].getOriginalFilename()).split("\\.")[1];
            String fileName = fileNames[i];
            FileHolder holder = new FileHolder(fileName, stream);
            res[i] = holder;
        }
        return res;
    }

    /**
     * 将File对象数组转化为FileHolder对象数组
     * @param files File对象数组
     * @param useRandomFileName 是否使用随机文件名:是-》利用UUID生成随机文件名 否-》使用原始文件名
     * @return FileHolder对象数组
     * @throws FileNotFoundException IO异常
     */
    public static FileHolder[] transferToFileHolder(File[] files, boolean useRandomFileName) throws FileNotFoundException {
        int len = files.length;
        FileHolder[] res = new FileHolder[len];
        for (int i = 0; i < len; i++) {
            FileInputStream stream = new FileInputStream(files[i]);
            String originFileName = files[i].getName();
            String suffix = originFileName.substring(originFileName.lastIndexOf('.')+1);
            String fileName = useRandomFileName? UUID.randomUUID() +"."+suffix : originFileName;
            FileHolder holder = new FileHolder(fileName, stream);
            res[i] = holder;
        }
        return res;
    }

    public static FileHolder transferToFileHolder(MultipartFile file,String fileName) throws IOException {
        return transferToFileHolder(new MultipartFile[]{file}, new String[]{fileName})[0];
    }


    /**
     * 封装文件流和文件名
     */
    public static class FileHolder{
        private String fileName;
        private InputStream stream;

        public FileHolder(String fileName, InputStream stream) {
            this.fileName = fileName;
            this.stream = stream;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }
    }

}

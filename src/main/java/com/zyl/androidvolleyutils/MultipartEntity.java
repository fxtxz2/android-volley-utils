package com.zyl.androidvolleyutils;

import android.text.TextUtils;
import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MultipartEntity implements HttpEntity{

	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"  
            .toCharArray();  
    /** 
     * 换行符 
     */  
    private final String NEW_LINE_STR = "\r\n";  
    private final String CONTENT_TYPE = "Content-Type: ";  
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";  
    /** 
     * 文本参数和字符集 
     */  
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";
  
    /** 
     * 字节流参数 
     */  
    private final String TYPE_OCTET_STREAM = "application/octet-stream";  
    /** 
     * 二进制参数 
     */  
    private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n".getBytes();  
    /** 
     * 文本参数 
     */  
    private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();  
  
    /** 
     * 分隔符 
     */  
    private String mBoundary = null;  
    /** 
     * 输出流 
     */  
    ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
  
    public MultipartEntity() {  
        this.mBoundary = generateBoundary();  
    }  
  
    /** 
     * 生成分隔符 
     *  
     * @return 
     */  
    private String generateBoundary() {
        final StringBuilder buf = new StringBuilder();
        final Random rand = new Random();  
        for (int i = 0; i < 30; i++) {  
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);  
        }  
        return buf.toString();  
    }  
  
    /** 
     * 参数开头的分隔符 
     *  
     * @throws IOException 
     */  
    private void writeFirstBoundary() throws IOException {  
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());  
    }  
  
    /** 
     * 添加文本参数 
     *  
     * @param paramName 参数名
     * @param value  参数value
     */  
    public void addStringPart(final String paramName, final String value) {  
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");  
    }
    /**
     * 将数据写入到输出流中
     *
     * @param paramName
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeToOutputStream(String paramName, byte[] rawData, String type,
                                     byte[] encodingBytes,
                                     String fileName) {
        try {
            writeFirstBoundary();
            mOutputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream.write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 把文件当文本参数添加,并且转化成base64
     * 添加文本参数
     *
     * @param paramName 参数名
     * @param value 参数值
     */
    public void addStringFilePart(final String paramName, final File value) {
        writeFileToOutputStream(paramName, value, TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }

    /**
     * 把文件当文本参数添加,并且转化成base64把文件当文本参数添加,并且转化成base64
     * 添加文本参数
     * 添加文本参数
     *
     * @param paramName
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeFileToOutputStream(String paramName, File rawData, String type, byte[] encodingBytes, String fileName) {
        InputStream fin = null;
        try {
            fin = new FileInputStream(rawData);
//            fin = new FileInputStream(new File("/mnt/sdcard/1.png"));
            writeFirstBoundary();
            mOutputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream.write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            final byte[] tmp = new byte[4096];
            int len;
//            int i = 0;
            while ((len = fin.read(tmp)) != -1) {
                mOutputStream.write(Base64.encode(tmp, 0, len, Base64.DEFAULT));
                mOutputStream.flush();
//                Log.d("files:" + i, Base64.encodeToString(tmp, 0, len, Base64.DEFAULT));
//                i++;
            }
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fin);
        }
    }


    /**
     * 添加二进制参数, 例如Bitmap的字节流参数 
     *  
     * @param paramName 参数名
     * @param rawData  参数值
     */  
    public void addBinaryPart(String paramName, final byte[] rawData) {  
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM, BINARY_ENCODING, "no-file");  
    }  
  
    /** 
     * 添加文件参数,可以实现文件上传功能 
     *  
     * @param key 参数名
     * @param file 文件对象
     */  
    public void addFilePart(final String key, final File file) {  
        InputStream fin = null;  
        try {  
            fin = new FileInputStream(file);  
            writeFirstBoundary();  
            final String type = CONTENT_TYPE + TYPE_OCTET_STREAM + NEW_LINE_STR;  
            mOutputStream.write(getContentDispositionBytes(key, file.getName()));  
            mOutputStream.write(type.getBytes());  
            mOutputStream.write(BINARY_ENCODING);  
  
            final byte[] tmp = new byte[4096];  
            int len;
            while ((len = fin.read(tmp)) != -1) {  
                mOutputStream.write(tmp, 0, len);  
            }  
            mOutputStream.flush();  
        } catch (final IOException e) {  
            e.printStackTrace();  
        } finally {  
            closeSilently(fin);  
        }  
    }  
  
    private void closeSilently(Closeable closeable) {  
        try {  
            if (closeable != null) {  
                closeable.close();  
            }  
        } catch (final IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    private byte[] getContentDispositionBytes(String paramName, String fileName) {  
        StringBuilder stringBuilder = new StringBuilder();  
        stringBuilder.append(CONTENT_DISPOSITION + "form-data; name=\"").append(paramName).append("\"");
        // 文本参数没有filename参数,设置为空即可  
        if (!TextUtils.isEmpty(fileName)) {  
            stringBuilder.append("; filename=\"").append(fileName).append("\"");
        }  
  
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();  
    }  
  
    @Override  
    public long getContentLength() {  
        return mOutputStream.toByteArray().length;  
    }  
  
    @Override  
    public Header getContentType() {  
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + mBoundary);  
    }  
  
    @Override  
    public boolean isChunked() {  
        return false;  
    }  
  
    @Override  
    public boolean isRepeatable() {  
        return false;  
    }  
  
    @Override  
    public boolean isStreaming() {  
        return false;  
    }  
  
    @Override  
    public void writeTo(final OutputStream outstream) throws IOException {  
        // 参数最末尾的结束符  
        final String endString = "--" + mBoundary + "--\r\n";  
        // 写入结束符  
        mOutputStream.write(endString.getBytes());  
        //  
        outstream.write(mOutputStream.toByteArray());  
    }  
  
    @Override  
    public Header getContentEncoding() {  
        return null;  
    }  
  
    @Override  
    public void consumeContent() throws IOException,  
            UnsupportedOperationException {  
        if (isStreaming()) {  
            throw new UnsupportedOperationException(  
                    "Streaming entity does not implement #consumeContent()");  
        }  
    }  
  
    @Override  
    public InputStream getContent() {  
        return new ByteArrayInputStream(mOutputStream.toByteArray());  
    }

}

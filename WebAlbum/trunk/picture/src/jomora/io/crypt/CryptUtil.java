package jomora.io.crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES方式の秘密鍵(非公開鍵)を用いて暗号化・復号化を行う。
 * @version $Id$
 */
public class CryptUtil {

    /**
     * 暗号化方式
     */
    private final String DEFAULT_ALGORITHM = "DES";

    /**
     * エンコード/デコードする単位バッファサイズ
     */
    private int buffSize = 1024;

    /**
     * デフォルトのSecretKeyを作成するためのbyte[]
     */
    private byte[] keyMaterial = {
              (byte)0xd0, (byte)0x89, (byte)0x29, (byte)0x88
            , (byte)0xd1, (byte)0xe8, (byte)0xa7, (byte)0xfe};

    /**
     * SecretKey
     */
    private SecretKey secretKey = null;

    /**
     * デフォルトコンストラクタ
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public CryptUtil()
        throws InvalidKeyException,
               NoSuchAlgorithmException ,
               InvalidKeySpecException {

        secretKey = createDESCryptKey(keyMaterial);
    }

    /**
     * 秘密鍵を受け取って設定するコンストラクタ
     * @param key 秘密鍵
     */
    public CryptUtil(SecretKey key) {
        setSecretKey(key);
    }

    /**
     * byte[]から秘密鍵を生成して設定するコンストラクタ
     * @param keyMaterial 秘密鍵生成のためのバイト配列
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public CryptUtil(byte[] keyMaterial)
        throws InvalidKeyException,
               NoSuchAlgorithmException,
               InvalidKeySpecException {

        setKeyMaterial(keyMaterial);
        setSecretKey(createDESCryptKey(getKeyMaterial()));
    }

    /**
     * 秘密鍵を設定する
     * @param key 秘密鍵
     */
    private final void setSecretKey(SecretKey key) {
        this.secretKey = key;
    }

    /**
     * 秘密鍵を取得する
     * @return 秘密鍵
     */
    private final SecretKey getSecretKey() {
        return this.secretKey;
    }

    /**
     * キーマテリアルを設定する
     * @param keyMaterial 秘密鍵生成のためのバイト配列
     */
    private final void setKeyMaterial(byte[] keyMaterial) {
        this.keyMaterial = keyMaterial;
    }

    /**
     * キーマテリアルを取得する
     * @return キーマテリアル
     */
    private final byte[] getKeyMaterial() {
        return this.keyMaterial;
    }

    /**
     * InputStreamの内容を暗号化してOutputStreamに書き出す
     * @param in 入力
     * @param out 出力
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public void encrypt(InputStream in, OutputStream out)
        throws NoSuchPaddingException,
               NoSuchAlgorithmException,
               InvalidKeyException,
               BadPaddingException,
               IllegalBlockSizeException,
               IOException  {
        crypt(Cipher.ENCRYPT_MODE, getSecretKey(), in, out);
    }

    /**
     * InputStreamの内容を復号化してOutputStreamに書き出す
     * @param in 入力
     * @param out 出力
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public void decrypt(InputStream in, OutputStream out)
        throws NoSuchPaddingException,
               NoSuchAlgorithmException,
               InvalidKeyException,
               BadPaddingException,
               IllegalBlockSizeException,
               IOException  {
        crypt(Cipher.DECRYPT_MODE, getSecretKey(), in, out);
    }

    /**
     * モードに従って、InputStreamの内容をKeyを用いて、
     * 暗号化/復号化してOutputStreamに書き出す
     * @param opmode オペレーションモード
     * @param key 秘密鍵
     * @param in 入力
     * @param out 出力
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    private void crypt(int opmode, Key key, InputStream in, OutputStream out)
        throws NoSuchPaddingException,
               NoSuchAlgorithmException,
               InvalidKeyException,
               BadPaddingException,
               IllegalBlockSizeException,
               IOException  {
        Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
        cipher.init(opmode, key);
        byte[] data = new byte[buffSize];
        int len;
        while ((len = in.read(data)) != -1) {
            out.write(cipher.update(data, 0, len));
        }
        byte[] bytes = cipher.doFinal();
        if (bytes != null) {
            out.write(bytes);
        }
        out.flush();
    }

    /**
     * DES方式の秘密鍵を生成する
     * @param keyMaterial キーマテリアル
     * @return 秘密鍵
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private SecretKey createDESCryptKey(byte[] keyMaterial)
        throws InvalidKeyException,
               NoSuchAlgorithmException ,
               InvalidKeySpecException {

        DESKeySpec keySpec = new DESKeySpec(keyMaterial);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DEFAULT_ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }

    /**
     * 文字列のDES暗号化、及びHex変換
     * @param str 暗号化する文字列
     * @return 暗号化された文字列
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public String encryptString(String str)
        throws NoSuchPaddingException,
               NoSuchAlgorithmException,
               InvalidKeyException,
               BadPaddingException,
               IllegalBlockSizeException,
               IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encrypt(in, out);

        byte[] ba = out.toByteArray();
        out.close();
        in.close();

        String outStr = new String();
        for (int i = 0; i < ba.length; i++){
            int num = (int)(ba[i] & 0xff);
            String tmpStr = Integer.toHexString(num);
            tmpStr = "00" + tmpStr;
            tmpStr = tmpStr.substring(tmpStr.length() -2 , tmpStr.length());
            outStr += tmpStr;
        }

       return outStr;
    }

    /**
     * 文字列のHex逆変換、及びDES複合化
     * @param str 複合化する文字列
     * @return 複合化された文字列
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public String decryptString(String str)
        throws NoSuchPaddingException,
               NoSuchAlgorithmException,
               InvalidKeyException,
               BadPaddingException,
               IllegalBlockSizeException,
               IOException {

        byte[] bi = new byte[str.length() / 2];
        for (int i = 0 ; i < str.length() / 2 ; i++){
            String buf = str.substring (i * 2 , i * 2 + 2);
            bi[i] = (byte)Integer.parseInt(buf,16);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(bi);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        decrypt(in, out);

        String outStr = out.toString();
        out.close();
        in.close();

        return outStr;
    }

    /**
     * 文字列のHex変換
     * @param str 変換する文字列
     * @return 変換された文字列
     * @throws IOException
     */
    public static String simpleEncryptString(String str) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(str);

        byte[] ba = baos.toByteArray();
        baos.close();
        oos.close();

        String outStr = new String();
        for (int i = 0; i < ba.length; i++){
            int num = (int)(ba[i] & 0xff);
            String tmpStr = Integer.toHexString(num);
            tmpStr = "00" + tmpStr;
            tmpStr = tmpStr.substring(tmpStr.length() -2 , tmpStr.length());
            outStr += tmpStr;
        }
       return outStr;
    }

    /**
     * 文字列のHex逆変換
     * @param str 逆変換する文字列
     * @return 逆変換された文字列
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static String simpleDecryptString(String str)
        throws IOException,
               ClassNotFoundException  {
        byte[] bi = new byte[str.length() / 2];
        for (int i = 0 ; i < str.length() / 2 ; i++){
            String buf = str.substring (i * 2 , i * 2 + 2);
            bi[i] = (byte)Integer.parseInt(buf, 16);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bi);
        ObjectInputStream ois = new ObjectInputStream(bais);
        String outStr = (String)ois.readObject();
        bais.close();
        ois.close();

        return outStr;
    }

}


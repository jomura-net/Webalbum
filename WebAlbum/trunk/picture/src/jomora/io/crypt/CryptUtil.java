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
 * DES�����̔閧��(����J��)��p���ĈÍ����E���������s���B
 * @version $Id$
 */
public class CryptUtil {

    /**
     * �Í�������
     */
    private final String DEFAULT_ALGORITHM = "DES";

    /**
     * �G���R�[�h/�f�R�[�h����P�ʃo�b�t�@�T�C�Y
     */
    private int buffSize = 1024;

    /**
     * �f�t�H���g��SecretKey���쐬���邽�߂�byte[]
     */
    private byte[] keyMaterial = {
              (byte)0xd0, (byte)0x89, (byte)0x29, (byte)0x88
            , (byte)0xd1, (byte)0xe8, (byte)0xa7, (byte)0xfe};

    /**
     * SecretKey
     */
    private SecretKey secretKey = null;

    /**
     * �f�t�H���g�R���X�g���N�^
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
     * �閧�����󂯎���Đݒ肷��R���X�g���N�^
     * @param key �閧��
     */
    public CryptUtil(SecretKey key) {
        setSecretKey(key);
    }

    /**
     * byte[]����閧���𐶐����Đݒ肷��R���X�g���N�^
     * @param keyMaterial �閧�������̂��߂̃o�C�g�z��
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
     * �閧����ݒ肷��
     * @param key �閧��
     */
    private final void setSecretKey(SecretKey key) {
        this.secretKey = key;
    }

    /**
     * �閧�����擾����
     * @return �閧��
     */
    private final SecretKey getSecretKey() {
        return this.secretKey;
    }

    /**
     * �L�[�}�e���A����ݒ肷��
     * @param keyMaterial �閧�������̂��߂̃o�C�g�z��
     */
    private final void setKeyMaterial(byte[] keyMaterial) {
        this.keyMaterial = keyMaterial;
    }

    /**
     * �L�[�}�e���A�����擾����
     * @return �L�[�}�e���A��
     */
    private final byte[] getKeyMaterial() {
        return this.keyMaterial;
    }

    /**
     * InputStream�̓��e���Í�������OutputStream�ɏ����o��
     * @param in ����
     * @param out �o��
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
     * InputStream�̓��e�𕜍�������OutputStream�ɏ����o��
     * @param in ����
     * @param out �o��
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
     * ���[�h�ɏ]���āAInputStream�̓��e��Key��p���āA
     * �Í���/����������OutputStream�ɏ����o��
     * @param opmode �I�y���[�V�������[�h
     * @param key �閧��
     * @param in ����
     * @param out �o��
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
     * DES�����̔閧���𐶐�����
     * @param keyMaterial �L�[�}�e���A��
     * @return �閧��
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
     * �������DES�Í����A�y��Hex�ϊ�
     * @param str �Í������镶����
     * @return �Í������ꂽ������
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
     * �������Hex�t�ϊ��A�y��DES������
     * @param str ���������镶����
     * @return ���������ꂽ������
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
     * �������Hex�ϊ�
     * @param str �ϊ����镶����
     * @return �ϊ����ꂽ������
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
     * �������Hex�t�ϊ�
     * @param str �t�ϊ����镶����
     * @return �t�ϊ����ꂽ������
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


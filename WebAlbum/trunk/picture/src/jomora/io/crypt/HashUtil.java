package jomora.io.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

	public static String sha2(String text) {
		byte[] cipher_byte;
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
        md.update(text.getBytes());
        cipher_byte = md.digest();

        return Base64.getEncoder().encodeToString(cipher_byte);
	}

	public static String md5(String text) {
		byte[] cipher_byte;
        MessageDigest md;
		try {
	        md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
        // �n�b�V���l���v�Z
        md.update(text.getBytes());
        cipher_byte = md.digest();

        // 16�i��������ɕϊ�
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < cipher_byte.length; i++) {
            String tmp = Integer.toHexString(cipher_byte[i] & 0xff);
            if(tmp.length() == 1) {
                buffer.append('0').append(tmp);
            } else {
                buffer.append(tmp);
            }
        }
        return buffer.toString();
    }

}

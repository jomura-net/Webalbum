package jomora.io.crypt;

import static org.junit.Assert.fail;

import org.junit.Test;

public class CryptUtilTest {

	@Test
	public void testCryptUtil() {

		String in = "これはテストです。";
		
		CryptUtil testCrypt = null;

		try {
			testCrypt = new CryptUtil();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			System.out.println("in : " + in);

			long startTime;
			long[] time = new long[100];
//			long sum = 0;

			String enc = testCrypt.encryptString(in);
			System.out.println("enc : " + enc);
			String dec = testCrypt.decryptString(enc);
			System.out.println("dec : " + dec);
			long total = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				startTime = System.currentTimeMillis();
				enc = testCrypt.encryptString(in);
				dec = testCrypt.decryptString(enc);
				time[i] = System.currentTimeMillis() - startTime;
				// System.out.print("time[" + i + "] = " + time[i] + "[msec] ");
//				sum += time[i];
			}
			total = System.currentTimeMillis() - total;
			System.out.println("平均は" + (total / 100) + "[msec]");

//			sum = 0;
			String simpleEnc = CryptUtil.simpleEncryptString(in);
			System.out.println("simpleEnc : " + simpleEnc);
			String simpleDec = CryptUtil.simpleDecryptString(simpleEnc);
			System.out.println("simpleDec : " + simpleDec);
			total = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				startTime = System.currentTimeMillis();
				simpleEnc = CryptUtil.simpleEncryptString(in);
				simpleDec = CryptUtil.simpleDecryptString(simpleEnc);
				time[i] = System.currentTimeMillis() - startTime;
				// System.out.print("time[" + i + "] = " + time[i] + "[msec] ");
//				sum += time[i];
			}
			total = System.currentTimeMillis() - total;
			System.out.println("平均は" + (total / 100) + "[msec]");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCryptUtilSecretKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testCryptUtilByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncrypt() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrypt() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncryptString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecryptString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSimpleEncryptString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSimpleDecryptString() {
		fail("Not yet implemented");
	}

}

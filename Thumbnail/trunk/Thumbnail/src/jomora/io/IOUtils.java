package jomora.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * �X�g���[�����o�͊֘A���[�e�B���e�B.
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2011/06/03 ���ō쐬
 */
public final class IOUtils {

    /**
     * �v���C�x�[�g�R���X�g���N�^.
     */
    private IOUtils() {
    }

    /**
     * ��O��throw������close����.
     * @param input ���̓X�g���[��
     */
    public static void closeQuietly(final InputStream input) {
        try {
            if (null != input) {
                input.close();
            }
        } catch (IOException ioe) {
            ioe.getMessage();
        }
    }

    /**
     * ��O��throw������close����.
     * @param output �o�̓X�g���[��
     */
    public static void closeQuietly(final OutputStream output) {
        try {
            if (null != output) {
                output.close();
            }
        } catch (IOException ioe) {
            ioe.getMessage();
        }
    }

}

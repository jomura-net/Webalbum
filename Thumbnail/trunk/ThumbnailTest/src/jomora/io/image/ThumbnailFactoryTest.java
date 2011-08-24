package jomora.io.image;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * ThumbnailFactory�N���X�̂��߂̃e�X�g�P�[�X�B
 * @author jomora@jomora.info
 * @version [2006.05.05] �쐬
 * @version [2006.05.06] �R�����g�ǉ�(CheckStyle,FindBugs�Ή�)
 */
public class ThumbnailFactoryTest extends TestCase {

    /**
     * �e�X�g�P�[�X���s���\�b�h�B
     * @param args �R�}���h���C�������B���w��Ŗ��Ȃ��B
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ThumbnailFactoryTest.class);
    }

    /**
     * �T���l�C���̍ő啝
     */
    private static final int MAX_WIDTH = 200;
    /**
     * �T���l�C���̍ő卂��
     */
    private static final int MAX_HEIGHT = 500;

    /**
     * createThumbnail���\�b�h�̃e�X�g�B
     * �e�X�g�s�x�A�t�@�C�����Ȃǂ̏����������K�v�B
     */
    public final void testCreateThumbnail() {

        // ���摜
        jomora.io.File file = new jomora.io.File("img/test.jpg");

        // ���O�ɃT���l�C���i�[�p�̃t�H���_������
        File thumbDir = new File(file.getParentFile(), "thumbnail");
        if (!thumbDir.exists()) {
            thumbDir.mkdir();
        }

        // �T���l�C��
        File thumFile = new File(thumbDir, "tn_"
                + file.getNameWithoutExtension() + ".jpg");

        FileInputStream fiStrm = null;
        FileOutputStream foStrm = null;
        try {
            fiStrm = new FileInputStream(file);
            foStrm = new FileOutputStream(thumFile);

            ThumbnailFactory.createThumbnail(fiStrm, foStrm,
                    MAX_WIDTH, MAX_HEIGHT, "jpg");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fiStrm != null) {
                try {
                    fiStrm.close();
                } catch (Exception e) {
                    // Do Nothing
                }
            }
            if (foStrm != null) {
                try {
                    foStrm.close();
                } catch (Exception e) {
                    // Do Nothing
                }
            }
        }
    }

    /**
     * isSupportedReaderFormat���\�b�h�̃e�X�g�B
     * �������B
     */
    public final void testIsSupportedReaderFormat() {
        // TODO �����������ꂽ���\�b�h�E�X�^�u
    }

}

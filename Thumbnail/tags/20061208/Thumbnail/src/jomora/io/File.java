package jomora.io;

import java.net.URI;

/**
 * java.io.File�N���X���g�������N���X�B
 * ��ɁA�t�@�C���g���q���������\�b�h���ǉ�����Ă���B
 * @author jomora@jomora.info
 * @version [2006.05.05] �R�����g�ǉ�(CheckStyle,FindBugs�Ή�)
 */
public class File extends java.io.File {

    /**
     * ����ID�B
     */
    private static final long serialVersionUID = -7378511302399827604L;

    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^�B
     * @param pathname �t�@�C���p�X
     */
    public File(String pathname) {
        super(pathname);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^�B
     * @param uri URI
     */
    public File(URI uri) {
        super(uri);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^�B
     * @param parent �e�t�H���_
     * @param child �q�t�@�C��
     */
    public File(java.io.File parent, String child) {
        super(parent, child);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^�B
     * @param parent �e�t�H���_
     * @param child �q�t�@�C��
     */
    public File(String parent, String child) {
        super(parent, child);
    }

    /**
     * �t�@�C�������擾����B
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q�t�̃t�@�C����
     */
    public static String getName(String filename) {
        return new File(filename).getName();
    }

    /**
     * �g���q�����̃t�@�C�������擾����B
     * @return  �g���q���Ȃ����t�@�C����
     */
    public final String getNameWithoutExtension() {
        String filename = this.getName();
        return getNameWithoutExtension(filename);
    }

    /**
     * �g���q�����̃t�@�C�������擾����B
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q���Ȃ����t�@�C����
     */
    public static String getNameWithoutExtension(String filename) {
        filename = new File(filename).getName();
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            return filename.substring(0, i);
        }
        return filename;
    }

    /**
     * �g���q���擾����B
     * @return  �g���q
     */
    public final String getExtension() {
        String filename = this.getName();
        return getExtension(filename);
    }

    /**
     * �g���q���擾����B
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q
     */
    public static String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            return filename.substring(i + 1);
        }
        return "";
    }

}

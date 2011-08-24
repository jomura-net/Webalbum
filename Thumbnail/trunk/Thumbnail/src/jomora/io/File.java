package jomora.io;

import java.net.URI;

/**
 * java.io.File�N���X���g�������N���X.
 * ��ɁA�t�@�C���g���q���������\�b�h���ǉ�����Ă���B
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2006/05/05 �R�����g�ǉ�(CheckStyle,FindBugs�Ή�)
 */
public class File extends java.io.File {

    /**
     * ����ID.
     */
    private static final long serialVersionUID = -7378511302399827604L;

    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^.
     * @param pathname �t�@�C���p�X
     */
    public File(final String pathname) {
        super(pathname);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^.
     * @param uri URI
     */
    public File(final URI uri) {
        super(uri);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^.
     * @param parent �e�t�H���_
     * @param child �q�t�@�C��
     */
    public File(final java.io.File parent, final String child) {
        super(parent, child);
    }
    /**
     * �X�[�p�[�N���X����̃R���X�g���N�^.
     * @param parent �e�t�H���_
     * @param child �q�t�@�C��
     */
    public File(final String parent, final String child) {
        super(parent, child);
    }

    /**
     * �t�@�C�������擾����.
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q�t�̃t�@�C����
     */
    public static String getName(final String filename) {
        return new File(filename).getName();
    }

    /**
     * �g���q�����̃t�@�C�������擾����.
     * @return  �g���q���Ȃ����t�@�C����
     */
    public final String getNameWithoutExtension() {
        String filename = this.getName();
        return getNameWithoutExtension(filename);
    }

    /**
     * �g���q�����̃t�@�C�������擾����.
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q���Ȃ����t�@�C����
     */
    public static String getNameWithoutExtension(final String filename) {
        String filenm = new File(filename).getName();
        int i = filenm.lastIndexOf('.');
        if (i > 0 && i < filenm.length() - 1) {
            return filenm.substring(0, i);
        }
        return filenm;
    }

    /**
     * �g���q���擾����.
     * @return  �g���q
     */
    public final String getExtension() {
        String filename = this.getName();
        return getExtension(filename);
    }

    /**
     * �g���q���擾����.
     * @param filename �g���q�t�̃t�@�C����
     * @return �g���q
     */
    public static String getExtension(final String filename) {
    	if (null == filename) {
            return null;
    	}
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            String ext = filename.substring(i + 1);
            int index = ext.indexOf("&");
            if (-1 != index) {
                ext = ext.substring(0, index);
            }
            return ext;
        }
        return "";
    }

}

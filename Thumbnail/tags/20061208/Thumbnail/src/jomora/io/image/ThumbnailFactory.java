package jomora.io.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * �T���l�C��
 * @author jomora@jomora.info
 * @version [2006.05.05] �R�����g�ǉ�(CheckStyle,FindBugs�Ή�)
 */
public final class ThumbnailFactory {

    /**
     * �v���C�x�[�g�R���X�g���N�^�B
     * �C���X�^���X����h�~����B
     */
    private ThumbnailFactory() {
    }

    /**
     * �T���l�C���摜���쐬����B ���摜�̃A�X�y�N�g���ێ������܂܁A
     *  �w�肵���T�C�Y�Ɏ��߂�B
     * ���摜���ǂݍ��݃T�|�[�g����Ă���摜�t�H�[�}�b�g�łȂ������ꍇ�ɂ́A
     *  ��O����������̂Œ��ӂ��K�v�B
     * ���O��isSupportedImageFormat()�Ń`�F�b�N���Ă��������B
     * �܂��A���摜�������o���T�|�[�g����Ă���摜�t�H�[�}�b�g�łȂ������ꍇ�ɂ́A
     *  jpg�`���ŏo�͂����B
     * �c�O�Ȃ���A��{�I�ɂ�jpg�ɂ����Ή����Ă��Ȃ��B
     * png��gif�̏ꍇ�AColorModel�̈ڍs���ł��Ă��Ȃ����߁A�F��������B
     * @param inStrm ���̉摜
     * @param outStrm �T���l�C���摜
     * @param maxThumbWidth �T���l�C���摜�̍ő啝
     * @param maxThumbHeight �T���l�C���摜�̍ő卂��
     * @param imageFormat �摜�t�H�[�}�b�g
     * @throws IOException �摜�̎擾�␶���Ɏ��s�����ꍇ�ɔ�������B
     */
    public static void createThumbnail(
            InputStream inStrm, OutputStream outStrm,
            int maxThumbWidth, int maxThumbHeight, String imageFormat)
            throws IOException {
        // ���摜
        BufferedImage image = ImageIO.read(inStrm);
        if (image == null) {
            throw new IOException("Can't read the original image.");
        }

        // ���摜�̕�
        int width = image.getWidth();
        // ���摜�̍���
        int height = image.getHeight();

        // ���̏k���{��
        double widthRate = (double)maxThumbWidth / width;
        // �����̏k���{��
        double heightRate = (double)maxThumbHeight / height;

        // �T���l�C���̏k���{���i�������j
        double rate = 1;

        // ���A�����̂����A���������̔{����L���ɂ���
        if (widthRate > heightRate) {
            rate = heightRate;
        } else {
            rate = widthRate;
        }
        // �g��͂��Ȃ�
        if (rate > 1) {
            rate = 1;
        }

        // ColorModel���Ǝ��w�肳��Ă���摜�̂��߂̎b��Ή�
        // �F�������܂��B(^^;;
        int imageType = image.getType();
        if (imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = BufferedImage.TYPE_INT_RGB;
        }

        // �T���l�C���摜
        BufferedImage shrinkImage = new BufferedImage(
                (int)(image.getWidth() * rate),
                (int)(image.getHeight() * rate), imageType);
        AffineTransformOp atOp = new AffineTransformOp(AffineTransform
                .getScaleInstance(rate, rate), null);
        // �T���l�C���摜�쐬
        atOp.filter(image, shrinkImage);

        // �����o���T�|�[�g����Ă��Ȃ��摜�t�H�[�}�b�g(gif��)�̏ꍇ�́A
        // PNG�`���ŏo�͂���B
        if (!isSupportedWriterFormat(imageFormat)) {
            imageFormat = "jpg";
        }
        // �T���l�C���摜�����o��
        ImageIO.write(shrinkImage, imageFormat, outStrm);
    }

    /**
     * �ǂݍ��݃T�|�[�g����Ă���摜�t�H�[�}�b�g���ǂ����`�F�b�N���܂��B
     *  �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false��Ԃ��܂��B
     * @param extension �摜�t�@�C���̊g���q�i��Gjpg, JPEG, pNg�Ȃǁj
     * @return �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false.
     */
    public static boolean isSupportedReaderFormat(String extension) {
        String[] readerNames = ImageIO.getReaderFormatNames();
        for (int i = 0; i < readerNames.length; i++) {
            if (readerNames[i].equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * �����o���T�|�[�g����Ă���摜�t�H�[�}�b�g���ǂ����`�F�b�N���܂��B
     *  �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false��Ԃ��܂��B
     * @param extension �摜�t�@�C���̊g���q�i��Gjpg, JPEG, pNg�Ȃǁj
     * @return �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false.
     */
    public static boolean isSupportedWriterFormat(String extension) {
        String[] writerNames = ImageIO.getWriterFormatNames();
        for (int i = 0; i < writerNames.length; i++) {
            if (writerNames[i].equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

}

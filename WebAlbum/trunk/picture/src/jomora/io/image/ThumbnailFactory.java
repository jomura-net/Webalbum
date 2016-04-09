package jomora.io.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 * �T���l�C���摜�쐬�N���X
 * @version $Id$
 */
public class ThumbnailFactory {

    private ThumbnailFactory() {
    }

    /**
     * �T���l�C���摜���쐬����B
     * ���摜�̃A�X�y�N�g���ێ������܂܁A�w�肵���T�C�Y�Ɏ��߂�B
     * ���摜���ǂݍ��݃T�|�[�g����Ă��Ȃ��摜�t�H�[�}�b�g�̏ꍇ�A��O����������B
     * formatName���������݃T�|�[�g����Ă��Ȃ��摜�t�H�[�}�b�g�̏ꍇ�A��̃o�C�g�z���Ԃ��B
     * @param inStrm ���̉摜
     * @param outStrm �T���l�C���摜
     * @param maxThumbWidth �T���l�C���摜�̍ő啝
     * @param maxThumbHeight �T���l�C���摜�̍ő卂��
     * @param formatName �摜�t�@�C���̊g���q
     * @throws IOException �摜�̎擾�␶���Ɏ��s�����ꍇ�ɔ�������B
     */
    public static void createThumbnail(
        InputStream inStrm,
        OutputStream outStrm,
        int maxThumbWidth,
        int maxThumbHeight, String formatName)
        throws IOException {
    	
    	ImageIO.setUseCache(false);

    	//���摜
//        BufferedImage image = ImageIO.read(inStrm);
        ImageReader ireader = (ImageReader) ImageIO.getImageReadersByFormatName(formatName).next();
        ireader.setInput(ImageIO.createImageInputStream(inStrm));
        BufferedImage image = ireader.read(0);
        ireader.dispose();
        
        if (image == null) {
        	throw new IOException("Can't read the original image."); 
        }

        //���摜�̕�		
        int width = image.getWidth();
        //���摜�̍���
        int height = image.getHeight();

        //���̏k���{��
        double widthRate = (double)maxThumbWidth / width;
        //�����̏k���{��
        double heightRate = (double)maxThumbHeight / height;

        //�T���l�C���̏k���{���i�������j
        double rate = 1;

        //���A�����̂����A���������̔{����L���ɂ���
        if (widthRate > heightRate) {
            rate = heightRate;
        } else {
            rate = widthRate;
        }
        //�g��͂��Ȃ�
        if (rate > 1) {
            rate = 1;
        }

        //�T���l�C���摜
        BufferedImage shrinkImage =
            new BufferedImage(
                (int) (image.getWidth() * rate),
                (int) (image.getHeight() * rate),
                image.getType());
        AffineTransformOp atOp =
            new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        //�T���l�C���摜�쐬
        atOp.filter(image, shrinkImage);
        //�T���l�C���摜�����o��
        ImageIO.write(shrinkImage, formatName, outStrm);
    }

	/**
	 * �ǂݍ��݃T�|�[�g����Ă���摜�t�H�[�}�b�g���ǂ����`�F�b�N���܂��B
	 * �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false��Ԃ��܂��B
	 * @param extension �摜�t�@�C���̊g���q�i��Gjpg, JPEG, pNg�Ȃǁj
	 * @return �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false.
	 */
	public static boolean isSupportedReaderFormat(String extension) {
		String[] readerNames = ImageIO.getReaderFormatNames();
		for (int i = 0 ; i < readerNames.length ; i++) {
			if (readerNames[i].equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �������݃T�|�[�g����Ă���摜�t�H�[�}�b�g���ǂ����`�F�b�N���܂��B
	 * �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false��Ԃ��܂��B
	 * @param extension �摜�t�@�C���̊g���q�i��Gjpg, JPEG, pNg�Ȃǁj
	 * @return �T�|�[�g����Ă���ꍇ��true, �����łȂ��ꍇ��false.
	 */
	public static boolean isSupportedWriterFormat(String extension) {
		String[] writerNames = ImageIO.getWriterFormatNames();
		for (int i = 0 ; i < writerNames.length ; i++) {
			if (writerNames[i].equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

    /*
     * ���p�T���v��
     */
	/*
    public static void main(String[] args) {
		//�T���l�C���̍ő�T�C�Y
        int maxWidth = 200;
        int maxHeight = 500;

        //���摜
        jomora.io.File file = new jomora.io.File("D:/�`���[���b�v.jpg");

        //���O�ɃT���l�C���i�[�p�̃t�H���_������
        File thumbDir = new File(file.getParentFile(), "thumbnail");
        if (!thumbDir.exists()) {
            thumbDir.mkdir();
        }

        //�T���l�C��
        File thumFile = new File(thumbDir, "tn_" + file.getNameWithoutExtension() + ".jpg");

        FileInputStream fiStrm = null;
        FileOutputStream foStrm = null;
        try {
            fiStrm = new FileInputStream(file);
            foStrm = new FileOutputStream(thumFile);

            ThumbnailFactory.createThumbnail(fiStrm, foStrm, maxWidth, maxHeight, "jpg");

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
    */
	
}

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
 * サムネイル画像作成クラス
 * @version $Id$
 */
public class ThumbnailFactory {

    private ThumbnailFactory() {
    }

    /**
     * サムネイル画像を作成する。
     * 元画像のアスペクト比を保持したまま、指定したサイズに収める。
     * 元画像が読み込みサポートされていない画像フォーマットの場合、例外が発生する。
     * formatNameが書き込みサポートされていない画像フォーマットの場合、空のバイト配列を返す。
     * @param inStrm 元の画像
     * @param outStrm サムネイル画像
     * @param maxThumbWidth サムネイル画像の最大幅
     * @param maxThumbHeight サムネイル画像の最大高さ
     * @param formatName 画像ファイルの拡張子
     * @throws IOException 画像の取得や生成に失敗した場合に発生する。
     */
    public static void createThumbnail(
        InputStream inStrm,
        OutputStream outStrm,
        int maxThumbWidth,
        int maxThumbHeight, String formatName)
        throws IOException {
    	
    	ImageIO.setUseCache(false);

    	//元画像
//        BufferedImage image = ImageIO.read(inStrm);
        ImageReader ireader = (ImageReader) ImageIO.getImageReadersByFormatName(formatName).next();
        ireader.setInput(ImageIO.createImageInputStream(inStrm));
        BufferedImage image = ireader.read(0);
        ireader.dispose();
        
        if (image == null) {
        	throw new IOException("Can't read the original image."); 
        }

        //元画像の幅		
        int width = image.getWidth();
        //元画像の高さ
        int height = image.getHeight();

        //幅の縮小倍率
        double widthRate = (double)maxThumbWidth / width;
        //高さの縮小倍率
        double heightRate = (double)maxThumbHeight / height;

        //サムネイルの縮小倍率（初期化）
        double rate = 1;

        //幅、高さのうち、小さい方の倍率を有効にする
        if (widthRate > heightRate) {
            rate = heightRate;
        } else {
            rate = widthRate;
        }
        //拡大はしない
        if (rate > 1) {
            rate = 1;
        }

        //サムネイル画像
        BufferedImage shrinkImage =
            new BufferedImage(
                (int) (image.getWidth() * rate),
                (int) (image.getHeight() * rate),
                image.getType());
        AffineTransformOp atOp =
            new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        //サムネイル画像作成
        atOp.filter(image, shrinkImage);
        //サムネイル画像書き出し
        ImageIO.write(shrinkImage, formatName, outStrm);
    }

	/**
	 * 読み込みサポートされている画像フォーマットかどうかチェックします。
	 * サポートされている場合はtrue, そうでない場合はfalseを返します。
	 * @param extension 画像ファイルの拡張子（例；jpg, JPEG, pNgなど）
	 * @return サポートされている場合はtrue, そうでない場合はfalse.
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
	 * 書き込みサポートされている画像フォーマットかどうかチェックします。
	 * サポートされている場合はtrue, そうでない場合はfalseを返します。
	 * @param extension 画像ファイルの拡張子（例；jpg, JPEG, pNgなど）
	 * @return サポートされている場合はtrue, そうでない場合はfalse.
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
     * 利用サンプル
     */
	/*
    public static void main(String[] args) {
		//サムネイルの最大サイズ
        int maxWidth = 200;
        int maxHeight = 500;

        //元画像
        jomora.io.File file = new jomora.io.File("D:/チューリップ.jpg");

        //事前にサムネイル格納用のフォルダを準備
        File thumbDir = new File(file.getParentFile(), "thumbnail");
        if (!thumbDir.exists()) {
            thumbDir.mkdir();
        }

        //サムネイル
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

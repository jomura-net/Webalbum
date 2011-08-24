package jomora.io.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * サムネイル
 * @author jomora@jomora.info
 * @version [2006.05.05] コメント追加(CheckStyle,FindBugs対応)
 */
public final class ThumbnailFactory {

    /**
     * プライベートコンストラクタ。
     * インスタンス化を防止する。
     */
    private ThumbnailFactory() {
    }

    /**
     * サムネイル画像を作成する。 元画像のアスペクト比を保持したまま、
     *  指定したサイズに収める。
     * 元画像が読み込みサポートされている画像フォーマットでなかった場合には、
     *  例外が発生するので注意が必要。
     * 事前にisSupportedImageFormat()でチェックしておきたい。
     * また、元画像が書き出しサポートされている画像フォーマットでなかった場合には、
     *  jpg形式で出力される。
     * 残念ながら、基本的にはjpgにしか対応していない。
     * pngやgifの場合、ColorModelの移行ができていないため、色が化ける。
     * @param inStrm 元の画像
     * @param outStrm サムネイル画像
     * @param maxThumbWidth サムネイル画像の最大幅
     * @param maxThumbHeight サムネイル画像の最大高さ
     * @param imageFormat 画像フォーマット
     * @throws IOException 画像の取得や生成に失敗した場合に発生する。
     */
    public static void createThumbnail(
            InputStream inStrm, OutputStream outStrm,
            int maxThumbWidth, int maxThumbHeight, String imageFormat)
            throws IOException {
        // 元画像
        BufferedImage image = ImageIO.read(inStrm);
        if (image == null) {
            throw new IOException("Can't read the original image.");
        }

        // 元画像の幅
        int width = image.getWidth();
        // 元画像の高さ
        int height = image.getHeight();

        // 幅の縮小倍率
        double widthRate = (double)maxThumbWidth / width;
        // 高さの縮小倍率
        double heightRate = (double)maxThumbHeight / height;

        // サムネイルの縮小倍率（初期化）
        double rate = 1;

        // 幅、高さのうち、小さい方の倍率を有効にする
        if (widthRate > heightRate) {
            rate = heightRate;
        } else {
            rate = widthRate;
        }
        // 拡大はしない
        if (rate > 1) {
            rate = 1;
        }

        // ColorModelが独自指定されている画像のための暫定対応
        // 色化けします。(^^;;
        int imageType = image.getType();
        if (imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = BufferedImage.TYPE_INT_RGB;
        }

        // サムネイル画像
        BufferedImage shrinkImage = new BufferedImage(
                (int)(image.getWidth() * rate),
                (int)(image.getHeight() * rate), imageType);
        AffineTransformOp atOp = new AffineTransformOp(AffineTransform
                .getScaleInstance(rate, rate), null);
        // サムネイル画像作成
        atOp.filter(image, shrinkImage);

        // 書き出しサポートされていない画像フォーマット(gif等)の場合は、
        // PNG形式で出力する。
        if (!isSupportedWriterFormat(imageFormat)) {
            imageFormat = "jpg";
        }
        // サムネイル画像書き出し
        ImageIO.write(shrinkImage, imageFormat, outStrm);
    }

    /**
     * 読み込みサポートされている画像フォーマットかどうかチェックします。
     *  サポートされている場合はtrue, そうでない場合はfalseを返します。
     * @param extension 画像ファイルの拡張子（例；jpg, JPEG, pNgなど）
     * @return サポートされている場合はtrue, そうでない場合はfalse.
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
     * 書き出しサポートされている画像フォーマットかどうかチェックします。
     *  サポートされている場合はtrue, そうでない場合はfalseを返します。
     * @param extension 画像ファイルの拡張子（例；jpg, JPEG, pNgなど）
     * @return サポートされている場合はtrue, そうでない場合はfalse.
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

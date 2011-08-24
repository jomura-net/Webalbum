package jomora.io.image;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * ThumbnailFactoryクラスのためのテストケース。
 * @author jomora@jomora.info
 * @version [2006.05.05] 作成
 * @version [2006.05.06] コメント追加(CheckStyle,FindBugs対応)
 */
public class ThumbnailFactoryTest extends TestCase {

    /**
     * テストケース実行メソッド。
     * @param args コマンドライン引数。無指定で問題ない。
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ThumbnailFactoryTest.class);
    }

    /**
     * サムネイルの最大幅
     */
    private static final int MAX_WIDTH = 200;
    /**
     * サムネイルの最大高さ
     */
    private static final int MAX_HEIGHT = 500;

    /**
     * createThumbnailメソッドのテスト。
     * テスト都度、ファイル名などの書き換えが必要。
     */
    public final void testCreateThumbnail() {

        // 元画像
        jomora.io.File file = new jomora.io.File("img/test.jpg");

        // 事前にサムネイル格納用のフォルダを準備
        File thumbDir = new File(file.getParentFile(), "thumbnail");
        if (!thumbDir.exists()) {
            thumbDir.mkdir();
        }

        // サムネイル
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
     * isSupportedReaderFormatメソッドのテスト。
     * 未実装。
     */
    public final void testIsSupportedReaderFormat() {
        // TODO 自動生成されたメソッド・スタブ
    }

}

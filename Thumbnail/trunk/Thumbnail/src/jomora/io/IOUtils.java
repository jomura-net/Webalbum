package jomora.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ストリーム入出力関連ユーティリティ.
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2011/06/03 初版作成
 */
public final class IOUtils {

    /**
     * プライベートコンストラクタ.
     */
    private IOUtils() {
    }

    /**
     * 例外をthrowせずにcloseする.
     * @param input 入力ストリーム
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
     * 例外をthrowせずにcloseする.
     * @param output 出力ストリーム
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

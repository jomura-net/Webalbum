package jomora.io;

import java.net.URI;

/**
 * java.io.Fileクラスを拡張したクラス。
 * 主に、ファイル拡張子を扱うメソッドが追加されている。
 * @author jomora@jomora.info
 * @version [2006.05.05] コメント追加(CheckStyle,FindBugs対応)
 */
public class File extends java.io.File {

    /**
     * 直列化ID。
     */
    private static final long serialVersionUID = -7378511302399827604L;

    /**
     * スーパークラスからのコンストラクタ。
     * @param pathname ファイルパス
     */
    public File(String pathname) {
        super(pathname);
    }
    /**
     * スーパークラスからのコンストラクタ。
     * @param uri URI
     */
    public File(URI uri) {
        super(uri);
    }
    /**
     * スーパークラスからのコンストラクタ。
     * @param parent 親フォルダ
     * @param child 子ファイル
     */
    public File(java.io.File parent, String child) {
        super(parent, child);
    }
    /**
     * スーパークラスからのコンストラクタ。
     * @param parent 親フォルダ
     * @param child 子ファイル
     */
    public File(String parent, String child) {
        super(parent, child);
    }

    /**
     * ファイル名を取得する。
     * @param filename 拡張子付のファイル名
     * @return 拡張子付のファイル名
     */
    public static String getName(String filename) {
        return new File(filename).getName();
    }

    /**
     * 拡張子無しのファイル名を取得する。
     * @return  拡張子を省いたファイル名
     */
    public final String getNameWithoutExtension() {
        String filename = this.getName();
        return getNameWithoutExtension(filename);
    }

    /**
     * 拡張子無しのファイル名を取得する。
     * @param filename 拡張子付のファイル名
     * @return 拡張子を省いたファイル名
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
     * 拡張子を取得する。
     * @return  拡張子
     */
    public final String getExtension() {
        String filename = this.getName();
        return getExtension(filename);
    }

    /**
     * 拡張子を取得する。
     * @param filename 拡張子付のファイル名
     * @return 拡張子
     */
    public static String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            return filename.substring(i + 1);
        }
        return "";
    }

}

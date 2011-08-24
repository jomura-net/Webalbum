package jomora.io;

import java.net.URI;

/**
 * java.io.Fileクラスを拡張したクラス.
 * 主に、ファイル拡張子を扱うメソッドが追加されている。
 * @author Jomora ( kazuhiko@jomura.net http://jomura.net/ )
 * @version 2006/05/05 コメント追加(CheckStyle,FindBugs対応)
 */
public class File extends java.io.File {

    /**
     * 直列化ID.
     */
    private static final long serialVersionUID = -7378511302399827604L;

    /**
     * スーパークラスからのコンストラクタ.
     * @param pathname ファイルパス
     */
    public File(final String pathname) {
        super(pathname);
    }
    /**
     * スーパークラスからのコンストラクタ.
     * @param uri URI
     */
    public File(final URI uri) {
        super(uri);
    }
    /**
     * スーパークラスからのコンストラクタ.
     * @param parent 親フォルダ
     * @param child 子ファイル
     */
    public File(final java.io.File parent, final String child) {
        super(parent, child);
    }
    /**
     * スーパークラスからのコンストラクタ.
     * @param parent 親フォルダ
     * @param child 子ファイル
     */
    public File(final String parent, final String child) {
        super(parent, child);
    }

    /**
     * ファイル名を取得する.
     * @param filename 拡張子付のファイル名
     * @return 拡張子付のファイル名
     */
    public static String getName(final String filename) {
        return new File(filename).getName();
    }

    /**
     * 拡張子無しのファイル名を取得する.
     * @return  拡張子を省いたファイル名
     */
    public final String getNameWithoutExtension() {
        String filename = this.getName();
        return getNameWithoutExtension(filename);
    }

    /**
     * 拡張子無しのファイル名を取得する.
     * @param filename 拡張子付のファイル名
     * @return 拡張子を省いたファイル名
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
     * 拡張子を取得する.
     * @return  拡張子
     */
    public final String getExtension() {
        String filename = this.getName();
        return getExtension(filename);
    }

    /**
     * 拡張子を取得する.
     * @param filename 拡張子付のファイル名
     * @return 拡張子
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

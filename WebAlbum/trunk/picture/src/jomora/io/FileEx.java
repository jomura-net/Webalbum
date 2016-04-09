package jomora.io;

import java.net.URI;

/**
 * 拡張Fileクラス
 * @version $Id$
 */
public class FileEx extends java.io.File {
	private static final long serialVersionUID = 1L;

	public FileEx(String arg0) {
        super(arg0);
    }

    public FileEx(URI arg0) {
        super(arg0);
    }

    public FileEx(java.io.File arg0, String arg1) {
        super(arg0, arg1);
    }

    public FileEx(String arg0, String arg1) {
        super(arg0, arg1);
    }

	/**
	 * ファイル名を取得する。
	 * @param filename 拡張子付のファイル名
	 * @return 拡張子を省いたファイル名
	 */
	public static String getName(String filename) {
		return new FileEx(filename).getName();
	}

	/**
	 * 拡張子無しのファイル名を取得する。
	 * @return 拡張子を省いたファイル名
	 */
	public String getNameWithoutExtension() {
		String filename = this.getName();
		return getNameWithoutExtension(filename);
	}

	/**
	 * 拡張子無しのファイル名を取得する。
	 * @param filename 拡張子付のファイル名
	 * @return 拡張子を省いたファイル名
	 */
	public static String getNameWithoutExtension(String filename) {
		filename = new FileEx(filename).getName();
		int i = filename.lastIndexOf('.');
		if (i > 0 && i < filename.length() - 1) {
			return filename.substring(0, i);
		}
		return filename;
	}

	/**
	 * 拡張子を取得する。
	 * @return 拡張子
	 */
	public String getExtension() {
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

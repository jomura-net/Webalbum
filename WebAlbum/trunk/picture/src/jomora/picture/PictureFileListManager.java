/**
 * 
 */
package jomora.picture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jomora.io.crypt.CryptUtil;
import jomora.io.crypt.HashUtil;
import jomora.io.image.ThumbnailFactory;

/**
 * 画像ファイルを取り扱うクラス
 * 
 * @author jomora
 */
public class PictureFileListManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(PictureFileListManager.class);
	
	private static final int MAX_WIDTH = 90;
	private static final int MAX_HEIGHT = 90;

	/**
	 * 本クラスのインスタンスを返す。 ServletContext内に登録されていれば、登録されているインスタンスを返す。
	 * 登録されていなければ、新たに生成し、ServletContextに登録する。
	 * @param context サーブレットコンテキスト
	 * @return PictureFileListManagerのインスタンス
	 */
	public static PictureFileListManager getInstance(ServletContext context)
			throws IOException, FileNotFoundException {
		return getInstance(context, false);
	}

	public synchronized static PictureFileListManager getInstance(ServletContext context,
			boolean updateFileList) throws IOException, FileNotFoundException {
		PictureFileListManager pflm = (PictureFileListManager) context.getAttribute("fileListManager");
		if (pflm == null) {
			pflm = new PictureFileListManager();
			context.setAttribute("fileListManager", pflm);
		} else {
			if (updateFileList) {
				pflm.setFileInfoMap();
			}
		}
		return pflm;
	}

	/**
	 * ファイルの拡張子からContentTypeを判別する。 jpeg, gif, png,
	 * bmpに対応。それ以外の拡張子では、ContentTypeを「image」とする。
	 * @param fileName 画像ファイル名
	 * @return コンテントタイプを表す文字列
	 */
	public static String getContentType(String fileName) {
		String extension = jomora.io.File.getExtension(fileName).toLowerCase();
		if (extension.equals("jpg") || extension.equals("jpeg")) {
			return "image/jpeg";
		} else if (extension.equals("gif")) {
			return "image/gif";
		} else if (extension.equals("png")) {
			return "image/png";
		} else if (extension.equals("bmp")) {
			return "image/bmp";
		} else {
			return "image";
		}
	}

	/**
	 * HTTP HeaderとするContentDispositionを取得する。
	 * @param absoluteFilePath ファイルパス
	 * @return ContentDispositionを表す文字列。
	 */
	public static String getContentDisposition(String absoluteFilePath) {
		String fileName = new File(absoluteFilePath).getName();
		return "inline; filename=\"" + fileName + "\"";
	}

	/**
	 * 画像ファイルがある親フォルダ
	 */
	private String pictureDir;

	/**
	 * 画像ファイル情報の集合
	 */
	private Map<String, FileInfo> fileInfoMap = new TreeMap<String, FileInfo>(new FilePathComparator());

	/*
	 * fileInfoMapオブジェクトを格納する一時ファイル名
	 */
	private String tmpFileName;

	/**
	 * コンストラクタ。 newInstance()メソッドから呼び出される。
	 */
	@SuppressWarnings("unchecked")
	private PictureFileListManager() throws FileNotFoundException, IOException {
		long startTime = System.currentTimeMillis();

		setPictureDir();

		//画像ファイル情報をファイルから取得する。
		if (new File(tmpFileName).exists()) {
			try {
				FileInputStream fis = new FileInputStream(tmpFileName);
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.fileInfoMap = (Map<String, FileInfo>)ois.readObject();
				ois.close();
				fis.close();
				log.info("Loaded fileInfoMap from " + tmpFileName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		setFileInfoMap();

		long spanTime = System.currentTimeMillis() - startTime;
		log.info(fileInfoMap.size() + " image files are loaded in " + spanTime + " ms.");
	}

	private void setPictureDir() throws FileNotFoundException, IOException {
		InitialContext context;
		try {
			context = new InitialContext();

			String _pictureDir = (String)context.lookup("java:comp/env/PictureDir");
			File dir = new File(_pictureDir);
			if (!dir.exists()) {
				throw new FileNotFoundException(_pictureDir + "is not found.");
			}
			pictureDir = dir.getAbsolutePath();
		} catch (NamingException e) {
			throw new IOException("The parameter 'PictureDir' can not be read.");
		}
		this.tmpFileName = System.getProperty("java.io.tmpdir") + File.separator + "thumbData_" + HashUtil.md5(pictureDir);
	}

	/**
	 * 画像ファイル情報の集合を取得する。
	 * @return 画像ファイル情報の集合
	 */
	public Map<String, FileInfo> getFileInfoMap() {
		return fileInfoMap;
	}

	/**
	 * ファイルシステム上のファイル情報と、fileInfoMapを同期する。
	 */
	private synchronized void setFileInfoMap() {
		// 存在しないファイルを抹消
		Set<String> keySet1 = this.fileInfoMap.keySet();
		String[] keys = keySet1.toArray(new String[keySet1.size()]);
		for (int i = 0; i < keys.length; i++) {
			String filePath = keys[i];
			removeFileInfo(filePath);
		}

		// 追加されたファイルを登録
		List<String> fileList = getFileList(new File(pictureDir));
		int size = fileList.size();
		for (int i = 0; i < size; i++) {
			putFileInfo(fileList.get(i));
		}
		
		//fileInfoMapをファイル保存
		try {
			FileOutputStream fos = new FileOutputStream(tmpFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(fileInfoMap);
			oos.flush();
			oos.close();
			fos.close();
			log.info("Saved fileInfoMap in " + tmpFileName);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private FileInfo putFileInfo(String filePath) {
		FileInfo fileInfo = null;
		if (!fileInfoMap.containsKey(filePath)) {
			String encFilePath;
			try {
				encFilePath = CryptUtil.simpleEncryptString(filePath);
				log.debug("put: " + filePath);
				fileInfo = fileInfoMap.put(filePath, new FileInfo(encFilePath, createThumbnail(filePath)));
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return fileInfo;
	}

	public FileInfo removeFileInfo(String filePath) {
		FileInfo fileInfo = null;
		File file = new File(getAbsoluteFilePath(filePath));
		if (!file.exists()) {
			log.debug("remove: " + filePath);
			fileInfo = fileInfoMap.remove(filePath);
		}
		return fileInfo;
	}

	/**
	 * ディレクトリ以下のファイル名を再帰的に取得。 取得されるファイル名の拡張子は、jpg, jpeg, bmp, gif, pngに限定。
	 * @param currentDir ディレクトリ
	 * @return ファイルリスト
	 */
	private List<String> getFileList(File currentDir) {
		List<String> currentFileList = new ArrayList<String>();
		File[] files = currentDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowName = name.toLowerCase();
				if (lowName.endsWith("jpg") || lowName.endsWith("jpeg")
						|| lowName.endsWith("bmp") || lowName.endsWith("gif")
						|| lowName.endsWith("png")) {
					return true;
				} else {
					return new File(dir, name).isDirectory();
				}
			}
		});
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				currentFileList.add(files[i].getAbsolutePath().substring(
						pictureDir.length() + 1));
			} else if (files[i].isDirectory()) {
				currentFileList.addAll(getFileList(files[i]));
			}
		}
		return currentFileList;
	}

	/**
	 * ファイルの絶対パスを取得する。
	 * @param filePath ファイルパス
	 * @return ファイルの絶対パス
	 */
	public String getAbsoluteFilePath(String filePath) {
		return pictureDir + File.separator + filePath;
	}

	/**
	 * 画像のサムネイルを取得する。
	 * @param filePath ファイルパス
	 * @return 画像のサムネイル
	 */
	private byte[] createThumbnail(String filePath) {
		byte[] bytes = new byte[0];
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		String absoluteFilePath = getAbsoluteFilePath(filePath);
		String extension = jomora.io.File.getExtension(absoluteFilePath)
				.toLowerCase();

		if (!ThumbnailFactory.isSupportedWriterFormat(extension)) {
			extension = "png";
		}

		try {
			fis = new FileInputStream(absoluteFilePath);
			baos = new ByteArrayOutputStream();
			ThumbnailFactory.createThumbnail(fis, baos, MAX_WIDTH, MAX_HEIGHT, extension);
			bytes = baos.toByteArray();
		} catch (Throwable e) {
			log.error("Can't create a thumbnail of " + filePath, e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				// Do Nothing
			}
		}
		return bytes;
	}

}

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
 * �摜�t�@�C������舵���N���X
 * 
 * @author jomora
 */
public class PictureFileListManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(PictureFileListManager.class);
	
	private static final int MAX_WIDTH = 90;
	private static final int MAX_HEIGHT = 90;

	/**
	 * �{�N���X�̃C���X�^���X��Ԃ��B ServletContext���ɓo�^����Ă���΁A�o�^����Ă���C���X�^���X��Ԃ��B
	 * �o�^����Ă��Ȃ���΁A�V���ɐ������AServletContext�ɓo�^����B
	 * @param context �T�[�u���b�g�R���e�L�X�g
	 * @return PictureFileListManager�̃C���X�^���X
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
	 * �t�@�C���̊g���q����ContentType�𔻕ʂ���B jpeg, gif, png,
	 * bmp�ɑΉ��B����ȊO�̊g���q�ł́AContentType���uimage�v�Ƃ���B
	 * @param fileName �摜�t�@�C����
	 * @return �R���e���g�^�C�v��\��������
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
	 * HTTP Header�Ƃ���ContentDisposition���擾����B
	 * @param absoluteFilePath �t�@�C���p�X
	 * @return ContentDisposition��\��������B
	 */
	public static String getContentDisposition(String absoluteFilePath) {
		String fileName = new File(absoluteFilePath).getName();
		return "inline; filename=\"" + fileName + "\"";
	}

	/**
	 * �摜�t�@�C��������e�t�H���_
	 */
	private String pictureDir;

	/**
	 * �摜�t�@�C�����̏W��
	 */
	private Map<String, FileInfo> fileInfoMap = new TreeMap<String, FileInfo>(new FilePathComparator());

	/*
	 * fileInfoMap�I�u�W�F�N�g���i�[����ꎞ�t�@�C����
	 */
	private String tmpFileName;

	/**
	 * �R���X�g���N�^�B newInstance()���\�b�h����Ăяo�����B
	 */
	@SuppressWarnings("unchecked")
	private PictureFileListManager() throws FileNotFoundException, IOException {
		long startTime = System.currentTimeMillis();

		setPictureDir();

		//�摜�t�@�C�������t�@�C������擾����B
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
	 * �摜�t�@�C�����̏W�����擾����B
	 * @return �摜�t�@�C�����̏W��
	 */
	public Map<String, FileInfo> getFileInfoMap() {
		return fileInfoMap;
	}

	/**
	 * �t�@�C���V�X�e����̃t�@�C�����ƁAfileInfoMap�𓯊�����B
	 */
	private synchronized void setFileInfoMap() {
		// ���݂��Ȃ��t�@�C���𖕏�
		Set<String> keySet1 = this.fileInfoMap.keySet();
		String[] keys = keySet1.toArray(new String[keySet1.size()]);
		for (int i = 0; i < keys.length; i++) {
			String filePath = keys[i];
			removeFileInfo(filePath);
		}

		// �ǉ����ꂽ�t�@�C����o�^
		List<String> fileList = getFileList(new File(pictureDir));
		int size = fileList.size();
		for (int i = 0; i < size; i++) {
			putFileInfo(fileList.get(i));
		}
		
		//fileInfoMap���t�@�C���ۑ�
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
	 * �f�B���N�g���ȉ��̃t�@�C�������ċA�I�Ɏ擾�B �擾�����t�@�C�����̊g���q�́Ajpg, jpeg, bmp, gif, png�Ɍ���B
	 * @param currentDir �f�B���N�g��
	 * @return �t�@�C�����X�g
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
	 * �t�@�C���̐�΃p�X���擾����B
	 * @param filePath �t�@�C���p�X
	 * @return �t�@�C���̐�΃p�X
	 */
	public String getAbsoluteFilePath(String filePath) {
		return pictureDir + File.separator + filePath;
	}

	/**
	 * �摜�̃T���l�C�����擾����B
	 * @param filePath �t�@�C���p�X
	 * @return �摜�̃T���l�C��
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

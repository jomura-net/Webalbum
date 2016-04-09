package jomora.picture.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jomora.io.crypt.CryptUtil;
import jomora.io.crypt.HashUtil;
import jomora.picture.PictureFileListManager;

/**
 * 指定された画像を返す。
 * @version $Id$
 */
public class ViewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(ViewServlet.class);

	private static final int BUFFERSIZE = 512;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession(false);

		String noneMatch = request.getHeader("If-None-Match");
//		String modifiedSince = request.getHeader("If-Modified-Since");

		String efpath = request.getPathInfo().substring(1, request.getPathInfo().lastIndexOf('.'));
		String t = request.getParameter("t");

		ServletContext sc = this.getServletContext();
		PictureFileListManager pflm = PictureFileListManager.getInstance(sc);

		String filePath;
		try {
			filePath = CryptUtil.simpleDecryptString(efpath);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
		String absoluteFilePath = pflm.getAbsoluteFilePath(filePath);
		File fileObj = new File(absoluteFilePath);

		//ファイル存在チェック
		if (!fileObj.exists()) {
			log.warn("Can't find the image file " + absoluteFilePath + ".");
			pflm.removeFileInfo(filePath);
			// throw new FileNotFoundException("Can't find the image file " + filePath + ".");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		long lastModified = fileObj.lastModified();
		String eTag = absoluteFilePath + lastModified;
		if ("1".equals(t)) {
			eTag = "t" + eTag;
		}
		eTag = HashUtil.sha2(eTag);
		if (eTag.equals(noneMatch)) {
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		response.setHeader("Content-Disposition", PictureFileListManager
				.getContentDisposition(absoluteFilePath));
		response.setContentType(PictureFileListManager
				.getContentType(absoluteFilePath));
		response.setDateHeader("Last-Modified", lastModified);
		response.setHeader("ETag", eTag);

		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
			if (!"1".equals(t)) {
				//原寸画像
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(absoluteFilePath);
					byte[] line = new byte[BUFFERSIZE];
					int i;
					while ((i = fis.read(line, 0, BUFFERSIZE)) != -1) {
						sos.write(line, 0, i);
					}
				} finally {
					if (null != fis) {
						fis.close();
					}
				}
			} else {
				//サムネイル
				byte[] bytes = pflm.getFileInfoMap().get(filePath).getThumbnail();
				sos.write(bytes);
			}
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
	}

}

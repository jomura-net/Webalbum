package jomora.picture.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jomora.io.crypt.CryptUtil;
import jomora.picture.PictureFileListManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class for Servlet: ViewServlet
 * 
 */
public class ViewServlet extends javax.servlet.http.HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(ViewServlet.class);

	private static final int BUFFERSIZE = 512;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String efpath = request.getParameter("efpath");
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

		response.setHeader("Content-Disposition", PictureFileListManager
				.getContentDisposition(absoluteFilePath));
		response.setContentType(PictureFileListManager
				.getContentType(absoluteFilePath));
		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
			if (!"1".equals(t)) {
				//ファイル存在チェック
				if (!new File(absoluteFilePath).exists()) {
					log.warn("Can't find the image file " + absoluteFilePath + ".");
					pflm.removeFileInfo(filePath);
					// throw new FileNotFoundException("Can't find the image file " +
					// filePath + ".");
					return;
				}
	
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

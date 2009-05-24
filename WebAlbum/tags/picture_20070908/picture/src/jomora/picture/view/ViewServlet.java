package jomora.picture.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jomora.io.crypt.CryptUtil;
import jomora.picture.PictureFileListManager;
import jomora.picture.schedule.SetFileInfoMapTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class for Servlet: ViewServlet
 * 
 */
public class ViewServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {

	private static final Log log = LogFactory.getLog(ViewServlet.class);

	private static final int BUFFERSIZE = 512;

	@Override
	public void init() throws ServletException {
		super.init();
		
		//開始時刻firstTimeと実行間隔peroidをweb.xmlから取得する。
		String[] FirstTimeParts = {"04", "00", "00"};
		long peroid = 86400000;
		try {
			InitialContext context = new InitialContext();
			String firstTime = (String)context.lookup("java:comp/env/FirstTime");
			FirstTimeParts = firstTime.split(":");
			peroid = ((Long)context.lookup("java:comp/env/Period")).longValue();
		} catch (NamingException e) {
			throw new ServletException("The parameter 'FirstTime' or 'Period' can not be read.", e);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(FirstTimeParts[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(FirstTimeParts[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(FirstTimeParts[2]));
		//開始時刻が過去である場合には、開始時刻を一日遅らせる。
		if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
			cal.add(Calendar.DATE, 1);
		}
		Date firstTime = cal.getTime();

		Timer timer1 = new Timer();
		timer1.scheduleAtFixedRate(new SetFileInfoMapTask(getServletContext()), firstTime, peroid); 
		log("Task scheduled.  firstTime:" + firstTime + "  period:" + peroid + "[ms]");
	}
	
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
		ServletOutputStream sos = response.getOutputStream();
		if (!"1".equals(t)) {
			//ファイル存在チェック
			if (!new File(absoluteFilePath).exists()) {
				log.warn("Can't find the image file " + absoluteFilePath + ".");
				pflm.removeFileInfo(filePath);
				// throw new FileNotFoundException("Can't find the image file " +
				// filePath + ".");
				return;
			}

			FileInputStream fis = new FileInputStream(absoluteFilePath);
			byte[] line = new byte[BUFFERSIZE];
			int i;
			while ((i = fis.read(line, 0, BUFFERSIZE)) != -1) {
				sos.write(line, 0, i);
			}
			fis.close();
		} else {
			byte[] bytes = pflm.getFileInfoMap().get(filePath).getThumbnail();
			sos.write(bytes);
		}
		sos.close();
	}
}

package jomora.picture.view;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jomora.io.File;
import jomora.picture.FileInfo;
import jomora.picture.PictureFileListManager;

/**
 * Servlet implementation class for Servlet: RandomViewServlet
 *
 */
public class RandomViewServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession(false);

		PictureFileListManager pflm = PictureFileListManager.getInstance(getServletContext());
		Map<String,FileInfo> fileInfoMap = pflm.getFileInfoMap();
		
		Set<String> filePathSet = fileInfoMap.keySet();
		int size = filePathSet.size();
		String[] filePaths = filePathSet.toArray(new String[size]);

		boolean canView;
		String filePath;
		int loop_count = size * 2;
		do {
			if (loop_count-- < 0) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			int index = (int)(Math.random() * size);
			filePath = filePaths[index];
			canView = (request.getParameter("adult") != null || filePath.indexOf("@adult") == -1);
		} while(!canView);

		String efpath = fileInfoMap.get(filePath).getEncodeFilePath();

		String url = "view/" + efpath + "." + File.getExtension(filePath);
		
		response.addHeader("PictureFilePath", java.net.URLEncoder.encode(filePath, "UTF-8"));
		//response.sendRedirect(url);
		getServletContext().getRequestDispatcher("/" + url).forward(request, response);
	}
}

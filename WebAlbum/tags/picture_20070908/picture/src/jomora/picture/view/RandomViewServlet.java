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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PictureFileListManager pflm = PictureFileListManager.getInstance(getServletContext());
		Map<String,FileInfo> fileInfoMap = pflm.getFileInfoMap();
		
		Set<String> filePathSet = fileInfoMap.keySet();
		int size = filePathSet.size();
		String[] filePaths = filePathSet.toArray(new String[size]);
		int index = (int)(Math.random() * size);
		
		String filePath = filePaths[index];
		String efpath = fileInfoMap.get(filePath).getEncodeFilePath();

		String url = "view/" + (int)(Math.random() * 1000) + "." + File.getExtension(filePath) + "?efpath=" + efpath;
		//response.sendRedirect(url);
		getServletContext().getRequestDispatcher("/" + url).forward(request, response);
	}
}

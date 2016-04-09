package jomora.net.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jomora.io.File;
import jomora.net.HtmlUtil;
import jomora.picture.FileInfo;
import jomora.picture.PictureFileListManager;

public class ListService {
	public ServletConfig config;
	public ServletContext application;
	public HttpServletRequest request;
	public HttpServletResponse response;
	public HttpSession session;

	public void init() {
	}

	public void destroy() {
	}

	public List<FindResult> find(FindCondition cond) throws Exception {
		if (cond.label == null) {
			cond.label = "";
		}
		String targetCatStr = java.net.URLDecoder.decode(cond.label, "UTF-8");

		boolean withAdult = request.getParameter("adult") != null;

		PictureFileListManager pflm = PictureFileListManager
				.getInstance(application);
		Map<String, FileInfo> fileInfoMap = pflm.getFileInfoMap();
		List<FindResult> results = new ArrayList<FindResult>();

//		int fileCount = 0;
		java.util.Iterator<String> iter = fileInfoMap.keySet().iterator();
		while (iter.hasNext()) {
//			fileCount++;
			String filePath = iter.next();
			boolean canView = withAdult || filePath.indexOf("@adult\\") == -1;
			if (!canView) {
				continue;
			}
			FileInfo fileInfo = (FileInfo) fileInfoMap.get(filePath);
			String encFilePath = fileInfo.getEncodeFilePath();
			String filePath4cat = filePath.replaceFirst("@adult\\\\", "");
			int index = filePath4cat.lastIndexOf(File.separator);
			String catStr = "";
			if (index > 0) {
				catStr = filePath4cat.substring(0, index);
			}

			if (catStr.equals(targetCatStr)) {
				FindResult result = new FindResult();
				result.linkUrl = "view.jsp?efpath=" + encFilePath;
//				result.imageUrl = "view/" + encFilePath + "."
//						+ File.getExtension(filePath) + "?efpath="
//						+ encFilePath + "&amp;t=1";
				result.imageUrl = "view/" + encFilePath + "."
						+ File.getExtension(filePath) + "?t=1";
				result.title = HtmlUtil.HTMLEncode(filePath);
				results.add(result);
			}
		}
		return results;
	}
}

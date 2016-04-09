package jomora.net.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jomora.io.FileEx;
import jomora.net.HtmlUtil;
import jomora.picture.FileInfo;
import jomora.picture.PictureFileListManager;

public class ListService {
	
	public class FindCondition {
		public String label;
	}

	public class FindResult {
		public String linkUrl;
		public String imageUrl;
		public String title;
	}

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

		Iterator<String> iter = fileInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String filePath = iter.next();
			boolean canView = withAdult || filePath.indexOf("@adult\\") == -1;
			if (!canView) {
				continue;
			}
			FileInfo fileInfo = (FileInfo) fileInfoMap.get(filePath);
			String encFilePath = fileInfo.getEncodeFilePath();
			String filePath4cat = filePath.replaceFirst("@adult\\\\", "");
			int index = filePath4cat.lastIndexOf(FileEx.separator);
			String catStr = "";
			if (index > 0) {
				catStr = filePath4cat.substring(0, index);
			}

			if (catStr.equals(targetCatStr)) {
				FindResult result = new FindResult();
				result.linkUrl = "view.jsp?efpath=" + encFilePath;
				result.imageUrl = "view/" + encFilePath + "."
						+ FileEx.getExtension(filePath) + "?t=1";
				result.title = HtmlUtil.HTMLEncode(filePath);
				results.add(result);
			}
		}
		return results;
	}
}

<%@ page pageEncoding="Shift_JIS" contentType="text/html; charset=Shift_JIS" %>
<%@ page import="jomora.picture.*,jomora.io.File,java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Shift_JIS"/>
<title>美しいものは愛でなくちゃ</title>
</head>
<body>
<table border="1"><tr><td>
<%
	boolean updateFileList = request.getParameter("check") != null;
	PictureFileListManager pflm = PictureFileListManager.getInstance(application, updateFileList);
	Map<String,FileInfo> fileInfoMap = pflm.getFileInfoMap();

	String prevCatStr = "";
	java.util.Iterator iter = fileInfoMap.keySet().iterator();
	while(iter.hasNext()) {
	    String filePath = (String)iter.next();
	    FileInfo fileInfo = (FileInfo)fileInfoMap.get(filePath);
		String encFilePath = fileInfo.getEncodeFilePath();
		int index = filePath.indexOf(File.separator);
		String catStr = "";
		if (index > 0) {
			catStr = filePath.substring(0, index);
		}
		if (!prevCatStr.equals(catStr)) {
		    prevCatStr = catStr;
			out.println("</td></tr><tr><th>" + catStr + "</th></tr><tr><td>");
		}
		String url = "view/" + (int)(Math.random() * 1000) + "." + File.getExtension(filePath) + "?efpath=" + encFilePath;
		boolean canViewXXX = request.getParameter("XXX") != null;
		if (filePath.indexOf("XXX") == -1 || canViewXXX) {
%>
<a href="view.jsp?efpath=<%= encFilePath %>"><img src="<%= url %>&t=1" alt="<%= filePath %>" border="0" /></a>
<%
		} else {
%>
<img src="<%= url %>&t=1" alt="<%= filePath %>" border="0" />
<%
		}
	}
%>
</td></tr>
</table>
</body>
</html>

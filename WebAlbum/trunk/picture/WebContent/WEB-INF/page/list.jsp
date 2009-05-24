<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="jomora.picture.*,jomora.io.File,java.util.*" %>
<html>
<head>
<meta name="robots" content="noindex,nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>美しいものは愛でなくちゃ</title>
<style type="text/css">
	a {text-decoration: none;}
</style>
<script type="text/javascript" src="cookie.js"></script>
<script><!--
if (!loadCookie("confirmed") && !confirm("The following contents include sexual expressions.\nAre U over 18 ?")) {
	if (history.length > 0) {
		history.back();
	} else {
		location.href = "/";
	}
} else {
	saveCookie("confirmed", "true");
}
//--></script>
<!-- script type="text/javascript" src="corner.js"></script -->
</head>
<body>

<p align="right"><a href="/wiki/?C%A1%F4%2F%CA%C9%BB%E6%A5%C1%A5%A7%A5%F3%A5%B8%A5%E3">壁紙として利用する</a></p>

<%
	boolean viewCategory = request.getParameter("category") != null;
	if (viewCategory) {
%>
<table border="1"><tr><td>
<%
	}
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
		    String encCatStr = java.net.URLEncoder.encode(catStr, "UTF-8");
		    if (viewCategory) {
				out.println("</td></tr><tr><th><a name=\"" + encCatStr + "\" href=\"#" + encCatStr + "\">" + catStr + "</a></th></tr><tr><td>");
			} else {
				out.println("<a name=\"" + encCatStr + "\" href=\"#" + encCatStr + "\">&nbsp;</a>");
			}
		}
		String url = "view/" + (int)(Math.random() * 1000) + "." + File.getExtension(filePath) + "?efpath=" + encFilePath;
		boolean canViewXXX = request.getParameter("XXX") != null;
		if (filePath.indexOf("XXX") == -1 || canViewXXX) {
%>
<a href="view.jsp?efpath=<%= encFilePath %>"><img src="<%= url %>&t=1" alt="<%= filePath %>" title="<%= filePath %>" border="0" class="corner iradius16 ishade50" /></a>
<%
		} else {
%>
<img src="<%= url %>&t=1" alt="<%= filePath %>" title="<%= filePath %>" border="0" class="corner iradius16 ishade50" />
<%
		}
	}

	if (viewCategory) {
%>
</td></tr></table>
<%
	}
%>

<table align="right"><tr>
	<td>
		category … <a href="?category=1">フォルダ毎表示</a><br />
		<!-- XXX … 詳細表示制限解除<br /> -->
		<!-- check … 最新の情報に更新<br /> -->
	</td>
</tr></table>

</body>
</html>

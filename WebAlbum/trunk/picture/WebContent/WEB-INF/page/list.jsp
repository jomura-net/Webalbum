<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="jomora.picture.*,jomora.io.File,jomora.net.HtmlUtil,java.util.*" %>
<html>
<head>
<meta name="robots" content="noindex,nofollow" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>美しいものは愛でなくちゃ</title>
<style type="text/css">
	a {text-decoration:none;}
	table {width:100%;}
	th {
		color:#fff;
		background-color:#aad;
		border-style:solid;
		border-color:#99c #336 #336 #99c;
		border-width:1px;
	}
	th a {color:#fff;}
	img {border-style:none; vertical-align:middle;}
</style>
<%
	boolean withAdult = request.getParameter("adult") != null;
	if (withAdult) {
%>
<script type="text/javascript" src="cookie.js"></script>
<script type="text/javascript" src="confirm.js"></script>
<%
	}
%>
<%-- script type="text/javascript" src="corner.js"></script --%>
</head>
<body>

<p align="right">
<a href="m&#97;&#105;&#108;&#116;&#111;&#58;&#106;&#111;&#109;o&#114;a&#64;&#106;omor&#97;.n&#101;&#116;?subject=[jomora.net:picture]"><img src="/archive/mail.gif" alt="Mail me!" style="vertical-align:baseline;" /> Mail me!</a>
 &nbsp; &nbsp; &nbsp; &nbsp; 
<a href="/wiki/?C%EF%BC%83%2F%E5%A3%81%E7%B4%99%E3%83%81%E3%82%A7%E3%83%B3%E3%82%B8%E3%83%A3"><img src="/archive/download.gif" alt="Get WallpaperSetter!" style="vertical-align:baseline;" /> Get WallpaperSetter!</a>
</p>

<%
	boolean viewCategory = request.getParameter("list") == null;
	if (viewCategory) {
%>
<table><tr><td>
<%
	}
	boolean updateFileList = request.getParameter("check") != null;
	PictureFileListManager pflm = PictureFileListManager.getInstance(application, updateFileList);
	Map<String,FileInfo> fileInfoMap = pflm.getFileInfoMap();

	String prevCatStr = "";
	int fileCount = 0;
	java.util.Iterator iter = fileInfoMap.keySet().iterator();
	while(iter.hasNext()) {
		fileCount++;
	    String filePath = (String)iter.next();
		boolean canView = withAdult || filePath.indexOf("@adult\\") == -1;
		if (!canView) continue;
	    FileInfo fileInfo = (FileInfo)fileInfoMap.get(filePath);
		String encFilePath = fileInfo.getEncodeFilePath();
		String filePath4cat = filePath.replaceFirst("@adult\\\\", "");
		int index = filePath4cat.lastIndexOf(File.separator);
		String catStr;
		if (index > 0) {
			catStr = filePath4cat.substring(0, index);
		} else {
			catStr = "not classified (Tell me who they are!)";
		}
		if (!prevCatStr.equals(catStr)) {
		    prevCatStr = catStr;
		    String encCatStr = java.net.URLEncoder.encode(catStr, "UTF-8");
		    if (viewCategory) {
				out.println("</td></tr></table><table><tr><th><a name=\"" + encCatStr + "\" href=\"#" + encCatStr + "\">" + catStr + "</a></th></tr><tr><td>");
			} else {
				out.println("<a name=\"" + encCatStr + "\" href=\"#" + encCatStr + "\">&nbsp;</a>");
			}
		}
		String url = "view/" + fileCount + "." + File.getExtension(filePath) + "?efpath=" + encFilePath;
		String htmlEncFilePath = HtmlUtil.HTMLEncode(filePath);
%><a href="view.jsp?efpath=<%= encFilePath %>">
<img src="<%= url %>&amp;t=1" alt="<%= htmlEncFilePath %>" title="<%= htmlEncFilePath %>" <%-- class="corner iradius16 ishade50" --%> />
</a>
<%
	}

	if (viewCategory) {
%>
</td></tr></table>
<%
	}
%>

<table><tr>
	<td align="right">
<%
	String paramAdult = "";
	if (withAdult) {
		paramAdult = "adult";
	}
	if (viewCategory) {
%>
		<a href="?list&<%= paramAdult %>">画像羅列表示</a><br />
<%
	} else {
%>
		<a href="?<%= paramAdult %>">フォルダ毎表示</a><br />
<%
	}
%>
	</td>
</tr></table>

</body>
</html>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>
<%@ page import="jomora.picture.*,jomora.io.File,jomora.net.HtmlUtil,java.util.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="robots" content="noindex,nofollow" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>美しいものは愛でなくちゃ</title>
<link rel="stylesheet" href="style.css" type="text/css" />
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

<div class="options">
<a href="&#109;&#97;&#105;l&#116;&#111;&#58;&#107;az&#64;&#106;omur&#97;&#46;n&#101;&#116;?subject=[jomura.net:picture]"><img src="images/mail.gif" alt="Mail me!" style="vertical-align:baseline;" /> Mail me!</a>
 &nbsp; &nbsp; &nbsp; &nbsp; 
<a href="http://jomura.net/wiki/?C%EF%BC%83%2F%E5%A3%81%E7%B4%99%E3%83%81%E3%82%A7%E3%83%B3%E3%82%B8%E3%83%A3"><img src="images/download.gif" alt="Get WallpaperSetter!" style="vertical-align:baseline;" /> Get WallpaperSetter!</a>
</div>

<div id="options" class="options">
	<a href="javascript:accordion.pr(1)">Expand All</a> | <a href="javascript:accordion.pr(-1)">Collapse All</a>
</div>

<ul class="acc" id="acc">
	<li>
        <h3>操作方法</h3>
        <div class="acc-section">
			<div class="acc-content">
				<noscript><p style="color:red;">Enable JavaScript to view pictures.</p></noscript>
				下のそれぞれをClickすると、それぞれが拡がって表示されます。<br />
				右の「Expand All|Collapse All」は遅いです。動作完了まで、辛抱強く待ってください。
<%
	boolean updateFileList = request.getParameter("check") != null;
	PictureFileListManager pflm = PictureFileListManager.getInstance(application, updateFileList);
	Map<String,FileInfo> fileInfoMap = pflm.getFileInfoMap();

	String prevCatStr = "";
	int fileCount = 0;
	java.util.Iterator<String> iter = fileInfoMap.keySet().iterator();
	while(iter.hasNext()) {
		fileCount++;
	    String filePath = iter.next();
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
			out.println(" </div></div></li><li><h3><a name=\"" + encCatStr
					+ "\" href=\"#" + encCatStr + "\">" + catStr
					+ "</a></h3><div class=\"acc-section\">"
					+ "<div class=\"acc-content\"> ");
//            out.println("\n\t\t\t</div>\n\t\t</div>\n\t</li>\n\t<li>\n\t\t<h3>" + catStr
//                    + "</h3>\n\t\t<div class=\"acc-section\">\n"
//                    + "\t\t\t<div class=\"acc-content\"> ");
		}
		//「画像の非同期呼び出し」はコメントアウトここから
/*
        String url = "view/" + fileCount + "." + File.getExtension(filePath) + "?efpath=" + encFilePath;
        String htmlEncFilePath = HtmlUtil.HTMLEncode(filePath);
%><a href="view.jsp?efpath=<%= encFilePath %>">
<img src="<%= url %>&amp;t=1" alt="<%= htmlEncFilePath %>" title="<%= htmlEncFilePath %>" <%-- class="corner iradius16 ishade50" --%> />
</a>
<%
*/
        //「画像の非同期呼び出し」はコメントアウトここまで
    }
%>
			</div>
		</div>
	</li>
</ul>

<script type="text/javascript" src="jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="script.js"></script>
<script type="text/javascript">
	var accordion = new TINY.accordion.slider("accordion");
	accordion.init("acc","h3",false,0);
</script>

</body>
</html>

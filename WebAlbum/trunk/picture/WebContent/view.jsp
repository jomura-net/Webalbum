<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.drew.metadata.*,jomora.picture.PictureFileListManager,jomora.io.File,jomora.net.HtmlUtil" %>
<html>
<head>
<meta name="robots" content="noindex,nofollow" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%
	String efpath = request.getParameter("efpath");
	String filePath = jomora.io.crypt.CryptUtil.simpleDecryptString(efpath);
	String htmlEncFilePath = HtmlUtil.HTMLEncode(filePath);
	String url = "view/" + (int)(Math.random() * 1000) + "." + File.getExtension(filePath) + "?efpath=" + efpath;
%>
<title><%= htmlEncFilePath %> - 美しいものは愛でなくちゃ</title>
<%
	if (filePath.indexOf("@adult\\") != -1) {
%>
<script type="text/javascript" src="cookie.js"></script>
<script type="text/javascript" src="confirm.js"></script>
<%
	}
%>
<script><!--
var oiw;
function resize() {
	var cw = document.body.clientWidth - 40;
	if (cw < oiw) {
		document.main_image.width = cw;
	} else {
		document.main_image.width = oiw;
	}
	document.getElementById("percent").innerHTML = "(" + Math.floor(document.main_image.width / oiw * 100) + "%)"
}
function sizeToOriginal() {
	if (document.main_image.width != oiw) {
		document.main_image.width = oiw;
		document.getElementById("percent").innerHTML = "(100%)";
	} else {
		resize()
	}
}
window.onload = function() {
	oiw = document.main_image.width;
	resize();
}
window.onresize = resize;
//--></script>
</head>
<body>
<div align="center">
<h4><%= htmlEncFilePath %></h4>
<div style="font-size:60%;text-align:right;"><span id="percent" style="font-size:150%;"></span> &nbsp; &nbsp; 
<script type="text/javascript"><!--
	document.write("画像をクリックすると元のサイズになります。");
// --></script>
<noscript>JavaScriptを有効にすると、画像サイズを調節できます。</noscript>
</div>
<a href="javascript:sizeToOriginal()"><img src="<%= url %>" alt="<%= htmlEncFilePath %>" title="<%= htmlEncFilePath %>" border="0" name="main_image" /></a><br />
<br />
<table border="1">
<%
	PictureFileListManager pflm = PictureFileListManager.getInstance(application);
	String absoluteFilePath = pflm.getAbsoluteFilePath(filePath);
	File jpegFile = new File(absoluteFilePath);
	try {
		Metadata metadata = com.drew.imaging.jpeg.JpegMetadataReader.readMetadata(jpegFile);
		// iterate through metadata directories
		java.util.Iterator directories = metadata.getDirectoryIterator();
		while (directories.hasNext()) {
		    Directory directory = (Directory)directories.next();
		    // iterate through tags and print to System.out
		    java.util.Iterator tags = directory.getTagIterator();
		    while (tags.hasNext()) {
		        Tag tag = (Tag)tags.next();
				try {
			        String desc = "&nbsp;";
			        if (tag.getDescription() != null) {
				        desc = tag.getDescription().trim();
				        desc = desc.length() == 0 ? "&nbsp;" : desc;
			        }
			        out.println("<tr><td>" + tag.getDirectoryName() + " : " + tag.getTagType()
			        		+ "</td><td>" + tag.getTagName() + "</td><td>" + desc + "</td></tr>");
				} catch(com.drew.metadata.MetadataException me) {
					log("Can't read metadata from " + filePath, me);
			        out.println("<tr><td>(can't read)</td><td>(can't read)</td><td>(can't read)</td></tr>");
				}
		    }
		}
	} catch(com.drew.imaging.jpeg.JpegProcessingException e) {
        out.print("<!-- not a JPEG file -->");
	}
%>
</table>
</div>

<br />
<%
    String adult = "";
    if (null != request.getParameter("adult")) {
    	adult = "?adult";
    }
%>
<div style="font-size:80%; text-align:right;"><a href="list<%= adult %>">一覧にイク</a> | <a href="javascript:(window.open('','_top').opener=top).close();">閉じる</a></div>

</body>
</html>

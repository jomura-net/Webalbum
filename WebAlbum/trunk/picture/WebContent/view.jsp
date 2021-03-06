<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ page import="com.drew.metadata.*,jomora.picture.PictureFileListManager,jomora.io.FileEx,jomora.net.HtmlUtil" %>
<html>
<head>
<meta name="robots" content="noindex,nofollow" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%
	String efpath = request.getParameter("efpath");
	String filePath = jomora.io.crypt.CryptUtil.simpleDecryptString(efpath);
	String htmlEncFilePath = HtmlUtil.HTMLEncode(filePath);
	String url = "view/" + efpath + "." + FileEx.getExtension(filePath);
%>
<title><%= htmlEncFilePath %> - 美しいものは愛でなくちゃ</title>
<style>
	table { border:1px solid; border-color:#bbb #555 #555 #bbb; }
	td    { border:1px solid; border-color:#555 #bbb #bbb #555; }
</style>
<%
	if (filePath.indexOf("@adult\\") != -1) {
%>
<script type="text/javascript" src="cookie.js"></script>
<script type="text/javascript" src="confirm.js"></script>
<%
	}
%>
<script>
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
	document.main_image.addEventListener("load", function(e) {
		oiw = document.main_image.width;
		resize();
		window.onresize = resize;
	});
	document.main_image.src = '<%= url %>';
}
</script>
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
<a href="javascript:sizeToOriginal()"><img alt="<%= htmlEncFilePath %>" title="<%= htmlEncFilePath %>" border="0" name="main_image" /></a><br />
<br />
<table>
<%
	PictureFileListManager pflm = PictureFileListManager.getInstance(application);
	String absoluteFilePath = pflm.getAbsoluteFilePath(filePath);
	FileEx jpegFile = new FileEx(absoluteFilePath);
	try {
		Metadata metadata = com.drew.imaging.jpeg.JpegMetadataReader.readMetadata(jpegFile);
		// iterate through metadata directories
		java.util.Iterator<Directory> directories = metadata.getDirectories().iterator();
		while (directories.hasNext()) {
		    Directory directory = directories.next();
		    // iterate through tags and print to System.out
		    java.util.Iterator<?> tags = directory.getTags().iterator();
		    while (tags.hasNext()) {
		        Tag tag = (Tag)tags.next();
// 				try {
			        String desc = "&nbsp;";
			        if (tag.getDescription() != null) {
				        desc = tag.getDescription().trim();
				        desc = desc.length() == 0 ? "&nbsp;" : desc;
			        }
			        out.println("<tr><td>" + tag.getDirectoryName() + " : " + tag.getTagType()
			        		+ "</td><td>" + tag.getTagName() + "</td><td>" + desc + "</td></tr>");
// 				} catch(com.drew.metadata.MetadataException me) {
// 					log("Can't read metadata from " + filePath, me);
// 			        out.println("<tr><td>(can't read)</td><td>(can't read)</td><td>(can't read)</td></tr>");
// 				}
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

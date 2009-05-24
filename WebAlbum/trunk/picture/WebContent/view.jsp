<%@ page language="java" contentType="text/html; charset=Shift_JIS" pageEncoding="Shift_JIS"%>
<%@ page import="com.drew.metadata.*,jomora.picture.PictureFileListManager,jomora.io.File" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Shift_JIS">
<%
	String efpath = request.getParameter("efpath");
	String filePath = jomora.io.crypt.CryptUtil.simpleDecryptString(efpath);
	String url = "view/" + (int)(Math.random() * 1000) + "." + File.getExtension(filePath) + "?efpath=" + efpath;
%>
<title><%= filePath %> - 美しいものは愛でなくちゃ</title>
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
<div  align="center">
<h4><%= filePath %> <span id="percent"></span></h4>
<div style="font-size:60%;text-align:right;">画像をクリックすると元のサイズになります。</div>
<a href="javascript:sizeToOriginal()"><img src="<%= url %>" alt="<%= filePath %>" border="0" name="main_image" /></a><br />
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
		        String desc = "&nbsp;";
		        if (tag.getDescription() != null) {
			        desc = tag.getDescription().trim();
			        desc = desc.length() == 0 ? "&nbsp;" : desc;
		        }
		        out.println("<tr><td>" + tag.getDirectoryName() + " : " + tag.getTagType()
		        		+ "</td><td>" + tag.getTagName() + "</td><td>" + desc + "</td></tr>");
		    }
		}
	} catch(com.drew.imaging.jpeg.JpegProcessingException e) {
        out.print("<!-- not a JPEG file -->");
	}
%>
</table>
</div>

</body>
</html>

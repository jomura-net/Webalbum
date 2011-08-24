<%@ page contentType="text/html;charset=Windows-31J" session="false" %>
<html>
<head>
<title>サムネイル画像の作成</title>
</head>
<body>

<form method="POST" enctype="multipart/form-data" action="maker.jpg" target="thumbnail">
ファイルパス：
<input type="file" name="path" size="75" /><br />
最大幅：
<input type="text" name="mtw" size="6" value="120" /><br />
最大高さ：
<input type="text" name="mth" size="6" value="90" /><br />
<input type="radio" name="type" value="attachment" />attachment - Downloadできます。<br />
<input type="radio" name="type" value="inline" checked="checked" />inline - Previewが表示されます。<br />
<input type="submit" value="サムネイル作成" />
</form>

<iframe name="thumbnail" frameborder="0"></iframe>

</body>
</html>
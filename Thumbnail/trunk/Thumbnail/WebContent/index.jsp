<%@ page contentType="text/html;charset=Windows-31J" session="false" %>
<html>
<head>
<title>�T���l�C���摜�̍쐬</title>
</head>
<body>

<form method="POST" enctype="multipart/form-data" action="maker.jpg" target="thumbnail">
�t�@�C���p�X�F
<input type="file" name="path" size="75" /><br />
�ő啝�F
<input type="text" name="mtw" size="6" value="120" /><br />
�ő卂���F
<input type="text" name="mth" size="6" value="90" /><br />
<input type="radio" name="type" value="attachment" />attachment - Download�ł��܂��B<br />
<input type="radio" name="type" value="inline" checked="checked" />inline - Preview���\������܂��B<br />
<input type="submit" value="�T���l�C���쐬" />
</form>

<iframe name="thumbnail" frameborder="0"></iframe>

</body>
</html>
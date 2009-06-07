function saveCookie(arg1, arg2, arg3, arg4){ //arg1=dataname arg2=data arg3=expiration days
	if (arg1 && arg2) {
		if (arg3) {
			xDay = new Date;
			xDay.setDate(xDay.getDate() + eval(arg3));
			xDay = xDay.toGMTString();
			_exp = ";expires=" + xDay;
		} else {
			_exp ="";
		}
		if (arg4) {
			_path = ";path=" + arg4;
		} else {
			_path = "";
		}
		document.cookie = escape(arg1) + "=" + escape(arg2) + _exp + _path + ";";
	}
}

function loadCookie(arg){ //arg=dataname
	if (arg) {
		cookieData = document.cookie + ";" ;
		arg = escape(arg);
		startPoint1 = cookieData.indexOf(arg);
		startPoint2 = cookieData.indexOf("=", startPoint1) + 1;
		endPoint = cookieData.indexOf(";", startPoint1);
		if (startPoint2 < endPoint && startPoint1 > -1 && startPoint2 - startPoint1 == arg.length + 1) {
			cookieData = cookieData.substring(startPoint2, endPoint);
			cookieData = unescape(cookieData);
			return cookieData
		}
	}
	return false
}

function deleteCookie(arg){ //arg=dataname
	if (arg) {
		arg = escape(arg);
		yDay = new Date;
		yDay.setHours(yDay.getHours() - 1); 
		yDay = yDay.toGMTString(); 
		document.cookie = arg + "=xxx" + ";expires=" + yDay;
	}
}

function chkCookie(arg){ //arg="name" or "value"
	if (document.cookie != "") {
		_cookie = " " + document.cookie;
		cookieData = _cookie.split(";");
		cookieDataName = new Array(cookieData.length);
		cookieDataValue = new Array(cookieData.length);
		for (i = 0; i < cookieData.length; i++) {
			if(cookieData[i].indexOf("=") > -1) {
				cookieDataName[i] = cookieData[i].substring(1, cookieData[i].indexOf("="));
				cookieDataValue[i] = cookieData[i].substring(cookieData[i].indexOf("=") + 1, cookieData[i].length);
			}
		}
		if (arg == "name" || arg == "NAME" || arg == "Name") {
			return cookieDataName;
		}
		if (arg == "value" || arg == "VALUE" || arg == "Value") {
			return cookieDataValue;
		} else {
			return cookieData.length;
		}
	} else if (!arg) {
		return 0;
	}
	return false;
}

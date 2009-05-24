if (!loadCookie("confirmed") && !confirm("The following contents include sexual expressions.\nAre U over 18 ?")) {
	if (history.length > 0) {
		history.back();
	} else {
		location.href = "/";
	}
} else {
	saveCookie("confirmed", "true");
}

package jomora.net;

public class HtmlUtil {
	public static String HTMLEncode(String p_src_str) {
		if (p_src_str == null) {
		    return null;
		} else {
		    return p_src_str.replaceAll("&", "&amp;").
		                     replaceAll("/", "&#47;").
		                     replaceAll("<", "&lt;").
		                     replaceAll(">", "&gt;").
		                     replaceAll("\"", "&quot;").
		                     replaceAll("!", "&#33;").
		                     replaceAll("\\?", "&#63;").
		                     replaceAll("=", "&#61;").
		                     replaceAll("%", "&#37;");
		}
	}
}

package jomora.picture;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

public class FilePathComparator implements Comparator<String>, Serializable {

	private final String rootDirStr = new String(new char[]{(char)0x00});
	
	public int compare(String arg0, String arg1) {
		arg0 = arg0.replaceFirst("@adult\\\\", "");
		arg1 = arg1.replaceFirst("@adult\\\\", "");
		
		int index = arg0.lastIndexOf(File.separator);
		String arg0f = arg0;
		if (index > 0) {
			arg0 = arg0.substring(0, index);
			arg0f = arg0f.substring(index);
		} else {
			arg0 = rootDirStr;
		}
		index = arg1.lastIndexOf(File.separator);
		String arg1f = arg1;
		if (index > 0) {
			arg1 = arg1.substring(0, index);
			arg1f = arg1f.substring(index);
		} else {
			arg1 = rootDirStr;
		}
		if (arg0.compareTo(arg1) != 0) {
			return arg0.compareToIgnoreCase(arg1);
		} else {
			return arg0f.compareToIgnoreCase(arg1f);
		}
	}
}

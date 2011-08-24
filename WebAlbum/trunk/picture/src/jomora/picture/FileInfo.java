package jomora.picture;

import java.io.Serializable;

public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String encodeFilePath;
    private byte[] thumbnail;

	public FileInfo() {
	}

	public FileInfo(String encodeFilePath, byte[] thumbnail) {
        this.encodeFilePath = encodeFilePath;
        this.thumbnail = thumbnail;
    }

    public String getEncodeFilePath() {
		return encodeFilePath;
	}

	public void setEncodeFilePath(String encodeFilePath) {
		this.encodeFilePath = encodeFilePath;
	}

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}

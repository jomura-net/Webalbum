package jomora.picture.schedule;

import java.io.IOException;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import jomora.picture.PictureFileListManager;

public class SetFileInfoMapTask extends TimerTask {

	private ServletContext ctx;
	
	public SetFileInfoMapTask(ServletContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		try {
			PictureFileListManager.getInstance(ctx, true);
		} catch (IOException e) {
			ctx.log(e.getMessage(), e);
		}
	}

}

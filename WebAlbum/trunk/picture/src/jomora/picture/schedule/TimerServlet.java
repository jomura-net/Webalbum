package jomora.picture.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Servlet implementation class TimerServlet
 */
public class TimerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Timer timer1;

	@Override
	public void init() throws ServletException {
		super.init();

		//開始時刻firstTimeと実行間隔peroidをweb.xmlから取得する。
		String[] FirstTimeParts = {"04", "00", "00"};
		long peroid = 86400000;
		String firstTimeStr = getInitParameter("FirstTime");
		if (null != firstTimeStr) {
			FirstTimeParts = firstTimeStr.split(":");
		}
		String periodStr = getInitParameter("Period");
		if (null != periodStr) {
			peroid = Long.parseLong(periodStr);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(FirstTimeParts[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(FirstTimeParts[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(FirstTimeParts[2]));
		//開始時刻が過去である場合には、開始時刻を一日遅らせる。
		if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
			cal.add(Calendar.DATE, 1);
		}
		Date firstTime = cal.getTime();

		timer1 = new Timer();
		timer1.scheduleAtFixedRate(new SetFileInfoMapTask(getServletContext()), firstTime, peroid); 
		log("Task scheduled.  firstTime:" + firstTime + "  period:" + peroid + "[ms]");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (null != timer1) {
			timer1.cancel();
		}
	}
}

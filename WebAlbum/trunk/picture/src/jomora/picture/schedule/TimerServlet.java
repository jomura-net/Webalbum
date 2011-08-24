package jomora.picture.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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

		//�J�n����firstTime�Ǝ��s�Ԋuperoid��web.xml����擾����B
		String[] FirstTimeParts = {"04", "00", "00"};
		long peroid = 86400000;
		try {
			InitialContext context = new InitialContext();
			String firstTime = (String)context.lookup("java:comp/env/FirstTime");
			FirstTimeParts = firstTime.split(":");
			peroid = ((Long)context.lookup("java:comp/env/Period")).longValue();
		} catch (NamingException e) {
			throw new ServletException("The parameter 'FirstTime' or 'Period' can not be read.", e);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(FirstTimeParts[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(FirstTimeParts[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(FirstTimeParts[2]));
		//�J�n�������ߋ��ł���ꍇ�ɂ́A�J�n����������x�点��B
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

package ec2017.rest;

import javax.servlet.ServletException;

import com.ibm.domino.napi.c.xsp.XSPNative;

import lotus.domino.Session;

public class Utils {

	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * 
	 * @param depth
	 *            depth in the call stack (0 means current method, 1 means call
	 *            method, ...)
	 * @return method name
	 */
	public static String getMethodName(final int depth) {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[ste.length - 1 - depth].getMethodName(); // Thank you Tom
															// Tresansky
	}

	public final static String getCurrentMethod() {
		return getMethodName(0);
	}

	/**
	 * schreibt ThreadId auf Konsole
	 */
	public static void dumpThredInfo() {
		try {
			System.out.println("Thread Id: " + Thread.currentThread().getId() + " // Method: " + getCurrentMethod());
		} catch (Exception e) {
		}

	}

	public static void logToConsole(final String caller, final String message) {
		System.out.println("[" + caller + "]: " + message);
	}

	public static <T> T recycle(lotus.domino.Base obj) {

		try {
			obj.recycle();
		} catch (Exception e) {
		}
		obj = null;

		return null;

	}

	/**
	 * create a new Domino session for the give username
	 * 
	 * @param userName
	 *            String containing the canonical username
	 * @return lotus.domino.Session for the given username
	 * @throws ServletException
	 */
	public static Session createUserSession(final String userName) throws ServletException {
		Session session = null;
		try {
			// long hList = NotesUtil.createUserNameList(userName);
			session = XSPNative.createXPageSession(userName, 0L, false, true);

			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return session;
	}

	/**
	 * tests if the given String is empty or not
	 * 
	 * @param toTest
	 *            the String
	 * @return boolean the result
	 */
	public static boolean isEmpty(final String toTest) {
		if (toTest == null)
			return true;

		if ("".equals(toTest))
			return true;

		return false;
	}
}

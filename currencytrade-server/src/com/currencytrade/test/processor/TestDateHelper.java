package com.currencytrade.test.processor;

import java.util.Date;

public class TestDateHelper {

	@org.junit.Test
	public void testDateHelperToString() {
		Date currentDate = new Date();

		// String dateString = DateHelper.getString(currentDate);
		// assert dateString != null : "testDateHelperToString: FAILED";

	}

	@org.junit.Test
	public void testDateHelperToDate() {
		String dateString = "24-JAN-15 10:27:44";
		Date date;
		// try {
		// date = DateHelper.getDate(dateString);
		// assert date != null : "testDateHelperToDate: FAILED";
		// } catch (ParseException e) {
		// assert false : "testDateHelperToDate: FAILED";
		// e.printStackTrace();
		// }

	}

}

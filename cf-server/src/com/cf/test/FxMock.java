package com.cf.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cf.model.Currency;
import com.cf.model.Fx;

public enum FxMock {
	INSTANCE;

	private List<Fx> content = new ArrayList<Fx>();

	FxMock() {
		content.add(new Fx(1234, Currency.EUR, Currency.GBP, 100.00, 75.00,
				new Date(), 0.75, "IE"));
		content.add(new Fx(1235, Currency.EUR, Currency.GBP, 100.00, 75.00,
				new Date(), 0.75, "UK"));
		content.add(new Fx(1234, Currency.EUR, Currency.GBP, 200.00, 150.00,
				new Date(), 0.75, "IE"));
		content.add(new Fx(1235, Currency.EUR, Currency.GBP, 120.00, 83.00,
				new Date(), 0.75, "UK"));
		content.add(new Fx(1236, Currency.GBP, Currency.EUR, 100.00, 75.00,
				new Date(), 0.75, "DE"));
	}

	public List<Fx> getContent() {
		return content;
	}
}

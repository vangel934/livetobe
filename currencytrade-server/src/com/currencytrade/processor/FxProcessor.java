package com.currencytrade.processor;

import com.currencytrade.model.Fx;

public interface FxProcessor {
	public void processFx(Fx fx)
			throws com.currencytrade.processor.exception.ProcessingException;
}

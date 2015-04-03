package com.cf.processor;

import com.cf.model.Fx;

public interface FxProcessor {
	public void processFx(Fx fx)
			throws com.cf.processor.exception.ProcessingException;
}

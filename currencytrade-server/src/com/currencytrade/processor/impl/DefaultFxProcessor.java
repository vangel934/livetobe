package com.currencytrade.processor.impl;

import javax.ws.rs.ProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.currencytrade.model.Fx;
import com.currencytrade.processor.FxProcessor;

public class DefaultFxProcessor implements FxProcessor {

	private static volatile FxProcessor INSTANCE = new DefaultFxProcessor();

	public static FxProcessor getInstance() {
		return INSTANCE;
	}

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void processFx(Fx fx)
			throws com.currencytrade.processor.exception.ProcessingException {
		try {
			// TODO constant execution....do it in executor
			ResourceHandler.INSTANCE.requestCSVWrite(fx);

			// } catch (IOException e) {
			// log.error(e.getMessage(), e);
			// throw new ProcessingException("fx.process.fault.write");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ProcessingException("fx.process.fault.general");
		}
	}

}

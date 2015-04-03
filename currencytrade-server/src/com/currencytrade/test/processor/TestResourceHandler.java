package com.currencytrade.test.processor;

import java.util.List;

import org.junit.Test;

import com.currencytrade.model.Fx;
import com.currencytrade.processor.impl.ResourceHandler;
import com.currencytrade.test.FxMock;

public class TestResourceHandler {

	@Test
	public void testWriting() {
		for (Fx fx : FxMock.INSTANCE.getContent()) {
			try {
				ResourceHandler.INSTANCE.requestCSVWrite(fx);

			} catch (Exception e) {
				assert false : "testWritingFx : FAILED";
				e.printStackTrace();
			}
		}

		try {
			// TimeUnit.SECONDS.wait(120);
			Thread.sleep(120 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testReading() {
		try {
			List<Fx> listFx = ResourceHandler.INSTANCE.readFromCSV();
			if (listFx == null) {
				assert false : "testReading : FAILED";
			}
			for (Fx fx : listFx) {
				System.out.println("FX:\n");
				System.out.println(fx.toString());
			}
		} catch (Exception e) {
			assert false : "testReading : FAILED";
		e.printStackTrace();
		}

	}
}

package com.cf.test.remote.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cf.remote.rest.FxService;
import com.cf.remote.rest.client.DefaultFxRestClient;
import com.cf.remote.rest.client.FxRestClient;
import com.cf.remote.rest.impl.RestFxService;
import com.cf.test.FxMock;

public class TestFxService {

	private static FxService service;
	private static FxMock fxMock;
	private static FxRestClient client;

	@BeforeClass
	public static void init() {
		service = new RestFxService();
		client = new DefaultFxRestClient();
		fxMock = FxMock.INSTANCE;

	}

	@Before
	public void waitBeforeExecution() {
		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFxService() {
		ExecutorService executor = Executors.newFixedThreadPool(20);

		for (int i = 0; i < 20; i++) {
			final int execCount = i + 1;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("Making " + execCount + ". call...");
						Response response = service.fx("user1", "user1", fxMock
								.getContent().get(0));
						System.out.println("" + execCount + ". call : "
								+ response.getStatus());
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}

		executor.shutdown();

		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRestFxService() {
		ExecutorService executor = Executors.newFixedThreadPool(10);

		for (int i = 0; i < 10; i++) {
			final int execCount = i + 1;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("Making " + execCount + ". call...");
						Response response = client.fx("user1", "user1", fxMock
								.getContent().get(0));
						System.out.println("" + execCount + ". call : "
								+ response.getStatus());
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			});
		}

		executor.shutdown();

		try {
			Thread.sleep(300 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

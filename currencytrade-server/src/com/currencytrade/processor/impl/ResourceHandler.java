package com.currencytrade.processor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.currencytrade.model.Currency;
import com.currencytrade.model.Fx;

public enum ResourceHandler {
	INSTANCE;

	// should be handled by property or system argument
	private static final int WRITER_DELAY = 10;
	private static final int WRITER_PERIOD = 30;

	private static final char CSV_SEPARATOR = ',';
	private static final int INDEX_USERID = 0;
	private static final int INDEX_CURRENCYFROM = 1;
	private static final int INDEX_CURRENCYTO = 2;
	private static final int INDEX_AMOUNTBUY = 3;
	private static final int INDEX_AMOUNTSELL = 4;
	private static final int INDEX_RATE = 5;
	private static final int INDEX_TIMEPLACED = 6;
	private static final int INDEX_COUNTRY = 7;

	private static final Object FILE_LOCK = new Object();
	private static final Object FX_LOCK = new Object();
	private volatile File fxFile;
	private List<Fx> pendingFx = new ArrayList<>();
	private Logger log = LoggerFactory.getLogger(getClass());

	ResourceHandler() {
		startPeriodicWriter();
	}

	private File getFxFile() throws IOException {
		if (fxFile == null) {
			synchronized (FILE_LOCK) {
				if (fxFile == null) {
					String fxFilePath = PropertyReader.INSTANCE
							.readFxLocation();
					if (fxFilePath != null) {
						fxFile = new File(fxFilePath);
						if (!fxFile.exists()) {
							// get and create dir
							File dir = fxFile.getParentFile();
							dir.mkdirs();

							// create new file
							fxFile.createNewFile();
							fxFile.setWritable(true);
						}

					}
				}
			}
		}
		return fxFile;

	}

	public void requestCSVWrite(Fx fx) {
		synchronized (FX_LOCK) {
			pendingFx.add(fx);
		}
	}

	private class PeriodicWriterThread implements Runnable {
		@Override
		public void run() {
			synchronized (FX_LOCK) {
				File origFile = null;
				File copyFile = null;
				try {
					// 1. get orig file
					origFile = getFxFile();
					// 2. defensive copy
					copyFile = FileHandler.copyFile(origFile, null);
					// 3. alter original
					if (!pendingFx.isEmpty()) {
						writeToCSV(pendingFx);
					}

					// clear it
					pendingFx.clear();

					// delete copy
					FileHandler.deleteFile(copyFile);

				} catch (IOException e) {
					e.printStackTrace();
					// log it
					log.error("writeToCSV.failed", e);
					// if exception return copy, delete original, do not empty
					// pendingFx

					try {
						FileHandler.copyFile(copyFile, origFile);
						FileHandler.deleteFile(copyFile);
					} catch (IOException e1) {
						e1.printStackTrace();
						log.error("writeToCSV.exceptionHandling.failed", e);
					}

				}
			}
		}

	}

	private void startPeriodicWriter() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		// ScheduledFuture<?> result
		executor.scheduleAtFixedRate(new PeriodicWriterThread(), WRITER_DELAY,
				WRITER_PERIOD, TimeUnit.SECONDS);

		// executor.shutdown();
	}

	private static class FileHandler {

		public static void deleteFile(File file) throws IOException {
			if (file == null) {
				// throw new IOException("deleteFile.file.missing");
				return;
			}
			Files.deleteIfExists(file.toPath());
		}

		public static File copyFile(File sourceFile, File destFile)
				throws IOException {
			if (sourceFile == null) {
				throw new IOException("copyFile.sourceFile.missing");
			}

			if (destFile == null) {
				destFile = new File(sourceFile.getParent() + "/tmpFx.csv");
			}

			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			FileChannel source = null;
			FileChannel destination = null;

			try {
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
			} finally {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			}

			return destFile;
		}
	}

	public void writeToCSV(List<Fx> listFx) throws IOException {

		// TODO opening and closing stream constantly...avoid it!!

		CsvWriter writer = new CsvWriter(
				new FileOutputStream(getFxFile(), true), CSV_SEPARATOR,
				Charset.forName("UTF-8"));

		String[] record = new String[8];
		for (Fx fx : listFx) {
			record[INDEX_USERID] = String.valueOf(fx.getUserId());
			record[INDEX_CURRENCYFROM] = fx.getCurrencyFrom().name();
			record[INDEX_CURRENCYTO] = fx.getCurrencyTo().name();
			record[INDEX_AMOUNTBUY] = String.valueOf(fx.getAmountBuy());
			record[INDEX_AMOUNTSELL] = String.valueOf(fx.getAmountSell());
			record[INDEX_RATE] = String.valueOf(fx.getRate());
			record[INDEX_TIMEPLACED] = DateHelper.getString(fx.getTimePlaced());
			record[INDEX_COUNTRY] = fx.getOriginatingCountry();

			writer.writeRecord(record);
		}

		// writer.endRecord();
		writer.flush();
		writer.close();

	}

	public List<Fx> readFromCSV() throws IOException {
		CsvReader reader = new CsvReader(new FileInputStream(getFxFile()),
				CSV_SEPARATOR, Charset.forName("UTF-8"));

		List<Fx> list = new ArrayList<>();

		while (reader.readRecord()) {
			Fx fx = new Fx();
			fx.setUserId(Long.valueOf(reader.get(INDEX_USERID)));
			fx.setCurrencyFrom(Currency.valueOf(reader.get(INDEX_CURRENCYFROM)));
			fx.setCurrencyTo(Currency.valueOf(reader.get(INDEX_CURRENCYTO)));
			fx.setAmountBuy(Double.valueOf(reader.get(INDEX_AMOUNTBUY)));
			fx.setAmountSell(Double.valueOf(reader.get(INDEX_AMOUNTSELL)));
			fx.setRate(Double.valueOf(reader.get(INDEX_RATE)));
			fx.setOriginatingCountry(reader.get(INDEX_COUNTRY));

			try {
				fx.setTimePlaced(DateHelper.getDate(reader
						.get(INDEX_TIMEPLACED)));
			} catch (ParseException e) {
				// ignore it to keep it simple for now...
				// TODO -> handle date parsing exception
				e.printStackTrace();
			}

			list.add(fx);
		}

		return list;
	}

	private static class DateHelper {
		// 24-JAN-15 10:27:44
		private static final String PATTERN = "dd-MM-yy kk:mm:ss";
		private static final DateFormat sdf = new SimpleDateFormat(PATTERN,
				Locale.ENGLISH);

		static Date getDate(String date) throws ParseException {
			return sdf.parse(date);
		}

		static String getString(Date date) {

			return sdf.format(date);
		}

	}

}

package com.currencytrade.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Fx implements Serializable {
	private static final long serialVersionUID = 2865644187663183023L;

	// {"userId": "134256", "currencyFrom": "EUR", "currencyTo": "GBP",
	// "amountSell": 1000, "amountBuy": 747.10, "rate": 0.7471, "timePlaced" :
	// "24-JAN-15 10:27:44", "originatingCountry" : "FR"}
	private long userId;
	private Currency currencyFrom;
	private Currency currencyTo;
	private double amountSell;
	private double amountBuy;
	private Date timePlaced;
	private double rate;
	private String originatingCountry;

	public Fx() {

	}

	public Fx(long userId, Currency currencyFrom, Currency currencyTo,
			double amountSell, double amountBuy, Date timePlaced, double rate,
			String originatingCountry) {
		super();
		this.userId = userId;
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.amountSell = amountSell;
		this.amountBuy = amountBuy;
		this.timePlaced = timePlaced;
		this.rate = rate;
		this.originatingCountry = originatingCountry;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Currency getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(Currency currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public Currency getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(Currency currencyTo) {
		this.currencyTo = currencyTo;
	}

	public double getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(double amountSell) {
		this.amountSell = amountSell;
	}

	public double getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(double amountBuy) {
		this.amountBuy = amountBuy;
	}

	public Date getTimePlaced() {
		return timePlaced;
	}

	public void setTimePlaced(Date timePlaced) {
		this.timePlaced = timePlaced;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getOriginatingCountry() {
		return originatingCountry;
	}

	public void setOriginatingCountry(String originatingCountry) {
		this.originatingCountry = originatingCountry;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:").append(getUserId()).append("\n");
		sb.append("from:").append(getCurrencyFrom()).append("\n");
		sb.append("to:").append(getCurrencyTo()).append("\n");
		sb.append("buy:").append(getAmountBuy()).append("\n");
		sb.append("sell:").append(getAmountSell()).append("\n");
		sb.append("rate:").append(getRate()).append("\n");
		sb.append("time:").append(getTimePlaced()).append("\n");
		sb.append("country:").append(getOriginatingCountry()).append("\n");

		return sb.toString();
	}
}

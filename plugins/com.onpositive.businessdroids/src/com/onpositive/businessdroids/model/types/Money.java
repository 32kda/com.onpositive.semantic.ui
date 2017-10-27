package com.onpositive.businessdroids.model.types;

import java.util.Currency;

public class Money {
	double amount;
	Currency currency;

	public Money(double amount, Currency currency) {
		super();
		this.amount = amount;
		this.currency = currency;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}

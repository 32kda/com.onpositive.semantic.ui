package com.onpositive.businessdroids.ui.dataview.renderers;

import java.text.NumberFormat;
import java.util.Currency;

import com.onpositive.businessdroids.model.types.Money;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.IViewer;


public class CurrencyRenderer extends StringRenderer {

	@Override
	public CharSequence getStringFromValue(Object fieldValue,
			IViewer tableModel, Object object) {
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
		if (fieldValue instanceof Number) {
			return currencyInstance.format(fieldValue);
		}
		if (fieldValue instanceof Money) {
			Money mn = (Money) fieldValue;
			double amount = mn.getAmount();
			Currency currency = mn.getCurrency();
			currencyInstance.setCurrency(currency);
			currencyInstance.setGroupingUsed(true);
			return currencyInstance.format(amount) + " " + currency.getSymbol();
		}
		return super.getStringFromValue(fieldValue, tableModel, object);
	}

}

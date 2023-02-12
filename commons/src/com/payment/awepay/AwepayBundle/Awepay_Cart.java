package com.payment.awepay.AwepayBundle;

import java.util.*;

public class Awepay_Cart extends Awepay_ApiElementBase {

	public Awepay_CartSummary summary;
	public List<Awepay_CartItem> items;

	public Awepay_Cart() {
		this.summary = new Awepay_CartSummary();
		this.items = new ArrayList<Awepay_CartItem>();
	}

	protected boolean validate() {
		if (this.items.isEmpty()) {
			this.addError("Must have at least 1 cart item.");
			return false;
		}
		for (int i = 0; i < this.items.size(); i++) {
			if (!this.items.get(i).validate()) {
				this.addErrors("CartItem[".concat(Integer.toString(i)).concat("]"), this.items.get(i).getErrors());
			}
		}
		if (!this._errors.isEmpty()) {
			return false;
		}
		return true;
	}

	public void addItem(String name, String quantity, String amount, String sku, String description) {
		Awepay_CartItem item = new Awepay_CartItem();
		item.name = name;
		item.quantity = quantity;
		item.amount_unit = String.format("%01.2f", Float.parseFloat(amount));
		item.item_no = sku;
		item.item_desc = description;
		this.items.add(item);

		this.summary.amount_purchase = String.format("%01.2f", (Float) (Float.parseFloat(this.summary.amount_purchase) + (Integer.parseInt(quantity) * Float.parseFloat(amount))));
		this.summary.quantity = ((Integer) (Integer.parseInt(this.summary.quantity) + Integer.parseInt(quantity))).toString();
	}

}

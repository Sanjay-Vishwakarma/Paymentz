package com.payment.awepay.AwepayBundle;

import java.lang.reflect.*;

public class Awepay_CartItem extends Awepay_ApiElementBase {

	public String name;
	public String quantity;
	public String amount_unit;
	public String item_no;
	public String item_desc;

	protected boolean validate() {
		String[] required_fields = {"name", "quantity", "amount_unit"};
		for (int i = 0; i < required_fields.length; i++) {
			try {
				Field field = this.getClass().getField(required_fields[i].toString());
				if (((String) field.get(this)).isEmpty()) {
					this.addError("`".concat(field.getName()).concat("` is required."));
				}
			} catch (Exception e) {
				continue;
			}
		}
		if (!this._errors.isEmpty()) {
			return false;
		}
		return true;
	}

}

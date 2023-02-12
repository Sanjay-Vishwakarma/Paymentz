package com.payment.awepay.AwepayBundle;

import java.util.*;
import java.lang.reflect.*;

public class Awepay_PaymentDetails extends Awepay_ApiElementBase {

	public String payby;
	public String card_name;
	public String card_no;
	public String card_ccv;
	public String card_exp_month;
	public String card_exp_year;
	public String md;
	public String redirecturl;
	public String useragent;
	public String browseragent;
	public String routing_no;
	public String account_no;
	public String type;
	public String regulation_e;
	public String className;
	public String receive_name;
	public String issue_no;
	public String start_date_month;
	public String start_date_year;
	public String schedule_id;
	protected String[] _required_fields;

	public boolean validate() {
		if (!(this.payby == null || this.payby.isEmpty()) && (this.payby.equals("ach") || this.payby.equals("check21"))) {
			this._required_fields = new String[]{"routing_no", "account_no", "type", "regulation_e", "class", "receive_name"};
		} else {
			this._required_fields = new String[]{"card_name", "card_no", "card_ccv", "card_exp_month", "card_exp_year"};
		}
		for (int i = 0; i < this._required_fields.length; i++) {
			try {
				Field field = this.getClass().getField(this._required_fields[i].toString());
				if (field.get(this) == null || ((String) field.get(this)).isEmpty()) {
					this.addError("`" + field.getName() + "` is required.");
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

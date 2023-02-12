package com.payment.awepay.AwepayBundle;

import java.util.*;
import java.lang.reflect.*;

public class Awepay_UserDetails extends Awepay_ApiElementBase {

	public String username;
	public String password;
	public String firstname;
	public String lastname;
	public String email;
	public String phone;
	public String mobile;
	public String address;
	public String suburb_city;
	public String state;
	public String country;
	public String postcode;
	public String ship_firstname;
	public String ship_lastname;
	public String ship_address;
	public String ship_suburb_city;
	public String ship_state;
	public String ship_country;
	public String ship_postcode;
	public String bank_name;
	public String bank_phone;
	public String ssn;
	public String dl;
	public String dob;
	public String uip;

	protected boolean validate() {
		String[] required_fields = {"firstname", "lastname", "email", "phone", "address", "suburb_city", "state", "country", "postcode"};
		for (int i = 0; i < required_fields.length; i++) {
			try {
				Field field = this.getClass().getField(required_fields[i].toString());
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

	@Override
	public Map<String, Object> toArray() {
		String[] ship_fields = {"ship_firstname", "ship_lastname", "ship_address", "ship_suburb_city", "ship_state", "ship_postcode", "ship_country"};
		for (int i = 0; i < ship_fields.length; i++) {
			try {
				Field field = this.getClass().getField(ship_fields[i].toString());
				if (field.get(this) == null || ((String) field.get(this)).isEmpty()) {
					field.set(this, this.getClass().getField(field.getName().replaceAll("ship_", "")).get(this));
				}
			} catch (Exception e) {}
		}
		if (this.uip == null || this.uip.isEmpty()) {
			this.uip = "127.0.0.1";
		}
		return super.toArray();
	}

}

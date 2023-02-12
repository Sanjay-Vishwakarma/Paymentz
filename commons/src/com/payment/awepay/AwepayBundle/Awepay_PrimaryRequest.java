package com.payment.awepay.AwepayBundle;

import java.util.*;

abstract public class Awepay_PrimaryRequest extends Awepay_ApiBase {

	public Awepay_UserDetails udetails;
	public Awepay_PaymentDetails paydetails;
	public Awepay_Cart cart;
	public Awepay_TransactionParameters txparams;

	public Awepay_PrimaryRequest() {
		this.udetails = new Awepay_UserDetails();
		this.paydetails = new Awepay_PaymentDetails();
		this.cart = new Awepay_Cart();
		this.txparams = new Awepay_TransactionParameters();
	}

	protected boolean validate() {
		if (this.sid == null || this.sid.isEmpty()) {
			this.addError("`sid` is required.");
		}

		if (this.rcode == null || this.rcode.isEmpty()) {
			this.addError("`rcode` is required.");
		}

		if (!this.udetails.validate()) {
			this.addErrors("Udetails", this.udetails.getErrors());
		}

		if (!this.paydetails.validate()) {
			this.addErrors("Paydetails", this.paydetails.getErrors());
		}

		if (!this.cart.validate()) {
			this.addErrors("Cart", this.cart.getErrors());
		}

		if (!this.txparams.validate()) {
			this.addErrors("Txparams", this.txparams.getErrors());
		}

		if (!this._errors.isEmpty()) {
			return false;
		}

		return true;
	}

	public void setUsername(String username) {
		this.udetails.username = username;
	}

	public void setPassword(String password) {
		this.udetails.password = password;
	}

	public void setFirstName(String name) {
		this.udetails.firstname = name;
	}

	public void setLastName(String name) {
		this.udetails.lastname = name;
	}

	public void setEmail(String email) {
		this.udetails.email = email;
	}

	public void setPhoneNumber(String phone) {
		phone = phone.replaceAll("[^0-9\\+#]", "");
		this.udetails.phone = phone;
	}

	public void setMobileNumber(String phone) {
		phone = phone.replaceAll("[^0-9\\+#]", "");
		this.udetails.mobile = phone;
	}

	public void setBillingAddress(String address) {
		address = address.replaceAll("(?i)[^0-9a-z /\\-]", "");
		this.udetails.address = address;
	}

	public void setBillingCity(String city) {
		this.udetails.suburb_city = city;
	}

	public void setBillingState(String state) {
		if (state.length() > 3) {
			System.out.println("The `state` field only accepts 2 and 3 character state codes as per the ISO standard");
		}
		this.udetails.state = state.toUpperCase();
	}

	public void setBillingCountry(String country) {
		if (country.length() != 2) {
			System.out.println("The `country` field only accepts the 2 character international country codes");
		}
		this.udetails.country = country.toUpperCase();
	}

	public void setBillingPostCode(String postcode) {
		this.udetails.postcode = postcode;
	}

	public void setShippingFirstName(String name) {
		this.udetails.ship_firstname = name;
	}

	public void setShippingLastName(String name) {
		this.udetails.ship_lastname = name;
	}

	public void setShippingAddress(String address) {
		address = address.replaceAll("(?i)[^0-9a-z /\\-]", "");
		this.udetails.ship_address = address;
	}

	public void setShippingCity(String city) {
		this.udetails.ship_suburb_city = city;
	}

	public void setShippingState(String state) {
		if (state.length() > 3) {
			System.out.println("The `shipping state` field only accepts 2 and 3 character state codes as per the ISO standard");
		}
		this.udetails.ship_state = state.toUpperCase();
	}

	public void setShippingCountry(String country) {
		if (country.length() != 2) {
			System.out.println("The `shipping country` field only accepts the 2 character international country codes");
		}
		this.udetails.ship_country = country.toUpperCase();
	}

	public void setShippingPostCode(String postcode) {
		this.udetails.ship_postcode = postcode;
	}

	public void setBankName(String name) {
		this.udetails.bank_name = name;
	}

	public void setBankPhoneNumber(String phone) {
		phone = phone.replaceAll("[^0-9\\+#]", "");
		this.udetails.bank_phone = phone;
	}

	public void setSocialSecurityNumber(String ssn) {
		this.udetails.ssn = ssn;
	}

	public void setDriversLicenceNumber(String dl) {
		this.udetails.dl = dl;
	}

	public void setDateOfBirth(String dob) {
		if (!dob.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
			System.out.println("Invalid date of birth supplied");
		}
		this.udetails.dob = dob;
	}

	public void setCustomerIPAddress(String ip) {

		if (!ip.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
			System.out.println("Customer IP Address supplied was invalid");
		}
		this.udetails.uip = ip;
	}

	public void setPaymentType(String type) {
		this.paydetails.payby = type.toLowerCase();
	}

	public void setCardName(String name) {
		this.paydetails.card_name = name;
	}

	public void setCardNumber(String number) {
		this.paydetails.card_no = number.replaceAll("[^0-9]", "");
		switch (number.substring(0, 1)) {
			case "4":
				this.setPaymentType(Awepay_ApiBase.VISA);
				break;
			case "5":
				this.setPaymentType(Awepay_ApiBase.MASTERCARD);
				break;
			default:
				break;
		}
	}

	public void setCardCVV(String cvv) {
		this.paydetails.card_ccv = cvv;
	}

	public void setCardExpiryMonth(String month) {
		if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
			System.out.print("Card expiry month was set outside the acceptible limits");
		}
		this.paydetails.card_exp_month = this.padLeft(month, 2, "0");
	}

	public void setCardExpiryYear(String year) {
		if (year.length() == 2) {
			year = "20".concat(year);
		}

		if (Integer.parseInt(year) < Calendar.getInstance().get(Calendar.YEAR)) {
			System.out.println("Card expiry year has already expired");
		}
		this.paydetails.card_exp_year = year;
	}

	public void setMD(String md) {
		this.paydetails.md = md;
	}

	public void setRedirectUrl(String url) {
		this.paydetails.redirecturl = url;
	}

	public void setUserAgent(String agent) {
		this.paydetails.useragent = agent;
	}

	public void setBrowserAccepts(String accepts) {
		this.paydetails.browseragent = accepts;
	}

	public void setRoutingNumber(String routing) {
		this.paydetails.routing_no = routing.replaceAll("[^0-9]", "");
	}

	public void setAccountNumber(String account) {
		this.paydetails.account_no = account.replaceAll("[^0-9]", "");
	}

	public void setChequeType(String type) {
		this.paydetails.type = type;
	}

	public void setRegulationE(boolean e) {
		this.paydetails.regulation_e = e ? "1" : "0";
	}

	public void setClassName(String className) {
		this.paydetails.className = className;
	}

	public void setReceiverName(String name) {
		this.paydetails.receive_name = name;
	}

	public void setIssueNumber(String number) {
		this.paydetails.issue_no = number;
	}

	public void setStartDateMonth(String month) {
		if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
			System.out.println("Start date month was set outside the acceptible limits");
		}
		this.paydetails.start_date_month = this.padLeft(month, 2, "0");
	}

	public void setStartDateYear(String year) {
		if (year.length() == 2) {
			year = "20".concat(year);
		}

		if (Integer.parseInt(year) < Calendar.getInstance().get(Calendar.YEAR)) {
			System.out.println("Start date year has already expired");
		}
		this.paydetails.start_date_year = year;
	}

	public void setScheduleId(String id) {
		this.paydetails.schedule_id = id;
	}

	public void addItem(String name, String quantity, String amount) {
		this.cart.addItem(name, quantity, amount, "", "");
	}

	public void addItem(String name, String quantity, String amount, String sku) {
		this.cart.addItem(name, quantity, amount, sku, "");
	}

	public void addItem(String name, String quantity, String amount, String sku, String description) {
		this.cart.addItem(name, quantity, amount, sku, description);
	}

	public void setWID(String wid) {
		this.txparams.wid = wid;
	}

	public void setTID(String tid) {
		this.txparams.tid = tid;
	}

	public void setReference1(String ref) {
		this.txparams.ref1 = ref;
	}

	public void setReference2(String ref) {
		this.txparams.ref2 = ref;
	}

	public void setReference3(String ref) {
		this.txparams.ref3 = ref;
	}

	public void setReference4(String ref) {
		this.txparams.ref4 = ref;
	}

	public void setPostbackUrl(String url) {
		this.txparams.postbackurl = url;
	}

}

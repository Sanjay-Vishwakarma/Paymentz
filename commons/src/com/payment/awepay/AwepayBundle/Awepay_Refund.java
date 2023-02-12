package com.payment.awepay.AwepayBundle;

public class Awepay_Refund extends Awepay_SecondaryRequest {

	public String reason;
	public String amount;
	public String postbackurl;
	public String sendNotification = "0";

	public Awepay_Refund() {
		this._action = "processRefund";
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setAmount(String amount) {
		this.amount = String.format("%01.2f", Float.parseFloat(amount));
	}

	public void setPostbackUrl(String url) {
		this.postbackurl = url;
	}

	public void setSendNotification(boolean send) {
		this.sendNotification = send ? "1" : "0";
	}
}

package com.payment.awepay.AwepayBundle;

abstract public class Awepay_SecondaryRequest extends Awepay_ApiBase {

	public String txid;

	public void setTransactionId(String txid) {
		this.txid = txid;
	}
}

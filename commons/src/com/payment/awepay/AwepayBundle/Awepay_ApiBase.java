package com.payment.awepay.AwepayBundle;

import java.util.*;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import java.io.*;

public class Awepay_ApiBase extends Awepay_ApiElementBase {
	protected String _url = "https://admin.awepay.com/soap/tx3.php?wsdl";;

	public static final String VISA = "visa";
	public static final String MASTERCARD = "mastercard";
	public static final String AMERICAN_EXPRESS = "amex";
	public static final String JCB = "jcb";
	public static final String DISCOVER = "discover";
	public static final String DINERS = "diners";
	public static final String UNION_PAY = "union_pay";
	public static final String ACH = "ach";
	public static final String CHECK21 = "check21";

	public static final int TYPE_CHECKING = 1;
	public static final int TYPE_SAVINGS = 2;

	public static final String STATUS_APPROVED = "OK";
	public static final String STATUS_DECLINED = "EXC";
	public static final String STATUS_ERROR = "PAYG_ERROR";
	public static final String STATUS_PENDING = "PENDING";
	public static final String STATUS_3D = "REQ";

	public static final String CLASS_INTERNET_INITIATED = "WEB";
	public static final String CLASS_ACCOUNT_RECEIVABLE = "ARC";
	public static final String CLASS_CUSTOMER_INITIATED = "CIE";
	public static final String CLASS_PREARRANGED_PAYMENT_DEPOSIT = "PPD";
	public static final String CLASS_TELEPHONE_INITIATED = "TEL";
	public static final String CLASS_CASH_CONCENTRATION_DISBURSEMENT = "CCD";
	public static final String CLASS_CORPORATE_TRADE_EXCHANGE = "CTX";

	protected Object _transport;
	protected String _action;
	protected String _status;
	protected String _txid;
	protected String _error;
	protected String _errorcode;
	protected String _3dform;

	public String sid;
	public String rcode;

	public Awepay_ApiBase() {

	}

	public Map<String, Object> dummyFunc() {
		return new HashMap<String, Object>();
	}

	public boolean call() {
		if (!this.validate()) {
			return false;
		}
		
		try {
			SOAPMessage message = MessageFactory.newInstance().createMessage();
			SOAPHeader header = message.getSOAPHeader();
			header.detachNode();

			SOAPBody body = message.getSOAPBody();
			QName bodyName = new QName(this._action);
			SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
			
			this.produceNodes(this.toArray(), bodyElement);
			
			SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage response = connection.call(message, this._url.replaceAll("\\?wsdl", ""));
			connection.close();
			
			SOAPBody responseBody = response.getSOAPBody();
			SOAPBodyElement responseElement = (SOAPBodyElement) responseBody.getChildElements().next();
			SOAPElement returnElement = (SOAPElement) responseElement.getChildElements().next();
			
			Map<String, Object> responseMap = this.parseResponseElement(returnElement);
			this.parseResponse(responseMap);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
	
	protected Map<String, Object> parseResponseElement(SOAPElement response) {
		Map<String, Object> returnVal = new HashMap<String, Object>();
		Iterator returnIt = response.getChildElements();
		while (returnIt.hasNext()) {
			SOAPElement o = (SOAPElement) returnIt.next();
			if (o.getChildNodes().getLength() == 0) {
				returnVal.put(o.getNodeName(), "");
			} else if (o.getChildNodes().getLength() == 1) { // Text value
				returnVal.put(o.getNodeName(), o.getChildNodes().item(0).getNodeValue());
			} else {
				returnVal.put(o.getNodeName(), this.parseResponseElement(o));
			}
			returnIt.remove();
		}
		return returnVal;
	}
	
	protected SOAPElement produceNodes(Map element, SOAPElement container) {
		Iterator it = element.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			try {
				SOAPElement symbol = container.addChildElement(pairs.getKey().toString());
				if (pairs.getValue() instanceof String) {
					symbol.addTextNode(pairs.getValue().toString());
				} else if (pairs.getValue() instanceof List) {
					List castPairs = (List) pairs.getValue();
					for (int i = 0; i < castPairs.size(); i++) {
						SOAPElement subsymbol = symbol.addChildElement("item");
						this.produceNodes((Map) castPairs.get(i), subsymbol);
					}
				} else {
					this.produceNodes((Map) pairs.getValue(), symbol);
				}
			} catch (Exception e) {}
			it.remove();
		}
		return container;
	}
	
	protected String getXmlFromSOAPMessage(SOAPMessage msg) throws SOAPException, IOException {
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		msg.writeTo(byteArrayOS);
		return new String(byteArrayOS.toByteArray());
	}

	protected void parseResponse(Map<String, Object> response) {
		this._status = response.get("status") == null ? null : response.get("status").toString();
		this._txid = response.get("txid") == null ? null : response.get("txid").toString();
		if (!((HashMap<String, Object>) response.get("required")).get("embedhtml").toString().isEmpty()) {
			this._3dform = ((HashMap<String, Object>) response.get("required")).get("embedhtml").toString();
		}
		this._error = ((HashMap<String, Object>) response.get("error")).get("msg").toString();
		this._errorcode = ((HashMap<String, Object>) response.get("error")).get("code").toString();
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setRCode(String rcode) {
		this.rcode = rcode;
	}

	public String getStatus() {
		return this._status;
	}

	public String getResponseErrorCode() {
		return this._errorcode;
	}

	public String getResponseErrorMessage() {
		return this._error;
	}

	public String getTransactionId() {
		return this._txid;
	}

	public String get3dForm() {
		return this._3dform;
	}

}

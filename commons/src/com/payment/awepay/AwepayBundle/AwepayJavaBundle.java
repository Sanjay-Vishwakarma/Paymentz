package com.payment.awepay.AwepayBundle;


import com.google.gson.Gson;

public class AwepayJavaBundle {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		AwepayJavaBundle.examplePayment(); // An example payment
		//AwepayJavaBundle.examplePreauth(); // An example authorization
		//AwepayJavaBundle.exampleSettlement(); // An example settlement
		//AwepayJavaBundle.exampleRefund(); // An example refund


	}
	
	public static void examplePayment() {
		Awepay_Payment payment = new Awepay_Payment();
		
		// Set Auth
		payment.setSid("2329");
		payment.setRCode("a3b4b7");
		
		// Set Billing Details
		payment.setFirstName("Rihen");
		payment.setLastName("Dhedia");
		payment.setEmail("test@test.com");
		payment.setPhoneNumber("123456789");
		payment.setBillingAddress("123 Test St");
		payment.setBillingCity("Testville");
		payment.setBillingState("CA");
		payment.setBillingPostCode("90210");
		payment.setBillingCountry("US");

		payment.setUsername("dragonphoenix");
		payment.setPassword("Test@1234");
		// Optionally set shipping details, using setShippingXxx()
		
		// Set Card Information
		payment.setCardName("Test Card");
		payment.setCardNumber("4111111111111111");
		payment.setCardExpiryMonth("8"); // Preferrable set the month by 2 digits, zero padded. The month will be zero padded automatically.
		payment.setCardExpiryYear("20"); // Preferrably set the full 4 digit year, 2 digits will be prepended with "20" automatically
		payment.setCardCVV("567");

		
		// Set Tracking Codes/Merchant Reference
		payment.setReference1("KHJHJHJY");
		payment.setWID("1234");
		payment.setTID("5678");

		// Set cart information
		payment.addItem("Test Item", "1", "5.00"); // name, quantity, amount per unit, [sku], [description]  --- 2 x 3.50 gives a total cost of $7.00
		
		// 3D Parameters and callback urls
		payment.setMD("987654"); // Use a unique id - preferably the current session id - request.getSession().getId()
		payment.setRedirectUrl("http://localhost:8081/transaction/Common3DFrontEndServlet"); // The url to return to upon 3D completion
		payment.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36"); // request.getHeader("User-Agent")
		payment.setBrowserAccepts("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"); // request.getHeader("Accept")

		Gson gObj = new Gson();
		System.out.println("request -"+gObj.toJson(payment));

		if (!payment.call()) {
			System.out.println(payment.getErrors());
		} else {
			AwepayJavaBundle.displayResult(payment);
		}
	}
	
	public static void examplePreauth() {
		Awepay_Preauth preauth = new Awepay_Preauth();

		// Set Auth
		preauth.setSid("2329");
		preauth.setRCode("a3b4b7");

		// Set Billing Details
		preauth.setFirstName("Test");
		preauth.setLastName("User");
		preauth.setEmail("test@test.com");
		preauth.setPhoneNumber("123456789");
		preauth.setBillingAddress("123 Test St");
		preauth.setBillingCity("Testville");
		preauth.setBillingState("CA");
		preauth.setBillingPostCode("90210");
		preauth.setBillingCountry("US");

		// Optionally set shipping details, using setShippingXxx()

		// Set Card Information
		preauth.setCardName("Test Card");
		preauth.setCardNumber("4111111111111111");
		preauth.setCardExpiryMonth("8"); // Preferrable set the month by 2 digits, zero padded. The month will be zero padded automatically.
		preauth.setCardExpiryYear("20"); // Preferrably set the full 4 digit year, 2 digits will be prepended with "20" automatically
		preauth.setCardCVV("567");

		// Set Tracking Codes/Merchant Reference
		preauth.setReference1("54789");

		// Set cart information
		preauth.addItem("Test Item", "2", "3.50"); // name, quantity, amount per unit, [sku], [description]  --- 2 x 3.50 gives a total cost of $7.00

		// 3D Parameters and callback urls
		preauth.setMD("321"); // Use a unique id - preferably the current session id - request.getSession().getId()
		preauth.setRedirectUrl("http://localhost:8081/transaction/Common3DFrontEndServlet"); // The url to return to upon 3D completion
		preauth.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36"); // request.getHeader("User-Agent")
		preauth.setBrowserAccepts("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"); // request.getHeader("Accept")

		if (!preauth.call()) {
			System.out.println(preauth.getErrors());
		} else {
			AwepayJavaBundle.displayResult(preauth);
		}
	}

	public static void exampleSettlement() {
		Awepay_Settlement settlement = new Awepay_Settlement();
		
		// Set Auth
		settlement.setSid("2329");
		settlement.setRCode("a3b4b7");
		
		// Set preauth reference
		settlement.setTransactionId("1543243777591368");
		
		if (!settlement.call()) {
			System.out.println(settlement.getErrors());
		} else {
			AwepayJavaBundle.displayResult(settlement);
		}
	}

	public static void exampleRefund() {
		Awepay_Refund refund = new Awepay_Refund();
		
		// Set Auth
		refund.setSid("2329");
		refund.setRCode("a3b4b7");
		
		// Set payment/preauth/settlement reference
		refund.setTransactionId("1543245558574090");
		refund.setAmount("2.00");
		refund.setReason("Customer request");
		
		if (!refund.call()) {
			System.out.println(refund.getErrors());
		} else {
			AwepayJavaBundle.displayResult(refund);
		}
	}

	public static void displayResult(Awepay_ApiBase result) {
		System.out.println("Transaction ID: " + result.getTransactionId());
		String status = "Error";
		if (result.getStatus() != null) {
			switch (result.getStatus()) {
				case Awepay_ApiBase.STATUS_APPROVED:
					status = "Approved";
					break;
				case Awepay_ApiBase.STATUS_PENDING:
					status = "Pending";
					break;
				case Awepay_ApiBase.STATUS_3D:
					status = "Requires 3DS Authentication";
					break;
				case Awepay_ApiBase.STATUS_DECLINED:
					status = "Declined";
					break;
				case Awepay_ApiBase.STATUS_ERROR:
				default:
					status = "Error";
					break;
			}
		}
		
		System.out.println("Result Status: " + result.getStatus());
		System.out.println("Status: " + status);
		System.out.println("Error Code: " + (result.getResponseErrorCode() == null || result.getResponseErrorCode().isEmpty() ? "No" : "Yes - " + result.getResponseErrorCode()));
		System.out.println("Error Message: " + (result.getResponseErrorMessage() == null || result.getResponseErrorMessage().isEmpty() ? "No" : "Yes - " + result.getResponseErrorMessage()));

		if (result.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_3D)) {
			System.out.println("3DS Form: " + result.get3dForm());
		}
	}
	
}

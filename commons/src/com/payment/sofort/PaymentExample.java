package com.payment.sofort;

import com.sofort.lib.DefaultSofortLibPayment;
import com.sofort.lib.SofortLibPayment;
import com.sofort.lib.internal.transformer.RawResponse;
import com.sofort.lib.products.request.PaymentRequest;
import com.sofort.lib.products.request.PaymentTransactionDetailsRequest;
import com.sofort.lib.products.request.parts.Notification;
import com.sofort.lib.products.response.PaymentResponse;
import com.sofort.lib.products.response.PaymentTransactionDetailsResponse;
import com.sofort.lib.products.response.SofortTransactionStatusNotification;
import com.sofort.lib.products.response.parts.PaymentTransactionDetails;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 11/1/15
 * Time: 9:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentExample
{

    public static void main(String[] args) {

        /* initialize the default sofort lib payment */
        final int customerId = 4711;
        final int projectId = 8150;
        final String apiKey = "API-KEY";

        final SofortLibPayment sofortLibPayment = new DefaultSofortLibPayment(customerId, apiKey);

        /*
           * 1st step -> start sofort payment, check for errors and warnings and
           * use the received payment url for redirection of the buyer
           */
        final String statusNotificationUrl = "https://my.shop/sue/paymentStatusNotification";
        final String successUrl = "https://my.shop/sue/successfulPayment";

        PaymentRequest paymentRequest = new PaymentRequest(projectId, 19.99, "EUR", Arrays.asList("Customer 47110815", "Bill 08154711"), false);

        paymentRequest.setNotificationUrls(Arrays.asList(new Notification(statusNotificationUrl)));
        paymentRequest.setSuccessUrl(successUrl);

        PaymentResponse paymentResponse = sofortLibPayment.sendPaymentRequest(paymentRequest);

        if (paymentResponse.hasResponseErrors() || paymentResponse.hasResponseWarnings()) {
            // check and handle the response errors and warnings
        }

        if (paymentResponse.hasNewPaymentWarnings()) {
            // check the new payment warnings
        }

        // start/resume/check listening on notificationUrls

        // Store or handle transId.
        //System.out.println(paymentResponse.getTransId());
        // Redirect customer to payment URL.
        //System.out.println(paymentResponse.getPaymentUrl());

        /*
           * 2nd step -> parse the received transaction changes notification
           */
        String statusNotification = /* received status notification */"";
        SofortTransactionStatusNotification statusNotificationResponse = sofortLibPayment.parseStatusNotificationResponse(new RawResponse(RawResponse.Status.OK, statusNotification));
        String statusNotificationTransId = statusNotificationResponse.getTransId();

        // handle notification responses
        //System.out.println(statusNotificationTransId);

        /*
           * 3rd step -> get transaction details for notified transaction
           */
        PaymentTransactionDetailsRequest transactionRequest = new PaymentTransactionDetailsRequest()
                .setTransIds(Arrays.asList(statusNotificationTransId));
        PaymentTransactionDetailsResponse transactionDetailsResponse = sofortLibPayment.sendTransactionDetailsRequest(transactionRequest);
        PaymentTransactionDetails detailsPayment = transactionDetailsResponse.getTransactions().get(0);

        // handle current status
        //System.out.println(detailsPayment.getStatus() + " " + detailsPayment.getStatusReason());
    }

}

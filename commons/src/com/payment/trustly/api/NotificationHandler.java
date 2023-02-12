/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Trustly Group AB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.payment.trustly.api;

import com.directi.pg.TransactionLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.trustly.api.commons.Method;
import com.payment.trustly.api.commons.NotificationDeserializer;
import com.payment.trustly.api.commons.ResponseStatus;
import com.payment.trustly.api.commons.exceptions.TrustlySignatureException;
import com.payment.trustly.api.data.notification.Notification;
import com.payment.trustly.api.data.notification.notificationdata.*;
import com.payment.trustly.api.data.response.Response;
import com.payment.trustly.api.requestbuilders.NotificationResponse.Build;
import com.payment.trustly.api.security.SignatureHandler;

public class NotificationHandler {

    final SignatureHandler signatureHandler = SignatureHandler.getInstance();
    TransactionLogger transactionLogger=new TransactionLogger(NotificationHandler.class.getName());

    /**
     * Deserializes and verifies incoming notification.
     * @param notificationJson Notification sent from Trustly.
     * @return Request object, a deserialized notification.
     */
    public Notification handleNotification(final String notificationJson) {
        final NotificationDeserializer deserializer = new NotificationDeserializer();

        deserializer.registerDataType(Method.CREDIT.toString(), CreditData.class);
        deserializer.registerDataType(Method.ACCOUNT.toString(), AccountNotificationData.class);
        deserializer.registerDataType(Method.CANCEL.toString(), CancelNotificationData.class);
        deserializer.registerDataType(Method.DEBIT.toString(), DebitNotificationData.class);
        deserializer.registerDataType(Method.PENDING.toString(), PendingNotificationData.class);

        final Gson gson = new GsonBuilder().registerTypeAdapter(Notification.class, deserializer)
                                     .create();

        final Notification notification = gson.fromJson(notificationJson, Notification.class);

        verifyNotification(notification);

        return notification;
    }

    private void verifyNotification(final Notification notification) {
        if (!signatureHandler.verifyNotificationSignature(notification)) {
            throw new TrustlySignatureException("Incoming data signature is not valid");
        }
    }

    /**
     * Creates a response for an incoming notification.
     * @param method method of the notification
     * @param uuid UUID of the incoming notification
     * @param status OK/FAIL
     * @return Notification response
     */
    public Response prepareNotificationResponse(final Method method, final String uuid, final ResponseStatus status) {
        transactionLogger.debug("-----inside prepareNotificationResponse-----");

        final Response response = new Build(method, uuid, status)
                .getResponse();

        transactionLogger.debug("-----before signNotificationResponse------");

        signatureHandler.signNotificationResponse(response);

        transactionLogger.debug("-----aftere signNotificationResponse------");

        return response;
    }

    public String toJson(final Response response) {
        return new Gson().toJson(response);
    }
}

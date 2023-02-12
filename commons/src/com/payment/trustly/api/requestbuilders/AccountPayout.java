/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Trustly Group AB
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

package com.payment.trustly.api.requestbuilders;


import com.payment.trustly.api.commons.Currency;
import com.payment.trustly.api.commons.Method;
import com.payment.trustly.api.data.request.Request;
import com.payment.trustly.api.data.request.RequestParameters;
import com.payment.trustly.api.data.request.requestdata.AccountPayoutData;
import com.payment.trustly.api.security.SignatureHandler;

import java.util.Map;
import java.util.TreeMap;

public class AccountPayout {
    private final Request request = new Request();

    public AccountPayout(final Build builder) {
        final RequestParameters params = new RequestParameters();
        params.setUUID(SignatureHandler.generateNewUUID());
        params.setData(builder.data);

        request.setMethod(Method.ACCOUNT_PAYOUT);
        request.setParams(params);
    }

    public Request getRequest() {
        return request;
    }

    public static class Build {
        private final AccountPayoutData data = new AccountPayoutData();
        private final Map<String, Object> attributes = new TreeMap<>();

        public Build(final String accountId, final String endUserId, final String messageId, final String amount,final String notificationURL, final Currency currency) {
            data.setAccountId(accountId);
            data.setEndUserId(endUserId);
            data.setMessageId(messageId);
            data.setAmount(amount);
            data.setCurrency(currency);
            data.setNotificationURL(notificationURL);
            data.setAttributes(attributes);
        }

        public Build senderInformation(final String address, final String countryCode, final String dateOfBirth, final String firstName, final String lastName, final String partyType) {
            final Map<String, Object> senderInformation = new TreeMap<>();
            senderInformation.put("Address", address);
            senderInformation.put("CountryCode", countryCode);
            senderInformation.put("DateOfBirth", dateOfBirth);
            senderInformation.put("Firstname", firstName);
            senderInformation.put("Lastname", lastName);
            senderInformation.put("PartyType", partyType);

            attributes.put("SenderInformation", senderInformation);
            return this;
        }

        public Request getRequest() {
            return new AccountPayout(this).getRequest();
        }
    }
}

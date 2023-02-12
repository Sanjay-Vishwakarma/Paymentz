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

package com.payment.trustly.api.data.request.requestdata;


import com.google.gson.annotations.SerializedName;
import com.payment.trustly.api.commons.Currency;
import com.payment.trustly.api.data.request.RequestData;

public class AccountPayoutData extends RequestData {
    @SerializedName("AccountID")
    private String accountId;
    @SerializedName("EndUserID")
    private String endUserId;
    @SerializedName("MessageID")
    private String messageId;
    @SerializedName("Amount")
    private String amount;
    @SerializedName("Currency")
    private Currency currency;

    @SerializedName("NotificationURL")
    private String notificationURL;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(final String endUserId) {
        this.endUserId = endUserId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    public String getNotificationURL()
    {
        return notificationURL;
    }

    public void setNotificationURL(String notificationURL)
    {
        this.notificationURL = notificationURL;
    }
}

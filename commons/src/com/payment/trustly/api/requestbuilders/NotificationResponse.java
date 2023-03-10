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

package com.payment.trustly.api.requestbuilders;

import com.directi.pg.TransactionLogger;
import com.payment.trustly.api.commons.Method;
import com.payment.trustly.api.commons.ResponseStatus;
import com.payment.trustly.api.data.response.Response;
import com.payment.trustly.api.data.response.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a new notification response ready to be sent to Trustly API.
 * The constructor contains the required fields of a notification response.
 *
 * Builder let you add additional information if any is available for the given response.
 *
 * The API specifics of the response can be found on https://trustly.com/en/developer/
 *
 * Example use for a default notification response:
 * Response response = new NotificationResponse.Build(method, uuid, responseStatus).getResponse();
 */
public class NotificationResponse {
    private final Response response = new Response();
    TransactionLogger transactionLogger= new TransactionLogger(NotificationResponse.class.getName());

    private NotificationResponse(final Build builder) {
        transactionLogger.debug("-----inside NotificationResponse-----");
        final Result result = new Result();
        result.setUuid(builder.uuid);
        result.setData(builder.data);
        result.setMethod(builder.method);

        response.setResult(result);
        response.setVersion("1.1");
    }

    public Response getResponse() {
        return response;
    }

    public static class Build {
        final String uuid;
        final Method method;
        private final Map<String, Object> data = new HashMap<>();
        TransactionLogger transactionLogger= new TransactionLogger(Build.class.getName());

        public Build(final Method method, final String uuid, final ResponseStatus status) {
            transactionLogger.debug("-----inside class Build------");
            this.uuid = uuid;
            this.method = method;
            data.put("status", status);
        }

        public Response getResponse() {
            transactionLogger.debug("------inside getResponse-------");
            return new NotificationResponse(this).getResponse();
        }
    }
}

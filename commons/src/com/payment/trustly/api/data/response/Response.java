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

package com.payment.trustly.api.data.response;

import com.google.gson.annotations.Expose;

public class Response {
    private String version;
    private Result result;
    @Expose(serialize = false)
    private java.lang.Error error;
    private String resJson;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    public java.lang.Error getError() {
        return error;
    }

    public void setError(final java.lang.Error error) {
        this.error = error;
    }

    public boolean successfulResult() {
        return result != null && error == null;
    }

    public String getUUID() {
        //return successfulResult() ? result.getUuid() : error.getError().getUuid();
        return result.getUuid();
    }

    public String getSignature() {
        return result.getSignature() ;
    }

    public String getResJson()
    {
        return resJson;
    }

    public void setResJson(String resJson)
    {
        this.resJson = resJson;
    }

    @Override
    public String toString() {
        return "VERSION: " + version +  "\nERROR: " + error + "\nRESULT:\n" + result.toString();
    }
}

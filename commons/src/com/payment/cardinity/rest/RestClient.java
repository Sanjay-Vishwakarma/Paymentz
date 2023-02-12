package com.payment.cardinity.rest;

import com.google.gson.reflect.TypeToken;
import com.payment.cardinity.model.Result;

import java.util.Map;

public interface RestClient {

    <T> Result<T> sendRequest(RestResource.RequestMethod method, String url, TypeToken<T> clazz, T requestObject);

    <T> Result<T> sendRequest(RestResource.RequestMethod method, String url, TypeToken<T> clazz);

    <T> Result<T> sendRequest(RestResource.RequestMethod method, String url, TypeToken<T> clazz, Map<String, String>
            params);

}

package com.payment.cardinity.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.cardinity.json.CardSerializer;
import com.payment.cardinity.json.PaymentDeserializer;
import com.payment.cardinity.json.UtcDateTypeAdapter;
import com.payment.cardinity.model.Card;
import com.payment.cardinity.model.Payment;

import java.util.Date;

public abstract class RestResource {

    // @formatter:off
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Payment.class, new PaymentDeserializer())
            .registerTypeAdapter(Card.class, new CardSerializer())
            .registerTypeAdapter(Date.class, new UtcDateTypeAdapter())
            .create();
    // @formatter:on


    public enum RequestMethod {
        GET, PATCH, POST
    }

    public enum Resource {
        PAYMENTS, REFUNDS, SETTLEMENTS, VOIDS
    }

}

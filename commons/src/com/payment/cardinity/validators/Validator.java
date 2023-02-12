package com.payment.cardinity.validators;

public interface Validator<T> {

    void validate(T object);
}

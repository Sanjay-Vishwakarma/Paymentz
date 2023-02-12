package com.payment.cardinity.validators;

import com.payment.cardinity.exceptions.ValidationException;
import com.payment.cardinity.model.Void;

public class VoidValidator implements Validator<Void> {

    @Override
    public void validate(Void voidP) {

        if (voidP == null)
            throw new ValidationException("Missing void object.");

        // Optional fields
        if (voidP.getDescription() != null && !ValidationUtils.validateIntervalLength(voidP.getDescription(), 0, 255))
            throw new ValidationException("Description maximum length 255 characters.");
    }

}

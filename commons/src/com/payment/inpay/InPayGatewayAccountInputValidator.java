package com.payment.inpay;

import com.directi.pg.Logger;
import com.payment.validators.CommonGatewayAccountInputValidator;
import org.owasp.esapi.ESAPI;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by supriya on 5/10/2016.
 */
public class InPayGatewayAccountInputValidator extends CommonGatewayAccountInputValidator
{
    private static Logger logger = new Logger(InPayGatewayAccountInputValidator.class.getName());
    public String gatewaySpecificAccountValidation(HttpServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        if (!ESAPI.validator().isValidInput("mid",request.getParameter("mid"), "Numbers", 20, false))
        {
            sb.append("Invalid mid.<BR>");
            logger.debug("Invalid mid");

        }
        if (!ESAPI.validator().isValidInput("secretkey",request.getParameter("secretkey"), "SafeString", 50, false))
        {
            sb.append("Invalid secretkey.<BR>");
            logger.debug("Invalid secretkey");

        }
        return sb.toString();
    }
}

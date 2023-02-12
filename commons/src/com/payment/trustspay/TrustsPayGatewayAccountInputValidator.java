package com.payment.trustspay;

import com.directi.pg.Logger;
import com.payment.validators.CommonGatewayAccountInputValidator;
import org.owasp.esapi.ESAPI;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by supriya on 5/10/2016.
 */
public class TrustsPayGatewayAccountInputValidator extends CommonGatewayAccountInputValidator
{
    private static Logger logger = new Logger(TrustsPayGatewayAccountInputValidator.class.getName());
    public String gatewaySpecificAccountValidation(HttpServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        if (!ESAPI.validator().isValidInput("mid",request.getParameter("mid"), "Numbers", 20, false))
        {
            sb.append("Invalid mid.<BR>");
            logger.debug("Invalid mid");

        }
        if (!ESAPI.validator().isValidInput("gatewayno",request.getParameter("gatewayno"), "Numbers", 20, false))
        {
            sb.append("Invalid gatewayno.<BR>");
            logger.debug("Invalid gatewayno");

        }
        if (!ESAPI.validator().isValidInput("signkey",request.getParameter("signkey"), "SafeString", 20, false))
        {
            sb.append("Invalid signkey.<BR>");
            logger.debug("Invalid signkey");

        }
        return sb.toString();
    }
}

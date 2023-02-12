package com.payment.payon.core;

import com.directi.pg.Logger;
import com.payment.validators.CommonGatewayAccountInputValidator;
import org.owasp.esapi.ESAPI;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by supriya on 5/10/2016.
 */
public class PayOnGatewayAccountInputValidator extends CommonGatewayAccountInputValidator
{
    private static Logger logger = new Logger(PayOnGatewayAccountInputValidator.class.getName());
    public String gatewaySpecificAccountValidation(HttpServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        if (!ESAPI.validator().isValidInput("connector", request.getParameter("connector"), "SafeString", 90, false))
        {
            sb.append("Invalid connector.<BR>");
            logger.debug("Invalid connector");
        }
        if (!ESAPI.validator().isValidInput("shortid", request.getParameter("shortid"), "SafeString", 90, true))
        {
            sb.append("Invalid shortid.<BR>");
            logger.debug("Invalid shortid");
        }
        if (!ESAPI.validator().isValidInput("type", request.getParameter("type"), "SafeString", 150, true))
        {
            sb.append("Invalid type.<BR>");
            logger.debug("Invalid type");
        }
        if (!ESAPI.validator().isValidInput("merchantname", request.getParameter("merchantname"), "SafeString", 150, true))
        {
            sb.append("Invalid merchantname.<BR>");
            logger.debug("Invalid merchantname");
        }
        if (!ESAPI.validator().isValidInput("username", request.getParameter("username"), "SafeString", 180, true))
        {
            sb.append("Invalid username.<BR>");
            logger.debug("Invalid username");
        }
        if (!ESAPI.validator().isValidInput("url", request.getParameter("url"), "SafeString", 180, true))
        {
            sb.append("Invalid url.<BR>");
            logger.debug("Invalid url");
        }
        if (!ESAPI.validator().isValidInput("key", request.getParameter("key"), "SafeString", 180, true))
        {
            sb.append("Invalid key.<BR>");
            logger.debug("Invalid key");
        }
        if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"), "SafeString", 20, true))
        {
            sb.append("Invalid terminalid.<BR>");
            logger.debug("Invalid terminalid");
        }
        if (!ESAPI.validator().isValidInput("password", request.getParameter("password"), "SafeString", 150, true))
        {
            sb.append("Invalid password.<BR>");
            logger.debug("Invalid password");
        }
        if (!ESAPI.validator().isValidInput("iban", request.getParameter("iban"), "SafeString", 150, true))
        {
            sb.append("Invalid iban.<BR>");
            logger.debug("Invalid iban");
        }
        if (!ESAPI.validator().isValidInput("uploaduser", request.getParameter("uploaduser"), "SafeString", 150, true))
        {
            sb.append("Invalid uploaduser.<BR>");
            logger.debug("Invalid uploaduser");
        }
        if (!ESAPI.validator().isValidInput("apiUserName", request.getParameter("apiUserName"), "SafeString", 765, true))
        {
            sb.append("Invalid apiUserName.<BR>");
            logger.debug("Invalid apiUserName");
        }
        if (!ESAPI.validator().isValidInput("apiPassword", request.getParameter("apiPassword"), "SafeString", 765, true))
        {
            sb.append("Invalid apiPassword.<BR>");
            logger.debug("Invalid apiPassword");
        }
        if (!ESAPI.validator().isValidInput("eci", request.getParameter("eci"), "SafeString", 25, true))
        {
            sb.append("Invalid eci.<BR>");
            logger.debug("Invalid eci");
        }
        if (!ESAPI.validator().isValidInput("verification", request.getParameter("verification"), "SafeString", 255, true))
        {
            sb.append("Invalid verification.<BR>");
            logger.debug("Invalid verification");
        }
        if (!ESAPI.validator().isValidInput("xid", request.getParameter("xid"), "SafeString", 255, true))
        {
            sb.append("Invalid xid.<BR>");
            logger.debug("Invalid xid");
        }
        if (!ESAPI.validator().isValidInput("RecurringCode", request.getParameter("RecurringCode"), "SafeString", 85, true))
        {
            sb.append("Invalid RecurringCode.<BR>");
            logger.debug("Invalid RecurringCode");
        }
        return sb.toString();
    }
}

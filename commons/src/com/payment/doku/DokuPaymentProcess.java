package com.payment.doku;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Rihen on 5/25/2021.
 */
public class DokuPaymentProcess extends CommonPaymentProcess
{
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.doku");
    private static TransactionLogger transactionLogger = new TransactionLogger(DokuPaymentProcess.class.getName());


    public static String getPaymentForm(String url, Boolean isTest)
    {
        transactionLogger.error("Form from DokuPaymentProcess..... url = "+ url );

        String checkoutUrl = "";
        if (isTest)
        {
            checkoutUrl = RB.getString("JS_SANDBOX");
        }
        else
        {
            checkoutUrl = RB.getString("JS_LIVE");
        }


        String html="<html>\n" +
                "    <head>\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "        <script src="+checkoutUrl+"></script>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <script type=\"text/javascript\">\n" +
                "            loadJokulCheckout('"+url+"'); // Replace it with the payment.url you retrieved from the response\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";


        transactionLogger.error("DokuPaymentProcess HTML =="+html);
        return html;
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside  DokuPaymentProcess Form REST NB---");
        transactionLogger.error("directKitResponseVO.getBankRedirectionUrl()---->"+directKitResponseVO.getBankRedirectionUrl());


//        String html="<html>\n" +
//                "    <head>\n" +
//                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
//                "        <script src=\"https://sandbox.doku.com/jokul-checkout-js/v1/jokul-checkout-1.0.0.js\"></script>\n" +
//                "    </head>\n" +
//                "    <body>\n" +
//                "        <script type=\"text/javascript\">\n" +
//                "            loadJokulCheckout('"+directKitResponseVO.getBankRedirectionUrl()+"'); // Replace it with the payment.url you retrieved from the response\n" +
//                "        </script>\n" +
//                "    </body>\n" +
//                "</html>";

//        directKitResponseVO.setBankRedirectionUrl(html);
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
}
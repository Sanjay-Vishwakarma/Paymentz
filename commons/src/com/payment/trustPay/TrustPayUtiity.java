package com.payment.trustPay;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by admin on 11/5/2016.
 */
public class TrustPayUtiity
{
    private static Logger log = new Logger(TrustPayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TrustPayPaymentGateway.class.getName());

    public CommRequestVO getTrustPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commCardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());
        commCardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());

        commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commTransactionDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());

        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());

        commRequestVO.setCardDetailsVO(commCardDetailsVO);
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

        return commRequestVO;
    }

    public String sendPaymentRequest (String data, URL url) throws PZTechnicalViolationException, IOException
    {
        log.debug("URL--->"+url);
        String response = null;
        InputStream is ;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        log.debug("connection open--->"+conn);

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(String.valueOf(data));
        wr.flush();
        wr.close();
        int responseCode = conn.getResponseCode();

        if (responseCode >= 400)
        {
            log.debug("responseCode---->"+responseCode);
            is = conn.getErrorStream();
        }
        else
        {
            log.debug("responseCode---->"+responseCode);
            is = conn.getInputStream();
        }
        try
        {
            response = IOUtils.toString(is);
            log.debug("Response from utils--->" + response);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("TrustPayUtiity.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        return response;
    }
}
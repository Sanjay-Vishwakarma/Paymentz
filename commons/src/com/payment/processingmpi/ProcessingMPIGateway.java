package com.payment.processingmpi;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.util.ResourceBundle;

/**
 * Created by Roshan on 2/9/2018.
 */
public class ProcessingMPIGateway
{
    public static final String GATEWAY_TYPE = "processingmpi";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.processingdotcom");
    private static TransactionLogger transactionLogger = new TransactionLogger(ProcessingMPIGateway.class.getName());

    public EnrollmentResponseVO processEnrollment(EnrollmentRequestVO enrollmentRequestVO) throws PZTechnicalViolationException,Exception
    {
        String enrollmentResponse ="";
        String URL="";

        String enrollmentRequest =
                "mid=" +enrollmentRequestVO.getMid() +
                        "&trans_amount=" +enrollmentRequestVO.getAmount() +
                        "&trans_id="+enrollmentRequestVO.getTrackid()+
                        "&cc_num=" +enrollmentRequestVO.getPan() +
                        "&cc_exp_yr="+enrollmentRequestVO.getExpiryYear() +
                        "&cc_exp_mth="+enrollmentRequestVO.getExpiryMonth();

        if(enrollmentRequestVO.isTestRequest()){
            URL=RB.getString("TEST_ENROLLMENT_URL");
        }
        else{
            URL=RB.getString("LIVE_ENROLLMENT_URL");
        }

        transactionLogger.error("-----enrollment request-----:" + enrollmentRequest);
        enrollmentResponse=ProcessingMpiUtils.doPostHTTPSURLConnection(URL,enrollmentRequest);
        transactionLogger.error("-----enrollment response-----:" + enrollmentResponse);

        EnrollmentResponseVO enrollmentResponseVO= ProcessingMpiUtils.getEnrollmentResponse(enrollmentResponse);
        return enrollmentResponseVO;
    }
    public ParesDecodeResponseVO processParesDecode(ParesDecodeRequestVO paresDecodeRequestVO) throws PZTechnicalViolationException,Exception
    {
        String URL="";
        String paresRequest =
                "PaRes=" + paresDecodeRequestVO.getPares() +
                        "&MD="+paresDecodeRequestVO.getMassageID();

        if(paresDecodeRequestVO.isTestRequest()){
            URL=RB.getString("TEST_PARES_DECODE_URL");
        }
        else{
            URL=RB.getString("LIVE_PARES_DECODE_URL");
        }

        transactionLogger.error("----- pares-----:" + paresRequest);
        String pares = ProcessingMpiUtils.doPostHTTPSURLConnection(URL, paresRequest);
        transactionLogger.error("-----decoded pares-----:" + pares);
        ParesDecodeResponseVO paResDecodeResponse = ProcessingMpiUtils.getPaResDecodeResponse(pares);
        return paResDecodeResponse;
    }
}

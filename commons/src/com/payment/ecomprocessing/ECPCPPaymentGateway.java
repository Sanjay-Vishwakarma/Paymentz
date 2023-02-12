package com.payment.ecomprocessing;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by devendra on 3/31/2020.
 */
public class ECPCPPaymentGateway extends AbstractPaymentGateway
{
        public static final String GATEWAY_TYPE = "ecpcp";
        final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ecp");
        private static TransactionLogger transactionLogger = new TransactionLogger(ECPCPPaymentGateway.class.getName());
        private static Logger log = new Logger(ECPPaymentGateway.class.getName());

        public ECPCPPaymentGateway(String accountId)
        {
            this.accountId = accountId;
        }

        public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
        {
            transactionLogger.error("Inside processSale ---");
            ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
            ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
            PZGenericConstraint genConstraint = new PZGenericConstraint("SBMPaymentGateway","processRebilling()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
            throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside authentication ---");
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("SBMPaymentGateway","processRebilling()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }



        public List<EcpResponseVo> processCardPresentTransactionByDate(String fromDate,String toDate,String processingType)
        {
            transactionLogger.error("Inside processRetrievalByDate() ---");
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
            String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
            transactionLogger.error("user name -----------: "+userName);
            transactionLogger.error("password -----------: "+password);
            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            transactionLogger.error("encodedCredentials ------------: "+encodedCredentials);
            ECPUtils ecpUtils = new ECPUtils();
            List<EcpResponseVo> list =new  ArrayList();

            boolean isTest = gatewayAccount.isTest();
            String testUrl = RB.getString("CARD_PRESENT_TEST_URL");
            String liveUrl = RB.getString("CARD_PRESENT_LIVE_URL");

            String request = ""
                    + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<processed_transaction_request>"
                    + "<start_date>"+ecpUtils.checkNull(fromDate)+"</start_date>"
                    + "<end_date>"+ecpUtils.checkNull(toDate)+"</end_date>"
                    + "<processing_type>"+ecpUtils.checkNull(processingType)+"</processing_type>"
                    + "</processed_transaction_request>";

            transactionLogger.error("request---------"+request);
            try
            {
                String response="";
                if(isTest)
                {
                    transactionLogger.error("test url ------ "+testUrl);
                    response = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, request, "Basic", encodedCredentials);
                }else{
                    transactionLogger.error("live url ------ "+liveUrl);
                    response = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, request, "Basic", encodedCredentials);
                }
                transactionLogger.error("card present response ------"+response);
                list = ecpUtils.readCardPresentResponse(response);
            }catch (Exception e){
                transactionLogger.error(e);
            }
            return list;
        }

        @Override
        public String getMaxWaitDays()
        {
            return null;
        }
    }





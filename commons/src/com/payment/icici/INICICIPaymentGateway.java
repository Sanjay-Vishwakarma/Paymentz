package com.payment.icici;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.dao.TransactionDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.icici.com.sfa.java.*;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: kiran
 * Date:
 * Time:
 * To change this template use File | Settings | File Templates.
 */
public class INICICIPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(INICICIPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(INICICIPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "inicici";
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public INICICIPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering in processAuthentication of INICICIPaymentGateway....");
        transactionLogger.debug("Entering processAuthentication of INICICIPaymentGateway...");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        INICICIResponseVO iniciciResponseVO=new INICICIResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();
        CardInfo cardDetails = new CardInfo();
        BillToAddress billToAddressDetails = new BillToAddress();
        CustomerDetails customerDetails = new CustomerDetails();
        ShipToAddress shipToaddressDetails = new ShipToAddress();

        ICICIMerchantDetails.setMerchantDetails(commMerchantVO.getMerchantId(),"","",genericAddressDetailsVO.getIp(),trackingID,"","","POST",genericTransDetailsVO.getCurrency(),
                "","req.Preauthorization",genericTransDetailsVO.getAmount(),"","","","","","");

        ICICIMerchantDetails.setMerchantRelatedTxnDetails(commMerchantVO.getMerchantId(), "","",trackingID,"","", "","", "POST",genericTransDetailsVO.getCurrency(),
                "req.Preauthorization", genericTransDetailsVO.getAmount(), "",
                "", "", "", "", "");

        cardDetails.setCardDetails(genericCardDetailsVO.getCardType(),genericCardDetailsVO.getCardNum(),getValidCVV(genericCardDetailsVO.getcVV(),genericCardDetailsVO.getCardType()),
                genericCardDetailsVO.getExpYear(),genericCardDetailsVO.getExpMonth(), "", "CREDI");


        shipToaddressDetails.setAddressDetails(genericAddressDetailsVO.getStreet(), "", "", genericAddressDetailsVO.getCity(),
                genericAddressDetailsVO.getState(), genericAddressDetailsVO.getZipCode(), genericAddressDetailsVO.getCountry(), genericAddressDetailsVO.getEmail());

        billToAddressDetails.setAddressDetails("", "",
                genericAddressDetailsVO.getStreet(), "", "",
                genericAddressDetailsVO.getCity(), genericAddressDetailsVO.getState(), genericAddressDetailsVO.getZipCode(), genericAddressDetailsVO.getCountry(), "");



        customerDetails.setCustomerDetails(genericAddressDetailsVO.getFirstname(), genericAddressDetailsVO.getLastname(),null ,
                null, genericAddressDetailsVO.getPhone(), "", "Y");

        SessionDetail sessionDetails = new SessionDetail();
        MerchanDise merchanDiseDetails = new MerchanDise();

        sessionDetails.setSessionDetails(genericAddressDetailsVO.getIp(), "", "","", "", "");
        merchanDiseDetails.setMerchanDiseDetails("", "", "","", "","");

        String authCode="";
        String rrNumber="";
        String status="";
        String description="";
        String remark="";
        String successCode="";
        String paymentId="";

        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        TransactionDAO tDAO = new TransactionDAO();
        TransactionDetailsVO vCurrentTransaction=null;
        vCurrentTransaction = tDAO.getDetailFromCommon(trackingID);
        try
        {

            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(billToAddressDetails,shipToaddressDetails, ICICIMerchantDetails,null,cardDetails,new PGReserveData(),customerDetails,
                    sessionDetails,null,merchanDiseDetails) ;
            transactionLogger.debug("response from icici==="+trackingID + "--" + pgResponse.toString());
            if("0".equals(pgResponse.getRespCode()))
            {

                status="success";
                description="Transaction successful";
                remark="Transaction Successful";
                authCode=pgResponse.getAuthIdCode();
                rrNumber=pgResponse.getRRN();
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();

            }
            else if("1".equals(pgResponse.getRespCode()))
            {

                status="failed";
                description="Transaction  failed";
                remark=pgResponse.getRespMessage();
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();
            }
            else if("2".equals(pgResponse.getRespCode()))
            {

                status="failed";
                description="Transaction  Failed";
                remark=pgResponse.getRespMessage();
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            iniciciResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            iniciciResponseVO.setStatus(status);
            iniciciResponseVO.setDescription(description);
            iniciciResponseVO.setAuthCode(authCode);
            iniciciResponseVO.setRRNumber(rrNumber);
            iniciciResponseVO.setTransactionType("auth");
            iniciciResponseVO.setDescriptor(descriptor);
            iniciciResponseVO.setTransactionStatus(vCurrentTransaction.getStatus());
            iniciciResponseVO.setErrorCode(successCode);
            iniciciResponseVO.setTransactionId(paymentId);
            iniciciResponseVO.setRemark(remark);

        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        return iniciciResponseVO;

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Entering in processSale of INICICIPaymentGateway....");
        transactionLogger.debug("Entering processSale of INICICIPaymentGateway...");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        INICICIResponseVO iniciciResponseVO=new INICICIResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();
        CardInfo cardDetails = new CardInfo();
        BillToAddress billToAddressDetails = new BillToAddress();
        CustomerDetails customerDetails = new CustomerDetails();
        ShipToAddress shipToaddressDetails = new ShipToAddress();

        ICICIMerchantDetails.setMerchantDetails(commMerchantVO.getMerchantId(),"","",genericAddressDetailsVO.getIp(),trackingID,"","","POST","INR",
                "","req.Sale",genericTransDetailsVO.getAmount(),"","","","","","");

        ICICIMerchantDetails.setMerchantRelatedTxnDetails(commMerchantVO.getMerchantId(), "","",trackingID,"","", "","", "POST", "INR",
                "req.Sale", genericTransDetailsVO.getAmount(), "",
                "", "", "", "", "");

        cardDetails.setCardDetails(genericCardDetailsVO.getCardType(), genericCardDetailsVO.getCardNum(),getValidCVV(genericCardDetailsVO.getcVV(),genericCardDetailsVO.getCardType()),
                genericCardDetailsVO.getExpYear(), genericCardDetailsVO.getExpMonth(), "", "CREDI");

        shipToaddressDetails.setAddressDetails(genericAddressDetailsVO.getStreet(), "", "", genericAddressDetailsVO.getCity(),
                genericAddressDetailsVO.getState(), genericAddressDetailsVO.getZipCode(), genericAddressDetailsVO.getCountry(), genericAddressDetailsVO.getEmail());

        billToAddressDetails.setAddressDetails("", "",
                genericAddressDetailsVO.getStreet(), "", "",
                genericAddressDetailsVO.getCity(), genericAddressDetailsVO.getState(), genericAddressDetailsVO.getZipCode(), genericAddressDetailsVO.getCountry(), "");

        customerDetails.setCustomerDetails(genericAddressDetailsVO.getFirstname(), genericAddressDetailsVO.getLastname(), new Address(),
                null, genericAddressDetailsVO.getPhone(), "", "");

        String status="";
        String description="";
        String remark="";
        String successCode="";
        String paymentId="";

        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        TransactionDAO tDAO = new TransactionDAO();
        TransactionDetailsVO vCurrentTransaction=null;
        vCurrentTransaction = tDAO.getDetailFromCommon(trackingID);

        try
        {

            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(billToAddressDetails,shipToaddressDetails, ICICIMerchantDetails,null,cardDetails,new PGReserveData(),customerDetails,
                    null,null,null) ;
            transactionLogger.debug("response from icici==="+trackingID + "--" + pgResponse.toString());
            if("0".equals(pgResponse.getRespCode())){
                status="success";
                description="Transaction  successful";
                remark="Transaction Successful";
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();
            }
            else if("1".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction  failed";
                remark=pgResponse.getRespMessage();
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();
            }
            else if("2".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction  Failed";
                remark=pgResponse.getRespMessage();
                successCode=pgResponse.getRespCode();
                paymentId=pgResponse.getEpgTxnId();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            iniciciResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            iniciciResponseVO.setStatus(status);
            iniciciResponseVO.setDescription(description);
            iniciciResponseVO.setTransactionType("Sale");
            iniciciResponseVO.setDescriptor(descriptor);
            iniciciResponseVO.setTransactionStatus(vCurrentTransaction.getStatus());
            iniciciResponseVO.setErrorCode(successCode);
            iniciciResponseVO.setTransactionId(paymentId);
            iniciciResponseVO.setRemark(remark);


        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception e)
        {
            log.error("Exception while connecting with bank",e);
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return iniciciResponseVO;

    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        log.debug("Entering in processCapture of INICICIPaymentGateway....");
        transactionLogger.debug("Entering processCapture of INICICIPaymentGateway...");
        INICICIRequestVO iniciciRequestVO=(INICICIRequestVO)requestVO;

        CommResponseVO commResponseVO=new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = iniciciRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = iniciciRequestVO.getCommMerchantVO();

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();
        ICICIMerchantDetails.setMerchantRelatedTxnDetails(commMerchantVO.getMerchantId(), "","",trackingID,iniciciRequestVO.getRTSRNumber(),iniciciRequestVO.getRRNumber(),iniciciRequestVO.getAuthCode(),"", "POST", "INR",
                "req.Authorization", genericTransDetailsVO.getAmount(), "",
                "", "", "", "", "");

        String status="";
        String description="";
        try
        {

            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(null,null, ICICIMerchantDetails,null,null,null,null,
                    null,null,null) ;
            transactionLogger.debug("pgResponse object in processCapture"+pgResponse.toString());
            transactionLogger.debug("test for built in processCapture");

            if("0".equals(pgResponse.getRespCode()))
            {
                status="success";
                description="Transaction successful";
            }
            else if("1".equals(pgResponse.getRespCode()))
            {
                status="failed";
                description="Transaction  failed";
            }
            else if("2".equals(pgResponse.getRespCode()))
            {
                status="failed";
                description="Transaction  Failed";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setStatus(status);
            commResponseVO.setDescription(description);
            commResponseVO.setTransactionType("Capture");

        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception e)
        {
            log.error("Exception while connecting with bank",e);
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Entering in processRefund of INICICIPaymentGateway....");
        transactionLogger.debug("Entering processRefund of INICICIPaymentGateway...");
        INICICIRequestVO iniciciRequestVO=(INICICIRequestVO)requestVO;

        CommResponseVO commResponseVO=new CommResponseVO();
        GenericTransDetailsVO genericTransDetailsVO = iniciciRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = iniciciRequestVO.getCommMerchantVO();

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();

        ICICIMerchantDetails.setMerchantRelatedTxnDetails(commMerchantVO.getMerchantId(), "","",trackingID,iniciciRequestVO.getRTSRNumber(),iniciciRequestVO.getRRNumber(),iniciciRequestVO.getAuthCode(),"", "POST",genericTransDetailsVO.getCurrency(),
                "req.Refund", genericTransDetailsVO.getAmount(), "",
                "", "", "", "", "");

        String status="";
        String description="";
        try
        {

            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(null,null, ICICIMerchantDetails,null,null,null,null,
                    null,null,null);
            transactionLogger.debug("response from icici==="+trackingID + "--" + pgResponse.toString());

            if("0".equals(pgResponse.getRespCode())){
                status="success";
                description="Transaction successful";
            }
            else if("1".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction  failed";
            }
            else if("2".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction  failed";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setStatus(status);
            commResponseVO.setDescription(description);
            commResponseVO.setTransactionType("refund");
        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception e)
        {
            log.error("Exception while connecting with bank",e);
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws  PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        log.debug("Entering in processInquiry of INICICIPaymentGateway");
        transactionLogger.debug("Entering in processInquiry of INICICIPaymentGateway");
        INICICIRequestVO iniciciRequestVO=(INICICIRequestVO)requestVO;
        INICICIResponseVO iniciciResponseVO=new INICICIResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = iniciciRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = iniciciRequestVO.getCommMerchantVO();

        String status="";
        String description="";
        String remark="";
        String successCode="";
        String paymentId="";

        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        try
        {
            PostLib postLib = new PostLib();
            ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();
            ICICIMerchantDetails.setMerchantOnlineInquiry(commMerchantVO.getMerchantId(),genericTransDetailsVO.getOrderId());

            PGSearchResponse pgSearchResponse =postLib.postStatusInquiry(ICICIMerchantDetails);
            transactionLogger.debug("response from icici==="+pgSearchResponse.toString());
            ArrayList oPgRespArr = pgSearchResponse.getPGResponseObjects();
            int index=0;
            PGResponse oPgResp=new PGResponse();
            for (index=0 ; index< oPgRespArr.size(); index++){
                oPgResp = (PGResponse) oPgRespArr.get(index);                ;
            }
            if("0".equals(pgSearchResponse.getRespCode())){
                if("0".equals(oPgResp.getRespCode())){
                    status="Successful";
                    description="Transaction  Successful";
                    remark=oPgResp.getRespMessage();
                    successCode=oPgResp.getRespCode();
                    paymentId=oPgResp.getEpgTxnId();
                }
                else if("1".equals(oPgResp.getRespCode())){
                    status="Rejected By Switch";
                    description="Transaction  Failed";
                    remark=oPgResp.getRespMessage();
                    successCode=oPgResp.getRespCode();
                    paymentId=oPgResp.getEpgTxnId();
                }
                else if("2".equals(oPgResp.getRespCode())){
                    status="Rejected By Payment Gateway";
                    description="Transaction  Failed";
                    remark=oPgResp.getRespMessage();
                    successCode=oPgResp.getRespCode();
                    paymentId=oPgResp.getEpgTxnId();
                }
            }
            else{
                status="";
                description="";
                descriptor="";
                remark="Exception while connecting to bank";
                successCode="";
                paymentId="";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            iniciciResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            iniciciResponseVO.setStatus(status);
            iniciciResponseVO.setTransactionType("Inquiry");
            iniciciResponseVO.setDescriptor(descriptor);
            iniciciResponseVO.setDescription(description);
            iniciciResponseVO.setErrorCode(successCode);
            iniciciResponseVO.setTransactionId(paymentId);
            iniciciResponseVO.setRemark(remark);
            iniciciResponseVO.setAuthCode(iniciciRequestVO.getAuthCode());
            iniciciResponseVO.setRRNumber(iniciciRequestVO.getRRNumber());

        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processInquiry()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processInquiry()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception e)
        {
            log.error("Exception while connecting with bank",e);
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processInquiry()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return iniciciResponseVO;
    }
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processVoid");
        transactionLogger.debug("Entering processVoid");
        INICICIRequestVO iniciciRequestVO=(INICICIRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = iniciciRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = iniciciRequestVO.getCommMerchantVO();

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();

        ICICIMerchantDetails.setMerchantRelatedTxnDetails(commMerchantVO.getMerchantId(), "","",trackingID,iniciciRequestVO.getRTSRNumber(),iniciciRequestVO.getRRNumber(),iniciciRequestVO.getAuthCode(),"", "POST", "INR",
                "req.PreAuthReversal", genericTransDetailsVO.getAmount(), "",
                "", "", "", "", "");

        String status="";
        String description="";
        try
        {

            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(null,null, ICICIMerchantDetails,null,null,null,null,
                    null,null,null) ;
            transactionLogger.debug("response from icici==="+trackingID + "--" + pgResponse.toString());
            if("0".equals(pgResponse.getRespCode())){
                status="success";
                description="Your transaction is cancelled successfully";
            }
            else if("1".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction failed for Cancellation";
            }
            else if("2".equals(pgResponse.getRespCode())){
                status="failed";
                description="Transaction failed for Cancellation";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setStatus(status);
            commResponseVO.setDescription(description);
            commResponseVO.setTransactionType("Cancel");
        }
        catch (ServiceException se)
        {
            log.error("Exception while connecting with bank",se);
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (RemoteException re)
        {
            log.error("Exception while connecting with bank",re);
            transactionLogger.error("Exception while connecting with bank", re);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, re.getMessage(), re.getCause());
        }
        catch (Exception e)
        {
            log.error("Exception while connecting with bank",e);
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("INICICIPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("INICICIPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public String getValidCVV(String cvv,String cardType)
    {
        if("VISA"==cardType){
            return cvv+"$1";
        }
        else{
            return cvv;
        }
    }
}

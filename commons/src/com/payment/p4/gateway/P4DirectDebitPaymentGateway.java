package com.payment.p4.gateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.p4.vos.p4MainVo.Request;
import com.payment.p4.vos.p4MainVo.Response;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;

/**
 * Created by Admin on 16/11/2015.
 */
public class P4DirectDebitPaymentGateway extends AbstractPaymentGateway
{
    private Functions functions = new Functions();

    public static final String GATEWAY_TYPE="P4SEPA";

    public final static String DIRECT_DEBIT_PREAUTH="EU.PA";
    public final static String DIRECT_DEBIT_MANDATE="EU.MD";
    public final static String DIRECT_DEBIT_DEBIT="EU.DB";
    public final static String DIRECT_DEBIT_REFUND="EU.RF";

    public Logger logger= new Logger(P4DirectDebitPaymentGateway.class.getName());
    public TransactionLogger transactionLogger= new TransactionLogger(P4DirectDebitPaymentGateway.class.getName());

    public String getMaxWaitDays()
    {
        return null;
    }

    public P4DirectDebitPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }

    /*public GenericResponseVO processAuthentication(String trackingId,GenericRequestVO requestVO)
    {
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO =new P4ResponseVO();

        Request request = p4Utils.getDirectDebitRequestForSepaAuthentication(commRequestVO, trackingId, DIRECT_DEBIT_PREAUTH, accountId);

        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Request.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(request,System.out);
        }
        catch (PropertyException e)
        {
            transactionLogger.error("PropertyException--->", e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("JAXBException--->", e);
        }

        Response response = p4Utils.sendRequestForDirectDebitTransfer(request);

        return p4ResponseVO;
    }*/

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("P4DirectDebitPayment","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO)
    {
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Response response =null;

        Request request=null;

        String mandateURL="";
        String revokeMandateURL="";
        boolean isRecurring=false;

        StringWriter stringWriter = new StringWriter();

        JAXBContext jaxbContext = null;

        if(commRequestVO.getCardDetailsVO()!=null && !functions.isValueNull(commRequestVO.getCardDetailsVO().getMandateId()))
        {
            response=processMandate(trackingId,commRequestVO);
            p4ResponseVO=p4Utils.convertP4SEPAResponseToSystemResponse(response);
            if("success".equals(p4ResponseVO.getStatus()))
            {
                if (response != null && response.getTransaction() != null && response.getTransaction().getIdentification() != null && functions.isValueNull(response.getTransaction().getIdentification().getRegistrationUniqueID()))
                {
                    commRequestVO.getCardDetailsVO().setMandateId(response.getTransaction().getIdentification().getRegistrationUniqueID());

                }
                if(response!=null && response.getTransaction()!=null && response.getTransaction().getFrontend()!=null)
                {
                    mandateURL =response.getTransaction().getFrontend().getMandateShowUrl();
                    p4ResponseVO.setMandateURL(mandateURL);
                    revokeMandateURL =response.getTransaction().getFrontend().getMandateRevokeUrl();
                    p4ResponseVO.setRevokeMandateURL(revokeMandateURL);
                }



                try
                {
                    jaxbContext = JAXBContext.newInstance(Response.class);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                    // output pretty printed
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    jaxbMarshaller.marshal(response,stringWriter);
                }
                catch (PropertyException e)
                {
                    transactionLogger.error("PropertyException--->", e);
                }
                catch (JAXBException e)
                {
                    transactionLogger.error("JAXBException--->", e);
                }

                logger.debug("Response:::"+stringWriter.toString());
                transactionLogger.debug("Response:::"+stringWriter.toString());

                isRecurring ="Y".equals(commRequestVO.getRecurringBillingVO().getRecurring())?true:false;
            }

        }



        if(functions.isValueNull(commRequestVO.getCardDetailsVO().getMandateId()))
        {
            request = p4Utils.getDirectDebitRequestForSepaSale(commRequestVO, trackingId, DIRECT_DEBIT_DEBIT, accountId);

            stringWriter = new StringWriter();

            try
            {
                jaxbContext = JAXBContext.newInstance(Request.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(request,stringWriter);
            }
            catch (PropertyException e)
            {
                transactionLogger.error("PropertyException--->", e);
            }
            catch (JAXBException e)
            {
                transactionLogger.error("JAXBException--->", e);
            }

            logger.debug("Request SALE:::"+stringWriter.toString());
            transactionLogger.debug("Request SALE:::"+stringWriter.toString());


            response = p4Utils.sendRequestForDirectDebitTransfer(request);

            stringWriter = new StringWriter();
            try
            {
                jaxbContext = JAXBContext.newInstance(Response.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(response,stringWriter);
            }
            catch (PropertyException e)
            {
                transactionLogger.error("PropertyException--->", e);
            }
            catch (JAXBException e)
            {
                transactionLogger.error("JAXBException--->", e);
            }

            logger.debug("Response SALE:::"+stringWriter.toString());
            transactionLogger.debug("Response SALE:::"+stringWriter.toString());


            p4ResponseVO = p4Utils.convertP4SEPAResponseToSystemResponse(response);

            p4ResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());

            if(functions.isValueNull(mandateURL))
            {
                p4ResponseVO.setMandateURL(mandateURL);
            }
            if(functions.isValueNull(revokeMandateURL))
            {
                p4ResponseVO.setRevokeMandateURL(revokeMandateURL);
            }
            p4ResponseVO.setRecurring(isRecurring);

        }
        return p4ResponseVO;
    }


    private Response processMandate(String trackingId,CommRequestVO commRequestVO)
    {
        P4Utils p4Utils = new P4Utils();

        Request request = p4Utils.getDirectDebitRequestForSepaMandate(commRequestVO,trackingId,DIRECT_DEBIT_MANDATE,accountId);

        StringWriter stringWriter = new StringWriter();
        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Request.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(request,stringWriter);
        }
        catch (PropertyException e)
        {
            transactionLogger.error("PropertyException--->", e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("JAXBException--->", e);
        }

        logger.debug("Request::::"+stringWriter.toString());
        transactionLogger.debug("Request::::"+stringWriter.toString());

        return p4Utils.sendRequestForDirectDebitTransfer(request);

    }

    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO)
    {
        logger.debug("inside P$DirectDebitPaymentGateway Refund---"+trackingId);
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getDirectDebitRequestForSepaRefund(commRequestVO,trackingId,DIRECT_DEBIT_REFUND,accountId);

        StringWriter stringWriter = new StringWriter();
        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Request.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(request,stringWriter);
        }
        catch (PropertyException e)
        {
            transactionLogger.error("PropertyException--->", e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("JAXBException--->", e);
        }

        logger.debug("Request:::"+stringWriter.toString());
        transactionLogger.debug("Request:::"+stringWriter.toString());

        Response response = p4Utils.sendRequestForDirectDebitTransfer(request);

        stringWriter = new StringWriter();
        try
        {
            jaxbContext = JAXBContext.newInstance(Response.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(response,stringWriter);
        }
        catch (PropertyException e)
        {
            transactionLogger.error("PropertyException--->", e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("JAXBException--->", e);
        }

        logger.debug("Response::"+stringWriter.toString());
        transactionLogger.debug("Response:::"+stringWriter.toString());

        p4ResponseVO = p4Utils.convertP4SEPARefundResponseToSystemResponse(response);

        return p4ResponseVO;
    }

    public static void main(String args[]) throws PZGenericConstraintViolationException
    {
        P4PaymentGateway p4PaymentGateway = new P4PaymentGateway("6666");

        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        commMerchantVO.setAccountId("123");
        transDetailsVO.setAmount("50"); //Amount * 100 according to the docs
        transDetailsVO.setCurrency("EUR");
        transDetailsVO.setPreviousTransactionId("ihg1fbxaxzeext6xun72wucvjcf2ktrkscblk11a");
        transDetailsVO.setOrderDesc("Qwerfdsat4");

        commAddressDetailsVO.setLanguage("ENG");

        commAddressDetailsVO.setFirstname("Swamy");
        commAddressDetailsVO.setLastname("Rapolu");
        commAddressDetailsVO.setBirthdate("19911207");
        commAddressDetailsVO.setStreet("Prabhadevi");
        commAddressDetailsVO.setZipCode("400025");
        commAddressDetailsVO.setCity("Mumbai");
        commAddressDetailsVO.setState("MH");
        commAddressDetailsVO.setCountry("IN");
        commAddressDetailsVO.setEmail("swamy@gmail.com");
        commAddressDetailsVO.setIp("172.125.98.11");
        commAddressDetailsVO.setPhone("9920896116");

        commCardDetailsVO.setCardHolderName("SwamyRapolu");
        commCardDetailsVO.setCardNum("4000000000000002");
        commCardDetailsVO.setcVV("123");

        //commCardDetailsVO.setPaymentType("CC");
        commCardDetailsVO.setExpMonth("07");
        commCardDetailsVO.setExpYear("2016");

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);

        P4Utils p4Utils = new P4Utils();

        Request request = p4Utils.getDirectDebitRequestForSepaRefund(commRequestVO, "28886", "EU.RF", null);

        Response response = p4Utils.sendRequestForDirectDebitTransfer(request);

        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Request.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(request,System.out);
        }
        catch (PropertyException e)
        {
            
        }
        catch (JAXBException e)
        {
            
        }

        System.out.println("Response::"+response);

        try
        {
            jaxbContext = JAXBContext.newInstance(Response.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(response,System.out);
        }
        catch (PropertyException e)
        {
            
        }
        catch (JAXBException e)
        {
            
        }

    }

    public GenericResponseVO processRebilling(String trackingid, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("P4DirectDebitPayment","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
}
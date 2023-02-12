package com.payment.p4.gateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.p4.vos.p4MainVo.Request;
import com.payment.p4.vos.p4MainVo.Response;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;

/**
 * Created by Admin on 26/10/2015.
 */
public class P4CreditCardPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(P4CreditCardPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4CreditCardPaymentGateway.class.getName());

    private Functions functions = new Functions();

    public static final String GATEWAY_TYPE = "P4Credit";

    public final static String CREDIT_CARD_PREAUTH = "CC.PA";
    public final static String CREDIT_CARD_SALE = "CC.DB";
    public final static String CREDIT_CARD_REFUND = "CC.RF";
    public final static String CREDIT_CARD_REVERSAL = "CC.RV";
    public final static String CREDIT_CARD_CAPTURE = "CC.CP";
    public final static String CREDIT_CARD_QUERY = "";

    public String getMaxWaitDays()
    {
        return null;
    }

    public P4CreditCardPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = null;

        Request request = p4Utils.getCreditCardRequestForAuthentication(commRequestVO, trackingID, CREDIT_CARD_PREAUTH,accountId);

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
            transactionLogger.error("PropertyException--->",e);
        }
        catch (JAXBException e)
        {
            transactionLogger.error("JAXBException--->", e);
        }

        logger.debug("Request:::"+stringWriter.toString());
        transactionLogger.debug("Request:::"+stringWriter.toString());

        Response response=p4Utils.sendRequestForCreditCard(request);

        stringWriter=new StringWriter();
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

        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForAuthorization(response);

        return p4ResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Entering processSale of P4CreditCardPaymentGateway...");
        transactionLogger.debug("Entering processSale of P4CreditCardPaymentGateway...");

        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = null;

        Request request = p4Utils.getCreditCardRequestForSale(commRequestVO, trackingID, CREDIT_CARD_SALE,accountId);

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


        Response response=p4Utils.sendRequestForCreditCard(request);

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

        logger.debug("Response:::"+stringWriter.toString());
        transactionLogger.debug("Response:::"+stringWriter.toString());


        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForSale(response);


        return p4ResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Entering processCapture of P4CreditCardPaymentGateway...");
        transactionLogger.debug("Entering processCapture of P4CreditCardPaymentGateway...");

        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getCreditCardRequestForCapture(commRequestVO, trackingID, CREDIT_CARD_CAPTURE,accountId);

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

        Response response=p4Utils.sendRequestForCreditCard(request);

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

        logger.debug("Response:::"+stringWriter.toString());
        transactionLogger.debug("Response:::"+stringWriter.toString());

        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForCapture(response);


        return p4ResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Entering processRefund of P4CreditCardPaymentGateway...");
        transactionLogger.debug("Entering processRefund of P4CreditCardPaymentGateway...");

        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getCreditCardRequestForRefund(commRequestVO, trackingID, CREDIT_CARD_REVERSAL, accountId);

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

        Response response=p4Utils.sendRequestForCreditCard(request);

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

        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForVoid(response);
        transactionLogger.debug("refund response----"+response);

        return p4ResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Entering processVoid of P4CreditCardPaymentGateway...");
        transactionLogger.debug("Entering processVoid of P4CreditCardPaymentGateway...");

        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getCreditCardRequestForVoid(commRequestVO, trackingID, CREDIT_CARD_REVERSAL,accountId);

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

        logger.debug("Request:::" + stringWriter.toString());
        transactionLogger.debug("Request:::"+stringWriter.toString());

        Response response=p4Utils.sendRequestForCreditCard(request);

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

        logger.debug("Response:::"+stringWriter.toString());
        transactionLogger.debug("Response:::"+stringWriter.toString());

        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForVoid(response);

        return p4ResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Entering processVoid of P4CreditCardPaymentGateway...");
        transactionLogger.debug("Entering processVoid of P4CreditCardPaymentGateway...");

        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getCreditCardRequestForQuery(commRequestVO, trackingID, CREDIT_CARD_QUERY,accountId);

        Response response=p4Utils.sendRequestForOnlineBankTransfer(request);

        p4ResponseVO=p4Utils.convertP4CreditCardResponseToPZForQuery(response);

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
        transDetailsVO.setPreviousTransactionId("k0cl5ozicz63kmnbxs27hsy2dz3py0tffcod8i8u");

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

        Request request = p4Utils.getCreditCardRequestForQuery(commRequestVO, "21651", "", null);

        Response response = p4Utils.sendRequestForQuery(request);

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
}

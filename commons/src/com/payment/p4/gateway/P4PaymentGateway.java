package com.payment.p4.gateway;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.p4.vos.p4MainVo.Request;
import com.payment.p4.vos.p4MainVo.Response;
import com.payment.p4.vos.transactionBlock.Transaction;
import com.payment.p4.vos.transactionBlock.frontEndBlock.Frontend;
import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.json.impl.MoxyXmlStructure;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 10/3/2015.
 */
public class P4PaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(P4PaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4PaymentGateway.class.getName());
    private Functions functions = new Functions();

    public static final String GATEWAY_TYPE = "P4NetBank";

    private final static String ONLINE_BANK_PREAUTH = "OT.PA";
    private final static String ONLINE_BANK_REFUND = "OT.RF";

    private Map<String,String> onlineTransactionBankMapping =new HashMap<String, String>();

    static
    {

    }


    public String getMaxWaitDays()
    {
        return null;
    }

    public P4PaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        String brand=getBankForOnlineTransactionThroughP4(accountId);

        Request request = p4Utils.getOnlineBankTransferRequestForPreAuth(commRequestVO, trackingID, ONLINE_BANK_PREAUTH, accountId,brand);

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

        logger.debug("Request:::"+stringWriter);
        transactionLogger.debug("Request:::"+stringWriter);

        Response response=p4Utils.sendRequestForOnlineBankTransfer(request);

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

        logger.debug("Response:::"+stringWriter);
        transactionLogger.debug("Response:::"+stringWriter);


        if(response!=null && response.getTransaction()!=null)
        {
            Transaction transaction=response.getTransaction();
            if(transaction.getProcessing()!=null)
            {
                Processing processing=transaction.getProcessing();

                p4ResponseVO.setTransactionStatus(processing.getResult());
                //p4ResponseVO.setRemark(processing.getReason() + "(" + processing.getReturn() + ")");
                p4ResponseVO.setDescription(processing.getState() + "(" + processing.getReturn() + ")");
                p4ResponseVO.setRemark(processing.getStatus() + "(" + processing.getState() + ")");
            }

            if(transaction.getIdentification()!=null)
            {
                Identification identification = transaction.getIdentification();

                p4ResponseVO.setTransactionId(identification.getUniqueID());
                p4ResponseVO.setShortID(identification.getShortID());
            }
            if(transaction.getPayment()!=null && transaction.getPayment().get(0).getClearing()!=null)
            {
                Clearing clearing = transaction.getPayment().get(0).getClearing();

                p4ResponseVO.setAmount(clearing.getAmount());
                p4ResponseVO.setDescriptor(clearing.getDescriptor());
            }
            if(transaction.getFrontend()!=null )
            {
                Frontend frontend = transaction.getFrontend();

                p4ResponseVO.setFormularURL(frontend.getFormularUrl());
                p4ResponseVO.setRedirectURL(frontend.getRedirectUrl());
            }

            if("ACK".equalsIgnoreCase(p4ResponseVO.getTransactionStatus()))
            {
                p4ResponseVO.setStatus("success");
            }
            else
            {
                p4ResponseVO.setStatus("fail");
            }
        }
        else
        {
            p4ResponseVO.setStatus("fail");
        }

        return p4ResponseVO;
    }



    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        P4Utils p4Utils = new P4Utils();

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        Request request = p4Utils.getOnlineBankTransferRequestForRefund(commRequestVO, trackingID, ONLINE_BANK_REFUND,accountId);

        StringWriter stringWriter=new StringWriter();

        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Request.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(request, stringWriter);
        }
        catch (PropertyException e)
        {
            logger.error("Property Exception while marshalling request:::", e);
        }
        catch (JAXBException e)
        {
            logger.error("JAXB Exception while marshalling request:::", e);
        }

        logger.debug("Request::::"+stringWriter.toString());

        Response response=p4Utils.sendRequestForOnlineBankTransfer(request);
        //Convert Response TO System Response TODO
        stringWriter=new StringWriter();

        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(response, stringWriter);
        }
        catch (PropertyException e)
        {
            logger.error("Property Exception while marshalling request:::", e);
        }
        catch (JAXBException e)
        {
            logger.error("JAXB Exception while marshalling request:::",e);
        }
        logger.debug("Response:::"+stringWriter.toString());

        p4ResponseVO=p4Utils.convertP4SEPARefundResponseToSystemResponse(response);


        return p4ResponseVO;
    }



    public static void main(String args[]) throws PZGenericConstraintViolationException
    {
        P4PaymentGateway p4PaymentGateway = new P4PaymentGateway("6666");

        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commMerchantVO.setAccountId("123");
        transDetailsVO.setAmount("10.00"); //Amount * 100 according to the docs
        transDetailsVO.setCurrency("EUR");
        transDetailsVO.setOrderDesc("asdadada");

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
        commAddressDetailsVO.setIp("172.125.98.100");
        commAddressDetailsVO.setPhone("9920896116");


        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        P4Utils p4Utils = new P4Utils();

       // Request request=p4Utils.getOnlineBankRequestForQuery(commRequestVO, "21736", "bajt5bp8tcwvhml30nphe65jbqfe2wgkt6r8k8l2", null);
        Request request=p4Utils.getOnlineBankTransferRequestForPreAuth(commRequestVO, "21736",ONLINE_BANK_PREAUTH,null,"SOFORT");

        Response response=p4Utils.sendRequestForOnlineBankTransfer(request);



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

    //load all gatewayAccount for P4Online
    private String getBankForOnlineTransactionThroughP4(String accountId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        String brand="SOFORT";
        try
        {
            con= Database.getConnection();
            String query="select brand from gateway_accounts_p4online where accountId=? ORDER BY id limit 1";

            preparedStatement=con.prepareStatement(query);
            preparedStatement.setString(1,accountId);

            resultSet=preparedStatement.executeQuery();

            if(resultSet.next())
            {
                 brand=resultSet.getString("brand");
            }

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(P4PaymentGateway.class.getName(),"getBankForOnlineTransactionThroughP4()",null,"transaction","Please Contact Support team for more information regarding the transaction", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(P4PaymentGateway.class.getName(),"getBankForOnlineTransactionThroughP4()",null,"transaction","Please Contact Support team for more information regarding the transaction", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }finally
        {
            Database.closeConnection(con);
        }

        return brand;
    }

    /*public static void main(String[] args)
    {
        Processing processing = new Processing();
        processing.setReason("hcdhdhdh");
        processing.setResult("yeyeyeyey");
        processing.setStatus("dgdgdgdgdg");
        processing.setCode1("dgdgdg");
        processing.setCode2("sdgterewge");
        processing.setCode3("3333w2323");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXBContext jaxbContext = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Processing.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(processing, out);
        }
        catch (PropertyException e)
        {
            e.printStackTrace();
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }

        byte[] byteArray = out.toByteArray();
        InputStream in = new ByteArrayInputStream(byteArray);

        Processing processing1 = null;
        try
        {
            jaxbContext = JAXBContext.newInstance(Processing.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            //jaxbUnmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            processing1 = (Processing) jaxbUnmarshaller.unmarshal(in);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }

        System.out.println("Processing :::" + processing1.getCode1());
        System.out.println("Processing :::" + processing1.getCode2());
        System.out.println("Processing :::" + processing1.getCode3());
    }*/
}

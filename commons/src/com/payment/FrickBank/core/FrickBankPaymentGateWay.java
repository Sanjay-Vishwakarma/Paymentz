package com.payment.FrickBank.core;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.FrickBank.core.message.*;
import com.payment.FrickBank.core.message.Transaction;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrickBankPaymentGateWay extends AbstractPaymentGateway
{

    private static Logger log = new Logger(FrickBankPaymentGateWay.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FrickBankPaymentGateWay.class.getName());

    private String Testurl ="https://test.ctpe.io/payment/ctpe";
    //Configuration MODE
    public static final String LIVEMODE = "LIVE";
    public static final String TESTMODE = "CONNECTOR_TEST";
    public static final String SALE = "CC.DB";
    public static final String CAPTURE = "CC.CP";
    public static final String CHARGEBACK = "CC.CB.00.00";
    public static final String AUTHORIZATION = "CC.PA";
    public static final String REFUND = "CC.RF";
    public static final String CANCLE = "CC.RV";
    private static Hashtable<String, FrickBankAccount> frickBankAccountHashtable;
    public static final String GATEWAY_TYPE = "Frickpayme";

    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException e)
        {
            log.error("Error while loading gateway accounts : " + Util.getStackTrace(e));
            transactionLogger.error("Error while loading gateway accounts : " + Util.getStackTrace(e));

            PZExceptionHandler.handleDBCVEException(e,"Frick Bank", PZOperations.FRICKBANK_LAOD_ACCOUNT);
        }

    }

    public FrickBankPaymentGateWay(String accountId)
    {
        this.accountId = accountId;
    }

    public static FrickBankAccount getFrickAccount(String accountId)
    {
        return frickBankAccountHashtable.get(accountId);
    }

    @Override
    public String getMaxWaitDays()
    {
        return "3.5";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        FrickBankRequestVO frickBankRequestVO= (FrickBankRequestVO) requestVO;
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();
        log.debug("FrickBankPaymentGateway Start1");
        Request request=new Request();
        Header header= populateAccountDetailsForHeader();
        log.debug("FrickBankPaymentGateway Start2");
        request.setHeader(header);
        log.debug("FrickBankPaymentGateway Start3");
        Transaction transaction=new Transaction();
        transaction = populateAccountDetails();
        Identification identification=new Identification();
        identification.setTransactionID(trackingID);
        transaction.setMode(TESTMODE);
        Payment payment = populateCustomerTransactionDetails(frickBankRequestVO.getTransDetailsVO());
        payment.setCode(SALE);
        log.debug("FrickBankPaymentGateway Start4");
        Account account = populateCustomerCardDetails(frickBankRequestVO.getCardDetailsVO(),frickBankRequestVO.getAddressDetailsVO());
        Customer customer = populateCustomerDetails(frickBankRequestVO.getAddressDetailsVO());
        transaction.setIdentification(identification);
        transaction.setPayment(payment);
        transaction.setAccount(account);
        transaction.setCustomer(customer);
        log.debug("FrickBankPaymentGateway Start5");
        request.setTransaction(transaction);

        try
        {
            Response response = processRequest(request);
            frickBankResponseVO = populateResponseDetails(response);
            log.debug("FrickBankPaymentGateway Start6");
            if(frickBankResponseVO!=null)
            {
                if(frickBankResponseVO.getResponsecode().equalsIgnoreCase("00"))
                {
                    frickBankResponseVO.setStatus("success");
                }
                else
                {
                    frickBankResponseVO.setStatus("fail");
                }
            }
            else
            {
                frickBankResponseVO.setStatus("fail");
                frickBankResponseVO.setDescription("Exception occure while parse response");
            }
        }
        catch (IOException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(FrickBankPaymentGateWay.class.getName(),"processSale()",null,"common","IO Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return frickBankResponseVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        FrickBankRequestVO frickBankRequestVO= (FrickBankRequestVO) requestVO;
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();

        Request request=new Request();
        Header header= populateAccountDetailsForHeader();

        request.setHeader(header);

        Transaction transaction=new Transaction();
        transaction = populateAccountDetails();
        Identification identification=new Identification();
        identification.setTransactionID(trackingID);
        transaction.setMode(TESTMODE);
        Payment payment = populateCustomerTransactionDetails(frickBankRequestVO.getTransDetailsVO());
        payment.setCode(AUTHORIZATION);

        Account account = populateCustomerCardDetails(frickBankRequestVO.getCardDetailsVO(),frickBankRequestVO.getAddressDetailsVO());
        Customer customer = populateCustomerDetails(frickBankRequestVO.getAddressDetailsVO());
        transaction.setIdentification(identification);
        transaction.setPayment(payment);
        transaction.setAccount(account);
        transaction.setCustomer(customer);

        request.setTransaction(transaction);

        try
        {
            Response response = processRequest(request);
            frickBankResponseVO = populateResponseDetails(response);

            if(frickBankResponseVO!=null)
            {
                if(frickBankResponseVO.getResponsecode().equalsIgnoreCase("00"))
                {
                    frickBankResponseVO.setStatus("success");
                }
                else
                {
                    frickBankResponseVO.setStatus("fail");
                }
            }
            else
            {
                frickBankResponseVO.setStatus("fail");
                frickBankResponseVO.setDescription("Exception occure while parse response");
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FrickBankPaymentGateWay.class.getName(), "processAuthentication()", null, "common", "IO Exception while authenticating transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        //xml file instance
        return frickBankResponseVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        FrickBankRequestVO frickBankRequestVO= (FrickBankRequestVO) requestVO;
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();

        Request request=new Request();
        Header header= populateAccountDetailsForHeader();

        request.setHeader(header);
        Transaction transaction=new Transaction();
        transaction = populateAccountDetails();
        Identification identification=new Identification();
        identification.setTransactionID(trackingID);
        identification.setReferenceID(frickBankRequestVO.getTransDetailsVO().getPreviousTransactionId());
        transaction.setMode(TESTMODE);
        transaction.setIdentification(identification);
        Payment payment = new Payment();
        payment.setCode(CANCLE);
        transaction.setPayment(payment);
        request.setTransaction(transaction);
        try
        {
            Response response = processRequest(request);
            frickBankResponseVO = populateResponseDetails(response);

            if(frickBankResponseVO!=null)
            {
                if(frickBankResponseVO.getResponsecode().equalsIgnoreCase("00"))
                {
                    frickBankResponseVO.setStatus("success");
                }
                else
                {
                    frickBankResponseVO.setStatus("fail");
                }
            }
            else
            {
                frickBankResponseVO.setStatus("fail");
                frickBankResponseVO.setDescription("Exception occure while parse response");
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FrickBankPaymentGateWay.class.getName(), "processVoid()", null, "common", "IO Exception while Cancelling the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        //xml file instance
        return frickBankResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        FrickBankRequestVO frickBankRequestVO= (FrickBankRequestVO) requestVO;
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();

        Request request=new Request();
        Header header= populateAccountDetailsForHeader();

        request.setHeader(header);
        Transaction transaction=new Transaction();
        transaction = populateAccountDetails();
        Identification identification=new Identification();
        identification.setTransactionID(trackingID);
        identification.setReferenceID(frickBankRequestVO.getTransDetailsVO().getPreviousTransactionId());
        transaction.setIdentification(identification);
        transaction.setMode(TESTMODE);
        Payment payment = populateCustomerTransactionDetails(frickBankRequestVO.getTransDetailsVO());
        payment.setCode(REFUND);
        transaction.setPayment(payment);
        request.setTransaction(transaction);
        try
        {
            Response response = processRequest(request);
            frickBankResponseVO = populateResponseDetails(response);

            if(frickBankResponseVO!=null)
            {
                if(frickBankResponseVO.getResponsecode().equalsIgnoreCase("00"))
                {
                    frickBankResponseVO.setStatus("success");
                }
                else
                {
                    frickBankResponseVO.setStatus("fail");
                }
            }
            else
            {
                frickBankResponseVO.setStatus("fail");
                frickBankResponseVO.setDescription("Exception occure while parse response");
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FrickBankPaymentGateWay.class.getName(), "processVoid()", null, "common", "IO Exception while Refunding the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        //xml file instance
        return frickBankResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        FrickBankRequestVO frickBankRequestVO= (FrickBankRequestVO) requestVO;
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();

        Request request=new Request();
        Header header= populateAccountDetailsForHeader();

        request.setHeader(header);
        Transaction transaction=new Transaction();
        transaction = populateAccountDetails();
        Identification identification=new Identification();
        identification.setTransactionID(trackingID);
        identification.setReferenceID(frickBankRequestVO.getTransDetailsVO().getPreviousTransactionId());
        transaction.setIdentification(identification);
        transaction.setMode(TESTMODE);
        Payment payment = populateCustomerTransactionDetails(frickBankRequestVO.getTransDetailsVO());
        payment.setCode(CAPTURE);
        transaction.setPayment(payment);
        request.setTransaction(transaction);
        try
        {
            Response response = processRequest(request);
            frickBankResponseVO = populateResponseDetails(response);

            if(frickBankResponseVO!=null)
            {
                if(frickBankResponseVO.getResponsecode().equalsIgnoreCase("00"))
                {
                    frickBankResponseVO.setStatus("success");
                }
                else
                {
                    frickBankResponseVO.setStatus("fail");
                }
            }
            else
            {
                frickBankResponseVO.setStatus("fail");
                frickBankResponseVO.setDescription("Exception occure while parse response");
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FrickBankPaymentGateWay.class.getName(), "processVoid()", null, "common", "IO Exception while Capturing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        //xml file instance
        return frickBankResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO)
    {
        return null;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("FrickBankPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private Header populateAccountDetailsForHeader()
    {
        FrickBankAccount accountDetails=getFrickAccount(accountId);

        Header header=new Header();
        Security security=new Security();
        security.setSender(accountDetails.getSender());
        header.setSecurity(security);

        return header;
    }

    private Transaction populateAccountDetails()
    {
        Transaction transaction=new Transaction();
        FrickBankAccount accountDetails=getFrickAccount(accountId);

        transaction.setChannel(accountDetails.getChannel());
        User user=new User() ;
        user.setLogin(accountDetails.getLogin());
        user.setPwd(accountDetails.getPwd());
        transaction.setUser(user);
        return transaction;
    }
    private Customer populateCustomerDetails(GenericAddressDetailsVO addressDetailsVO) throws PZConstraintViolationException
    {
        if(addressDetailsVO==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","AddressDetails  not provided while placing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while placing the transaction",new Throwable("AddressDetails  not provided while placing the transaction"));
        }

        if(addressDetailsVO.getCountry() ==null || addressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Country not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Country not provided while placing the transaction",new Throwable("Country not provided while placing the transaction"));
        }
        if(addressDetailsVO.getPhone() ==null || addressDetailsVO.getPhone().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Phone NO not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing the transaction",new Throwable("Phone NO not provided while placing the transaction"));
        }
        if(addressDetailsVO.getEmail() ==null || addressDetailsVO.getEmail().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Email Id not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email ID not provided while placing the transaction",new Throwable("Email Id not provided while placing the transaction"));
        }

        if(addressDetailsVO.getFirstname()==null|| addressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","First Name not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing the transaction",new Throwable("First Name not provided while placing the transaction"));

        }
        if(addressDetailsVO.getLastname()==null|| addressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Last Name not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing the transaction",new Throwable("Last Name not provided while placing the transaction"));

        }
        if(addressDetailsVO.getIp() ==null || addressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","IP Address not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while placing the transaction",new Throwable("IP Address not provided while placing the transaction"));
        }

        if(addressDetailsVO.getZipCode()==null|| addressDetailsVO.getZipCode().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","ZIP Code not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"ZIP Code not provided while placing the transaction",new Throwable("ZIP Code not provided while placing the transaction"));

        }
        if(addressDetailsVO.getStreet()==null|| addressDetailsVO.getStreet().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Street not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Street not provided while placing the transaction",new Throwable("Street not provided while placing the transaction"));

        }
        if(addressDetailsVO.getCity()==null|| addressDetailsVO.getCity().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","City not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"City not provided while placing the transaction",new Throwable("City not provided while placing the transaction"));

        }


        //name details
        Name name=new Name();
        name.setSalutation("NONE");
        name.setFamily(addressDetailsVO.getLastname());
        name.setGiven(addressDetailsVO.getFirstname());
        name.setTitle("");
        name.setCompany("");

        //address details
        Address address = new Address();
        address.setCity(addressDetailsVO.getCity());
        address.setCountry(addressDetailsVO.getCountry());
        address.setState(addressDetailsVO.getState());
        address.setStreet(addressDetailsVO.getStreet());
        address.setZip(addressDetailsVO.getZipCode());

        //contect details
        Contact contact = new Contact();
        contact.setEmail(addressDetailsVO.getEmail());
        contact.setIp(addressDetailsVO.getIp());
        contact.setMobile(addressDetailsVO.getPhone());
        contact.setPhone(addressDetailsVO.getPhone());

        Customer customer=new Customer();
        customer.setName(name);
        customer.setAddress(address);
        customer.setContact(contact);
        return customer;
    }

    private Account populateCustomerCardDetails(GenericCardDetailsVO cardDetailsVO,GenericAddressDetailsVO addressDetailsVO) throws PZConstraintViolationException
    {
        if(cardDetailsVO==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","cardDetails not provided while placing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"cardDetails not provided while placing the transaction",new Throwable("cardDetails not provided while placing the transaction"));
        }
        if(addressDetailsVO==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","AddressDetails not provided while placing the transaction",PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails not provided while placing the transaction",new Throwable("AddressDetails not provided while placing the transaction"));
        }

        if(addressDetailsVO.getFirstname()==null|| addressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","First Name not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing the transaction",new Throwable("First Name not provided while placing the transaction"));

        }
        if(addressDetailsVO.getLastname()==null|| addressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Last Name not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing the transaction",new Throwable("Last Name not provided while placing the transaction"));

        }
        if(cardDetailsVO.getCardNum() ==null || cardDetailsVO.getCardNum().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Card NO not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing the transaction",new Throwable("Card NO not provided while placing the transaction"));
        }
        if(cardDetailsVO.getExpMonth() ==null || cardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Expiry Month not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing the transaction",new Throwable("Expiry Month not provided while placing the transaction"));
        }
        if(cardDetailsVO.getExpYear() ==null || cardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","Expiry Year not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing the transaction",new Throwable("Expiry Year not provided while placing the transaction"));
        }

        if(cardDetailsVO.getcVV() ==null || cardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerCardDetails()",null,"common","CVV not provided while placing the transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing the transaction",new Throwable("CVV not provided while placing the transaction"));
        }


        String name=addressDetailsVO.getFirstname() +" "+addressDetailsVO.getLastname() ;

        Account account = new Account();
        Expire expire =new Expire();
        account.setBrand(Functions.getCardType(cardDetailsVO.getCardNum()));
        account.setNumber(cardDetailsVO.getCardNum());
        account.setHolder(name);
        expire.setMonth(cardDetailsVO.getExpMonth());
        expire.setYear(cardDetailsVO.getExpYear());
        account.setExpire(expire);
        account.setVerification(cardDetailsVO.getcVV());

        return account;
    }

    private Payment populateCustomerTransactionDetails(GenericTransDetailsVO transDetailsVO) throws PZConstraintViolationException
    {
        if(transDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerTransactionDetails()",null,"common","TransDetails not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransDetails  not provided while placing transaction",new Throwable("TransDetails  not provided while placing transaction"));
        }
        if(transDetailsVO.getAmount() == null || transDetailsVO.getAmount().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerTransactionDetails()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(transDetailsVO.getCurrency() == null || transDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(FrickBankPaymentGateWay.class.getName(),"populateCustomerTransactionDetails()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }

        Presentation presentation = new Presentation();
        presentation.setAmount(transDetailsVO.getAmount());
        presentation.setCurrency(transDetailsVO.getCurrency());
        presentation.setUsage(transDetailsVO.getOrderId());

        Payment payment=new Payment();
        payment.setPresentation(presentation);
        return payment;
    }

    private FrickBankResponseVO populateResponseDetails(Response response)
    {
        FrickBankResponseVO frickBankResponseVO=new FrickBankResponseVO();
        String description=response.getTransaction().getProcessing().getReturn().getValue();
        frickBankResponseVO.setShortID(response.getTransaction().getIdentification().getShortID());
        frickBankResponseVO.setTimestamp(response.getTransaction().getProcessing().getTimestamp());
        frickBankResponseVO.setTransactionStatus(response.getTransaction().getProcessing().getReason().getValue());
        frickBankResponseVO.setDescription(description.replaceAll("'",""));
        frickBankResponseVO.setErrorCode(response.getTransaction().getProcessing().getReturn().getCode());
        frickBankResponseVO.setTransactionType(response.getTransaction().getProcessing().getCode());
        frickBankResponseVO.setTransactionId(response.getTransaction().getIdentification().getUniqueID());
        frickBankResponseVO.setResult(response.getTransaction().getProcessing().getReason().getValue());
        frickBankResponseVO.setResponsecode(response.getTransaction().getProcessing().getReason().getCode());
        frickBankResponseVO.setReferanceid(response.getTransaction().getIdentification().getReferenceID());
        frickBankResponseVO.setFxdate(response.getTransaction().getPayment().getClearing().getFxDate());
        return  frickBankResponseVO;
    }

    private Response processRequest(Request request) throws IOException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("", "com.payment.FrickBank.core.message");
        StringBuilder xmlOut = new StringBuilder(xstream.toXML(request));
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(Testurl);
        post.setDoAuthentication(true);
        NameValuePair[] params = {new NameValuePair("load", xmlOut.toString())};

        post.setRequestBody(params);
        post.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        httpClient.executeMethod(post);
        String responseXML = new String(post.getResponseBody());
        XStream xstream1 = new XStream();
        xstream1.autodetectAnnotations(true);
        xstream1.alias("Response", Response.class);
        Response response = (Response) xstream1.fromXML(responseXML);


        return response;
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading FrickBank Accounts Details");
        transactionLogger.info("Loading FrickBank Accounts Details");
        frickBankAccountHashtable = new Hashtable<String, FrickBankAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_frickbank", conn);
            while (rs.next())
            {
                FrickBankAccount account = new FrickBankAccount(rs);
                frickBankAccountHashtable.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(FrickBankPaymentGateWay.class.getName(),"loadPayAccounts()",null,"common","(STATIC BLOCK)SQLException while loading the gateway accounts details of FrickBank from gateway_accounts_frickbank table", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(FrickBankPaymentGateWay.class.getName(), "loadPayAccounts()", null, "common", "(STATIC BLOCK)SQLException while loading the gateway accounts details of FrickBank from gateway_accounts_frickbank table", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}

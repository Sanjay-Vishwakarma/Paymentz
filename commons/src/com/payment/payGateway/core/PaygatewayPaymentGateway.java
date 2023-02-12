package com.payment.payGateway.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub;
import org.apache.axis.AxisFault;

import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh Dani
 * Date: 12/17/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaygatewayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "Paygate";
    public String Action_Payment="payment";
    public String Action_Gateway = "1";
    private static Hashtable<String, PaygatewayAccount> PaygatewayAccounts;
    private static Logger log=new Logger(PaygatewayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(PaygatewayPaymentGateway.class.getName());
    private final static String URL = "https://paygateway.net/api/processtx.asmx?wsdl";

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    static
    {
        try
        {
            loadPayAccounts();
        }
        catch (PZDBViolationException dbe)
        {
            log.error("Error while loading gateway accounts : " + Util.getStackTrace(dbe));
            transactionLogger.error("Error while loading gateway accounts : " + Util.getStackTrace(dbe));
            PZExceptionHandler.handleDBCVEException(dbe, null, null);
            //throw new RuntimeException(e);
        }
    }
    public static PaygatewayAccount getAccountdetails(String accountId)
    {
        return PaygatewayAccounts.get(accountId);
    }

    public PaygatewayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {

        PZGenericConstraint genConstraint = new PZGenericConstraint("PaygatewayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);


    }
    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        try
        {
            validateForSale(trackingId,requestVO);
            PaygatewayAccount account= getAccountdetails(accountId);
            String emailid = addressDetailsVO.getEmail();
            String lastname = addressDetailsVO.getLastname();
            String firstname = addressDetailsVO.getFirstname();
            String street = addressDetailsVO.getStreet();
            String city = addressDetailsVO.getCity();
            String zipcode = addressDetailsVO.getZipCode();
            String state = addressDetailsVO.getState();
            String country = addressDetailsVO.getCountry();
            String phone = addressDetailsVO.getPhone();
            String birthDate = addressDetailsVO.getBirthdate();

            String amount = genericTransDetailsVO.getAmount();
            String currency = genericTransDetailsVO.getCurrency();
            String cardno = genericCardDetailsVO.getCardNum();
            String expmon = genericCardDetailsVO.getExpMonth();
            String expyr = genericCardDetailsVO.getExpYear();
            String cvv = genericCardDetailsVO.getcVV();
            String ip = addressDetailsVO.getIp();
            String prodname = genericTransDetailsVO.getOrderDesc();
            String cardtype = getCardTypeLowerCase(requestVO);

            PayGateway_ServiceSoap12Stub binding = new PayGateway_ServiceSoap12Stub();
            String descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();


            assertNotNull("binding is null", binding);

            binding.setTimeout(60000);


            String calculatedSHA = processTxSHA(account.getPassphrase(), amount, GatewayAccountService.getGatewayAccount(accountId).getMerchantId(), emailid, cardno, ip);

            com.payment.payGateway.core.message.paygateway.process.TTransacResult response = null;

            response = binding.processTx(GatewayAccountService.getGatewayAccount(accountId).getMerchantId(), GatewayAccountService.getGatewayAccount(accountId).getPassword(), calculatedSHA.trim(), Action_Payment, Action_Gateway, trackingId, emailid, lastname, firstname, street, city, zipcode, state, country, phone, "", "", "", "", "", "", "", "", prodname, amount, currency, cardtype, cardno, expmon, expyr, cvv, ip, "", "", "", birthDate, "");


            String status = "fail";

            if(response != null && response.getResp_trans_status() != null && response.getResp_trans_status().equalsIgnoreCase("00000"))
            {
                status = "success";
                //commResponseVO.setDescription(response.getResp_trans_description_status());
                commResponseVO.setDescription("Transaction Successful");
            }
            else
            {
                status="fail";
                //commResponseVO.setDescription(response.getResp_trans_description_status());
                commResponseVO.setDescription("Transaction Failed");
            }
            commResponseVO.setTransactionId(response.getResp_trans_id());
            commResponseVO.setMerchantId(response.getResp_trans_merchant_id());
            commResponseVO.setAmount(response.getResp_trans_amount());
            commResponseVO.setTransactionType(response.getResp_action_type());
            commResponseVO.setErrorCode(response.getResp_trans_status());
            //commResponseVO.setDescription(response.getResp_trans_description_status());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            log.debug(" paygateway status for sale::"+status);
        }
        catch (AxisFault axisFault)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaygatewayPaymentGateway.class.getName(),"processSale()",null,"common","AxisFault exception while placing transaction",PZTechnicalExceptionEnum.AXISFAULT,null,axisFault.getMessage(),axisFault.getCause());
        }
        catch (RemoteException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(PaygatewayPaymentGateway.class.getName(), "processSale()", null, "common", "Remote exception while placing transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaygatewayPaymentGateway.java","processSale()",null,"common","Technical Exception Occured",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO ;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private void validateForSale(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        GenericCardDetailsVO genericCardDetailsVO=commRequestVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        if(genericCardDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }
        if(genericTransDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(addressDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if(trackingId==null || trackingId.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }
        if(addressDetailsVO.getEmail()==null || addressDetailsVO.getEmail().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email Id not provided while placing transaction",new Throwable("Email Id not provided while placing transaction"));
        }
        if(addressDetailsVO.getLastname()==null || addressDetailsVO.getLastname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name  not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));
        }
        if(addressDetailsVO.getFirstname()==null || addressDetailsVO.getFirstname().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));
        }
        if(addressDetailsVO.getStreet()==null || addressDetailsVO.getStreet().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Street not provided while placing transaction",new Throwable("Street not provided while placing transaction"));
        }
        if(addressDetailsVO.getCity()==null || addressDetailsVO.getCity().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"City not provided while placing transaction",new Throwable("City not provided while placing transaction"));
        }
        if(addressDetailsVO.getZipCode()==null || addressDetailsVO.getZipCode().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","ZIP Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Zip Code not provided while placing transaction",new Throwable("Zip Code not provided while placing transaction"));
        }
        if(addressDetailsVO.getState()==null || addressDetailsVO.getState().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"State not provided while placing transaction",new Throwable("State not provided while placing transaction"));
        }
        if(addressDetailsVO.getCountry()==null || addressDetailsVO.getCountry().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Country not provided while placing transaction",new Throwable("Country not provided while placing transaction"));
        }
        if(addressDetailsVO.getPhone()==null || addressDetailsVO.getPhone().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
        }
        if(addressDetailsVO.getBirthdate()==null || addressDetailsVO.getBirthdate().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Birt Date not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Birth Date not provided while placing transaction",new Throwable("Birth Date not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount()==null || genericTransDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getCurrency()==null || genericTransDetailsVO.getCurrency().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
        }
        if(Functions.getCardType(genericCardDetailsVO.getCardNum())==null || Functions.getCardType(genericCardDetailsVO.getCardNum()).equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Card Type not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Type not provided while placing transaction",new Throwable("Card Type not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
        }
        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
        }
        if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getOrderDesc()==null || genericTransDetailsVO.getOrderDesc().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"validateForSale()",null,"common","Order Description not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order Description not provided while placing transaction",new Throwable("Order Derscription not provided while placing transaction"));
        }
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        CommRequestVO payVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=payVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        PaygatewayAccount account= getAccountdetails(accountId);
        if(commTransactionDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"processRefund()",null,"common","TransactionDetails  not provided while Refunding transaction", PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Refunding transaction",new Throwable("TransactionDetails  not provided while Refunding transaction"));
        }
        if(commTransactionDetailsVO.getAmount()==null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PaygatewayPaymentGateway.class.getName(),"processRefund()",null,"common","Amount not provided while Refunding transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding transaction",new Throwable("Amount not provided while Refunding transaction"));
        }
        String passphrase = account.getPassphrase();
        String trasid = commTransactionDetailsVO.getPreviousTransactionId();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        try
        {
            PayGateway_ServiceSoap12Stub binding = null;


                binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();


            assertNotNull("binding is null", binding);
            binding.setTimeout(60000);

            com.payment.payGateway.core.message.paygateway.process.TTransacResult res = null;
            String calculatedSHA = processRefundSHA(passphrase,accountid,trasid);
            res = binding.processRefund(accountid, GatewayAccountService.getGatewayAccount(accountId).getPassword(), calculatedSHA, trasid,commTransactionDetailsVO.getAmount(), "", "", "", "", "");

            String status = null;

            if(res != null && res.getResp_trans_status() != null && res.getResp_trans_status().equalsIgnoreCase("R0000"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }

            commResponseVO.setTransactionId(res.getResp_trans_id());
            commResponseVO.setMerchantId(res.getResp_trans_merchant_id());
            commResponseVO.setAmount(res.getResp_trans_amount());
            commResponseVO.setTransactionType(res.getResp_action_type());
            commResponseVO.setErrorCode(res.getResp_trans_status());
            commResponseVO.setDescription(res.getResp_trans_detailled_status());
            commResponseVO.setStatus(status);
        }

        catch (RemoteException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PaygatewayPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occured", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.PAYGATEWAY_REFUND);
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PaygatewayPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occured", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public static String processTxSHA(String passphrese, String amount, String accountid, String email, String cardno, String ip) throws PZTechnicalViolationException
    {
        String sha = passphrese + amount + accountid + email + cardno + ip;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch(NoSuchAlgorithmException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaygatewayPaymentGateway.java","processRefundSHA()",null,"common","Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ex.getMessage(),ex.getCause());
            //throw new RuntimeException(ex);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaygatewayPaymentGateway.java","processRefundSHA()",null,"common","Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ue.getMessage(),ue.getCause());
        }

        return hexString.toString().toLowerCase().trim();

    }

    public String processRefundSHA(String passphrese, String accountid, String trasid)throws PZTechnicalViolationException
    {

        String sha = passphrese + accountid + trasid;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch(NoSuchAlgorithmException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaygatewayPaymentGateway.java","processRefundSHA()",null,"common","Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ex.getMessage(),ex.getCause());
            //throw new RuntimeException(ex);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaygatewayPaymentGateway.java","processRefundSHA()",null,"common","Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ue.getMessage(),ue.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }
    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading paygateway Accounts......");
        transactionLogger.info("Loading paygateway Accounts......");
        PaygatewayAccounts = new Hashtable<String, PaygatewayAccount>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_paygateway", conn);
            while (rs.next())
            {
                PaygatewayAccount account = new PaygatewayAccount(rs);
                PaygatewayAccounts.put(account.getAccountid() + "", account);
            }
        }
        catch (SQLException q)
        {
            PZExceptionHandler.raiseDBViolationException("PaygatewayPaymentGateway.java","loadPayAccounts()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,q.getMessage(),q.getCause());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("PaygatewayPaymentGateway.java","loadPayAccounts()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public String getCardTypeLowerCase(GenericRequestVO requestVO)
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        String cardType = cardDetailsVO.getCardType();
        if(cardType!=null)
        {
            if(cardDetailsVO.getCardType().equals("VISA"))
            {
                cardType = "visa";
            }
            else if(cardDetailsVO.getCardType().equals("MC"))
            {
                cardType = "mastercard";
            }
            else if(cardDetailsVO.getCardType().equals("DINER"))
            {
                cardType = "diner";
            }
            else if(cardDetailsVO.getCardType().equals("AMEX"))
            {
                cardType = "amex";
            }
            else if(cardDetailsVO.getCardType().equals("DISC"))
            {
                cardType = "discover";
            }

        }

        return cardType;
    }

}
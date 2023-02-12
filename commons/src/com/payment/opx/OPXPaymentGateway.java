package com.payment.opx;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;

import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payGateway.core.PaygatewayAccount;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/3/15
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class OPXPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(OPXPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(OPXPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "OPX";
    private final static String processDebit = "https://gate.opx.io/process/debit";
    private final static String processRefund = "https://gate.opx.io/process/refund";
    private final static String processPreauthorize = "https://gate.opx.io/process/preauthorize";
    private final static String processCapture = "https://gate.opx.io/process/capture";
    private final static String processVoid = "https://gate.opx.io/process/reversal";
    private final static String processChargeback = "https://gate.opx.io/process/chargeback";
    private final static String processCredit = "https://gate.opx.io/process/credit";
    private static Hashtable<String, OPXAccounts> opxAccounts;
    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public  OPXPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static OPXAccounts getAccountdetails(String accountId)
    {
        return opxAccounts.get(accountId);
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
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = null;
        Functions functions=new Functions();
        OPXUtils opxUtils=new OPXUtils();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        OPXAccounts opxAccounts=getAccountdetails(accountId);
        String serviceKey=opxAccounts.getServiceKey();
        String routingKey=opxAccounts.getRoutingKey();
        String dob=addressDetailsVO.getBirthdate();
        String dob_Year=null;
        String dob_Month=null;
        String dob_Day=null;
        if(functions.isValueNull(dob))
        {
            dob_Year = dob.substring(0,4);
            dob_Month = dob.substring(4,6);
            dob_Day = dob.substring(6);
        }

        String req="<ProcessDebitRequest mode=\"sync\">\n" +
                "    <transactions mcTxId=\""+trackingID+"\" currency=\""+account.getCurrency()+"\" amount=\""+getAmount(genericTransDetailsVO.getAmount())+"\" usageL1=\""+genericTransDetailsVO.getOrderDesc()+"\" usageL2=\""+genericTransDetailsVO.getOrderDesc()+"\">" +
                "        <method>" +
                "            <VISA number=\""+genericCardDetailsVO.getCardNum()+"\" verification=\""+genericCardDetailsVO.getcVV()+"\" ownerFirstName=\""+addressDetailsVO.getFirstname()+"\" ownerLastName=\""+addressDetailsVO.getLastname()+"\" expireYear=\""+genericCardDetailsVO.getExpYear()+"\" expireMonth=\""+genericCardDetailsVO.getExpMonth()+"\"/>" +
                "        </method>" +
                "        <location lat=\"48.159616\" lon=\"11.586322\"/>" +
                "        <interactive redirectUrl=\"https://www.demoshop.de/order.php\" sessionId=\"\"/>" +
                "        <customer email=\""+addressDetailsVO.getEmail()+"\" ip=\""+addressDetailsVO.getIp()+"\" firstName=\""+addressDetailsVO.getFirstname()+"\" lastName=\""+addressDetailsVO.getLastname()+"\" salutation=\"Mr\" birthYear=\""+dob_Year+"\" birthMonth=\""+dob_Month+"\" birthDay=\""+dob_Day+"\">" +
                "            <addresses city=\""+addressDetailsVO.getCity()+"\" street=\""+addressDetailsVO.getStreet()+"\" streetNo=\"\" state=\""+addressDetailsVO.getState()+"\" country=\""+addressDetailsVO.getCountry()+"\" type=\"private\" extension=\"\" zip=\""+addressDetailsVO.getZipCode()+"\"/>" +
                "            <phones number=\""+addressDetailsVO.getPhone()+"\" type=\"landline_private\" country=\""+addressDetailsVO.getCountry()+"\"/>" +
                "        </customer>" +
                "    </transactions>" +
                "</ProcessDebitRequest>";
        String response = opxUtils.doPostHTTPSURLConnection(processDebit, req.trim(),serviceKey,routingKey);
        transactionLogger.error(response);
        commResponseVO = opxUtils.getResponseVo(response);
        if(commResponseVO!=null)
        {
            if("00.001.000.000".equals(commResponseVO.getStatus()))
            {
                commResponseVO.setStatus("success");
            }
            else
            {
                commResponseVO.setStatus("fail");
            }
        }
        return commResponseVO;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = null;
        OPXUtils opxUtils=new OPXUtils();
        /*GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions=new Functions();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String serviceKey=account.getMerchantId();
        String routingKey=account.getFRAUD_FTP_PASSWORD();
        String dob=addressDetailsVO.getBirthdate();
        String dob_Year=null;
        String dob_Month=null;
        String dob_Day=null;
        if(functions.isValueNull(dob))
        {
            dob_Year = dob.substring(0,4);
            dob_Month = dob.substring(4,6);
            dob_Day = dob.substring(6);
        }

        String req="<ProcessDebitRequest mode=\"sync\">\n" +
                "    <transactions mcTxId="+trackingID+" currency="+account.getCurrency()+" amount="+getAmount(genericTransDetailsVO.getAmount())+" usageL1="+genericTransDetailsVO.getOrderId()+" usageL2="+genericTransDetailsVO.getOrderDesc()+">\n" +
                "        <method>\n" +
                "            <VISA number="+genericCardDetailsVO.getCardNum()+" verification="+genericCardDetailsVO.getcVV()+" ownerFirstName="+addressDetailsVO.getFirstname()+" ownerLastName="+addressDetailsVO.getLastname()+" expireYear="+genericCardDetailsVO.getExpYear()+" expireMonth="+genericCardDetailsVO.getExpMonth()+"/>\n" +
                "        </method>\n" +
                "        <location lat=\"\" lon=\"\"/>\n" +
                "        <interactive redirectUrl="+genericTransDetailsVO.getRedirectUrl()+" sessionId=\"\"/>\n" +
                "        <customer email="+addressDetailsVO.getEmail()+" ip="+addressDetailsVO.getIp()+" firstName="+addressDetailsVO.getFirstname()+" lastName="+addressDetailsVO.getLastname()+" salutation=\"Mr\" birthYear="+dob_Year+" birthMonth="+dob_Month+" birthDay="+dob_Day+">\n" +
                "            <addresses city="+addressDetailsVO.getCity()+" street="+addressDetailsVO.getStreet()+" streetNo=\"\" state="+addressDetailsVO.getState()+" country="+addressDetailsVO.getCountry()+" type=\"\" extension=\"\" zip="+addressDetailsVO.getZipCode()+"/>\n" +
                "            <phones number="+addressDetailsVO.getPhone()+" type=\"landline_private\" country="+addressDetailsVO.getTelnocc()+"/>\n" +
                "        </customer>\n" +
                "    </transactions>\n" +
                "</ProcessDebitRequest>";*/

        String req1="<ProcessPreAuthorizeRequest>\n" +
                "    <transactions mcTxId=\"test0027345kjh45\" currency=\"USD\" trackId=\"94857\" amount=\"4321\" usageL1=\"Amazon Order: 1234\" usageL2=\"Customer Nr: 112233\">\n" +
                "        <method>\n" +
                "            <VISA number=\"4916374934428065\" verification=\"123\" ownerFirstName=\"John\" ownerLastName=\"Doe\" expireYear=\"2020\" expireMonth=\"06\"/>\n" +
                "        </method>\n" +
                "        <location lat=\"48.159616\" lon=\"11.586322\"/>\n" +
                "        <interactive redirectUrl=\"https://www.demoshop.de/order.php\" sessionId=\"3b31ef31-034b-43ce-9c97-9d727cb54ec7\"/>\n" +
                "        <customer email=\"john_jenny_doe@mac.com\" ip=\"221.17.9.27\" firstName=\"John\" lastName=\"Doe\" salutation=\"Mr\" birthYear=\"1963\" birthMonth=\"09\" birthDay=\"11\">\n" +
                "            <addresses city=\"Munich\" street=\"Leopoldstr\" streetNo=\"147\" state=\"BAY\" country=\"DE\" type=\"private\" extension=\"Suite 117\" zip=\"80802\"/>\n" +
                "            <phones number=\"+4989 12345\" type=\"landline_private\" country=\"DE\"/>\n" +
                "        </customer>\n" +
                "    </transactions>\n" +
                "</ProcessPreAuthorizeRequest>";

        String response = opxUtils.doPostHTTPSURLConnection(processPreauthorize, req1.trim(),null,null);
        //commResponseVO = opxUtils.getResponseVo(response);
        System.out.println(response);
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = null;
        OPXUtils opxUtils=new OPXUtils();
        OPXAccounts opxAccounts=getAccountdetails(accountId);
        String serviceKey=opxAccounts.getServiceKey();
        String routingKey=opxAccounts.getRoutingKey();
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        log.debug("Refund Process ON");
        String req="<ProcessRefundRequest txId=\""+genericTransDetailsVO.getPreviousTransactionId()+"\" amount=\""+getAmount(genericTransDetailsVO.getAmount())+"\"/>";
        log.debug("Refund Process ON "+req);
        String response = opxUtils.doPostHTTPSURLConnection(processRefund, req.trim(),serviceKey,routingKey);
        System.out.println(response);
        log.debug("Refund Process ON "+response);
        commResponseVO = opxUtils.getRefundResponseVo(response);
        if(commResponseVO!=null)
        {
            if("00.001.000.000".equals(commResponseVO.getStatus()))
            {
                commResponseVO.setStatus("success");
            }
            else
            {
                commResponseVO.setStatus("fail");
            }
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = null;
        OPXUtils opxUtils=new OPXUtils();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        OPXAccounts opxAccounts=getAccountdetails(accountId);
        String serviceKey=opxAccounts.getServiceKey();
        String routingKey=opxAccounts.getRoutingKey();
        String req="<ProcessCaptureRequest txId=\"\" amount=\"\">" +
                "<method>" +
                "<VISA number=\"4916374934428065\" verification=\"123\" ownerFirstName=\"John\" ownerLastName=\"Doe\" expireYear=\"2020\" expireMonth=\"06\"/>\n" +
                "</method>   " +
                "</ProcessCaptureRequest>";
        String response = null;

            response = opxUtils.doPostHTTPSURLConnection(processCapture, req.trim(),serviceKey,routingKey);


        commResponseVO = opxUtils.getResponseVo(response);
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("OPXPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public static void loadPayAccounts() throws PZDBViolationException
    {
        log.info("Loading paygateway Accounts......");
        transactionLogger.info("Loading paygateway Accounts......");
        opxAccounts = new Hashtable<String, OPXAccounts>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_accounts_opx", conn);
            while (rs.next())
            {
                OPXAccounts account = new OPXAccounts(rs);
                opxAccounts.put(account.getAccountid() + "", account);
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

    public String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
}

package com.payment.paynetics.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by admin on 9/5/2017.
 */
public class PayneticsPaymentProcess extends CommonPaymentProcess
{
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.paynetics");
    private static TransactionLogger transactionLogger = new TransactionLogger(PayneticsPaymentProcess.class.getName());
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO           = null;
        CommTransactionDetailsVO commTransactionDetailsVO   = null;
        CommCardDetailsVO commCardDetailsVO                 = null;
        CommMerchantVO commMerchantVO                       = null;


        if(requestVO.getTransDetailsVO() != null){
            commTransactionDetailsVO = requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO = new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO() != null){
            commAddressDetailsVO = requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO = new CommAddressDetailsVO();

        }if(requestVO.getCardDetailsVO() != null){
        commCardDetailsVO = requestVO.getCardDetailsVO();
    }else{
        commCardDetailsVO = new CommCardDetailsVO();
    }
        if(requestVO.getCommMerchantVO() != null){
            commMerchantVO = requestVO.getCommMerchantVO();
        }else{
            commMerchantVO = new CommMerchantVO();
        }

        Integer trackingId  = cancelRequest.getTrackingId();

        Hashtable transDetails  = getTransactionDetails(String.valueOf(trackingId), "authsuccessful");

        commTransactionDetailsVO.setAmount((String) transDetails.get("amount"));
        commTransactionDetailsVO.setCurrency((String) transDetails.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transDetails.get("paymentid"));
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));

        commCardDetailsVO.setExpMonth((String) transDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transDetails.get("expyear"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));

        commAddressDetailsVO.setFirstname((String) transDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transDetails.get("country"));
        commAddressDetailsVO.setCity((String) transDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transDetails.get("zip"));
        commAddressDetailsVO.setState((String)transDetails.get("state"));
        commAddressDetailsVO.setStreet((String)transDetails.get("address"));

        commMerchantVO.setAccountId((String)transDetails.get("accountid"));

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

    }
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;
        CommCardDetailsVO commCardDetailsVO=null;
        CommMerchantVO commMerchantVO=null;

        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }if(requestVO.getCardDetailsVO()!=null){
        commCardDetailsVO=requestVO.getCardDetailsVO();
    }else{
        commCardDetailsVO=new CommCardDetailsVO();
    }
        if(requestVO.getCommMerchantVO()!=null){
            commMerchantVO=requestVO.getCommMerchantVO();
        }else{
            commMerchantVO=new CommMerchantVO();
        }

        Integer trackingId=captureRequest.getTrackingId();

        Hashtable transDetails= getTransactionDetails(String.valueOf(trackingId), "authsuccessful");

        commTransactionDetailsVO.setAmount((String) transDetails.get("amount"));
        commTransactionDetailsVO.setCurrency((String) transDetails.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transDetails.get("paymentid"));
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));

        commCardDetailsVO.setExpMonth((String) transDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transDetails.get("expyear"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));

        commAddressDetailsVO.setFirstname((String) transDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transDetails.get("country"));
        commAddressDetailsVO.setCity((String) transDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transDetails.get("zip"));
        commAddressDetailsVO.setState((String)transDetails.get("state"));
        commAddressDetailsVO.setStreet((String)transDetails.get("address"));

        commMerchantVO.setAccountId((String)transDetails.get("accountid"));

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

    }
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        transactionLogger.error("Entered into setRefundVOParamsextension");
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;
        CommCardDetailsVO commCardDetailsVO=null;
        CommMerchantVO commMerchantVO=null;

        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }if(requestVO.getCardDetailsVO()!=null){
        commCardDetailsVO=requestVO.getCardDetailsVO();
    }else{
        commCardDetailsVO=new CommCardDetailsVO();
    }
        if(requestVO.getCommMerchantVO()!=null){
            commMerchantVO=requestVO.getCommMerchantVO();
        }else{
            commMerchantVO=new CommMerchantVO();
        }

        Integer trackingId=refundRequest.getTrackingId();
        Hashtable transDetails= getTransactionDetails(String.valueOf(trackingId), "capturesuccess");

        //commTransactionDetailsVO.setAmount((String)transDetails.get("amount"));
        commTransactionDetailsVO.setCurrency((String) transDetails.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transDetails.get("paymentid"));
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));

        commCardDetailsVO.setExpMonth((String) transDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transDetails.get("expyear"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));

        commAddressDetailsVO.setFirstname((String) transDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transDetails.get("country"));
        commAddressDetailsVO.setCity((String) transDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transDetails.get("zip"));
        commAddressDetailsVO.setState((String)transDetails.get("state"));
        commAddressDetailsVO.setStreet((String)transDetails.get("address"));

        commMerchantVO.setAccountId((String)transDetails.get("accountid"));

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

    }

    public void setPayneticsRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
    {
        Functions functions= new Functions();
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;
        CommCardDetailsVO commCardDetailsVO=null;
        CommMerchantVO commMerchantVO=null;


        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }if(requestVO.getCardDetailsVO()!=null){
        commCardDetailsVO=requestVO.getCardDetailsVO();
    }else{
        commCardDetailsVO=new CommCardDetailsVO();
    }
        if(requestVO.getCommMerchantVO()!=null){
            commMerchantVO=requestVO.getCommMerchantVO();
        }else{
            commMerchantVO=new CommMerchantVO();
        }

        Hashtable transDetails= getTransactionDetails(trackingId,"authstarted");

        commTransactionDetailsVO.setAmount((String)transDetails.get("amount"));
        commTransactionDetailsVO.setCurrency((String) transDetails.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String) transDetails.get("transid"));
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));
        commTransactionDetailsVO.setRedirectUrl((String) transDetails.get("redirecturl"));
        commTransactionDetailsVO.setPrevTransactionStatus("authstarted");
        commTransactionDetailsVO.setTemplateAmount((String) transDetails.get("templateamount"));
        commTransactionDetailsVO.setTemplateCurrency((String) transDetails.get("templatecurrency"));
        commTransactionDetailsVO.setPaymentType((String) transDetails.get("paymodeid"));
        commTransactionDetailsVO.setCardType((String) transDetails.get("cardtypeid"));

        commCardDetailsVO.setExpMonth((String) transDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transDetails.get("expyear"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));

        commAddressDetailsVO.setFirstname((String)transDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transDetails.get("country"));
        commAddressDetailsVO.setCity((String) transDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transDetails.get("zip"));
        commAddressDetailsVO.setState((String) transDetails.get("state"));
        commAddressDetailsVO.setStreet((String) transDetails.get("address"));
        commMerchantVO.setAccountId((String) transDetails.get("accountid"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }

    public void setPayneticsRequestVO(CommRequestVO requestVO, String trackingId, String cvv) throws PZDBViolationException
    {
        Functions functions                                 = new Functions();
        CommAddressDetailsVO commAddressDetailsVO           = null;
        CommTransactionDetailsVO commTransactionDetailsVO   = null;
        CommCardDetailsVO commCardDetailsVO                 = null;
        CommMerchantVO commMerchantVO                       = null;


        if (requestVO.getTransDetailsVO() != null)
        {
            commTransactionDetailsVO = requestVO.getTransDetailsVO();
        }
        else
        {
            commTransactionDetailsVO = new CommTransactionDetailsVO();
        }
        if (requestVO.getAddressDetailsVO() != null)
        {
            commAddressDetailsVO = requestVO.getAddressDetailsVO();
        }
        else
        {
            commAddressDetailsVO = new CommAddressDetailsVO();

        }
        if (requestVO.getCardDetailsVO() != null)
        {
            commCardDetailsVO = requestVO.getCardDetailsVO();
        }
        else
        {
            commCardDetailsVO = new CommCardDetailsVO();
        }
        if (requestVO.getCommMerchantVO() != null)
        {
            commMerchantVO = requestVO.getCommMerchantVO();
        }
        else
        {
            commMerchantVO = new CommMerchantVO();
        }

        Hashtable transDetails = getTransactionDetails(trackingId, "authstarted");

        commTransactionDetailsVO.setAmount((String) transDetails.get("amount"));
        commTransactionDetailsVO.setCurrency((String) transDetails.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String) transDetails.get("transid"));
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));
        commTransactionDetailsVO.setRedirectUrl((String) transDetails.get("redirecturl"));
        commTransactionDetailsVO.setPrevTransactionStatus((String) transDetails.get("transactionstatus"));
        commTransactionDetailsVO.setPaymentType((String) transDetails.get("paymodeid"));
        commTransactionDetailsVO.setCardType((String) transDetails.get("cardtypeid"));
        commTransactionDetailsVO.setCustomerId((String) transDetails.get("customerId"));
        commTransactionDetailsVO.setNotificationUrl((String) transDetails.get("notificationUrl"));
        commTransactionDetailsVO.setTerminalId((String) transDetails.get("terminalId"));
        commTransactionDetailsVO.setVersion((String) transDetails.get("version"));
        commTransactionDetailsVO.setRedirectMethod((String) transDetails.get("hrcode"));

        commCardDetailsVO.setExpMonth((String) transDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transDetails.get("expyear"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));
        commCardDetailsVO.setcVV(cvv);

        commAddressDetailsVO.setFirstname((String) transDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transDetails.get("country"));
        commAddressDetailsVO.setCity((String) transDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transDetails.get("zip"));
        commAddressDetailsVO.setState((String) transDetails.get("state"));
        commAddressDetailsVO.setStreet((String) transDetails.get("address"));
        commAddressDetailsVO.setTmpl_amount((String) transDetails.get("templateamount"));
        commAddressDetailsVO.setTmpl_currency((String) transDetails.get("templatecurrency"));

        commMerchantVO.setAccountId((String) transDetails.get("accountid"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }
    private Hashtable getTransactionDetails(String trackingid, String status) throws PZDBViolationException
    {
        PreparedStatement preparedStatement=null;
        ResultSet resultSet = null;
        Connection connection=null;
        Hashtable transDetail=new Hashtable();
        Functions  functions=new Functions();
        String amount=null;
        String firstname=null;
        String lastname=null;
        String name=null;
        String ccnum=null;
        String country=null;
        String city=null;
        String expdate=null;

        String ipaddress=null;
        String currency=null;
        String emailaddr=null;
        String address=null;
        String state=null;
        String responsecode=null;
        String zip=null;
        String telnocc=null;
        String telno=null;
        String accountId=null;
        String orderdescription=null;
        String description=null;
        String paymentId=null;
        String toId=null;
        String redirectUrl=null;
        String templateamount=null;
        String templatecurrency=null;
        String paymodeid=null;
        String cardtypeid=null;
        String transactionStatus = null;
        String customerId=null;
        String notificationUrl=null;
        String version=null;
        String terminalId=null;

        try
        {
            connection= Database.getConnection();
            String qry = "SELECT t.accountid,t.street,t.state,t.zip,t.amount,t.status as transactionstatus,d.status,t.firstname,t.lastname,t.name,t.ccnum,t.country,t.city,t.expdate,d.ipaddress,t.currency,t.emailaddr,d.responsecode,t.telnocc,t.telno,d.responsetransactionid,t.orderdescription,t.description,t.toid,t.paymentid,t.redirecturl,t.templateamount,t.templatecurrency,t.paymodeid,t.cardtypeid,t.customerId,t.notificationUrl,t.terminalid,t.version,t.hrcode FROM transaction_common AS t,transaction_common_details AS d WHERE t.trackingid=d.trackingid AND t.trackingid=? AND d.status IN (?)";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2,status);
            resultSet= preparedStatement.executeQuery();

            if(resultSet.next())
            {
                amount=resultSet.getString("amount");
                firstname=resultSet.getString("firstname");
                lastname=resultSet.getString("lastname");
                name=resultSet.getString("name");
                ccnum= PzEncryptor.decryptPAN(resultSet.getString("ccnum"));

                country=resultSet.getString("country");
                city=resultSet.getString("city");
                expdate= PzEncryptor.decryptExpiryDate(resultSet.getString("expdate"));
                ipaddress=resultSet.getString("ipaddress");
                currency=resultSet.getString("currency");
                emailaddr=resultSet.getString("emailaddr");
                address=resultSet.getString("street");
                state=resultSet.getString("state");
                zip=resultSet.getString("zip");
                telnocc=resultSet.getString("telnocc");
                telno=resultSet.getString("telno");
                accountId=resultSet.getString("accountid");
                orderdescription=resultSet.getString("orderdescription");
                description=resultSet.getString("description");
                toId=resultSet.getString("toid");
                paymentId=resultSet.getString("paymentid");
                redirectUrl=resultSet.getString("redirecturl");
                templateamount=resultSet.getString("templateamount");
                templatecurrency=resultSet.getString("templatecurrency");
                paymodeid=resultSet.getString("paymodeid");
                cardtypeid=resultSet.getString("cardtypeid");
                transactionStatus = resultSet.getString("transactionstatus");
                customerId=resultSet.getString("customerId");
                notificationUrl=resultSet.getString("notificationUrl");
                terminalId=resultSet.getString("terminalid");
                version=resultSet.getString("version");

                String temp[]=expdate.split("/");
                if(functions.isValueNull(temp[0]))
                {
                    transDetail.put("expmonth",temp[0]);
                }
                if(functions.isValueNull(temp[1]))
                {
                    transDetail.put("expyear",temp[1]);
                }
                if(functions.isValueNull(amount))
                {
                    transDetail.put("amount",amount);
                }
                if(functions.isValueNull(firstname))
                {
                    transDetail.put("firstname",firstname);
                }
                if(functions.isValueNull(lastname))
                {
                    transDetail.put("lastname",lastname);
                }
                if(functions.isValueNull(name))
                {
                    transDetail.put("name",name);
                }
                if(functions.isValueNull(ccnum))
                {
                    transDetail.put("ccnum",ccnum);
                }
                if(functions.isValueNull(country))
                {
                    transDetail.put("country",country);
                }
                if(functions.isValueNull(city))
                {
                    transDetail.put("city",city);
                }
                if(functions.isValueNull(expdate))
                {
                    transDetail.put("expdate",expdate);
                }
                if(functions.isValueNull(ipaddress))
                {
                    transDetail.put("ipaddress",ipaddress);
                }
                if(functions.isValueNull(currency))
                {
                    transDetail.put("currency",currency);
                }
                if(functions.isValueNull(emailaddr))
                {
                    transDetail.put("emailaddr",emailaddr);
                }
                if(functions.isValueNull(address))
                {
                    transDetail.put("address",address);
                }
                if(functions.isValueNull(state))
                {
                    transDetail.put("state",state);
                }
                if(functions.isValueNull(zip))
                {
                    transDetail.put("zip",zip);
                }
                if(functions.isValueNull(telnocc))
                {
                    transDetail.put("telnocc",telnocc);
                }
                if(functions.isValueNull(telno))
                {
                    transDetail.put("telno",telno);
                }
                if(functions.isValueNull(accountId))
                {
                    transDetail.put("accountid",accountId);
                }
                if(functions.isValueNull(orderdescription))
                {
                    transDetail.put("orderdescription",orderdescription);
                }
                if(functions.isValueNull(description))
                {
                    transDetail.put("description",description);
                }
                if(functions.isValueNull(toId))
                {
                    transDetail.put("toid",toId);
                }
                if(functions.isValueNull(paymentId))
                {
                    transDetail.put("paymentid",paymentId);
                }
                if(functions.isValueNull(redirectUrl))
                {
                    transDetail.put("redirecturl",redirectUrl);
                }
                if(functions.isValueNull(templateamount))
                {
                    transDetail.put("templateamount",templateamount);
                }
                if(functions.isValueNull(templatecurrency))
                {
                    transDetail.put("templatecurrency",templatecurrency);
                }
                if(functions.isValueNull(paymodeid))
                {
                    transDetail.put("paymodeid",paymodeid);
                }
                if(functions.isValueNull(cardtypeid))
                {
                    transDetail.put("cardtypeid",cardtypeid);
                }
                if (functions.isValueNull(transactionStatus))
                {
                    transDetail.put("transactionstatus", transactionStatus);
                }
                if (functions.isValueNull(customerId))
                {
                    transDetail.put("customerId", customerId);
                }
                if (functions.isValueNull(notificationUrl))
                {
                    transDetail.put("notificationUrl", notificationUrl);
                }
                if (functions.isValueNull(terminalId))
                {
                    transDetail.put("terminalId", terminalId);
                }
                if (functions.isValueNull(version))
                {
                    transDetail.put("version", version);
                }
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError in PayneticsPaymentProcess---",systemError);
            PZExceptionHandler.raiseDBViolationException(PayneticsPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in PayneticsPaymentProcess---", e);
            PZExceptionHandler.raiseDBViolationException(PayneticsPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }

        return transDetail;
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        StringBuffer form = new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">");
        if(functions.isValueNull(response3D.getPaReq()))
            form.append("<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">");
        if(functions.isValueNull(response3D.getTerURL()))
            form.append("<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">");
        if(functions.isValueNull(response3D.getMd()))
            form.append("<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">");
        if(functions.isValueNull(response3D.getCreq()))
            form.append("<input type=\"hidden\" name=\"creq\" value=\""+response3D.getCreq()+"\">");
        if(functions.isValueNull(response3D.getThreeDSSessionData()))
            form.append("<input type=\"hidden\" name=\"threeDSSessionData\" value=\""+response3D.getThreeDSSessionData()+"\">");
        if(functions.isValueNull(response3D.getThreeDSServerTransID()))
            form.append("<input type=\"hidden\" name=\"threeDSServerTransID\" value=\""+response3D.getThreeDSServerTransID()+"\">");
        form.append("</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>");
        return form.toString();
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        StringBuffer form = new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">");
                if(functions.isValueNull(response3D.getPaReq()))
                form.append("<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">");
                if(functions.isValueNull(response3D.getTerURL()))
                form.append("<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">");
                if(functions.isValueNull(response3D.getMd()))
                form.append("<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">");
                if(functions.isValueNull(response3D.getCreq()))
                form.append("<input type=\"hidden\" name=\"creq\" value=\""+response3D.getCreq()+"\">");
                if(functions.isValueNull(response3D.getThreeDSSessionData()))
                form.append("<input type=\"hidden\" name=\"threeDSSessionData\" value=\""+response3D.getThreeDSSessionData()+"\">");
                if(functions.isValueNull(response3D.getThreeDSServerTransID()))
                form.append("<input type=\"hidden\" name=\"threeDSServerTransID\" value=\""+response3D.getThreeDSServerTransID()+"\">");
                form.append("</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>");
        return form.toString();
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---" + response3D.getUrlFor3DRedirect());

        if (functions.isValueNull(response3D.getCreq()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("creq");
            asyncParameterVO.setValue(response3D.getCreq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getThreeDSSessionData()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("threeDSSessionData");
            asyncParameterVO.setValue(response3D.getThreeDSSessionData());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getPaReq()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("PaReq");
            asyncParameterVO.setValue(response3D.getPaReq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getTerURL()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("TermUrl");
            asyncParameterVO.setValue(response3D.getTerURL());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }

        if(functions.isValueNull(response3D.getMd())){
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("MD");
            asyncParameterVO.setValue(response3D.getMd());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getThreeDSServerTransID()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("threeDSServerTransID");
            asyncParameterVO.setValue(response3D.getThreeDSServerTransID());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public void setEnrollmentRequestVOExtention(EnrollmentRequestVO enrollmentRequestVO,TransactionDetailsVO transactionDetailsVO)
    {
        Functions functions=new Functions();
        PayneticsUtils payneticsUtils=new PayneticsUtils();
        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(transactionDetailsVO.getAccountId());
        enrollmentRequestVO.setMid(gatewayAccountVO.getMpiMid());
        if(functions.isValueNull(enrollmentRequestVO.getHostUrl()))
            enrollmentRequestVO.setTermUrl("https://"+enrollmentRequestVO.getHostUrl()+RB.getString("HOST_URL"));
        else
            enrollmentRequestVO.setTermUrl(RB.getString("TERM_URL_3DS"));
    }



}

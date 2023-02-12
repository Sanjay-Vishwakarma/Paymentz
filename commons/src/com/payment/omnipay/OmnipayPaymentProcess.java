package com.payment.omnipay;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Admin on 2022-01-24.
 */
public class OmnipayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(OmnipayPaymentProcess.class.getName());
    private static Functions function = new Functions();

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionlogger.error("Omnipay 3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "";
        String responseType = "";
        if(response3D.getRequestMap() != null && response3D.getRequestMap().containsKey("responseType")){
            responseType =  response3D.getRequestMap().getOrDefault("responseType", "");
        }
        if (responseType.equalsIgnoreCase("REDIRECT"))
        {
            form += "<form name=\"launch3D\" method=\"GET\" action=\"" + response3D.getUrlFor3DRedirect() + "\">";
            form += "</form><script language=\"javascript\">window.location=\"" + response3D.getUrlFor3DRedirect() + "\";</script>";
        }
        else
        {
            form += "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                    "<input type=\"hidden\" name=\"ACSUrl\" value=\"" + response3D.getRedirectUrl() + "\">" +
                    "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                    "<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                    "<input type=\"hidden\" name=\"CReq\" value=\"" + response3D.getCreq() + "\">" +
                    "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">" +
                    "</form>" +
                    "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        }
        transactionlogger.error("form for "+trackingId+"---->"+" responseType >>> "+responseType+" >>>>>>>>> "+form);
        return form;
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside OmnipayPaymentProcess --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside OmnipayPaymentProcess getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside  OmnipayPaymentProcess---"+response3D.getUrlFor3DRedirect());

        asyncParameterVO    = new AsyncParameterVO();
        asyncParameterVO.setName("ACSUrl");
        asyncParameterVO.setValue(response3D.getRedirectUrl());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO    = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO    = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO    = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO    = new AsyncParameterVO();
        asyncParameterVO.setName("CReq");
        asyncParameterVO.setValue(response3D.getCreq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        String responseType = "";
        if(response3D.getRequestMap() != null && response3D.getRequestMap().containsKey("responseType")){
            responseType =  response3D.getRequestMap().getOrDefault("responseType", "");
        }

        if (responseType.equalsIgnoreCase("REDIRECT")){
            directKitResponseVO.setMethod("GET");
        }else {
            directKitResponseVO.setMethod("POST");
        }
        transactionlogger.error(directKitResponseVO.getTrackingId()+">>>>> responseType:"+responseType+" -->> "+"Method:"+directKitResponseVO.getMethod());
    }

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO           = requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = requestVO.getCardDetailsVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionlogger.debug("Entering ActionEntry for OmnipayPaymentProcess");
        System.out.println("entering");

        int results=0;
        Connection cn = null;

        try
        {
            String ifsc="";
            String bankaccount="";
            String fullname="";
            String bankRefNo="";
            String spkRefNo="";
            if(responseVO!=null){
                ifsc=responseVO.getIfsc();
                bankaccount=responseVO.getBankaccount();
                fullname=responseVO.getFullname();
                bankRefNo=responseVO.getBankRefNo();
                spkRefNo=responseVO.getSpkRefNo();
            }
            if(commRequestVO!=null){
                ifsc=commRequestVO.getTransDetailsVO().getBankIfsc();
                bankaccount=commRequestVO.getTransDetailsVO().getBankAccountNo();
                fullname=commRequestVO.getTransDetailsVO().getCustomerBankAccountName();

            }

            cn = Database.getConnection();
            String sql = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,dtstamp) values (?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, fullname);
            pstmt.setString(3, bankaccount);
            pstmt.setString(4, ifsc);
            pstmt.setString(5,amount);
            pstmt.setString(6,status);

            results = pstmt.executeUpdate();
            transactionlogger.error("SqlQuery Omnipay :-----" + pstmt);

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
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

    private Hashtable getTransactionDetails(String trackingid, String status) throws PZDBViolationException
    {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet                 = null;
        Connection connection               = null;
        Hashtable transDetail               = new Hashtable();
        Functions functions                 = new Functions();
        String amount                       = null;
        String firstname                    = null;
        String lastname                     = null;
        String name                         = null;
        String ccnum                        = null;
        String country                      = null;
        String city                         = null;
        String expdate                      = null;
        String ipaddress                    = null;
        String currency                     = null;
        String emailaddr                    = null;
        String address                      = null;
        String state                        = null;
        String responsecode                 = null;
        String zip                          = null;
        String telnocc                      = null;
        String telno                        = null;
        String accountId                    = null;
        String orderdescription             = null;
        String description                  = null;
        String paymentId                    = null;
        String toId                         = null;
        String redirectUrl                  = null;
        String templateamount               = null;
        String templatecurrency             = null;
        String paymodeid                    = null;
        String cardtypeid                   = null;
        String transactionStatus            = null;
        String customerId                   = null;
        String notificationUrl              = null;
        String version                      = null;
        String terminalId                   = null;
        String toType                       = null;

        try
        {
            connection= Database.getConnection();
            String qry          = "SELECT t.totype,t.accountid,t.street,t.state,t.zip,t.amount,t.status as transactionstatus,d.status,t.firstname,t.lastname,t.name,t.ccnum,t.country,t.city,t.expdate,d.ipaddress,t.currency,t.emailaddr,d.responsecode,t.telnocc,t.telno,d.responsetransactionid,t.orderdescription,t.description,t.toid,t.paymentid,t.redirecturl,t.templateamount,t.templatecurrency,t.paymodeid,t.cardtypeid,t.customerId,t.notificationUrl,t.terminalid,t.version FROM transaction_common AS t,transaction_common_details AS d WHERE t.trackingid=d.trackingid AND t.trackingid=? AND d.status IN (?)";
            preparedStatement   =   connection.prepareStatement(qry);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2,status);
            resultSet= preparedStatement.executeQuery();

            if(resultSet.next())
            {
                amount      = resultSet.getString("amount");
                firstname   = resultSet.getString("firstname");
                lastname    = resultSet.getString("lastname");
                name        = resultSet.getString("name");
                ccnum       = PzEncryptor.decryptPAN(resultSet.getString("ccnum"));
                country     = resultSet.getString("country");
                city        = resultSet.getString("city");
                expdate     = PzEncryptor.decryptExpiryDate(resultSet.getString("expdate"));
                ipaddress   = resultSet.getString("ipaddress");
                currency    = resultSet.getString("currency");
                emailaddr   = resultSet.getString("emailaddr");
                address     = resultSet.getString("street");
                state       = resultSet.getString("state");
                zip         = resultSet.getString("zip");
                telnocc     = resultSet.getString("telnocc");
                telno       = resultSet.getString("telno");
                accountId   = resultSet.getString("accountid");
                orderdescription    = resultSet.getString("orderdescription");
                description         = resultSet.getString("description");
                toId                = resultSet.getString("toid");
                toType              = resultSet.getString("totype");
                paymentId           = resultSet.getString("paymentid");
                redirectUrl         = resultSet.getString("redirecturl");
                templateamount      = resultSet.getString("templateamount");
                templatecurrency    = resultSet.getString("templatecurrency");
                paymodeid           = resultSet.getString("paymodeid");
                cardtypeid          = resultSet.getString("cardtypeid");
                transactionStatus   = resultSet.getString("transactionstatus");
                customerId          = resultSet.getString("customerId");
                notificationUrl     = resultSet.getString("notificationUrl");
                terminalId          = resultSet.getString("terminalid");
                version             = resultSet.getString("version");

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
                if(functions.isValueNull(toType))
                {
                    transDetail.put("totype",toType);
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
            transactionlogger.error("SystemError in OmnipayPaymentProcess---",systemError);
            PZExceptionHandler.raiseDBViolationException(OmnipayPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionlogger.error("SQLException in OmnipayPaymentProcess---", e);
            PZExceptionHandler.raiseDBViolationException(OmnipayPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }

        return transDetail;
    }

    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = new CommCardDetailsVO();

        int trackingid  =   refundRequest.getTrackingId();
        transactionlogger.error("trackingid in omnipay----" + refundRequest.getTrackingId());

        Hashtable transdetail = getTransactionDetails(String.valueOf(trackingid),"capturesuccess");

        //commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commTransactionDetailsVO.setAmount(refundRequest.getRefundAmount());
        commTransactionDetailsVO.setCurrency((String) transdetail.get("currency"));
        if (function.isValueNull(requestVO.getTransDetailsVO().getOrderDesc()))
            commTransactionDetailsVO.setOrderDesc(requestVO.getTransDetailsVO().getOrderDesc());
        if (function.isValueNull(String.valueOf(transdetail.get("paymentid"))))
            commTransactionDetailsVO.setPreviousTransactionId((String) transdetail.get("paymentid"));
        else
            commTransactionDetailsVO.setPreviousTransactionId((String) transdetail.get("transid"));

        commTransactionDetailsVO.setTotype((String) transdetail.get("totype"));

        commAddressDetailsVO.setFirstname((String) transdetail.get("firstname"));
        commAddressDetailsVO.setLastname((String) transdetail.get("lastname"));
        commAddressDetailsVO.setCountry((String) transdetail.get("country"));
        commAddressDetailsVO.setCity((String) transdetail.get("city"));
        commAddressDetailsVO.setEmail((String)transdetail.get("emailaddr"));
        commAddressDetailsVO.setIp((String)transdetail.get("ipaddress"));
        commAddressDetailsVO.setPhone((String)transdetail.get("telno"));
        commAddressDetailsVO.setZipCode((String)transdetail.get("zip"));
        commAddressDetailsVO.setState((String)transdetail.get("state"));
        commAddressDetailsVO.setStreet((String)transdetail.get("address"));
        commCardDetailsVO.setExpMonth((String) transdetail.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transdetail.get("expyear"));
        commCardDetailsVO.setCardNum((String) transdetail.get("ccnum"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

}

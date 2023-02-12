package com.payment.appletree;

import com.directi.pg.*;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Admin on 8/17/2021.
 */
public class AppletreeCellulantPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger = new TransactionLogger(AppletreeCellulantPaymentProcess.class.getName());
    private static Functions functions  = new Functions();

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target="target=\"_blank\"";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        transactionLogger.error("form---->"+form);
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        transactionLogger.error("form---->"+form);
        return form;
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

    }

    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = new CommCardDetailsVO();

        int trackingid  =   refundRequest.getTrackingId();
        transactionLogger.error("trackingid in boombill----" + refundRequest.getTrackingId());

        Hashtable transdetail = getTransactionDetails(String.valueOf(trackingid),"capturesuccess");

        //commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commTransactionDetailsVO.setAmount(refundRequest.getRefundAmount());
        commTransactionDetailsVO.setCurrency((String) transdetail.get("currency"));
        commTransactionDetailsVO.setPreviousTransactionId((String) transdetail.get("paymentid"));
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
            connection          = Database.getConnection();
            String qry          = "SELECT t.accountid,t.street,t.state,t.zip,t.amount,t.status as transactionstatus,d.status,t.firstname,t.lastname,t.name,t.ccnum,t.country,t.city,t.expdate,d.ipaddress,t.currency,t.emailaddr,d.responsecode,t.telnocc,t.telno,d.responsetransactionid,t.orderdescription,t.description,t.toid,t.paymentid,t.redirecturl,t.templateamount,t.templatecurrency,t.paymodeid,t.cardtypeid,t.customerId,t.notificationUrl,t.terminalid,t.version FROM transaction_common AS t,transaction_common_details AS d WHERE t.trackingid=d.trackingid AND t.trackingid=? AND d.status IN (?)";
            preparedStatement   = connection.prepareStatement(qry);
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

                String temp[] = expdate.split("/");
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
            transactionLogger.error("SystemError in BoomBill---",systemError);
            PZExceptionHandler.raiseDBViolationException(AppletreeCellulantPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in BoomBill---", e);
            PZExceptionHandler.raiseDBViolationException(AppletreeCellulantPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }

        return transDetail;
    }
}

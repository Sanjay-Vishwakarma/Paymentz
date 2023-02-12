package com.payment.npayon;

import com.directi.pg.*;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by admin on 6/23/2018.
 */
public class NPayOnPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(NPayOnPaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\"" + target + ">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">" +
                "<input type=\"hidden\" name=\"connector\" value=\"" + response3D.getConnector() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        transactionLogger.error("form::::::::" + form);
        return form;
    }


    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)//new
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name='launch3D' method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+ "\">"+
                "<input type=\"hidden\" name=\"creq\" value=\"" + response3D.getCreq() + "\">" +
                "<input type=\"hidden\" name=\"threeDSSessionData\" value=\"" + response3D.getThreeDSSessionData() + "\">" +
                "<input type=\"hidden\" name=\"threeDSMethodData\" value=\"" + response3D.getThreeDSMethodData() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">" +
                "<input type=\"hidden\" name=\"connector\" value=\"" + response3D.getConnector() + "\">" +
                "</form>" +

               "<script language=\"javascript\">document.launch3D.submit();</script>";
        transactionLogger.error("form::::::::" + form);
        transactionLogger.error("launch3D url::::::::" + response3D.getUrlFor3DRedirect());
        transactionLogger.error("PaReq::::::::" + response3D.getPaReq());
        transactionLogger.error("TermUrl::::::::" + response3D.getTerURL());
        transactionLogger.error("MD::::::::" + response3D.getMd());
        transactionLogger.error("Connector::::::::" + response3D.getConnector());
        transactionLogger.error("creq::::::::" + response3D.getCreq());
        transactionLogger.error("threeDSSessionData::::::::" + response3D.getThreeDSSessionData());
        transactionLogger.error("threeDSMethodData::::::::" + response3D.getThreeDSMethodData());
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---" + response3D.getUrlFor3DRedirect());

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

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("connector");
        asyncParameterVO.setValue(response3D.getConnector());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("threeDSSessionData");
        asyncParameterVO.setValue(response3D.getThreeDSSessionData());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("threeDSMethodData");
        asyncParameterVO.setValue(response3D.getThreeDSMethodData());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("creq");
        asyncParameterVO.setValue(response3D.getCreq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public void setNPayOnInquiryRequestVO(CommRequestVO requestVO, String trackingId, String bankTransactionId) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO = null;
        CommTransactionDetailsVO commTransactionDetailsVO = null;
        CommCardDetailsVO commCardDetailsVO = null;
        CommMerchantVO commMerchantVO = null;


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
        commTransactionDetailsVO.setPreviousTransactionId(bankTransactionId);
        commTransactionDetailsVO.setOrderDesc((String) transDetails.get("orderdescription"));
        commTransactionDetailsVO.setOrderId((String) transDetails.get("description"));
        commTransactionDetailsVO.setToId((String) transDetails.get("toid"));
        commTransactionDetailsVO.setRedirectUrl((String) transDetails.get("redirecturl"));
        commTransactionDetailsVO.setPrevTransactionStatus("authstarted");
        commTransactionDetailsVO.setTemplateAmount((String) transDetails.get("templateamount"));
        commTransactionDetailsVO.setTemplateCurrency((String) transDetails.get("templatecurrency"));
        commTransactionDetailsVO.setPaymentType((String) transDetails.get("paymodeid"));
        commTransactionDetailsVO.setCardType((String) transDetails.get("cardtypeid"));
        commTransactionDetailsVO.setCustomerId((String) transDetails.get("customerId"));
        commTransactionDetailsVO.setVersion((String) transDetails.get("version"));
        commTransactionDetailsVO.setFromtype((String) transDetails.get("fromType"));
        commCardDetailsVO.setCardNum((String) transDetails.get("ccnum"));

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
        commMerchantVO.setAccountId((String) transDetails.get("accountid"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }

    private Hashtable getTransactionDetails(String trackingid, String status) throws PZDBViolationException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Hashtable transDetail = new Hashtable();
        Functions functions = new Functions();
        String amount = null;
        String firstname = null;
        String lastname = null;
        String name = null;
        String ccnum = null;
        String country = null;
        String city = null;
        String expdate = null;

        String ipaddress = null;
        String currency = null;
        String emailaddr = null;
        String address = null;
        String state = null;
        String responsecode = null;
        String zip = null;
        String telnocc = null;
        String telno = null;
        String accountId = null;
        String orderdescription = null;
        String description = null;
        String paymentId = null;
        String toId = null;
        String redirectUrl = null;
        String templateamount = null;
        String templatecurrency = null;
        String paymodeid = null;
        String cardtypeid = null;
        String customerId = null;
        String version = null;
        String fromType=null;
        try
        {
            connection = Database.getConnection();
            String qry = "SELECT t.accountid,t.street,t.state,t.zip,t.amount,d.status,t.firstname,t.lastname,t.name,t.ccnum,t.country,t.city,t.expdate,d.ipaddress,t.currency,t.emailaddr,d.responsecode,t.telnocc,t.telno,d.responsetransactionid,t.orderdescription,t.description,t.toid,t.paymentid,t.redirecturl,t.templateamount,t.templatecurrency,t.paymodeid,t.cardtypeid,t.customerId,t.version,t.fromtype  FROM transaction_common AS t,transaction_common_details AS d WHERE t.trackingid=d.trackingid AND t.trackingid=? AND d.status IN (?)";
            preparedStatement = connection.prepareStatement(qry);
            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, status);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                amount = resultSet.getString("amount");
                firstname = resultSet.getString("firstname");
                lastname = resultSet.getString("lastname");
                name = resultSet.getString("name");
                ccnum = PzEncryptor.decryptPAN(resultSet.getString("ccnum"));
                country = resultSet.getString("country");
                city = resultSet.getString("city");
                ipaddress = resultSet.getString("ipaddress");
                currency = resultSet.getString("currency");
                emailaddr = resultSet.getString("emailaddr");
                address = resultSet.getString("street");
                state = resultSet.getString("state");
                zip = resultSet.getString("zip");
                telnocc = resultSet.getString("telnocc");
                telno = resultSet.getString("telno");
                accountId = resultSet.getString("accountid");
                orderdescription = resultSet.getString("orderdescription");
                description = resultSet.getString("description");
                toId = resultSet.getString("toid");
                paymentId = resultSet.getString("paymentid");
                redirectUrl = resultSet.getString("redirecturl");
                templateamount = resultSet.getString("templateamount");
                templatecurrency = resultSet.getString("templatecurrency");
                paymodeid = resultSet.getString("paymodeid");
                cardtypeid = resultSet.getString("cardtypeid");
                customerId = resultSet.getString("customerId");
                version = resultSet.getString("version");
                fromType=resultSet.getString("fromtype");

                if (functions.isValueNull(fromType))
                {
                    transDetail.put("fromType", fromType);
                }
                if (functions.isValueNull(amount))
                {
                    transDetail.put("amount", amount);
                }
                if (functions.isValueNull(firstname))
                {
                    transDetail.put("firstname", firstname);
                }
                if (functions.isValueNull(lastname))
                {
                    transDetail.put("lastname", lastname);
                }
                if (functions.isValueNull(name))
                {
                    transDetail.put("name", name);
                }
                if (functions.isValueNull(ccnum))
                {
                    transDetail.put("ccnum", ccnum);
                }
                if (functions.isValueNull(country))
                {
                    transDetail.put("country", country);
                }
                if (functions.isValueNull(city))
                {
                    transDetail.put("city", city);
                }
                if (functions.isValueNull(expdate))
                {
                    transDetail.put("expdate", expdate);
                }
                if (functions.isValueNull(ipaddress))
                {
                    transDetail.put("ipaddress", ipaddress);
                }
                if (functions.isValueNull(currency))
                {
                    transDetail.put("currency", currency);
                }
                if (functions.isValueNull(emailaddr))
                {
                    transDetail.put("emailaddr", emailaddr);
                }
                if (functions.isValueNull(address))
                {
                    transDetail.put("address", address);
                }
                if (functions.isValueNull(state))
                {
                    transDetail.put("state", state);
                }
                if (functions.isValueNull(zip))
                {
                    transDetail.put("zip", zip);
                }
                if (functions.isValueNull(telnocc))
                {
                    transDetail.put("telnocc", telnocc);
                }
                if (functions.isValueNull(telno))
                {
                    transDetail.put("telno", telno);
                }
                if (functions.isValueNull(accountId))
                {
                    transDetail.put("accountid", accountId);
                }
                if (functions.isValueNull(orderdescription))
                {
                    transDetail.put("orderdescription", orderdescription);
                }
                if (functions.isValueNull(description))
                {
                    transDetail.put("description", description);
                }
                if (functions.isValueNull(toId))
                {
                    transDetail.put("toid", toId);
                }
                if (functions.isValueNull(paymentId))
                {
                    transDetail.put("paymentid", paymentId);
                }
                if (functions.isValueNull(redirectUrl))
                {
                    transDetail.put("redirecturl", redirectUrl);
                }
                if (functions.isValueNull(templateamount))
                {
                    transDetail.put("templateamount", templateamount);
                }
                if (functions.isValueNull(templatecurrency))
                {
                    transDetail.put("templatecurrency", templatecurrency);
                }
                if (functions.isValueNull(paymodeid))
                {
                    transDetail.put("paymodeid", paymodeid);
                }
                if (functions.isValueNull(cardtypeid))
                {
                    transDetail.put("cardtypeid", cardtypeid);
                }
                if(functions.isValueNull(customerId))
                {
                    transDetail.put("customerId",customerId);
                }
                if(functions.isValueNull(version))
                {
                    transDetail.put("version",version);
                }
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError ::::::::::::",systemError);
            PZExceptionHandler.raiseDBViolationException(NPayOnPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ::::::::::::", e);
            PZExceptionHandler.raiseDBViolationException(NPayOnPaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }

        return transDetail;
    }
    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}

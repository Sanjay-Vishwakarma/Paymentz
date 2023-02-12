package com.payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.SafeChargeRequestVO;
import com.directi.pg.core.valueObjects.SafeChargeResponseVO;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 9/5/13
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeChargePaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(SafeChargePaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SafeChargePaymentProcess.class.getName());

    private Hashtable getTransactionDetails(String trackingid, String status) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement1=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        Hashtable transDetail=new Hashtable();
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
        String authcode=null;
        String transid=null;
        String accountId="";
        String description="";
        String paymentId="";
        String toId="";
        String payModeId="";
        String cardTypeId="";
        String orderDescription="";
        String redirectUrl = "";
        String templateamount="";
        String templatecurrency="";
        String customerId="";
        try
        {
            connection= Database.getConnection();
            String qry1="select S.AuthCode from transaction_common_details as D , transaction_safecharge_details as S where D.trackingid=? and D.status=? and D.detailid=S.detailid";
            preparedStatement1=connection.prepareStatement(qry1);
            preparedStatement1.setString(1,trackingid);
            preparedStatement1.setString(2,status);
            resultSet1= preparedStatement1.executeQuery();
            if(resultSet1.next())
            {
                authcode=resultSet1.getString("AuthCode");

            }
            if(authcode!=null)
            {
                transDetail.put("authcode",authcode);
            }
            String qry="SELECT t.description,t.orderdescription,t.street,t.state,t.zip,t.amount,d.status,t.firstname,t.lastname,t.name,t.ccnum,t.country,t.city,t.expdate,d.ipaddress,t.currency,t.emailaddr,d.responsecode,t.telnocc,t.telno,d.responsetransactionid,t.accountid,t.paymentid,t.toid,t.paymodeid,t.cardtypeid,t.redirecturl,t.templateamount,t.templatecurrency,t.customerId  FROM transaction_common AS t,transaction_common_details AS d WHERE t.trackingid=d.trackingid AND t.trackingid=? AND d.status IN (?)";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,trackingid);
            preparedStatement.setString(2,status);

            transactionLogger.debug("getTransactionDetails---"+preparedStatement);
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
                transid=resultSet.getString("responsetransactionid");
                ipaddress=resultSet.getString("ipaddress");
                currency=resultSet.getString("currency");
                emailaddr=resultSet.getString("emailaddr");
                address=resultSet.getString("street");
                state=resultSet.getString("state");
                zip=resultSet.getString("zip");
                responsecode=resultSet.getString("responsecode");
                telnocc=resultSet.getString("telnocc");
                telno=resultSet.getString("telno");
                accountId=resultSet.getString("accountid");
                description=resultSet.getString("description");
                paymentId=resultSet.getString("paymentid");
                toId=resultSet.getString("toid");
                payModeId=resultSet.getString("paymodeid");
                cardTypeId=resultSet.getString("cardtypeid");
                orderDescription=resultSet.getString("orderdescription");
                redirectUrl = resultSet.getString("redirecturl");
                templateamount=resultSet.getString("templateamount");
                templatecurrency=resultSet.getString("templatecurrency");
                status = resultSet.getString("status");
                customerId = resultSet.getString("customerId");


                String temp[]=expdate.split("/");
                if(temp[0]!=null || !temp[0].equals(""))
                {
                    transDetail.put("expmonth",temp[0]);
                }
                if(temp[1]!=null || !temp[1].equals(""))
                {
                    transDetail.put("expyear",temp[1]);
                }
                if(amount!=null || !amount.equals(""))
                {
                    transDetail.put("amount",amount);
                }
                if(firstname!=null || !firstname.equals(""))
                {
                    transDetail.put("firstname",firstname);
                }
                if(lastname!=null || !lastname.equals(""))
                {
                    transDetail.put("lastname",lastname);
                }
                if(name!=null || !name.equals(""))
                {
                    transDetail.put("name",name);
                }
                if(ccnum!=null || !ccnum.equals(""))
                {
                    transDetail.put("ccnum",ccnum);
                }
                if(country!=null || !country.equals(""))
                {
                    transDetail.put("country",country);
                }
                if(city!=null || !city.equals(""))
                {
                    transDetail.put("city",city);
                }
                if(expdate!=null || !expdate.equals(""))
                {
                    transDetail.put("expdate",expdate);
                }

                if(ipaddress!=null && !ipaddress.equals(""))
                {
                    transDetail.put("ipaddress",ipaddress);
                }
                if(currency!=null || !currency.equals(""))
                {
                    transDetail.put("currency",currency);
                }
                if(emailaddr!=null || !emailaddr.equals(""))
                {
                    transDetail.put("emailaddr",emailaddr);
                }
                if(address!=null || !address.equals(""))
                {
                    transDetail.put("address",address);
                }
                if(state!=null || !state.equals(""))
                {
                    transDetail.put("state",state);
                }
                if(zip!=null || !zip.equals(""))
                {
                    transDetail.put("zip",zip);
                }
                if(responsecode!=null || !responsecode.equals(""))
                {
                    transDetail.put("responsecode",responsecode);
                }
                if(telnocc!=null || !telnocc.equals(""))
                {
                    transDetail.put("telnocc",telnocc);
                }
                if(telno!=null || !telno.equals(""))
                {
                    transDetail.put("telno",telno);
                }
                if(transid!=null && !transid.equals(""))
                {
                    transDetail.put("transid",transid);
                }
                if(accountId!=null && !accountId.equals(""))
                {
                    transDetail.put("accountid",accountId);
                }
                if(description!=null && !description.equals(""))
                {
                    transDetail.put("description",description);
                }
                if(paymentId!=null && !paymentId.equals(""))
                {
                    transDetail.put("paymentid",paymentId);
                }
                if(toId!=null && !toId.equals(""))
                {
                    transDetail.put("toid",toId);
                }
                if(payModeId!=null && !payModeId.equals(""))
                {
                    transDetail.put("paymodeid",payModeId);
                }
                if(cardTypeId!=null && !cardTypeId.equals(""))
                {
                    transDetail.put("cardtypeid",cardTypeId);
                }
                if(orderDescription!=null && !orderDescription.equals(""))
                {
                    transDetail.put("orderdescription",orderDescription);
                }
                if(redirectUrl!=null && !redirectUrl.equals(""))
                {
                    transDetail.put("redirectUrl",redirectUrl);
                }
                if(templateamount!=null && !templateamount.equals(""))
                {
                    transDetail.put("templateamount",templateamount);
                }
                if(templatecurrency!=null && !templatecurrency.equals(""))
                {
                    transDetail.put("templatecurrency",templatecurrency);
                }
                if(customerId!=null && !customerId.equals(""))
                {
                    transDetail.put("customerId",customerId);
                }
                transDetail.put("status",status);
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentProcess.class.getName(),"getTransactionDetails()",null,"common","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentProcess.class.getName(), "getTransactionDetails()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closePreparedStatement(preparedStatement1);
            Database.closeResultSet(resultSet);
            Database.closeResultSet(resultSet1);
            Database.closeConnection(connection);
        }

        return transDetail;
    }



    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        SafeChargeRequestVO safeChargeRequestVO=(SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        int trackingid=captureRequest.getTrackingId();
        Hashtable transdetail= getTransactionDetails(String.valueOf(trackingid),"authsuccessful");

        commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commAddressDetailsVO.setFirstname((String)transdetail.get("firstname"));
        commAddressDetailsVO.setLastname((String)transdetail.get("lastname"));
        safeChargeRequestVO.setAuthCode((String)transdetail.get("authcode"));
        commCardDetailsVO.setExpMonth((String)transdetail.get("expmonth"));
        commCardDetailsVO.setExpYear((String)transdetail.get("expyear"));
        commTransactionDetailsVO.setCurrency((String)transdetail.get("currency"));
        commCardDetailsVO.setCardNum((String)transdetail.get("ccnum"));
        commAddressDetailsVO.setCountry((String)transdetail.get("country"));
        commAddressDetailsVO.setCity((String)transdetail.get("city"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transdetail.get("transid"));
        commAddressDetailsVO.setEmail((String)transdetail.get("emailaddr"));
        commAddressDetailsVO.setIp((String)transdetail.get("ipaddress"));
        commAddressDetailsVO.setPhone((String)transdetail.get("telno"));
        commAddressDetailsVO.setZipCode((String)transdetail.get("zip"));
        commAddressDetailsVO.setState((String)transdetail.get("state"));
        commAddressDetailsVO.setStreet((String)transdetail.get("address"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }


    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        SafeChargeRequestVO safeChargeRequestVO=(SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        int trackingid=refundRequest.getTrackingId();
        System.out.println("trackingid in safecharge----"+refundRequest.getTrackingId());
        Hashtable transdetail= getTransactionDetails(String.valueOf(trackingid),"capturesuccess");

        //commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commTransactionDetailsVO.setAmount(refundRequest.getRefundAmount());
        commAddressDetailsVO.setFirstname((String)transdetail.get("firstname"));
        commAddressDetailsVO.setLastname((String)transdetail.get("lastname"));
        safeChargeRequestVO.setAuthCode((String)transdetail.get("authcode"));
        commCardDetailsVO.setExpMonth((String)transdetail.get("expmonth"));
        commCardDetailsVO.setExpYear((String)transdetail.get("expyear"));
        commTransactionDetailsVO.setCurrency((String)transdetail.get("currency"));
        commCardDetailsVO.setCardNum((String)transdetail.get("ccnum"));
        commAddressDetailsVO.setCountry((String)transdetail.get("country"));
        commAddressDetailsVO.setCity((String)transdetail.get("city"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transdetail.get("transid"));
        commAddressDetailsVO.setEmail((String)transdetail.get("emailaddr"));
        commAddressDetailsVO.setIp((String)transdetail.get("ipaddress"));
        commAddressDetailsVO.setPhone((String)transdetail.get("telno"));
        commAddressDetailsVO.setZipCode((String)transdetail.get("zip"));
        commAddressDetailsVO.setState((String)transdetail.get("state"));
        commAddressDetailsVO.setStreet((String)transdetail.get("address"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }



    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        super.setInquiryVOParamsExtension(requestVO, pzInquiryRequest);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        SafeChargeRequestVO safeChargeRequestVO=(SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        int trackingid=cancelRequest.getTrackingId();
        Hashtable transdetail= getTransactionDetails(String.valueOf(trackingid),"authsuccessful");

        commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commAddressDetailsVO.setFirstname((String)transdetail.get("firstname"));
        commAddressDetailsVO.setLastname((String)transdetail.get("lastname"));
        safeChargeRequestVO.setAuthCode((String)transdetail.get("authcode"));
        commCardDetailsVO.setExpMonth((String)transdetail.get("expmonth"));
        commCardDetailsVO.setExpYear((String)transdetail.get("expyear"));
        commTransactionDetailsVO.setCurrency((String)transdetail.get("currency"));
        commCardDetailsVO.setCardNum((String)transdetail.get("ccnum"));
        commAddressDetailsVO.setCountry((String)transdetail.get("country"));
        commAddressDetailsVO.setCity((String)transdetail.get("city"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transdetail.get("transid"));
        commAddressDetailsVO.setEmail((String)transdetail.get("emailaddr"));
        commAddressDetailsVO.setIp((String)transdetail.get("ipaddress"));
        commAddressDetailsVO.setPhone((String)transdetail.get("telno"));
        commAddressDetailsVO.setZipCode((String)transdetail.get("zip"));
        commAddressDetailsVO.setState((String)transdetail.get("state"));
        commAddressDetailsVO.setStreet((String)transdetail.get("address"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
    public void setSafeChargeRequestVO(CommRequestVO requestVO,String trackingId,String PARes,String cvv) throws PZDBViolationException{
        SafeChargeRequestVO safeChargeRequestVO=(SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        Hashtable transactionDetails= getTransactionDetails(trackingId,"authstarted");
        commCardDetailsVO.setCardHolderName(transactionDetails.get("firstname")+" "+transactionDetails.get("lastname"));
        commCardDetailsVO.setCardNum((String) transactionDetails.get("ccnum"));
        commCardDetailsVO.setExpMonth((String) transactionDetails.get("expmonth"));
        commCardDetailsVO.setExpYear((String) transactionDetails.get("expyear"));
        commCardDetailsVO.setcVV(cvv);

        commTransactionDetailsVO.setCurrency((String) transactionDetails.get("currency"));
        commTransactionDetailsVO.setAmount((String) transactionDetails.get("amount"));
        commTransactionDetailsVO.setOrderId((String)transactionDetails.get("description"));
        commTransactionDetailsVO.setOrderDesc((String)transactionDetails.get("orderdescription"));
        commTransactionDetailsVO.setPreviousTransactionId((String) transactionDetails.get("paymentid"));
        commTransactionDetailsVO.setToId((String) transactionDetails.get("toid"));
        commTransactionDetailsVO.setPaymentType((String) transactionDetails.get("paymodeid"));
        commTransactionDetailsVO.setCardType((String) transactionDetails.get("cardtypeid"));
        commTransactionDetailsVO.setPrevTransactionStatus((String) transactionDetails.get("status"));
        commTransactionDetailsVO.setRedirectUrl((String) transactionDetails.get("redirectUrl"));
        commTransactionDetailsVO.setCustomerId((String) transactionDetails.get("customerId"));

        commAddressDetailsVO.setFirstname((String) transactionDetails.get("firstname"));
        commAddressDetailsVO.setLastname((String) transactionDetails.get("lastname"));
        commAddressDetailsVO.setCountry((String) transactionDetails.get("country"));
        commAddressDetailsVO.setCity((String) transactionDetails.get("city"));
        commAddressDetailsVO.setEmail((String) transactionDetails.get("emailaddr"));
        commAddressDetailsVO.setIp((String) transactionDetails.get("ipaddress"));
        commAddressDetailsVO.setPhone((String) transactionDetails.get("telno"));
        commAddressDetailsVO.setZipCode((String) transactionDetails.get("zip"));
        commAddressDetailsVO.setState((String) transactionDetails.get("state"));
        commAddressDetailsVO.setStreet((String) transactionDetails.get("address"));
        commAddressDetailsVO.setTmpl_amount((String) transactionDetails.get("templateamount"));
        commAddressDetailsVO.setTmpl_currency((String) transactionDetails.get("templatecurrency"));

        commMerchantVO.setAccountId((String)transactionDetails.get("accountid"));

        safeChargeRequestVO.setPARes(PARes);
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

        transactionLogger.debug("dbStatus-----"+commTransactionDetailsVO.getPrevTransactionStatus());
    }

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        log.debug("enter in safecharge actionentry extension");
        transactionLogger.debug("enter in safecharge actionentry extension");
        int i=0;
        Connection conn= null;
        SafeChargeResponseVO safeChargeResponseVO= (SafeChargeResponseVO) responseVO;
        String AuthCode="";
        String AVSCode="";
        String ExErrCode="";
        //String token="";

        if(responseVO!=null)
        {
            AuthCode=safeChargeResponseVO.getAuthCode();
            AVSCode=safeChargeResponseVO.getAVSCode();
            ExErrCode=safeChargeResponseVO.getExErrCode();
        }   //token=safeChargeResponseVO.getToken();
        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_safecharge_details(detailid,AuthCode,AVSCode,ExErrCode) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,AuthCode+"");
            pstmt.setString(3,AVSCode+"");
            pstmt.setString(4,ExErrCode+"");
            //pstmt.setString(5,token+"");

            i= pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafeChargePaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        SafeChargeRequestVO safeChargeRequestVO=(SafeChargeRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=safeChargeRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=safeChargeRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=safeChargeRequestVO.getCardDetailsVO();
        int trackingid=payoutRequest.getTrackingId();
        Hashtable transdetail= getTransactionDetails(String.valueOf(trackingid),"capturesuccess");

        //commTransactionDetailsVO.setAmount((String)transdetail.get("amount"));
        commTransactionDetailsVO.setAmount(payoutRequest.getPayoutAmount());
        commAddressDetailsVO.setFirstname((String)transdetail.get("firstname"));
        commAddressDetailsVO.setLastname((String)transdetail.get("lastname"));
        safeChargeRequestVO.setAuthCode((String)transdetail.get("authcode"));
        commCardDetailsVO.setExpMonth((String)transdetail.get("expmonth"));
        commCardDetailsVO.setExpYear((String)transdetail.get("expyear"));
        commTransactionDetailsVO.setCurrency((String)transdetail.get("currency"));
        commCardDetailsVO.setCardNum((String)transdetail.get("ccnum"));
        commAddressDetailsVO.setCountry((String)transdetail.get("country"));
        commAddressDetailsVO.setCity((String)transdetail.get("city"));
        commTransactionDetailsVO.setPreviousTransactionId((String)transdetail.get("transid"));
        commAddressDetailsVO.setEmail((String)transdetail.get("emailaddr"));
        commAddressDetailsVO.setIp((String)transdetail.get("ipaddress"));
        commAddressDetailsVO.setPhone((String)transdetail.get("telno"));
        commAddressDetailsVO.setZipCode((String)transdetail.get("zip"));
        commAddressDetailsVO.setState((String)transdetail.get("state"));
        commAddressDetailsVO.setStreet((String)transdetail.get("address"));
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside safecharge payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

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
}



                

                
                
                
                
                
                
                
                
                
                
                
                
                
                




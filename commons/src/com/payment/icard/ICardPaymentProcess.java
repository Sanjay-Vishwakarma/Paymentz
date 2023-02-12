package com.payment.icard;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/4/2019.
 */
public class ICardPaymentProcess extends CommonPaymentProcess
{
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.icard");
    private static ICardLogger transactionLogger = new ICardLogger(ICardPaymentProcess.class.getName());

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">");
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
                form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        return form.toString();
    }

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">");
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
        form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        return form.toString();
    }

    public void setICardRequestVO(CommRequestVO requestVO,String trackingId, String cvv) throws PZDBViolationException
    {
        transactionLogger.error("Inside ICard Payment Process Set RequestVO-----");
        CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO                       = new CommMerchantVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionVO                  = transactionManager.getTransDetailFromCommon(trackingId);

        commCardDetailsVO.setCardHolderName(transactionVO.getFirstName()+" "+transactionVO.getLastName());
        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commCardDetailsVO.setCardType(transactionVO.getCardtype());
        String expdate = transactionVO.getExpdate();

        if (expdate != null)
        {
            String eDate        = PzEncryptor.decryptExpiryDate(expdate);
            String expMonth     = eDate.split("\\/")[0];
            String expYear      = eDate.split("\\/")[1];
            commCardDetailsVO.setExpMonth(expMonth);
            commCardDetailsVO.setExpYear(expYear);
        }
        commCardDetailsVO.setcVV(cvv);
        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commTransactionDetailsVO.setNotificationUrl(transactionVO.getNotificationUrl());
        commTransactionDetailsVO.setRedirectMethod(transactionVO.getRedirectMethod());

        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setCity(transactionVO.getCity());
        commAddressDetailsVO.setEmail(transactionVO.getEmailaddr());
        commAddressDetailsVO.setIp(transactionVO.getIpAddress());
        commAddressDetailsVO.setPhone(transactionVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionVO.getZip());
        commAddressDetailsVO.setState(transactionVO.getState());
        commAddressDetailsVO.setStreet(transactionVO.getStreet());
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());
        commAddressDetailsVO.setCardHolderIpAddress(transactionVO.getIpAddress());
        commAddressDetailsVO.setCustomerid(transactionVO.getCustomerId());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

        transactionLogger.error("dbStatus:::::::"+commTransactionDetailsVO.getPrevTransactionStatus());
    }
    public static HashMap getPreviousTransactionDetails(String trackingid,String status)
    {
        String approval = "";
        Connection conn = null;
        PreparedStatement ps = null;
        HashMap detailHash = new HashMap();
        try
        {
            conn = Database.getConnection();
            String query = "select responsehashinfo,responsetransactionid from transaction_common_details where trackingid='" + trackingid + "' AND status IN("+status+") AND responsehashinfo IS NOT NULL AND responsehashinfo!='' LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                detailHash.put("responsehashinfo",rs.getString("responsehashinfo"));
                detailHash.put("responsetransactionid",rs.getString("responsetransactionid"));
                transactionLogger.error("responsehashinfo or approval------" + detailHash);
            }
            transactionLogger.error("Sql Query-----" + ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----" + e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException-----" + se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return detailHash;
    }


    public static String getPreviousStan(String trackingid,String type)
    {
        String approval = "";
        Connection conn = null;
        PreparedStatement ps = null;
        String originalStan = "";
        try
        {
            conn = Database.getConnection();
            String query = "select stan from transaction_icard_details where trackingid='" + trackingid + "' AND type='"+type+"'";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                originalStan = rs.getString("stan");
                transactionLogger.error("originalStan for cancel------" + originalStan);
            }
            transactionLogger.error("Sql Query-----" + ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----" + e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException-----" + se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return originalStan;
    }


    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside ICardPaymentProcess setRefundVOParamsextension -----");
        int trackingid = refundRequest.getTrackingId();
        HashMap detailHash = getPreviousTransactionDetails(String.valueOf(trackingid),"'authsuccessful','capturesuccess'");
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        if(functions.isValueNull(detailHash.get("responsehashinfo").toString()))
            transactionDetailsVO.setResponseHashInfo((String)detailHash.get("responsehashinfo"));
        if(functions.isValueNull(detailHash.get("responsetransactionid").toString()))
            transactionDetailsVO.setPreviousTransactionId((String)detailHash.get("responsetransactionid"));
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }

    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        transactionLogger.error("-----inside setCancelVOParamsExtension-----");
        Functions functions= new Functions();
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();

        String trackingid = transactionDetailsVO.getOrderId();
        String originalStan = getPreviousStan(String.valueOf(trackingid),"AUTH");

        if(functions.isValueNull(originalStan))
            transactionDetailsVO.setResponseHashInfo(originalStan);

        requestVO.setTransDetailsVO(transactionDetailsVO);

    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

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
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(transactionDetailsVO.getAccountId());
        enrollmentRequestVO.setMid(gatewayAccount.getCHARGEBACK_FTP_PATH());
        if(functions.isValueNull(enrollmentRequestVO.getHostUrl()))
            enrollmentRequestVO.setTermUrl("https://"+enrollmentRequestVO.getHostUrl()+RB.getString("HOST_URL"));
        else
        enrollmentRequestVO.setTermUrl(RB.getString("TERM_URL"));
    }

}
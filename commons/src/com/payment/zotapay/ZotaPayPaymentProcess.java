package com.payment.zotapay;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 7/11/2018.
 */
public class ZotaPayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ZotaPayPaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        StringBuffer sb= new StringBuffer();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        Document html = Jsoup.parse(response3D.getUrlFor3DRedirect());
        String url=html.getElementsByTag("form").attr("action");
        List<AsyncParameterVO> asyncParameterVOs=getParams(response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        sb.append("<form name=\"launch3D\" method=\"POST\" action=\""+url+ "\"" + target + ">");

        if(asyncParameterVOs.size()>0){
            for(AsyncParameterVO asyncParameterVO:asyncParameterVOs){
                sb.append("<input type=\"hidden\" name=\""+asyncParameterVO.getName()+"\" value=\""+asyncParameterVO.getValue()+"\">");
            }
        }
        sb.append( "</form>");
        sb.append("<script language=\"javascript\"> document.launch3D.submit(); </script>");
        transactionLogger.debug("Form---------"+sb.toString());
        return sb.toString();
    }

    public String getSpecificVirtualTerminalJSP()
        {
            return "payforasiaspecificfields.jsp";
        }


    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = response3D.getUrlFor3DRedirect();
        return form;
    }

    public void setZotapayRequestVO(CommRequestVO requestVO, String trackingId) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionVO = transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        transactionLogger.debug("dbStatus----" + commTransactionDetailsVO.getPrevTransactionStatus());
        transactionLogger.debug("paymentid----" + commTransactionDetailsVO.getPaymentId());

        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPaymentId(transactionVO.getPaymentId());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());
        commAddressDetailsVO.setCustomerid(transactionVO.getCustomerId());

        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.debug("----inside 3DResponseVO-----");
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside nest payment process---" + response3D.getUrlFor3DRedirect());

        Document html = Jsoup.parse(response3D.getUrlFor3DRedirect());
        transactionLogger.debug("Url------"+html.getElementsByTag("form").attr("action"));
        directKitResponseVO.setBankRedirectionUrl(html.getElementsByTag("form").attr("action"));

        for (Element input : html.getElementsByTag("input"))
        {
            int i=0;
            transactionLogger.debug("Parameters-----"+input.attr("name")+"--"+ input.attr("value"));
            asyncParameterVO = new AsyncParameterVO();
            if(!input.attr("name").equalsIgnoreCase("submit")){
                asyncParameterVO.setName(input.attr("name"));
                asyncParameterVO.setValue(input.attr("value"));
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            i++;
        }
    }

    public List<AsyncParameterVO> getParams(String decodedHtml){
        Document html = Jsoup.parse(decodedHtml);
        AsyncParameterVO asyncParameterVO=null;
        List<AsyncParameterVO> asyncParameterVOs=new ArrayList<>();
        for (Element input : html.getElementsByTag("input"))
        {
            int i=0;
            asyncParameterVO = new AsyncParameterVO();
            transactionLogger.debug("Parameters-----"+input.attr("name")+"--"+ input.attr("value"));
            if(!input.attr("name").equalsIgnoreCase("submit")) {
                asyncParameterVO.setName(input.attr("name"));
                asyncParameterVO.setValue(input.attr("value"));
                asyncParameterVOs.add(asyncParameterVO);
            }
            i++;
        }
        return asyncParameterVOs;
    }

    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside ZotapayPaymentProcess------");
        int trackingid = pzInquiryRequest.getTrackingId();
        String sn = getPreviousTransactionDetails(String.valueOf(trackingid));
        if(!functions.isValueNull(sn)){
            sn=getPreviousTransactionDetails(String.valueOf(trackingid),"3D_authstarted");
        }
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        transactionDetailsVO.setResponseHashInfo(sn);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }

    public static String getPreviousTransactionDetails(String trackingid)
    {
        String sn = "";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String query = "select responsehashinfo from transaction_common_details where trackingid='" + trackingid + "' AND status in ('authsuccessful','capturesuccess','reversed','cancelled') ORDER BY detailid DESC LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                sn = rs.getString("responsehashinfo");
                transactionLogger.error("transactionId------" + sn);
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
        return sn;
    }

        public static String getPreviousTransactionDetails(String trackingid,String status)
    {
        String sn = "";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String query = "select responsehashinfo from transaction_common_details where trackingid='" + trackingid + "' AND status='"+status+"' ORDER BY detailid DESC LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                sn = rs.getString("responsehashinfo");
                transactionLogger.error("transactionId------" + sn);
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
        return sn;
    }


    public static String getPreviousStatus(String trackingid)
    {
        String status = "";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String query = "select status from transaction_common_details where trackingid='"+trackingid+"' ORDER BY detailid DESC LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                status = rs.getString("status");
                transactionLogger.error("status------" + status);
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
        return status;
    }

    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside ZotapayPaymentProcess------");
        int trackingid = captureRequest.getTrackingId();
        String sn = getPreviousTransactionDetails(String.valueOf(trackingid),"authsuccessful");
        if(!functions.isValueNull(sn)){
            sn=getPreviousTransactionDetails(String.valueOf(trackingid),"3D_authstarted");
        }

        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        transactionDetailsVO.setResponseHashInfo(sn);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside ZotapayPaymentProcess------");
        int trackingid = refundRequest.getTrackingId();
        String sn = getPreviousTransactionDetails(String.valueOf(trackingid),"capturesuccess");
        if(!functions.isValueNull(sn)){
            sn=getPreviousTransactionDetails(String.valueOf(trackingid),"3D_authstarted");
        }
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        transactionDetailsVO.setResponseHashInfo(sn);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside ZotapayPaymentProcess------");
        int trackingid = cancelRequest.getTrackingId();
        String sn = getPreviousTransactionDetails(String.valueOf(trackingid),"authsuccessful");
        if(!functions.isValueNull(sn)){
            sn=getPreviousTransactionDetails(String.valueOf(trackingid),"3D_authstarted");
        }
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        transactionDetailsVO.setResponseHashInfo(sn);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }
}
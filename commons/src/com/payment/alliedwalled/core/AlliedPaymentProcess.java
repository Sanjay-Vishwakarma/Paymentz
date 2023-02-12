package com.payment.alliedwalled.core;

import com.directi.pg.Database;
import com.directi.pg.PzEncryptor;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZInquiryRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 4/23/18.
 */
public class AlliedPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger= new TransactionLogger(AlliedPaymentProcess.class.getName());

    public static String getPreviousTransactionDetails(String trackingid){
        String transactionId="";
        Connection conn=null;
        PreparedStatement ps = null;
        try{
            conn= Database.getConnection();
            String query="select responsetransactionid from transaction_common_details where trackingid='"+trackingid+"' AND status in ('authsuccessful','capturesuccess','reversed') ORDER BY detailid DESC LIMIT 1";
            ps= conn.prepareStatement(query);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                transactionId=rs.getString("responsetransactionid");
                transactionLogger.error("transactionId------"+transactionId);
            }
            transactionLogger.error("Sql Query-----"+ps);
        }catch(SystemError e){
            transactionLogger.debug("SystemError-----"+e);
        }catch (SQLException se){
            transactionLogger.debug("SQLException-----"+se);
        }
        finally {
            Database.closeConnection(conn);
        }

        return transactionId;
    }

    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        transactionLogger.debug("Inside AlliedPaymentProcesss------");
       int trackingid=pzInquiryRequest.getTrackingId();
        String transactionId=getPreviousTransactionDetails(String.valueOf(trackingid));

        CommTransactionDetailsVO transactionDetailsVO= new CommTransactionDetailsVO();
        transactionDetailsVO.setPreviousTransactionId(transactionId);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
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
        return form;
    }

    public void setAWRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
    {
        CommRequestVO commRequestVO=(CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        transactionLogger.debug("dbStatus----"+commTransactionDetailsVO.getPrevTransactionStatus());
        transactionLogger.debug("paymentid----"+commTransactionDetailsVO.getPaymentId());

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
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside Allied Wallet Rest 3d payment process---"+response3D.getUrlFor3DRedirect());
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

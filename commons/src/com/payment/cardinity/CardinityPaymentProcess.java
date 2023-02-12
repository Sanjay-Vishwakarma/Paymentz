package com.payment.cardinity;

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
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 4/18/18.
 */
public class CardinityPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(CardinityPaymentProcess.class.getName());
    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed for VT....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">"+
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

    public void setCardinityRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
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
        commTransactionDetailsVO.setVersion(transactionVO.getVersion());
        commTransactionDetailsVO.setCustomerId(transactionVO.getCustomerId());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());

        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {

        int trackingid=refundRequest.getTrackingId();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(String.valueOf(trackingid));
        transactionLogger.debug("paymentid-----"+transactionVO.getPaymentId());
        transactionLogger.debug("amount-----"+refundRequest.getRefundAmount());
        commTransactionDetailsVO.setPreviousTransactionId(transactionVO.getPaymentId());
        commTransactionDetailsVO.setAmount(refundRequest.getRefundAmount());
        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }

    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        Connection conn= null;
        PreparedStatement ps=null;
        String trackingid= String.valueOf(pzInquiryRequest.getTrackingId());
        try{
            conn= Database.getConnection();
            String sqlQuery="select status,responsetransactionid from transaction_common_details where trackingid="+trackingid+" order by detailid desc limit 1";
            ps=conn.prepareStatement(sqlQuery);
            ResultSet rs =ps.executeQuery();
            while (rs.next()){
               requestVO.getTransDetailsVO().setPaymentId(rs.getString("responsetransactionid"));
            }
            System.out.println("SqlQuery-----"+ps);
            System.out.println("responsetransactionid-----"+requestVO.getTransDetailsVO().getPreviousTransactionId());

        }catch (SystemError se){
            transactionLogger.error("SystemError-----",se);
        }catch (SQLException e){
            transactionLogger.error("SQLException-----",e);
        }finally
        {
            Database.closeConnection(conn);
        }
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside cardinity Rest 3d payment process---"+response3D.getUrlFor3DRedirect());
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

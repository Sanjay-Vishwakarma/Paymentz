package com.payment.sabadell;

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
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 7/16/18.
 */
public class SabadellPaymentProcess extends CommonPaymentProcess
{
    private TransactionLogger transactionLogger= new TransactionLogger(SabadellPaymentGateway.class.getName());

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getRequestMap().get("Url")+"\""+target+">"+
                "<input type=\"hidden\" name=\"Ds_SignatureVersion\" value=\""+response3D.getRequestMap().get("Ds_SignatureVersion")+"\">"+
                "<input type=\"hidden\" name=\"Ds_MerchantParameters\" value=\""+response3D.getRequestMap().get("Ds_MerchantParameters")+"\">"+
                "<input type=\"hidden\" name=\"Ds_Signature\" value=\""+response3D.getRequestMap().get("Ds_Signature")+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getRequestMap().get("Url"));

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getRequestMap().get("Url")+"\">"+
                "<input type=\"hidden\" name=\"Ds_SignatureVersion\" value=\""+response3D.getRequestMap().get("Ds_SignatureVersion")+"\">"+
                "<input type=\"hidden\" name=\"Ds_MerchantParameters\" value=\""+response3D.getRequestMap().get("Ds_MerchantParameters")+"\">"+
                "<input type=\"hidden\" name=\"Ds_Signature\" value=\""+response3D.getRequestMap().get("Ds_Signature")+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside Sabadell  Rest 3d payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getRequestMap().get("Url"));

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("Ds_SignatureVersion");
        asyncParameterVO.setValue(response3D.getRequestMap().get("Ds_SignatureVersion"));
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("Ds_MerchantParameters");
        asyncParameterVO.setValue(response3D.getRequestMap().get("Ds_MerchantParameters"));
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("Ds_Signature");
        asyncParameterVO.setValue(response3D.getRequestMap().get("Ds_Signature"));
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

    }

    public void setSabadellRequestVO(CommRequestVO requestVO, String trackingId) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionVO = transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

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

    public String getTransactionType(String trackingid,String status){
        Connection conn=null;
        PreparedStatement ps=null;
        String transType="";
        try{
            String query="Select transtype from transaction_common_details where trackingid="+trackingid+" and status='"+status+"'";
            conn=Database.getConnection();
            ps=conn.prepareStatement(query);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                transType=rs.getString("transtype");
            }

            transactionLogger.debug("SqlQuery-----"+ps);
        }catch (SystemError se){
            transactionLogger.error("SystemError-----",se);
        }catch (SQLException e){
            transactionLogger.error("SQLException-----",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        return transType;
    }

    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        int trackingid=captureRequest.getTrackingId();
        String transType=getTransactionType(String.valueOf(trackingid),"authsuccessful");
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        commTransactionDetailsVO.setTransactionType(transType);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        int trackingid=refundRequest.getTrackingId();
        String transType=getTransactionType(String.valueOf(trackingid),"capturesuccess");
        if(transType.equalsIgnoreCase("capture"))
        {
            transType = getTransactionType(String.valueOf(trackingid), "authsuccessful");
        }
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        commTransactionDetailsVO.setTransactionType(transType);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        int trackingid=cancelRequest.getTrackingId();
        String transType=getTransactionType(String.valueOf(trackingid),"authsuccessful");
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        commTransactionDetailsVO.setTransactionType(transType);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
}

package com.payment.payspace;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 7/15/17.
 */
public class PaySpacePaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySpacePaymentProcess.class.getName());
    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        int trackingid=captureRequest.getTrackingId();
        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        transactionManager.getCommonTransactionDetailsNew(transactionDetailsVO, String.valueOf(trackingid), null);

        if(functions.isValueNull(transactionDetailsVO.getCcnum())){
            if(requestVO.getCardDetailsVO()!=null){
                requestVO.getCardDetailsVO().setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            }
            else{
                CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                requestVO.setCardDetailsVO(commCardDetailsVO);
            }

        }

        if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
            requestVO.getAddressDetailsVO().setEmail(transactionDetailsVO.getEmailaddr());
        }
    }
    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        int trackingid=refundRequest.getTrackingId();
        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        transactionManager.getCommonTransactionDetailsNew(transactionDetailsVO, String.valueOf(trackingid), null);

        if(functions.isValueNull(transactionDetailsVO.getCcnum())){
            if(requestVO.getCardDetailsVO()!=null){
                requestVO.getCardDetailsVO().setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            }
            else{
                CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                requestVO.setCardDetailsVO(commCardDetailsVO);
            }

        }
        if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
            requestVO.getAddressDetailsVO().setEmail(transactionDetailsVO.getEmailaddr());
        }
    }
    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        int trackingid=cancelRequest.getTrackingId();
        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        transactionManager.getCommonTransactionDetailsNew(transactionDetailsVO, String.valueOf(trackingid), null);

        if(functions.isValueNull(transactionDetailsVO.getCcnum())){
            if(requestVO.getCardDetailsVO()!=null){
                requestVO.getCardDetailsVO().setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            }
            else{
                CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                requestVO.setCardDetailsVO(commCardDetailsVO);
            }

        }

        if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
            requestVO.getAddressDetailsVO().setEmail(transactionDetailsVO.getEmailaddr());
        }
    }
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest inquiryRequest) throws PZDBViolationException
    {
        int trackingid=inquiryRequest.getTrackingId();
        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        transactionManager.getCommonTransactionDetailsNew(transactionDetailsVO, String.valueOf(trackingid), null);

        if(functions.isValueNull(transactionDetailsVO.getCcnum())){
            if(requestVO.getCardDetailsVO()!=null){
                requestVO.getCardDetailsVO().setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            }
            else{
                CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                requestVO.setCardDetailsVO(commCardDetailsVO);
            }

        }

        if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
            requestVO.getAddressDetailsVO().setEmail(transactionDetailsVO.getEmailaddr());
        }
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"redirect_method\" value=\""+response3D.getRedirectMethod()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";

        transactionLogger.error("-----payspace3d form-----");
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"redirect_method\" value=\""+response3D.getRedirectMethod()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";

        transactionLogger.error("-----payspace3d form-----");
        return form;
    }
    public String getSpecificVirtualTerminalJSP()
    {
        return "clearsettlespecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
    public String updateBankTransactionId(String bankTransactionId,String trackingId)throws SQLException,SystemError
    {
        String result="";
        Connection conn=null;
        PreparedStatement preparedStatement=null;
        try
        {
            conn= Database.getConnection();
            String query="update transaction_common SET paymentid=? WHERE trackingId=?";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,bankTransactionId);
            preparedStatement.setString(2,trackingId);
            int k=preparedStatement.executeUpdate();
            if(k>0){
                result="success";
            }else{
                result="fail" ;
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;

    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;

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
        asyncParameterVO.setName("redirect_method");
        asyncParameterVO.setValue(response3D.getRedirectMethod());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
}

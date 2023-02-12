package com.payment.emexpay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 12/15/2017.
 */
public class EmexpayPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger=new TransactionLogger(EmexpayPaymentProcess.class.getName());
    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {

        transactionLogger.debug("enter in Emexpay actionentery extension");
        int i=0;
        Connection conn= null;

        EmexpayVO emexpayVO = null;

        String holder_name = "";
        String token = "";
        String stamp = "";
        String brand="";
        String bank_code="";
        String rrn="";
        String ref_id="";
        String gateway_id="";
        String message="";
        String address_details = "";



        if(responseVO != null  && !responseVO.equals(""))
        {
             emexpayVO = (EmexpayVO) responseVO;

            holder_name = emexpayVO.getHolder_name();
            token = emexpayVO.getToken();
            stamp = emexpayVO.getStamp();
            brand=emexpayVO.getBrand();
            bank_code=emexpayVO.getBank_code();
            rrn=emexpayVO.getRrn();
            ref_id=emexpayVO.getRef_id();
            gateway_id=emexpayVO.getGateway_id();
            message=emexpayVO.getMessage();
            address_details = emexpayVO.getAddress_details();

        }
        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_emexpay_details(detailid,trackingid,holder_name,token,stamp,brand,bank_code,rrn,ref_id,gateway_id,message,address_details) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,trackingId+"");
            pstmt.setString(3,holder_name);
            pstmt.setString(4,token);
            pstmt.setString(5,stamp);
            pstmt.setString(6,brand);
            pstmt.setString(7,bank_code);
            pstmt.setString(8,rrn);
            pstmt.setString(9,ref_id);
            pstmt.setString(10,gateway_id);
            pstmt.setString(11,message);
            pstmt.setString(12,address_details);

            i= pstmt.executeUpdate();
            transactionLogger.debug("insert emexpay---"+sql);
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(EmexpayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(EmexpayPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }

    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId)throws PZDBViolationException
    {
        transactionLogger.error("-----inside getCustomerAccountDetails of emexpay----- ");
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            conn = Database.getConnection();
            String query = "select token from transaction_emexpay_details where trackingid=? order by token desc limit 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,previousTransTrackingId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setAccountNumber(rs.getString("token"));
            }
            transactionLogger.debug("trackingid query----"+stmt);
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeConnection(conn);
        }
        return commCardDetailsVO;
    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("-----inside getExtentionDetails of emexpay------");
        Connection connection = null;
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT token FROM transaction_emexpay_details WHERE trackingid=? order by token desc limit 1";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {

                if(functions.isValueNull(rs.getString("token")))
                    commonValidatorVO.setCustomerBankId(rs.getString("token"));
            }
            transactionLogger.debug("detail table for emexpay---"+p);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("System error",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return commonValidatorVO;
    }

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        //System.out.println("inside Emexpay Payout Extention---");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        int memberId=payoutRequest.getMemberId();

        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(String.valueOf(memberId));

        commTransactionDetailsVO.setCustomerBankId(payoutRequest.getCustomerBankId());
        commTransactionDetailsVO.setCustomerId(payoutRequest.getCustomerId());
        commAddressDetailsVO.setEmail(payoutRequest.getCustomerEmail());
        if(merchantDetailsVO!=null){
            commAddressDetailsVO.setBirthdate(merchantDetailsVO.getExpDateOffset());
            commAddressDetailsVO.setCountry(merchantDetailsVO.getCountry());
        }
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
    public void setEmexpayRequestVO(CommRequestVO requestVO,String trackingId,String PARes,String id,String pa_res_url) throws PZDBViolationException
    {
        EmexpayRequestVO emexpayRequestVO=(EmexpayRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commCardDetailsVO.setCardNum(transactionVO.getCcnum());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commTransactionDetailsVO.setTemplateAmount(transactionVO.getTemplateamount());
        commTransactionDetailsVO.setTemplateCurrency(transactionVO.getTemplatecurrency());

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
        commAddressDetailsVO.setCustomerid(transactionVO.getCustomerId());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        emexpayRequestVO.setPaRes(PARes);
        emexpayRequestVO.setMD(id);
        emexpayRequestVO.setPa_res_url(pa_res_url);
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }


    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">" +
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">" +
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        //"<script language=\"javascript\">document.launch3D.target=\"_blank\"</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        Functions functions = new Functions();
        transactionLogger.debug("inside wirecard payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        if (functions.isValueNull(response3D.getPaReq()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("PaReq");
            asyncParameterVO.setValue(response3D.getPaReq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }

        if (functions.isValueNull(response3D.getTerURL()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("TermUrl");
            asyncParameterVO.setValue(response3D.getTerURL());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }

        if (functions.isValueNull(response3D.getMd()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("MD");
            asyncParameterVO.setValue(response3D.getMd());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
    }

}

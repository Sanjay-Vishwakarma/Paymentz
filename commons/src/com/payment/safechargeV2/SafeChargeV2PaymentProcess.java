package com.payment.safechargeV2;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 9/10/2020.
 */
public class SafeChargeV2PaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SafeChargeV2PaymentProcess.class.getName());
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        StringBuffer form = new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">");
        if(functions.isValueNull(response3D.getPaReq()))
            form.append("<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">");
        if(functions.isValueNull(response3D.getTerURL()))
            form.append("<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">");
        if(functions.isValueNull(response3D.getMd()))
            form.append("<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">");
        if(functions.isValueNull(response3D.getThreeDSSessionData()))
            form.append("<input type=\"hidden\" name=\"threeDSSessionData\" value=\""+response3D.getThreeDSSessionData()+"\">");
        if(functions.isValueNull(response3D.getCreq()))
            form.append("<input type=\"hidden\" name=\"cReq\" value=\""+response3D.getCreq()+"\">");
        form.append("</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>");
        transactionLogger.error("form....." + form);
        return form.toString();
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        StringBuffer form = new StringBuffer("<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">");
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
        form.append("</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>");
        return form.toString();
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---" + response3D.getUrlFor3DRedirect());

        if (functions.isValueNull(response3D.getCreq()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("creq");
            asyncParameterVO.setValue(response3D.getCreq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getThreeDSSessionData())){
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

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String responsehashinfo="";
        CommAddressDetailsVO addressDetailsVO=requestVO.getAddressDetailsVO();
        addressDetailsVO.setIp(payoutRequest.getIpAddress());
        requestVO.setAddressDetailsVO(addressDetailsVO);
        try
        {
            con=Database.getConnection();
            String query="select responsehashinfo from transaction_common_details where trackingid=? and status in ('capturesuccess','authsuccessful')";
            ps=con.prepareStatement(query);
            ps.setString(1, String.valueOf(payoutRequest.getTrackingId()));
            rs=ps.executeQuery();
            if (rs.next())
            {
                responsehashinfo=rs.getString("responsehashinfo");
            }
            requestVO.setUniqueId(responsehashinfo);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError----->",systemError);
            PZExceptionHandler.raiseDBViolationException(SafeChargeV2PaymentProcess.class.getName(), "setPayoutVOParamsextension", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException----->", e);
            PZExceptionHandler.raiseDBViolationException(SafeChargeV2PaymentProcess.class.getName(), "setPayoutVOParamsextension", null, "Common", "SQLException while connecting to transaction common  table", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}

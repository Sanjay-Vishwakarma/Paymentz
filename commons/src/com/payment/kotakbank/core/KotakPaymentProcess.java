package com.payment.kotakbank.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by ThinkPadT410 on 3/20/2017.
 */
public class KotakPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(KotakPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(KotakPaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId,String ctoken, Comm3DResponseVO comm3DResponseVO)
    {
        String target = "target=_blank";
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(comm3DResponseVO.getUrlFor3DRedirect()).append("\" method=\"post\""+target+">\n");

        for (String key : comm3DResponseVO.getRequestMap().keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + comm3DResponseVO.getRequestMap().get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

    public String get3DConfirmationForm(String trackingId,String ctoken, Comm3DResponseVO comm3DResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(comm3DResponseVO.getUrlFor3DRedirect()).append("\" method=\"post\">\n");

        for (String key : comm3DResponseVO.getRequestMap().keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + comm3DResponseVO.getRequestMap().get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for KotakPaymentProcess Details");
        transactionLogger.debug("Entering ActionEntry for KotakPaymentProcess Details");

        KotakResponseVO kotakResponseVO = new KotakResponseVO();
        String batchNumber = "";
        String cavv = "";
        String eci = "";
        String authStatus = "";
        String enrolled = "";
        String authCode = "";
        String retRefNumber = "";
        String responseCode = "";
        String remark = "";

        if (responseVO != null && !responseVO.equals(""))
        {
            batchNumber = kotakResponseVO.getBatchNumber();
            cavv = kotakResponseVO.getCavv();
            eci = kotakResponseVO.getEci();
            authStatus = kotakResponseVO.getAuthStatus();
            enrolled = kotakResponseVO.getEnrolled();
            authCode = kotakResponseVO.getAuthCode();
            retRefNumber = kotakResponseVO.getRetRefNumber();
            responseCode = kotakResponseVO.getResponseCode();
            remark = kotakResponseVO.getRemark();
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        int k = 0;

        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_kotak_details(detailid,trackingid,amount,batchnumber,cavv,eci,3ds_authstatus,3ds_enrolled,authcode,retrefnumber,responsecode,remark) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, batchNumber);
            pstmt.setString(5, cavv);
            pstmt.setString(6, eci);
            pstmt.setString(7, authStatus);
            pstmt.setString(8, enrolled);
            pstmt.setString(9, authCode);
            pstmt.setString(10, retRefNumber);
            pstmt.setString(11, responseCode);
            pstmt.setString(12, remark);
            k = pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(KotakPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(KotakPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());

        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return k;
    }

}

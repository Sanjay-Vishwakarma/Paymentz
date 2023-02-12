package net.partner;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.MerchantMonitoringManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.merchantmonitoring.MonitoringAlertDetailVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailServiceHelper;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jitendra
 * Date: 22/01/2018
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerMerchantResendLog extends HttpServlet
{
    Logger logger = new Logger(PartnerMerchantResendLog.class.getName());
    private static Logger log = new Logger(PartnerMerchantResendLog.class.getName());
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doProcess(request,response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session)){
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }


        String alertId =request.getParameter("alertid");
        String action=request.getParameter("action");
        String terminalId =request.getParameter("terminalid");

        MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
        MonitoringAlertDetailVO  monitoringAlertDetailVO=merchantMonitoringManager.getLogAlertDetail(alertId);

        Functions functions=new Functions();
        if("sendmail".equals(action)){
            Map<String,List<MonitoringAlertDetailVO>> stringListMap=merchantMonitoringManager.getRuleLogDetailPerTerminalForResend(alertId);
            try
            {
                MerchantDAO merchantDAO=new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(monitoringAlertDetailVO.getMemberId());
                String alertTeam=monitoringAlertDetailVO.getAlertTeam();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();

                HashMap mailPlaceHolder = new HashMap();
                mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                if(functions.isValueNull(monitoringAlertDetailVO.getReportFileName()))
                {
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, monitoringAlertDetailVO.getReportFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH,SETTLEMENT_FILE_PATH +monitoringAlertDetailVO.getReportFileName());
                }
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(stringListMap, merchantDetailsVO));
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                MailEventEnum mailEventEnum=getMerchantMonitoringEvent(alertTeam);
                asynchronousMailService.sendMerchantMonitoringAlert(mailEventEnum, mailPlaceHolder);
            }
            catch (Exception e){
                //System.out.println("Exception:::::"+e);
            }
            String msg="Mail Sent";
            request.setAttribute("msg",msg);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        else if("sendfile".equals(action)){
            try{
                File f = new File(SETTLEMENT_FILE_PATH+monitoringAlertDetailVO.getReportFileName());
                String exportPath=monitoringAlertDetailVO.getReportFileName();
                String filename=f.getName();
                if(filename==null || filename.isEmpty())
                {
                    String errormsg = "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available.<BR></b></font></center>";
                    request.setAttribute("msg", errormsg);
                    RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                sendFile(exportPath,filename,f,response);
            }
            catch (Exception e){
                String msg="Internal server error occurred while processing your request";
                request.setAttribute("msg",msg);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                //System.out.println("Exception:::::"+e);
            }
        }
        else if ("suspendaccount".equals(action)){
            Connection con = null;
            try{
                PreparedStatement pstmt = null;
                con = Database.getConnection();
                String query = "UPDATE member_account_mapping SET isActive='N' WHERE terminalid=?";
                pstmt = con.prepareStatement(query.toString());
                pstmt.setString(1, terminalId);
                int k;
                k=pstmt.executeUpdate();
                if(k>0){
                    String errormsg = "<center><font class=\"text\" face=\"arial\"><b>Terminal Suspended Successfully<BR></b></font></center>";
                    request.setAttribute("msg", errormsg);
                    RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                }
            }
            catch (Exception e){
                String msg="Internal server error occurred while processing your request";
                request.setAttribute("msg",msg);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        else if ("alertAction".equals(action))
        {
            try{
                MonitoringAlertDetailVO monitoringAlertDetailVO1= (MonitoringAlertDetailVO) merchantMonitoringManager.getAlertDetail(alertId);
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOsList=merchantMonitoringManager.getRuleLogDetail(alertId);
                Map<String,List<MonitoringAlertDetailVO>> stringListMap=merchantMonitoringManager.getRuleLogDetailPerTerminal(alertId);
                request.setAttribute("details", monitoringAlertDetailVO1);
                request.setAttribute("monitoringAlertDetailVOsList",monitoringAlertDetailVOsList);
                request.setAttribute("stringListMap",stringListMap);
                RequestDispatcher rd = request.getRequestDispatcher("/monitoringAlertDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (Exception e){
                String msg="Internal server error occurred while processing your request";
                request.setAttribute("msg",msg);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
        }
        else{
            String msg = "Invalid Action";
            request.setAttribute("msg", msg);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMonitoringRuleLog.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
    }
    public static boolean sendFile(String filepath,String filename,File f,HttpServletResponse response)throws Exception
    {
        int length = 0;
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1)){
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();

        log.info("Successfully donloaded  file======"+filename);
        return true;
    }

    public MailEventEnum getMerchantMonitoringEvent(String merchantTeam)
    {
        if ("Sales".equals(merchantTeam)){
            return MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT;
        }
        else if ("Refund".equals(merchantTeam)){
            return MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_MERCHANT;
        }
        else if ("Chargeback".equals(merchantTeam)){
            return MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_MERCHANT;
        }
        else if ("Fraud".equals(merchantTeam)){
            return MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_FRAUD;
        }
        else if ("Technical".equals(merchantTeam)){
            return MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_TECH;
        }
        return null;
    }
}

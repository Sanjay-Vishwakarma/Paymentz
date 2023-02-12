package net.partner;

import com.directi.pg.*;
import com.manager.TerminalManager;
import com.manager.dao.EmiDAO;
import com.manager.vo.TerminalVO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Rihen
 * Date: 4/23/19
 * Time: 6:07pm
 * To change this template use File | Settings | File Templates.
 */
public class EmiConfiguration extends HttpServlet
{
    static Logger log                                   = new Logger(EmiConfiguration.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(EmiConfiguration.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        for(Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for EmiConfiguration-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        HttpSession session         = req.getSession();
        User user                   = (User) session.getAttribute("ESAPIUserSessionKey");
        String merchantid           = (String) session.getAttribute("merchantid");
        StringBuilder sErrorMessage = new StringBuilder();
        PartnerFunctions partner    = new PartnerFunctions();
        Functions functions         = new Functions();
        TerminalManager terminalManager = new TerminalManager();
        TerminalVO terminalVO           = null;
        List<EmiVO> emiVOList           = null;
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        ResourceBundle rb1                          = null;
        String language_property1                   = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String EmiCofiguration_memberid_errormsg                = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_memberid_errormsg")) ? rb1.getString("EmiCofiguration_memberid_errormsg") : "Invalid Member ID or Member ID should not be empty";
        String EmiCofiguration_partnerid_errormsg               = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_partnerid_errormsg")) ? rb1.getString("EmiCofiguration_partnerid_errormsg") : "Invalid Partner ID or Partner ID should not be empty";
        String EmiCofiguration_member_configuration_errormsg    = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_member_configuration_errormsg")) ? rb1.getString("EmiCofiguration_member_configuration_errormsg") : "Invalid partner member configuration.";
        String EmiCofiguration_terminalid_errormsg              = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_terminalid_errormsg")) ? rb1.getString("EmiCofiguration_terminalid_errormsg") : "Invalid Terminal ID or Terminal ID should not be empty";
        String EmiCofiguration_no_errormsg                      = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_no_errormsg")) ? rb1.getString("EmiCofiguration_no_errormsg") : "No Records Found";
        String EmiCofiguration_please_errormsg                  = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_please_errormsg")) ? rb1.getString("EmiCofiguration_please_errormsg") : "Please Provide Member ID, Terminal ID, Start Date, End Date and EMI Period.";
        String EmiCofiguration_start_date_errormsg              = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_start_date_errormsg")) ? rb1.getString("EmiCofiguration_start_date_errormsg") : "Invalid StartDate or End Date.";
        String EmiCofiguration_please_emi_errormsg              = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_please_emi_errormsg")) ? rb1.getString("EmiCofiguration_please_emi_errormsg") : "Please provide EMI Period.";
        String EmiCofiguration_please_memberid_errormsg         = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_please_memberid_errormsg")) ? rb1.getString("EmiCofiguration_please_memberid_errormsg") : "Please provide Member ID.";
        String EmiCofiguration_please_terminalid_errormsg       = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_please_terminalid_errormsg")) ? rb1.getString("EmiCofiguration_please_terminalid_errormsg") : "Please provide Terminal ID.";
        String EmiCofiguration_emi_updated_errormsg             = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_emi_updated_errormsg")) ? rb1.getString("EmiCofiguration_emi_updated_errormsg") : "EMI Updated Successful.";
        String EmiCofiguration_emi_failed_errormsg              = StringUtils.isNotEmpty(rb1.getString("EmiCofiguration_emi_failed_errormsg")) ? rb1.getString("EmiCofiguration_emi_failed_errormsg") : "EMI Updated Failed.";

        String active ="N";
        if(functions.isValueNull(req.getParameter("active")))
            active = req.getParameter("active");
        transactionLogger.error("ACTIVE---- "+active);
        String memberid     = req.getParameter("memberid");
        String terminal     = req.getParameter("terminal");
        String emiPeriod    = req.getParameter("emiPeriod");
        String fromdate     = req.getParameter("fdate");
        String todate       = req.getParameter("tdate");
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String pid          = req.getParameter("pid");
        String partnerid    = "" ;
        String fdtstamp     = "";
        String tdtstamp     = "";
        StringBuffer errorMessage   = new StringBuffer();
        String action               = req.getParameter("create");
        String EOL                  = "<BR><BR>";
        if(!functions.isValueNull(terminal)&&!functions.isValueNull(memberid))
        {
            StringBuffer str        = new StringBuffer("Invalid MemberID & Invalid TerminalID");
            req.setAttribute("error",str.toString());
            RequestDispatcher redi  = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
            redi.forward(req, res);
            return;
        }
        if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 50, false))
        {
            errorMessage.append( EmiCofiguration_memberid_errormsg+ EOL);
        }
        if (!ESAPI.validator().isValidInput("terminalid", terminal, "Numbers", 50, false))
        {
            errorMessage.append(EmiCofiguration_terminalid_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 50, true))
        {
            errorMessage.append( EmiCofiguration_partnerid_errormsg+ EOL);
        }
        if (functions.isValueNull(errorMessage.toString()))
        {
            req.setAttribute("error", errorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerMemberMapped(memberid, req.getParameter("pid")))
            {
                req.setAttribute("error",EmiCofiguration_member_configuration_errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else if (!functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerSuperpartnerMembersMapped(memberid, req.getParameter("partnerid")))
            {
                req.setAttribute("error",EmiCofiguration_member_configuration_errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

        }catch(Exception e){
            log.error("Exception---" + e);
        }
        if (!ESAPI.validator().isValidInput("terminalid", terminal, "Numbers", 50, false))
        {

            errorMessage.append(EmiCofiguration_terminalid_errormsg+EOL);
            req.setAttribute("error", errorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }


        Calendar rightNow       = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        EmiDAO emiDAO           = new EmiDAO();
        Date date               = null;
        int count               = 0;

        try
        {
            terminalVO      = terminalManager.getMemberTerminalInfo(memberid,terminal);
            if(terminalVO == null)
            {
                errorMessage.append(EmiCofiguration_no_errormsg + EOL);
            }
            if (functions.isValueNull(errorMessage.toString()))
            {
                req.setAttribute("error", errorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            emiVOList       = emiDAO.listOfEmiForMerchant(memberid,terminal);
            if (functions.isValueNull(fromdate) && functions.isValueNull(todate))
            {
                date            = sdf.parse(fromdate);
                rightNow.setTime(date);
                String fdate    = String.valueOf(rightNow.get(Calendar.DATE));
                String fmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
                String fyear    = String.valueOf(rightNow.get(Calendar.YEAR));
                //to Date
                date            = sdf.parse(todate);
                rightNow.setTime(date);
                String tdate    = String.valueOf(rightNow.get(Calendar.DATE));
                String tmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
                String tyear    = String.valueOf(rightNow.get(Calendar.YEAR));

                String starttime[]= startTime.split("\\:");
                String endtime[]= endTime.split("\\:");

                fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, starttime[0], starttime[1], starttime[2]);
                tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endtime[0], endtime[1], endtime[2]);
            }
            if(!functions.isValueNull(fromdate) && !functions.isValueNull(todate) && !functions.isValueNull(emiPeriod) && !functions.isValueNull(memberid) && !functions.isValueNull(terminal)){
                sErrorMessage.append(EmiCofiguration_please_errormsg);
                req.setAttribute("sErrorMessage",sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            int fromDate    = Integer.parseInt(fdtstamp);
            int endDate     = Integer.parseInt(tdtstamp);

            if(fromDate>endDate){
                sErrorMessage.append(EmiCofiguration_start_date_errormsg);
                req.setAttribute("sErrorMessage", sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if(!functions.isValueNull(emiPeriod)){
                sErrorMessage.append(EmiCofiguration_please_emi_errormsg);
                req.setAttribute("sErrorMessage", sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if(!functions.isValueNull(memberid)){
                sErrorMessage.append(EmiCofiguration_please_memberid_errormsg);
                req.setAttribute("sErrorMessage", sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            
            if(!functions.isValueNull(terminal)){
                sErrorMessage.append(EmiCofiguration_please_terminalid_errormsg);
                req.setAttribute("sErrorMessage", sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }




            if ("create".equalsIgnoreCase(action))
            {

                emiDAO.removeDetails(terminal);
                transactionLogger.error("in create ----" + fdtstamp + " " + tdtstamp + " " + memberid + " " + emiPeriod + " " + terminal + " " + active);
                count = emiDAO.addEmiConfig(fdtstamp, tdtstamp, memberid, emiPeriod, terminal, active);
                if (count > 0) {
                    sErrorMessage.append(EmiCofiguration_emi_updated_errormsg);
                    emiVOList = emiDAO.listOfEmiForMerchant(memberid,terminal);
                }
                else {
                    sErrorMessage.append(EmiCofiguration_emi_failed_errormsg);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception---" + e);
            Functions.NewShowConfirmation1("Error", "Internal System Error...");
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("emiVOList",emiVOList);
        req.setAttribute("sErrorMessage",sErrorMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/emiConfiguration.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}

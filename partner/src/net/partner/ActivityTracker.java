package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.ActivityTrackManager;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Wallet on 26/10/2020.
 */
public class ActivityTracker extends HttpServlet
{
    private static Logger log                       = new Logger(ActivityTracker.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in Patner Activity Tracker");
        HttpSession session                     = req.getSession();
        User user                               =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner                = new PartnerFunctions();

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String errormsg = "";
        String EOL      = "<BR>";

        Functions functions         = new Functions();
        String username             = "";
        String modulename           = "";
        String partner_Id            = "";
        int records                 = 15;
        int pageno                  = 1;
        int start = 0; // start index
        int end = 0; // end index
        String fyear            = "";
        String fmonth           = "";
        String fdate            = "";
        String tyear            = "";
        String tmonth           = "";
        String tdate            = "";
        Calendar rightNow               = Calendar.getInstance();
        SimpleDateFormat sdf            = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
        Date date                       = null;

        ActivityTrackManager activityTrackManager   = null;
        Hashtable hash                              = null;
        ActivityTrackerVOs activityTrackerVOs       = null;
        String startTime    = "00:00:00";
        String endTime      = "23:59:59";
        String fromdate     = null;
        String todate       = null;
        String errorList =null;
        try
        {
            String pid              = req.getParameter("pid");
            String partnerid        = (String)session.getAttribute("merchantid");
            errorList               = validateOptionalParameters(req);

            if(errorList !=null && !errorList.equals(""))
            {
                //redirect to jsp page for invalid data entry
                req.setAttribute("error",errorList);
                RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
            {
                req.setAttribute("error","Invalid Partner ID");
                RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            //role         = req.getParameter("role");
            username        = req.getParameter("username");
            modulename      = req.getParameter("modulename");
            //partnerId       = req.getParameter("partnerId");
            String timezone = req.getParameter("timezone");
            fromdate        = req.getParameter("fromdate");
            todate          = req.getParameter("todate");

            log.error("Input pid  >>>>>>>>>> "+pid);
            log.error("login partnerid >>>>>>>>>> "+partnerid);

            if (functions.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid, partnerid))
            {
                partner_Id = pid;
            }else if (!functions.isValueNull(pid))
            {
                /*if (functions.isValueNull(merchantid) && !partner.isPartnerSuperpartnerMembersMapped(merchantid, partnerid))
                {
                    req.setAttribute("message", "Invalid Partner Member Mapping");
                    RequestDispatcher rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
               */ String Roles = partner.getRoleofPartner(partnerid);
                if (Roles.contains("superpartner"))
                {
                    LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
                    memberHash =partner.getPartnerDetails(partnerid);
                    partner_Id = partnerid;
                    for(int partnerID : memberHash.keySet())
                    {
                        partner_Id+= "," + Integer.toString(partnerID);
                    }
                }
                else
                {
                    partner_Id = partnerid;
                }
            }else
            {
                req.setAttribute("error", "Invalid Partner Mapping");
                RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            log.error("partner_Id >>>>>>>>>> "+partner_Id);

            if (functions.isValueNull(timezone))
            {
                timezone = timezone.substring(0,timezone.indexOf("|"));
            }
            //from date
            if (!functions.isValueNull(fromdate) || !functions.isValueNull(todate))
            {
                req.setAttribute("error", "Please provide the appropriate From Date and To Date for search.");
                RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            Date frtzdate       = sdf.parse(fromdate);
            String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
            //to Date
            Date totzdate       = sdf.parse(todate);
            String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
            String fdatetime1   = fdatetime;
            String tdatetime1   = tdatetime;

            date        = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate   = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date    = sdf.parse(todate);
            rightNow.setTime(date);
            tdate   = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");
            String fdtstamp         = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp         = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            pageno      = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records     = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            activityTrackerVOs       = new ActivityTrackerVOs();
            activityTrackManager     = new ActivityTrackManager();

            //activityTrackerVOs.setRole(role);
            activityTrackerVOs.setUser_name(username);
            activityTrackerVOs.setModule_name(modulename);
            activityTrackerVOs.setPartnerId(partner_Id);

            hash = activityTrackManager.getActivityList(activityTrackerVOs, records, pageno, tdtstamp, fdtstamp);
            req.setAttribute("activitylist", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
       /* catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/activityTracker.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }*/
        catch (Exception e)
        {
            log.error("Exception ::::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }

    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PARTNER_ID);
        inputFieldsListOptional.add(InputFields.USERNAME);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);


        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PARTNER_ID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

        /*ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory, errorList2,false);*/

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error  +error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }

        /*if(!errorList2.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList2.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList2.getError(inputFields.toString()).getLogMessage());
                    error = error + error.concat(errorList2.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }*/
        return error;
    }


}

import com.directi.pg.*;
import com.manager.dao.EmiDAO;
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

/**
 * Created by Mahima Rai on 11/5/2018.
 */
public class EmiConfig extends HttpServlet
{
    private static Logger log = new Logger(Invoice.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String merchantid = (String) session.getAttribute("merchantid");
        StringBuilder sErrorMessage = new StringBuilder();
        Merchants merchants = new Merchants();
        Functions functions = new Functions();
        List<EmiVO> emiVOList=null;
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String active = "N";
        if(functions.isValueNull(req.getParameter("active")))
            active = req.getParameter("active");
        String emiPeriod=req.getParameter("emiPeriod");
        String fromdate = req.getParameter("fdate");
        String todate = req.getParameter("tdate");
        String startTime=req.getParameter("starttime");
        String endTime=req.getParameter("endtime");
        String fdtstamp = "";
        String tdtstamp = "";
        String errorMessage = "";
        String action=req.getParameter("create");

        if (functions.isValueNull(errorMessage))
        {
            req.setAttribute("error", errorMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/emiConfig.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        EmiDAO emiDAO=new EmiDAO();
        Date date = null;
        int count=0;

        try
        {
//            emiVOList=emiDAO.listOfEmiForMerchant(merchantid,ter);
            if (functions.isValueNull(fromdate) && functions.isValueNull(todate))
            {
                date = sdf.parse(fromdate);
                rightNow.setTime(date);
                String fdate = String.valueOf(rightNow.get(Calendar.DATE));
                String fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
                String fyear = String.valueOf(rightNow.get(Calendar.YEAR));
                //to Date
                date = sdf.parse(todate);
                rightNow.setTime(date);
                String tdate = String.valueOf(rightNow.get(Calendar.DATE));
                String tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
                String tyear = String.valueOf(rightNow.get(Calendar.YEAR));

                String starttime[]= startTime.split("\\:");
                String endtime[]= endTime.split("\\:");

                fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, starttime[0], starttime[1], starttime[2]);
                tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endtime[0], endtime[1], endtime[2]);
            }
            if(!functions.isValueNull(fromdate) && !functions.isValueNull(todate) && !functions.isValueNull(emiPeriod)){
                sErrorMessage.append("Please Provide Start Date,End Date and EMI Period.");
                req.setAttribute("sErrorMessage",sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfig.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            int fromDate= Integer.parseInt(fdtstamp);
            int endDate= Integer.parseInt(tdtstamp);

            if(fromDate>endDate){
                sErrorMessage.append("Invalid StartDate or End Date.");
                req.setAttribute("sErrorMessage",sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfig.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if(!functions.isValueNull(emiPeriod)){
                sErrorMessage.append("Please provide EMI Period.");
                req.setAttribute("sErrorMessage",sErrorMessage.toString());
                req.setAttribute("emiVOList",emiVOList);
                RequestDispatcher rd = req.getRequestDispatcher("/emiConfig.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if ("create".equalsIgnoreCase(action))
            {
                //emiDAO.removeDetails(merchantid);
//                count = emiDAO.addEmiConfig(fdtstamp, tdtstamp, merchantid, emiPeriod, active);
                if (count > 0) {
                    sErrorMessage.append("EMI Updated Successful.");
//                    emiVOList = emiDAO.listOfEmiForMerchant(merchantid);
                }
                else {
                    sErrorMessage.append("EMI Updated Failed.");
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception In EMIConfig::::",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error...");
        }
        StringBuilder chargeBackMessage=new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("emiVOList",emiVOList);
        req.setAttribute("sErrorMessage",sErrorMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/emiConfig.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}

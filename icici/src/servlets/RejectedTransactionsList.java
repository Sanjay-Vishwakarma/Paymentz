import com.directi.pg.*;
import com.manager.dao.PartnerDAO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class RejectedTransactionsList extends HttpServlet
{
    private static Logger log = new Logger(RejectedTransactionsList.class.getName());
    Functions functions = new Functions();
    PartnerDAO partnerDAO = new PartnerDAO();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        boolean flag = false;
        String errorMsg = "";
        String EOL = "<BR>";

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Invalid Input", e);
            errorMsg += "<center><font class=\"text\" face=\"arial\"><b>" + errorMsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            log.debug("message..." + e.getMessage());
            req.setAttribute("errormessage", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        if (functions.isValueNull(req.getParameter("action")) && "RejectedTransactionsList".equals(req.getParameter("action")) && functions.isValueNull(req.getParameter("STrackingid")))
        {
            String icicitransid = req.getParameter("STrackingid");
            try
            {
                Hashtable hash = getTransactionDetails(icicitransid);
                req.setAttribute("transactionsdetails", hash);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (SystemError se)
            {
                log.error("SysyemError in doGet method", se);
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>" + errorMsg + se.getMessage() + EOL + "</b></font></center>";
                flag = false;
                log.error("message..." + se.getMessage());
                req.setAttribute("errormessage", errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (Exception e)
            {
                log.error("Exception in doGet method", e);
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>" + errorMsg + e.getMessage() + EOL + "</b></font></center>";
                flag = false;
                log.debug("message..." + e.getMessage());
                req.setAttribute("errormessage", errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
            return;
        }

        String toId = "";
        if (!req.getParameter("toid").equalsIgnoreCase("0"))
        {
            toId = req.getParameter("toid");
        }

        String description = req.getParameter("desc");
        String rejectReason = req.getParameter("rejectreason");
        String name = req.getParameter("name");
        String amount = req.getParameter("amount");
        String firstSix = req.getParameter("firstfourofccnum");
        String lastFour = req.getParameter("lastfourofccnum");
        String emailAddr = req.getParameter("emailaddr");
        String perfectMatch = Functions.checkStringNull(req.getParameter("perfectmatch"));
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String phone= req.getParameter("phone");
        String bankAccountNumber= req.getParameter("bankAccountNumber");
        String partnername=req.getParameter("partnerid");
        String trackingID = req.getParameter("STrackingid");

                if(functions.isValueNull(partnername)){
                    partnername=partnerDAO.getPartnerName(partnername);
                }

        Calendar rightNow = Calendar.getInstance();

        startTime   = startTime.trim();
        endTime     = endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime   = "00:00:00";
        }
        if (!functions.isValueNull(endTime))
        {
            endTime = "23:59:59";
        }

        String startTimeArr[]   = startTime.split(":");
        String endTimeArr[]     = endTime.split(":");

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);
        try
        {
            log.debug("All parameters are passed in listTransaction ");

            Hashtable hash = listTransactions(toId, partnername, description, name, emailAddr, rejectReason, amount, firstSix, lastFour, tdtstamp, fdtstamp, records, pageno, perfectMatch, phone, bankAccountNumber,trackingID);
            req.setAttribute("transactionsdetails", hash);
        }
        catch (SystemError se)
        {
            log.error("errror in doPost ", se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        catch (Exception e)
        {
            log.error("Exception:::: errror in doPost", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        if (!ESAPI.validator().isValidInput(phone, req.getParameter("phone"),"Phone",20,true))
        {
            req.setAttribute("message","Invalid Phone number");
            RequestDispatcher rd= req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        if (!ESAPI.validator().isValidInput(bankAccountNumber, req.getParameter("bankAccountNumber"),"OnlyNumber",50,true))
        {
            req.setAttribute("message","Invalid Bank Account Number");
            RequestDispatcher rd= req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req,res);
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    public Hashtable listTransactions(String toId, String partnername, String description, String name, String email, String rejectReason, String amount, String firstSix, String lastFour, String tdtstamp, String fdtstamp, int records, int pageno, String perfectmatch, String phone,String bankAccountNumber, String trackingID) throws SystemError
    {
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT * from transaction_fail_log where id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_fail_log as temp where id>0  ");
            if (functions.isValueNull(amount))
            {
                query.append(" and amount = '" + amount + "'");
                countquery.append(" and amount = '" + amount + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and email = '" + email + "'");
                countquery.append(" and email = '" + email + "'");
            }
            if (functions.isValueNull(toId))
            {
                query.append(" and toid = " + toId);
                countquery.append(" and toid = " + toId);
            }
            if (functions.isValueNull(partnername))
            {
                query.append(" and totype = '" + partnername + "'");
                countquery.append(" and totype = '" + partnername + "'");
            }
            if (functions.isValueNull(rejectReason))
            {
                query.append(" and rejectreason = '" + rejectReason + "'");
                countquery.append(" and rejectreason = '" + rejectReason + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and description = '" + description + "'");
                countquery.append(" and description = '" + description + "'");
            }
            if (functions.isValueNull(name))
            {
                String arr[] = name.split(" ");
                String firstName = "";
                String lastName = "";
                try
                {
                    firstName = arr[0];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    firstName = "";
                }
                try
                {
                    lastName = arr[1];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    lastName = "";
                }
                if (functions.isValueNull(firstName))
                {
                    query.append(" and firstname = '" + firstName + "'");
                    query.append(" and lastname = '" + lastName + "'");
                }
                if (functions.isValueNull(lastName))
                {
                    countquery.append(" and firstname = '" + firstName + "'");
                    countquery.append(" and lastname = '" + lastName + "'");
                }
            }
            if (functions.isValueNull(firstSix))
            {
                query.append(" and firstsix = '" + firstSix + "'");
                countquery.append(" and firstsix = '" + firstSix + "'");
            }
            if (functions.isValueNull(lastFour))
            {
                query.append(" and lastfour = '" + lastFour + "'");
                countquery.append(" and lastfour = '" + lastFour + "'");
            }
            if (functions.isValueNull(phone))
            {
                query.append(" and phone = '" + phone + "'");
                countquery.append(" and phone = '" + phone + "'");
            }
            if (functions.isValueNull(bankAccountNumber))
            {
                query.append(" and bankAccountNumber = '" + bankAccountNumber + "'");
                countquery.append(" and bankAccountNumber = '" + bankAccountNumber + "'");
            }
            if(functions.isValueNull(trackingID))
            {
                query.append(" and id = '" + trackingID + "'");
                countquery.append(" and id = '" + trackingID + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= '" + fdtstamp + "'");
                countquery.append(" and dtstamp >= '" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= '" + tdtstamp + "'");
                countquery.append(" and dtstamp <= '" + tdtstamp + "'");
            }
            query.append(" order by  dtstamp DESC");
            query.append(" limit " + start + "," + end);

            log.debug("===query===" + query);
            log.debug("===count query===" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getTransactionDetails(String iciciTransId) throws SystemError
    {
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT m.*,tfl.*,from_unixtime(tfl.dtstamp) as transactiontime from transaction_fail_log as tfl left join members as m on tfl.toid=m.memberid where id=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, iciciTransId);
            log.debug("trackingid query----" + pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.NAME_SMALL);
        inputFieldsListMandatory.add(InputFields.DESC);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.FIRSTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.LASTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.ORDERDESC);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.PARTNERID);
        inputValidator.InputValidations(req, inputFieldsListMandatory, true);
    }
}

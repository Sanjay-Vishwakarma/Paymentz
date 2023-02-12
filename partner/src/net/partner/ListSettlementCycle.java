package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.utils.CommonFunctionUtil;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Sandip on 3/14/2018.
 */
public class ListSettlementCycle extends HttpServlet
{
    Logger logger = new Logger(PartnerMerchantCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        int records = 15;
        int pageno = 1;

        Functions functions = new Functions();
        Hashtable hash = null;

        String accountId = request.getParameter("reqaccountid");
        String startDate = request.getParameter("fromdate");
        String endDate  = request.getParameter("todate");
        String status = request.getParameter("status");
        String partnerId=request.getParameter("partnerid");
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        if(!functions.isValueNull(partnerId))
        {
            if (user.getRoles().contains("superpartner"))
            {
                partnerId = partnerFunctions.getListOfSubPartner((String) session.getAttribute("merchantid"));
            }
            else
                partnerId = (String) session.getAttribute("merchantid");
        }

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            request.setAttribute("message", e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
            startDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
            endDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT *,FROM_UNIXTIME(dtstamp) AS creationon  FROM merchant_settlement_cycle_master WHERE id>0");
            StringBuilder countQuery = new StringBuilder("select count(*) from merchant_settlement_cycle_master WHERE id>0");
            if (functions.isValueNull(partnerId))
            {

                query.append(" and partnerid IN("+partnerId+")");
                countQuery.append(" and partnerid IN("+partnerId+")");
            }
            query.append(" AND STATUS IN('Initiated','BankWireGenerated','SettlementUploaded','MerchantWireGenerated','Completed')");
            countQuery.append(" AND STATUS IN('Initiated','BankWireGenerated','SettlementUploaded','MerchantWireGenerated','Completed')");
            if (functions.isValueNull(accountId))
            {

                query.append(" and accountid=?");
                countQuery.append(" and accountid=?");
            }
            if (functions.isValueNull(startDate))
            {
                query.append(" and startdate>=?");
                countQuery.append(" and startdate>=?");
            }
            if (functions.isValueNull(endDate))
            {
                query.append(" and enddate<=?");
                countQuery.append(" and enddate<=?");
            }
            if (functions.isValueNull(status)&& !status.equalsIgnoreCase("all"))
            {
                query.append(" and status=?");
                countQuery.append(" and status=?");
            }
            query.append(" order by id desc LIMIT ? , ? ");

            logger.debug("Query  :-" + query.toString());
            logger.debug("CountQuery  :-" + countQuery.toString());

            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countQuery.toString());

           /* if (functions.isValueNull(partnerId))
            {
                pstmt.setString(counter, partnerId);
                pstmt1.setString(counter, partnerId);
                counter++;
            }*/
            if (functions.isValueNull(accountId))
            {
                pstmt.setString(counter, accountId);
                pstmt1.setString(counter, accountId);
                counter++;
            }
            if (functions.isValueNull(startDate))
            {
                pstmt.setString(counter, startDate);
                pstmt1.setString(counter, startDate);
                counter++;
            }
            if (functions.isValueNull(endDate))
            {
                pstmt.setString(counter, endDate);
                pstmt1.setString(counter, endDate);
                counter++;
            }
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            logger.debug("Query pstmt:-" + pstmt);
            logger.debug("CountQuery pstmt:-" + pstmt1);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            rs = pstmt1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
            request.setAttribute("transdetails", hash);
            logger.debug("forward to jsp" + hash);
        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        /*catch (ParseException e)
        {
            logger.error("ParseException", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }*/
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);

        inputValidator.InputValidations(req, inputFieldsListMandatory, true);
    }
}

package net.partner;

import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Swamy on 26/4/2017.
 */
public class ContractualPartner extends HttpServlet
{
    private static Logger logger = new Logger(ContractualPartner.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Inside AddContractualPartner.java");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rdError = request.getRequestDispatcher("/contractualPartner.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/contractualPartner.jsp?Success=YES&ctoken=" + user.getCSRFToken());

        String error = "";

        int records = 15;
        int pageno = 1;

        int start = 0; // start index
        int end = 0; // end index

        //String partnerId = "";
        String bankName = request.getParameter("bankName");
        String partnerid = (String) session.getAttribute("merchantid");
        String pid = request.getParameter("pid");

        String errorMsg = "";

        if (!ESAPI.validator().isValidInput("pid", request.getParameter("pid"), "Numbers", 20, true))
        {
            errorMsg = "Invalid Partner ID.";
            request.setAttribute("error1", errorMsg);
            rdError.forward(request, response);
            return;
        }
        try
        {
            if (functions.isValueNull(request.getParameter("pid")) && !partner.isPartnerSuperpartnerMapped(request.getParameter("pid"), partnerid))
            {
                 errorMsg = "Invalid Partner Mapping.";
                request.setAttribute("error1", errorMsg);
                rdError.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception---"+e);
        }

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection con = null;
        ResultSet rs = null;
        Hashtable hash = null;
        StringBuffer query =null;
        StringBuffer countquery = null;
            try
            {
                //con = Database.getConnection();
                con = Database.getRDBConnection();
                if(functions.isValueNull(pid)){
                    query = new StringBuffer("select ca.partnerid,bankname,ca.contractual_partnerid,ca.contractual_partnername FROM contractualpartner_appmanager ca  WHERE ca.partnerid > 0");
                    countquery = new StringBuffer("select count(*) FROM contractualpartner_appmanager ca WHERE ca.partnerid > 0");

                        query.append(" and ca.partnerId =" + ESAPI.encoder().encodeForSQL(me, pid));
                        countquery.append(" and ca.partnerId =" + ESAPI.encoder().encodeForSQL(me, pid));



                }else{
                query = new StringBuffer("select ca.partnerid,bankname,ca.contractual_partnerid,ca.contractual_partnername FROM contractualpartner_appmanager ca JOIN partners p ON ca.partnerid = p.partnerId WHERE ca.partnerid > 0");
                countquery = new StringBuffer("select count(*) FROM contractualpartner_appmanager ca JOIN partners p ON ca.partnerid = p.partnerId WHERE ca.partnerid > 0");

                 query.append(" and (p.partnerId =" + ESAPI.encoder().encodeForSQL(me, partnerid));
                    query.append(" or p.superadminid =" + ESAPI.encoder().encodeForSQL(me, partnerid)+")");
                    countquery.append(" and (p.partnerId =" + ESAPI.encoder().encodeForSQL(me, partnerid));
                    countquery.append(" or p.superadminid =" + ESAPI.encoder().encodeForSQL(me, partnerid) + ")");
                }

                if (functions.isValueNull(bankName))
                {
                    query.append(" and bankname = '" + ESAPI.encoder().encodeForSQL(me, bankName) + "'");
                    countquery.append(" and bankname = '" + ESAPI.encoder().encodeForSQL(me, bankName) + "'");
                }

                query.append(" order by partnerid desc LIMIT " + start + "," + end);
                System.out.println("Query:-" + query);
                logger.debug("Query:-" + query);
                logger.debug("CountQuery:-" + countquery);
                hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));

                rs = Database.executeQuery(countquery.toString(), con);
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");

                if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
                request.setAttribute("transdetails", hash);

            }
            catch (SystemError systemError)
            {
                logger.error("SystemError---" + systemError);
            }
            catch (SQLException e)
            {
                logger.error("SQLException---" + e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }
            rdSuccess.forward(request, response);

    }
}

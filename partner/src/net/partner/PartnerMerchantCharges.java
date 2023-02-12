package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by swamy on 27/8/2015.
 */
public class PartnerMerchantCharges extends HttpServlet
{
    Logger logger=new Logger(PartnerMerchantCharges.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {

        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Connection conn = null;
        ResultSet rs = null;

        int records=15;
        int pageno=1;

        Functions functions = new Functions();
        int counter = 1;
        HashMap hash = null;

        String partnerid = request.getParameter("partnerId");
        String delete="";

        int start = 0; // start index
        int end = 0; // end index
        if(functions.isValueNull(request.getParameter("delete")))
            delete = request.getParameter("delete");

        String qry="";
        PreparedStatement pstmt2=null;
        String success="";
        String message="";

        if (delete.equalsIgnoreCase("delete"))
        {
            try
            {
                String id1=request.getParameter("ids");
                String ids[]={};
                ids = id1.split(",");
                for (String id : ids)
                {
                    conn=Database.getConnection();
                    qry = "DELETE FROM member_accounts_charges_mapping WHERE mappingid=?";
                    pstmt2 = conn.prepareStatement(qry);
                    pstmt2.setString(1,id);
                    int j = pstmt2.executeUpdate();
                    if (j>= 1)
                    {
                        success = "Records Deleted Successfully.";
                    }
                    else
                    {
                        message = "Delete failed";
                    }
                    request.setAttribute("success", success);
                    request.setAttribute("message",message);
                }
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            catch (Exception e)
            {
                logger.error(e);
                request.setAttribute("message",e.getMessage());
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }

        try
        {
            //validateOptionalParameter(request);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(request, inputFieldsListMandatory, true);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            request.setAttribute("message", e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        String error="";
        if (!ESAPI.validator().isValidInput("pid",request.getParameter("pid"),"Numbers",20,true))
        {
            logger.error("Invalid Partner ID.");
            error="Invalid Partner ID.";
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        if (!ESAPI.validator().isValidInput("memberid",request.getParameter("memberid"),"Numbers",20,true))
        {
            logger.error("Invalid Merchant ID.");
            error="Invalid Merchant ID.";
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        if (!ESAPI.validator().isValidInput("terminalid",request.getParameter("terminalid"),"Numbers",20,true))
        {
            logger.error("Invalid Terminal ID.");
            error="Invalid Terminal ID.";
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        String memberId = request.getParameter("memberid");
        String pid = request.getParameter("pid");
        try
        {
            if(functions.isValueNull(memberId))
            {
                if (functions.isValueNull(pid) && !partner.isPartnerMemberMapped(memberId, pid))
                {
                    logger.error("PartnerID and MerchantID mismatch.");
                    error = "PartnerID and MerchantID mismatch.";
                    request.setAttribute("error", error);
                    RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            if(functions.isValueNull(pid) && !partner.isPartnerSuperpartnerMapped(pid, partnerid)){

                error = "Invalid Partner mapping.";
                request.setAttribute("error", error);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        catch(Exception e){
            logger.error("Exception---" + e);
        }
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        String terminalid = request.getParameter("terminalid");
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        //CHANGED THE QUERY TO FETCH MEMBERS AS PER PARTNER AND SUPERPARTNER.
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("SELECT macm.mappingid,macm.memberid,macm.terminalid,macm.accountid,macm.paymodeid,macm.cardtypeid,macm.chargeid,cm.chargename,macm.chargevalue,macm.sequencenum,macm.agentchargevalue,macm.partnerchargevalue,m.partnerId as partnerid FROM member_accounts_charges_mapping AS macm JOIN charge_master AS cm ON cm.chargeid=macm.chargeid JOIN members as m ON m.memberid=macm.memberid JOIN partners p ON m.partnerId=p.partnerId");
            StringBuilder countquery = new StringBuilder("select count(*) from member_accounts_charges_mapping AS macm JOIN charge_master AS cm ON cm.chargeid=macm.chargeid JOIN members as m ON m.memberid=macm.memberid JOIN partners p ON m.partnerId=p.partnerId");

            if (functions.isValueNull(pid))
            {
                query.append(" and  m.partnerId=?");
                countquery.append(" and  m.partnerId=?");
            }
            else
            {
                query.append(" and (p.superadminid=? or m.partnerId=?)");
                countquery.append(" and (p.superadminid=? or m.partnerId=?)");
            }
            if (functions.isValueNull(memberId))
            {
                //query.append(" and macm.memberid='" + ESAPI.encoder().encodeForSQL(me,memberId ) + "'");
                query.append(" and macm.memberid= ? ");
                countquery.append(" and macm.memberid= ? ");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and terminalid= ? ");
                countquery.append(" and terminalid= ? ");
            }
            query.append(" order by macm.mappingid desc LIMIT ? , ? ");
            logger.debug("Query:-"+query);
            logger.debug("CountQuery:-"+countquery);

            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countquery.toString());
            if (functions.isValueNull(pid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, pid));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, pid));
                counter++;

            }
            else
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,partnerid));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me,partnerid));
                counter++;
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,partnerid ));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, partnerid));
                counter++;
            }

            if (functions.isValueNull(memberId))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,memberId ));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me,memberId ));
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me,terminalid ));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me,terminalid ));
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);
            logger.debug("pstmt:::::" + pstmt);
            logger.debug("pstmt1:::::"+pstmt1);

            hash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
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
            logger.debug("forward to jsp"+hash);
        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantCharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    /*private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/
}











package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/17/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberDetailList extends HttpServlet
{
    private static Logger log = new Logger(MemberDetailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        String partnerid=(String)session.getAttribute("merchantid");
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions=new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        int records=30;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index

        String memberId = null;
        String contactName=null;
        String companyName=null;
        String emailId=null;
        String userName=null;
        String status=null;
        String country=null;
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String fdtstamp = null;
        String tdtstamp = null;
        String dateType = null;
        String pid = null;
        String superpartner= null;
        errorList = validateOptionalParameters(req);
        HashMap memberHash = null;

        if(errorList!=null && !errorList.equals(""))
        {
            //redirect to jsp page for invalid data entry
            req.setAttribute("error",errorList);
            RequestDispatcher rd = req.getRequestDispatcher("/memberlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            memberId=req.getParameter("memberid");
            companyName=req.getParameter("company_name");
            contactName=req.getParameter("contact_persons");
            emailId=req.getParameter("contact_emails");
            userName=req.getParameter("username");
            status=req.getParameter("status");
            country=req.getParameter("country");
            req.setAttribute("memberid",memberId);
            req.setAttribute("company_name",companyName);
            req.setAttribute("contact_persons",contactName);
            req.setAttribute("contact_emails",emailId);
            req.setAttribute("username",userName);
            req.setAttribute("status",status);
            req.setAttribute("country",country);
            fdate = req.getParameter("fromdate");
            tdate = req.getParameter("todate");
            fmonth = req.getParameter("fmonth");
            tmonth =  req.getParameter("tmonth");
            fyear = req.getParameter("fyear");
            tyear = req.getParameter("tyear");
            dateType = req.getParameter("datetype");
            pid = req.getParameter("partnerlist");

            Calendar cal= Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date= null;
            try
            {
                if(functions.isFutureDateComparisonWithFromAndToDate(fdate, tdate, "dd/MM/yyyy"))
                {
                    req.setAttribute("catchError","Invalid From & To date");
                    RequestDispatcher rd = req.getRequestDispatcher("/memberlist.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }

                date = sdf.parse(fdate);
                cal.setTime(date);
                fdate = String.valueOf(cal.get(Calendar.DATE));
                fmonth = String.valueOf(cal.get(Calendar.MONTH));
                fyear = String.valueOf(cal.get(Calendar.YEAR));

                //to Date
                date = sdf.parse(tdate);
                cal.setTime(date);
                tdate = String.valueOf(cal.get(Calendar.DATE));
                tmonth = String.valueOf(cal.get(Calendar.MONTH));
                tyear = String.valueOf(cal.get(Calendar.YEAR));

                log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);
                //conversion to dtstamp
                fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
                tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            }
            catch (Exception e)
            {
                log.debug("Exception:::"+e);
            }
            res.setContentType("text/html");
            req.setAttribute("fdtstamp", fdtstamp);
            req.setAttribute("tdtstamp", tdtstamp);

        }
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 30);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        try
        {
            start = (pageno - 1) * records;
            end = records;
            //conn= Database.getConnection();
            //CHANGE THE QUERY TO FETCH MEMBERS AS PER PARTNER AND SUPERPARTNER
            conn= Database.getRDBConnection();
            StringBuilder selectMemberDetail=new StringBuilder("SELECT * FROM partners p, members m where m.partnerId=p.partnerId ");
            StringBuilder countquery = new StringBuilder("select count(*) from partners p, members m where m.partnerId=p.partnerId");

            if(functions.isValueNull(pid))
            {
                superpartner = pid;
            }else{
                superpartner = partnerid;
            }
            String Roles = partner.getRoleofPartner(partnerid);
            if(functions.isValueNull(pid))
            {
                selectMemberDetail.append(" and m.partnerId=?");
                countquery.append(" and m.partnerId=?");
            }else if (Roles.contains("superpartner"))
            {
                    selectMemberDetail.append(" and (p.superadminid=? or m.partnerId=?)");
                    countquery.append(" and (p.superadminid=? or m.partnerId=?)");
            }

            /*if(functions.isValueNull(pid))
            {
                superpartner = pid;
            }else{
                superpartner = partnerid;
            }
            String Roles = partner.getRoleofPartner(superpartner);
            if(functions.isValueNull(superpartner))
            {
                if (Roles.equals("superpartner"))
                {
                    selectMemberDetail.append(" and (p.superadminid=? or m.partnerId=?)");
                    countquery.append(" and (p.superadminid=? or m.partnerId=?)");
                }
                else
                {
                    selectMemberDetail.append(" and m.partnerId=?");
                    countquery.append(" and m.partnerId=?");
                }
            }*/

            if(functions.isValueNull(memberId))
            {
                selectMemberDetail.append(" and m.memberid=?");
                countquery.append(" and m.memberid=?");
            }
            if(functions.isValueNull(userName))
            {
                selectMemberDetail.append(" and m.login= ? ");
                countquery.append(" and m.login= ? ");
            }
            if(functions.isValueNull(companyName))
            {
                selectMemberDetail.append(" and m.company_name=?");
                countquery.append(" and m.company_name=?");
            }
            if(functions.isValueNull(contactName))
            {
                selectMemberDetail.append(" and m.contact_persons=?");
                countquery.append(" and m.contact_persons= ?");
            }
            if(functions.isValueNull(emailId))
            {
                selectMemberDetail.append(" and m.contact_emails=?");
                countquery.append(" and m.contact_emails=?");
            }
            if(functions.isValueNull(status))
            {
                selectMemberDetail.append(" and m.activation=?");
                countquery.append(" and m.activation=?");
            }
            if(functions.isValueNull(country))
            {
                selectMemberDetail.append(" and m.country=?");
                countquery.append(" and m.country=?");
            }
            if ("creation_date".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                selectMemberDetail.append(" and m.dtstamp >= ?");
                countquery.append(" and m.dtstamp >= ?");
            }
            else
            {
                selectMemberDetail.append(" and m.activation_date >= ? ");
                countquery.append(" and m.activation_date >= ?");

            }
            if ("creation_date".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                selectMemberDetail.append(" and m.dtstamp <= ?");
                countquery.append(" and m.dtstamp <= ?");
            }
            else
            {
                selectMemberDetail.append(" and m.activation_date <= ? AND m.activation_date !='null'");
                countquery.append(" and m.activation_date <= ? AND m.activation_date !='null'");
            }
            selectMemberDetail.append(" order by m.memberid DESC");
            selectMemberDetail.append(" LIMIT ? , ?");

            log.debug("selectMemberDetail query:::::"+selectMemberDetail.toString());
            log.debug("count query:::::"+countquery.toString());

            pstmt = conn.prepareStatement(selectMemberDetail.toString());
            pstmt1 = conn.prepareStatement(countquery.toString());

            if(functions.isValueNull(pid))
            {
                pstmt.setString(counter, pid);
                pstmt1.setString(counter, pid);
                counter++;
            }else if (Roles.contains("superpartner"))
            {
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
                pstmt.setString(counter, partnerid);
                pstmt1.setString(counter, partnerid);
                counter++;
            }
            if (functions.isValueNull(memberId))
            {
                pstmt.setString(counter, memberId);
                pstmt1.setString(counter, memberId);
                counter++;
            }
            if (functions.isValueNull(userName))
            {
            pstmt.setString(counter, userName);
            pstmt1.setString(counter, userName);
            counter++;
            }
            if (functions.isValueNull(companyName))
            {
                pstmt.setString(counter, companyName);
                pstmt1.setString(counter, companyName);
                counter++;
            }
            if (functions.isValueNull(contactName))
            {
                pstmt.setString(counter, contactName);
                pstmt1.setString(counter, contactName);
                counter++;
            }
            if (functions.isValueNull(emailId))
            {
                pstmt.setString(counter, emailId);
                pstmt1.setString(counter, emailId);
                counter++;
            }
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            if (functions.isValueNull(country))
            {
                pstmt.setString(counter, country);
                pstmt1.setString(counter, country);
                counter++;
            }
            if ("creation_date".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                pstmt.setString(counter, fdtstamp);
                pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            if ("creation_date".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                pstmt.setString(counter, tdtstamp);
                pstmt1.setString(counter, tdtstamp);
                counter++;
            }
            else
            {
                pstmt.setString(counter, tdtstamp);
                pstmt1.setString(counter, tdtstamp);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("selectMemberDetail:::::" + pstmt);
           // memberHash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(selectMemberDetail.toString(), conn));
            memberHash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            memberHash.put("totalrecords", "" + totalrecords);
            memberHash.put("records", "0");

            if (totalrecords > 0)
            {
                memberHash.put("records", "" + (memberHash.size() - 2));
            }

            req.setAttribute("transdetails", memberHash);
            //redirect to jsp page with records

        }
        catch (SystemError systemError)
        {
            log.error("SystemError while listing member details",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("Sql exception while listing member details", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e){
            log.error("error", e);
            Functions.ShowMessage("Error", "error");

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/memberlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.COMPANY_NAME);
        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);
        inputFieldsListOptional.add(InputFields.USERNAME);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.PARTNERLIST);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }
}

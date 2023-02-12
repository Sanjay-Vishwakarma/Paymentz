package net.partner;

import com.directi.pg.*;
import com.manager.dao.WhiteListDAO;
import com.payment.MultiplePartnerUtill;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/14/15
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinRouting extends HttpServlet
{
    private static Logger logger = new Logger(BinRouting.class.getName());
    private Functions functions = new Functions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        StringBuilder sErrorMessage = new StringBuilder();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String BinRouting_memberid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_memberid_errormsg")) ? rb1.getString("BinRouting_memberid_errormsg") : "Invalid memberID or memberID should not be empty";
        String BinRouting_mapped_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_mapped_errormsg")) ? rb1.getString("BinRouting_mapped_errormsg") : "Partner ID/Member ID NOT mapped.";
        String BinRouting_notmapped_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_notmapped_errormsg")) ? rb1.getString("BinRouting_notmapped_errormsg") : "Partner ID/Member ID NOT mapped.";
        String BinRouting_partnerid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_partnerid_errormsg")) ? rb1.getString("BinRouting_partnerid_errormsg") : "Invalid PartnerId";
        String BinRouting_accountid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_accountid_errormsg")) ? rb1.getString("BinRouting_accountid_errormsg") : "Invalid accountID or accountID should not be empty";
        String BinRouting_start_bin_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_start_bin_errormsg")) ? rb1.getString("BinRouting_start_bin_errormsg") : "Invalid Start Bin or Start Bin should not be empty";
        String BinRouting_end_bin_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_end_bin_errormsg")) ? rb1.getString("BinRouting_end_bin_errormsg") : "Invalid End Bin or End Bin should not be empty";
        String BinRouting_memberid1_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_memberid1_errormsg")) ? rb1.getString("BinRouting_memberid1_errormsg") : "MemberID/AccountID NOT mapped.";
        String BinRouting_greater_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_greater_errormsg")) ? rb1.getString("BinRouting_greater_errormsg") : "Start Bin greater than End Bin.";
        String BinRouting_already_errormsg = StringUtils.isNotEmpty(rb1.getString("BinRouting_already_errormsg")) ? rb1.getString("BinRouting_already_errormsg") : "Your Bin Already Whitelisted";

        MultiplePartnerUtill multiplePartnerUtill=new MultiplePartnerUtill();
        List<WhitelistingDetailsVO> listDAO=new ArrayList<>();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        int start = 0;
        int end = 0;
        String errormsg="";
        String memberid ="";
        String accountid ="";
        String success = "";
        String EOL="<br>";
        int pageno=1;
        int records=15;
        String pid="";

        String startBin=req.getParameter("startBin");
        String endBin=req.getParameter("endBin");
        accountid=req.getParameter("accountid");
        String action=req.getParameter("block");
        memberid = req.getParameter("memberid");
        pid = req.getParameter("pid");

        errormsg = errormsg + optinalValidateParameters(req);
        if(functions.isValueNull(errormsg)){
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/binRouting.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            boolean isValid = true;
            if ("block".equalsIgnoreCase(action))
                isValid = false;

            if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 50, false))
            {
                logger.debug("Invalid MemberId");
                sErrorMessage.append(BinRouting_memberid_errormsg + EOL);
            }else{
                if (functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerMemberMapped(memberid, req.getParameter("pid")))
                {
                    sErrorMessage.append(BinRouting_mapped_errormsg + EOL);
                }
                else if (!functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerSuperpartnerMembersMapped(memberid, req.getParameter("partnerid")))
                {
                    sErrorMessage.append(BinRouting_notmapped_errormsg + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                logger.debug("Invalid partnerid");
                sErrorMessage.append(BinRouting_partnerid_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountid, "Numbers", 50, isValid))
            {
                logger.debug("Invalid AccountId");
                sErrorMessage.append(BinRouting_accountid_errormsg + EOL);
            }

            if (!ESAPI.validator().isValidInput("startBin", startBin, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append(BinRouting_start_bin_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("endBin", endBin, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append(BinRouting_end_bin_errormsg + EOL);
            }

            if(functions.isValueNull(memberid)&& functions.isValueNull(accountid)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountid);
                if(valid==false){
                    sErrorMessage.append(BinRouting_memberid1_errormsg);
                }
            }
            if (functions.isValueNull(startBin) && functions.isValueNull(endBin))
            {
                if(startBin.compareTo(endBin)>0){
                    sErrorMessage.append(BinRouting_greater_errormsg);
                }
            }

            if(sErrorMessage.length()>0){
                req.setAttribute("error",sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/binRouting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if ("block".equalsIgnoreCase(action))
            {
                listDAO = whiteListDAO.getWhiteListBin(startBin, endBin, accountid, memberid);
                if(listDAO.size()<=0)
                {
                    if (functions.isValueNull(startBin) && functions.isValueNull(endBin))
                    {
                        boolean flag = multiplePartnerUtill.addBinDetails(startBin, endBin, accountid, memberid,actionExecutorId,actionExecutorName);
                        if (flag)
                        {
                            success = "Bin Range:-" + " " + startBin + "-" + endBin + " Uploaded Successful for Member ID:" + memberid;
                        }
                    }
                }
                else
                {
                    errormsg =BinRouting_already_errormsg ;
                }
            }
        }
        catch (Exception e){
            logger.error("Exception---"+e);
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;

        HashMap hash = null;
        StringBuilder query = new StringBuilder("select id,startBin,endBin,accountid,memberid,actionExecutorId,actionExecutorName from whitelist_bins where id>0 ");
        StringBuilder countquery = new StringBuilder("select count(*) from whitelist_bins where id>0 ");

        if (functions.isValueNull(memberid))
        {
            query.append("and  memberid = ?");
            countquery.append("and  memberid = ?");
        }
        if (functions.isValueNull(accountid))
        {
            query.append(" and accountid = ?");
            countquery.append(" and accountid = ?");
        }
        if (functions.isValueNull(startBin) && functions.isValueNull(endBin))
        {
            query.append(" and startBin <= ? and endBin >= ?");
            countquery.append(" and startBin <= ? and endBin >= ?");
        }
       /* if (functions.isValueNull(actionExecutorId))
        {
            query.append(" and actionExecutorId in ("+actionExecutorId+")");

        }
        if (functions.isValueNull(actionExecutorName))
        {
            query.append(" and actionExecutorName in ("+actionExecutorName+")");

        }
*/
        query.append(" order by id desc LIMIT ? , ?");
        Connection con = null;
        PreparedStatement p=null,p1=null;
        ResultSet rs=null;
        int counter = 1;

        try
        {
            con = Database.getRDBConnection();
            p=con.prepareStatement(query.toString());
            p1=con.prepareStatement(countquery.toString());

            if (partner.isValueNull(memberid))
            {
                p.setString(counter,memberid);
                p1.setString(counter,memberid);
                counter++;
            }
            if (partner.isValueNull(accountid))
            {
                p.setString(counter,accountid);
                p1.setString(counter,accountid);
                counter++;
            }
            if (partner.isValueNull(startBin))
            {
                p.setString(counter,startBin);
                p1.setString(counter,startBin);
                counter++;
            }
            if (partner.isValueNull(endBin))
            {
                p.setString(counter,endBin);
                p1.setString(counter,endBin);
                counter++;
            }
            p.setInt(counter,start);
            counter++;
            p.setInt(counter, end);
            hash = Database.getHashMapFromResultSet(p.executeQuery());
            rs = p1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
            else
            {
                hash = new HashMap();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
            req.setAttribute("memberdetails", hash);

        }
        catch (SystemError se)
        {
            logger.error("System Error::::",se);
            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal System Error");
        }
        finally
        {
            try
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(p);
                Database.closePreparedStatement(p1);
                Database.closeConnection(con);
            }
            catch (Exception e)
            {
                logger.error("SQL Exception::::",e);
                sErrorMessage.append("Internal System Error");
            }
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("error",sErrorMessage.toString());
        req.setAttribute("success",success);
        req.setAttribute("error",errormsg);
        req.setAttribute("memberdetails",hash);
        RequestDispatcher rd = req.getRequestDispatcher("/binRouting.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private String optinalValidateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
package net.partner;

import com.directi.pg.*;
import com.manager.WhiteListManager;
import com.manager.dao.WhiteListDAO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/5/2020.
 */
public class BinCountryRouting extends HttpServlet
{
    private static Logger logger = new Logger(BinRouting.class.getName());
    private Functions functions = new Functions();
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        StringBuilder sErrorMessage = new StringBuilder();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String BinCountryRouting_memberid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_memberid_errormsg")) ? rb1.getString("BinCountryRouting_memberid_errormsg") : "Invalid memberID or memberID should not be empty";
        String BinCountryRouting_partnerid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_partnerid_errormsg")) ? rb1.getString("BinCountryRouting_partnerid_errormsg") : "Partner ID/Member ID NOT mapped.";
        String BinCountryRouting_partner_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_partner_errormsg")) ? rb1.getString("BinCountryRouting_partner_errormsg") : "Invalid PartnerId";
        String BinCountryRouting_accountid_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_accountid_errormsg")) ? rb1.getString("BinCountryRouting_accountid_errormsg") : "Invalid accountID or accountID should not be empty";
        String BinCountryRouting_notmapped_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_notmapped_errormsg")) ? rb1.getString("BinCountryRouting_notmapped_errormsg") : "MemberID/AccountID NOT mapped.";
        String BinCountryRouting_country_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_country_errormsg")) ? rb1.getString("BinCountryRouting_country_errormsg") : "Please select at least one country";
        String BinCountryRouting_uploaded_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_uploaded_errormsg")) ? rb1.getString("BinCountryRouting_uploaded_errormsg") : "Country Uploaded Successful for Member ID:";
        String BinCountryRouting_whitelisted_errormsg = StringUtils.isNotEmpty(rb1.getString("BinCountryRouting_whitelisted_errormsg")) ? rb1.getString("BinCountryRouting_whitelisted_errormsg") : "Your Country Already Whitelisted";

        WhiteListDAO whiteListDAO=new WhiteListDAO();
        WhiteListManager whiteListManager=new WhiteListManager();
        List<String>  dbCountryList=new ArrayList<>();
        List<String>  countryList=new ArrayList<>();
        List<String>  reqCountryList=new ArrayList<>();
        HashMap hash = null;
        Connection con = null;
        PreparedStatement p=null,p1=null;
        ResultSet rs=null;
        RequestDispatcher rd = request.getRequestDispatcher("/binCountryRouting.jsp?ctoken="+user.getCSRFToken());

        String partnerid = session.getAttribute("partnerId").toString();
        String country=request.getParameter("country");
        String accountid=request.getParameter("accountid");
        String action=request.getParameter("upload");
        String memberid = request.getParameter("memberid");
        String errormsg="";
        String success = "";
        String EOL="<br>";
        int start = 0;
        int end = 0;
        int pageno=1;
        int records=15;
        String pid = request.getParameter("pid");
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        errormsg = errormsg + optionalValidateParameters(request);
        if(functions.isValueNull(errormsg)){
            request.setAttribute("error",errormsg);
            rd.forward(request, response);
            return;
        }
        try
        {
            boolean isValid = true;
            if ("upload".equalsIgnoreCase(action))
                isValid = false;

            if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 50, false))
            {
                logger.debug("Invalid MemberId");
                sErrorMessage.append(BinCountryRouting_memberid_errormsg + EOL);
            }
            else
            {
                if (functions.isValueNull(request.getParameter("pid")) && !partner.isPartnerMemberMapped(memberid, request.getParameter("pid")))
                {
                    sErrorMessage.append(BinCountryRouting_partnerid_errormsg + EOL);
                }
                else if (!functions.isValueNull(request.getParameter("pid")) && !partner.isPartnerSuperpartnerMembersMapped(memberid, partnerid))
                {
                    sErrorMessage.append(BinCountryRouting_partnerid_errormsg + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                logger.debug("Invalid partnerid");
                sErrorMessage.append(BinCountryRouting_partner_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountid, "Numbers", 50, isValid))
            {
                logger.debug("Invalid AccountId");
                sErrorMessage.append(BinCountryRouting_accountid_errormsg + EOL);
            }
            if(functions.isValueNull(memberid)&& functions.isValueNull(accountid)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountid);
                if(valid==false){
                    sErrorMessage.append(BinCountryRouting_notmapped_errormsg);
                }
            }
            if(!functions.isValueNull(country) && "upload".equalsIgnoreCase(action))
            {
                logger.debug("Please select at least one country");
                sErrorMessage.append(BinCountryRouting_country_errormsg + EOL);
            }
            if(sErrorMessage.length()>0){
                request.setAttribute("error",sErrorMessage.toString());
                rd.forward(request, response);
                return;
            }
            String[] countries=null;
            String country1="";
            if(functions.isValueNull(country) && country.contains(","))
            {
                countries=country.split(",");
                int i=0;
                for (String cty : countries)
                {
                    if(i==0)
                        country1+="'"+cty+"'";
                    else
                        country1+=",'"+cty+"'";
                    i++;
                    reqCountryList.add(cty);
                }
            }else if(functions.isValueNull(country))
            {
                country1 = "'"+country+"'";
                reqCountryList.add(country);
            }
            if ("upload".equalsIgnoreCase(action))
            {

                dbCountryList=whiteListManager.getWhiteListBinCountry(country1, accountid, memberid,actionExecutorId,actionExecutorName);

                for (String cty : reqCountryList)
                {
                    if(!dbCountryList.contains(cty))
                        countryList.add(cty);
                }

                if(!countryList.isEmpty())
                {
                    boolean flag = whiteListManager.insertBinCountryRoutingDetails(memberid, accountid, countryList,actionExecutorId,actionExecutorName);
                    if (flag)
                    {
                        success =BinCountryRouting_uploaded_errormsg  + memberid;
                    }
                }else
                    errormsg =BinCountryRouting_whitelisted_errormsg ;

            }
            pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

            start = (pageno - 1) * records;
            end = records;

            StringBuilder query = new StringBuilder("select id,country,accountid,memberid,actionExecutorId,actionExecutorName from whitelist_bin_country where id>0 ");
            StringBuilder countquery = new StringBuilder("select count(*) from whitelist_bin_country where id>0 ");

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
            if (functions.isValueNull(country))
            {
                query.append(" and country in ("+country1+")");
                countquery.append(" and country in ("+country1+")");
            }
            query.append(" order by id desc LIMIT ? , ?");
            int counter = 1;
                con = Database.getConnection();
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


                p.setInt(counter, start);
                counter++;
                p.setInt(counter, end);
            logger.error("Search Query ---->"+p);
            logger.error("Search Query ---->"+p1);
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

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError --->",systemError);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException--->",e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException --->",e);
        }finally
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
                errormsg="Internal System Error";
            }
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        request.setAttribute("success",success);
        request.setAttribute("error",errormsg);
        request.setAttribute("whitelistCountryDetails",hash);
        rd.forward(request, response);
    }
    private String optionalValidateParameters(HttpServletRequest req)
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

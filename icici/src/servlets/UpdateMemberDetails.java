import com.directi.pg.*;
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
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 4/18/14
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMemberDetails extends HttpServlet
{
    private static Logger log = new Logger(UpdateMemberDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Functions functions = new Functions();
        log.debug("Entering in List UpdateMemberDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String memberId="";
        String action = "";
        String isReadOnly = "view";
        Connection conn = null;
        String companyName=null;
        String personName=null;
        String emailId=null;
        String address=null;
        String city =null;
        String state=null;
        String zip=null;
        String country=null;
        String phoneNo=null;
        String brandName=null;
        String siteName=null;
        String domain=null;
        String errorList ="";
        String mainContactCcMailId=null;
        String mainContactBccMailId=null;
        String mainContactPhone=null;

        String support_persons = null;
        String support_emails = null;
        String support_cCmailid=null;
        String support_bccMailid=null;
        String support_phone=null;

        String cbContactName=null;
        String cbContactMailId=null;
        String cbContactCcMailId=null;
        String cbContactBccMailId=null;
        String cbContactPhone=null;
        String refundContactName=null;
        String refundContactMailId=null;
        String refundContactCcMailId=null;
        String refundContactBccMailId=null;
        String refundContactPhone=null;
        String salesContactName=null;
        String salesContactMailId=null;
        String salesContactCcMailId=null;
        String salesContactBccMailId=null;
        String salesContactPhone=null;
        String fraudContactName=null;
        String fraudContactMailId=null;
        String fraudContactCcMailId=null;
        String fraudContactBccMailId=null;
        String fraudContactPhone=null;
        String technicalContactName=null;
        String technicalContactMailId=null;
        String technicalContactCcMailId=null;
        String technicalContactBccMailId=null;
        String technicalContactphone=null;
        String billingContactName=null;
        String billingContactMailId=null;
        String billingContactCcMailId=null;
        String billingContactBccMailId=null;
        String billingContactPhone=null;

        try
        {
            memberId = ESAPI.validator().getValidInput("memberid",req.getParameter("memberid"),"Numbers",10,true);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,true);
        }
        catch (ValidationException e)
        {
            log.error("Validation Exception",e);
        }
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try
        {
            Hashtable memberHash = null;
            conn= Database.getConnection();
            String qry="SELECT m.*,mc.* FROM members AS m left JOIN merchant_configuration AS mc on m.memberid=mc.memberid where m.memberid=?";
            preparedStatement1= conn.prepareStatement(qry);
            preparedStatement1.setString(1,memberId);
            memberHash = Database.getHashFromResultSet(preparedStatement1.executeQuery());

            if(action!=null && !action.equals(""))
            {
                if(action.equalsIgnoreCase("view"))
                {
                    req.setAttribute("isreadonly","view");
                    req.setAttribute("memberDetail",memberHash);
                    RequestDispatcher rd = req.getRequestDispatcher("/memberdetaillist.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if(action.equalsIgnoreCase("modify"))
                {
                    req.setAttribute("memberDetail",memberHash);
                    req.setAttribute("isreadonly","modify");
                    if(req.getParameter("update")!=null)
                    {
                        req.setAttribute("memberDetail",null);
                        errorList =errorList+ validateUpdateMerchantParameters(req);
                        if(errorList!=null && !errorList.equals(""))
                        {
                            req.setAttribute("error",errorList);
                            RequestDispatcher rd = req.getRequestDispatcher("/memberdetaillist.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(req, res);
                            return;
                        }
                        else
                        {
                            memberId=req.getParameter("memberid");
                            companyName=req.getParameter("company_name");
                            personName=req.getParameter("contact_persons");
                            emailId=req.getParameter("contact_emails");
                            address=req.getParameter("address");
                            city=req.getParameter("city");
                            state=req.getParameter("state");
                            zip=req.getParameter("zip");
                            if(functions.isValueNull(req.getParameter("country")))
                            {
                                String [] splitValue = req.getParameter("country").split("\\|");
                                country = splitValue[0];
                            }
                            phoneNo = req.getParameter("phonecc")+"-"+req.getParameter("telno");
                            brandName=req.getParameter("brandname");
                            siteName=req.getParameter("sitename");
                            domain=req.getParameter("domain");
                            mainContactCcMailId=req.getParameter("maincontact_ccmailid");
                            mainContactBccMailId=req.getParameter("maincontact_bccmailid");
                            mainContactPhone=req.getParameter("maincontact_phone");
                            cbContactName=req.getParameter("cbcontact_name");
                            cbContactMailId=req.getParameter("cbcontact_mailid");
                            cbContactCcMailId=req.getParameter("cbcontact_ccmailid");
                            cbContactBccMailId=req.getParameter("cbcontact_bccmailid");
                            cbContactPhone=req.getParameter("cbcontact_phone");
                            refundContactName=req.getParameter("refundcontact_name");
                            refundContactMailId=req.getParameter("refundcontact_mailid");
                            refundContactCcMailId=req.getParameter("refundcontact_ccmailid");
                            refundContactBccMailId=req.getParameter("refundcontact_bccmailid");
                            refundContactPhone=req.getParameter("refundcontact_phone");
                            salesContactName=req.getParameter("salescontact_name");
                            salesContactMailId=req.getParameter("salescontact_mailid");
                            salesContactCcMailId=req.getParameter("salescontact_ccmailid");
                            salesContactBccMailId=req.getParameter("salescontact_bccmailid");
                            salesContactPhone=req.getParameter("salescontact_phone");
                            fraudContactName=req.getParameter("fraudcontact_name");
                            fraudContactMailId=req.getParameter("fraudcontact_mailid");
                            fraudContactCcMailId=req.getParameter("fraudcontact_ccmailid");
                            fraudContactBccMailId=req.getParameter("fraudcontact_bccmailid");
                            fraudContactPhone=req.getParameter("fraudcontact_phone");
                            technicalContactName=req.getParameter("technicalcontact_name");
                            technicalContactMailId=req.getParameter("technicalcontact_mailid");
                            technicalContactCcMailId=req.getParameter("technicalcontact_ccmailid");
                            technicalContactBccMailId=req.getParameter("technicalcontact_bccmailid");
                            technicalContactphone=req.getParameter("technicalcontact_phone");
                            billingContactName=req.getParameter("billingcontact_name");
                            billingContactMailId=req.getParameter("billingcontact_mailid");
                            billingContactCcMailId=req.getParameter("billingcontact_ccmailid");
                            billingContactBccMailId=req.getParameter("billingcontact_bccmailid");
                            billingContactPhone=req.getParameter("billingcontact_phone");
                            support_persons=req.getParameter("support_persons");
                            support_emails=req.getParameter("support_emails");
                            support_cCmailid=req.getParameter("support_ccmailid");
                            support_bccMailid=req.getParameter("support_bccmailid");
                            support_phone=req.getParameter("support_phone");

                        }

                        String updateMemberSetails="UPDATE members SET company_name=?,contact_persons=?,contact_emails=?,address=?,city=?,state=?,zip=?,country=?,telno=?,brandname=?,sitename=?,domain=? WHERE memberid=?";
                        preparedStatement=conn.prepareStatement(updateMemberSetails);
                        preparedStatement.setString(1,companyName);
                        preparedStatement.setString(2,personName);
                        preparedStatement.setString(3,emailId);
                        preparedStatement.setString(4,address);
                        preparedStatement.setString(5,city);
                        preparedStatement.setString(6,state);
                        preparedStatement.setString(7,zip);
                        preparedStatement.setString(8,country);
                        preparedStatement.setString(9,phoneNo);
                        preparedStatement.setString(10,brandName);
                        preparedStatement.setString(11,siteName);
                        preparedStatement.setString(12,domain);
                        preparedStatement.setString(13,memberId);
                        int i=preparedStatement.executeUpdate();
                        //System.out.println("prepared statement"+i);
                        if(i==1)
                        {
                            String query="select memberid from merchant_configuration where memberid=?";
                            preparedStatement=conn.prepareStatement(query);
                            preparedStatement.setString(1,memberId);
                            rs=preparedStatement.executeQuery();
                            if(!rs.next())
                            {
                                String query1="insert into merchant_configuration(id,memberid)values(NULL,?)";
                                preparedStatement=conn.prepareStatement(query1);
                                preparedStatement.setString(1,memberId);
                                int k2=preparedStatement.executeUpdate();
                            }

                            String query2="update merchant_configuration set maincontact_ccmailid=?,maincontact_phone=?,cbcontact_name=?,cbcontact_mailid=?,cbcontact_ccmailid=?,cbcontact_phone=?,refundcontact_name=?,refundcontact_mailid=?,refundcontact_ccmailid=?,refundcontact_phone=?,salescontact_name=?,\n" +
                                    "salescontact_mailid=?,salescontact_ccmailid=?,salescontact_phone=?,fraudcontact_name=?,fraudcontact_mailid=?,fraudcontact_ccmailid=?,fraudcontact_phone=?,technicalcontact_name=?,technicalcontact_mailid=?,technicalcontact_ccmailid=?,technicalcontact_phone=?,\n" +
                                    "billingcontact_name=?,billingcontact_mailid=?,billingcontact_ccmailid=?,billingcontact_phone=?,support_persons=?,support_emails=?,support_ccmailid=?,support_phone=?,maincontact_bccmailid=?,support_bccmailid=?,cbcontact_bccmailid=?,refundcontact_bccmailid=?," +
                                    "salescontact_bccmailid=?,fraudcontact_bccmailid=?,technicalcontact_bccmailid=?,billingcontact_bccmailid=? where memberid=?";

                            preparedStatement=conn.prepareStatement(query2);
                            preparedStatement.setString(1,mainContactCcMailId);
                            preparedStatement.setString(2,mainContactPhone);
                            preparedStatement.setString(3,cbContactName);
                            preparedStatement.setString(4,cbContactMailId);
                            preparedStatement.setString(5,cbContactCcMailId);
                            preparedStatement.setString(6,cbContactPhone);
                            preparedStatement.setString(7,refundContactName);
                            preparedStatement.setString(8,refundContactMailId);
                            preparedStatement.setString(9,refundContactCcMailId);
                            preparedStatement.setString(10,refundContactPhone);
                            preparedStatement.setString(11,salesContactName);
                            preparedStatement.setString(12,salesContactMailId);
                            preparedStatement.setString(13,salesContactCcMailId);
                            preparedStatement.setString(14,salesContactPhone);
                            preparedStatement.setString(15,fraudContactName);
                            preparedStatement.setString(16,fraudContactMailId);
                            preparedStatement.setString(17,fraudContactCcMailId);
                            preparedStatement.setString(18,fraudContactPhone);
                            preparedStatement.setString(19,technicalContactName);
                            preparedStatement.setString(20,technicalContactMailId);
                            preparedStatement.setString(21,technicalContactCcMailId);
                            preparedStatement.setString(22,technicalContactphone);
                            preparedStatement.setString(23,billingContactName);
                            preparedStatement.setString(24,billingContactMailId);
                            preparedStatement.setString(25,billingContactCcMailId);
                            preparedStatement.setString(26,billingContactPhone);
                            preparedStatement.setString(27,support_persons);
                            preparedStatement.setString(28,support_emails);
                            preparedStatement.setString(29,support_cCmailid);
                            preparedStatement.setString(30,support_phone);

                            //maincontact_bccmailid,support_bccmailid,cbcontact_bccmailid,refundcontact_bccmailid,salescontact_bccmailid,fraudcontact_bccmailid,technicalcontact_bccmailid,billingcontact_bccmailid,

                            preparedStatement.setString(31,mainContactBccMailId);
                            preparedStatement.setString(32,support_bccMailid);
                            preparedStatement.setString(33,cbContactBccMailId);
                            preparedStatement.setString(34,refundContactBccMailId);
                            preparedStatement.setString(35,salesContactBccMailId);
                            preparedStatement.setString(36,fraudContactBccMailId);
                            preparedStatement.setString(37,technicalContactBccMailId);
                            preparedStatement.setString(38,billingContactBccMailId);

                            preparedStatement.setString(39,memberId);
                            int k2=preparedStatement.executeUpdate();
                            req.setAttribute("error","Merchant profile details has been updated successfully");
                        }
                        else
                        {
                            req.setAttribute("error","Merchant profile updation failed");
                        }
                    }
                    RequestDispatcher rd = req.getRequestDispatcher("/memberdetaillist.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
            }
            else
            {
                req.setAttribute("memberid",memberId);
                req.setAttribute("error","Action is getting empty");
                RequestDispatcher rd = req.getRequestDispatcher("/memberdetaillist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

        }
        catch (SystemError systemError)
        {
            log.error("SystemError ",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closePreparedStatement(preparedStatement1);
            Database.closeConnection(conn);
        }
    }

    public String validateUpdateMerchantParameters(HttpServletRequest request)
    {
        String errormsg="";
        String EOL="<BR>";
        String country="";
        Functions functions=new Functions();
        if (!ESAPI.validator().isValidInput("company_name ", request.getParameter("company_name"), "companyName", 100, false))
        {
            errormsg = errormsg + "Invalid company name." + EOL;
        }
        if(functions.isValueNull(request.getParameter("country")))
        {
            String [] splitValue = request.getParameter("country").split("\\|");
            country = splitValue[0];
        }
        if (!ESAPI.validator().isValidInput("country",country, "StrictString", 50, false))
        {
            errormsg = errormsg + "Invalid country." + EOL;
        }
        if (!ESAPI.validator().isValidInput("address", request.getParameter("address"), "Address", 200, true) || functions.hasHTMLTags(request.getParameter("address")))
        {
            errormsg = errormsg + "Invalid address." + EOL;
        }
        if (!ESAPI.validator().isValidInput("zip", request.getParameter("zip"), "Zip", 100, true))
        {
            errormsg = errormsg + "Invalid postal code." + EOL;
        }
        if (!ESAPI.validator().isValidInput("telno", request.getParameter("telno"), "SignupPhone", 20, false))
        {
            errormsg = errormsg + "Invalid support number." + EOL;
        }
        if (!ESAPI.validator().isValidInput("sitename", request.getParameter("sitename"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("sitename")))
        {
            errormsg = errormsg + "Invalid sitename. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("domain", request.getParameter("domain"), "DomainURL", 5000, true))
        {
            errormsg = errormsg + "Invalid domain." + EOL;
        }
        if (!ESAPI.validator().isValidInput("contact_persons", request.getParameter("contact_persons"), "contactName", 100, false))
        {
            errormsg = errormsg + "Invalid main contact name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("contact_emails", request.getParameter("contact_emails"), "Email", 100, false))
        {
            errormsg = errormsg + "Invalid main contact mailid." + EOL;
        }
        if (!ESAPI.validator().isValidInput("city", request.getParameter("city"), "Address", 30, true) || functions.hasHTMLTags(request.getParameter("city")))
        {
            errormsg = errormsg + "Invalid city." + EOL;
        }
        if (!ESAPI.validator().isValidInput("city", request.getParameter("state"), "Address", 30, true) || functions.hasHTMLTags(request.getParameter("state")))
        {
            errormsg = errormsg + "Invalid state." + EOL;
        }
        if (functions.hasHTMLTags(request.getParameter("phonecc")))
        {
            errormsg = errormsg + "Invalid Phone no CC. " + EOL;
        }
        if (!ESAPI.validator().isValidInput("brandname", request.getParameter("brandname"), "companyName", 30, true))
        {
            errormsg = errormsg + "Invalid brandname." + EOL;
        }
        if(functions.isValueNull(request.getParameter("maincontact_ccmailid"))){
            String columnName = request.getParameter("maincontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("maincontact_ccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid contact cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("maincontact_bccmailid"))){
            String columnName = request.getParameter("maincontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("maincontact_bccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid contact bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if (!ESAPI.validator().isValidInput("maincontact_phone", request.getParameter("maincontact_phone"), "SignupPhone", 20, true))
        {
            errormsg = errormsg + "Invalid main contact phone." + EOL;
        }

        if (!ESAPI.validator().isValidInput("support_persons", request.getParameter("support_persons"), "contactName", 250, false))
        {
            errormsg = errormsg + "Invalid Customer Support Contact name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("support_emails", request.getParameter("support_emails"), "Email", 250, false))
        {
            errormsg = errormsg + "Invalid Customer support mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("support_ccmailid"))){
            String columnName = request.getParameter("support_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("support_ccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid Customer support cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("support_bccmailid"))){
            String columnName = request.getParameter("support_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("support_bccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid Customer support bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if (!ESAPI.validator().isValidInput("support_phone", request.getParameter("support_phone"), "SignupPhone", 20, true))
        {
            errormsg = errormsg + "Invalid Customer support phone number." + EOL;
        }

        if (!ESAPI.validator().isValidInput("salescontact_name", request.getParameter("salescontact_name"), "contactName", 100, true))
        {
            errormsg = errormsg + "Invalid sales contact name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("salescontact_mailid", request.getParameter("salescontact_mailid"), "Email", 250, true))
        {
            errormsg = errormsg + "Invalid sales contact mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("salescontact_ccmailid"))){
            String columnName = request.getParameter("salescontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("salescontact_ccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid sales cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("salescontact_bccmailid"))){
            String columnName = request.getParameter("salescontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("salescontact_bccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid sales bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if (!ESAPI.validator().isValidInput("salescontact_phone", request.getParameter("salescontact_phone"), "SignupPhone", 20, true))
        {
            errormsg = errormsg + "Invalid sales contact phone." + EOL;
        }
        if (!ESAPI.validator().isValidInput("billingcontact_name", request.getParameter("billingcontact_name"), "contactName", 100, true))
        {
            errormsg = errormsg + "Invalid billing contact name." + EOL;
        }
        if (!ESAPI.validator().isValidInput("billingcontact_mailid", request.getParameter("billingcontact_mailid"), "Email", 250, true))
        {
            errormsg = errormsg + "Invalid billing mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("billingcontact_ccmailid"))){
            String columnName = request.getParameter("billingcontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("billingcontact_ccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid billing cc mailid "+tempColumn+" ." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("billingcontact_bccmailid"))){
            String columnName = request.getParameter("billingcontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if (!ESAPI.validator().isValidInput("billingcontact_bccmailid", tempColumn, "Email", 250, true))
                {
                    errormsg = errormsg + "Invalid billing bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if (!ESAPI.validator().isValidInput("billingcontact_phone", request.getParameter("billingcontact_phone"), "SignupPhone", 20, true))
        {
            errormsg = errormsg + "Invalid billing phone." + EOL;
        }
        if(!ESAPI.validator().isValidInput("fraudcontact_name", request.getParameter("fraudcontact_name"),"contactName", 100,true))
        {
            errormsg=errormsg + "Invalid fraud contact_name." + EOL;
        }
        if(!ESAPI.validator().isValidInput("fraudcontact_mailid",request.getParameter("fraudcontact_mailid"),"Email", 250, true))
        {
            errormsg=errormsg + "Invalid fraud contact mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("fraudcontact_ccmailid"))){
            String columnName = request.getParameter("fraudcontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("fraudcontact_ccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid fraud contact cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("fraudcontact_bccmailid"))){
            String columnName = request.getParameter("fraudcontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("fraudcontact_bccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid fraud contact bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(!ESAPI.validator().isValidInput("fraudcontact_phone",request.getParameter("fraudcontact_phone"),"SignupPhone", 20, true))
        {
            errormsg=errormsg + "Invalid fraud contact phone." + EOL;
        }
        if(!ESAPI.validator().isValidInput("refundcontact_name", request.getParameter("refundcontact_name"),"contactName", 100,true))
        {
            errormsg=errormsg + "Invalid refund contact name." + EOL;
        }
        if(!ESAPI.validator().isValidInput("refundcontact_mailid",request.getParameter("refundcontact_mailid"),"Email", 250, true))
        {
            errormsg=errormsg + "Invalid refund contact mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("refundcontact_ccmailid"))){
            String columnName = request.getParameter("refundcontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("refundcontact_ccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid refund contact cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("refundcontact_bccmailid"))){
            String columnName = request.getParameter("refundcontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("refundcontact_bccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid refund bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if(!ESAPI.validator().isValidInput("refundcontact_phone",request.getParameter("refundcontact_phone"),"SignupPhone", 20, true))
        {
            errormsg=errormsg + "Invalid refund contact phone." + EOL;
        }
        if(!ESAPI.validator().isValidInput("cbcontact_name", request.getParameter("cbcontact_name"),"contactName", 100,true))
        {
            errormsg=errormsg + "Invalid chargeback contact person." + EOL;
        }
        if(!ESAPI.validator().isValidInput("cbcontact_mailid",request.getParameter("cbcontact_mailid"),"Email", 250, true))
        {
            errormsg=errormsg + "Invalid chargeback mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("cbcontact_ccmailid"))){
            String columnName = request.getParameter("cbcontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("cbcontact_ccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid chargeback cc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if(functions.isValueNull(request.getParameter("cbcontact_bccmailid"))){
            String columnName = request.getParameter("cbcontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("cbcontact_bccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid chargeback bcc mailid" +tempColumn+"." + EOL;
                }
            }
        }

        if(!ESAPI.validator().isValidInput("cbcontact_phone",request.getParameter("cbcontact_phone"),"SignupPhone", 20, true))
        {
            errormsg=errormsg + "Invalid chargeback contact mailid." + EOL;
        }
        if(!ESAPI.validator().isValidInput("technicalcontact_name", request.getParameter("technicalcontact_name"),"contactName", 100,true))
        {
            errormsg=errormsg + "Invalid technical contact name." + EOL;
        }
        if(!ESAPI.validator().isValidInput("technicalcontact_mailid",request.getParameter("technicalcontact_mailid"),"Email", 250, true))
        {
            errormsg=errormsg + "Invalid technical contact mailid." + EOL;
        }
        if(functions.isValueNull(request.getParameter("technicalcontact_ccmailid"))){
            String columnName = request.getParameter("technicalcontact_ccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("technicalcontact_ccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid technical  contact cc mailid "+tempColumn+"." + EOL;
                }
            }
        }
        if(functions.isValueNull(request.getParameter("technicalcontact_bccmailid"))){
            String columnName = request.getParameter("technicalcontact_bccmailid");
            String[] columnNameArray = columnName.split(";");

            for (int i = 0; i < columnNameArray.length; i++)
            {
                String tempColumn = "";
                tempColumn = columnNameArray[i];
                if(!ESAPI.validator().isValidInput("technicalcontact_bccmailid",tempColumn,"Email", 250, true))
                {
                    errormsg=errormsg + "Invalid technical  contact bcc mailid "+tempColumn+"." + EOL;
                }
            }
        }

        if(!ESAPI.validator().isValidInput("technicalcontact_phone",request.getParameter("technicalcontact_phone"),"SignupPhone", 20, true))
        {
            errormsg=errormsg + "Invalid technical contact phone." + EOL;
        }
        return errormsg;
    }
}

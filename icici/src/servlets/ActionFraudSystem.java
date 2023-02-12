import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/12/14
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionFraudSystem extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystem.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in ActionFraudSystem");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        try
        {
            validateOptionalParameter(req);
            if(functions.hasHTMLTags(req.getParameter("weburl"))){
               errormsg = "Invalid Website Url" + EOL;
             }
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionMemberFraudSystem.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String action=req.getParameter("action");
        String mappingid=req.getParameter("mappingid");
        try
        {
            conn= Database.getConnection();
            String qry="";
            HashMap rsDetails=null;
            if(action.equalsIgnoreCase("modify"))
            {
                rsDetails=new HashMap();
                qry="select fsid,fsname,contact_person,contact_email,url,offline,online,api from fraudsystem_master where fsid=?";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                rs=pstmt.executeQuery();
                if(rs.next())
                {
                    rsDetails.put("fsid",rs.getString("fsid"));
                    rsDetails.put("fsname",rs.getString("fsname"));
                    rsDetails.put("contactperson",rs.getString("contact_person"));
                    rsDetails.put("contactemail",rs.getString("contact_email"));
                    rsDetails.put("weburl",rs.getString("url"));
                    rsDetails.put("offline",rs.getString("offline"));
                    rsDetails.put("online",rs.getString("online"));
                    rsDetails.put("api",rs.getString("api"));
                }
                rsDetails.put("mappingid",mappingid);
                req.setAttribute("chargedetails",rsDetails);
            }
            else if(action.equalsIgnoreCase("update"))
            {
                logger.debug("Request Is Comming For Update Record");
                /*String fsName=req.getParameter("fsname");*/
                String contactPerson=req.getParameter("contactperson");
                String contactEmail=req.getParameter("contactemail");
                String webUrl=req.getParameter("weburl");
                String isOffline=req.getParameter("offline");
                String isOnline=req.getParameter("online");
                String isAPICall=req.getParameter("apicall");

                Hashtable hashtable=new Hashtable();
                /*hashtable.put("fsname",fsName);*/
                hashtable.put("contactperson",contactPerson);
                hashtable.put("contactemail",contactEmail);
                hashtable.put("weburl",webUrl);

                List<String> errorList=validateMandatoryParameter(hashtable);
                if(!(errorList.size()>0))
                {
                    qry="update fraudsystem_master set contact_person=?,contact_email=?,url=?,offline=?,online=?,api=? where fsid=?";
                    pstmt=conn.prepareStatement(qry);
                    pstmt.setString(1,contactPerson);
                    pstmt.setString(2,contactEmail);
                    pstmt.setString(3,webUrl);
                    pstmt.setString(4,isOffline);
                    pstmt.setString(5,isOnline);
                    pstmt.setString(6,isAPICall);
                    pstmt.setString(7,mappingid);
                    int k=pstmt.executeUpdate();
                    if(k>0)
                    {
                        errormsg ="<center><font class=\"textb\"><b> Record Update Successfully</b></font></center>";
                    }
                    else
                    {
                        errormsg="<center><font class=\"textb\" face=\"arial\"><b> Error while updating record</b></font></center>";
                    }
                }
                else
                {
                    req.setAttribute("errorList",errorList);
                }

            }
            else
            {
                errormsg = "<center><font class=\"text\" face=\"arial\"><b> Action is not defined</b></font></center>";

            }

            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionFraudSystem.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {
            logger.error("system error occurred ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException occurred", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MAPPINGID);
        inputFieldsListMandatory.add(InputFields.ACTION);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
    private List<String> validateMandatoryParameter(Hashtable<String, String> otherDetail)
    {
        logger.error("Enter Into validateMandatoryParameter");
        InputValidator inputValidator = new InputValidator();
        List<String> error = new ArrayList<String>();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        /*hashTable.put(InputFields.FSNAME,otherDetail.get("fsname"));*/
        hashTable.put(InputFields.NAME,otherDetail.get("contactperson"));
        hashTable.put(InputFields.EMAIL,otherDetail.get("contactemail"));
        hashTable.put(InputFields.DOMAIN,otherDetail.get("weburl"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);
        logger.debug("End Of  Reading All Mandatory Parameter");
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.add(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }


        return error;


    }
}

import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.fraud.FraudSystemService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 9/09/14
 * Time: 7:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddNewFraudSystem extends HttpServlet
{
    private static Logger logger = new Logger(AddNewFraudSystem.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering in  AddNewFraudSystem");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String result = "";
        String fsName =request.getParameter("fsname");
        String contactPerson =request.getParameter("contactperson");
        String contactEmail =request.getParameter("contactemail");
        String webUrl =request.getParameter("weburl");
        String isOffline =request.getParameter("offline");
        String isOnline =request.getParameter("online");
        String isAPICall =request.getParameter("apicall");

        Hashtable hashtable=new Hashtable();
        hashtable.put("fsname",fsName);
        hashtable.put("contactperson",contactPerson);
        hashtable.put("contactemail",contactEmail);
        hashtable.put("weburl",webUrl);
        Functions functions = new Functions();
        List<String> errorList=validateMandatoryParameter(hashtable);
        if(functions.hasHTMLTags(webUrl)){
            errorList.add("Invalid Website Url" + "<BR>");
        }
        if(!(errorList.size()>0))
        {
            result=addNewFraudSystem(fsName,contactPerson,contactEmail,webUrl,isOffline,isOnline,isAPICall);
            request.setAttribute("message",result);
            RequestDispatcher rd = request.getRequestDispatcher("/addNewFraudSystem.jsp?message=" + result + "&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        else
        {
            request.setAttribute("errorList",errorList);
            RequestDispatcher rd = request.getRequestDispatcher("/addNewFraudSystem.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
    }
    public String addNewFraudSystem(String fsName,String contactPerson,String contactEmail,String webUrl,String isOffline,String isOnline,String isAPICall)
    {
        Connection conn = null;
        String result = "";
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn= Database.getConnection();
            String selectQuery = "SELECT fsname FROM fraudsystem_master WHERE fsname =? ";
            PreparedStatement selectPstmt = conn.prepareStatement(selectQuery);
            selectPstmt.setString(1,fsName);
            rs = selectPstmt.executeQuery();
            if(rs.next())
            {
                result = "This Fraud-System  Exists In System, Please specify some other name.";
            }
            else
            {
                String query = "insert into fraudsystem_master(fsname,contact_person,contact_email,url,offline,online,api)values (?,?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,fsName);
                pstmt.setString(2,contactPerson);
                pstmt.setString(3,contactEmail);
                pstmt.setString(4,webUrl);
                pstmt.setString(5,isOffline);
                pstmt.setString(6,isOnline);
                pstmt.setString(7,isAPICall);
                int k=pstmt.executeUpdate();
                if(k>0)
                {
                    result="New Fraud System Added Successfully.";
                    FraudSystemService.loadFraudSystems();
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exceptoin : ",e);
            result = e.getMessage();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }
    private List<String> validateMandatoryParameter(Hashtable<String, String> otherDetail)
    {
        logger.error("Enter Into validateMandatoryParameter");
        InputValidator inputValidator = new InputValidator();
        List<String> error = new ArrayList<String>();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.FSNAME,otherDetail.get("fsname"));
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


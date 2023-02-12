import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
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
public class ManageMemberFraudSystem extends HttpServlet
{
    private static Logger logger = new Logger(ManageMemberFraudSystem.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering In  ManageMemberFraudSystem");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        User user =(User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String result = "";
        String fsId =request.getParameter("fsid");
        String memberId =request.getParameter("memberid");
        String isActive =request.getParameter("isActive");
        String isOnlineFraudCheck =request.getParameter("isonlinefraudcheck");
        String isFraudAPIUser =request.getParameter("isfraudapiuser");
        String isTest =request.getParameter("isTest");

        Hashtable hashtable=new Hashtable();
        hashtable.put("fsid",fsId);
        hashtable.put("memberid",memberId);

        List errorList=validateMandatoryParameter(hashtable);
        if(!(errorList.size()>0))
        {
            result=addNewMemberFraudSystemMapping(fsId,memberId,isActive,isOnlineFraudCheck,isFraudAPIUser,isTest);
            request.setAttribute("message",result);
            RequestDispatcher rd = request.getRequestDispatcher("/manageMemberFraudSystem.jsp?message=" + result + "&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        else
        {
            request.setAttribute("errorList",errorList);
            RequestDispatcher rd = request.getRequestDispatcher("/manageMemberFraudSystem.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
    }
    public String addNewMemberFraudSystemMapping(String fsId,String memberId,String isActive,String isOnlineFraudCheck,String isFraudAPIUser,String isTest)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String result = "";
        try
        {
            conn= Database.getConnection();
            String selectQuery = "SELECT id FROM member_fraudsystem_mapping WHERE memberid=? ";
            preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, memberId);
            rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                result = "This Member Is Mapped With Other Fraud System.You May Change By Updating Fraud System";
            }
            else
            {
                String query = "insert into member_fraudsystem_mapping(fsid,memberid,isActive,isonlinefraudcheck,isapiuser,isTest)values (?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,fsId);
                pstmt.setString(2,memberId);
                pstmt.setString(3,isActive);
                pstmt.setString(4,isOnlineFraudCheck);
                pstmt.setString(5,isFraudAPIUser);
                pstmt.setString(6,isTest);
                int k=pstmt.executeUpdate();
                if(k>0)
                {
                    result="New Member Fraud System Mapping Added Successfully.";
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
            Database.closePreparedStatement(preparedStatement);
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

        hashTable.put(InputFields.FSID,otherDetail.get("fsid"));
        hashTable.put(InputFields.MEMBERID,otherDetail.get("memberid"));
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


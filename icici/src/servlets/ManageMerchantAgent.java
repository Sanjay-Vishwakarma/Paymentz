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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;



/**
 * Created by IntelliJ IDEA.
 * User: sandip1
 * Date: 6/30/15
 * Time: 4:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ManageMerchantAgent extends HttpServlet
{
    private static Logger logger = new Logger(ManageMerchantAgent.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =(User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String result = "";
        String agentId =request.getParameter("agentid");
        String memberId =request.getParameter("memberid");

        Hashtable hashtable=new Hashtable();
        hashtable.put("agentid",agentId);
        hashtable.put("memberid",memberId);

        List errorList=validateMandatoryParameter(hashtable);
        if(!(errorList.size()>0))
        {
            result=addNewMerchantAgentMapping(agentId, memberId);
            request.setAttribute("message",result);
            RequestDispatcher rd = request.getRequestDispatcher("/manageMerchantAgent.jsp?message=" + result + "&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        else
        {
            request.setAttribute("errorList",errorList);
            RequestDispatcher rd = request.getRequestDispatcher("/manageMerchantAgent.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
    }
    public String addNewMerchantAgentMapping(String agentId,String memberId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        String result = "";
        try
        {
            conn= Database.getConnection();
            StringBuffer selectQuery =new StringBuffer("SELECT mappingid FROM merchant_agent_mapping WHERE memberid=? and agentid=? ");
            preparedStatement = conn.prepareStatement(selectQuery.toString());
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, agentId);
            rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                result = "Mapping Already Available";
            }
            else
            {
                StringBuffer query =new StringBuffer("insert into merchant_agent_mapping(memberid,agentid,creationtime)values (?,?,unix_timestamp(now()))");
                pstmt = conn.prepareStatement(query.toString());
                pstmt.setString(1,memberId);
                pstmt.setString(2,agentId);
                int k=pstmt.executeUpdate();
                if(k>0)
                {
                    result="New Merchant Agent Mapping Added Successfully.";
                }
                else
                {
                    result="New Merchant Agent Mapping Added Failed.";
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
        InputValidator inputValidator = new InputValidator();
        List<String> error = new ArrayList<String>();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();
        hashTable.put(InputFields.AGENTID,otherDetail.get("agentid"));
        hashTable.put(InputFields.MEMBERID,otherDetail.get("memberid"));
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);
        if(!errorList.isEmpty()){
            for(InputFields inputFields :hashTable.keySet()){
                if(errorList.getError(inputFields.toString())!=null){
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.add(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}


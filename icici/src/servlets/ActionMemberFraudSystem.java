import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/10/14
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionMemberFraudSystem extends HttpServlet
{
    private static Logger logger = new Logger(ActionMemberFraudSystem.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in ActionMemberFraudSystem");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("ctoken===" + req.getParameter("ctoken"));

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs2=null;
        String errormsg="";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(req);
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
                qry="select id,fsid,memberid,isonlinefraudcheck,isapiuser,isActive,isTest from member_fraudsystem_mapping where id=?";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                rs2=pstmt.executeQuery();
                if(rs2.next())
                {
                    rsDetails.put("memberid",rs2.getString("memberid"));
                    rsDetails.put("id",rs2.getString("id"));
                    rsDetails.put("fsid",rs2.getString("fsid"));
                    rsDetails.put("isonlinefraudcheck",rs2.getString("isonlinefraudcheck"));
                    rsDetails.put("isapiuser",rs2.getString("isapiuser"));
                    rsDetails.put("isActive",rs2.getString("isActive"));
                    rsDetails.put("isTest",rs2.getString("isTest"));
                }
                rsDetails.put("mappingid",mappingid);
                req.setAttribute("chargedetails",rsDetails);
            }
            else if(action.equalsIgnoreCase("update"))
            {
                logger.debug("Request Is Comming For Update Record");
                String fsid=req.getParameter("fsid");
                String memberId=req.getParameter("memberid");
                String isOnlineFraudCheck=req.getParameter("isonlinefraudcheck");
                String IsAPIUSer=req.getParameter("isapiuser");
                String isActive=req.getParameter("isActive");
                String isTest=req.getParameter("isTest");
                //TODO:Do Validation For The fsid,memberid,isActive
                qry="update member_fraudsystem_mapping set fsid=?,memberid=?,isonlinefraudcheck=?,isapiuser=?,isActive=?,isTest=? where id=?";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,fsid);
                pstmt.setString(2,memberId);
                pstmt.setString(3,isOnlineFraudCheck);
                pstmt.setString(4,IsAPIUSer);
                pstmt.setString(5,isActive);
                pstmt.setString(6,isTest);
                pstmt.setString(7,mappingid);
                int k=pstmt.executeUpdate();
                if(k>0)
                {
                    errormsg ="<center><font class=\"textb\" ><b> Record Update Successfully</b></font></center>";

                }
                else
                {
                    errormsg="<center><font class=\"textb\" ><b> Error while updating record</b></font></center>";
                }
            }
            else
            {
                errormsg = "<center><font class=\"textb\" ><b> Action is not defined</b></font></center>";

            }
            req.setAttribute("action",action);
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionMemberFraudSystem.jsp?ctoken="+user.getCSRFToken());
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
        finally {
            Database.closeResultSet(rs2);
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
}

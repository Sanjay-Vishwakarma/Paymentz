
import com.directi.pg.*;
import com.manager.AuthenticationManager;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by NIKET on 11-06-2016.
 */
public class BlockedUserList extends HttpServlet
{
    private Logger logger = new Logger(BlockedUserList.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in BlockedEmailList");
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ServletContext application = getServletContext();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;
        String errorMessage = "";
        String EOL = "<BR>";
        Functions functions = new Functions();
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index
        String login=null;

        //Manager Instance
        //AuthenticationManager authenticationManager = new AuthenticationManager();
        login=request.getParameter("login");
        String error = "";
        error = error + validateOptionalParameters(request);
        if(error.length() > 0)
        {
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/unblockuser.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

        //VO instance
       /* PaginationVO paginationVO = new PaginationVO();
        List<DefaultUser> defaultUsers=null;*/
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select accountid,login,roles from user where unblocked='locked'");
            StringBuffer countquery = new StringBuffer("select count(*) from user where unblocked='locked'");

            if (functions.isValueNull(login))
            {
                query.append(" and login='" + ESAPI.encoder().encodeForSQL(me,login)+ "'");
                countquery.append(" and login='" + ESAPI.encoder().encodeForSQL(me,login)+ "'");
            }
            query.append(" order by roles desc LIMIT " + start + "," + end);
            logger.debug("Query:-"+query);
            logger.debug("CountQuery:-"+countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            request.setAttribute("transdetails", hash);
            logger.debug("forward to jsp"+hash);

           /* paginationVO.setInputs("");
            paginationVO.setPageNo(Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno", request.getParameter("SPageno"), "Numbers", 5, true), 1));
            paginationVO.setPage(BlockedUserList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE"))));

            defaultUsers=authenticationManager.getAllLockedUser(paginationVO);

            //setting response
            request.setAttribute("PaginationVO",paginationVO);
            request.setAttribute("DefaultUsers",defaultUsers);*/
            /*RequestDispatcher rdSuccess= request.getRequestDispatcher("/unblockuser.jsp?Success=YES&ctoken="+user.getCSRFToken());
            rdSuccess.forward(request,response);
            return;*/
        }
       /* catch (PZDBViolationException e)
        {
            PZExceptionHandler.handleDBCVEException(e,"","Fetching user from the table");
            //request.setAttribute("errorMessage","Internal Issue while fetching user from the list");
            Functions.ShowMessage("Error", "Internal Issue while fetching user from the list");
        }
        catch (ValidationException e)
        {
            logger.error("ValidationException----",e);
            Functions.ShowMessage("Error", "Internal Issue while fetching user from the list");
        }*/
        catch (SQLException e)
        {
            logger.error("SQLException----",e);
            Functions.ShowMessage("Error", "Internal Issue while fetching user from the list");
        }
        catch (SystemError s)
        {
            logger.error("SystemError----",s);
            Functions.ShowMessage("Error", "Internal Issue while fetching user from the list");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        /*request.setAttribute("PaginationVO",paginationVO);
        request.setAttribute("DefaultUsers",defaultUsers);*/
        RequestDispatcher rdError=request.getRequestDispatcher("/unblockuser.jsp?ctoken="+user.getCSRFToken());
        rdError.forward(request,response);
        return;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
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

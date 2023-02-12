import com.directi.pg.*;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import java.util.*;

public class SearchCSE extends HttpServlet
{
   private static Logger logger=new Logger("logger1");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session= Functions.getNewSession(request);
        User user =(User)session.getAttribute("ESAPIUserSessionKey");

        if(!com.directi.pg.Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        Hashtable detail = null;
        String error = "";
        int records=10;
        int pageno=1;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno", request.getParameter("SPageno"), "Numbers", 5, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records", request.getParameter("SRecords"), "Numbers", 5, true), 10);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 10;

        }

        try{
            List<InputFields> inputFieldsListOptional=new ArrayList();
            inputFieldsListOptional.add(InputFields.CSEEid);
            inputFieldsListOptional.add(InputFields.CSEname);
            error= error + validateOptionalParameters(request);
            if(error.isEmpty())
            {
                String csId=request.getParameter("Eid");
                String csName=request.getParameter("Ename");

                if(csId.equals(""))
                {
                    csId=null;
                }
                if(csName.equals(""))
                {
                    csName=null;
                }
                try
                {
                    detail= CustomerSupport.CSElist(csId, csName, pageno, records);

                }
                catch (SystemError systemError)
                {
//                    systemError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                      logger.error(" SYSTEM ERROR::",systemError);
                }
                request.setAttribute("CSElist",detail);

                RequestDispatcher rd =request.getRequestDispatcher("/cseManager.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);

            }
            else
            {
                request.setAttribute("error",error);
                RequestDispatcher rd=request.getRequestDispatcher("/cseManager.jsp?MES=X&ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }
        }
        catch (Exception e)
        {
            logger.error(" Exception in main class::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }


    }
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.INVOICENO);

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

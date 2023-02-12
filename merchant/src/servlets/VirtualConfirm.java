import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.checkers.PaymentChecker;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Nov 27, 2012
 * Time: 10:16:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualConfirm  extends HttpServlet
{

   private static Logger log = new Logger(VirtualConfirm.class.getName());
   Connection con = null;
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
       doPost(request, response);
   }

       public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
       {   log.debug("Enter in VirtualConfirm");
           HttpSession session = req.getSession();
           Merchants merchants = new Merchants();

           if (!merchants.isLoggedIn(session))
           {   log.debug("member is logout ");
               res.sendRedirect("/merchant/virtualLogout.jsp");
               return;
           }
           User user =  (User)session.getAttribute("ESAPIUserSessionKey");
           PaymentChecker paymentChecker = new PaymentChecker();
           //variabe list

           String error = "";
           String EOL = "<BR>";
           boolean flag = true;
           StringBuffer sbuf = new StringBuffer();
           Hashtable otherdetails = new Hashtable();
           Hashtable hiddenvariables = new Hashtable();
           PrintWriter pWriter = res.getWriter();

           String name=null;
           String checksum=null;
           String totype=(String) session.getAttribute("company");
           ServletContext sc = getServletContext();


           String redirecturl=req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/merchant/virtualRedirect.jsp";

           //validate all requested variables
           error = error + validateMandatoryParameters(req);
           if(!error.equals(""))
               flag =false;


           error = error+ validateOptionalParameters(req);
           if(!error.equals(""))
               flag =false;

           String autoSelectTerminal = req.getParameter("autoSelectTerminal");
           error = error+ validateConditionalParameters(req,autoSelectTerminal);
           if(!error.equals(""))
               flag =false;

           String mid = req.getParameter("mid");
           String oid = req.getParameter("oid");
           String total = req.getParameter("total");
           String b_address = req.getParameter("b_address");
           String b_city = req.getParameter("b_city");
           String b_zipcode = req.getParameter("b_zipcode");
           String b_state = req.getParameter("b_state");
           String country = req.getParameter("country");
           String countrycode = req.getParameter("countrycode");
           String phone1 = req.getParameter("phone1");
           String phone2 = req.getParameter("phone2");
           String email = req.getParameter("email");
           String paymenttype = req.getParameter("paymenttype");
           String cardtype = req.getParameter("cardtype");
           String key = req.getParameter("bitkey");
           String checksumAlgorithm = req.getParameter("checksumAlgorithm");
           String terminalid = req.getParameter("terminalid");
           String ipAddr = Functions.getIpAddress(req);
           String currency = req.getParameter("currency");

            if(autoSelectTerminal.equals("Y") && paymentChecker.isCardTypeAndPayModeValid(paymenttype,cardtype,mid)==false)
            {
                error = error + "Invalid Payment Details"+EOL;
                flag = false;
            }
           else if (autoSelectTerminal.equals("N") && paymentChecker.isTerminalIdValid(terminalid,mid)==false)
            {
                error = error + "Invalid Terminal type"+EOL;
                flag = false;
            }
           //calcute checksum
           try
           {
               checksum = Functions.generateChecksumV4(mid,totype,total,oid,redirecturl,key,checksumAlgorithm);
           }
           catch (NoSuchAlgorithmException e)
           {
              log.error("EXCEPTION",e);  //To change body of catch statement use File | Settings | File Templates.
           }
           log.debug(error);
           //set hidden veriables

           if (flag == true)
           {

               hiddenvariables.put("mid",mid);
               hiddenvariables.put("oid",oid);
               hiddenvariables.put("total",total);
               //hiddenvariables.put("name",name);
               hiddenvariables.put("b_address",b_address);
               hiddenvariables.put("b_city",b_city);
               hiddenvariables.put("b_zipcode",b_zipcode);
               hiddenvariables.put("b_state",b_state);
               hiddenvariables.put("country",country);
               hiddenvariables.put("countrycode",countrycode);
               hiddenvariables.put("phone1",phone1);
               hiddenvariables.put("phone2",phone2);
               hiddenvariables.put("email",email);
               hiddenvariables.put("currency",currency);
               if(paymenttype!=null)
               {
               hiddenvariables.put("paymenttype",paymenttype);
               hiddenvariables.put("paymode",GatewayAccountService.getPaymentTypes(paymenttype));
               }
               else
               {
               hiddenvariables.put("paymenttype","");
               hiddenvariables.put("paymode","");
               }
               if(cardtype!=null)
               {
                   hiddenvariables.put("cardtype",GatewayAccountService.getCardType(cardtype));
                   hiddenvariables.put("cardtypeid",cardtype);
               }
               else
               {
                   hiddenvariables.put("cardtype","");
                   hiddenvariables.put("cardtypeid","");
               }
               if(terminalid!=null)
               {
                   hiddenvariables.put("terminalid",terminalid);
               }
               else
               {
                   hiddenvariables.put("terminalid","");
               }
               if(ipAddr != null)
               {
                   hiddenvariables.put("ipaddr",ipAddr);
               }
               else
               {
                   hiddenvariables.put("ipaddr","");
               }
               hiddenvariables.put("autoSelectTerminal",autoSelectTerminal);
               hiddenvariables.put("checksum",checksum);
               hiddenvariables.put("key",key);
               hiddenvariables.put("totype",totype);
               hiddenvariables.put("redirecturl",redirecturl);

               req.setAttribute("hiddenvariables",hiddenvariables);

               RequestDispatcher rd = req.getRequestDispatcher("/virtualConfirm.jsp?ctoken="+user.getCSRFToken());
               rd.forward(req, res);
           }
           else
           {

               req.setAttribute("error",error);
               RequestDispatcher rd = req.getRequestDispatcher("/servlet/VirtualTerminal?ctoken="+user.getCSRFToken());
               rd.forward(req, res);

           }

       }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.MID);
        inputFieldsListMandatory.add(InputFields.OID);
        inputFieldsListMandatory.add(InputFields.TOTAL);
        inputFieldsListMandatory.add(InputFields.BITKEY);
        inputFieldsListMandatory.add(InputFields.CHECKSUMALGORITHM);


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    private String validateConditionalParameters(HttpServletRequest req, String autoSelectTerminal)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();
        if(autoSelectTerminal.equals("Y"))
        {
            inputFieldsListMandatory.add(InputFields.CARDTYPE);
            inputFieldsListMandatory.add(InputFields.PAYMENTYPE);
        }
        else
        {
            inputFieldsListMandatory.add(InputFields.TERMINALID);
        }


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.B_ADDRESS);
        inputFieldsListOptional.add(InputFields.B_CITY);
        inputFieldsListOptional.add(InputFields.B_ZIPCODE);
        inputFieldsListOptional.add(InputFields.B_STATE);
        inputFieldsListOptional.add(InputFields.TMPL_COUNTRY);
        inputFieldsListOptional.add(InputFields.TMPL_TELNO);
        inputFieldsListOptional.add(InputFields.COUNTRYCODE);
        inputFieldsListOptional.add(InputFields.PHONE1);
        inputFieldsListOptional.add(InputFields.PHONE2);
        inputFieldsListOptional.add(InputFields.TMPL_EMAILADDR);
        inputFieldsListOptional.add(InputFields.IPADDR);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }


   }

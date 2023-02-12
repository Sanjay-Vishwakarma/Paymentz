package payment;

import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.InvoiceVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.invoice.validators.InvoiceValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jinesh on 5/18/2015.
 */
public class PayInvoiceController extends HttpServlet
{
    private static Logger log=new Logger(PayInvoiceController.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(PayInvoiceController.class.getName());


    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException, IOException
    {
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        Functions functions = new Functions();

        String error = "";
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        InvoiceValidator invoiceValidator = new InvoiceValidator();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        String newCtoken = "";

        String invoiceno = req.getParameter("invoiceno");
        String ctokengot = req.getParameter("ctoken");
        String terminalId = "";

        if (functions.isValueNull(req.getParameter("terminalid")))
        {
            terminalId = req.getParameter("terminalid");
        }
        try
        {


            newCtoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


            InvoiceVO invoiceVO = new InvoiceVO();
            //Hashtable details=(invoiceEntry.getInvoiceDetailsForController(invoiceno));

            invoiceEntry.getInvoiceDetailsForController(invoiceVO,invoiceno);
            invoiceVO.setTerminalid(terminalId);
            PartnerDetailsVO partnerDetailsVO = invoiceVO.getPartnerDetailsVO();
            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            long current_time=0,generated_time = 0;
            //float diffTime = 0.1f;
            String checksum = "";

            merchantDetailsVO.setLogoName(invoiceVO.getPartnerDetailsVO().getLogoName());
            merchantDetailsVO.setPartnerId(invoiceVO.getMerchantDetailsVO().getPartnerId());

            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            error = validateInvoiceParameters(req);
            log.error("validation error from payinvoicecontroller---"+error);
            if(!error.equals("") && error!=null)
            {
                req.setAttribute("error", error);
                req.setAttribute("standardvo",commonValidatorVO);
                RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+newCtoken);
                rd.forward(req,res);
                return;
            }

            current_time = dateFormat.parse(String.valueOf(dateFormat.format(date))).getTime();
            generated_time = dateFormat.parse(invoiceVO.getTimestamp()).getTime();

            log.debug("cur time---" + dateFormat.format(date));

            float diffTime = current_time-generated_time;

            invoiceValidator.validateInvoice(invoiceVO,ctokengot);
            log.debug("gen in pic1---" + invoiceVO.getExpirationPeriod());

            String toid = invoiceVO.getMemberid();
            String totype = partnerDetailsVO.getPartnerName();
            String amount = invoiceVO.getAmount();
            String description = invoiceVO.getDescription();
            String redirectUrl = invoiceVO.getRedirecturl();
            String clkey = invoiceVO.getMerchantDetailsVO().getKey();
            String algo = invoiceVO.getMerchantDetailsVO().getChecksumAlgo();

            checksum = Functions.generateChecksumV4(toid,totype,amount,description,redirectUrl,clkey,algo);

            log.debug("===checksum=="+checksum);
            transactionLogger.debug("===checksum=="+checksum);
            String form = "";
            if (functions.isValueNull(terminalId) || !terminalId.equals(""))
            {
                invoiceEntry.getPaymodeCardfromTerminal(terminalId, invoiceVO);
                 form = submitAutoForm(invoiceVO,checksum,newCtoken);
            }
            else
            {
                form = submitAutoFormWithoutTerminal(invoiceVO, checksum, newCtoken);
            }

            pWriter.print(form);
            return;
        }
        catch (PZConstraintViolationException pce)
        {
            log.error("PZConstraintViolationException in PayInvoiceController_old---", pce);
            error = pce.getPzConstraint().getMessage();
            log.debug("error meaasge from payinvoicecontroller---" + error);
            req.setAttribute("error", error);
            req.setAttribute("standardvo",commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+newCtoken);
            rd.forward(req,res);
            return;
        }
        catch (ParseException pe)
        {
            log.debug("date parse exception---"+pe);
        }
        catch (NoSuchAlgorithmException ns)
        {
            log.debug("NoSuch Algorithm Exception---"+ns);
        }
    }

    public String submitAutoForm(InvoiceVO invoiceVO,String checksum,String ctoken)
    {
        String form = "<form name=\"pic\" method=\"POST\" action=\"/transaction/PayProcessController?ctoken="+ctoken+"\">" +
                "<input type=hidden name=\"toid\" value=\""+invoiceVO.getMemberid()+"\"/>" +
                "<input type=hidden name=\"totype\" value=\""+invoiceVO.getPartnerDetailsVO().getPartnerName()+"\"/>" +
                "<input type=hidden name=\"amount\" value=\""+invoiceVO.getAmount()+"\"/>" +
                "<input type=hidden name=\"orderdescription\" value=\""+invoiceVO.getOrderDescription()+"\"/>" +
                "<input type=hidden name=\"description\" value=\""+invoiceVO.getDescription()+"\"/>" +
                "<input type=hidden name=\"redirecturl\" value=\""+invoiceVO.getRedirecturl()+"\"/>" +
                "<input type=hidden name=\"ipaddr\" value=\""+invoiceVO.getMerchantIpAddress()+"\"/>" +
                "<input type=hidden name=\"checksum\" value=\""+checksum+"\"/>" +
                "<input type=hidden name=\"TMPL_CURRENCY\" value=\""+invoiceVO.getCurrency()+"\"/>" +
                "<input type=hidden name=\"TMPL_AMOUNT\" value=\""+invoiceVO.getAmount()+"\"/>" +
                "<input type=hidden name=\"TMPL_COUNTRY\" value=\""+invoiceVO.getCountry()+"\"/>" +
                "<input type=hidden name=\"TMPL_city\" value=\""+invoiceVO.getCity()+"\"/>" +
                "<input type=hidden name=\"TMPL_state\" value=\""+invoiceVO.getState()+"\"/>" +
                "<input type=hidden name=\"TMPL_zip\" value=\""+invoiceVO.getZip()+"\"/>" +
                "<input type=hidden name=\"TMPL_street\" value=\""+invoiceVO.getStreet()+"\"/>" +
                "<input type=hidden name=\"TMPL_telno\" value=\""+invoiceVO.getTelno()+"\"/>" +
                "<input type=hidden name=\"TMPL_telnocc\" value=\""+invoiceVO.getTelCc()+"\"/>" +
                "<input type=hidden name=\"TMPL_emailaddr\" value=\""+invoiceVO.getEmail()+"\"/>" +
                "<input type=hidden name=\"paymenttype\" value=\""+invoiceVO.getMerchantTerminalVo().getPaymodeName()+"\"/>" +
                "<input type=hidden name=\"cardtype\" value=\""+invoiceVO.getMerchantTerminalVo().getCardTypeName()+"\"/>" +
                "<input type=hidden name=\"terminalid\" value=\""+invoiceVO.getTerminalid()+"\"/>" +
                "<input type=hidden name=\"invoicenumber\" value=\""+invoiceVO.getInvoiceno()+"\"/>" +
                "</form>" +
                "<script language=\"javascript\">document.pic.submit();</script>";
        return form;
    }

    public String submitAutoFormWithoutTerminal(InvoiceVO invoiceVO,String checksum,String ctoken)
    {
        String form = "<form name=\"pic\" method=\"POST\" action=\"/transaction/PayProcessController?ctoken="+ctoken+"\">" +
                "<input type=hidden name=\"toid\" value=\""+invoiceVO.getMemberid()+"\"/>" +
                "<input type=hidden name=\"totype\" value=\""+invoiceVO.getPartnerDetailsVO().getPartnerName()+"\"/>" +
                "<input type=hidden name=\"amount\" value=\""+invoiceVO.getAmount()+"\"/>" +
                "<input type=hidden name=\"orderdescription\" value=\""+invoiceVO.getOrderDescription()+"\"/>" +
                "<input type=hidden name=\"description\" value=\""+invoiceVO.getDescription()+"\"/>" +
                "<input type=hidden name=\"redirecturl\" value=\""+invoiceVO.getRedirecturl()+"\"/>" +
                "<input type=hidden name=\"ipaddr\" value=\""+invoiceVO.getMerchantIpAddress()+"\"/>" +
                "<input type=hidden name=\"checksum\" value=\""+checksum+"\"/>" +
                "<input type=hidden name=\"TMPL_CURRENCY\" value=\""+invoiceVO.getCurrency()+"\"/>" +
                "<input type=hidden name=\"TMPL_AMOUNT\" value=\""+invoiceVO.getAmount()+"\"/>" +
                "<input type=hidden name=\"TMPL_COUNTRY\" value=\""+invoiceVO.getCountry()+"\"/>" +
                "<input type=hidden name=\"TMPL_city\" value=\""+invoiceVO.getCity()+"\"/>" +
                "<input type=hidden name=\"TMPL_state\" value=\""+invoiceVO.getState()+"\"/>" +
                "<input type=hidden name=\"TMPL_zip\" value=\""+invoiceVO.getZip()+"\"/>" +
                "<input type=hidden name=\"TMPL_street\" value=\""+invoiceVO.getStreet()+"\"/>" +
                "<input type=hidden name=\"TMPL_telno\" value=\""+invoiceVO.getTelno()+"\"/>" +
                "<input type=hidden name=\"TMPL_telnocc\" value=\""+invoiceVO.getTelCc()+"\"/>" +
                "<input type=hidden name=\"TMPL_emailaddr\" value=\""+invoiceVO.getEmail()+"\"/>" +
                "<input type=hidden name=\"invoicenumber\" value=\""+invoiceVO.getInvoiceno()+"\"/>" +
                "</form>" +
                "<script language=\"javascript\">document.pic.submit();</script>";
        return form;
    }


    private String validateInvoiceParameters(HttpServletRequest request)
    {
        String error = "";
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.INVOICENO);
        //inputMandatoryParameter.add(InputFields.TERMINALID);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);

        for(InputFields inputFields :inputMandatoryParameter)
        {
            if(validationErrorList.getError(inputFields.toString())!=null)
            {
                log.debug(validationErrorList.getError(inputFields.toString()).getLogMessage());
                error = error+validationErrorList.getError(inputFields.toString()).getMessage();
            }
        }
        return error;
    }
}

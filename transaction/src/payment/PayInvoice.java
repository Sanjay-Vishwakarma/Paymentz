package payment;

import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.InvoiceVO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jinesh on 5/18/2015.
 */
public class PayInvoice extends HttpServlet
{
    private static Logger log=new Logger(PayInvoice.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(PayInvoice.class.getName());


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
        MerchantDAO merchantDAO=new MerchantDAO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        String newCtoken = "";
        String invoiceUrl = "";

        String invoiceno = req.getParameter("inv");
        String ctokengot = req.getParameter("ct");
        String terminalId = "";
        if (functions.isValueNull(req.getParameter("t")))
        {
            terminalId = req.getParameter("t");
        }
        try
        {
            newCtoken = ESAPI.randomizer().getRandomString(16, DefaultEncoder.CHAR_ALPHANUMERICS);
            error = validateInvoiceParameters(req);
            log.error("validation error from payinvoice---"+error);
            if(!error.equals("") && error!=null)
            {
                req.setAttribute("error", error);
                req.setAttribute("standardvo",commonValidatorVO);
                RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+newCtoken);
                rd.forward(req,res);
                return;
            }

            InvoiceVO invoiceVO = new InvoiceVO();
            invoiceEntry.getInvoiceDetailsForController(invoiceVO, invoiceno);

            invoiceVO.setTerminalid(terminalId);
            PartnerDetailsVO partnerDetailsVO = invoiceVO.getPartnerDetailsVO();
            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            long current_time=0,generated_time = 0;
            //float diffTime = 0.1f;
            String checksum = "";
            merchantDetailsVO=merchantDAO.getMemberDetails(invoiceVO.getMemberid());
            merchantDetailsVO.setLogoName(invoiceVO.getPartnerDetailsVO().getLogoName());
            merchantDetailsVO.setPartnerId(invoiceVO.getMerchantDetailsVO().getPartnerId());
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);


            invoiceValidator.validateInvoice(invoiceVO, ctokengot);
            log.debug("gen in pic1---" + invoiceVO.getExpirationPeriod());

            invoiceEntry.getInvoiceConfigurationDetailsForTransaction(invoiceVO);
            Float Latefee = Float.valueOf(invoiceVO.getLatefee());
            String duedays = invoiceVO.getDuedate();
            //System.out.println("duedays"+duedays);
            Float amount1 = Float.valueOf(invoiceVO.getAmount());
            Float lateamount= 0f;
            String lateamount2="";

            Date currentDate = new Date();//Current Date
            Date invoiceGenDate = dateFormat.parse(invoiceVO.getTimestamp());
            Calendar c = Calendar.getInstance();
            c.setTime(invoiceGenDate);
            c.add(Calendar.DATE, Integer.parseInt(duedays));
            Date dueDate = c.getTime();//Due days added date


            if (invoiceVO.getIslatefee().equals("Y")&&currentDate.after(dueDate))
            {
                //add latine fee to amount
                lateamount = Latefee.floatValue() + amount1.floatValue();
                lateamount2 = String.format("%.2f", lateamount);
            }
            else
            {
                lateamount = (amount1);
                lateamount2 = String.format("%.2f", lateamount);
            }

            String toid = invoiceVO.getMemberid();
            String totype = partnerDetailsVO.getPartnerName();
            String amount = String.valueOf(lateamount2);
            String description = invoiceVO.getDescription();
            String redirectUrl = invoiceVO.getRedirecturl();
            String clkey = invoiceVO.getMerchantDetailsVO().getKey();
            String algo = invoiceVO.getMerchantDetailsVO().getChecksumAlgo();
            invoiceVO.setAmount(amount);
            checksum = Functions.generateChecksumV4(toid,totype,invoiceVO.getAmount(),description,redirectUrl,clkey,algo);

            log.debug("===checksum=="+checksum);
            transactionLogger.debug("===checksum=="+checksum);

            String form = "";
            if("Y".equalsIgnoreCase(partnerDetailsVO.getCheckoutInvoice()))
                invoiceUrl = "https://"+merchantDetailsVO.getHostUrl()+"/transaction/Checkout";
            else
                invoiceUrl = "https://"+merchantDetailsVO.getHostUrl()+"/transaction/PayProcessController";

            if (functions.isValueNull(terminalId) || !terminalId.equals(""))
            {
                invoiceVO.setTerminalid(terminalId);
                invoiceEntry.getPaymodeCardfromTerminal(terminalId, invoiceVO);
                form = submitAutoForm(invoiceUrl,invoiceVO,checksum,newCtoken);
            }
            else
            {
                form = submitAutoFormWithoutTerminal(invoiceUrl,invoiceVO, checksum, newCtoken);
            }

            pWriter.print(form);
            return;
        }
        catch (PZConstraintViolationException pce)
        {
            log.error("PZConstraintViolationException in PayInvoice_old---", pce);
            error = pce.getPzConstraint().getMessage();
            log.debug("error meaasge from payinvoice---" + error);
            req.setAttribute("error", error);
            req.setAttribute("standardvo",commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+newCtoken);
            rd.forward(req,res);
            return;
        }
        catch (ParseException pe)
        {
            log.error("date parse exception---", pe);
        }
        catch (NoSuchAlgorithmException ns)
        {
            log.error("NoSuch Algorithm Exception---", ns);
        }
        catch (PZDBViolationException e)
        {
            log.error("NoSuch Algorithm Exception---", e);
        }
    }

    public String submitAutoForm(String invoiceUrl,InvoiceVO invoiceVO,String checksum,String ctoken)
    {
        log.debug("Invoice URL---"+invoiceUrl);
        String form = "<form name=\"pic\" method=\"POST\" action=\""+invoiceUrl+"?ctoken="+ctoken+"\">" +
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

    public String submitAutoFormWithoutTerminal(String invoiceUrl,InvoiceVO invoiceVO,String checksum,String ctoken)
    {
        log.debug("Invoice URL---"+invoiceUrl);
        String form = "<form name=\"pic\" method=\"POST\" action=\""+invoiceUrl+"?ctoken="+ctoken+"\">" +
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
        inputMandatoryParameter.add(InputFields.INV);
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

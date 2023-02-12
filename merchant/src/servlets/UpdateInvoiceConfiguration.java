import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.DefaultProductList;
import com.invoice.vo.InvoiceVO;
import com.invoice.vo.UnitList;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//import java.io.PrintWriter;

/**
 * Created by Trupti on 5/31/2017.
 */
public class UpdateInvoiceConfiguration extends HttpServlet
{
    private static Logger log = new Logger(UpdateInvoiceConfiguration.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();
        //PrintWriter out = response.getWriter();
        InvoiceVO invoiceVO = new InvoiceVO();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        String error = "";  
        Functions functions = new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String UpdateInvoiceConfiguration_row_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_row_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_row_errormsg"): "Invalid Default productDescription for row";
        String UpdateInvoiceConfiguration_amount_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_amount_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_amount_errormsg"): "Invalid Default productAmount for row";
        String UpdateInvoiceConfiguration_default_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_default_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_default_errormsg"): "Invalid Default tax for row ";
        String UpdateInvoiceConfiguration_intial_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_intial_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_intial_errormsg"): "Invalid intial, accepts only [A-Z] with max length 4";
        String UpdateInvoiceConfiguration_redirect_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_redirect_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_redirect_errormsg"): "Invalid Redirect Url ";
        String UpdateInvoiceConfiguration_expiration_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_expiration_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_expiration_errormsg"): "Invalid expiration period ";
        String UpdateInvoiceConfiguration_state_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_state_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_state_errormsg"): "Invalid state ";
        String UpdateInvoiceConfiguration_duedate_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_duedate_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_duedate_errormsg"): "Invalid duedate";
        String UpdateInvoiceConfiguration_latefree_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_latefree_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_latefree_errormsg"): "Invalid latefee";
        String UpdateInvoiceConfiguration_expiration1_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_expiration1_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_expiration1_errormsg"): "Due Days is Greater than expiration Period";
        String UpdateInvoiceConfiguration_updated_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_updated_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_updated_errormsg"): "Invoice details updated successfully";
        String UpdateInvoiceConfiguration_updation_errormsg = StringUtils.isNotEmpty(rb1.getString("UpdateInvoiceConfiguration_updation_errormsg"))?rb1.getString("UpdateInvoiceConfiguration_updation_errormsg"): "Invoice details updation failed";


        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");

        try
        {
            String memberid = (String) session.getAttribute("merchantid");
            String initial = request.getParameter("initial");
            String terminalid = "";
            String terminal = "";
            if (functions.isValueNull(request.getParameter("terminal")))
            {
                terminal = request.getParameter("terminal");
                String[] t = terminal.split("-");
                terminalid = t[0];
            }
            else
            {
                terminalid = "";
            }
            String redirectUrl = request.getParameter("redirecturl");
            //String currency = request.getParameter("currency");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String country = request.getParameter("countrycode");
            String phonecc = request.getParameter("phonecc");
            String GST = request.getParameter("gst");
            String isSMS = "";
            String paymentterms = "";
            if(functions.isValueNull(request.getParameter("paymentterms")))
            {
                paymentterms = request.getParameter("paymentterms");
            }
            else
            {
                paymentterms = "";
            }
            String isemail = "";
            String isapp = "";
            String isduedate = "";
            String duedate = request.getParameter("duedate");
            String expPeriod = request.getParameter("expirationperiod");
            String EOL = "<BR>";
            boolean flag = true;

            String islatefee = "";
            String latefee = request.getParameter("latefee");
            String unit = request.getParameter("unit");
            String redirectpage = "/invoiceConfiguration.jsp?ctoken=" + user.getCSRFToken();
            String unitCounter = request.getParameter("unitCounter");
            String loaddefaultproductlist = request.getParameter("loadDefaultProdList");

            List<UnitList> listOfUnit = new ArrayList<UnitList>();
            List<String> listOfUnit1 = new ArrayList<String>();

            if(functions.isValueNull(request.getParameter("unitCounter")) && request.getParameter("unitCounter").length() > 0)
            {
                unitCounter = request.getParameter("unitCounter");
                for(int i=1;i<=Integer.parseInt(unitCounter);i++)
                {
                    UnitList unitList = new UnitList();
                    if(request.getParameter("defaultunit"+i)==null)
                    {
                        continue;
                    }
                    if(!ESAPI.validator().isValidInput("defaultunit",request.getParameter("defaultunit"+i),"alphanum",255,false))
                    {
                        log.debug("Invalid Default Unit for "+i);
                        error = error + "Invalid Default Unit for row "+i +EOL;
                        flag = false;
                    }
                    else
                    {
                        unitList.setDefaultunit(request.getParameter("defaultunit" + i));
                    }

                    listOfUnit.add(unitList);
                    listOfUnit1.add(request.getParameter("defaultunit"+i));
                }
            }
           /* else
            {

            }*/

            List<DefaultProductList> listOfProduct = new ArrayList<DefaultProductList>();
            String productCounter = "";
            if(functions.isValueNull(request.getParameter("productCounter")) && request.getParameter("productCounter").length() > 0)
            {
                productCounter = request.getParameter("productCounter");
                for(int i=1;i<=Integer.parseInt(productCounter);i++)
                {
                    DefaultProductList productList = new DefaultProductList();
                    if(request.getParameter("productDescription"+i)==null)
                    {
                        continue;
                    }
                    if(!ESAPI.validator().isValidInput("productDescription",request.getParameter("productDescription"+i),"alphanum",255,false))
                    {
                        log.debug("Invalid Default productDescription for "+i);
                        error = error + UpdateInvoiceConfiguration_row_errormsg+i +EOL;
                        flag = false;
                    }
                    else
                    {
                        productList.setProductDescription(request.getParameter("productDescription" + i));
                    }
                    if(!ESAPI.validator().isValidInput("productAmount",request.getParameter("productAmount"+i),"alphanum",255,true))
                    {
                        log.debug("Invalid Default productAmount for "+i);
                        error = error + UpdateInvoiceConfiguration_amount_errormsg+i +EOL;
                        flag = false;
                    }
                    else
                    {
                        productList.setProductAmount(request.getParameter("productAmount" + i));
                    }
                    productList.setUnit(request.getParameter("productunit" + i));

                    if(!ESAPI.validator().isValidInput("tax",request.getParameter("tax"+i),"Amount",255,true))
                    {
                        log.debug("Invalid Default tax for "+i);
                        error = error + UpdateInvoiceConfiguration_default_errormsg+i +EOL;
                        flag = false;
                    }
                    else
                    {
                        productList.setTax(request.getParameter("tax" + i));
                    }
                    listOfProduct.add(productList);
                }
            }

            if (functions.isValueNull(request.getParameter("isSMS")))
            {
                isSMS = ("Y");
            }
            else
            {
                isSMS = ("N");
            }

            if (functions.isValueNull(request.getParameter("isEmail")))
            {
                isemail = ("Y");
            }
            else
            {
                isemail = ("N");
            }


            if (functions.isValueNull(request.getParameter("isApp")))
            {
                isapp = ("Y");
            }
            else
            {
                isapp = ("N");
            }

            if (functions.isValueNull(request.getParameter("isduedate")))
            {
                isduedate = ("Y");
            }
            else
            {
                isduedate = ("N");
            }
            if (functions.isValueNull(request.getParameter("islatefee")))
            {
                islatefee = ("Y");
            }
            else
            {
                islatefee = ("N");
            }
            if (functions.isValueNull(request.getParameter("loadDefaultProdList")))
            {
                loaddefaultproductlist = ("Y");
            }
            else
            {
                loaddefaultproductlist = ("N");
            }
            invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(memberid);

            String successMsg = null;


            terminalid = ESAPI.validator().getValidInput("terminal", terminalid, "OnlyNumber", 10, true);



            /*if (currency.equals(""))
            {
                error = (error + "please select currency" + EOL);
            }
            else
            {
                currency = ESAPI.validator().getValidInput("currency", request.getParameter("currency"), "StrictString", 4, false);

            }*/
            if (!ESAPI.validator().isValidInput("gst", request.getParameter("gst"), "Amount", 6, true))
            {

                error = error + "Invalid GST " + EOL;
            }
            else
            {
                GST = request.getParameter("gst");
            }

            if (!ESAPI.validator().isValidInput("city", request.getParameter("city"), "StrictString", 30, true)|| functions.hasHTMLTags(request.getParameter("city")))
            {

                error = error + "Invalid city " + EOL;
            }
            else
            {
                city = request.getParameter("city");
            }
            if (!ESAPI.validator().isValidInput("initial", request.getParameter("initial"), "StrictString",4 , true) || functions.hasHTMLTags(request.getParameter("initial")))
            {
                error = error + UpdateInvoiceConfiguration_intial_errormsg + EOL;
            }
            else
            {
                initial = request.getParameter("initial");
            }
            if (!ESAPI.validator().isValidInput("redirecturl", request.getParameter("redirecturl"), "URL", 255, false) || functions.hasHTMLTags(request.getParameter("redirecturl")))
           {
               error = error + UpdateInvoiceConfiguration_redirect_errormsg + EOL;
           }
            redirectUrl= request.getParameter("redirecturl");

            if (!ESAPI.validator().isValidInput("expirationperiod", request.getParameter("expirationperiod"), "OnlyNumber",3, true))
           {
               error = error + UpdateInvoiceConfiguration_expiration_errormsg + EOL;
           }
            expPeriod= request.getParameter("expirationperiod");

            if(!ESAPI.validator().isValidInput("state", request.getParameter("state"), "StrictString", 30, true))
            {
                error = error + UpdateInvoiceConfiguration_state_errormsg+ EOL;
            }
            state= request.getParameter("state");

            if(!ESAPI.validator().isValidInput("paymentterms", request.getParameter("paymentterms"), "Description", 100, true))
            {
                error = error + "Invalid Terms & Conditions"+ EOL;
            }

            if (!ESAPI.validator().isValidInput("duedate",request.getParameter("duedate"),"OnlyNumber",10,true))
            {
                error = error + UpdateInvoiceConfiguration_duedate_errormsg + EOL;
            }
            duedate=request.getParameter("duedate");

            if (!ESAPI.validator().isValidInput("latefee",request.getParameter("latefee"),"Numbers",10,true))
            {

                error = error + UpdateInvoiceConfiguration_latefree_errormsg + EOL;

            }
            latefee=request.getParameter("latefee");

            if(functions.isValueNull(error))
            {
                request.setAttribute("error", error);
                request.setAttribute("unitList",listOfUnit1);
                request.setAttribute("defaultProductList",listOfProduct);
                request.setAttribute("invoicevo", invoiceVO);
                RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
                return;
            }

                if (!country.equalsIgnoreCase("--"))
                {
                    country = ESAPI.validator().getValidInput("country", request.getParameter("countrycode"), "CountryCode", 3, true);
                }
                else
                {
                    country = "";
                }


            if (functions.isValueNull(phonecc))
            {

                if (!phonecc.equalsIgnoreCase("--"))
                {
                    phonecc = ESAPI.validator().getValidInput("phonecc", request.getParameter("phonecc"), "SafeString", 4, true);
                }
                else
                {
                    phonecc = "";
                }
            }
                   // GST = ESAPI.validator().getValidInput("gst", request.getParameter("gst"), "AmountStr", 6, true);

            if (functions.isValueNull(duedate) && functions.isValueNull(expPeriod) && Integer.parseInt(duedate) >= Integer.parseInt(expPeriod))
            {
                /*if ((Integer.parseInt(duedate) >= Integer.parseInt(expPeriod)))
                {*/
                    error = UpdateInvoiceConfiguration_expiration1_errormsg;
                    request.setAttribute("error", error);
                    request.setAttribute("invoicevo", invoiceVO);
                    request.setAttribute("unitList",listOfUnit1);
                    request.setAttribute("defaultProductList",listOfProduct);
                    request.setAttribute("unit", unit);
                    RequestDispatcher rd1 = request.getRequestDispatcher(redirectpage);
                    rd1.forward(request, response);
                    return;
                /*}*/
            }
            invoiceVO.setMemberid(memberid.toUpperCase());
            invoiceVO.setInitial(initial);
            invoiceVO.setRedirecturl(redirectUrl);
            invoiceVO.setExpirationPeriod(expPeriod);
            //invoiceVO.setCurrency(currency);
            invoiceVO.setCity(city);
            invoiceVO.setState(state);
            invoiceVO.setCountry(country);
            invoiceVO.setTelCc(phonecc);
            invoiceVO.setGST(GST);
            invoiceVO.setIssms(isSMS);
            invoiceVO.setIsemail(isemail);
            invoiceVO.setIsapp(isapp);
            invoiceVO.setPaymentterms(paymentterms);
            invoiceVO.setIsduedate(isduedate);
            invoiceVO.setIslatefee(islatefee);
            invoiceVO.setDuedate(duedate);
            invoiceVO.setLatefee(latefee);
            invoiceVO.setUnit(unit);
            invoiceVO.setDefaultunitList(listOfUnit);
            invoiceVO.setDefaultProductList(listOfProduct);
            invoiceVO.setLoadDefaultProductList(loaddefaultproductlist);
            if (functions.isValueNull(terminalid))
            {
                if (!terminalid.equalsIgnoreCase("0"))
                {
                    invoiceVO.setTerminalid(terminalid);
                }
                else
                {
                    invoiceVO.setTerminalid("");
                }
            }
            else
            {
                invoiceVO.setTerminalid("");
            }

            boolean status = invoiceEntry.updateInvoiceConfigurationDetails(invoiceVO);
            if (status)
            {
                 successMsg = UpdateInvoiceConfiguration_updated_errormsg;
            }
            else
            {
                error = UpdateInvoiceConfiguration_updation_errormsg;
            }

            request.setAttribute("initTerminalValue",request.getParameter("terminal"));
            List<String> unitList = invoiceEntry.getUnitList(invoiceVO.getMemberid());
            List<DefaultProductList> prodList = invoiceEntry.getDefaultProductList(invoiceVO.getMemberid());


            request.setAttribute("defaultProductList", prodList);
            request.setAttribute("unitList",unitList);
            request.setAttribute("invoicevo", invoiceVO);
            request.setAttribute("error",error);
            request.setAttribute("successMsg",successMsg);
         //   request.setAttribute("paymentterms",paymentterms);

            RequestDispatcher rd2 = request.getRequestDispatcher(redirectpage);
            rd2.forward(request, response);
        }

        catch (ValidationException e)
        {
            log.error("Invalid INPUT", e);
            //PZExceptionHandler.raiseAndHandleConstraintViolationException("UpdateInvoiceConfiguration.java", "doPost()", null, "Merchant", "Validation Exception Thrown:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), memberid, null);
            error = "Invalid value entered";
            request.setAttribute("error",error);
            request.setAttribute("invoicevo",invoiceVO);
            RequestDispatcher rd = request.getRequestDispatcher("/invoiceConfiguration.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            log.error("Exception----",e);
            //e.printStackTrace();
            log.debug("Exception ::" + e);
        }
    }
}
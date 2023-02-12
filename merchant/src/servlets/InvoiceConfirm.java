import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.DefaultProductList;
import com.invoice.vo.ProductList;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Nov 27, 2012
 * Time: 10:16:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceConfirm extends HttpServlet
{
    private static Logger log = new Logger(InvoiceConfirm.class.getName());
    Connection con = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Enter in InvoiceConfirm");
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String InvoiceConfirm_product_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_product_errormsg"))?rb1.getString("InvoiceConfirm_product_errormsg"): "Invalid Product Description for row";
        String InvoiceConfirm_account_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_account_errormsg"))?rb1.getString("InvoiceConfirm_account_errormsg"): "Invalid Product Amount for row";
        String InvoiceConfirm_quantity_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_quantity_errormsg"))?rb1.getString("InvoiceConfirm_quantity_errormsg"): "Invalid Quantity for row";
        String InvoiceConfirm_tax_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_tax_errormsg"))?rb1.getString("InvoiceConfirm_tax_errormsg"): "Invalid Tax percentage for row";
        String InvoiceConfirm_valid_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_valid_errormsg"))?rb1.getString("InvoiceConfirm_valid_errormsg"): "Please enter Valid Redirect URL.";
        String InvoiceConfirm_expiry_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_expiry_errormsg"))?rb1.getString("InvoiceConfirm_expiry_errormsg"): "Invalid Expiry Time";
        String InvoiceConfirm_merchant_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_merchant_errormsg"))?rb1.getString("InvoiceConfirm_merchant_errormsg"): "Invalid Merchant Id";
        String InvoiceConfirm_order_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_order_errormsg"))?rb1.getString("InvoiceConfirm_order_errormsg"): "Invalid Order Id";
        String InvoiceConfirm_duplicate_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_duplicate_errormsg"))?rb1.getString("InvoiceConfirm_duplicate_errormsg"): "Duplicate order ID";
        String InvoiceConfirm_order1_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_order1_errormsg"))?rb1.getString("InvoiceConfirm_order1_errormsg"): "Invalid Order Description";
        String InvoiceConfirm_amount_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_amount_errormsg"))?rb1.getString("InvoiceConfirm_amount_errormsg"): "Invalid Amount";
        String InvoiceConfirm_jpy_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_jpy_errormsg"))?rb1.getString("InvoiceConfirm_jpy_errormsg"): "JPY currency does not have cent value after decimal. Please give .00 as decimal value";
        String InvoiceConfirm_customer_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_customer_errormsg"))?rb1.getString("InvoiceConfirm_customer_errormsg"): "Invalid Customer Name";
        String InvoiceConfirm_address_errormsg= StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_address_errormsg"))?rb1.getString("InvoiceConfirm_address_errormsg"): "Invalid Address";
        String InvoiceConfirm_city_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_city_errormsg"))?rb1.getString("InvoiceConfirm_city_errormsg"): "Invalid City";
        String InvoiceConfirm_zip_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_zip_errormsg"))?rb1.getString("InvoiceConfirm_zip_errormsg"): "Invalid Zip Code";
        String InvoiceConfirm_state_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_state_errormsg"))?rb1.getString("InvoiceConfirm_state_errormsg"): "Invalid State";
        String InvoiceConfirm_country_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_country_errormsg"))?rb1.getString("InvoiceConfirm_country_errormsg"): "Invalid Country Code";
        String InvoiceConfirm_number_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_number_errormsg"))?rb1.getString("InvoiceConfirm_number_errormsg"): "Invalid Country number";
        String InvoiceConfirm_phoneno_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_phoneno_errormsg"))?rb1.getString("InvoiceConfirm_phoneno_errormsg"): "Invalid Phone Number";
        String InvoiceConfirm_customerid_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_customerid_errormsg"))?rb1.getString("InvoiceConfirm_customerid_errormsg"): "Invalid Customer Email ID";
        String InvoiceConfirm_currency_errormsg = StringUtils.isNotEmpty(rb1.getString("InvoiceConfirm_currency_errormsg"))?rb1.getString("InvoiceConfirm_currency_errormsg"): "Invalid currency";
        InvoiceEntry invoiceEntry = new InvoiceEntry();


        if (!merchants.isLoggedIn(session))
        {
            log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        //variabe list
        String error = "";
        String EOL = "<BR>";
        boolean flag = true;
        Hashtable hiddenvariables = new Hashtable();
        String memberid = null;
        String orderid = null;
        String orderdesc = null;
        String amount = null;  //Amount
        String custname = null;
        String address = null;
        String city = null;
        String zipcode = null;
        String state = null;
        String countrycode = null;
        String phonecc = null;
        String phone = null;
        String custemail = null;
        String currency = req.getParameter("curr");
        String expTime = "";
        int time = 0;
        String productCounter = "";
        String totype = (String) session.getAttribute("company");
        String redirecturl = "";
        Functions functions = new Functions();
        String taxAmount = req.getParameter("taxamount");

        String gstcb = req.getParameter("gstcb");
        String grandTotal = req.getParameter("grandtotal");
        String defaultLanguage = req.getParameter("defaultLanguage");

        List<ProductList> listOfProducts = new ArrayList<ProductList>();
        List<DefaultProductList> defaultProductLists=new ArrayList<DefaultProductList>();
        if (functions.isValueNull(req.getParameter("productCounter")) && req.getParameter("productCounter").length() > 0)
        {
            productCounter = req.getParameter("productCounter");
            for (int i = 1; i <= Integer.parseInt(productCounter); i++)
            {
                ProductList productList = new ProductList();
                DefaultProductList defaultProductList=new DefaultProductList();
                if (req.getParameter("product" + i) == null && req.getParameter("price" + i) == null)
                {
                    continue;
                }
                //Product Description
                if (!ESAPI.validator().isValidInput("product", req.getParameter("product" + i), "Description", 30, false))
                {
                    log.debug("Invalid Product Description for " + i);
                    error = error + InvoiceConfirm_product_errormsg + i + EOL;
                    flag = false;
                }
                else
                {
                    productList.setProductDescription(req.getParameter("product" + i));
                    defaultProductList.setProductDescription(req.getParameter("product" + i));
                }
                //Product Price
                if (!ESAPI.validator().isValidInput("price", req.getParameter("price" + i), "Amount", 30, false))
                {
                    log.debug("Invalid Product Amount for row " + i);
                    error = error + InvoiceConfirm_account_errormsg + i + EOL;
                    flag = false;
                }
                else
                {
                    productList.setProductAmount(req.getParameter("price" + i));
                    defaultProductList.setProductAmount(req.getParameter("price" + i));
                }
                //Product Quantity
                if (!ESAPI.validator().isValidInput("qty", req.getParameter("qty" + i), "Numbers", 3, false))
                {
                    log.debug("Invalid Quantity for row " + i + EOL);
                    error = error + InvoiceConfirm_quantity_errormsg+ i + EOL;
                    flag = false;
                }
                else
                {
                    productList.setQuantity(req.getParameter("qty" + i));
                    defaultProductList.setQuantity(req.getParameter("qty" + i));
                }
                //Tax
                if (!ESAPI.validator().isValidInput("tax", req.getParameter("tax" + i), "Amount", 10, true))
                {
                    log.debug("Invalid Tax percentage for row " + i + EOL);
                    error = error + InvoiceConfirm_tax_errormsg + i + EOL;
                    flag = false;
                }
                else
                {
                    productList.setTax(req.getParameter("tax" + i));
                    defaultProductList.setTax(req.getParameter("tax" + i));
                }
                //Line Total
                productList.setProductTotal(req.getParameter("linetotal" + i));
                defaultProductList.setProductTotal(req.getParameter("linetotal" + i));

                //unit
                productList.setProductUnit(req.getParameter("productunit" + i));
                defaultProductList.setUnit(req.getParameter("productunit" + i));

                defaultProductLists.add(defaultProductList);
                listOfProducts.add(productList);
            }
        }
        if (!ESAPI.validator().isValidInput("url", (String) req.getParameter("redirecturl"), "URL", 100, false)|| functions.hasHTMLTags((String) req.getParameter("redirecturl")))
        {
            log.debug("PLS enter Valid Redirect URL");
            error = error + InvoiceConfirm_valid_errormsg + EOL;
            flag = false;
        }
        else
        {
            redirecturl = (String) req.getParameter("redirecturl");
        }

        if (!ESAPI.validator().isValidInput("expire", req.getParameter("expire"), "Numbers", 3, false))
        {
            log.debug("Invalid Expiry Time");
            error = error + InvoiceConfirm_expiry_errormsg + EOL;
            flag = false;
        }
        else
        {
            expTime = req.getParameter("expire");
            if (req.getParameter("frequency") != null && "Days".equalsIgnoreCase(req.getParameter("frequency")))
            {
                time = Integer.parseInt(req.getParameter("expire")) * 24;
            }
            else
            {
                time = Integer.parseInt(req.getParameter("expire"));
            }
        }

        //validate all requested variables
        if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid"), "Numbers", 30, false))
        {
            log.debug("Invalid memberid");
            error = error + InvoiceConfirm_merchant_errormsg + EOL;
            flag = false;
        }
        else
        {
            memberid = req.getParameter("memberid");
        }
        if (!ESAPI.validator().isValidInput("orderid", req.getParameter("orderid"), "Description", 30, false))
        {
            log.debug("Invalid orderid");
            error = error + InvoiceConfirm_order_errormsg + EOL;
            flag = false;
        }
        else
        {
            orderid = req.getParameter("orderid");
        }
        boolean uniqueOrder = invoiceEntry.isOrderUnique(req.getParameter("orderid"));
        if (!uniqueOrder)
        {
            log.debug("Invalid orderid");
            error = error + InvoiceConfirm_duplicate_errormsg + EOL;
            flag = false;
        }
        else
        {
            orderid = req.getParameter("orderid");
        }

        if (!ESAPI.validator().isValidInput("orderdesc", req.getParameter("orderdesc"), "Description", 30, true))
        {
            log.debug("Invalid order description");
            error = error + InvoiceConfirm_order1_errormsg + EOL;
            flag = false;
        }
        else
        {
            orderdesc = req.getParameter("orderdesc");
        }
        log.debug("Checkpoint 1");
        if (!ESAPI.validator().isValidInput("amount", req.getParameter("amount"), "Amount", 30, false) || (req.getParameter("amount").equalsIgnoreCase("0.00")))
        {
            log.debug("Invalid amount");
            error = error + InvoiceConfirm_amount_errormsg + EOL;
            flag = false;
        }
        else
        {
            amount = req.getParameter("amount");
        }
        if (req.getParameter("curr").equalsIgnoreCase("JPY") && !req.getParameter("amount").contains("."))
        {
            log.debug("Invalid amount for JPY currency");
            error = error + InvoiceConfirm_jpy_errormsg + EOL;
            flag = false;
        }
        else
        {
            amount = req.getParameter("amount");
        }

        if (!ESAPI.validator().isValidInput("custname", req.getParameter("custname"), "Name", 30, true)|| functions.hasHTMLTags(req.getParameter("custname")))
        {
            log.debug("Invalid Customer name");
            error = error + InvoiceConfirm_customer_errormsg + EOL;
            flag = false;
        }
        else
        {
            custname = req.getParameter("custname");
        }
      /*  if (error.length() > 0)
        {
            req.setAttribute("error", error);
            log.debug("forwarding request to invoice generate");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/GenerateInvoice?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }*/

        if (!ESAPI.validator().isValidInput("address", req.getParameter("address"), "Description", 100, true))
        {
            log.debug("Invalid address");
            error = error + InvoiceConfirm_address_errormsg + EOL;
            flag = false;
        }
        else
        {
            address = req.getParameter("address");
        }

        if (!ESAPI.validator().isValidInput("city", req.getParameter("city"), "Description", 40, true))
        {
            log.debug("Invalid city");
            error = error + InvoiceConfirm_city_errormsg + EOL;
            flag = false;
        }
        else
        {
            city = req.getParameter("city");
        }

        if (!ESAPI.validator().isValidInput("zipcode", req.getParameter("zipcode"), "Zip", 9, true))
        {
            log.debug("Invalid Zip");
            error = error + InvoiceConfirm_zip_errormsg + EOL;
            flag = false;

        }
        else
        {
            zipcode = req.getParameter("zipcode");
        }
        if (!ESAPI.validator().isValidInput("state", req.getParameter("state"), "Description", 30, true))
        {
            log.debug("Invalid state");
            error = error + InvoiceConfirm_state_errormsg + EOL;
            flag = false;
        }
        else
        {
            state = req.getParameter("state");
        }
        if (!req.getParameter("countrycode").equalsIgnoreCase("--"))
        {
            if (!ESAPI.validator().isValidInput("countrycode", req.getParameter("countrycode"), "CountryCode", 30, true))
            {
                log.debug("Invalid countrycode");
                error = error + InvoiceConfirm_country_errormsg + EOL;
                flag = false;
            }
            else
            {
                countrycode = req.getParameter("countrycode");
            }
        }
        else
        {
            countrycode = "";
        }
        log.debug("Checkpoint 2");
        if (!req.getParameter("phonecc").equalsIgnoreCase("--"))
        {
            if (!ESAPI.validator().isValidInput("phonecc", req.getParameter("phonecc"), "Phone", 30, true))
            {
                log.debug("Invalid phone");
                error = error + InvoiceConfirm_number_errormsg + EOL;
                flag = false;
            }
            else
            {
                phonecc = req.getParameter("phonecc");
            }
        }
        else
        {
            phonecc = "";
        }

        if (!ESAPI.validator().isValidInput("phone", req.getParameter("phone"), "Phone", 15, true))
        {
            log.debug("Invalid phone");
            error = error + InvoiceConfirm_phoneno_errormsg + EOL;
            flag = false;
        }
        else
        {
            phone = req.getParameter("phone");
        }
        if ((!ESAPI.validator().isValidInput("custemail", req.getParameter("custemail"), "Email", 50, true)) || ((String) req.getParameter("custemail")).equals(""))
        {
            log.debug("Invalid Customer email id");
            error = error + InvoiceConfirm_customerid_errormsg + EOL;
            flag = false;
        }
        else
        {
            custemail = req.getParameter("custemail");
        }
        String autoSelectTerminal = req.getParameter("autoselectterminal");

        if (req.getParameter("curr").equals("all") || !ESAPI.validator().isValidInput("currency", req.getParameter("curr"), "StrictString", 3, false))
        {
            log.debug("Invalid currency");
            error = error + InvoiceConfirm_currency_errormsg + EOL;
            flag = false;
        }
        else
        {
            phone = req.getParameter("phone");
        }
        /*boolean b = updatedefaultLanguage(defaultLanguage,memberid);
        if (b)
        {
            log.debug("defaultLanguage updated successfully" + defaultLanguage);
        }*/

        //calcute checksum
        log.debug("Checkpoint 3");
        //set hidden veriables
        if (flag)
        {
            hiddenvariables.put("memberid", memberid);
            log.debug("entering memberid");
            hiddenvariables.put("orderid", orderid);
            log.debug("entering orderid");
            hiddenvariables.put("orderdesc", orderdesc);
            log.debug("entering orderdesc");
            hiddenvariables.put("amount", amount);
            log.debug("entering amount");
            hiddenvariables.put("custname", custname);
            log.debug("entering name");
            hiddenvariables.put("address", address);
            log.debug("entering addr");
            hiddenvariables.put("city", city);
            hiddenvariables.put("zipcode", zipcode);
            log.debug("entering zip");
            hiddenvariables.put("state", state);

            hiddenvariables.put("country", countrycode);
            log.debug("entering country");
            hiddenvariables.put("phonecc", phonecc);
            hiddenvariables.put("phone", phone);
            log.debug("entering phone");
            hiddenvariables.put("custemail", custemail);
            hiddenvariables.put("listofproducts", listOfProducts);
            if (null != taxAmount)
            {
                hiddenvariables.put("taxamount", taxAmount);
            }
            else
            {
                hiddenvariables.put("taxamount", "");
            }
            if (null != gstcb)
            {
                hiddenvariables.put("gstcb", gstcb);
            }
            else
            {
                hiddenvariables.put("gstcb", "");
            }
            if (null != taxAmount)
            {
                hiddenvariables.put("grandtotal", grandTotal);
            }
            else
            {
                hiddenvariables.put("grandtotal", "");
            }
            log.debug("entering email and custemail");
            log.debug("entering paymodeid ");

            if (functions.isValueNull(req.getParameter("terminal")) && !(req.getParameter("terminal").equals("0")))
            {
                String terminalValue[] = req.getParameter("terminal").split("-");
                String terminalid = terminalValue[0];
               // String curr = terminalValue[1];
                String paymode = terminalValue[1];
                String cardtypeid = terminalValue[2];

                hiddenvariables.put("paymodeid", paymode);
                hiddenvariables.put("cardname", cardtypeid);
                hiddenvariables.put("terminalid", terminalid);
            }


            hiddenvariables.put("currency", currency);

            log.debug("entering totype");
            hiddenvariables.put("totype", totype);
            hiddenvariables.put("redirecturl", redirecturl);
            hiddenvariables.put("autoselectterminal", autoSelectTerminal);
            hiddenvariables.put("expPeriod", time);
            hiddenvariables.put("expTime", expTime);
            hiddenvariables.put("frequency", req.getParameter("frequency"));
            hiddenvariables.put("defaultLanguage", req.getParameter("defaultLanguage"));

            req.setAttribute("hiddenvariables", hiddenvariables);

            RequestDispatcher rd = req.getRequestDispatcher("/invoiceConfirm.jsp?ctoken=" + user.getCSRFToken());
            log.debug("forwarding request to invoice confirm");
            rd.forward(req, res);
        }
        else
        {
            req.setAttribute("error", error);
            req.setAttribute("amount", req.getParameter("amount"));
            req.setAttribute("custemail", req.getParameter("custemail"));
            req.setAttribute("custname", req.getParameter("custname"));
            req.setAttribute("orderdesc", req.getParameter("orderdesc"));
            req.setAttribute("address", req.getParameter("address"));
            req.setAttribute("city", req.getParameter("city"));
            req.setAttribute("zipcode", req.getParameter("zipcode"));
            req.setAttribute("countrycode", req.getParameter("countrycode"));
            req.setAttribute("state", req.getParameter("state"));
            req.setAttribute("phonecc", req.getParameter("phonecc"));
            req.setAttribute("phone", req.getParameter("phone"));
            req.setAttribute("taxamount",req.getParameter("taxamount"));
            req.setAttribute("grandtotal",req.getParameter("grandtotal"));
            req.setAttribute("listOfProducts",defaultProductLists);
            log.debug("forwarding request to invoice generate");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/GenerateInvoice?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
    }

    /*public boolean updatedefaultLanguage(String defaultLanguage,String memberid)
    {
        boolean status = false;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con = Database.getConnection();
            String query = "update `members` set defaultLanguage=? where memberid=?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,defaultLanguage);
            preparedStatement.setString(2,memberid);
            log.error("query update");
            int k = preparedStatement.executeUpdate();
            if (k == 1)
            {
                status = true;
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException while updating defaultLanguage flag", se);
        }
        catch (SystemError e)
        {
            log.error("SystemError while updating defaultLanguage flag", e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return status;
    }*/
}

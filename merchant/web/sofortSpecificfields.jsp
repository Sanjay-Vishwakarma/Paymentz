<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 7/2/15
  Time: 9:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
    String fileName = getValue((String) session.getAttribute("filename"));
    String email = getValue(genericAddressDetailsVO.getEmail());
    String city = getValue(genericAddressDetailsVO.getCity());
    String street = getValue(genericAddressDetailsVO.getStreet());
    String zip = getValue(genericAddressDetailsVO.getZipCode());
    String state = getValue(genericAddressDetailsVO.getState());
    String country = getValue(genericAddressDetailsVO.getCountry());
    String telno = getValue(genericAddressDetailsVO.getPhone());
    String telnocc = getValue(genericAddressDetailsVO.getTelnocc());
    String TMPL_COUNTRY = getValue(genericAddressDetailsVO.getCountry());
    String firstname = getValue(genericAddressDetailsVO.getFirstname());
    String lastname = getValue(genericAddressDetailsVO.getLastname());
%>

<table  style="border:1px solid #34495e;margin-left:35% " bgcolor="white" align="center" width="50%" cellpadding="2" cellspacing="2">
    <tr>
        <td style="padding:2px ">
            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                    <td style="padding:3px "bgcolor="#007ACC" colspan="4" class="texthead"><i class="fa fa-table " style="float:left;padding-top:9px;"></i></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                    <td style="padding:2px " class="textb">&nbsp;</td>
                    <td style="padding:2px " class="textb">Phone</td>
                    <td style="padding:2px " class="textb">:</td>
                    <td style="padding:2px "><input type="text" name="telnocc" size="5" maxlength="5" class="txtbox" style="width:100px;" readonly="readonly" title="Example(Country Code - Phone Number)">
                        -
                        <input type="text" name="telno" size="20" maxlength="20" class="txtbox" title="Example(Country Code - Phone Number)"></td>
                </tr>
                <tr>
                    <td style="padding:2px " class="textb">&nbsp;</td>
                    <td style="padding:2px " class="textb">Email ID</td>
                    <td style="padding:2px " class="textb">:</td>
                    <td style="padding:2px ">
                        <input type="text" name="emailaddr" size="30" maxlength="40" title="Ex: abc@xyz.com" value="" class="txtbox" style="width: 256px"/>
                    </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

            </table>
        </td>
    </tr>
</table>

<input type="hidden" name="street" value="<%=street%>">
<input type="hidden" name="city" value="<%=city%>">
<input type="hidden" name="zip" value="<%=zip%>">
<input type="hidden" name="state" value="<%=state%>">
<input type="hidden" name="country" value="<%=country%>">
<%!
    private String getValue(String data)
    {
        String tempVal="";
        if(data==null)
        {
            tempVal="";
        }
        else
        {
            tempVal= data;
        }
        return tempVal;
    }
%>
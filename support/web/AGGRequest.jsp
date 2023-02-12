<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jul 21, 2012
  Time: 7:19:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


         <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
         <HTML><HEAD><TITLE>Payment Gateway</TITLE>
         <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
         <meta http-equiv="Expires" content="0">
         <meta http-equiv="Pragma" content="no-cache">
         </HEAD>
         <SCRIPT language="javascript">



         function checkNotEmpty()
         {
         	var message="";
         	var focus="f";
         	if (document.mainForm.CARDHOLDER.value.length == 0){
         		message=message+"Please enter name of Cardholder.\n"
         		document.mainForm.CARDHOLDER.focus();
         		focus="t";
         	}
         	if (document.mainForm.ccid.value.length <3){
         		message=message+"Please enter Card Verification Number.\n";
         		if(focus!="t"){
         			document.mainForm.ccid.focus();
         			focus="t";
         		}
         	}
         	if(isNaN(document.mainForm.ccid.value)){
         		message=message+"Please Enter a Numeric value in the Card Verification Number.\n";
         		document.mainForm.ccid.value='';
         		if(focus!="t"){
         			document.mainForm.ccid.focus();
         			focus="t";
         		}
         	}
         	if (document.mainForm.street.value.length == 0){
         		message=message+"Please enter Street.\n";
         		if(focus!="t"){
         			document.mainForm.street.focus();
         			focus="t";
         		}
         	}
         	if (document.mainForm.city.value.length == 0){
         		message=message+"Please enter City.\n";
         		if(focus!="t"){
         			document.mainForm.city.focus();
         			focus="t";
         		}
         	}
         	if (document.mainForm.state.value.length == 0){
         		message=message+"Please enter State.\n";
         		if(focus!="t"){
         			document.mainForm.state.focus();
         			focus="t";
         		}
         	}
         	if (document.mainForm.zip.value.length == 0){
         		message=message+"Please enter Zip.\n";
         		if(focus!="t"){
         			document.mainForm.zip.focus();
         			focus="t";
         		}
         	}

         	var vartelnocc = document.mainForm.telnocc;
         	var vartelno = document.mainForm.telno;

         	if((isNaN(vartelnocc.value))||vartelnocc.value.length==0){
         		message=message+"Please Enter a Numeric value for country code.\n";
         		if(focus!="t"){
         			vartelnocc.focus();
         			focus="t";
         		}
         	}
         	if((isNaN(vartelno.value))||vartelno.value.length==0){
         		message=message+"Please Enter a Numeric value in the Tel. number.\n";
         		if(focus!="t"){
         			vartelno.focus();
         			focus="t";
         		}
         	}
         	if (document.mainForm.emailaddr.value.length == 0){
         		message=message+"Please enter Email Address.\n";
         		if(focus!="t"){
         			document.mainForm.emailaddr.focus();
         			focus="t";
         		}
         	}
         	if(focus=="t"){
         		alert(message);
         		return false;
         	}else
         		return true;
         }

         function CardCheck()
         {
         	var ptr = document.mainForm.PAN ;
         	Chk=parseInt(ptr.value.substring(0,1));
         	Chk1=ptr.value.length;

         	if((isNaN(ptr.value))||ptr.value.length==0){
         		alert('Please Enter a Numeric value in the Card Number');
         		ptr.value='';
         		ptr.focus();
         		return false;
         	}
         	NumStr = parseInt(ptr.value).toString();
         	NumLen = NumStr.length;
         	if((Chk==3 && NumLen!=14 && Chk1!=NumLen)||(Chk!=3 && NumLen!=16 && Chk1!=NumLen)){
         		alert('Please Enter a Numeric value in the Card Number');
         		ptr.focus();
         		ptr.value='';
         		return false;
         	}

         	if((Chk==3 && Chk1!=14)||(Chk!=3 && Chk1<16)||(NumStr <0)){
         		alert('Please enter the card number correctly');
         		ptr.focus();
         		ptr.value='';
         		return false;
         	}
         	if(!checkNotEmpty())
         		return false;
         	else
             	return true;
         }
         var flag = '0';




         </script>
         <BODY aLink=#000080 leftMargin=0 link=#003399
         topMargin=0 vLink=#800080 marginheight="0" marginwidth="0" bgcolor="BLACK" text="RED" background="">

         <FORM name="mainForm" METHOD="POST" ACTION="/support/servlet/AGG">


           <table border="0" cellpadding="5" cellspacing="0" width="80%">
             <tr>
               <td width="100%" colspan="2"><font face="arial,verdana,helvetica" size="2">This
                 payment will be processed. This is a Secure Connection and
                 your information is secure and private.</font></td>

             </tr>


            <tr>
             <td>
               <p align="right"><b><font face="arial,verdana,helvetica" size="2">Request ID</font>:</b></p></td>
             <center>
             <td><font face="arial,verdana,helvetica" size="2"><input name="REQUESTID" size="30" autocomplete="OFF" ></font></td>
             </tr>

             <tr>


           <tr>
             <td>
               <p align="right"><b><font face="arial,verdana,helvetica" size="2">Account ID</font>:</b></p></td>
             <center>
             <td><font face="arial,verdana,helvetica" size="2"><input name="ACCOUNTID" size="30" autocomplete="OFF" ></font></td>
             </tr>

             <tr>
             <td>
               <p align="right"><b><font face="arial,verdana,helvetica" size="2">Product Code</font>:</b></p></td>
             <center>
             <td><font face="arial,verdana,helvetica" size="2"><input name="PRODUCTCODE" size="30" autocomplete="OFF" ></font></td>
             </tr>

             <tr>
             <td colspan="2">

               <font face="arial,verdana,helvetica" size="2">Enter your Credit Card
               Details</font></td>
             </tr>
           <tr>
             <td>
               <p align="right"><b><font face="arial,verdana,helvetica" size="2">Name on
               Card</font>:</b></p></td>
             <center>
             <td><input name="CARDHOLDER" size="30" autocomplete="OFF" ></td>

             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Card Number</font>:</b></p></td>
               <td><input name="PAN" size="30" maxlength="16" autocomplete="OFF"><font face="arial,verdana,helvetica" size="1"><nobr>&nbsp;&nbsp;( Visa/Mastercard only )</font></td>
             </tr>
             <tr>

               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Expiration
                 Date</font>:</b></p></td>
               <td>
         	<select NAME="EXPIRE_MONTH">
         	<option VALUE="01" selected>January</option>
         	<option VALUE="02">February</option>
         	<option VALUE="03">March</option>

         	<option VALUE="04">April</option>
         	<option VALUE="05">May</option>
         	<option VALUE="06">June</option>
         	<option VALUE="07">July</option>
         	<option VALUE="08">August</option>
         	<option VALUE="09">September</option>

         	<option VALUE="10">October</option>
         	<option VALUE="11">November</option>
         	<option VALUE="12">December</option>
         	</select>
         	<select NAME="EXPIRE_YEAR">
         	<option VALUE="2006" >2006</option>
         	<option VALUE="2007">2007</option>

         	<option VALUE="2008">2008</option>
         	<option VALUE="2009"SELECTED>2009</option>
         	<option VALUE="2010">2010</option>
         	<option VALUE="2011">2011</option>
         	<option VALUE="2012">2012</option>
         	<option VALUE="2013">2013</option>

         	<option VALUE="2014">2014</option>
         	<option VALUE="2015">2015</option>
         	<option VALUE="2016">2016</option>
         	<option VALUE="2017">2017</option>
         	<option VALUE="2018">2018</option>
         	<option VALUE="2019">2019</option>

         	<option VALUE="2020">2020</option>
         	</select>
               </td>
             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Card
                 Verification Number</font>:</b><br><font face="arial,verdana,helvetica" size="1"></font></p></td>

               <td><input name="ccid" size="2" maxlength="3" autocomplete="OFF" > <font face="arial,verdana,helvetica" size="1"><nobr>(Locate
                 the last 3 digits of the number on the back of your card)</font> </td>
             </tr>
             <tr>
               <td colspan="2"><font face="arial,verdana,helvetica" size="2">Billing
                 Address (where you receive your Credit Card bills)</font></td>
             </tr>
             <tr>
               <td>

                 <p align="right"><b><font face="arial,verdana,helvetica" size="2" >Street</font>:</b></p></td>
               <td><input name="street" size="30" value="4 Bunglow" onpaste="return pasted('ADDRCP');" ></td>
             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">City</font>:</b></p></td>
               <td><input name="city" size="30" value="Mumbai" ></td>

             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">State</font>:</b></p></td>
               <td><input name="state" value="Maharashtra" size="30" ></td>
             </tr>
             <tr>
               <td>

                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Zip</font>:</b></p></td>
               <td><input name="zip" size="30" maxlength="10" value = "400053" ></td>
             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Country</font>:</b></p></td>
               <td>

         	<select name="country" >
         	<option value="">Select a Country</option>
         		<option value="AF"  >Afghanistan</option>
         		<option value="AL"  >Albania</option>
         		<option value="DZ"  >Algeria</option>
         		<option value="AS"  >American Samoa</option>

         		<option value="AD"  >Andorra</option>
         		<option value="AO"  >Angola</option>
         		<option value="AI"  >Anguilla</option>
         		<option value="AQ"  >Antarctica</option>
         		<option value="AG"  >Antigua And Barbuda</option>
         		<option value="AR"  >Argentina</option>

         		<option value="AM"  >Armenia</option>
         		<option value="AW"  >Aruba</option>
         		<option value="AU"  >Australia</option>
         		<option value="AT"  >Austria</option>
         		<option value="AZ"  >Azerbaijan</option>
         		<option value="BS"  >Bahamas, The</option>

         		<option value="BH"  >Bahrain</option>
         		<option value="BD"  >Bangladesh</option>
         		<option value="BB"  >Barbados</option>
         		<option value="BY"  >Belarus</option>
         		<option value="BE"  >Belgium</option>
         		<option value="BZ"  >Belize</option>

         		<option value="BJ"  >Benin</option>
         		<option value="BM"  >Bermuda</option>
         		<option value="BT"  >Bhutan</option>
         		<option value="BO"  >Bolivia</option>
         		<option value="BA"  >Bosnia and Herzegovina</option>
         		<option value="BW"  >Botswana</option>

         		<option value="BV"  >Bouvet Island</option>
         		<option value="BR"  >Brazil</option>
         		<option value="IO"  >British Indian Ocean Territory</option>
         		<option value="BN"  >Brunei</option>
         		<option value="BG"  >Bulgaria</option>
         		<option value="BF"  >Burkina Faso</option>

         		<option value="BI"  >Burundi</option>
         		<option value="KH"  >Cambodia</option>
         		<option value="CM"  >Cameroon</option>
         		<option value="CA"  >Canada</option>
         		<option value="CV"  >Cape Verde</option>
         		<option value="KY"  >Cayman Islands</option>

         		<option value="CF"  >Central African Republic</option>
         		<option value="TD"   >Chad</option>
         		<option value="CL"  >Chile</option>
         		<option value="CN"  >China</option>
         		<option value="CX"  >Christmas Island</option>
         		<option value="CC"  >Cocos (Keeling) Islands</option>

         		<option value="CO"  >Colombia</option>
         		<option value="KM"  >Comoros</option>
         		<option value="CG"  >Congo</option>
         		<option value="CD"  >Congo, Democractic Republic of the</option>
         		<option value="CK"  >Cook Islands</option>
         		<option value="CR"  >Costa Rica</option>

         		<option value="CI"  >Cote D''Ivoire (Ivory Coast)</option>
         		<option value="HR"  >Croatia (Hrvatska)</option>
         		<option value="CU"  >Cuba</option>
         		<option value="CY"  >Cyprus</option>
         		<option value="CZ"  >Czech Republic</option>
         		<option value="DK"  >Denmark</option>

         		<option value="DJ"  >Djibouti</option>
         		<option value="DM"  >Dominica</option>
         		<option value="DO"  >Dominican Republic</option>
         		<option value="TP"  >East Timor</option>
         		<option value="EC"  >Ecuador</option>
         		<option value="EG"  >Egypt</option>

         		<option value="SV"  >El Salvador</option>
         		<option value="GQ"  >Equatorial Guinea</option>
         		<option value="ER"  >Eritrea</option>
         		<option value="EE"  >Estonia</option>
         		<option value="ET"  >Ethiopia</option>
         		<option value="FK"  >Falkland Islands (Islas Malvinas)</option>

         		<option value="FO"  >Faroe Islands</option>
         		<option value="FJ"  >Fiji Islands</option>
         		<option value="FI"  >Finland</option>
         		<option value="FR"  >France</option>
         		<option value="GF"  >French Guiana</option>
         		<option value="PF"  >French Polynesia</option>

         		<option value="TF"  >French Southern Territories</option>
         		<option value="GA"  >Gabon</option>
         		<option value="GM"  >Gambia, The</option>
         		<option value="GE"  >Georgia</option>
         		<option value="DE"  >Germany</option>
         		<option value="GH"  >Ghana</option>

         		<option value="GI"  >Gibraltar</option>
         		<option value="GR"  >Greece</option>
         		<option value="GL"  >Greenland</option>
         		<option value="GD"  >Grenada</option>
         		<option value="GP"  >Guadeloupe</option>
         		<option value="GU"  >Guam</option>

         		<option value="GT"  >Guatemala</option>
         		<option value="GN"  >Guinea</option>
         		<option value="GW"  >Guinea-Bissau</option>
         		<option value="GY"  >Guyana</option>
         		<option value="HT"  >Haiti</option>
         		<option value="HM"  >Heard and McDonald Islands</option>

         		<option value="HN"  >Honduras</option>
         		<option value="HK"  >Hong Kong S.A.R.</option>
         		<option value="HU"  >Hungary</option>
         		<option value="IS"  >Iceland</option>
         		<option value="IN" selected >India</option>
         		<option value="ID"  >Indonesia</option>

         		<option value="IR"  >Iran</option>
         		<option value="IQ"  >Iraq</option>
         		<option value="IE"  >Ireland</option>
         		<option value="IL"  >Israel</option>
         		<option value="IT"  >Italy</option>
         		<option value="JM"  >Jamaica</option>

         		<option value="JP"  >Japan</option>
         		<option value="JO"  >Jordan</option>
         		<option value="KZ"  >Kazakhstan</option>
         		<option value="KE"  >Kenya</option>
         		<option value="KI"  >Kiribati</option>
         		<option value="KR"  >Korea</option>

         		<option value="KP"  >Korea, North</option>
         		<option value="KW"  >Kuwait</option>
         		<option value="KG"  >Kyrgyzstan</option>
         		<option value="LA"  >Laos</option>
         		<option value="LV"  >Latvia</option>
         		<option value="LB"  >Lebanon</option>

         		<option value="LS"  >Lesotho</option>
         		<option value="LR"  >Liberia</option>
         		<option value="LY"  >Libya</option>
         		<option value="LI"  >Liechtenstein</option>
         		<option value="LT"  >Lithuania</option>
         		<option value="LU"  >Luxembourg</option>

         		<option value="MO"  >Macau S.A.R.</option>
         		<option value="MK"  >Macedonia, Former Yugoslav Republic of</option>
         		<option value="MG"  >Madagascar</option>
         		<option value="MW"  >Malawi</option>
         		<option value="MY"  >Malaysia</option>
         		<option value="MV"  >Maldives</option>

         		<option value="ML"  >Mali</option>
         		<option value="MT"  >Malta</option>
         		<option value="MH"  >Marshall Islands</option>
         		<option value="MQ"  >Martinique</option>
         		<option value="MR"  >Mauritania</option>
         		<option value="MU"  >Mauritius</option>

         		<option value="YT"  >Mayotte</option>
         		<option value="MX"  >Mexico</option>
         		<option value="FM"  >Micronesia</option>
         		<option value="MD"  >Moldova</option>
         		<option value="MC"  >Monaco</option>
         		<option value="MN"  >Mongolia</option>

         		<option value="MS"  >Montserrat</option>
         		<option value="MA"  >Morocco</option>
         		<option value="MZ"  >Mozambique</option>
         		<option value="MM"  >Myanmar</option>
         		<option value="NA"  >Namibia</option>
         		<option value="NR"  >Nauru</option>

         		<option value="NP"  >Nepal</option>
         		<option value="AN"  >Netherlands Antilles</option>
         		<option value="NL"  >Netherlands, The</option>
         		<option value="NC"  >New Caledonia</option>
         		<option value="NZ"  >New Zealand</option>
         		<option value="NI"  >Nicaragua</option>

         		<option value="NE"  >Niger</option>
         		<option value="NG"  >Nigeria</option>
         		<option value="NU"  >Niue</option>
         		<option value="NF"  >Norfolk Island</option>
         		<option value="MP"  >Northern Mariana Islands</option>
         		<option value="NO"  >Norway</option>

         		<option value="OM"  >Oman</option>
         		<option value="PK"  >Pakistan</option>
         		<option value="PW"  >Palau</option>
         		<option value="PA"  >Panama</option>
         		<option value="PG"  >Papua new Guinea</option>
         		<option value="PY"  >Paraguay</option>

         		<option value="PE"  >Peru</option>
         		<option value="PH"  >Philippines</option>
         		<option value="PN"  >Pitcairn Island</option>
         		<option value="PL"  >Poland</option>
         		<option value="PT"  >Portugal</option>
         		<option value="PR"  >Puerto Rico</option>

         		<option value="QA"  >Qatar</option>
         		<option value="RE"  >Reunion</option>
         		<option value="RO"  >Romania</option>
         		<option value="RU"  >Russia</option>
         		<option value="RW"  >Rwanda</option>
         		<option value="SH"  >Saint Helena</option>

         		<option value="KN"  >Saint Kitts And Nevis</option>
         		<option value="LC"  >Saint Lucia</option>
         		<option value="PM"  >Saint Pierre and Miquelon</option>
         		<option value="VC"  >Saint Vincent And The Grenadines</option>
         		<option value="WS"  >Samoa</option>
         		<option value="SM"  >San Marino</option>

         		<option value="ST"  >Sao Tome and Principe</option>
         		<option value="SA"  >Saudi Arabia</option>
         		<option value="SN"  >Senegal</option>
         		<option value="SC"  >Seychelles</option>
         		<option value="SL"  >Sierra Leone</option>
         		<option value="SG"  >Singapore</option>

         		<option value="SK"  >Slovakia</option>
         		<option value="SI"  >Slovenia</option>
         		<option value="SB"  >Solomon Islands</option>
         		<option value="SO"  >Somalia</option>
         		<option value="ZA"  >South Africa</option>
         		<option value="GS"  >South Georgia And The South Sandwich Islands</option>

         		<option value="ES"  >Spain</option>
         		<option value="LK"  >Sri Lanka</option>
         		<option value="SD"  >Sudan</option>
         		<option value="SR"  >Suriname</option>
         		<option value="SJ"  >Svalbard And Jan Mayen Islands</option>
         		<option value="SZ"  >Swaziland</option>

         		<option value="SE"  >Sweden</option>
         		<option value="CH"  >Switzerland</option>
         		<option value="SY"  >Syria</option>
         		<option value="TW"  >Taiwan</option>
         		<option value="TJ"  >Tajikistan</option>
         		<option value="TZ"  >Tanzania</option>

         		<option value="TH"  >Thailand</option>
         		<option value="TG"  >Togo</option>
         		<option value="TK"  >Tokelau</option>
         		<option value="TO"  >Tonga</option>
         		<option value="TT"  >Trinidad And Tobago</option>
         		<option value="TN"  >Tunisia</option>

         		<option value="TR"  >Turkey</option>
         		<option value="TM"  >Turkmenistan</option>
         		<option value="TC"  >Turks And Caicos Islands</option>
         		<option value="TV"  >Tuvalu</option>
         		<option value="UG"  >Uganda</option>
         		<option value="UA"  >Ukraine</option>

         		<option value="AE"  >United Arab Emirates</option>
         		<option value="UK"  >United Kingdom</option>
         		<option value="US"  >United States</option>
         		<option value="UM"  >United States Minor Outlying Islands</option>
         		<option value="UY"  >Uruguay</option>
         		<option value="UZ"  >Uzbekistan</option>

         		<option value="VU"  >Vanuatu</option>
         		<option value="VA"  >Vatican City State (Holy See)</option>
         		<option value="VE"  >Venezuela</option>
         		<option value="VN"  >Vietnam</option>
         		<option value="VG"  >Virgin Islands (British)</option>
         		<option value="VI"  >Virgin Islands (US)</option>

         		<option value="WF"  >Wallis And Futuna Islands</option>
         		<option value="YE"  >Yemen</option>
         		<option value="YU"  >Yugoslavia</option>
         		<option value="ZM"  >Zambia</option>
         		<option value="ZW"  >Zimbabwe</option>
         	</select>

               </td>
             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Tel</font>:</b></p></td>
               <td><input name="telnocc" size="2" maxlength="3" value="091"> - <input name="telno" size="9" maxlength="10" value="226370256">
                 <font face="arial,verdana,helvetica" size="1">(eg 001 - 2306370256)</font>

               </td>
             </tr>
             <tr>
               <td colspan="2"><font face="arial,verdana,helvetica" size="2">Please enter
                 your email address below for any correspondence with respect to this
                 transaction</font></td>
             </tr>
             <tr>
               <td>
                 <p align="right"><b><font face="arial,verdana,helvetica" size="2">Email
                 Address</font>:</b></p></td>

               <td><input name="emailaddr" size="30" value="dhiresh_mehta@yahoo.co.in"  ></td>
             </tr>
              <tr>
               <td width="25%" height="1">
                 <p align="right"><input type="submit" value="Continue"></p>
               </td>
             </tr>

           </table>
         </center>
         </div>

         </BODY></HTML>


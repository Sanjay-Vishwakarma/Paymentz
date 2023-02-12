<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileDetailsVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.enums.ApplicationManagerTypes" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() { $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"}); });
</script>
<%!
    private Functions functions = new Functions();
    private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
    boolean view=false;
    String globaldisabled="";
    if(functions.isValueNull(request.getParameter("view"))) {
        globaldisabled="disabled";
        view=true;
    }
    else { globaldisabled="";}
    String disableNamePrinciple2="";
    String disableNamePrinciple3="";
    String disableNamePrinciple4="";
    String disablecolor="";

    String disableShareHolder2="";
    String disableShareHolder3="";
    String disableShareHolder4="";

    ApplicationManagerVO applicationManagerVO=null;
    OwnershipProfileVO ownershipProfileVO=null;
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory4 = new OwnershipProfileDetailsVO();
    ValidationErrorList validationErrorList=null;
    Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap = new HashMap();
    if(session.getAttribute("applicationManagerVO")!=null) { applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");}
    if(applicationManagerVO.getOwnershipProfileVO()!=null)
    {
        ownershipProfileVO=applicationManagerVO.getOwnershipProfileVO();
        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().size() > 0)
        {
            ownershipProfileDetailsVO_shareholder1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1");
            ownershipProfileDetailsVO_shareholder2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2");
            ownershipProfileDetailsVO_shareholder3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3");
            ownershipProfileDetailsVO_shareholder4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER4");
            ownershipProfileDetailsVO_corporateShareholder1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1");
            ownershipProfileDetailsVO_corporateShareholder2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2");
            ownershipProfileDetailsVO_corporateShareholder3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3");
            ownershipProfileDetailsVO_corporateShareholder4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER4");
            ownershipProfileDetailsVO_director1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1");
            ownershipProfileDetailsVO_director2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2");
            ownershipProfileDetailsVO_director3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3");
            ownershipProfileDetailsVO_director4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR4");
            ownershipProfileDetailsVO_authorizeSignatory1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1");
            ownershipProfileDetailsVO_authorizeSignatory2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2");
            ownershipProfileDetailsVO_authorizeSignatory3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3");
            ownershipProfileDetailsVO_authorizeSignatory4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY4");
        }
    }
    if(ownershipProfileVO==null)
    {
        ownershipProfileVO=new OwnershipProfileVO();
        ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER4",ownershipProfileDetailsVO_shareholder4);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER4",ownershipProfileDetailsVO_corporateShareholder4);
        ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
        ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
        ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
        ownershipProfileDetailsVOMap.put("DIRECTOR4",ownershipProfileDetailsVO_director4);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY4",ownershipProfileDetailsVO_authorizeSignatory4);
    }

    if(session.getAttribute("validationErrorList")!=null) {validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");}
    else if(request.getAttribute("validationErrorList")!=null) {validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");}
    else {validationErrorList= new ValidationErrorList();}

    /*if(!functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || 100<=(Integer.valueOf(ownershipProfileVO.getNameprincipal1_owned())))
    {
        disableNamePrinciple2="disabled";
        disableNamePrinciple3="disabled";
        disablecolor="background-color:#EBEBE4";
    }
    if((!functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || !functions.isValueNull(ownershipProfileVO.getNameprincipal2_owned())) || ((functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || functions.isValueNull(ownershipProfileVO.getNameprincipal2_owned())) && 100<=(Integer.valueOf(ownershipProfileVO.getNameprincipal1_owned())+Integer.valueOf(ownershipProfileVO.getNameprincipal2_owned()))))
    {
        disableNamePrinciple3="disabled";
        disablecolor="background-color:#EBEBE4";
    }*/

    if(ownershipProfileDetailsVO_shareholder1!=null)
    {
        if (!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned())))
        {
            disableShareHolder2 = "disabled";
            disableShareHolder3 = "disabled";
            disableShareHolder4 = "disabled";
            disablecolor = "background-color:#EBEBE4";
        }
    }

    if(ownershipProfileDetailsVO_shareholder1!=null && ownershipProfileDetailsVO_shareholder2!=null)
    {
        if ((!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_shareholder2.getOwned()))))
        {
            disableShareHolder3 = "disabled";
            disableShareHolder4 = "disabled";
            disablecolor = "background-color:#EBEBE4";
        }
    }

    if(ownershipProfileDetailsVO_shareholder1!=null && ownershipProfileDetailsVO_shareholder2!=null && ownershipProfileDetailsVO_shareholder3!=null)
    {
        if ((!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())|| !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())|| functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_shareholder2.getOwned())+ Integer.valueOf(ownershipProfileDetailsVO_shareholder3.getOwned()))))
        {
            disableShareHolder4 = "disabled";
            disablecolor = "background-color:#EBEBE4";
        }
    }

    //for specific condition
    Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep=new HashMap<Boolean, Set<BankInputName>>();
    Set<BankInputName> ownershipProfileInputList=new HashSet<BankInputName>();

    if(request.getAttribute("fullValidationForStep")!=null)
    {
        fullValidationForStep= (Map<Integer, Map<Boolean, Set<BankInputName>>>) request.getAttribute("fullValidationForStep");
        if(functions.isValueNull(request.getParameter("currentPageNO")) && fullValidationForStep.containsKey(Integer.valueOf(request.getParameter("currentPageNO"))))
        {
            //System.out.println("Inside PageViseFullValidationForStep:::");
            fullPageViseValidationForStep=fullValidationForStep.get(Integer.valueOf(request.getParameter("currentPageNO")));
            //System.out.println("PageViseFullValidationForStep::::"+fullPageViseValidationForStep);
            if(fullPageViseValidationForStep.containsKey(false))
                ownershipProfileInputList.addAll(fullPageViseValidationForStep.get(false));
            if(fullPageViseValidationForStep.containsKey(true))
                ownershipProfileInputList.addAll(fullPageViseValidationForStep.get(true));
        }
    }
    //end
%>
<%--flip style--%>
<%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>--%>
<script>
    $(document).ready(function(){
        $("#flip1").click(function(){
            $("#panel1").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip2").click(function(){
            $("#panel2").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip3").click(function(){
            $("#panel3").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip4").click(function(){
            $("#panel4").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip5").click(function(){
            $("#panel5").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip6").click(function(){
            $("#panel6").slideToggle("slow");
        });
    });
</script>
<script>
    $(document).ready(function(){
        $("#flip7").click(function(){
            $("#panel7").slideToggle("slow");
        });
    });
</script>
<style>
    #panel1, #flip1 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel1 {
        padding: 17px;
        display: none;
    }
</style>
<style>
    #panel2, #flip2 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel2 {
        padding: 17px;
        display: none;
    }
</style>
<style>
    #panel3, #flip3 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;

    }

    #panel3 {
        padding: 17px;
        display: none;
    }
</style>
<style>
    #panel4, #flip4 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel4 {
        padding: 17px;
        display: none;
    }
</style>
<style>
    #panel5, #flip5 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel5 {
        padding: 17px;
        display: none;
    }
</style>
<style>
    #panel6, #flip6 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel6 {
        padding: 17px;
        display: none;
    }
</style>

<style>
    #panel7, #flip7 {
        padding: 5px;
        text-align: center;
        background-color: #e5eecc;
    }

    #panel7 {
        padding: 17px;
        display: none;
    }
</style>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        $('#nameprincipal1_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#nameprincipal1_passportissuedate').datepicker('setEndDate','+10y');
        $('#nameprincipal2_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#nameprincipal2_passportissuedate').datepicker('setEndDate','+10y');
        $('#nameprincipal3_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#nameprincipal3_passportissuedate').datepicker('setEndDate','+10y');
        $('#nameprincipal4_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#nameprincipal4_passportissuedate').datepicker('setEndDate','+10y');
        $('#shareholderprofile1_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#shareholderprofile1_passportissuedate').datepicker('setEndDate','+10y');
        $('#shareholderprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#shareholderprofile2_passportissuedate').datepicker('setEndDate','+10y');
        $('#shareholderprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#shareholderprofile3_passportissuedate').datepicker('setEndDate','+10y');
         $('#shareholderprofile4_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#shareholderprofile4_passportissuedate').datepicker('setEndDate','+10y');
        $('#directorsprofile_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#directorsprofile_passportissuedate').datepicker('setEndDate','+10y');
        $('#directorsprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#directorsprofile2_passportissuedate').datepicker('setEndDate','+10y');
        $('#directorsprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#directorsprofile3_passportissuedate').datepicker('setEndDate','+10y');
         $('#directorsprofile4_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#directorsprofile4_passportissuedate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile_passportissuedate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile2_passportissuedate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile3_passportissuedate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile4_Passportexpirydate').datepicker('setEndDate','+10y');
        $('#authorizedsignatoryprofile4_passportissuedate').datepicker('setEndDate','+10y');
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
    function fillShareholder1(f)
    {   if (f.Shareholder1.checked == true)
    {
        f.shareholderprofile1.value = f.nameprincipal1.value;
        f.shareholderprofile1_title.value = f.nameprincipal1_title.value;
        f.shareholderprofile1_lastname.value = f.nameprincipal1_lastname.value;
        f.shareholderprofile1_owned.value = f.nameprincipal1_owned.value;
        f.shareholderprofile1_address.value = f.nameprincipal1_address.value;
        f.shareholderprofile1_city.value = f.nameprincipal1_city.value;
        f.shareholderprofile1_State.value = f.nameprincipal1_State.value;
        f.shareholderprofile1_zip.value = f.nameprincipal1_zip.value;
        f.shareholderprofile1_country.value = f.nameprincipal1_country.value;
        f.shareholderprofile1_street.value = f.nameprincipal1_street.value;
        f.shareholderprofile1_telnocc1.value = f.nameprincipal1_telnocc1.value;
        f.shareholderprofile1_telephonenumber.value = f.nameprincipal1_telephonenumber.value;
        f.shareholderprofile1_emailaddress.value = f.nameprincipal1_emailaddress.value;
        f.shareholderprofile1_identificationtypeselect.value = f.nameprincipal1_identificationtypeselect.value;
        f.shareholderprofile1_identificationtype.value = f.nameprincipal1_identificationtype.value;
        f.shareholderprofile1_dateofbirth.value = f.nameprincipal1_dateofbirth.value;
        f.shareholderprofile1_nationality.value = f.nameprincipal1_nationality.value;
        f.shareholderprofile1_Passportexpirydate.value = f.nameprincipal1_Passportexpirydate.value;
        f.shareholderprofile1_passportissuedate.value = f.nameprincipal1_passportissuedate.value;

        if(f.politicallyexposed1.value=="Y"){$('input:radio[name=shareholderprofile1_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=shareholderprofile1_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord1.value=="Y"){$('input:radio[name=shareholderprofile1_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=shareholderprofile1_criminalrecord]:checked').val('N');}
    }
    }
    function fillShareholder2(f)
    {if (f.Shareholder2.checked == true)
    {
        f.shareholderprofile2.value = f.nameprincipal2.value;
        f.shareholderprofile2_title.value = f.nameprincipal2_title.value;
        f.shareholderprofile2_lastname.value = f.nameprincipal2_lastname.value;
        f.shareholderprofile2_owned.value = f.nameprincipal2_owned.value;
        f.shareholderprofile2_address.value = f.nameprincipal2_address.value;
        f.shareholderprofile2_city.value = f.nameprincipal2_city.value;
        f.shareholderprofile2_State.value = f.nameprincipal2_State.value;
        f.shareholderprofile2_zip.value = f.nameprincipal2_zip.value;
        f.shareholderprofile2_country.value = f.nameprincipal2_country.value;
        f.shareholderprofile2_street.value = f.nameprincipal2_street.value;
        f.shareholderprofile2_telnocc2.value = f.nameprincipal2_telnocc2.value;
        f.shareholderprofile2_telephonenumber.value = f.nameprincipal2_telephonenumber.value;
        f.shareholderprofile2_emailaddress.value = f.nameprincipal2_emailaddress.value;
        f.shareholderprofile2_identificationtypeselect.value = f.nameprincipal2_identificationtypeselect.value;
        f.shareholderprofile2_identificationtype.value = f.nameprincipal2_identificationtype.value;
        f.shareholderprofile2_dateofbirth.value = f.nameprincipal2_dateofbirth.value;
        f.shareholderprofile2_nationality.value = f.nameprincipal2_nationality.value;
        f.shareholderprofile2_Passportexpirydate.value = f.nameprincipal2_Passportexpirydate.value;
        f.shareholderprofile2_passportissuedate.value = f.nameprincipal2_passportissuedate.value;

        if(f.politicallyexposed2.value=="Y"){$('input:radio[name=shareholderprofile2_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=shareholderprofile2_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord2.value=="Y"){$('input:radio[name=shareholderprofile2_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=shareholderprofile2_criminalrecord]:checked').val('N');}
    }
    }

    function fillShareholder3(f)
    {
        if (f.Shareholder3.checked == true)
        {
            //ADD new check box
            f.shareholderprofile3.value = f.nameprincipal3.value;
            f.shareholderprofile3_title.value = f.nameprincipal3_title.value;
            f.shareholderprofile3_lastname.value = f.nameprincipal3_lastname.value;
            f.shareholderprofile3_owned.value = f.nameprincipal3_owned.value;
            f.shareholderprofile3_address.value = f.nameprincipal3_address.value;
            f.shareholderprofile3_city.value = f.nameprincipal3_city.value;
            f.shareholderprofile3_State.value = f.nameprincipal3_State.value;
            f.shareholderprofile3_zip.value = f.nameprincipal3_zip.value;
            f.shareholderprofile3_country.value = f.nameprincipal3_country.value;
            f.shareholderprofile3_street.value = f.nameprincipal3_street.value;
            f.shareholderprofile3_telnocc2.value = f.nameprincipal3_telnocc1.value;
            f.shareholderprofile3_telephonenumber.value = f.nameprincipal3_telephonenumber.value;
            f.shareholderprofile3_emailaddress.value = f.nameprincipal3_emailaddress.value;
            f.shareholderprofile3_identificationtypeselect.value = f.nameprincipal3_identificationtypeselect.value;
            f.shareholderprofile3_identificationtype.value = f.nameprincipal3_identificationtype.value;
            f.shareholderprofile3_dateofbirth.value = f.nameprincipal3_dateofbirth.value;
            f.shareholderprofile3_nationality.value = f.nameprincipal3_nationality.value;
            f.shareholderprofile3_Passportexpirydate.value = f.nameprincipal3_Passportexpirydate.value;
            f.shareholderprofile3_passportissuedate.value = f.nameprincipal3_passportissuedate.value;

            if (f.politicallyexposed3.value == "Y")
            {
                $('input:radio[name=shareholderprofile3_politicallyexposed]:checked').val('Y');
            }
            else
            {
                $('input:radio[name=shareholderprofile3_politicallyexposed]:checked').val('N');
            }

            if (f.criminalrecord3.value == "Y")
            {
                $('input:radio[name=shareholderprofile3_criminalrecord]:checked').val('Y');
            }
            else
            {
                $('input:radio[name=shareholderprofile3_criminalrecord]:checked').val('N');
            }
        }
    }
        function fillShareholder4(f)
        {if (f.Shareholder4.checked == true)
        {
            //ADD new check box
            f.shareholderprofile4.value = f.nameprincipal4.value;
            f.shareholderprofile4_title.value = f.nameprincipal4_title.value;
            f.shareholderprofile4_lastname.value = f.nameprincipal4_lastname.value;
            f.shareholderprofile4_owned.value = f.nameprincipal4_owned.value;
            f.shareholderprofile4_address.value = f.nameprincipal4_address.value;
            f.shareholderprofile4_city.value = f.nameprincipal4_city.value;
            f.shareholderprofile4_State.value = f.nameprincipal4_State.value;
            f.shareholderprofile4_zip.value = f.nameprincipal4_zip.value;
            f.shareholderprofile4_country.value = f.nameprincipal4_country.value;
            f.shareholderprofile4_street.value = f.nameprincipal4_street.value;
            f.shareholderprofile4_telnocc2.value = f.nameprincipal4_telnocc1.value;
            f.shareholderprofile4_telephonenumber.value = f.nameprincipal4_telephonenumber.value;
            f.shareholderprofile4_emailaddress.value = f.nameprincipal4_emailaddress.value;
            f.shareholderprofile4_identificationtypeselect.value = f.nameprincipal4_identificationtypeselect.value;
            f.shareholderprofile4_identificationtype.value = f.nameprincipal4_identificationtype.value;
            f.shareholderprofile4_dateofbirth.value= f.nameprincipal4_dateofbirth.value;
            f.shareholderprofile4_nationality.value = f.nameprincipal4_nationality.value;
            f.shareholderprofile4_Passportexpirydate.value = f.nameprincipal4_Passportexpirydate.value;
            f.shareholderprofile4_passportissuedate.value = f.nameprincipal4_passportissuedate.value;

            if(f.politicallyexposed4.value=="Y"){$('input:radio[name=shareholderprofile4_politicallyexposed]:checked').val('Y');}
            else {$('input:radio[name=shareholderprofile4_politicallyexposed]:checked').val('N');}

            if(f.criminalrecord4.value=="Y"){$('input:radio[name=shareholderprofile4_criminalrecord]:checked').val('Y');}
            else {$('input:radio[name=shareholderprofile4_criminalrecord]:checked').val('N');}
        }
    }

    function fillDirector1(f)
    {if (f.Director1.checked == true)
    {
        f.directorsprofile.value= f.nameprincipal1.value;
        f.directorsprofile_title.value= f.nameprincipal1_title.value;
        f.directorsprofile_lastname.value= f.nameprincipal1_lastname.value;
        f.directorsprofile_address.value= f.nameprincipal1_address.value;
        f.directorsprofile_city.value= f.nameprincipal1_city.value;
        f.directorsprofile_State.value= f.nameprincipal1_State.value;
        f.directorsprofile_zip.value= f.nameprincipal1_zip.value;
        f.directorsprofile_country.value= f.nameprincipal1_country.value;
        f.directorsprofile_street.value= f.nameprincipal1_street.value;
        f.directorsprofile_telnocc1.value= f.nameprincipal1_telnocc1.value;
        f.directorsprofile_telephonenumber.value= f.nameprincipal1_telephonenumber.value;
        f.directorsprofile_emailaddress.value= f.nameprincipal1_emailaddress.value;
        f.directorsprofile_identificationtypeselect.value= f.nameprincipal1_identificationtypeselect.value;
        f.directorsprofile_identificationtype.value= f.nameprincipal1_identificationtype.value;
        f.directorsprofile_dateofbirth.value= f.nameprincipal1_dateofbirth.value;
        f.directorsprofile_nationality.value= f.nameprincipal1_nationality.value;
        f.directorsprofile_Passportexpirydate.value= f.nameprincipal1_Passportexpirydate.value;
        f.directorsprofile_passportissuedate.value= f.nameprincipal1_passportissuedate.value;

        if(f.politicallyexposed1.value=="Y"){$('input:radio[name=directorsprofile_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord1.value=="Y"){$('input:radio[name=directorsprofile_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile_criminalrecord]:checked').val('N');}
    }
    }
    function fillDirector2(f)
    {if (f.Director2.checked == true)
    {
        f.directorsprofile2.value= f.nameprincipal2.value;
        f.directorsprofile2_title.value= f.nameprincipal2_title.value;
        f.directorsprofile2_lastname.value= f.nameprincipal2_lastname.value;
        f.directorsprofile2_address.value= f.nameprincipal2_address.value;
        f.directorsprofile2_city.value= f.nameprincipal2_city.value;
        f.directorsprofile2_State.value= f.nameprincipal2_State.value;
        f.directorsprofile2_zip.value= f.nameprincipal2_zip.value;
        f.directorsprofile2_country.value= f.nameprincipal2_country.value;
        f.directorsprofile2_street.value= f.nameprincipal2_street.value;
        f.directorsprofile2_telnocc1.value= f.nameprincipal2_telnocc2.value;
        f.directorsprofile2_telephonenumber.value= f.nameprincipal2_telephonenumber.value;
        f.directorsprofile2_emailaddress.value= f.nameprincipal2_emailaddress.value;
        f.directorsprofile2_identificationtypeselect.value= f.nameprincipal2_identificationtypeselect.value;
        f.directorsprofile2_identificationtype.value= f.nameprincipal2_identificationtype.value;
        f.directorsprofile2_dateofbirth.value= f.nameprincipal2_dateofbirth.value;
        f.directorsprofile2_nationality.value= f.nameprincipal2_nationality.value;
        f.directorsprofile2_Passportexpirydate.value= f.nameprincipal2_Passportexpirydate.value;
        f.directorsprofile2_passportissuedate.value= f.nameprincipal2_passportissuedate.value;

        if(f.politicallyexposed2.value=="Y"){$('input:radio[name=directorsprofile2_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile2_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord2.value=="Y"){$('input:radio[name=directorsprofile2_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile2_criminalrecord]:checked').val('N');}
    }
    }
    function fillDirector3(f)
    {if (f.Director3.checked == true)
    {
        f.directorsprofile3.value= f.nameprincipal3.value;
        f.directorsprofile3_title.value= f.nameprincipal3_title.value;
        f.directorsprofile3_lastname.value= f.nameprincipal3_lastname.value;
        f.directorsprofile3_address.value= f.nameprincipal3_address.value;
        f.directorsprofile3_city.value= f.nameprincipal3_city.value;
        f.directorsprofile3_State.value= f.nameprincipal3_State.value;
        f.directorsprofile3_zip.value= f.nameprincipal3_zip.value;
        f.directorsprofile3_country.value= f.nameprincipal3_country.value;
        f.directorsprofile3_street.value= f.nameprincipal3_street.value;
        f.directorsprofile3_telnocc1.value= f.nameprincipal3_telnocc1.value;
        f.directorsprofile3_telephonenumber.value= f.nameprincipal3_telephonenumber.value;
        f.directorsprofile3_emailaddress.value= f.nameprincipal3_emailaddress.value;
        f.directorsprofile3_identificationtypeselect.value= f.nameprincipal3_identificationtypeselect.value;
        f.directorsprofile3_identificationtype.value= f.nameprincipal3_identificationtype.value;
        f.directorsprofile3_dateofbirth.value= f.nameprincipal3_dateofbirth.value;
        f.directorsprofile3_nationality.value= f.nameprincipal3_nationality.value;
        f.directorsprofile3_Passportexpirydate.value= f.nameprincipal3_Passportexpirydate.value;
        f.directorsprofile3_passportissuedate.value= f.nameprincipal3_passportissuedate.value;

        if(f.politicallyexposed3.value=="Y"){$('input:radio[name=directorsprofile3_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile3_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord3.value=="Y"){$('input:radio[name=directorsprofile3_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile3_criminalrecord]:checked').val('N');}
    }
    }
    function fillDirector4(f)
    {if (f.Director4.checked == true)
    {
        f.directorsprofile4.value= f.nameprincipal4.value;
        f.directorsprofile4_title.value= f.nameprincipal4_title.value;
        f.directorsprofile4_lastname.value= f.nameprincipal4_lastname.value;
        f.directorsprofile4_address.value= f.nameprincipal4_address.value;
        f.directorsprofile4_city.value= f.nameprincipal4_city.value;
        f.directorsprofile4_State.value= f.nameprincipal4_State.value;
        f.directorsprofile4_zip.value= f.nameprincipal4_zip.value;
        f.directorsprofile4_country.value= f.nameprincipal4_country.value;
        f.directorsprofile4_street.value= f.nameprincipal4_street.value;
        f.directorsprofile4_telnocc1.value= f.nameprincipal4_telnocc1.value;
        f.directorsprofile4_telephonenumber.value= f.nameprincipal4_telephonenumber.value;
        f.directorsprofile4_emailaddress.value= f.nameprincipal4_emailaddress.value;
        f.directorsprofile4_identificationtypeselect.value= f.nameprincipal4_identificationtypeselect.value;
        f.directorsprofile4_identificationtype.value= f.nameprincipal4_identificationtype.value;
        f.directorsprofile4_dateofbirth.value= f.nameprincipal4_dateofbirth.value;
        f.directorsprofile4_nationality.value= f.nameprincipal4_nationality.value;
        f.directorsprofile4_Passportexpirydate.value= f.nameprincipal4_Passportexpirydate.value;
        f.directorsprofile4_passportissuedate.value= f.nameprincipal4_passportissuedate.value;

        if(f.politicallyexposed4.value=="Y"){$('input:radio[name=directorsprofile4_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile4_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord3.value=="Y"){$('input:radio[name=directorsprofile4_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=directorsprofile4_criminalrecord]:checked').val('N');}
    }
    }
    function fillAuthorize1(f)
    {if (f.Authorize1.checked == true)
    {
        f.authorizedsignatoryprofile.value = f.nameprincipal1.value;
        f.authorizedsignatoryprofile_title.value = f.nameprincipal1_title.value;
        f.authorizedsignatoryprofile_lastname.value = f.nameprincipal1_lastname.value;
        f.authorizedsignatoryprofile_telnocc1.value = f.nameprincipal1_telnocc1.value;
        f.authorizedsignatoryprofile_telephonenumber.value = f.nameprincipal1_telephonenumber.value;
        f.authorizedsignatoryprofile_emailaddress.value = f.nameprincipal1_emailaddress.value;
        f.authorizedsignatoryprofile_dateofbirth.value = f.nameprincipal1_dateofbirth.value;
        f.authorizedsignatoryprofile_identificationtypeselect.value = f.nameprincipal1_identificationtypeselect.value;
        f.authorizedsignatoryprofile_identificationtype.value = f.nameprincipal1_identificationtype.value;
        f.authorizedsignatoryprofile_State.value = f.nameprincipal1_State.value;
        f.authorizedsignatoryprofile_address.value = f.nameprincipal1_address.value;
        f.authorizedsignatoryprofile_city.value = f.nameprincipal1_city.value;
        f.authorizedsignatoryprofile_zip.value = f.nameprincipal1_zip.value;
        f.authorizedsignatoryprofile_country.value = f.nameprincipal1_country.value;
        f.authorizedsignatoryprofile_street.value = f.nameprincipal1_street.value;
        f.authorizedsignatoryprofile_nationality.value = f.nameprincipal1_nationality.value;
        f.authorizedsignatoryprofile_Passportexpirydate.value = f.nameprincipal1_Passportexpirydate.value;
        f.authorizedsignatoryprofile_passportissuedate.value = f.nameprincipal1_passportissuedate.value;

        if(f.politicallyexposed1.value=="Y"){$('input:radio[name=authorizedsignatoryprofile1_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile1_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord1.value=="Y"){$('input:radio[name=authorizedsignatoryprofile1_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile1_criminalrecord]:checked').val('N');}
    }
    }
    function fillAuthorize2(f)
    {if (f.Authorize2.checked == true)
    {
        f.authorizedsignatoryprofile2.value = f.nameprincipal2.value;
        f.authorizedsignatoryprofile2_title.value = f.nameprincipal2_title.value;
        f.authorizedsignatoryprofile2_lastname.value = f.nameprincipal2_lastname.value;
        f.authorizedsignatoryprofile2_telnocc1.value = f.nameprincipal2_telnocc2.value;
        f.authorizedsignatoryprofile2_telephonenumber.value = f.nameprincipal2_telephonenumber.value;
        f.authorizedsignatoryprofile2_emailaddress.value = f.nameprincipal2_emailaddress.value;
        f.authorizedsignatoryprofile2_dateofbirth.value = f.nameprincipal2_dateofbirth.value;
        f.authorizedsignatoryprofile2_identificationtypeselect.value = f.nameprincipal2_identificationtypeselect.value;
        f.authorizedsignatoryprofile2_identificationtype.value = f.nameprincipal2_identificationtype.value;
        f.authorizedsignatoryprofile2_State.value = f.nameprincipal2_State.value;
        f.authorizedsignatoryprofile2_address.value = f.nameprincipal2_address.value;
        f.authorizedsignatoryprofile2_city.value = f.nameprincipal2_city.value;
        f.authorizedsignatoryprofile2_zip.value = f.nameprincipal2_zip.value;
        f.authorizedsignatoryprofile2_country.value = f.nameprincipal2_country.value;
        f.authorizedsignatoryprofile2_street.value = f.nameprincipal2_street.value;
        f.authorizedsignatoryprofile2_nationality.value = f.nameprincipal2_nationality.value;
        f.authorizedsignatoryprofile2_Passportexpirydate.value = f.nameprincipal2_Passportexpirydate.value;
        f.authorizedsignatoryprofile2_passportissuedate.value = f.nameprincipal2_passportissuedate.value;

        if(f.politicallyexposed2.value=="Y"){$('input:radio[name=authorizedsignatoryprofile2_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile2_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord2.value=="Y"){$('input:radio[name=authorizedsignatoryprofile2_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile2_criminalrecord]:checked').val('N');}
    }
    }
    function fillAuthorize3(f)
    {if (f.Authorize3.checked == true)
    {
        f.authorizedsignatoryprofile3.value = f.nameprincipal3.value;
        f.authorizedsignatoryprofile3_title.value = f.nameprincipal3_title.value;
        f.authorizedsignatoryprofile3_lastname.value = f.nameprincipal3_lastname.value;
        f.authorizedsignatoryprofile3_telnocc1.value = f.nameprincipal3_telnocc1.value;
        f.authorizedsignatoryprofile3_telephonenumber.value = f.nameprincipal3_telephonenumber.value;
        f.authorizedsignatoryprofile3_emailaddress.value = f.nameprincipal3_emailaddress.value;
        f.authorizedsignatoryprofile3_dateofbirth.value = f.nameprincipal3_dateofbirth.value;
        f.authorizedsignatoryprofile3_identificationtypeselect.value = f.nameprincipal3_identificationtypeselect.value;
        f.authorizedsignatoryprofile3_identificationtype.value = f.nameprincipal3_identificationtype.value;
        f.authorizedsignatoryprofile3_State.value = f.nameprincipal3_State.value;
        f.authorizedsignatoryprofile3_address.value = f.nameprincipal3_address.value;
        f.authorizedsignatoryprofile3_city.value = f.nameprincipal3_city.value;
        f.authorizedsignatoryprofile3_zip.value = f.nameprincipal3_zip.value;
        f.authorizedsignatoryprofile3_country.value = f.nameprincipal3_country.value;
        f.authorizedsignatoryprofile3_street.value = f.nameprincipal3_street.value;
        f.authorizedsignatoryprofile3_nationality.value = f.nameprincipal3_nationality.value;
        f.authorizedsignatoryprofile3_Passportexpirydate.value = f.nameprincipal3_Passportexpirydate.value;
        f.authorizedsignatoryprofile3_passportissuedate.value = f.nameprincipal3_passportissuedate.value;

        if(f.politicallyexposed3.value=="Y"){$('input:radio[name=authorizedsignatoryprofile3_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile3_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord3.value=="Y"){$('input:radio[name=authorizedsignatoryprofile3_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile3_criminalrecord]:checked').val('N');}
    }
    }

    function fillAuthorize4(f)
    {if (f.Authorize4.checked == true)
    {
        f.authorizedsignatoryprofile4.value = f.nameprincipal4.value;
        f.authorizedsignatoryprofile4_title.value = f.nameprincipal4_title.value;
        f.authorizedsignatoryprofile4_lastname.value = f.nameprincipal4_lastname.value;
        f.authorizedsignatoryprofile4_telnocc1.value = f.nameprincipal4_telnocc1.value;
        f.authorizedsignatoryprofile4_telephonenumber.value = f.nameprincipal4_telephonenumber.value;
        f.authorizedsignatoryprofile4_emailaddress.value = f.nameprincipal4_emailaddress.value;
        f.authorizedsignatoryprofile4_dateofbirth.value = f.nameprincipal4_dateofbirth.value;
        f.authorizedsignatoryprofile4_identificationtypeselect.value = f.nameprincipal4_identificationtypeselect.value;
        f.authorizedsignatoryprofile4_identificationtype.value = f.nameprincipal4_identificationtype.value;
        f.authorizedsignatoryprofile4_State.value = f.nameprincipal4_State.value;
        f.authorizedsignatoryprofile4_address.value = f.nameprincipal4_address.value;
        f.authorizedsignatoryprofile4_city.value = f.nameprincipal4_city.value;
        f.authorizedsignatoryprofile4_zip.value = f.nameprincipal4_zip.value;
        f.authorizedsignatoryprofile4_country.value = f.nameprincipal4_country.value;
        f.authorizedsignatoryprofile4_street.value = f.nameprincipal4_street.value;
        f.authorizedsignatoryprofile4_nationality.value = f.nameprincipal4_nationality.value;
        f.authorizedsignatoryprofile4_Passportexpirydate.value = f.nameprincipal4_Passportexpirydate.value;
        f.authorizedsignatoryprofile4_passportissuedate.value = f.nameprincipal4_passportissuedate.value;

        if(f.politicallyexposed4.value=="Y"){$('input:radio[name=authorizedsignatoryprofile4_politicallyexposed]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile4_politicallyexposed]:checked').val('N');}

        if(f.criminalrecord4.value=="Y"){$('input:radio[name=authorizedsignatoryprofile4_criminalrecord]:checked').val('Y');}
        else {$('input:radio[name=authorizedsignatoryprofile4_criminalrecord]:checked').val('N');}
    }
    }
</script>
<div class="container-fluid ">
    <div class="row" style="margin-top: 100px;margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <br><br>


            <%--Shareholders information--%>
            <%
                if(ownershipProfileInputList.contains(BankInputName.shareholderprofile1_title) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_owned) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_address) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_city) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_zip) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_country) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_street) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_State) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile1_criminalrecord) ||

                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_title) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_owned) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_address) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_city) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_zip) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_country) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_street) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_telnocc2) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_State) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile2_criminalrecord) ||

                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_title) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_owned) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_address) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_city) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_zip) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_country) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_street) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_telnocc2) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_State) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile3_criminalrecord) ||

                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_title) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_owned) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_address) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_city) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_zip) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_country) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_street) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_telnocc2) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_State) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.shareholderprofile4_criminalrecord) ||

                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Name) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_RegNumber) ||
//                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_dateofregistration) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Address) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_City) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_State) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_ZipCode) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Country) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Street) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtype) ||

                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Name) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_RegNumber) ||
//                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_dateofregistration) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Address) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_City) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_State) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_ZipCode) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Country) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Street) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtype) ||

                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Name) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_RegNumber) ||
//                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_dateofregistration) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Address) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_City) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_State) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_ZipCode) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Street) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Country) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtype) ||

                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Name) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_RegNumber) ||
//                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_dateofregistration) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Address) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_City) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_State) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_ZipCode) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Street) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Country) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.corporateshareholder4_identificationtype) ||view)
                {
            %>
            <div class="row" style="margin-left: 25px;">
                <div class="col-lg-12">
                    <div class="panel panel-default" style="margin-top: 0px">
                        <div class="panel-heading" id="flip5" style="background-color: #9fabb7">
                            Shareholder Profile and Corporate Shareholder Profile(% owned must be equal to 25% or more)
                            <div style="margin-left: 90%;margin-top: -15px;">
                                <img src="/merchant/images/down_Arrow.jpg" border="0" style="width:28%">
                            </div>
                        </div>


                        <br>
                        <div id="panel5" style="background-color: #ffffff">

                            <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>">
                                <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Shareholder Profile after entering the data provided"%>
                            </h2></center>
                            <br>
                            <div class="form-group col-md-8 has-feedback">
                                <label for="numOfShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">How many natural person (not corporate) own more than 25% of the shares in your business?</label>
                            </div>
                            <div class="form-group col-md-3 has-feedback">
                                <input type="text" class="form-control"  id="numOfShareholders" name="numOfShareholders" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfShareholders())?ownershipProfileVO.getNumOfShareholders():""%>"/><%if(validationErrorList.getError("numOfShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top:0!important;display: block;color:#a94442"></i><%}%>
                            </div>
                            <%--individual shareholder--%>

                            <div class="row" style="margin-left:27px">
                                <div class="col-lg-12">
                                    <div class="panel panel-default" style="margin-top: 0px" >
                                        <div class="panel-heading" id="flip6" style="background-color: #9fabb7">
                                            Shareholder Profile and Corporate Shareholder Profile(% owned must be equal to 25% or more)
                                            <div style="margin-left: 90%;margin-top: -15px;">
                                                <img src="/merchant/images/down_Arrow.jpg" border="0" style="width:28%">
                                            </div>
                                        </div>
                                        <br>
                                        <div id="panel6" style="background-color: #ffffff">
                                            <div class="container-fluid " style="padding-left: 0px;adding-right: 0px;">
                                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Individual Shareholder Profile 1</h2>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                                            <select class="form-control" name="shareholderprofile1_title" <%=globaldisabled%>>
                                                                <option value="">Select Title</option>
                                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>MR</option>
                                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>MRS</option>
                                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>MS</option>
                                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>MISS</option>
                                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>MASTER</option>
                                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder1.getTitle())==true?"selected":""%>>DR</option>

                                                            </select> <%if(validationErrorList.getError("shareholderprofile1_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1" name="shareholderprofile1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getFirstname()) ? ownershipProfileDetailsVO_shareholder1.getFirstname():""%>" /><%if(validationErrorList.getError("shareholderprofile1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile1_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_lastname" name="shareholderprofile1_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getLastname()) ? ownershipProfileDetailsVO_shareholder1.getLastname():""%>" /><%if(validationErrorList.getError("shareholderprofile1_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile1_dateofbirth" name="shareholderprofile1_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"<%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile1_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_telnocc1" name="shareholderprofile1_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telnocc1")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile1_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_telephonenumber" name="shareholderprofile1_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_emailaddress" name="shareholderprofile1_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder1.getEmailaddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_owned" name="shareholderprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) ? ownershipProfileDetailsVO_shareholder1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder('shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',1)"/>

                                                            <%if(validationErrorList.getError("shareholderprofile1_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg">Invalid Percentage Holding</span><%}%>
                                                            <br><span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile1_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="shareholderprofile1_addressproof" id="shareholderprofile1_addressproof"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressProof()) ? ownershipProfileDetailsVO_shareholder1.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_addressId" name="shareholderprofile1_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressId()) ? ownershipProfileDetailsVO_shareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("shareholderprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_address" name="shareholderprofile1_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddress()) ? ownershipProfileDetailsVO_shareholder1.getAddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_street" name="shareholderprofile1_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getStreet()) ? ownershipProfileDetailsVO_shareholder1.getStreet():""%>" /> <%if(validationErrorList.getError("shareholderprofile1_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_city" name="shareholderprofile1_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCity()) ? ownershipProfileDetailsVO_shareholder1.getCity():""%>"/><%if(validationErrorList.getError("shareholderprofile1_city")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_State" name="shareholderprofile1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getState()) ? ownershipProfileDetailsVO_shareholder1.getState():""%>"/><%if(validationErrorList.getError("shareholderprofile1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                                            <select  name="shareholderprofile1_country" onchange="myjunk1('shareholderprofile1_country','shareholderprofile1_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCountry()) ? ownershipProfileDetailsVO_shareholder1.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile1_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_zip" name="shareholderprofile1_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getZipcode()) ? ownershipProfileDetailsVO_shareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("shareholderprofile1_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                                            <select class="form-control" name="shareholderprofile1_identificationtypeselect" id="shareholderprofile1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile1_identificationtype" name="shareholderprofile1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                                            <select  class="form-control" id="shareholderprofile1_nationality" name="shareholderprofile1_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"   <%=globaldisabled%>>
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getNationality()) ? ownershipProfileDetailsVO_shareholder1.getNationality():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile1_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile1_passportissuedate" name="shareholderprofile1_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile1_Passportexpirydate" name="shareholderprofile1_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label <%--for="shareholderprofile1_politicallyexposed"--%> style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile1_politicallyexposedY" name="shareholderprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile1_politicallyexposedN" name="shareholderprofile1_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile1_criminalrecord" name="shareholderprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile1_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                                        </div>

                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Individual Shareholder Profile 2</h2>

                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                                            <select class="form-control" name="shareholderprofile2_title" <%=disableShareHolder2%> <%=globaldisabled%>>
                                                                <option value="">Select Title</option>
                                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>MR</option>
                                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>MRS</option>
                                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>MS</option>
                                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>MISS</option>
                                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>MASTER</option>
                                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder2.getTitle())==true?"selected":""%>>DR</option>

                                                            </select> <%if(validationErrorList.getError("shareholderprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2" name="shareholderprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getFirstname()) ? ownershipProfileDetailsVO_shareholder2.getFirstname():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_lastname" name="shareholderprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getLastname()) ? ownershipProfileDetailsVO_shareholder2.getLastname():""%>" <%=disableShareHolder2%> /><%if(validationErrorList.getError("shareholderprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile2_dateofbirth" name="shareholderprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getDateofbirth()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile2_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_telnocc2" name="shareholderprofile2_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder2.getTelnocc1():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_telephonenumber" name="shareholderprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%> <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder2.getTelephonenumber():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_emailaddress" name="shareholderprofile2_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder2.getEmailaddress():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                                            <input type="text" class="form-control"   name="shareholderprofile2_owned" id="shareholderprofile2_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned()) ? ownershipProfileDetailsVO_shareholder2.getOwned():""%>" onkeypress="return isNumberKey(event)"  onchange="return myShareholder('shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',2)" <%=disableShareHolder2%>/>

                                                            <%if(validationErrorList.getError("shareholderprofile2_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg">Invalid Percentage Holding</span><%}%>
                                                            <%--<span style="margin-top: 0px;" class="errormesage"><%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))||validationErrorList.getError("shareholderprofile2_owned")!=null?"background-color: #f2dede;":""%> <%=validationErrorList.getError("shareholderplus_owned")==null ?(validationErrorList.getError("shareholderprofile2_owned")!=null?"Invalid owned":""):(functions.isValueNull(request.getParameter("shareholderprofile2_owned"))? validationErrorList.getError("shareholderplus_owned").getLogMessage():"")%></span>--%>
                                                            <br><span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile2_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="shareholderprofile2_addressproof" id="shareholderprofile2_addressproof"class="txtbox" <%=globaldisabled%> <%=disableShareHolder2%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressProof()) ? ownershipProfileDetailsVO_shareholder2.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_addressId" name="shareholderprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressId()) ? ownershipProfileDetailsVO_shareholder2.getAddressId():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_address" name="shareholderprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddress()) ? ownershipProfileDetailsVO_shareholder2.getAddress():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_street" name="shareholderprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getStreet()) ? ownershipProfileDetailsVO_shareholder2.getStreet():""%>" <%=disableShareHolder2%> /> <%if(validationErrorList.getError("shareholderprofile2_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_city" name="shareholderprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCity()) ? ownershipProfileDetailsVO_shareholder2.getCity():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_State" name="shareholderprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getState()) ? ownershipProfileDetailsVO_shareholder2.getState():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                                            <select onchange="myjunk1('shareholderprofile2_country','shareholderprofile2_telnocc2');"  class="form-control" name="shareholderprofile2_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():""%>" <%=disableShareHolder2%> /> <%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><%}%>
                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_zip" name="shareholderprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getZipcode()) ? ownershipProfileDetailsVO_shareholder2.getZipcode():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                                            <select class="form-control" name="shareholderprofile2_identificationtypeselect" id="shareholderprofile2_identificationtypeselect" class="txtbox" <%=globaldisabled%> <%=disableShareHolder2%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile2_identificationtype" name="shareholderprofile2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtype():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                                            <select  class="form-control" id="shareholderprofile2_nationality" name="shareholderprofile2_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=disableShareHolder2%>   <%=globaldisabled%>>
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getNationality()) ? ownershipProfileDetailsVO_shareholder2.getNationality():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile2_passportissuedate" name="shareholderprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile2_Passportexpirydate" name="shareholderprofile2_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile2_politicallyexposed" name="shareholderprofile2_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile2_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile2_criminalrecord" name="shareholderprofile2_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile2_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                                        </div>

                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Individual Shareholder Profile 3</h2>

                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                                            <select class="form-control" name="shareholderprofile3_title" <%=globaldisabled%> <%=disableShareHolder3%>>
                                                                <option value="">Select Title</option>
                                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>MR</option>
                                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>MRS</option>
                                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>MS</option>
                                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>MISS</option>
                                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>MASTER</option>
                                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder3.getTitle())==true?"selected":""%>>DR</option>

                                                            </select> <%if(validationErrorList.getError("shareholderprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3" name="shareholderprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getFirstname()) ? ownershipProfileDetailsVO_shareholder3.getFirstname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_lastname" name="shareholderprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getLastname()) ? ownershipProfileDetailsVO_shareholder3.getLastname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile3_dateofbirth" name="shareholderprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getDateofbirth()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile3_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_telnocc2" name="shareholderprofile3_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder3.getTelnocc1():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_telephonenumber" name="shareholderprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder3.getTelephonenumber():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_emailaddress" name="shareholderprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder3.getEmailaddress():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                                            <input type="text" align="center" class="form-control" name="shareholderprofile3_owned" id="shareholderprofile3_owned" <%=globaldisabled%> <%=disableShareHolder3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned()) ? ownershipProfileDetailsVO_shareholder3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder('shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',3)" <%=disableShareHolder3%>/>

                                                            <%if(validationErrorList.getError("shareholderprofile3_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg">Invalid Percentage Holding</span><%}%>
                                                            <br><span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile3_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                            <%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="shareholderprofile3_addressproof" id="shareholderprofile3_addressproof"class="txtbox" <%=globaldisabled%> <%=disableShareHolder3%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressProof()) ? ownershipProfileDetailsVO_shareholder3.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_addressId" name="shareholderprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressId()) ? ownershipProfileDetailsVO_shareholder3.getAddressId():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_address" name="shareholderprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddress()) ? ownershipProfileDetailsVO_shareholder3.getAddress():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_street" name="shareholderprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getStreet()) ? ownershipProfileDetailsVO_shareholder3.getStreet():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile3_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_city" name="shareholderprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCity()) ? ownershipProfileDetailsVO_shareholder3.getCity():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_State" name="shareholderprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getState()) ? ownershipProfileDetailsVO_shareholder3.getState():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                                            <select onchange="myjunk1('shareholderprofile3_country','shareholderprofile3_telnocc2');"  class="form-control"  name="shareholderprofile3_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_zip" name="shareholderprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getZipcode()) ? ownershipProfileDetailsVO_shareholder3.getZipcode():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                                            <select class="form-control" name="shareholderprofile3_identificationtypeselect" id="shareholderprofile3_identificationtypeselect" class="form-control" <%=globaldisabled%> <%=disableShareHolder3%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile3_identificationtype" name="shareholderprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtype():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                                            <select  class="form-control" id="shareholderprofile3_nationality" name="shareholderprofile3_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=disableShareHolder3%>   <%=globaldisabled%>>
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getNationality()) ? ownershipProfileDetailsVO_shareholder3.getNationality():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile3_passportissuedate" name="shareholderprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile3_Passportexpirydate" name="shareholderprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile3_politicallyexposed" name="shareholderprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile3_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile3_criminalrecord" name="shareholderprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile3_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                                        </div>

                                                        <!--shareholder 4-->

                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Individual Shareholder Profile 4</h2>

                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                                            <select class="form-control" name="shareholderprofile4_title" <%=globaldisabled%> <%=disableShareHolder4%>>
                                                                <option value="">Select Title</option>
                                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>MR</option>
                                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>MRS</option>
                                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>MS</option>
                                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>MISS</option>
                                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>MASTER</option>
                                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder4.getTitle())==true?"selected":""%>>DR</option>

                                                            </select> <%if(validationErrorList.getError("shareholderprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4" name="shareholderprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getFirstname()) ? ownershipProfileDetailsVO_shareholder4.getFirstname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-3 has-feedback">
                                                            <label for="shareholderprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_lastname" name="shareholderprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getLastname()) ? ownershipProfileDetailsVO_shareholder4.getLastname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile4_dateofbirth" name="shareholderprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getDateofbirth()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile4_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_telnocc2" name="shareholderprofile4_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder4.getTelnocc1():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-2 has-feedback">
                                                            <label for="shareholderprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_telephonenumber" name="shareholderprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder4.getTelephonenumber():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_emailaddress" name="shareholderprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder4.getEmailaddress():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                                            <input type="text" align="center" class="form-control" name="shareholderprofile4_owned" id="shareholderprofile4_owned" <%=globaldisabled%> <%=disableShareHolder4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getOwned()) ? ownershipProfileDetailsVO_shareholder4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder('shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',4)" <%=disableShareHolder4%>/>

                                                            <%if(validationErrorList.getError("shareholderprofile4_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg">Invalid Percentage Holding</span><%}%>
                                                            <br><span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile4_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                            <%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="shareholderprofile4_addressproof" id="shareholderprofile4_addressproof"class="txtbox" <%=globaldisabled%> <%=disableShareHolder4%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddressProof()) ? ownershipProfileDetailsVO_shareholder4.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_addressId" name="shareholderprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddressId()) ? ownershipProfileDetailsVO_shareholder4.getAddressId():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_address" name="shareholderprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddress()) ? ownershipProfileDetailsVO_shareholder4.getAddress():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_street" name="shareholderprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getStreet()) ? ownershipProfileDetailsVO_shareholder4.getStreet():""%>" <%=disableShareHolder4%> /> <%if(validationErrorList.getError("shareholderprofile4_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_city" name="shareholderprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCity()) ? ownershipProfileDetailsVO_shareholder4.getCity():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_State" name="shareholderprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getState()) ? ownershipProfileDetailsVO_shareholder4.getState():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                                            <select onchange="myjunk1('shareholderprofile4_country','shareholderprofile4_telnocc2');"  class="form-control"  name="shareholderprofile4_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():""%>" <%=disableShareHolder4%> /> <%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_zip" name="shareholderprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getZipcode()) ? ownershipProfileDetailsVO_shareholder4.getZipcode():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                                            <select class="form-control" name="shareholderprofile4_identificationtypeselect" id="shareholderprofile4_identificationtypeselect" class="form-control" <%=globaldisabled%> <%=disableShareHolder4%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("shareholderprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                                            <input type="text" class="form-control"  id="shareholderprofile4_identificationtype" name="shareholderprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtype():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                                            <select  class="form-control" id="shareholderprofile4_nationality" name="shareholderprofile4_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=disableShareHolder4%>   <%=globaldisabled%>>
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getNationality()) ? ownershipProfileDetailsVO_shareholder4.getNationality():"")%>
                                                            </select><%if(validationErrorList.getError("shareholderprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile4_passportissuedate" name="shareholderprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="shareholderprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                                            <input type="text" class="form-control datepicker"  id="shareholderprofile4_Passportexpirydate" name="shareholderprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile4_politicallyexposed" name="shareholderprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile4_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                                        </div>
                                                        <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                            <label for="shareholderprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                                            &nbsp;&nbsp;<input type="radio" id="shareholderprofile4_criminalrecord" name="shareholderprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                                            &nbsp;&nbsp;<input type="radio" name="shareholderprofile4_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row" style="margin-left:27px">
                                <div class="col-lg-12">
                                    <div class="panel panel-default" style="margin-top: 0px" >
                                        <div class="panel-heading" id="flip7" style="background-color: #9fabb7">
                                            Corporate Shareholder Profile
                                            <div style="margin-left: 90%;margin-top: -15px;">
                                                <img src="/merchant/images/down_Arrow.jpg" border="0" style="width:28%">
                                            </div>
                                        </div>

                                        <br>
                                        <div id="panel7" style="background-color: #ffffff">

                                            <div class="form-group col-md-8 has-feedback">
                                                <label for="numOfCorporateShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">How many Corporate Shareholders do you have that more than 25% in your business?</label>
                                            </div>
                                            <div class="form-group col-md-3 has-feedback">
                                                <input type="text" class="form-control"  id="numOfCorporateShareholders" name="numOfCorporateShareholders" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders())?ownershipProfileVO.getNumOfCorporateShareholders():""%>"/><%if(validationErrorList.getError("numOfCorporateShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top:0!important;display: block;color:#a94442"></i><%}%>
                                            </div>

                                            <div class="container-fluid ">
                                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Corporate Shareholder Profile 1</h2>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Corporate Shareholder 1</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_Name" name="corporateshareholder1_Name" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getName()) ? ownershipProfileDetailsVO_corporateShareholder1.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder1_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_RegNumber" name="corporateshareholder1_RegNumber" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder1_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_owned" name="corporateshareholder1_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder1.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder1_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="corporateshareholder1_addressproof" id="corporateshareholder1_addressproof"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_addressId" name="corporateshareholder1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_Address" name="corporateshareholder1_Address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_Street" name="corporateshareholder1_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder1.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_City" name="corporateshareholder1_City" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCity()) ? ownershipProfileDetailsVO_corporateShareholder1.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder1_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_State" name="corporateshareholder1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getState()) ? ownershipProfileDetailsVO_corporateShareholder1.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                                            <select  name="corporateshareholder1_Country" class="form-control" <%=globaldisabled%> >
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder1.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("corporateshareholder1_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_ZipCode" name="corporateshareholder1_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder1_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                                            <select class="form-control" name="corporateshareholder1_identificationtypeselect" id="corporateshareholder1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder1_identificationtype" name="corporateshareholder1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="container-fluid ">
                                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Corporate Shareholder Profile 2</h2>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Corporate Shareholder 2</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_Name" name="corporateshareholder2_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getName()) ? ownershipProfileDetailsVO_corporateShareholder2.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder2_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_RegNumber" name="corporateshareholder2_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder2_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_owned" name="corporateshareholder2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder2.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder2_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="corporateshareholder2_addressproof" id="corporateshareholder2_addressproof"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_addressId" name="corporateshareholder2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_Address" name="corporateshareholder2_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_Street" name="corporateshareholder2_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder2.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_City" name="corporateshareholder2_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCity()) ? ownershipProfileDetailsVO_corporateShareholder2.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder2_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_State" name="corporateshareholder2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getState()) ? ownershipProfileDetailsVO_corporateShareholder2.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                                            <select  name="corporateshareholder2_Country" class="form-control" <%=globaldisabled%> >
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder2.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("corporateshareholder2_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_ZipCode" name="corporateshareholder2_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder2.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder2_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12"> <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Details :: </u></label></div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                                            <select class="form-control" name="corporateshareholder2_identificationtypeselect" id="corporateshareholder2_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder2_identificationtype" name="corporateshareholder2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>


                                            <div class="container-fluid ">
                                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Corporate Shareholder Profile 3</h2>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Corporate Shareholder 3</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_Name" name="corporateshareholder3_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getName()) ? ownershipProfileDetailsVO_corporateShareholder3.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder3_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_RegNumber" name="corporateshareholder3_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder3_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <%--<div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_dateofregistration" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Date</label>
                                                            <input type="text" class="form-control datepicker"  id="corporateshareholder3_dateofregistration" name="corporateshareholder3_dateofregistration"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getCorporateshareholder3_dateofregistration())==true?commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileVO.getCorporateshareholder3_dateofregistration()):""%>"/><%if(validationErrorList.getError("corporateshareholder3_dateofregistration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>--%>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_owned" name="corporateshareholder3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder3.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder3_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="corporateshareholder3_addressproof" id="corporateshareholder3_addressproof"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_addressId" name="corporateshareholder3_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_Address" name="corporateshareholder3_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_Street" name="corporateshareholder3_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder3.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_City" name="corporateshareholder3_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCity()) ? ownershipProfileDetailsVO_corporateShareholder3.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder3_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_State" name="corporateshareholder3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getState()) ? ownershipProfileDetailsVO_corporateShareholder3.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label >
                                                            <select  name="corporateshareholder3_Country" class="form-control" <%=globaldisabled%> >
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder3.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("corporateshareholder3_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_ZipCode" name="corporateshareholder3_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder3.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder3_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12"> <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Details :: </u></label></div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                                            <select class="form-control" name="corporateshareholder3_identificationtypeselect" id="corporateshareholder3_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder3_identificationtype" name="corporateshareholder3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <!--corporate shareholder 4-->

                                            <div class="container-fluid ">
                                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Corporate Shareholder Profile 4</h2>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Corporate Shareholder 4</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_Name" name="corporateshareholder4_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getName()) ? ownershipProfileDetailsVO_corporateShareholder4.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder4_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_RegNumber" name="corporateshareholder4_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder4_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <%--<div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder3_dateofregistration" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Date</label>
                                                            <input type="text" class="form-control datepicker"  id="corporateshareholder3_dateofregistration" name="corporateshareholder3_dateofregistration"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getCorporateshareholder3_dateofregistration())==true?commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileVO.getCorporateshareholder3_dateofregistration()):""%>"/><%if(validationErrorList.getError("corporateshareholder3_dateofregistration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>--%>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_owned" name="corporateshareholder4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder4.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder4_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12">
                                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                                            <select class="form-control" name="corporateshareholder4_addressproof" id="corporateshareholder4_addressproof"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressProof() : ""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_addressId" name="corporateshareholder4_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_Address" name="corporateshareholder4_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_Street" name="corporateshareholder4_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder4.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_City" name="corporateshareholder4_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCity()) ? ownershipProfileDetailsVO_corporateShareholder4.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder4_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_State" name="corporateshareholder4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getState()) ? ownershipProfileDetailsVO_corporateShareholder4.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label >
                                                            <select  name="corporateshareholder4_Country" class="form-control" <%=globaldisabled%> >
                                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder4.getCountry():"")%>
                                                            </select><%if(validationErrorList.getError("corporateshareholder4_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_ZipCode" name="corporateshareholder4_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder4.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder4_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>

                                                        <div class="form-group col-md-12"> <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Details :: </u></label></div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                                            <select class="form-control" name="corporateshareholder4_identificationtypeselect" id="corporateshareholder4_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                <%
                                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect():""));
                                                                %>
                                                            </select>
                                                            <%if(validationErrorList.getError("corporateshareholder4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                        <div class="form-group col-md-4 has-feedback">
                                                            <label for="corporateshareholder4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                                            <input type="text" class="form-control"  id="corporateshareholder4_identificationtype" name="corporateshareholder4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(ownershipProfileInputList.contains(BankInputName.directorsprofile_title)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_address) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_city) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_zip) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_country) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_street) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_identificationtypeselect)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_State) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_addressId) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile_owned) ||

                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_title) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_address) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_city) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_zip) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_country) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_street) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_State) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_addressId) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile2_owned) ||

                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_title)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_address) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_city) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_zip) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_country) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_street) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_telephonenumber)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_State) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_owned) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile3_addressId) ||

                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_title)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_address) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_city) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_zip) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_country) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_street) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_telephonenumber)||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_State) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_owned) ||
                        ownershipProfileInputList.contains(BankInputName.directorsprofile4_addressId) ||view)
                {
            %>

            <%--Directors Profile--%>
            <div class="row" style="margin-left: 25px;">
                <div class="col-lg-12">
                    <div class="panel panel-default" style="margin-top: 0px" >
                        <div class="panel-heading" id="flip3" style="background-color: #9fabb7;">
                            Directors Profile
                            <div style="margin-left: 90%;margin-top: -15px;">
                                <img src="/merchant/images/down_Arrow.jpg" border="0" style="width:28%">
                            </div>
                        </div>

                        <br>
                        <div id="panel3" style="background-color: #ffffff">
                            <div class="container-fluid ">

                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">


                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                        <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>">
                                            <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Directors Profile after entering the data provided"%></h2></center>

                                        <br>
                                        <div class="form-group col-md-9 has-feedback">
                                            <label for="numOfDirectors" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">How many Directors do you have?</label>
                                        </div>
                                        <div class="form-group col-md-3 has-feedback">
                                            <input type="text" class="form-control"  id="numOfDirectors" name="numOfDirectors" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfDirectors())?ownershipProfileVO.getNumOfDirectors():""%>"/><%if(validationErrorList.getError("numOfDirectors")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top:0!important;display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Directors Profile 1</h2>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                            <select class="form-control" name="directorsprofile_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director1.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("directorsprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                            <input type="text" class="form-control"  id="directorsprofile" name="directorsprofile"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getFirstname()) ? ownershipProfileDetailsVO_director1.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_lastname" name="directorsprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getLastname()) ? ownershipProfileDetailsVO_director1.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile_dateofbirth" name="directorsprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_telnocc1" name="directorsprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelnocc1()) ? ownershipProfileDetailsVO_director1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_telephonenumber" name="directorsprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelephonenumber()) ? ownershipProfileDetailsVO_director1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_emailaddress" name="directorsprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getEmailaddress()) ? ownershipProfileDetailsVO_director1.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_owned" name="directorsprofile_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned())==true?ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned():""%>" /><%if(validationErrorList.getError("directorsprofile_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="directorsprofile_addressproof" id="directorsprofile_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressProof()) ? ownershipProfileDetailsVO_director1.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile_addressId" name="directorsprofile_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressId()) ? ownershipProfileDetailsVO_director1.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_address" name="directorsprofile_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddress()) ? ownershipProfileDetailsVO_director1.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_street" name="directorsprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getStreet()) ? ownershipProfileDetailsVO_director1.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="directorsprofile_city" name="directorsprofile_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getCity()) ? ownershipProfileDetailsVO_director1.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile_State" name="directorsprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getState()) ? ownershipProfileDetailsVO_director1.getState():""%>"/><%if(validationErrorList.getError("directorsprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                            <select  name="directorsprofile_country" onchange="myjunk1('directorsprofile_country','directorsprofile_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getCountry()) ? ownershipProfileDetailsVO_director1.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile_zip" name="directorsprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getZipcode()) ? ownershipProfileDetailsVO_director1.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                            <select class="form-control" name="directorsprofile_identificationtypeselect" id="directorsprofile_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director1.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                            <input type="text" class="form-control"  id="directorsprofile_identificationtype" name="directorsprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtype()) ? ownershipProfileDetailsVO_director1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="directorsprofile_nationality" name="directorsprofile_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getNationality()) ? ownershipProfileDetailsVO_director1.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile_passportissuedate" name="directorsprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker" id="directorsprofile_Passportexpirydate" name="directorsprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile_politicallyexposed" name="directorsprofile_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile_criminalrecord" name="directorsprofile_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Directors Profile 2</h2>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="directorsprofile2_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director2.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("directorsprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>


                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile2" name="directorsprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getFirstname()) ? ownershipProfileDetailsVO_director2.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_lastname" name="directorsprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getLastname()) ? ownershipProfileDetailsVO_director2.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile2_dateofbirth" name="directorsprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_telnocc1" name="directorsprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelnocc1()) ? ownershipProfileDetailsVO_director2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_telephonenumber" name="directorsprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelephonenumber()) ? ownershipProfileDetailsVO_director2.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_emailaddress" name="directorsprofile2_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getEmailaddress()) ? ownershipProfileDetailsVO_director2.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_owned" name="directorsprofile2_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getOwned())==true?ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getOwned():""%>" /><%if(validationErrorList.getError("directorsprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="directorsprofile2_addressproof" id="directorsprofile2_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressProof()) ? ownershipProfileDetailsVO_director2.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_addressId" name="directorsprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressId()) ? ownershipProfileDetailsVO_director2.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_address" name="directorsprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddress()) ? ownershipProfileDetailsVO_director2.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_street" name="directorsprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getStreet()) ? ownershipProfileDetailsVO_director2.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_city" name="directorsprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getCity()) ? ownershipProfileDetailsVO_director2.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_State" name="directorsprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getState()) ? ownershipProfileDetailsVO_director2.getState():""%>"/><%if(validationErrorList.getError("directorsprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="directorsprofile2_country" onchange="myjunk1('directorsprofile2_country','directorsprofile2_telnocc1');" class="form-control" <%=globaldisabled%> >
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getCountry()) ? ownershipProfileDetailsVO_director2.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_zip" name="directorsprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getZipcode()) ? ownershipProfileDetailsVO_director2.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="directorsprofile2_identificationtypeselect" id="directorsprofile2_identificationtypeselect" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director2.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile2_identificationtype" name="directorsprofile2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtype()) ? ownershipProfileDetailsVO_director2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>


                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="directorsprofile2_nationality" name="directorsprofile2_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getNationality()) ? ownershipProfileDetailsVO_director2.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile2_passportissuedate" name="directorsprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile2_Passportexpirydate" name="directorsprofile2_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile2_politicallyexposed" name="directorsprofile2_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director2.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile2_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_director2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile2_criminalrecord" name="directorsprofile2_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director2.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile2_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_director2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>


                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Directors Profile 3</h2>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="directorsprofile3_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director3.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("directorsprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile3" name="directorsprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getFirstname()) ? ownershipProfileDetailsVO_director3.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_lastname" name="directorsprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getLastname()) ? ownershipProfileDetailsVO_director3.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile3_dateofbirth" name="directorsprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_telnocc1" name="directorsprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelnocc1()) ? ownershipProfileDetailsVO_director3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_telephonenumber" name="directorsprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelephonenumber()) ? ownershipProfileDetailsVO_director3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_emailaddress" name="directorsprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getEmailaddress()) ? ownershipProfileDetailsVO_director3.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_owned" name="directorsprofile3_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned())==true?ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned():""%>" /><%if(validationErrorList.getError("directorsprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>1
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="directorsprofile3_addressproof" id="directorsprofile3_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressProof()) ? ownershipProfileDetailsVO_director3.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_addressId" name="directorsprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressId()) ? ownershipProfileDetailsVO_director3.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_address" name="directorsprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddress()) ? ownershipProfileDetailsVO_director3.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_street" name="directorsprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getStreet()) ? ownershipProfileDetailsVO_director3.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_city" name="directorsprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getCity()) ? ownershipProfileDetailsVO_director3.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_State" name="directorsprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getState()) ? ownershipProfileDetailsVO_director3.getState():""%>"/><%if(validationErrorList.getError("directorsprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="directorsprofile3_country" <%=globaldisabled%> onchange="myjunk1('directorsprofile3_country','directorsprofile3_telnocc1');"  class="form-control">
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getCountry()) ? ownershipProfileDetailsVO_director3.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_zip" name="directorsprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getZipcode()) ? ownershipProfileDetailsVO_director3.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="directorsprofile3_identificationtypeselect" id="directorsprofile3_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director3.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile3_identificationtype" name="directorsprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtype()) ? ownershipProfileDetailsVO_director3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="directorsprofile3_nationality" name="directorsprofile3_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getNationality()) ? ownershipProfileDetailsVO_director3.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile3_passportissuedate" name="directorsprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile3_Passportexpirydate" name="directorsprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile3_politicallyexposed" name="directorsprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile3_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile3_criminalrecord" name="directorsprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile3_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                        <!-- Director 4 -->

                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Directors Profile 4</h2>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="directorsprofile4_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director4.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("directorsprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile4" name="directorsprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getFirstname()) ? ownershipProfileDetailsVO_director4.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-3 has-feedback">
                                            <label for="directorsprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_lastname" name="directorsprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getLastname()) ? ownershipProfileDetailsVO_director4.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile4_dateofbirth" name="directorsprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_telnocc1" name="directorsprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelnocc1()) ? ownershipProfileDetailsVO_director4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="directorsprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_telephonenumber" name="directorsprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelephonenumber()) ? ownershipProfileDetailsVO_director4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_emailaddress" name="directorsprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getEmailaddress()) ? ownershipProfileDetailsVO_director4.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_owned" name="directorsprofile4_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned())==true?ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned():""%>" /><%if(validationErrorList.getError("directorsprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="directorsprofile4_addressproof" id="directorsprofile4_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressProof()) ? ownershipProfileDetailsVO_director4.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_addressId" name="directorsprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressId()) ? ownershipProfileDetailsVO_director4.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_address" name="directorsprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddress()) ? ownershipProfileDetailsVO_director4.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_street" name="directorsprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getStreet()) ? ownershipProfileDetailsVO_director4.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_city" name="directorsprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getCity()) ? ownershipProfileDetailsVO_director4.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_State" name="directorsprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getState()) ? ownershipProfileDetailsVO_director4.getState():""%>"/><%if(validationErrorList.getError("directorsprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="directorsprofile4_country" <%=globaldisabled%> onchange="myjunk1('directorsprofile4_country','directorsprofile4_telnocc1');"  class="form-control">
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getCountry()) ? ownershipProfileDetailsVO_director4.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_zip" name="directorsprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getZipcode()) ? ownershipProfileDetailsVO_director4.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="directorsprofile4_identificationtypeselect" id="directorsprofile4_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director4.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("directorsprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="directorsprofile4_identificationtype" name="directorsprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtype()) ? ownershipProfileDetailsVO_director4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="directorsprofile4_nationality" name="directorsprofile4_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getNationality()) ? ownershipProfileDetailsVO_director4.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("directorsprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile4_passportissuedate" name="directorsprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="directorsprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="directorsprofile4_Passportexpirydate" name="directorsprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile4_politicallyexposed" name="directorsprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile4_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>

                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="directorsprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="directorsprofile4_criminalrecord" name="directorsprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director4.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="directorsprofile4_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
            <%
                if(ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_title) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_address) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_city) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_zip) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_country) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_street) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_designation) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_identificationtypeselect)||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_State) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_addressId) ||

                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_title) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_address) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_city) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_zip) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_country) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_street) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_telephonenumber)  ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_designation) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_State) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_addressId) ||

                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_title) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_address) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_city) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_zip) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_country) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_street) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_designation) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_State) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_addressId) ||

                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_title) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_lastname) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_address) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_city) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_dateofbirth) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_zip) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_country) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_street) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_telnocc1) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_telephonenumber) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_emailaddress) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_designation) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_identificationtypeselect) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_identificationtype) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_State) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_nationality) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_passportissuedate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_Passportexpirydate) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_politicallyexposed) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_criminalrecord) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_addressproof) ||
                        ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_addressId) ||view)
                {
            %>
            <%--Authorized Signatory Profile--%>
            <div class="row" style="margin-left: 25px;">
                <div class="col-lg-12">
                    <div class="panel panel-default" style="margin-top: 0px" >
                        <div class="panel-heading" id="flip4" style="background-color: #9fabb7;">
                            Authorize Signatory Profile
                            <div style="margin-left: 90%;margin-top: -15px;">
                                <img src="/merchant/images/down_Arrow.jpg" border="0" style="width:28%">
                            </div>
                        </div>
                        <br>
                        <div id="panel4" style="background-color: #ffffff">
                            <div class="container-fluid ">
                                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                                    <div class="form foreground bodypanelfont_color panelbody_color">
                                        <center><h2 style="width:45%" class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>"> <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Authorized Signatory after entering the data provided"%></h2></center>

                                        <div class="form-group col-md-9 has-feedback">
                                            <label for="numOfAuthrisedSignatory" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">How many Authorised Signatories do you have?</label>
                                        </div>
                                        <div class="form-group col-md-3 has-feedback">
                                            <input type="text" class="form-control"  id="numOfAuthrisedSignatory" name="numOfAuthrisedSignatory" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory())?ownershipProfileVO.getNumOfAuthrisedSignatory():""%>"/><%if(validationErrorList.getError("numOfAuthrisedSignatory")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top:0!important;display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Authorize Signatory Profile 1</h2>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title*</label>
                                            <select class="form-control" name="authorizedsignatoryprofile_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("authorizedsignatoryprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile" name="authorizedsignatoryprofile"style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_lastname" name="authorizedsignatoryprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth*</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_dateofbirth" name="authorizedsignatoryprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile1_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile1_designation" name="authorizedsignatoryprofile1_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory1.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_telnocc1" name="authorizedsignatoryprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_telephonenumber" name="authorizedsignatoryprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_emailaddress" name="authorizedsignatoryprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control"  id="authorizedsignatoryprofile1_owned" name="authorizedsignatoryprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory1.getOwned():""%>" onkeypress="return isNumberKey(event)" <%--onchange="return myOwnerShip(this.value,'nameprincipal2_owned','nameprincipal3_owned',1)"--%>/>
                                                <span class="input-group-addon" style="font-weight: 800;">%</span>
                                                <%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                <span style="<%=validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile1_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile1_owned").getLogMessage()%><%}%></span>
                                            </div>
                                        </div>


                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="authorizedsignatoryprofile1_addressproof" id="authorizedsignatoryprofile1_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile1_addressId" name="authorizedsignatoryprofile1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_address" name="authorizedsignatoryprofile_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_street" name="authorizedsignatoryprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory1.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_city" name="authorizedsignatoryprofile_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_State" name="authorizedsignatoryprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getState()) ? ownershipProfileDetailsVO_authorizeSignatory1.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                            <select  name="authorizedsignatoryprofile_country" <%=globaldisabled%> onchange="myjunk1('authorizedsignatoryprofile_country','authorizedsignatoryprofile_telnocc1');" class="form-control" >
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_zip" name="authorizedsignatoryprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory1.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type*</label>
                                            <select class="form-control" name="authorizedsignatoryprofile_identificationtypeselect" id="authorizedsignatoryprofile_identificationtypeselect" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID*</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile_identificationtype" name="authorizedsignatoryprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="authorizedsignatoryprofile_nationality" name="authorizedsignatoryprofile_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory1.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_passportissuedate" name="authorizedsignatoryprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_Passportexpirydate" name="authorizedsignatoryprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile1_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile1_politicallyexposed" name="authorizedsignatoryprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile1_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile1_criminalrecord" name="authorizedsignatoryprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile1_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Authorize Signatory Profile 2</h2>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="authorizedsignatoryprofile2_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("authorizedsignatoryprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2" name="authorizedsignatoryprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull((ownershipProfileDetailsVO_authorizeSignatory2.getFirstname())) ? ownershipProfileDetailsVO_authorizeSignatory2.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_lastname" name="authorizedsignatoryprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory2.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_dateofbirth" name="authorizedsignatoryprofile2_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_designation" name="authorizedsignatoryprofile2_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory2.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telnocc1" name="authorizedsignatoryprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telephonenumber" name="authorizedsignatoryprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_emailaddress" name="authorizedsignatoryprofile2_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control"  id="authorizedsignatoryprofile2_owned" name="authorizedsignatoryprofile2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory2.getOwned():""%>" onkeypress="return isNumberKey(event)" <%--onchange="return myOwnerShip(this.value,'nameprincipal2_owned','nameprincipal3_owned',1)"--%>/>
                                                <span class="input-group-addon" style="font-weight: 800;">%</span>
                                                <%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                <span style="<%=validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile2_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile2_owned").getLogMessage()%><%}%></span>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="authorizedsignatoryprofile2_addressproof" id="authorizedsignatoryprofile2_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_addressId" name="authorizedsignatoryprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_address" name="authorizedsignatoryprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_street" name="authorizedsignatoryprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory2.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_city" name="authorizedsignatoryprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_State" name="authorizedsignatoryprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getState()) ? ownershipProfileDetailsVO_authorizeSignatory2.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="authorizedsignatoryprofile2_country" onchange="myjunk1('authorizedsignatoryprofile2_country','authorizedsignatoryprofile2_telnocc1');" class="form-control" <%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_zip" name="authorizedsignatoryprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory2.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="authorizedsignatoryprofile2_identificationtypeselect" id="authorizedsignatoryprofile2_identificationtypeselect" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>

                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile2_identificationtype" name="authorizedsignatoryprofile2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="authorizedsignatoryprofile2_nationality" name="authorizedsignatoryprofile2_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory2.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_passportissuedate" name="authorizedsignatoryprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_Passportexpirydate" name="authorizedsignatoryprofile2_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile2_politicallyexposed" name="authorizedsignatoryprofile2_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile2_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile2_criminalrecord" name="authorizedsignatoryprofile2_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile2_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Authorize Signatory Profile 3</h2>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="authorizedsignatoryprofile3_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("authorizedsignatoryprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3" name="authorizedsignatoryprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_lastname" name="authorizedsignatoryprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_dateofbirth" name="authorizedsignatoryprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_designation" name="authorizedsignatoryprofile3_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory3.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telnocc1" name="authorizedsignatoryprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telephonenumber" name="authorizedsignatoryprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_emailaddress" name="authorizedsignatoryprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control"  id="authorizedsignatoryprofile3_owned" name="authorizedsignatoryprofile3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory3.getOwned():""%>" onkeypress="return isNumberKey(event)" <%--onchange="return myOwnerShip(this.value,'nameprincipal2_owned','nameprincipal3_owned',1)"--%>/>
                                                <span class="input-group-addon" style="font-weight: 800;">%</span>
                                                <%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                <span style="<%=validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile3_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile3_owned").getLogMessage()%><%}%></span>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="authorizedsignatoryprofile3_addressproof" id="authorizedsignatoryprofile3_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_addressId" name="authorizedsignatoryprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_address" name="authorizedsignatoryprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_street" name="authorizedsignatoryprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory3.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_city" name="authorizedsignatoryprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_State" name="authorizedsignatoryprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getState()) ? ownershipProfileDetailsVO_authorizeSignatory3.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="authorizedsignatoryprofile3_country" onchange="myjunk1('authorizedsignatoryprofile3_country','authorizedsignatoryprofile3_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_zip" name="authorizedsignatoryprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory3.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="authorizedsignatoryprofile3_identificationtypeselect" id="authorizedsignatoryprofile3_identificationtypeselect" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile3_identificationtype" name="authorizedsignatoryprofile3_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="authorizedsignatoryprofile3_nationality" name="authorizedsignatoryprofile3_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory3.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_passportissuedate" name="authorizedsignatoryprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_Passportexpirydate" name="authorizedsignatoryprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile3_politicallyexposed" name="authorizedsignatoryprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile3_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile3_criminalrecord" name="authorizedsignatoryprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile3_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>

                                        <!--authotized signatory 4-->
                                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Authorize Signatory Profile 4</h2>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Title</label>
                                            <select class="form-control" name="authorizedsignatoryprofile4_title" <%=globaldisabled%>>
                                                <option value="">Select Title</option>
                                                <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>MR</option>
                                                <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>MRS</option>
                                                <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>MS</option>
                                                <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>MISS</option>
                                                <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>MASTER</option>
                                                <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())==true?"selected":""%>>DR</option>

                                            </select> <%if(validationErrorList.getError("authorizedsignatoryprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">First&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4" name="authorizedsignatoryprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Last&nbsp;Name</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_lastname" name="authorizedsignatoryprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Birth</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_dateofbirth" name="authorizedsignatoryprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_designation" name="authorizedsignatoryprofile4_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory4.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telnocc1" name="authorizedsignatoryprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <label for="authorizedsignatoryprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telephonenumber" name="authorizedsignatoryprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_emailaddress" name="authorizedsignatoryprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Percentage(%)holding*</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control"  id="authorizedsignatoryprofile4_owned" name="authorizedsignatoryprofile4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory4.getOwned():""%>" onkeypress="return isNumberKey(event)" <%--onchange="return myOwnerShip(this.value,'nameprincipal2_owned','nameprincipal3_owned',1)"--%>/>
                                                <span class="input-group-addon" style="font-weight: 800;">%</span>
                                                <%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                <span style="<%=validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile4_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile4_owned").getLogMessage()%><%}%></span>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Address Details :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                            <select class="form-control" name="authorizedsignatoryprofile4_addressproof" id="authorizedsignatoryprofile4_addressproof"class="txtbox" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof() : ""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_addressId" name="authorizedsignatoryprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_address" name="authorizedsignatoryprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_street" name="authorizedsignatoryprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory4.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_city" name="authorizedsignatoryprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County of ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_State" name="authorizedsignatoryprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getState()) ? ownershipProfileDetailsVO_authorizeSignatory4.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                            <select  name="authorizedsignatoryprofile4_country" onchange="myjunk1('authorizedsignatoryprofile4_country','authorizedsignatoryprofile4_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCountry():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_zip" name="authorizedsignatoryprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory4.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Identification Type Information :: </u></label>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification Type</label>
                                            <select class="form-control" name="authorizedsignatoryprofile4_identificationtypeselect" id="authorizedsignatoryprofile4_identificationtypeselect" <%=globaldisabled%>>
                                                <%
                                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect():""));
                                                %>
                                            </select>
                                            <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Identification ID</label>
                                            <input type="text" class="form-control"  id="authorizedsignatoryprofile4_identificationtype" name="authorizedsignatoryprofile4_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Nationality</label>
                                            <select  class="form-control" id="authorizedsignatoryprofile4_nationality" name="authorizedsignatoryprofile4_nationality" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>>
                                                <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory4.getNationality():"")%>
                                            </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Issuing date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_passportissuedate" name="authorizedsignatoryprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label for="authorizedsignatoryprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Expiry date of ID</label>
                                            <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_Passportexpirydate" name="authorizedsignatoryprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Politically exposed person?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile4_politicallyexposed" name="authorizedsignatoryprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile4_politicallyexposed" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No
                                        </div>
                                        <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                            <label for="authorizedsignatoryprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Existence of criminal record?</label>
                                            &nbsp;&nbsp;<input type="radio" id="authorizedsignatoryprofile4_criminalrecord" name="authorizedsignatoryprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                            &nbsp;&nbsp;<input type="radio" name="authorizedsignatoryprofile4_criminalrecord" value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) ?"checked":""%> />&nbsp;No
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
                ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
                ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
                ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
                ownershipProfileDetailsVOMap.put("SHAREHOLDER4",ownershipProfileDetailsVO_shareholder4);
                ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
                ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
                ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
                ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER4",ownershipProfileDetailsVO_corporateShareholder4);
                ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
                ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
                ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
                ownershipProfileDetailsVOMap.put("DIRECTOR4",ownershipProfileDetailsVO_director4);
                ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
                ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
                ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
                ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY4",ownershipProfileDetailsVO_authorizeSignatory4);
                ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownershipProfileDetailsVOMap);
                applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
            %>
            <br><br><br><br><br><br><br><br><br>
            <%


                if(ownershipProfileInputList.size()==0 && !view)
                {
                    out.println("<div class=\"reporttable\" >");
                    out.println(Functions.NewShowConfirmation("Profile", "No details need to be provided"));
                    out.println("</div>");
                }
            %>
        </div>
    </div>
</div>



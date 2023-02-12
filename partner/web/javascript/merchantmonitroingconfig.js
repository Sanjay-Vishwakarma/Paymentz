/**
 * Created by admin on 1/30/2018.
 */
var expanded = false;
function showCheckboxes(terminalId,mappingId) {
    var checkboxes = document.getElementById(terminalId+'_checkboxes_'+mappingId);
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }
}
var expanded = false;
function showCheckboxesPSP(terminalId,mappingId) {
    var checkboxes = document.getElementById(terminalId+'_checkboxespsp_'+mappingId);
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }
}
var expanded = false;
function showCheckboxesMerchant(terminalId,mappingId) {
    var checkboxes = document.getElementById(terminalId+'_checkboxesmerchant_'+mappingId);
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }
}
var expanded = false;
function showCheckboxesAgent(terminalId,mappingId) {
    var checkboxes = document.getElementById(terminalId+'_checkboxesagent_'+mappingId);
    if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
    } else {
        checkboxes.style.display = "none";
        expanded = false;
    }
}
function confirmsubmit2(i){
    var checkboxes = document.getElementsByName("mappingid_" + i);
    var total_boxes = checkboxes.length;
    flag = false;
    for (i = 0; i < total_boxes; i++){
        if (checkboxes[i].checked){
            flag = true;
            break;
        }
    }
    if (!flag){
        alert("select at least one rule");
        return false;
    }
    if (confirm("Do you really want to update all selected rule.")){
        document.getElementById("details" + i).submit();
    }
    else{
        return false;
    }
}
function doChangesForAlertActivation(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_alertActiovation_' + mappingId).value = document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value;
}
function doChangesForSuspensionActivation(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_suspensionActivation_' + mappingId).value = document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value;
}
function doChangesForAlertToAdmin(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdmin_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value;
}
function doChangesForAlertToAdminSales(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdminSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value;
}
function doChangesForAlertToAdminRF(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdminRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value;
}
function doChangesForAlertToAdminCB(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdminCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value;
}
function doChangesForAlertToAdminFraud(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdminFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value;
}
function doChangesForAlertToAdminTech(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAdminTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value;
}
function doChangesForAlertToPSP(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartner_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value;
}
function doChangesForAlertToPartnerSales(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartnerSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value;
}
function doChangesForAlertToPartnerRF(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartnerRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value;
}
function doChangesForAlertToPartnerCB(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartnerCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value;
}
function doChangesForAlertToPartnerFraud(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartnerFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value;
}
function doChangesForAlertToPartnerTech(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToPartnerTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value;
}
function doChangesForAlertToMerchant(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchant_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value;
}
function doChangesForAlertToMerchantSales(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchantSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value;
}
function doChangesForAlertToMerchantRF(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchantRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value;
}
function doChangesForAlertToMerchantCB(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchantCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value;
}
function doChangesForAlertToMerchantFraud(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchantFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value;
}
function doChangesForAlertToMerchantTech(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToMerchantTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value;
}
function doChangesForAlertToAgent(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgent_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value;
}
function doChangesForAlertToAgentSales(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgentSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value;
}
function doChangesForAlertToAgentRF(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgentRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value;
}
function doChangesForAlertToAgentCB(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgentCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value;
}
function doChangesForAlertToAgentFraud(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgentFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value;
}
function doChangesForAlertToAgentTech(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isAlertToAgentTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value;
}
function ToggleAll(checkbox, terminalid){
    flag = checkbox.checked;
    var checkboxes = document.getElementsByName("mappingid_" + terminalid);
    var total_boxes = checkboxes.length;
    for (i = 0; i < total_boxes; i++){
        checkboxes[i].checked = flag;
    }
}
function doChangeFrequency(data,mappingId,terminalId){
    var value=confirm("Do you really want to change selected frequency?")
    if(value){
        document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value=data.value;
    }
    else{
        data.value=document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value
    }
}
function showCheckboxesFrequency(terminalId,mappingId){
    var checkboxes = document.getElementById(terminalId+'_checkboxesfrequency_'+mappingId);
    if(!expanded){
        checkboxes.style.display = "block";
        expanded = true;
    }
    else{
        checkboxes.style.display = "none";
        expanded = false;
    }
}
function doChangesForDailyExecution(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isdailyexecution_' + mappingId).value = document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value;
}
function doChangesForWeeklyExecution(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_isweeklyexecution_' + mappingId).value = document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value;
}
function doChangesForMonthlyExecution(data, mappingId,terminalId){
    if (data.checked){
        document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value = "Y";
    }
    else{
        document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value = "N";
    }
    var value=document.getElementById(terminalId+'_ismonthlyexecution_' + mappingId).value = document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value;
}
function selectTerminals(data,ctoken){
    document.f1.action="/partner/partnerRiskRuleMapping.jsp?ctoken="+ctoken;
    document.f1.submit();
}
function dailyEnableDisableTextBox(data,mappingId,terminalId){
    if(data.checked){
        document.getElementsByName(terminalId+"_alertThreshold_"+mappingId)[0].disabled = false;
        document.getElementsByName(terminalId+'_suspensionThreshold_'+mappingId)[0].disabled = false;
    }
    else{
        document.getElementsByName(terminalId+"_alertThreshold_"+mappingId)[0].disabled = true;
        document.getElementsByName(terminalId+'_suspensionThreshold_'+mappingId)[0].disabled = true;
    }
}
function weeklyEnableDisableTextBox(data,mappingId,terminalId){
    if(data.checked){
        document.getElementsByName(terminalId+"_weeklyAlertThreshold_"+mappingId)[0].disabled = false;
        document.getElementsByName(terminalId+'_weeklySuspensionThreshold_'+mappingId)[0].disabled = false;
    }
    else{
        document.getElementsByName(terminalId+"_weeklyAlertThreshold_"+mappingId)[0].disabled = true;
        document.getElementsByName(terminalId+'_weeklySuspensionThreshold_'+mappingId)[0].disabled = true;
    }
}
function monthlyEnableDisableTextBox(data,mappingId,terminalId){
    if(data.checked){
        document.getElementsByName(terminalId+"_monthlyAlertThreshold_"+mappingId)[0].disabled = false;
        document.getElementsByName(terminalId+'_monthlySuspensionThreshold_'+mappingId)[0].disabled = false;
    }
    else{
        document.getElementsByName(terminalId+"_monthlyAlertThreshold_"+mappingId)[0].disabled = true;
        document.getElementsByName(terminalId+'_monthlySuspensionThreshold_'+mappingId)[0].disabled = true;
    }
}

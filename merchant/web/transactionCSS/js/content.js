var redirectURL;
var pay_methods;
var all_options;
var addressValidation;
var addressDisplayFlag;
var addressValidationDebitCard;
var addressDisplayFlagDebitCard;
var currency;

var personalInfoValidation;
var personalInfoDisplay ;
var OTPInfoDisplay;
var currentTab = 0;
var error;
var browser;

var SelectPayMethodText;
var NextText;
var ErrorText;
var ErrorEng = "ERROR";

var Months, SelectMonths;
var isCheckout = false;
var isError = false;
var payNoText = $('#payNowLabel').text();
// payment options variables for different languages
/*var CreditCards;
 var DebitCards;
 var NetBanking;
 var Vouchers;
 var Wallet;
 var ClearSettle;
 var SEPA;*/

// errors text variables for different languages
var InvalidYear, InvalidMonth, InvalidDay, InvalidDate, InvalidCVV, InvalidEMI, InvalidCardHolderName;
var CannotBeCurrentMonth, YearCannotBeBefore, InvalidExpiry;
var EmptyEmail, InvalidEmail, EmptyAddress, InvalidAddress, EmptyCity, EmptyCountry, EmptyState, EmptyZip, EmptyBirhtdate, EmptyPhoneNo, InvalidPhoneNo;
var InvalidVisa, InvalidMaster, InvalidDiner, InvalidAMEX, InvalidDISC, InvalidJCB, InvalidMAESTRO, InvalidINSTAPAYMENT, InvalidRUPAY, InvalidCard;
var VisaNotPermitted, MasterNotPermitted, DinerNotPermitted, AmexNotPermitted, DiscNotPermitted, JcbNotPermitted, MaestroNotPermitted, InstapaymentNotPermitted, RUPAYNotPermitted;

var label;
var typeLabel;
var banglaCartType;
var CajaRuralCartType;

// new window

//var win;
////$(window).load(function() {
////        openPopup();
////});
//
//function openPopup() {
//    if(win && !win.closed){ //checks to see if window is open
//        win.focus();
//    } else {
//        win = window.open('https://www.google.com/');
//        win.focus();
//    }
//}
//
//function polling(){
//    if (win && win.closed) {
//        alert("in polling" )
//        clearInterval(timer);
//        alert('popup window is closed.');
//    }
//}
//timer = setInterval('polling()',1000);
//
//var validNavigation = false;
//
//function endSession() {
//    console.log("in end session ");
//    alert("in end session ");
//// Browser or broswer tab is closed
//// Do sth here ...
//    win.close();
//    alert("Browser window closed");
//}
//
//window.onbeforeunload = function() {
//        endSession();
//}

// end of new window


//window.onbeforeunload = function(evt) {
//    document.timeoutform.submit();
//    console.log("in on before unload",evt);
//}

console.log("document ready state = ", document.readyState)

document.addEventListener('readystatechange', (function(){
    console.log("inside ready stage change = ", document.readyState);
    var tabsElem= document.querySelectorAll('.tabs-li');
    if(document.readyState === 'interactive'){
        tabsElem.forEach(function (elem){
            elem.style.cssText="pointer-events:none";
        });
    }
    else if (document.readyState === 'complete') {
        // document ready
        console.log("inside complete =", document.readyState);
        console.log(tabsElem)
        tabsElem.forEach(function (elem){
            elem.style.removeProperty("pointer-events");
        });
    }
}));

$(document).ready(function ()
{
    //var tabsElem= document.querySelectorAll('.tabs-li');
    browser = get_browser();
    console.log("in ready function ", browser);
    console.log("document ready state = ", document.readyState)
    //console.log("inside ready = ", tabsElem)
    //tabsElem.forEach((elem) => elem.style.removeProperty("pointer-events"));
    if (browser.indexOf("Edge") > -1)
    {
        $("#load").removeClass("loader").addClass("loader-edge");
    }
    else
    {
        $("#load").removeClass("loader-edge").addClass("loader");
    }


    if ((browser.indexOf("IE 11") > -1) || (browser.indexOf("MSIE 10") > -1) || (browser.indexOf("MSIE 9") > -1) || (browser.indexOf("MSIE 8") > -1))
    {
        console.log("in if ie");
        removejscssfile('/merchant/transactionCSS/css/fail.css', 'css');
    }
    else
    {
        console.log("in else not IE");
        loadStyle('/merchant/transactionCSS/css/fail.css');
    }

    //openPopup();
    //endSession()


    $('#toolOrderID').mouseover(function ()
    {
        document.getElementById("tooltiptextOrderId").style.visibility = "visible";
    });
    $('#toolOrderID').mouseout(function ()
    {
        document.getElementById("tooltiptextOrderId").style.visibility = "hidden";
    });
    $('#tooltiptextOrderId').mouseover(function ()
    {
        document.getElementById("tooltiptextOrderId").style.visibility = "visible";
    });
    $('#tooltiptextOrderId').mouseout(function ()
    {
        document.getElementById("tooltiptextOrderId").style.visibility = "hidden";
    });


    var tab = document.getElementById("payMethod").innerText;
    if (pay_methods)
    {
        $("#topRight").show();
        $("#backArrow").show();
    }
    // hide top bar buttons on error page
    else if (tab == ErrorEng)
    {
        $("#topRightErrorPage").hide();
    }
    else
    {
        $("#topRight").hide();
        $("#backArrow").hide();
    }


    var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
    var eventerssss = window[eventMethod];
    var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

    // Listen to message from parent to child IFrame window
    eventerssss(messageEvent, function (e)
    {
        console.log("in eventersssssssssssssssss", e.data);
        if (e.data.redirecturl)
        {
            console.log("if data exists");
            document.body.style.background = "none";
            $("#closebtn").removeClass("hide"); // checkoutPayment Page
            $("#closebtnError").removeClass("hide"); // checkoutError Page
            $("#closebtnConfirmation").removeClass("hide"); // confirmationCheckout Page

            $("#cancelbackArrow").addClass('hide');

            $("#topRight").hide();
            if (isError == false)
            {
                $("#backArrow").hide();
            }
            isCheckout = true;
            redirectURL = e.data.redirecturl;
        }
        else
        {
            console.log("if data does not exists");
        }
    }, false);

    if (document.getElementById('timer'))
    {
        var timerValue = document.getElementById("timeoutValue").value;
        timerValue = timerValue ? timerValue : "10:00"
        document.getElementById('timer').innerHTML = timerValue;
        startTimer();
    }
});

function removejscssfile(filename, filetype)
{
    var targetelement = (filetype == "js") ? "script" : (filetype == "css") ? "link" : "none" //determine element type to create nodelist from
    var targetattr = (filetype == "js") ? "src" : (filetype == "css") ? "href" : "none" //determine corresponding attribute to test for
    var allsuspects = document.getElementsByTagName(targetelement)
    for (var i = allsuspects.length; i >= 0; i--)
    { //search backwards within nodelist for matching elements to remove
        if (allsuspects[i] && allsuspects[i].getAttribute(targetattr) != null && allsuspects[i].getAttribute(targetattr).indexOf(filename) != -1)
            allsuspects[i].parentNode.removeChild(allsuspects[i]) //remove element by calling parentNode.removeChild()
    }
}

function loadStyle(href, callback)
{
    // avoid duplicates
    for (var i = 0; i < document.styleSheets.length; i++)
    {
        if (document.styleSheets[i].href == href)
        {
            return;
        }
    }
    var head = document.getElementsByTagName('head')[0];
    var link = document.createElement('link');
    link.rel = 'stylesheet';
    link.type = 'text/css';
    link.href = href;
    if (callback)
    {
        link.onload = function ()
        {
            callback()
        }
    }
    head.appendChild(link);
}

function startTimer()
{
    var presentTime = document.getElementById('timer').innerHTML;
    var timeArray = presentTime.split(/[:]+/);
    var m = timeArray[0];
    var s = checkSecond((timeArray[1] - 1));
    if (s == 59)
    {
        m = m - 1
    }

    document.getElementById('timer').innerHTML =
            m + ":" + s;
    if (m == 00 & s == 00)
    {
        document.getElementById('sessionout').submit();
    }
    else
    {
        setTimeout(startTimer, 1000);
    }

}

function checkSecond(sec)
{
    if (sec < 10 && sec >= 0)
    {
        sec = "0" + sec
    }
    ; // add zero in front of numbers < 10
    if (sec < 0)
    {
        sec = "59"
    }
    ;
    return sec;
}

function get_browser()
{
    var ua = navigator.userAgent, tem,
            M = ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
    if (/trident/i.test(M[1]))
    {
        tem = /\brv[ :]+(\d+)/g.exec(ua) || [];
        return 'IE ' + (tem[1] || '');
    }
    if (M[1] === 'Chrome')
    {
        tem = ua.match(/\b(OPR|Edge)\/(\d+)/);
        if (tem != null) return tem.slice(1).join(' ').replace('OPR', 'Opera');
    }
    M = M[2] ? [M[1], M[2]] : [navigator.appName, navigator.appVersion, '-?'];
    if ((tem = ua.match(/version\/(\d+)/i)) != null) M.splice(1, 1, tem[1]);
    return M.join(' ');
}

var response = "";
function getResponseFromCheckout(redirect, form)
{
    console.log("in getResponseFromCheckout", redirect, form);
    redirectURL = redirect;
    var obj = {};
    var elements = form.querySelectorAll("input, select, textarea");
    for (var i = 0; i < elements.length; ++i)
    {
        var element = elements[i];
        var name = element.name;
        var value = element.value;
        if (name)
        {
            obj[name] = value;
        }
    }
    console.log("response ---", obj);
    response = obj;
    //console.log("form -- ", $(form).serializeArray())
    closefunc();
}

// Button Checkout close button on top
function closefunc()
{
    console.log("in close function", response);
    // window.parent.postMessage(response, "http://localhost/checkoutbutton/checkout.php");
    if (response.trackingid)
    {
        if (redirectURL)
        {
            console.log("in close", response, redirectURL);
            window.parent.postMessage(response, redirectURL);
            // window.postMessage(response,redirectURL);
        }
    }
}


function cancelError()
{
    document.errorCancelForm.submit();
}


var landingTab;
var UPICurrent;
function loadFromBody(paymentmethodLabel, paymentmethod, cardtype, cardtypeDisplayLabel, error, paymentList, personalDetailDisplay, personalDetailValidation, payType, payTypeLabel, terminalMapSize,country,IsOTPRequired)
{
    console.log("in load from body", paymentmethodLabel, paymentmethod, cardtype, cardtypeDisplayLabel, error, paymentList, personalDetailDisplay, personalDetailValidation, payType, payTypeLabel, terminalMapSize,country,IsOTPRequired);
    personalInfoValidation  = personalDetailValidation;
    personalInfoDisplay     = personalDetailDisplay;
    OTPInfoDisplay          = IsOTPRequired;
    UPICurrent =  cardtype;

    // if payment method is 1 then this payType will have value
    if (payType)
    {
        PaymentOptionHideShow(payTypeLabel, payType);
    }
    else
    {
        if (paymentmethod)
        {
            PaymentOptionHideShow(paymentmethodLabel, paymentmethod);
        }
    }
    if (cardtype)
    {
        if (paymentmethod == 'CreditCards')
        {
            if (personalInfoDisplay == 'N')
            {
                document.getElementById("personalinfo").classList.remove("tab");
                document.getElementById("personalinfo").style.display = "none";
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    document.getElementById("personalinfo").classList.add("tab");
                    document.getElementById("personalinfo").style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    document.getElementById("personalinfo").classList.add("tab");
                    document.getElementById("personalinfo").style.display = "inline-block";
                    document.getElementById("mobilePersonal").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    document.getElementById("personalinfo").classList.add("tab");
                    document.getElementById("personalinfo").style.display = "inline-block";
                    document.getElementById("emailPersonal").style.display = "none";
                }
                else
                {
                    document.getElementById("personalinfo").classList.add("tab");
                    document.getElementById("personalinfo").style.display = "inline-block";
                }
            }
            cardInfo('CreditCards');
        }
        if (paymentmethod == 'DebitCards')
        {
            if (personalInfoDisplay == 'N')
            {
                document.getElementById("personalinfodc").classList.remove("tab");
                document.getElementById("personalinfodc").style.display = "none";
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    document.getElementById("personalinfodc").classList.add("tab");
                    document.getElementById("personalinfodc").style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    document.getElementById("personalinfodc").classList.add("tab");
                    document.getElementById("personalinfodc").style.display = "inline-block";
                    document.getElementById("personalinfodc").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    document.getElementById("personalinfodc").classList.add("tab");
                    document.getElementById("personalinfodc").style.display = "inline-block";
                    document.getElementById("personalinfodc").style.display = "none";
                }
                else
                {
                    document.getElementById("personalinfodc").classList.add("tab");
                    document.getElementById("personalinfodc").style.display = "inline-block";
                }
            }
            debitCardHideShow('DebitCards');
        }
        if (paymentmethod == 'Wallet')
        {
            walletHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'NetBanking')
        {
            netBankingHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'Vouchers')
        {
            VoucherHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'SEPA')
        {
            sepaHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == "PrepaidCards")
        {
            prepaidCardHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'CHK')
        {
            chkHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'eCheck')
        {
            eCheckHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'CupUpi')
        {
            CupUpiHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'MobileBanking')
        {
            mobileBankingHideShow(cardtype, cardtypeDisplayLabel);
        }

        if (paymentmethod == 'CajaRural')
        {
            CajaRuralHideShow(cardtype, cardtypeDisplayLabel,isAutoSubmit);
        }
        if (paymentmethod == 'BankTransfer')
        {
            var isSubmit = false;
            bankTransferGetHideShow(cardtype, cardtypeDisplayLabel,isSubmit);
        }
        if (paymentmethod == 'BankTransferAfrica' || !paymentmethod == 'GiftCardAfrica' || paymentmethod == 'MobileMoneyAfrica' || paymentmethod == 'WalletAfrica')
        {
            if (paymentmethod == 'MobileMoneyAfrica' && (cardtype == "Airtel_Uganda" || cardtype == "MTN_Uganda"))
            {
                banglaCartType = cardtype;

                // MobileMoneyHideShow(cardtype, cardtypeDisplayLabel);
            }else{
                instantBankTransferHideShow(cardtype, cardtypeDisplayLabel,paymentmethod,country);
            }

        }

        if (paymentmethod == 'GiftCardAfrica')
        {
            document.getElementById('payMethodDisplay').innerText   = cardtypeDisplayLabel ;
            document.getElementById('payMethod').innerText          = cardtype;
        }

        if (paymentmethod == 'DelayedBankTransfer')
        {
            delayedBankTransferHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'GiftCards')
        {
            giftCardsHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'MTNMOMO')
        {

            MTNMOMOHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'SMARTFASTPAY')
        {

            SMARTFASTPAYHideShow(cardtype, cardtypeDisplayLabel);
        }

        if (paymentmethod == 'CASH')
        {

            CASHHideShow(cardtype, cardtypeDisplayLabel);
        }

        if (paymentmethod == 'CARD')
        {
            CARDHideShow(cardtype, cardtypeDisplayLabel);
        }
        if (paymentmethod == 'BITCOIN')
        {
            bitcoinHideShow(cardtype, cardtypeDisplayLabel);
        }


        if (paymentmethod == 'MobileBankingBangla')
        {
            var isAutoSubmit= true;
            if(personalInfoDisplay == 'Y'){
                isAutoSubmit = false;
            }
            banglaCartType = cardtypeDisplayLabel;
            console.log("wihthoutFlag ",isAutoSubmit," "+banglaCartType)
            if(isAutoSubmit){
                MobileBankingBanglaHideShow(cardtype, cardtypeDisplayLabel,isAutoSubmit)
            }

            document.getElementById('payMethodDisplay').innerText   = cardtypeDisplayLabel ;
            document.getElementById('payMethod').innerText          = cardtype;
        }

        if (paymentmethod == 'CajaRural')
        {
            var isAutoSubmit= true;
            if(personalInfoDisplay == 'Y'){
                isAutoSubmit = false;
            }
            CajaRuralCartType = cardtypeDisplayLabel;
            console.log("wihthoutFlag ",isAutoSubmit," "+CajaRuralCartType)
            if(isAutoSubmit){
                CajaRuralHideShow(cardtype, cardtypeDisplayLabel,isAutoSubmit)
            }

            document.getElementById('payMethodDisplay').innerText   = cardtypeDisplayLabel ;
            document.getElementById('payMethod').innerText          = cardtype;
        }

        if (paymentmethod == 'TIGERPAY')
        {
            var isAutoSubmit= true;
            if(personalInfoDisplay == 'Y'){
                isAutoSubmit = false;
            }
            banglaCartType = cardtype;
            if(isAutoSubmit){
                TIGERPAYHideShow(cardtype, cardtypeDisplayLabel,isAutoSubmit)
            }
            document.getElementById('payMethodDisplay').innerText   = cardtypeDisplayLabel ;
            document.getElementById('payMethod').innerText          = cardtype;

            //TIGERPAYHideShow(cardtype, cardtypeDisplayLabel,isAutoSubmit);
        }
        /*
         */
        //if (paymentmethod == 'DOKU')
        //{
        //    dokuHideShow(cardtype, cardtypeDisplayLabel);
        //}
        if (paymentmethod == 'ClearSettle')
        {
            var elem = document.getElementById(id).querySelector('#personalinfo');
            if(elem)
            {
                if (personalInfoDisplay == 'N')
                {
                    document.getElementById("personalinfoCS").classList.remove("tab");
                    document.getElementById("personalinfoCS").style.display = "none";
                }
                else
                {
                    if (personalInfoDisplay == 'Y')
                    {
                        document.getElementById("personalinfoCS").classList.add("tab");
                        document.getElementById("personalinfoCS").style.display = "inline-block";
                    }
                    else if (personalInfoDisplay == 'E')
                    {
                        document.getElementById("personalinfoCS").classList.add("tab");
                        document.getElementById("personalinfoCS").style.display = "inline-block";
                        document.getElementById("personalinfoCS").style.display = "none";
                    }
                    else if (personalInfoDisplay == 'M')
                    {
                        document.getElementById("personalinfoCS").classList.add("tab");
                        document.getElementById("personalinfoCS").style.display = "inline-block";
                        document.getElementById("personalinfoCS").style.display = "none";
                    }
                    else
                    {
                        document.getElementById("personalinfoCS").classList.add("tab");
                        document.getElementById("personalinfoCS").style.display = "inline-block";
                    }
                }
            }
            typeLabel = payTypeLabel;
        }
        if (paymentmethod == 'UPI' || paymentmethod == 'NetBankingBangladesh' || paymentmethod == 'Net-Banking'
                || paymentmethod == 'Wallets'|| paymentmethod == 'WalletBangla')
        {
            var id =paymentmethod;
            if(paymentmethod == 'Net-Banking'){
                id ='NetBankingIndia';
            }
            else if(paymentmethod == 'Wallets'){
                id ='WalletIndia';
            }
            else{
                id =paymentmethod;
            }

            var elem = document.getElementById(id).querySelector('#personalinfo');
            if(elem)
            {
                if (personalInfoDisplay == 'N')
                {
                    elem.classList.remove("tab");
                    elem.style.display = "none";
                }
                else
                {
                    if (personalInfoDisplay == 'Y')
                    {
                        elem.classList.add("tab");
                        elem.style.display = "inline-block";
                    }
                    else if (personalInfoDisplay == 'E')
                    {
                        elem.classList.add("tab");
                        elem.style.display = "inline-block";
                        document.getElementById("mobilePersonal").style.display = "none";
                    }
                    else if (personalInfoDisplay == 'M')
                    {
                        elem.classList.add("tab");
                        elem.style.display = "inline-block";
                        document.getElementById("emailPersonal").style.display = "none";
                    }
                    else
                    {
                        elem.classList.add("tab");
                        elem.style.display = "inline-block";
                    }

                }
            }
        }
    }

    if (terminalMapSize == 1 || (terminalMapSize == 1 && paymentmethod))
    {
        if (document.getElementById("payMethod").innerText.indexOf(" > ") > -1)
        {
            landingTab = document.getElementById("payMethodDisplay").innerText.split(" > ")[0];
        }
        else
        {
            landingTab = document.getElementById("payMethodDisplay").innerText;
        }
    }
    if (error)
    {
        isError = true;
        document.getElementById("payMethodDisplay").innerText = ErrorText;
        document.getElementById("payMethod").innerText = ErrorEng;
    }
    console.log("landing Tab --", landingTab);
    backButtonConfig();
}

function backButtonConfig()
{
    var currentTab;
    if (document.getElementById("payMethod").innerText.indexOf(" > ") > -1)
    {
        currentTab = document.getElementById("payMethodDisplay").innerText.split(" > ")[0];
    }
    else
    {
        currentTab = document.getElementById("payMethodDisplay").innerText;
    }

    if (currentTab == landingTab)
    {
        if (isCheckout == false)
        {
            $("#cancelbackArrow").removeClass('hide');
        }
        $("#backArrow").addClass('hide');
    }
    else
    {
        $("#backArrow").removeClass('hide');
        if (isCheckout == false)
        {
            $("#cancelbackArrow").addClass('hide');
        }
    }
}

function cancel()   // < arrow to open Cancel Modal
{
    $("#CancelModal").addClass("show").css("display", "inline-block");
}

function closeCancel()  // Close the modal
{
    $("#CancelModal").removeClass("show").css("display", "none");
}

function cancelTransaction()    // Cancel the Deposit
{
    $("#cancelForm").submit();
    $("#CancelModal").removeClass("show").css("display", "none");
}

function PaymentOptionHideShow(method, engLabel)
{
    pay_methods = engLabel;
    label = method;
    var no_of_options = $("#" + pay_methods).find(".tabs-li-wallet");
    if (no_of_options.length <= 3)
    {
        for (var i = 0; i < no_of_options.length; i++)
        {
            $("#" + pay_methods).find(".tabs-li-wallet")[i].className += " tabs-li-wallet1";
            $("#" + pay_methods).find("img")[i].className += " tabs-li-image";
            $("#" + pay_methods).find(".label-style")[i].className += " tabs-li-label";
        }
    }

    all_options = [
        'CreditCards', 'Wallet', 'DebitCards', 'NetBanking', 'Vouchers', 'ClearSettle', 'SEPA', 'PrepaidCards', 'PostpaidCards', 'VirtualAccount', 'ACH', 'CHK',
        'Net-Banking', 'Wallets', 'UPI', 'CRYPTO', 'ROMCARD', 'TOJIKA', 'CupUpi', 'BITCOIN', 'PAYG', 'ZOTA', 'PayBoutique', 'eCheck',
        'BankTransfer', 'NetBankingBangladesh', 'WalletBangladesh','MobileBanking', 'DOKU', 'ECOSPEND','InstantBankTransfer', 'DelayedBankTransfer','GiftCards',
        'FASTPAY','MTNMOMO','TIGERPAY','MobileBankingBangla','CARD','BankTransferAfrica','GiftCardAfrica','MobileMoneyAfrica','WalletAfrica','CajaRural',
        'SMARTFASTPAY','CASH'/*, 'CreditCardsIndia' , 'DebitCardsIndia',*/
    ];

    if (pay_methods == 'ClearSettle')
    {
        var csPerElem = document.getElementById("personalinfoCS");
        if(csPerElem)
        {
            if (personalInfoDisplay == 'N')
            {
                csPerElem.classList.remove("tab");
                csPerElem.style.display = "none";
                //clearSettle();
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    csPerElem.classList.add("tab");
                    csPerElem.style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    csPerElem.classList.add("tab");
                    csPerElem.style.display = "inline-block";
                    document.getElementById("mobilePersonal").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    csPerElem.classList.add("tab");
                    csPerElem.style.display = "inline-block";
                    document.getElementById("emailPersonal").style.display = "none";
                }
                else
                {
                    csPerElem.classList.add("tab");
                    csPerElem.style.display = "inline-block";
                }

            }
        }
        typeLabel = label;
        showTab(0, pay_methods);
    }
    if (pay_methods == 'CreditCards')
    {
        var ccPerElem = document.getElementById(pay_methods).querySelector('#personalinfo');
        if(ccPerElem)
        {
            if (personalInfoDisplay == 'N')
            {
                ccPerElem.classList.remove("tab");
                ccPerElem.style.display = "none";
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    ccPerElem.classList.add("tab");
                    ccPerElem.style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    ccPerElem.classList.add("tab");
                    ccPerElem.style.display = "inline-block";
                    document.getElementById("mobilePersonal").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    ccPerElem.classList.add("tab");
                    ccPerElem.style.display = "inline-block";
                    document.getElementById("emailPersonal").style.display = "none";
                }
                else
                {
                    ccPerElem.classList.add("tab");
                    ccPerElem.style.display = "inline-block";
                }

            }
        }
        showTab(0, 'CreditCards');
        cardInfo('CreditCards');
    }
    if (pay_methods == 'DebitCards')
    {
        var dcPerElem = document.getElementById(pay_methods).querySelector('#personalinfodc');
        if(dcPerElem)
        {
            if (personalInfoDisplay == 'N')
            {
                dcPerElem.classList.remove("tab");
                dcPerElem.style.display = "none";
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    dcPerElem.classList.add("tab");
                    dcPerElem.style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    dcPerElem.classList.add("tab");
                    dcPerElem.style.display = "inline-block";
                    document.getElementById("mobilePersonal").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    dcPerElem.classList.add("tab");
                    dcPerElem.style.display = "inline-block";
                    document.getElementById("emailPersonal").style.display = "none";
                }
                else
                {
                    dcPerElem.classList.add("tab");
                    dcPerElem.style.display = "inline-block";
                }

            }
        }
        showTab(0, 'DebitCards');
        debitCardHideShow('DebitCards');
    }
    if (pay_methods == "GiftCardAfrica" || pay_methods == 'TIGERPAY' || pay_methods == 'UPI' || pay_methods == 'NetBankingBangladesh' || pay_methods == 'Net-Banking'
            || pay_methods == 'Wallets' || pay_methods == 'WalletBangla' || pay_methods == 'MobileBankingBangla' || pay_methods=='CajaRural')
    {
        var id = pay_methods;
        if(pay_methods == 'Net-Banking'){
            id ='NetBankingIndia';
        }
        else if(pay_methods == 'Wallets'){
            id ='WalletIndia';
        }
        else{
            id =pay_methods;
        }
        var elem = document.getElementById(id).querySelector('#personalinfo');
        if(elem)
        {
            if (personalInfoDisplay == 'N')
            {
                elem.classList.remove("tab");
                elem.style.display = "none";
            }
            else
            {
                if (personalInfoDisplay == 'Y')
                {
                    elem.classList.add("tab");
                    elem.style.display = "inline-block";
                }
                else if (personalInfoDisplay == 'E')
                {
                    elem.classList.add("tab");
                    elem.style.display = "inline-block";
                    document.getElementById("mobilePersonal").style.display = "none";
                }
                else if (personalInfoDisplay == 'M')
                {
                    elem.classList.add("tab");
                    elem.style.display = "inline-block";
                    document.getElementById("emailPersonal").style.display = "none";
                }
                else
                {
                    elem.classList.add("tab");
                    elem.style.display = "inline-block";
                }
            }
        }
    }


    document.getElementById('payMethodDisplay').innerText = label;
    document.getElementById('payMethod').innerText = pay_methods;
    $("#topRight").show();
    $("#backArrow").show();
    $("#options").hide();
    $('#' + pay_methods).addClass('active');

    $("#" + pay_methods + "Option").show();

    // this if is for methods who do not have any options and directly the form gets opened on click
    if (pay_methods == "UPI")
    {
        typeLabel = label;
        showTab(0, pay_methods);
        $("#paybutton").removeClass("disabledbutton");
            }
    else if (pay_methods == "Wallets")
    {
        typeLabel = label;
        showTab(0, pay_methods);
        selectedWallet();
    }
    else if (pay_methods == "Net-Banking")
    {
        typeLabel = label;
        showTab(0, pay_methods);
        BankIndia();
    }
    //else if (pay_methods == "CupUpi")
    //{
    //    typeLabel = label;
    //    showTab(0, pay_methods);
    //    Cup UpiHideShow('CupUpi');
    //}//'NetBankingBangladesh','WalletBangladesh'

    else if (pay_methods == "ACH" || pay_methods == "ROMCARD" || pay_methods == "TOJIKA" || pay_methods == "ZOTA" ||
            pay_methods == "NetBankingBangladesh" || pay_methods == "WalletBangladesh" || pay_methods == "FASTPAY")
    {
        typeLabel = label;
        showTab(0, pay_methods);
    }
    else if(pay_methods == "MobileBankingBangla" || pay_methods == "TIGERPAY" || (pay_methods == "MobileMoneyAfrica" && (banglaCartType == "Airtel_Uganda" || banglaCartType == "MTN_Uganda" || pay_methods == "CajaRural"))) {

        typeLabel = label;
        showTab(0, pay_methods);
        $("#paybutton").hide();
    }else if(pay_methods == "GiftCardAfrica"){
        typeLabel = label;
        showTab(0, pay_methods);
        document.getElementById('payMethodDisplay').innerText   = "Redboxx Gift Card Code"
    }

    backButtonConfig();
}

function selectedWallet()
{
    var inp = document.getElementById('wallets').querySelectorAll('input');
    if (inp)
    {
        for (var i = 0; i <= inp.length; i++)
        {
            if (inp[i])
            {
                if (inp[i].checked)
                {
                    $("#paybutton").removeClass("disabledbutton");
                    break;
                }
                else
                {
                    $("#paybutton").addClass("disabledbutton");
                }
            }
        }
    }
    else
    {
        $("#paybutton").addClass("disabledbutton");
    }
}


function BankIndia()
{
    var li = document.getElementById("NetBankingIndiaTab").querySelectorAll('li');
    var select = document.getElementById("netBankingBankName");

    if (select.selectedIndex)
    {
        $("#paybutton").removeClass("disabledbutton");
    }
    else
    {
        $("#paybutton").addClass("disabledbutton");
        for (var i = 0; i < li.length; i++)
        {
            li[i].classList.remove('selectedBank');
        }
    }
}


function selectBank(bankname)
{
    var select = document.getElementById("netBankingBankName");
    for (var i = 0; i < select.options.length; i++)
    {
        if (bankname == 'ICICI' || bankname == 'AXIS' || bankname == 'KOTAK' || bankname == 'YES' || bankname == 'HDFC')
        {
            if (select.options[i].text.indexOf(bankname) > -1)
            {
                select.selectedIndex = i;
                break;
            }
        }
        else if (select.options[i].text.toUpperCase().indexOf(bankname.toUpperCase()) > -1)
        {
            select.selectedIndex = i;
            break;
        }
    }

    var li = document.getElementById("NetBankingIndiaTab").querySelectorAll('li');
    for (var i = 0; i < li.length; i++)
    {
        li[i].classList.remove('selectedBank');
    }
    document.getElementById(bankname).classList.add("selectedBank");

    BankIndia();
}

function dropdownbank(val)
{
    var dropdownvalue = val.options[val.selectedIndex].innerHTML;
    var li = document.getElementById("NetBankingIndiaTab").querySelectorAll('li');
    for (var i = 0; i < li.length; i++)
    {
        li[i].classList.remove('selectedBank');
    }

    if (document.getElementById(dropdownvalue))
    {
        document.getElementById(dropdownvalue).classList.add("selectedBank");
    }

    BankIndia();
}

function backToHome()
{
    $(".check_mark").addClass("hide").fadeOut();
    if ($("#" + pay_methods).find(".active")[1])
    {
        var formname = $("#" + pay_methods).find(".active")[1].getElementsByTagName("form")[0].id;
        var x = $("#" + formname).find(".tab");
        if (x[currentTab])
        {
            x[currentTab].style.display = "none";
            currentTab = 0;
        }
        else
        {
            x[0].style.display = "none";
            currentTab = 0;
        }
    }

    var activeTab = document.getElementById("payMethod").innerText;
    if (activeTab.indexOf(" > ") > -1)
    {
        activeTab = document.getElementById("payMethod").innerText.split(" > ")[0];

    }
    else if (activeTab == ErrorEng)
    {
        //activeTab = document.getElementById("topLeft").innerText;
    }
    else
    {
        activeTab = document.getElementById("payMethod").innerText;//.split(" ").join("");
    }

    if ($('#' + pay_methods).find(".active")[1])
    {
        var fname = $('#' + pay_methods).find(".active")[1].getElementsByTagName("form")[0].id;
        document.getElementById(fname).reset();
    }

    $('#' + pay_methods).removeClass('active');
    $("#options").show();
    $('#paybutton').addClass('hide');

    if (!($('#' + activeTab)[0]))
    {
        $("#card").removeClass('active');
        $("#dcard").removeClass('active');
    }
    else
    {
        $("#" + activeTab).removeClass('active');
    }

    document.getElementById('payMethodDisplay').innerText = SelectPayMethodText;
    document.getElementById('payMethod').innerText = "Select a Payment Method!";
    $("#topRightError").hide();
    $("#topRight").hide();
    $("#backArrow").hide();
    $("#options").show();

}

function back()
{
    var activeTab = document.getElementById("payMethod").innerText;
    $(".check_mark").addClass("hide").fadeOut();

    if ($("#" + pay_methods).find(".active")[1])
    {
        var formname = $("#" + pay_methods).find(".active")[1].getElementsByTagName("form")[0].id;
        var x = $("#" + formname).find(".tab");
        console.log("in iffffffff ", activeTab == "ERROR", x, formname);
        if (activeTab == ErrorEng)
        {
            x[0].style.display = "block";
        }
        else
        {
            if (x[currentTab])
            {
                x[currentTab].style.display = "none";
                currentTab = 0;
            }
            else
            {
                x[0].style.display = "none";
                currentTab = 0;
            }
        }
    }
    else
    {
        console.log("in ellllllllssssssssssssssssssssseeeeeeeeeeee");
    }

    if (!(activeTab.indexOf(" > ") > -1))
    {
        activeTab = document.getElementById("payMethod").innerText.split(" ").join("");
    }
    else
    {
        activeTab = document.getElementById("payMethod").innerText.split(" > ")[0];
    }

    for (var i = 0; i < all_options.length; i++)
    {
        console.log(all_options[i], activeTab, pay_methods);
        //+ " > " + ($("#"+method).find(".tab").find("p")[currentTab].innerText);
        if (all_options[i] == activeTab)
        {
            console.log("in first if");
            $("#" + activeTab).removeClass('active');
            backToHome();
            if (document.getElementsByClassName('terms-checkbox')[0])
            {
                disablePayButton();
            }
            else
            {
                console.log("NO CHECKBOX")
            }
            break;
        }
        else if (!($('#' + activeTab)[0]))
        {
            if ($('#' + pay_methods).find(".active")[1])
            {
                var fname = $('#' + pay_methods).find(".active")[1].getElementsByTagName("form")[0].id;
                document.getElementById(fname).reset();
            }
            $("#card").removeClass('active');
            $("#dcard").removeClass('active');
            $("#" + pay_methods + "Option").show();

            document.getElementById('payMethodDisplay').innerText = label;
            document.getElementById('payMethod').innerText = pay_methods;
            if (document.getElementsByClassName('terms-checkbox')[0])
            {
                disablePayButton();
            }
            else
            {
                console.log("NO CHECKBOX")
            }
        }
        else
        {
            if ($('#' + activeTab)[0])
            {
                if ($('#' + activeTab)[0].getElementsByTagName("form")[0])
                {
                    var fname = $('#' + activeTab)[0].getElementsByTagName("form")[0].id;
                    document.getElementById(fname).reset();
                }
            }
            $("#" + activeTab).removeClass('active');
            $("#" + pay_methods + "Option").show();

            document.getElementById('payMethodDisplay').innerText = label;
            document.getElementById('payMethod').innerText = pay_methods;

            $("#paybutton").addClass("hide");
            if (document.getElementsByClassName('terms-checkbox')[0])
            {
                disablePayButton();
            }
            else
            {
                console.log("NO CHECKBOX")
            }

        }
    }
    backButtonConfig();
}

var apiCall = "";
function walletHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#WalletOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;


    if (method == "QR")
    {
        document.getElementById("paybutton").classList.add("hide");
        apiCall = setInterval('QRAjaxCall()', 5000);
    }
    else if (method == "SQR")
    {
        document.getElementById("paybutton").classList.add("hide");
    }
    else if (method == "Momo_wallet")
    {
        show_loader();
        document.Momo_walletForm.submit();
    }
    else if (method == "Zalo_Pay")
    {
        show_loader();
        document.Zalo_PayForm.submit();
    }else if (method == "VIETTEL_PAY")
    {
        show_loader();
        document.VIETTEL_PAYForm.submit();
    }else if (method == "TRUEWALLET")
    {
        show_loader();
        document.TRUEWALLETForm.submit();
    }else if (method == "OVOWALLET")
    {
        show_loader();
        document.OVOWALLETForm.submit();
    }else if (method == "GOPAY")
    {
        show_loader();
        document.GOPAYForm.submit();
    }
    showTab(currentTab, method);
    backButtonConfig();
}

function VoucherHideShow(method, displayLabel)
{
    method          = method+"_V";
    typeLabel       = displayLabel;
    $("#VouchersOption").hide();
    $("#" + method).addClass('active');
    if(method== "FLEXEPIN_VOUCHER_V")
    {
        $('#payNowLabel').text("Redeem");
    }
    else
    {
        $('#payNowLabel').text(payNoText)
    }
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function prepaidCardHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#PrepaidCardsOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function netBankingHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#NetBankingOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    if (method == "VirtualAccount")
    {
        show_loader();
        document.VirtualAccountForm.submit();
    }
    showTab(currentTab, method);
    backButtonConfig();
}

function chkHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#CHKOption").hide();
    if (method == "CHK")
    {
        $("#" + method + "ID").addClass('active');
    }
    else if (method == "eCheck")
    {
        $("#" + method + "CHKID").addClass('active');
    }
    else
    {
        $("#" + method).addClass('active');
    }
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function sepaHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#SEPAOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function cryptoHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    // $("#CryptoOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    document.elegroForm.submit();
    //showTab(currentTab, method);
    backButtonConfig();
}
function bitcoinHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#BitCoinOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText   = typeLabel;
    document.getElementById('payMethod').innerText          = method;

    if (method == "BCPAYGATE")
    {
        document.BCPAYGATEForm.submit();
        show_loader();
    }else if (method == "BITCLEAR")
    {
        document.BITCLEARForm.submit();
        show_loader();
    }
    showTab(currentTab, method);
    backButtonConfig();
}

function paygHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#PayGOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    if (method == "DUSPAYDD")
    {
        document.duspayDDForm.submit();
    }
    backButtonConfig();
}

function payBoutiqueHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#PayBoutiqueOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    //showTab(currentTab, method);
    document.pbForm.submit();
    backButtonConfig();
}

function eCheckHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#eCheckOption").hide();
    if (method == "CHK")
    {
        $("#" + method + "ID").addClass('active');
    }
    else if (method == "eCheck")
    {
        $("#" + method + "ID").addClass('active');
        method = method + "ID";
    }
    else
    {
        $("#" + method).addClass('active');
    }
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function bankTransferGetHideShow(method, displayLabel,isSubmit)
{
    console.log("method == " + method + ", displayLabel == " + displayLabel)
    typeLabel = displayLabel;
    if(method != "Transfr")
    {
        $("#BankTransferOption").hide();
        $("#" + method).addClass('active');
    }
    if(method == "Transfr" || method == "BankTransferPG")
    {
        document.getElementById('payMethodDisplay').innerText   = "Bank Transfer";
        document.getElementById('payMethod').innerText          = method;
    }else if (method == "EFT")
    {
        document.getElementById('payMethodDisplay').innerText   = typeLabel;
        document.getElementById('payMethod').innerText          = method;
        show_loader();
        document.EFTForm.submit();
    }else if (method == "VFD")
    {
        document.getElementById('payMethodDisplay').innerText   = typeLabel;
        document.getElementById('payMethod').innerText          = method;
        show_loader();
        document.VFDForm.submit();
    }
    else
    {
        document.getElementById('payMethodDisplay').innerText   = typeLabel;
        document.getElementById('payMethod').innerText          = method;
    }
    showTab(currentTab, method);
    if (method == "JPBANK")
    {
        document.JPBANKForm.submit();
        show_loader();
    }
    if (isSubmit && method == "Transfr")
    {
        document.TransfrForm.submit();
        show_loader();
    }
    backButtonConfig();
}
function mobileBankingHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    method=method+"_MB";
    $("#MobileBankingOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}

function CajaRuralHideShow(method, displayLabel,isAutoSubmit)
{
    typeLabel = displayLabel;
    method=method+"_RP";
    $("#CajaRuralOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    $('#emailaddr_CajaRural').val($('#email_nbi').val());
    $('#firstname_CajaRural').val($('#firstname_nbi').val());
    $('#lastname_CajaRural').val($('#lastname_nbi').val());
    $('#phone-CC_CajaRural').val($('#phonecc_id_nbi').val());
    $('#telno_CajaRural').val($('#RPphoneNum').val());

    if(isAutoSubmit){

        document.CajaRuralForm.submit();
        show_loader();
    }

    showTab(currentTab, method);
    backButtonConfig();
}


function dokuHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#DokuOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    if (method == "DOKU")
    {
        document.dokuForm.submit();
        show_loader();
    }
    showTab(currentTab, method);
    backButtonConfig();
}

function ecospendHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#EcospendOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    var form_name = method+"form_ES";
    document.forms[form_name].submit();
    show_loader();
    /*if (method == "INSTANTPAYMENT")
     {
     document.INSTANTPAYMENTform_ES.submit();
     show_loader();
     }
     if (method == "STANDINGORDERS")
     {
     document.STANDINGORDERSform_ES.submit();
     show_loader();
     }
     if (method == "PAYBYLINK")
     {
     document.PAYBYLINKform_ES.submit();
     show_loader();
     }
     if (method == "SCHEDULEDPAYMENT")
     {
     document.SCHEDULEDPAYMENTform_ES.submit();
     show_loader();
     }*/


    //showTab(currentTab, method);
    backButtonConfig();
}

function instantBankTransferHideShow(method, displayLabel, paymentType,countryValue)
{
    typeLabel   = displayLabel;
    method      = method;

    $("#" + paymentType+"Option").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText   = typeLabel;
    document.getElementById('payMethod').innerText          = method;
    showTab(currentTab, method);
    backButtonConfig();
    setCountry(countryValue,'country_input_africa_'+method)
    pincodecc('country_input_africa_'+method,'country_'+method,'phonecc_id_'+method,'phonecc_'+method)
}


function delayedBankTransferHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    method=method+"_DBT";
    $("#DelayedBankTransferOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}


function giftCardsHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    method=method+"_GC";
    $("#GiftCardsOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}
function MTNMOMOHideShow(method, displayLabel)
{

    typeLabel = displayLabel;
    method    = method + "_MTN";
    $("#MTNMOMOOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}


function SMARTFASTPAYHideShow(method, displayLabel)
{

    typeLabel = displayLabel;
    method    = method;
    $("#SMARTFASTPAYOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    backButtonConfig();
}



function fastpayHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#Option").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    if (method == "ECOSPEND")
    {
        document.ecospendForm.submit();
        show_loader();
    }
    //showTab(currentTab, method);
    backButtonConfig();
}


var availableCardType = [];
var terminal;

function cardInfo(method)
{
    typeLabel = label;
    if (document.getElementById("VISA"))
    {
        availableCardType.push("VISA");
    }
    if (document.getElementById("MC"))
    {
        availableCardType.push("MC");
    }
    if (document.getElementById("AMEX"))
    {
        availableCardType.push("AMEX");
    }
    if (document.getElementById("DISC"))
    {
        availableCardType.push("DISC");
    }
    if (document.getElementById("DINER"))
    {
        availableCardType.push("DINER");
    }
    if (document.getElementById("JCB"))
    {
        availableCardType.push("JCB");
    }
    if (document.getElementById("MAESTRO"))
    {
        availableCardType.push("MAESTRO");
    }
    if (document.getElementById("INSTAPAYMENT"))
    {
        availableCardType.push("INSTAPAYMENT");
    }
    if (document.getElementById("RUPAY"))
    {
        availableCardType.push("RUPAY");
    }

    $("#CreditCardsOption").hide();
    $("#card").addClass('active');
}

var availableDebitCardType = [];

function debitCardHideShow(method)
{
    typeLabel = label;
    if (document.getElementById("VISA_DC"))
    {
        availableDebitCardType.push("VISA");
    }
    if (document.getElementById("MC_DC"))
    {
        availableDebitCardType.push("MC");
    }
    if (document.getElementById("AMEX_DC"))
    {
        availableDebitCardType.push("AMEX");
    }
    if (document.getElementById("DISC_DC"))
    {
        availableDebitCardType.push("DISC");
    }
    if (document.getElementById("DINER_DC"))
    {
        availableDebitCardType.push("DINER");
    }
    if (document.getElementById("JCB_DC"))
    {
        availableDebitCardType.push("JCB");
    }
    if (document.getElementById("MAESTRO_DC"))
    {
        availableDebitCardType.push("MAESTRO");
    }
    if (document.getElementById("INSTAPAYMENT_DC"))
    {
        availableDebitCardType.push("INSTAPAYMENT");
    }
    if (document.getElementById("RUPAY_DC"))
    {
        availableDebitCardType.push("RUPAY");
    }

    $("#DebitCardsOption").hide();
    $("#dcard").addClass('active');
}

function CupUpiHideShow(method, displayLabel)
{
    availableCardType.push("VISA");
    availableCardType.push("MC");
    availableCardType.push("AMEX");
    availableCardType.push("DISC");
    availableCardType.push("DINER");
    availableCardType.push("JCB");
    availableCardType.push("MAESTRO");
    availableCardType.push("INSTAPAYMENT");
    availableCardType.push("RUPAY");

    typeLabel = displayLabel;
    $("#CupUpiOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    showTab(currentTab, method);
    if (method == "SecurePay")
    {
        document.securePayForm.submit();
    }
    backButtonConfig();
}

function show_loader()
{
    setTimeout(function ()
    {
        $(".check_mark").removeClass("hide");
        $("#load").removeClass("hide");
        $('.circle-loader').removeClass('load-complete');
        $("#error_div").addClass("hide");
        $("#continuebutton").addClass("hide");
        $("#error").addClass("hide");
        setTimeout(function ()
        {
            $("#error_div").removeClass("hide");
            $("#continuebutton").removeClass("hide");
            $("#error").removeClass("hide");
            $("#load").addClass("hide");
        }, 1800000);
    }, 0);
}


function success_button()
{
    //$(".check_mark").addClass("hide").fadeOut();
    //backToHome();
    $("confirmationForm").submit();
}

function pay()
{
    if ($("#" + pay_methods).find(".active")[1] != undefined)
    {
        var method = $("#" + pay_methods).find(".active")[1].id;
        var formname = $("#" + method).find("form")[0].id;

        if (!validateForm(formname))
        {
            console.log("in if errorsss");
            return false;
        }
        else
        {
            console.log("in first Success", formname);
            var marketplace = "N";
            if ($("#marketPlace"))
                marketplace = $("#marketPlace").val();
            if (marketplace == "split")
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckoutSplit');
                $("#" + formname).submit()
            }
            else
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckout');
                $("#" + formname).submit()
            }

            setTimeout(function ()
            {
                $(".check_mark").removeClass("hide");
                $("#load").removeClass("hide");
                $('.circle-loader').removeClass('load-complete');
                $("#error_div").addClass("hide");
                $("#continuebutton").addClass("hide");
                $("#error").addClass("hide");
                setTimeout(function ()
                {
                    $("#error_div").removeClass("hide");
                    $("#continuebutton").removeClass("hide");
                    $("#error").removeClass("hide");
                    $("#load").addClass("hide");
                }, 1800000);
            }, 0);
        }
    }
    else if ($("#" + pay_methods).find(".active")[0] != undefined)
    {
        var method = $("#" + pay_methods).find(".active")[0].id;
        var formname = $("#" + method).find("form")[0].id;

        if (!validateForm(formname))
        {
            console.log("in else if errorsss");
            return false;
        }
        else
        {
            console.log("in Second Success", formname);
            var marketplace = "N";
            if ($("#marketPlace"))
                marketplace = $("#marketPlace").val();
            if (marketplace == "split")
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckoutSplit');
                $("#" + formname).submit()
            }
            else
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckout');
                $("#" + formname).submit()
            }

            setTimeout(function ()
            {
                $(".check_mark").removeClass("hide");
                $("#load").removeClass("hide");
                $('.circle-loader').removeClass('load-complete');
                $("#error_div").addClass("hide");
                $("#continuebutton").addClass("hide");
                $("#error").addClass("hide");
                setTimeout(function ()
                {
                    $("#error_div").removeClass("hide");
                    $("#continuebutton").removeClass("hide");
                    $("#error").removeClass("hide");
                    $("#load").addClass("hide");
                }, 1800000);
            }, 0);
        }
    }
    else if ($("#" + pay_methods).find("form")[0].id)
    {
        var formname = $("#" + pay_methods).find("form")[0].id;
        if (!validateForm(formname))
        {
            console.log("in else if 2 errorsss");
            return false;
        }
        else
        {
            console.log(" in Third Success", formname);
            var marketplace = "N";
            if ($("#marketPlace"))
                marketplace = $("#marketPlace").val();
            if (marketplace == "split")
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckoutSplit');
                $("#" + formname).submit()
            }
            else
            {
                $("#" + formname).attr('action', '/transaction/SingleCallCheckout');
                $("#" + formname).submit()
            }

            //success code
            setTimeout(function ()
            {
                $(".check_mark").removeClass("hide");
                $("#load").removeClass("hide");
                $('.circle-loader').removeClass('load-complete');
                $("#error_div").addClass("hide");
                $("#continuebutton").addClass("hide");
                $("#error").addClass("hide");
                setTimeout(function ()
                {
                    $("#error_div").removeClass("hide");
                    $("#continuebutton").removeClass("hide");
                    $("#error").removeClass("hide");
                    $("#load").addClass("hide");
                }, 1800000);
            }, 0);
        }
    }
    else
    {
        console.log("no form");
    }
}


function submitForm(pVal, cVal)
{
    /*$('#paymenttype').val(pVal);
     $('#cardType').val(cVal);
     $("#myform").submit();*/
}

var confirm_status = "";
function QRAjaxCall()
{
    var trackindID = document.getElementById("QRTrackingID").value;
    console.log("In QR ajax call -----" + trackindID);

    $.ajax({
        url: '/transaction/checkConfirmation',
        async: false,
        type: 'POST',
        data: {trackingId: trackindID, param: "ajax"},
        success: function (data, status)
        {
            console.log(data);
            confirm_status = data;
        },
        error: function (xhr, status, error)
        {
            console.log(status, error);
        }
    });

    console.log("AFTER AJAX  ------- status ---------", confirm_status);

    if (confirm_status.status == "success" || confirm_status.status == "fail")
    {
        document.getElementById("successMsg").classList.remove("hide");
        document.getElementById("successMsg").innerHTML = confirm_status.status == "success" ? "Transaction Successful !" : "Transaction Failed !";
        document.getElementById("success_status").value = confirm_status.status;
        clearInterval(apiCall);
        document.QRconfirmForm.submit();
    }
}

function toggleEMIRomCard(checkboxId, emiId)
{
    if (document.getElementById(checkboxId).checked)
    {
        document.getElementById(emiId).disabled = false;
    }
    else
    {
        document.getElementById(emiId).disabled = true;
    }
}

function emiFlag(type, firstSix, payMode, CardNo, Curr)
{
    var memberId = document.getElementById("memberId").value;
    var binRouting = document.getElementById("binRouting").value;
    var terminal = document.getElementById('terminalid').value;
    var token = document.getElementById("CCToken").value;
    var result;
    var dataObj = {
        cardType: type,
        firstSix: firstSix,
        memberId: memberId,
        binRouting: binRouting,
        terminal: terminal,
        payMode: payMode,
        cardNo: CardNo,
        Currency: Curr
    };

    $.ajax({
        url: '/transaction/EmiServlet?ctoken=' + token,
        async: true,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(dataObj),
        success: function (data, status)
        {
            console.log(data, status);
            result = data;
            displayEmi(result);
        },
        error: function (xhr, status, error)
        {
            console.log(status, error);
            result.status = status;
        }
    });
}

function displayEmi(result)
{
    var data = result;
    var emi = pay_methods == "CreditCards" ? document.getElementById("EMIOption") : document.getElementById("EMIOptiondc");
    var select = pay_methods == "CreditCards" ? document.getElementById("installment") : document.getElementById("installmentdc");
    var opt = data.emi_period ? data.emi_period.split(",") : "";
    var option;
    var startD = new Date(data.startdate);
    var endD = new Date(data.enddate);
    var today = new Date();

    if (today >= startD && today <= endD)
    {
        if (data.emiSupport == "Y")
        {
            emi.style.display = "inline-block";

            if (select.options.length)
            {
                select.innerHTML = "";
            }

            for (var a = 0; a <= opt.length; a++)
            {
                option = document.createElement("option");
                if (a == opt.length)
                {
                    option.text = SelectMonths;
                    option.value = 0;
                    option.selected = "selected";
                    select.add(option, 0);
                }
                else
                {
                    option.text = opt[a] + " " + Months;
                    option.value = opt[a];
                    select.add(option);
                }
            }
        }
        else
        {
            emi.style.display = "none";
        }
    }
    else
    {
        emi.style.display = "none";
    }
}

function toggleEMI()
{
    var select = pay_methods == "CreditCards" ? document.getElementById("installment") : document.getElementById("installmentdc");
    select.disabled = !select.disabled;

    if (select.disabled)
    {
        select.selectedIndex = 0;
    }
}

function validateEmi()
{
    var select = pay_methods == "CreditCards" ? document.getElementById("installment") : document.getElementById("installmentdc");
    var divID = pay_methods == "CreditCards" ? document.getElementById("emiSelect").id : document.getElementById("emiSelectdc").id;

    if (select.value == 0)
    {
        error = InvalidEMI;
        valid = false;
        error_msg(error, divID);
    }
    else
    {
        valid = true;
    }


    return valid;
}


// multiple step form START

//var currentTab = 0; // Current tab is set to be the first tab (0)

function showTab(n, method)
{
    console.log("showTab >>> "+n, method);

    // This function will display the specified tab of the form...
    if ($("#" + method).find("form")[0])
    {
        var formname = $("#" + method).find("form")[0].id;
        var x = $("#" + formname).find(".tab");

        if (x[n])
        {
            x[n].style.display = "inline-block";
        }
        else
        {
            console.log("showTab formname >>> "+ formname);
            /*if(x[n]){
             x[n].style.display = "none";
             $("#"+formname).find(".form-btn")[0].style.display = "none";
             $("#paybutton").addClass("hide");
             }*/
            currentTab = 0;
            n = 0;
            $("#" + formname).find(".form-btn")[0].style.display = "none";
            x[0].style.display = "inline-block";
            if (x[1])
            {
                x[1].style.display = "none";
            }
            $("#paybutton").addClass("hide");
        }

        //... and fix the Previous/Next buttons:
        if (n == 0)
        {
            document.getElementById('payMethodDisplay').innerText = typeLabel;
            document.getElementById('payMethod').innerText = method;

            if ($("#" + formname).find(".form-btn")[0])
            {
                $("#" + formname).find(".form-btn")[0].style.display = "none";
            }
            $("#paybutton").addClass("hide");
            $("#" + formname).find("#terms").addClass("hide");
        }
        else
        {

            $("#" + formname).find(".form-btn")[0].style.display = "inline";
            $("#paybutton").addClass("hide");
            $("#" + formname).find("#terms").addClass("hide");

            document.getElementById('payMethodDisplay').innerText = typeLabel;
            document.getElementById('payMethod').innerText = method;
        }
        if (x[n])
        {
            if (n == (x.length - 1))
            {
                if ($("#" + formname).find(".form-btn")[1])
                {
                    $("#" + formname).find(".form-btn")[1].style.display = "none";
                }
                $("#paybutton").removeClass("hide");
                $("#" + formname).find("#terms").removeClass("hide");
            }
            else
            {
                //document.getElementById("nextBtn").innerHTML = "Next";
                $("#" + formname).find(".form-btn")[1].style.display = "block";
                $("#" + formname).find(".form-btn")[1].innerText = NextText;
            }
        }
        else
        {
            $("#" + formname).find(".form-btn")[1].style.display = "none";
            $("#paybutton").removeClass("hide");
            $("#" + formname).find("#terms").removeClass("hide");
        }
    }
}

function nextPrev(n)
{
    var formname, noOfTabs;
    console.log("in begining ", currentTab + " " + pay_methods);
    var isExsits        = $("#MobileBankingBanglaTab").length;
    var isDirectSubmit  = $("#MobileBankingBanglaTab").children("li").length;

    var isExsitsRP          = $("#CajaRuralTab").length;
    var isDirectSubmitRP    = $("#CajaRuralTab").children("li").length;

    var isExsitsBT          = $("#BitCoinTab").length;
    var isDirectSubmitBT    = $("#BitCoinTab").children("li").length;

    if (pay_methods == "BITCOIN" && isExsitsBT == 1 && isDirectSubmitBT == 1)
    {
        formname = $("#" + pay_methods).find("form")[0].id;
        var displayLabel = banglaCartType;
        console.log("displayLabel ", displayLabel, " pay_methods", pay_methods, " formname " + formname);
        document.getElementById('payMethodDisplay').innerText = displayLabel;
        bitcoinHideShow(displayLabel, pay_methods)
    }

    if (pay_methods == "CajaRural" && isExsitsRP == 1 && isDirectSubmitRP == 1)
    {
        formname = $("#" + pay_methods).find("form")[0].id;
        if (n == 1 && !validateForm(formname))
        {
            return false;
        }
        var displayLabel = CajaRuralCartType;
        console.log("displayLabel ", displayLabel, " pay_methods", pay_methods, " formname " + formname);
        document.getElementById('payMethodDisplay').innerText = displayLabel;
        CajaRuralHideShow(displayLabel, pay_methods, true)
    }
    var isTIGERPAYTabExsits            = $("#TIGERPAYTab").length;
    var isTIGERPAYTabDirectSubmit      = $("#TIGERPAYTab").children("li").length;


    if(pay_methods == "TIGERPAY" && isTIGERPAYTabExsits == 1 && isTIGERPAYTabDirectSubmit == 1 ){
        formname =  $("#" + pay_methods).find("form")[0].id;
        if (n == 1 && !validateForm(formname)){
            return false;
        }
        var displayLabel  = banglaCartType;
        console.log("displayLabel ",displayLabel," pay_methods",pay_methods," formname "+formname);
        document.getElementById('payMethodDisplay').innerText = displayLabel;
        TIGERPAYHideShow(displayLabel,pay_methods, true)
    }

    if ($("#" + pay_methods).find(".active")[1])
    {
        console.log("in first if ", currentTab);
        formname = $("#" + pay_methods).find(".active")[1].getElementsByTagName("form")[0].id;
    }
    else if ($("#" + pay_methods).find(".active")[0])
    {
        console.log("in first else if ", currentTab);
        formname = $("#" + pay_methods).find(".active")[0].getElementsByTagName("form")[0].id;
        //currentTab = 0;
    }
    else
    {
        console.log("in first else ", currentTab);
        formname = $("#" + pay_methods).find("form")[0].id;
        //currentTab = 0;
    }

    // This function will figure out which tab to display
    if(OTPInfoDisplay == "Y" && $("#SuccessOTP").val()!="Y" && (personalInfoDisplay == "Y" || personalInfoDisplay == "E" ||  personalInfoDisplay == "M"))
    {
        /* $("#CancelModalCreditcard").hide();*/
        if (n == 1 && !validateForm(formname))
        {
            return false;
        }else
        {
            validateEmailOtp(personalInfoDisplay);
        }
    }
    else
    {
        noOfTabs = $("#" + formname).find(".tab");                    // document.getElementsByClassName("tab");
        // Exit the function if any field in the current tab is invalid:
        if (n == 1 && !validateForm(formname)) return false;

        noOfTabs[currentTab].style.display = "none";                // Hide the current tab:
        console.log("n ", n, " currentTab ", currentTab, "formname", formname);
        currentTab = currentTab + n;                    // Increase or decrease the current tab by 1:
        console.log("currentTab ", currentTab);
        // if you have reached the end of the form...
        if (currentTab > noOfTabs.length)
        {
            // ... the form gets submitted:
            //document.getElementById("regForm").submit();
            return false;
        }

        var tab;

        if ($("#" + pay_methods).hasClass("tab-pane"))
        {
            if ($("#" + pay_methods).find(".active")[0])
            {

                if ($("#" + pay_methods).find(".active")[1])
                {
                    console.log("in if currentTab", currentTab);
                    tab = $("#" + pay_methods).find(".active")[1].id;
                    showTab(currentTab, tab);
                }
                else
                {
                    console.log("in else currentTab", currentTab);
                    tab = $("#" + pay_methods).find(".active")[0].id;
                    showTab(currentTab, tab);
                }
            }
            else
            {
                tab = pay_methods;
                showTab(currentTab, tab);
            }
        }
        else
        {
            back();
        }
    }

}


function showaddress()
{
    if (addressDisplayFlag && addressValidation)
    {
        if (document.getElementById("addressinfo").classList.contains("hide"))
        {
            document.getElementById("addressinfo").classList.remove("hide");
            if (document.getElementById("EMIOption").style.display != "inline-block")
            {
                $('#cardinfo').animate({
                    scrollTop: (0, 235)
                }, 500);
            }
        }
    }
}

function showaddressDebitCard()
{
    if (addressDisplayFlagDebitCard && addressValidationDebitCard)
    {
        if (document.getElementById("addressinfodc").classList.contains("hide"))
        {
            document.getElementById("addressinfodc").classList.remove("hide");
            if (document.getElementById("EMIOptiondc").style.display != "inline-block")
            {
                $('#cardinfodc').animate({
                    scrollTop: (0, 235)
                }, 500);
            }
        }
    }
}

function showaddressCupUpi()
{
    if (document.getElementById("addressinfo_cu").classList.contains("hide"))
    {
        document.getElementById("addressinfo_cu").classList.remove("hide");

        $('#cardinfo_cu').animate({
            scrollTop: (0, 235)
        }, 500);
    }
}


function validateForm(formname)
{
    // This function deals with validation of the form fields
    console.log("formname----> "+formname,"  "+personalInfoValidation);
    var x, y, i, tabname, valid = true;
    x = $("#" + formname).find(".tab");
    y = x[currentTab].getElementsByTagName("input");

    // A loop that checks every input field in the current tab:

    for (i = 0; i < y.length; i++)
    {
        console.log("tab = ", x[currentTab].id);
        // If a field is empty...
        if (personalInfoValidation == 'N' && (x[currentTab].id == "personalinfo" || x[currentTab].id == "personalinfodc"))
        {
            console.log("in personal info flag if");
            if (y[i].type == "email" && y[i].value != "")
            {
                if (validateEmail(y[i].value, document.getElementById(y[i].id).parentElement.id))
                {
                    valid = true;
                }
                else
                {
                    console.log("in email else ");
                    y[i].className += " invalid";
                    valid = false;
                }

            }
            else if ((y[i].id.indexOf("phonecc-id") > -1) && y[i].value != "")
            {
                if (document.getElementById(y[i].id).value)
                {
                    $("#" + y[i].id).removeClass("invalid");
                }
                else
                {
                    y[i].className += " invalid";
                }
            }
            else if (y[i].id.indexOf("phonenophonecc") > -1 && y[i].value != "")
            {
                var regex = new RegExp('^[0-9]+$');
                if (regex.test(document.getElementById(y[i].id).value))
                {
                    console.log("in if phoneno")
                    $("#" + y[i].id).removeClass("invalid");
                    valid = true;
                }
                else
                {
                    console.log("in else phone no")
                    if (document.getElementById(y[i].id).parentElement.id)
                        error_msg(EmptyPhoneNo, document.getElementById(y[i].id).parentElement.id)
                    y[i].className += " invalid";
                    valid = false;
                }
            }
            else if (y[i].id.indexOf("INphoneNum") > -1 && y[i].value != "")
            {
                // indian phone number
                if (validateInPhone(y[i].value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if INPhoneNumber")
                    valid = true;
                }
                else
                {
                    console.log("in else INPhoneNumber");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            else if (y[i].id.indexOf("AFphoneNum") > -1 && y[i].value != "")
            {
                var length = "10";
                if(banglaCartType == "Airtel_Uganda")
                {
                    length = "9"
                }
                // indian phone number
                if (validatePhoneAfrica(y[i].value, document.getElementById(y[i].id).parentElement.id), length)
                {
                    console.log("in if AfricaPhoneNumber")
                    valid = true;
                }
                else
                {
                    console.log("in else AfricaPhoneNumber");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            else if (y[i].id.indexOf("RPphoneNum") > -1 && y[i].value != "" && CajaRuralCartType == "CajaRural")
            {
                // indian phone number
                console.log("inside CajaRuralNumber  >>>>>>>>")
                if (validatePhoneForAll(y[i].value,document.getElementById(y[i].id).parentElement.id,9,15))
                {
                    console.log("in if CajaRuralNumber  >>>>>>>>")
                    valid = true;
                }
                else
                {
                    console.log("in else CajaRuralNumber");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            else if ((y[i].id.indexOf("csbirthdate") > -1 ) && y[i].value != "")
            {
                console.log("in else cs bithdate");
                if (clearSettleBirthDate(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    valid = true;
                }
                else
                {
                    console.log("in else cs bithdate false");
                    valid = false;
                }
            }
            else if (y[i].id.indexOf("birthdate") > -1 && y[i].value != "")
            {
                console.log("in else bithdate");
                if (birthdayCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    valid = true;
                }
                else
                {
                    return valid = false;
                }
            }
        }
        else if (addressDisplayFlag == "N" && x[currentTab].id == "addressinfo" && addressValidation == "N")
        {
            if (y[i].value == "")
            {
                console.log("in else if addressDisplayFlag", y[i].id, x[currentTab].id);
                valid = true;
            }
        }
        else if (addressDisplayFlag == "N" && ( x[currentTab].id.indexOf("cardinfo") > -1) && addressValidation == "N")
        {
            if (y[i].value == "")
            {
                console.log("in else if addressDisplayFlag cardinfo", y[i].id, x[currentTab].id);
                valid = true;
            }
            if (y[i].id.indexOf("cardNo") > -1)
            {
                if (y[i].value == "")
                {
                    console.log("in cardno", y[i].value);
                    y[i].className += " invalid";
                    valid = false;
                }
                if (isValidCardCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in first if cardno");
                    valid = true;
                }
                else
                {
                    console.log("in first else cardno");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("Expiry") > -1)
            {
                if (expiryCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if Expiry");
                    valid = true;
                }
                else
                {
                    console.log("in else Expiry");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("fname") > -1)
            {
                if (validateCardHolderName(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if firstname check");
                    valid = true;
                }
                else
                {
                    console.log("in else firstname check");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("CVV") > -1)
            {
                if (validateCVV(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if CVV")
                    valid = true;
                }
                else
                {
                    console.log("in else CVV");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("address") > -1 && y[i].value != "")
            {
                if (validateAddress(document.getElementById(y[i].id).value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("emi") > -1)
            {
                if (y[i].checked)
                {
                    if (validateEmi())
                    {
                        console.log("in if validation for emi ");
                        valid = true;
                    }
                    else
                    {
                        console.log("in else validation for emi");
                        $('#cardinfo').animate({
                            scrollTop: (0, 0)
                        }, 500);
                        return valid = false;
                    }
                }
                else
                {
                    valid = true;
                }
            }
        }
        else if (addressDisplayFlagDebitCard == "N" && x[currentTab].id == "addressinfodc" && addressValidationDebitCard == "N")
        {
            if (y[i].value == "")
            {
                console.log("in else if addressDisplayFlagDebitCard", y[i].id);
                valid = true;
            }
        }
        else if (addressDisplayFlagDebitCard == "N" && ( x[currentTab].id.indexOf("cardinfo") > -1 ) && addressValidationDebitCard == "N")
        {

            if (y[i].value == "")
            {
                console.log("in else if addressDisplayFlagDebitCard cardinfo", y[i].id);
                valid = true;
            }
            if (y[i].id.indexOf("cardNo") > -1)
            {
                if (y[i].value == "")
                {
                    console.log("in cardno", y[i].value);
                    y[i].className += " invalid";
                    valid = false;
                }
                if (isValidCardCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in first if cardno")
                    valid = true;
                }
                else
                {
                    console.log("in first else cardno");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("Expiry") > -1)
            {
                if (expiryCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if Expiry")
                    valid = true;
                }
                else
                {
                    console.log("in else Expiry");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("fname") > -1)
            {
                if (validateCardHolderName(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if firstname check")
                    valid = true;
                }
                else
                {
                    console.log("in else firstname check");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("CVV") > -1)
            {
                if (validateCVV(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if CVV")
                    valid = true;
                }
                else
                {
                    console.log("in else CVV");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("address") > -1 && y[i].value != "")
            {
                if (validateAddress(document.getElementById(y[i].id).value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("emi") > -1)
            {
                if (y[i].checked)
                {
                    if (validateEmi())
                    {
                        console.log("in if validation for emi ");
                        valid = true;
                    }
                    else
                    {
                        console.log("in else validation for emi");
                        $('#cardinfo').animate({
                            scrollTop: (0, 0)
                        }, 500);
                        return valid = false;
                    }
                }
                else
                {
                    valid = true;
                }
            }
        }
        else if (addressValidation == 'N' && x[currentTab].id == "cardinfo")
        {
            if (y[i].id.indexOf("address") > -1 && y[i].value != "")
            {
                if (validateAddress(document.getElementById(y[i].id).value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
        }
        else if (addressValidationDebitCard == 'N' && x[currentTab].id == "cardinfodc")
        {
            if (y[i].id.indexOf("address") > -1 && y[i].value != "")
            {
                if (validateAddress(document.getElementById(y[i].id).value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
        }
        else
        {
            console.log("in last else ", y[i].id, x[currentTab].id)
            if ((y[i].value == "" || y[i].value == null) && y[i].id.indexOf("state") != 1)
            {
                console.log("in else if value null", y[i].id);
                y[i].className += " invalid";
                valid = false;
            }
            if (y[i].id.indexOf("cardNo") > -1)
            {
                if (y[i].value == "")
                {
                    console.log("in cardno", y[i].value);
                    y[i].className += " invalid";
                    valid = false;
                }
                if (isValidCardCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in first if cardno")
                    valid = true;
                }
                else
                {
                    console.log("in first else cardno");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("Expiry") > -1)
            {
                if (expiryCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if Expiry")
                    valid = true;
                }
                else
                {
                    console.log("in else Expiry");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("birthdate") > -1)
            {
                if (birthdayCheck(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    valid = true;
                }
                else
                {
                    return valid = false;
                }
            }
            if (y[i].type == "email")
            {
                if (validateEmail(y[i].value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if email")
                    valid = true;
                }
                else
                {
                    console.log("in email else ");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("phonecc-id") > -1)
            {
                if (document.getElementById(y[i].id).value)
                {
                    console.log("in if phonecc")
                    $("#" + y[i].id).removeClass("invalid");
                }
                else
                {
                    console.log("in else phone cc")
                    y[i].className += " invalid";
                }
            }
            if (y[i].id.indexOf("phonenophonecc") > -1)
            {
                var regex = new RegExp('^[0-9]+$');
                if (regex.test(document.getElementById(y[i].id).value))
                {
                    console.log("in if phoneno")
                    $("#" + y[i].id).removeClass("invalid");
                    valid = true;
                }
                else
                {
                    console.log("in else phone no")
                    if (document.getElementById(y[i].id).parentElement.id)
                        error_msg(EmptyPhoneNo, document.getElementById(y[i].id).parentElement.id)
                    y[i].className += " invalid";
                    valid = false;
                }
            }
            if (y[i].id.indexOf("INphoneNum") > -1)
            {
                // indian phone number
                if (validateInPhone(y[i].value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if INPhoneNumber")
                    valid = true;
                }
                else
                {
                    console.log("in else INPhoneNumber");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("csbirthdate") > -1)
            {
                if (clearSettleBirthDate(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    valid = true;
                }
                else
                {
                    console.log("in else bithdate");
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("fname") > -1)
            {
                if (validateCardHolderName(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if firstname check")
                    valid = true;
                }
                else
                {
                    console.log("in else firstname check");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("CVV") > -1)
            {
                if (validateCVV(y[i].id, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if CVV")
                    valid = true;
                }
                else
                {
                    console.log("in else CVV");
                    $('#cardinfo').animate({
                        scrollTop: (0, 0)
                    }, 500);
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("address") > -1)
            {
                if (validateAddress(document.getElementById(y[i].id).value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("firstname") > -1)
            {
                if (y[i].value)
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("lastname") > -1)
            {
                if (y[i].value)
                {
                    console.log("in if address")
                    valid = true;
                }
                else
                {
                    console.log("in else address");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("upi") > -1)
            {
                console.log("a = ", y[i])
                if (validateUPI(y[i].id))
                {
                    console.log("in if UPI")
                    valid = true;
                }
                else
                {
                    console.log("in else UPI");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }
            if (y[i].id.indexOf("emi") > -1)
            {
                if (y[i].checked)
                {
                    if (validateEmi())
                    {
                        console.log("in if validation for emi ");
                        valid = true;
                    }
                    else
                    {
                        console.log("in else validation for emi");
                        $('#cardinfo').animate({
                            scrollTop: (0, 0)
                        }, 500);
                        return valid = false;
                    }
                }
                else
                {
                    valid = true;
                }
            }
            if (y[i].id.indexOf("AccNo") > -1)
            {
                if (valiDateData(y[i].value, document.getElementById(y[i].id).parentElement.id))
                {
                    console.log("in if ap AccNo")
                    valid = true;
                }
                else
                {
                    console.log("in esle ap AccNo");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }

            console.log("bank code >>>>>  ",y[i].id.indexOf(formname+"BankCode"));
            console.log("bank code value >>>>>  ",y[i].value);
            console.log("bank code >>>>> ",document.getElementById(y[i].id).parentElement.id);

            if (y[i].id.indexOf(formname+"BankCode") > -1 && (formname =='Internet_BankingForm' || formname =='Bank_TransferForm'))
            {
                if (y[i].value == "")
                {
                    error ="Please Select At Least One Bank"
                    error_msg(error, document.getElementById(y[i].id).parentElement.id);
                    valid = false;
                }else{
                    valid=true;
                }
            }




            if(pay_methods == "TIGERPAY"){
                if (y[i].id.indexOf("firstname_nbi") > -1)
                {
                    if (validateName(y[i].id, document.getElementById(y[i].id).parentElement.id))
                    {
                        console.log("in if ap AccNo")
                        valid = true;
                    }
                    else
                    {
                        console.log("in esle ap AccNo");
                        y[i].className += " invalid";
                        return valid = false;
                    }
                }
                if (y[i].id.indexOf("lastname_nbi") > -1)
                {
                    if (validateName(y[i].id, document.getElementById(y[i].id).parentElement.id))
                    {
                        console.log("in if ap AccNo")
                        valid = true;
                    }
                    else
                    {
                        console.log("in esle ap AccNo");
                        y[i].className += " invalid";
                        return valid = false;
                    }
                }
            }
            if (y[i].id.indexOf("RPphoneNum") > -1  && CajaRuralCartType == "CajaRural")
            {
                //&& y[i].value != ""

                console.log("inside CajaRuralNumber  >>>>>>>>")
                if (validatePhoneForAll(y[i].value,document.getElementById(y[i].id).parentElement.id,9,15))
                {
                    console.log("in if CajaRuralNumber  >>>>>>>>")
                    valid = true;
                }
                else
                {
                    console.log("in else CajaRuralNumber");
                    y[i].className += " invalid";
                    return valid = false;
                }
            }


        }
    }

    console.log("valid =", valid);

    return valid; // return the valid status
}

function fixStepIndicator(n)
{
    // This function removes the "active" class of all steps...
    var i, x = $("#" + pay_methods).find(".step");               //document.getElementsByClassName("step");
    for (i = 0; i < x.length; i++)
    {
        x[i].className = x[i].className.replace(" active", "");
    }
    //... and adds the "active" class on the current step:
    x[n].className += " active";
}

// multiple step form END


// validations
jQuery(function ($)
{
    // If JavaScript is enabled, hide fallback select field
    $('.no-js').removeClass('no-js').addClass('js');

    // When the user focuses on the credit card input field, hide the status
    $('.card input').bind('focus', function ()
    {
        $('.card .status').hide();
    });

    // When the user tabs or clicks away from the credit card input field, show the status
    $('.card input').bind('blur', function ()
    {
        // $('.card .status').show();
    });

    // Run jQuery.cardcheck on the input
    $('.card input').cardcheck({
        callback: function (result)
        {
            var status = (result.validLen && result.validLuhn) ? 'valid' : 'invalid',
                    message = '',
                    types = '';

            // Get the names of all accepted card types to use in the status message.
            for (i in result.opts.types)
            {
                types += result.opts.types[i].name + ", ";
            }
            types = types.substring(0, types.length - 2);

            // Set status message
            if (result.len < 1)
            {
                message = 'Please provide a credit card number.';
            }
            else if (!result.cardClass)
            {
                message = 'We accept the following types of cards: ' + types + '.';
            }
            else if (!result.validLen)
            {
                message = 'Please check that this number matches your ' + result.cardName + ' (it appears to be the wrong number of digits.)';
            }
            else if (!result.validLuhn)
            {
                message = 'Please check that this number matches your ' + result.cardName + ' (did you mistype a digit?)';
            }
            else
            {
                message = 'Great, looks like a valid ' + result.cardName + '.';
            }
            // // Show credit card icon
            $('.card .card_icon').removeClass().addClass('card_icon ' + result.cardClass);

            // // Show status message
            $('.card .status').removeClass('invalid valid').addClass(status).children('.status_message').text(message)
        }
    });
});


function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : evt.keyCode
    console.log("charCode ",charCode)
    if ($.inArray(charCode, [46, 8, 9, 27, 13, 110, 190, 118]) !== -1 ||
                // Allow: Ctrl+A,Ctrl+C,Ctrl+V, Command+A
            ((charCode == 65 || charCode == 86 || charCode == 67) && (evt.ctrlKey === true || evt.metaKey === true)) ||
                // Allow: home, end, left, right, down, up
            (charCode >= 35 && charCode <= 40))
    {
        // let it happen, don't do anything
        return;
    }

    if (charCode > 31 && (charCode < 48 || charCode > 57))
    {
        evt.preventDefault();
    }

    return true;
}

function onPasteNumCheck(evt)
{
    var regex = new RegExp('^[0-9]+$');
    if (regex.test(document.getElementById(evt.target.id).value))
    {
        return true;
    }
    else
    {
        document.getElementById(evt.target.id).value = "";
        return false;
    }
}



function isLetterKey(evt)
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if ((charCode < 65 || charCode > 90 ) && (charCode < 97 || charCode > 122 ) && charCode != 8)
    {
        return false;
    }
    return true;
}


function alphaNumCheck(evt)
{
    var keyCode = (evt.which) ? evt.which : event.keyCode;

    if ($.inArray(keyCode, [8, 9, 27, 13, 110, 190, 118]) !== -1 ||
                // Allow: Ctrl+A,Ctrl+C,Ctrl+V, Command+A
            ((keyCode == 65 || keyCode == 86 || keyCode == 67) && (evt.ctrlKey === true || evt.metaKey === true)))
    {
        // let it happen, don't do anything
        return;
    }

    if (!((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122) ))
    {
        evt.preventDefault();
    }

}


function onPasteAlphaNumCheck(evt)
{
    var regex = new RegExp('^[a-zA-Z0-9]+$');
    if (regex.test(document.getElementById(evt.target.id).value))
    {
        return true;
    }
    else
    {
        document.getElementById(evt.target.id).value = "";
        return false;
    }
}


function addSlash(event, id)
{
    eventCheck(event);
    var val = document.getElementById(id).value;
    if (val.length >= 2 && val.length < 4)
    {
        if ((event.keyCode != 8))
        {
            if (val.slice(0, 2) > 12)
            {
                document.getElementById(id).value = "0" + val.slice(0, 1) + "/" + val.slice(1, 4);
            }
            else
            {
                if (!(val.indexOf("/") > -1))
                {
                    document.getElementById(id).value = val.slice(0, 2) + "/" + val.slice(2, 4);
                }
            }
        }

    }

}

function eventCheck(event)
{
    if (event.keyCode == 8)
    {
        if (event.target.id == "Expiry")
        {
            document.getElementById(event.target.id).value = document.getElementById(event.target.id).value;
        }
        else
        {
            document.getElementById(event.target.id).value = "";
        }
    }
}


function clearSettleBirthDate(id, divID)
{
    var val = document.getElementById(id).value;
    var error = "";
    var valid;
    var length = val.length;
    var date = new Date();
    var year = date.getFullYear().toString();
    var y = val.slice(0, 4);
    var m = val.slice(4, 6);
    var d = val.slice(6, 8);

    if (val.length == 8)
    {
        if (y <= 1900 || y > year)
        {
            valid = false;
            error = InvalidYear;
        }
        else if (m < 1 || m > 12)
        {
            valid = false;
            error = InvalidMonth;
        }
        else if (d < 1 || d > 31)
        {
            valid = false;
            error = InvalidDay;
        }
        else
        {
            valid = true;
        }
    }
    else
    {
        valid = false;
        error = InvalidDate;
    }

    error_msg(error, divID);
    return valid;
}

function expiryCheckMonth(id, divID)
{
    var val = document.getElementById(id).value;
    var error = "";
    var valid;
    var d = new Date();
    var m = d.getMonth() + 1;
    var length;

    length = val.length;

    if (val.length == 1 && val >= 1 && val < 10)
    {
        document.getElementById(id).value = val.replace(val, "0" + val);
    }

    else if (!(val >= 1 && val <= 12))
    {
        valid = false;
        error = InvalidMonth;
    }
    else if (val <= m)
    {
        valid = false;
        error = CannotBeCurrentMonth;
    }
    else
    {
        valid = true;
    }

    error_msg(error, divID);
    return valid;
}


function expiryCheckMY(MonthId, MonthDivId, YearId, YearDivId)
{
    var val = document.getElementById(MonthId).value;
    var mm;
    if (val.length == 1 && val >= 1 && val <= 10)
    {
        mm = val.replace(val, "0" + val);
        document.getElementById(MonthId).value = mm;
    }
    else
    {
        mm = document.getElementById(MonthId).value;
    }
    var yy = document.getElementById(YearId).value;

    var d = new Date();
    var m = d.getMonth() + 1;
    var y = d.getFullYear().toString().slice(2);
    var length;
    var error = "";
    var valid;

    if (yy == "")
    {
        valid = false;
        error = InvalidYear;
        error_msg(error, YearDivId);
    }

    if (!(mm >= 1 && mm <= 12))
    {
        valid = false;
        error = InvalidMonth;
        error_msg(error, MonthDivId);
    }
    else if (yy < y)
    {
        valid = false;
        error = YearCannotBeBefore + d.getFullYear();
        error_msg(error, YearDivId);
    }
    else if (mm <= m && yy <= y)
    {
        valid = false;
        error = "Month cannot be before " + m;
        error_msg(error, MonthDivId);
    }
    else if (mm <= m && yy > y)
    {
        valid = true;
    }
    else
    {
        valid = true;
    }

    //error_msg(error , divID);
    return valid;
}


function expiryCheck(exp, divID)
{
    var val = document.getElementById(exp).value;
    var expiryValidationFlag = document.getElementById('cardExpiryDateCheck') ? document.getElementById('cardExpiryDateCheck').value : 'N';
    var numberformat = /^[0-9]{1,2}$/;
    var d = new Date();
    var m = d.getMonth() + 1;
    var y = d.getFullYear().toString().slice(2);
    var length;
    var error = "";
    var valid;
    val = val.replace("/", "")
    length = val.length;
    if (length >= 4)
    {
        //if((val.slice(0, 2) >= 01 && val.slice(0, 2) <= 09)){
        //    valid = false;
        //    error = "Please enter month in single digit" ;
        //}
        if (!(val.slice(0, 2) >= 1 && val.slice(0, 2) <= 12))
        {
            valid = false;
            error = InvalidMonth;
        }
        else if (expiryValidationFlag == 'Y' && (val.slice(0, 2) <= m && val.slice(2, 4) <= y))
        {
            valid = false;
            error = CannotBeCurrentMonth;
        }
        else if (val.slice(0, 2) <= m && val.slice(2, 4) > y)
        {
            valid = true;
            // error = "Month cannot be before current month" ;
        }
        else if (!val.slice(2, 4).match(numberformat))
        {
            valid = false;
            error = InvalidYear
        }
        else if (expiryValidationFlag == 'Y' && (val.slice(2, 4) < y))
        {
            valid = false;
            error = YearCannotBeBefore + " " + d.getFullYear();
        }
        else
        {
            valid = true;
            // if(!(val.indexOf("/")) > -1){
            //     document.getElementById(exp).value=val.slice(0, 2) + "/" + val.slice(2,4)
            // }
        }

        if (valid == true)
        {
            if (!(val.indexOf("/") > -1))
            {
                document.getElementById(exp).value = val.slice(0, 2) + "/" + val.slice(2, 4);
            }
        }

    }
    else if (length == 3)
    {
        val.replace("/", "")
        if (!(val.slice(0, 1) >= 1 && val.slice(0, 1) <= 12))
        {
            valid = false;
            error = InvalidMonth;
        }
        else if (!val.slice(0, 2).match(numberformat))
        {
            valid = false;
            error = InvalidMonth;
        }
        else if (expiryValidationFlag == 'Y' && (val.slice(0, 1) <= m && val.slice(1, 3) <= y))
        {
            valid = false;
            error = CannotBeCurrentMonth;
        }
        else if (!val.slice(1, 3).match(numberformat))
        {
            valid = false;
            error = InvalidYear;
        }
        else if (val.slice(0, 1) <= m && val.slice(1, 3) > y)
        {
            valid = true;
            // error = "Month cannot be before current month" ;
        }
        else if (!val.slice(1, 3).match(numberformat))
        {
            valid = false;
            error = InvalidYear;
        }
        else if (expiryValidationFlag == 'Y' && (val.slice(1, 3) < y))
        {
            valid = false;
            error = YearCannotBeBefore + " " + d.getFullYear();
        }
        else
        {
            valid = true;
            if (!(val.indexOf("/") > -1))
            {
                document.getElementById(exp).value = val.slice(0, 1) + "/" + val.slice(1, 3)
            }
        }

        if (valid == true)
        {
            if (!(val.indexOf("/") > -1))
            {
                document.getElementById(exp).value = val.slice(0, 1) + "/" + val.slice(1, 3)
            }
        }
    }
    else
    {
        valid = false;
        error = InvalidExpiry;
    }

    error_msg(error, divID);
    return valid;
}

function validateEmail(inputText, divID)
{
    var mailformat = /^\w+[^@!#$%^&*~"'`;:,]+([\.+-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,10})+$/;
    /*/^\w+([\.+-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,5})+$/;*/

    var error = "";
    var valid;
    try
    {
        if (inputText == "")
        {
            error = EmptyEmail;
            valid = false;
        }
        else if (!(inputText.match(mailformat)))
        {
            error = InvalidEmail;
            valid = false;
        }
        else
        {
            valid = true;
        }
    }catch(err)
    {
        error = InvalidEmail;
        valid = false;
    }
    error_msg(error, divID);
    return valid;
}

function validateInPhone(value, id){
    var error = "";
    var valid;
    var regex = new RegExp('^[0-9]+$');
    value = value.toString();
    try {
        if (value == "")
        {
            error = EmptyPhoneNo;
            valid = false;
        }
        else if (!regex.test(value)){
            console.log("in fail regex test")
            error = InvalidPhoneNo;
            valid = false;
        }
        else if (value.length < 10 || value.length > 10 )
        {
            error = InvalidPhoneNo;
            valid = false;
        }
        else
        {
            valid = true;
        }
    }
    catch(err) {
        error = InvalidPhoneNo;
        valid = false;
    }
    error_msg(error, id);
    return valid;
}



function  validatePhoneForAll(value,id,minlength,maxlength){
    console.log("inside validatePhoneForAll    >>>>>>>>>>>> ")
    console.log("inside minlength >>>>>>>>>>>> "+minlength)
    console.log("inside maxlength >>>>>>>>>>>> "+maxlength)
    var error = "";
    var valid=true;
    var regex = new RegExp('^[0-9]+$');
    value = value.toString();
    try {
        if (value == "")
        {
            error = EmptyPhoneNo;
            valid = false;
        }
        else if (!regex.test(value)){
            console.log("in fail regex test")
            error = InvalidPhoneNo;
            valid = false;
        }
        else if (value.length < minlength ||  value.length > maxlength )
        {
            console.log("<======inside else=====>")
            console.log("minlength======>"+minlength);
            console.log("maxlength======>"+maxlength);
            error = InvalidPhoneNo;
            valid = false;
        }

    }
    catch(err) {
        error = InvalidPhoneNo;
        valid = false;
    }
    error_msg(error, id);
    return valid;
}

function validateAddress(inputText, divID)
{
    var error = "";
    var valid;
    //var regex = /^[#.0-9a-zA-Z\s,-]+$/;

    if (inputText == "")
    {
        error = EmptyAddress;
        valid = false;
    }
    //else if(!regex.test(inputText))
    //{
    //    error = InvalidAddress;
    //    valid = false;
    //}
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function validateCity(inputText, divID)
{
    var error = "";
    var valid;
    var div = document.getElementById(divID);
    if (inputText == "")
    {
        error = div.querySelector('label') ? div.querySelector('label').innerHTML + " cannot be empty" : EmptyCity;
        valid = false;
    }
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function validateCountry(inputText, divID)
{
    var error = "";
    var valid;
    if (inputText == "")
    {
        error = EmptyCountry;
        valid = false;
    }
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function validateState(inputText, divID)
{
    var error = "";
    var valid;
    var div = document.getElementById(divID);
    if (inputText == "")
    {
        error = div.querySelector('label') ? div.querySelector('label').innerHTML + " cannot be empty" : EmptyState;
        valid = false;
    }
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function validateZip(inputText, divID)
{
    var error = "";
    var valid;
    var div = document.getElementById(divID);
    if (inputText == "")
    {
        error = div.querySelector('label') ? div.querySelector('label').innerHTML + " cannot be empty" : EmptyZip;
        valid = false;
    }
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function birthdayCheck(input, divID)
{
    var val = document.getElementById(input).value;
    var error = "";
    var valid;
    var x = new Date(val);
    var Cnow = new Date();
    if (val == "")
    {
        error = EmptyBirhtdate;
        $("#" + input).focus();
        valid = false;
    }
    else if (Cnow.getFullYear() - x.getFullYear() < 18)
    {
        error = "Should be above 18 years ";
        // $("#"+input).val('');
        valid = false;
    }
    else
    {
        valid = true;
    }
    error_msg(error, divID);
    return valid
}

function validateCVV(input, divID)
{
    var val = document.getElementById(input).value;
    var error = "";
    var valid;
    if (val.length < 3)
    {
        error = InvalidCVV;
        valid = false;
    }
    else
    {
        valid = true;
        if (divID == "CardCVV")
        {
            showaddress();
        }
        else if (divID == "CardCVVdc")
        {
            showaddressDebitCard();
        }
        else if (divID == "CUCardCVV")
        {
            showaddressCupUpi();
        }
    }

    error_msg(error, divID);
    return valid;
}

function validateCardHolderName(input, divID)
{
    var val = document.getElementById(input).value;
    var error = "";
    var valid;
    var regex = new RegExp('[0-9!@#$%^&*()_+?><:;,./{}]');
    if (divID)
    {
        if (val == "" || val == null || val == " ")
        {
            error = InvalidCardHolderName;
            valid = false;
        }
        else if (regex.test(val))
        {
            error = InvalidCardHolderName;
            valid = false;
        }
        else
        {
            valid = true;
        }

        error_msg(error, divID);
        return valid;
    }
}


function validateUPI(input, event)
{
    if (event && event.keyCode === 13)
    {
        event.preventDefault();
    }
    var val = document.getElementById(input).value;
    var valid;

    if (val == "" || val == null || val == " ")
    {
        //   $("#paybutton").addClass("disabledbutton");
        valid = false;
    }
    else
    {
        valid = true;
        $("#paybutton").removeClass("disabledbutton");
    }

    return valid;
}


function validateAllPay()
{
    var select = document.getElementById("allpayBankName");

    if (select.selectedIndex)
    {
        $("#paybutton").removeClass("disabledbutton");
    }
    else
    {
        $("#paybutton").addClass("disabledbutton");
    }
}


function error_msg(error, divID)
{
    if (error != "")
    {
        var Div = document.createElement("div")
        Div.className = "help";
        Div.innerHTML = error;
        document.getElementById(divID).appendChild(Div);
        setTimeout(function ()
        {
            document.getElementById(divID).removeChild(Div);
        }, 3000);
    }
}


// start of no cc page
function isValidCardCheck(cardno, divID, triggered)
{
    var ccNum = document.getElementById(cardno).value;
    var length = ccNum.length;
    var error = "";
    var type = "";
    var valid;

    if (ccNum.indexOf("4") === 0)
    {
        if (length != 13 && length != 16 && length != 19)
        {
            error = InvalidVisa;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("VISA") > -1 || availableDebitCardType.indexOf("VISA") > -1)
            {
                type = "VISA";
                valid = true;
            }
            else
            {
                error = VisaNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }

    else if (ccNum.indexOf("60") === 0 || ccNum.indexOf("6521") === 0 || ccNum.indexOf("6522") === 0 || ccNum.indexOf("3536")===0 || ccNum.indexOf("3538")===0 || ccNum.indexOf("5028")===0 || ccNum.indexOf("5085")===0 || ccNum.indexOf("5086")===0 || ccNum.indexOf("5087")===0 || ccNum.indexOf("5088")===0 || ccNum.indexOf("5089")===0 || ccNum.indexOf("6061")===0 || ccNum.indexOf("6062")===0 || ccNum.indexOf("6063")===0 || ccNum.indexOf("6064")===0 || ccNum.indexOf("6065")===0 || ccNum.indexOf("6066")===0 || ccNum.indexOf("6067")===0 || ccNum.indexOf("6068")===0 || ccNum.indexOf("6069")===0 || ccNum.indexOf("6070")===0 || ccNum.indexOf("6071")===0 || ccNum.indexOf("6072")===0 || ccNum.indexOf("6073")===0 || ccNum.indexOf("6074")===0 || ccNum.indexOf("6075")===0 || ccNum.indexOf("6076")===0 || ccNum.indexOf("6077")===0 || ccNum.indexOf("6078")===0 || ccNum.indexOf("6079")===0 || ccNum.indexOf("6080")===0 || ccNum.indexOf("6081")===0 || ccNum.indexOf("6082")===0 || ccNum.indexOf("6083")===0 || ccNum.indexOf("6084")===0 || ccNum.indexOf("6085")===0 || ccNum.indexOf("6086")===0 || ccNum.indexOf("6087")===0 || ccNum.indexOf("6088")===0 || ccNum.indexOf("6089")===0 || ccNum.indexOf("6273")===0 || ccNum.indexOf("6521")===0 || ccNum.indexOf("6522")===0 || ccNum.indexOf("6523")===0 || ccNum.indexOf("6524")===0 || ccNum.indexOf("6525")===0 || ccNum.indexOf("6528")===0 || ccNum.indexOf("6950")===0 || ccNum.indexOf("8172")===0 || ccNum.indexOf("8201")===0 || ccNum.indexOf("8888")===0 || ccNum.indexOf("9999")===0)
    {
        if (ccNum.indexOf("6011") === 0)
        {
            if (length != 16 && length != 19)
            {
                error = InvalidDISC;
                error_msg(error, divID);
                valid = false;
            }
            else
            {
                if (availableCardType.indexOf("DISC") > -1 || availableDebitCardType.indexOf("DISC") > -1)
                {
                    type = "DISC";
                    valid = true;
                }
                else
                {
                    error = DiscNotPermitted;
                    error_msg(error, divID);
                    valid = false;
                }
            }
        }
        else
        {
            if (length != 16)
            {
                error = InvalidRUPAY;
                error_msg(error, divID);
                valid = false;
            }
            else
            {
                if (availableCardType.indexOf("RUPAY") > -1 || availableDebitCardType.indexOf("RUPAY") > -1)
                {
                    type = "RUPAY";
                    valid = true;
                }
                else
                {
                    error = RUPAYNotPermitted;
                    error_msg(error, divID);
                    valid = false;
                }
            }
        }
    }


    else if (ccNum.indexOf("51") === 0 || ccNum.indexOf("52") === 0 || ccNum.indexOf("53") === 0 || ccNum.indexOf("54") === 0 || ccNum.indexOf("55") === 0 || ccNum.indexOf("22") === 0 ||
            ccNum.indexOf("23") === 0 || ccNum.indexOf("24") === 0 || ccNum.indexOf("25") === 0 || ccNum.indexOf("26") === 0 || ccNum.indexOf("27") === 0 ||
            ccNum.indexOf("50") === 0 || ccNum.indexOf("58") === 0 || ccNum.indexOf("57") === 0 || ccNum.indexOf("63") === 0 || ccNum.indexOf("67") === 0 ||
            ccNum.indexOf("56") === 0
    )
    {
        if (ccNum.indexOf("50") === 0 || ccNum.indexOf("58") === 0 || ccNum.indexOf("57") === 0 || ccNum.indexOf("63") === 0 || ccNum.indexOf("67") === 0 ||
                ccNum.indexOf("56") === 0 )
        {
            if (length != 16 && length != 19)
            {
                error = InvalidMAESTRO;
                error_msg(error, divID);
                valid = false;
            }
            if (availableCardType.indexOf("MAESTRO") > -1 || availableDebitCardType.indexOf("MAESTRO") > -1)
            {
                type = "MAESTRO";
                valid = true;
            }
            else if (availableCardType.indexOf("MC") > -1 || availableDebitCardType.indexOf("MC") > -1)
            {
                type = "MC";
                valid = true;
            }
            else
            {
                error = MaestroNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
        else
        {
            if (length != 16 && length != 19)
            {
                error = InvalidMaster;
                error_msg(error, divID);
                valid = false;
            }
            if (availableCardType.indexOf("MC") > -1 || availableDebitCardType.indexOf("MC") > -1)
            {
                type = "MC";
                valid = true;
            }
            else
            {
                error = MasterNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else if (ccNum.indexOf("300") === 0 || ccNum.indexOf("301") === 0 || ccNum.indexOf("302") === 0 || ccNum.indexOf("303") === 0 || ccNum.indexOf("304") === 0
            || ccNum.indexOf("305") === 0 || ccNum.indexOf("36") === 0 || ccNum.indexOf("54") === 0 || ccNum.indexOf("55") === 0)
    {
        if (length != 14 && length != 16)
        {
            error = InvalidDiner;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("DINER") > -1 || availableDebitCardType.indexOf("DINER") > -1)
            {
                type = "DINER";
                valid = true;
            }
            else
            {
                error = DinerNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else if (ccNum.indexOf("34") === 0 || ccNum.indexOf("37") === 0)
    {
        if (length != 15)
        {
            error = InvalidAMEX;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("AMEX") > -1 || availableDebitCardType.indexOf("AMEX") > -1)
            {
                type = "AMEX";
                valid = true;
            }
            else
            {
                error = AmexNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }

    else if (ccNum.indexOf("6011") === 0 || ccNum.indexOf("622") === 0 || ccNum.indexOf("644") === 0 || ccNum.indexOf(" 645") === 0 || ccNum.indexOf("646") === 0 ||
            ccNum.indexOf("647") === 0 || ccNum.indexOf("648") === 0 || ccNum.indexOf("649") === 0 || ccNum.indexOf("65") === 0)
    {
        if (length != 16 && length != 19)
        {
            error = InvalidDISC;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("DISC") > -1 || availableDebitCardType.indexOf("DISC") > -1)
            {
                type = "DISC";
                valid = true;
            }
            else
            {
                error = DiscNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else if (ccNum.indexOf("35") === 0)
    {
        if (length != 16 && length != 19)
        {
            error = InvalidJCB;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("JCB") > -1 || availableDebitCardType.indexOf("JCB") > -1)
            {
                type = "JCB";
                valid = true;
            }
            else
            {
                error = JcbNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else if (ccNum.indexOf("5018") === 0 || ccNum.indexOf("5020") === 0 || ccNum.indexOf("5038") === 0 || ccNum.indexOf("5893") === 0 || ccNum.indexOf("6304") === 0 ||
            ccNum.indexOf("6759") === 0 || ccNum.indexOf("6761") === 0 || ccNum.indexOf("6762") === 0 || ccNum.indexOf("6763") === 0 || ccNum.indexOf("6709") === 0 ||
            ccNum.indexOf("6799") === 0 || ccNum.indexOf("6705") === 0 )
    {
        if (length != 16 && length != 19)
        {
            error = InvalidMAESTRO;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("MAESTRO") > -1 || availableDebitCardType.indexOf("MAESTRO") > -1)
            {
                type = "MAESTRO";
                valid = true;
            }
            else
            {
                error = MaestroNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else if (ccNum.indexOf("637") === 0 || ccNum.indexOf("638") === 0 || ccNum.indexOf("639") === 0)
    {
        if (length != 16)
        {
            error = InvalidINSTAPAYMENT;
            error_msg(error, divID);
            valid = false;
        }
        else
        {
            if (availableCardType.indexOf("INSTAPAYMENT") > -1 || availableDebitCardType.indexOf("INSTAPAYMENT") > -1)
            {
                type = "INSTAPAYMENT";
                valid = true;
            }
            else
            {
                error = InstapaymentNotPermitted;
                error_msg(error, divID);
                valid = false;
            }
        }
    }
    else
    {
        error = InvalidCard;
        error_msg(error, divID);
        valid = false;
    }


    if (valid == true)
    {
        var firstSix = ccNum.substr(0, 6);
        var payMode;

        var addressDisplay;
        if (pay_methods == 'CreditCards')
        {
            payMode = "CC";
            if ($("#CreditCards").find("#" + type)[0])
            {
                var val = $("#CreditCards").find("#" + type)[0].value;
                $("#CreditCards").find("#paymentBrand")[0].value = type;
                //document.getElementById('paymentBrand').value = type;
                //document.getElementById('terminalid').value = val.split(",")[0];
                $("#CreditCards").find("#terminalid")[0].value = val.split(",")[0];
                addressValidation = val.split(",")[1];
                addressDisplayFlag = val.split(",")[2];
                currency = val.split(",")[3];
            }

            if (addressDisplayFlag == "N" && addressValidation == "N")
            {
                //document.getElementById("hideaddressinfo").style.display = "none";
                $("#CreditCards").find('#hideaddressinfo')[0].style.display = "none";
            }
            else
            {
                //document.getElementById("hideaddressinfo").style.display = "inline-block";
                $("#CreditCards").find('#hideaddressinfo')[0].style.display = "inline-block";
            }

            if (document.getElementById("CVV").value)
            {
                showaddress();
            }
        }

        if (pay_methods == 'DebitCards')
        {
            payMode = "DC";
            if ($("#DebitCards").find("#" + type + "_DC")[0])
            {
                var val = $("#DebitCards").find("#" + type + "_DC")[0].value;
                //document.getElementById('paymentBranddc').value = type;
                $("#DebitCards").find("#paymentBranddc")[0].value = type;
                $("#DebitCards").find("#terminaliddc")[0].value = val.split(",")[0];
                //document.getElementById('terminaliddc').value = val.split(",")[0];
                addressValidationDebitCard = val.split(",")[1];
                addressDisplayFlagDebitCard = val.split(",")[2];
                currency = val.split(",")[3];
            }
            if (addressDisplayFlagDebitCard == "N" && addressValidationDebitCard == "N")
            {
                //document.getElementById("hideaddressinfodc").style.display = "none";
                $("#DebitCards").find('#hideaddressinfodc')[0].style.display = "none";
            }
            else
            {
                //document.getElementById("hideaddressinfodc").style.display = "inline-block";
                $("#DebitCards").find('#hideaddressinfodc')[0].style.display = "inline-block";
            }

            if (document.getElementById("CVVdc").value)
            {
                showaddressDebitCard();
            }
        }

        var emiSupportFlag = document.getElementById("emiSupport").value;

        if (emiSupportFlag == "Y")
        {
            if (triggered)
            {
                emiFlag(type, firstSix, payMode, ccNum, currency);
            }
        }
        else
        {
            console.log("in else no emi");
            var emi = pay_methods == "CreditCards" ? document.getElementById("EMIOption") : document.getElementById("EMIOptiondc");
            if (emi)
            {
                emi.style.display = "none";
            }
        }
    }
    else
    {
        console.log("not valid ");
        var emi = pay_methods == "CreditCards" ? document.getElementById("EMIOption") : document.getElementById("EMIOptiondc");
        if (emi)
        {
            emi.style.display = "none";
        }
    }

    if (pay_methods == 'CupUpi')
    {
        document.getElementById("hideaddressinfo_cu").style.display = "inline-block";
        if (document.getElementById("CU_CVV").value)
        {
            showaddressCupUpi();
        }
    }

    error_msg(error, divID);
    return valid;
}

//end of no cc page


// end of validations


function disablePayButton(event)
{
    var val = document.getElementById("consentFlag").value;
    if (val === "Y")
    {
        if (event && event.checked)
        {
            $("#paybutton").removeClass("disabledbutton");
        }
        else
        {
            $("#paybutton").addClass("disabledbutton");
        }
    }
    else
    {
        $("#paybutton").removeClass("disabledbutton");
    }
}


function setCountry(val, countryID)
{
    var country_code = val;
    if (country_code == "AF" || country_code == "AFG")
    {
        document.getElementById(countryID).value = "Afghanistan"
    }
    if (country_code == "AX" || country_code == "ALA")
    {
        document.getElementById(countryID).value = "Åland Islands"
    }
    if (country_code == "AL" || country_code == "ALB")
    {
        document.getElementById(countryID).value = "Albania"
    }
    if (country_code == "DZ" || country_code == "DZA")
    {
        document.getElementById(countryID).value = "Algeria"
    }
    if (country_code == "AS" || country_code == "ASM")
    {
        document.getElementById(countryID).value = "American Samoa"
    }
    if (country_code == "AD" || country_code == "AND")
    {
        document.getElementById(countryID).value = "Andorra"
    }
    if (country_code == "AO" || country_code == "AGO")
    {
        document.getElementById(countryID).value = "Angola"
    }
    if (country_code == "AI" || country_code == "AIA")
    {
        document.getElementById(countryID).value = "Anguilla"
    }
    if (country_code == "AQ" || country_code == "ATA")
    {
        document.getElementById(countryID).value = "Antarctica"
    }
    if (country_code == "AG" || country_code == "ATG")
    {
        document.getElementById(countryID).value = "Antigua and Barbuda"
    }
    if (country_code == "AR" || country_code == "ARG")
    {
        document.getElementById(countryID).value = "Argentina"
    }
    if (country_code == "AM" || country_code == "ARM")
    {
        document.getElementById(countryID).value = "Armenia"
    }
    if (country_code == "AW" || country_code == "ABW")
    {
        document.getElementById(countryID).value = "Aruba"
    }
    if (country_code == "AU" || country_code == "AUS")
    {
        document.getElementById(countryID).value = "Australia"
    }
    if (country_code == "AT" || country_code == "AUT")
    {
        document.getElementById(countryID).value = "Austria"
    }
    if (country_code == "AZ" || country_code == "AZE")
    {
        document.getElementById(countryID).value = "Azerbaijan"
    }
    if (country_code == "BS" || country_code == "BHS")
    {
        document.getElementById(countryID).value = "Bahamas"
    }
    if (country_code == "BH" || country_code == "BHR")
    {
        document.getElementById(countryID).value = "Bahrain"
    }
    if (country_code == "BD" || country_code == "BGD")
    {
        document.getElementById(countryID).value = "Bangladesh"
    }
    if (country_code == "BB" || country_code == "BRB")
    {
        document.getElementById(countryID).value = "Barbados"
    }
    if (country_code == "BY" || country_code == "BLR")
    {
        document.getElementById(countryID).value = "Belarus"
    }
    if (country_code == "BE" || country_code == "BEL")
    {
        document.getElementById(countryID).value = "Belgium"
    }
    if (country_code == "BZ" || country_code == "BLZ")
    {
        document.getElementById(countryID).value = "Belize"
    }
    if (country_code == "BJ" || country_code == "BEN")
    {
        document.getElementById(countryID).value = "Benin"
    }
    if (country_code == "BM" || country_code == "BMU")
    {
        document.getElementById(countryID).value = "Bermuda"
    }
    if (country_code == "BT" || country_code == "BTN")
    {
        document.getElementById(countryID).value = "Bhutan"
    }
    if (country_code == "BO" || country_code == "BOL")
    {
        document.getElementById(countryID).value = "Bolivia"
    }
    if (country_code == "BQ" || country_code == "BES")
    {
        document.getElementById(countryID).value = "Bonaire"
    }
    if (country_code == "BA" || country_code == "BIH")
    {
        document.getElementById(countryID).value = "Bosnia and Herzegovina"
    }
    if (country_code == "BW" || country_code == "BWA")
    {
        document.getElementById(countryID).value = "Botswana"
    }
    if (country_code == "BR" || country_code == "BRA")
    {
        document.getElementById(countryID).value = "Brazil"
    }
    if (country_code == "IO" || country_code == "IOT")
    {
        document.getElementById(countryID).value = "British Indian Ocean Territory"
    }
    if (country_code == "BN" || country_code == "BRN")
    {
        document.getElementById(countryID).value = "Brunei Darussalam"
    }
    if (country_code == "BG" || country_code == "BGR")
    {
        document.getElementById(countryID).value = "Bulgaria"
    }
    if (country_code == "BF" || country_code == "BFA")
    {
        document.getElementById(countryID).value = "Burkina Faso"
    }
    if (country_code == "BI" || country_code == "BDI")
    {
        document.getElementById(countryID).value = "Burundi"
    }
    if (country_code == "KH" || country_code == "KHM")
    {
        document.getElementById(countryID).value = "Cambodia"
    }
    if (country_code == "CM" || country_code == "CMR")
    {
        document.getElementById(countryID).value = "Cameroon"
    }
    if (country_code == "CA" || country_code == "CAN")
    {
        document.getElementById(countryID).value = "Canada"
    }
    if (country_code == "CV" || country_code == "CPV")
    {
        document.getElementById(countryID).value = "Cape Verde"
    }
    if (country_code == "KY" || country_code == "CYM")
    {
        document.getElementById(countryID).value = "Cayman Islands"
    }
    if (country_code == "CF" || country_code == "CAF")
    {
        document.getElementById(countryID).value = "Central African Republic"
    }
    if (country_code == "TD" || country_code == "TCD")
    {
        document.getElementById(countryID).value = "Chad"
    }
    if (country_code == "CL" || country_code == "CHL")
    {
        document.getElementById(countryID).value = "Chile"
    }
    if (country_code == "CN" || country_code == "CHN")
    {
        document.getElementById(countryID).value = "China"
    }
    if (country_code == "CX" || country_code == "CXR")
    {
        document.getElementById(countryID).value = "Christmas Island"
    }
    if (country_code == "CC" || country_code == "CCK")
    {
        document.getElementById(countryID).value = "Cocos (Keeling) Islands"
    }
    if (country_code == "CO" || country_code == "COL")
    {
        document.getElementById(countryID).value = "Colombia"
    }
    if (country_code == "KM" || country_code == "COM")
    {
        document.getElementById(countryID).value = "Comoros"
    }
    if (country_code == "CG" || country_code == "COD")
    {
        document.getElementById(countryID).value = "Congo"
    }
    if (country_code == "CD" || country_code == "COD")
    {
        document.getElementById(countryID).value = "Congo"
    }
    if (country_code == "CK" || country_code == "COK")
    {
        document.getElementById(countryID).value = "Cook Islands"
    }
    if (country_code == "CR" || country_code == "CRI")
    {
        document.getElementById(countryID).value = "Costa Rica"
    }
    if (country_code == "CI" || country_code == "CIV")
    {
        document.getElementById(countryID).value = "Côte d'Ivoire"
    }
    if (country_code == "HR" || country_code == "HRV")
    {
        document.getElementById(countryID).value = "Croatia"
    }
    if (country_code == "CU" || country_code == "CUB")
    {
        document.getElementById(countryID).value = "Cuba"
    }
    if (country_code == "CW" || country_code == "CUW")
    {
        document.getElementById(countryID).value = "Curaçao"
    }
    if (country_code == "CY" || country_code == "CYP")
    {
        document.getElementById(countryID).value = "Cyprus"
    }
    if (country_code == "CZ" || country_code == "CZE")
    {
        document.getElementById(countryID).value = "Czech Republic"
    }
    if (country_code == "DK" || country_code == "DNK")
    {
        document.getElementById(countryID).value = "Denmark"
    }
    if (country_code == "DJ" || country_code == "DJI")
    {
        document.getElementById(countryID).value = "Djibouti"
    }
    if (country_code == "DM" || country_code == "DMA")
    {
        document.getElementById(countryID).value = "Dominica"
    }
    if (country_code == "DO" || country_code == "DOM")
    {
        document.getElementById(countryID).value = "Dominican Republic"
    }
    if (country_code == "EC" || country_code == "ECU")
    {
        document.getElementById(countryID).value = "Ecuador"
    }
    if (country_code == "EG" || country_code == "EGY")
    {
        document.getElementById(countryID).value = "Egypt"
    }
    if (country_code == "SV" || country_code == "SLV")
    {
        document.getElementById(countryID).value = "El Salvador"
    }
    if (country_code == "GQ" || country_code == "GNQ")
    {
        document.getElementById(countryID).value = "Equatorial Guinea"
    }
    if (country_code == "ER" || country_code == "ERI")
    {
        document.getElementById(countryID).value = "Eritrea"
    }
    if (country_code == "EE" || country_code == "EST")
    {
        document.getElementById(countryID).value = "Estonia"
    }
    if (country_code == "ET" || country_code == "ETH")
    {
        document.getElementById(countryID).value = "Ethiopia"
    }
    if (country_code == "FK" || country_code == "FLK")
    {
        document.getElementById(countryID).value = "Falkland Islands"
    }
    if (country_code == "FO" || country_code == "FRO")
    {
        document.getElementById(countryID).value = "Faroe Islands"
    }
    if (country_code == "FJ" || country_code == "FJI")
    {
        document.getElementById(countryID).value = "Fiji"
    }
    if (country_code == "FI" || country_code == "FIN")
    {
        document.getElementById(countryID).value = "Finland"
    }
    if (country_code == "FR" || country_code == "FRA")
    {
        document.getElementById(countryID).value = "France"
    }
    if (country_code == "GF" || country_code == "GUF")
    {
        document.getElementById(countryID).value = "French Guiana"
    }
    if (country_code == "PF" || country_code == "PYF")
    {
        document.getElementById(countryID).value = "French Polynesia"
    }
    if (country_code == "TF" || country_code == "ATF")
    {
        document.getElementById(countryID).value = "French Southern Territories"
    }
    if (country_code == "GA" || country_code == "GAB")
    {
        document.getElementById(countryID).value = "Gabon"
    }
    if (country_code == "GM" || country_code == "GMB")
    {
        document.getElementById(countryID).value = "Gambia"
    }
    if (country_code == "GE" || country_code == "GEO")
    {
        document.getElementById(countryID).value = "Georgia"
    }
    if (country_code == "DE" || country_code == "DEU")
    {
        document.getElementById(countryID).value = "Germany"
    }
    if (country_code == "GH" || country_code == "GHA")
    {
        document.getElementById(countryID).value = "Ghana"
    }
    if (country_code == "GI" || country_code == "GIB")
    {
        document.getElementById(countryID).value = "Gibraltar"
    }
    if (country_code == "GR" || country_code == "GRC")
    {
        document.getElementById(countryID).value = "Greece"
    }
    if (country_code == "GL" || country_code == "GRL")
    {
        document.getElementById(countryID).value = "Greenland"
    }
    if (country_code == "GD" || country_code == "GRD")
    {
        document.getElementById(countryID).value = "Grenada"
    }
    if (country_code == "GP" || country_code == "GLP")
    {
        document.getElementById(countryID).value = "Guadeloupe"
    }
    if (country_code == "GU" || country_code == "GUM")
    {
        document.getElementById(countryID).value = "Guam"
    }
    if (country_code == "GT" || country_code == "GTM")
    {
        document.getElementById(countryID).value = "Guatemala"
    }
    if (country_code == "GG" || country_code == "GGY")
    {
        document.getElementById(countryID).value = "Guernsey"
    }
    if (country_code == "GN" || country_code == "GIN")
    {
        document.getElementById(countryID).value = "Guinea"
    }
    if (country_code == "GW" || country_code == "GNB")
    {
        document.getElementById(countryID).value = "Guinea-Bissau"
    }
    if (country_code == "GY" || country_code == "GUY")
    {
        document.getElementById(countryID).value = "Guyana"
    }
    if (country_code == "HT" || country_code == "HTI")
    {
        document.getElementById(countryID).value = "Haiti"
    }
    if (country_code == "HM" || country_code == "HMD")
    {
        document.getElementById(countryID).value = "Heard Island & McDonald Islands"
    }
    if (country_code == "VA" || country_code == "VAT")
    {
        document.getElementById(countryID).value = "Vatican City"
    }
    if (country_code == "HN" || country_code == "HND")
    {
        document.getElementById(countryID).value = "Honduras"
    }
    if (country_code == "HK" || country_code == "HKG")
    {
        document.getElementById(countryID).value = "Hong Kong"
    }
    if (country_code == "HU" || country_code == "HUN")
    {
        document.getElementById(countryID).value = "Hungary"
    }
    if (country_code == "IS" || country_code == "ISL")
    {
        document.getElementById(countryID).value = "Iceland"
    }
    if (country_code == "IN" || country_code == "IND")
    {
        document.getElementById(countryID).value = "India"
    }
    if (country_code == "ID" || country_code == "IDN")
    {
        document.getElementById(countryID).value = "Indonesia"
    }
    if (country_code == "XZ")
    {
        document.getElementById(countryID).value = "Installations in International Waters"
    }
    if (country_code == "IR" || country_code == "IRN")
    {
        document.getElementById(countryID).value = "Iran"
    }
    if (country_code == "IQ" || country_code == "IRQ")
    {
        document.getElementById(countryID).value = "Iraq"
    }
    if (country_code == "IE" || country_code == "IRL")
    {
        document.getElementById(countryID).value = "Ireland"
    }
    if (country_code == "IM" || country_code == "IMN")
    {
        document.getElementById(countryID).value = "Isle of Man"
    }
    if (country_code == "IL" || country_code == "ISR")
    {
        document.getElementById(countryID).value = "Israel"
    }
    if (country_code == "IT" || country_code == "ITA")
    {
        document.getElementById(countryID).value = "Italy"
    }
    if (country_code == "JM" || country_code == "JAM")
    {
        document.getElementById(countryID).value = "Jamaica"
    }
    if (country_code == "JP" || country_code == "JPN")
    {
        document.getElementById(countryID).value = "Japan"
    }
    if (country_code == "JE" || country_code == "JEY")
    {
        document.getElementById(countryID).value = "Jersey"
    }
    if (country_code == "JO" || country_code == "JOR")
    {
        document.getElementById(countryID).value = "Jordan"
    }
    if (country_code == "KZ" || country_code == "KAZ")
    {
        document.getElementById(countryID).value = "Kazakhstan"
    }
    if (country_code == "KE" || country_code == "KEN")
    {
        document.getElementById(countryID).value = "Kenya"
    }
    if (country_code == "KI" || country_code == "KIR")
    {
        document.getElementById(countryID).value = "Kiribati"
    }
    if (country_code == "KP" || country_code == "PRK")
    {
        document.getElementById(countryID).value = "Korea"
    }
    if (country_code == "KR" || country_code == "KOR")
    {
        document.getElementById(countryID).value = "Korea"
    }
    if (country_code == "KW" || country_code == "KWT")
    {
        document.getElementById(countryID).value = "Kuwait"
    }
    if (country_code == "KG" || country_code == "KGZ")
    {
        document.getElementById(countryID).value = "Kyrgyzstan"
    }
    if (country_code == "LA" || country_code == "LAO")
    {
        document.getElementById(countryID).value = "Lao People's Democratic Republic"
    }
    if (country_code == "LV" || country_code == "LVA")
    {
        document.getElementById(countryID).value = "Latvia"
    }
    if (country_code == "LB" || country_code == "LBN")
    {
        document.getElementById(countryID).value = "Lebanon"
    }
    if (country_code == "LS" || country_code == "LSO")
    {
        document.getElementById(countryID).value = "Lesotho"
    }
    if (country_code == "LR" || country_code == "LBR")
    {
        document.getElementById(countryID).value = "Liberia"
    }
    if (country_code == "LY" || country_code == "LBY")
    {
        document.getElementById(countryID).value = "Libya"
    }
    if (country_code == "LI" || country_code == "LIE")
    {
        document.getElementById(countryID).value = "Liechtenstein"
    }
    if (country_code == "LT" || country_code == "LTU")
    {
        document.getElementById(countryID).value = "Lithuania"
    }
    if (country_code == "LU" || country_code == "LUX")
    {
        document.getElementById(countryID).value = "Luxembourg"
    }
    if (country_code == "MO" || country_code == "MAC")
    {
        document.getElementById(countryID).value = "Macao"
    }
    if (country_code == "MK" || country_code == "MKD")
    {
        document.getElementById(countryID).value = "Macedonia"
    }
    if (country_code == "MG" || country_code == "MDG")
    {
        document.getElementById(countryID).value = "Madagascar"
    }
    if (country_code == "MW" || country_code == "MWI")
    {
        document.getElementById(countryID).value = "Malawi"
    }
    if (country_code == "MY" || country_code == "MYS")
    {
        document.getElementById(countryID).value = "Malaysia"
    }
    if (country_code == "MV" || country_code == "MDV")
    {
        document.getElementById(countryID).value = "Maldives"
    }
    if (country_code == "ML" || country_code == "MLI")
    {
        document.getElementById(countryID).value = "Mali"
    }
    if (country_code == "MT" || country_code == "MLT")
    {
        document.getElementById(countryID).value = "Malta"
    }
    if (country_code == "MH" || country_code == "MHL")
    {
        document.getElementById(countryID).value = "Marshall Islands"
    }
    if (country_code == "MQ" || country_code == "MTQ")
    {
        document.getElementById(countryID).value = "Martinique"
    }
    if (country_code == "MR" || country_code == "MRT")
    {
        document.getElementById(countryID).value = "Mauritania"
    }
    if (country_code == "MU" || country_code == "MUS")
    {
        document.getElementById(countryID).value = "Mauritius"
    }
    if (country_code == "YT" || country_code == "MYT")
    {
        document.getElementById(countryID).value = "Mayotte"
    }
    if (country_code == "MX" || country_code == "MEX")
    {
        document.getElementById(countryID).value = "Mexico"
    }
    if (country_code == "FM" || country_code == "FSM")
    {
        document.getElementById(countryID).value = "Micronesia"
    }
    if (country_code == "MD" || country_code == "MDA")
    {
        document.getElementById(countryID).value = "Moldova"
    }
    if (country_code == "MC" || country_code == "MCO")
    {
        document.getElementById(countryID).value = "Monaco"
    }
    if (country_code == "MN" || country_code == "MNG")
    {
        document.getElementById(countryID).value = "Mongolia"
    }
    if (country_code == "ME" || country_code == "MNE")
    {
        document.getElementById(countryID).value = "Montenegro"
    }
    if (country_code == "MS" || country_code == "MSR")
    {
        document.getElementById(countryID).value = "Montserrat"
    }
    if (country_code == "MA" || country_code == "MAR")
    {
        document.getElementById(countryID).value = "Morocco"
    }
    if (country_code == "MZ" || country_code == "MOZ")
    {
        document.getElementById(countryID).value = "Mozambique"
    }
    if (country_code == "MM" || country_code == "MMR")
    {
        document.getElementById(countryID).value = "Myanmar"
    }
    if (country_code == "NA" || country_code == "NAM")
    {
        document.getElementById(countryID).value = "Namibia"
    }
    if (country_code == "NR" || country_code == "NRU")
    {
        document.getElementById(countryID).value = "Nauru"
    }
    if (country_code == "NP" || country_code == "NPL")
    {
        document.getElementById(countryID).value = "Nepal"
    }
    if (country_code == "NL" || country_code == "NLD")
    {
        document.getElementById(countryID).value = "Netherlands"
    }
    if (country_code == "NC" || country_code == "NCL")
    {
        document.getElementById(countryID).value = "New Caledonia"
    }
    if (country_code == "NZ" || country_code == "NZL")
    {
        document.getElementById(countryID).value = "New Zealand"
    }
    if (country_code == "NI" || country_code == "NIC")
    {
        document.getElementById(countryID).value = "Nicaragua"
    }
    if (country_code == "NE" || country_code == "NER")
    {
        document.getElementById(countryID).value = "Niger"
    }
    if (country_code == "NG" || country_code == "NGA")
    {
        document.getElementById(countryID).value = "Nigeria"
    }
    if (country_code == "NU" || country_code == "NIU")
    {
        document.getElementById(countryID).value = "Niue"
    }
    if (country_code == "NF" || country_code == "NFK")
    {
        document.getElementById(countryID).value = "Norfolk Island"
    }
    if (country_code == "MP" || country_code == "MNP")
    {
        document.getElementById(countryID).value = "Northern Mariana Islands"
    }
    if (country_code == "NO" || country_code == "NOR")
    {
        document.getElementById(countryID).value = "Norway"
    }
    if (country_code == "OM" || country_code == "OMN")
    {
        document.getElementById(countryID).value = "Oman"
    }
    if (country_code == "PK" || country_code == "PAK")
    {
        document.getElementById(countryID).value = "Pakistan"
    }
    if (country_code == "PW" || country_code == "PLW")
    {
        document.getElementById(countryID).value = "Palau"
    }
    if (country_code == "PS" || country_code == "PSE")
    {
        document.getElementById(countryID).value = "Palestine"
    }
    if (country_code == "PA" || country_code == "PAN")
    {
        document.getElementById(countryID).value = "Panama"
    }
    if (country_code == "PG" || country_code == "PNG")
    {
        document.getElementById(countryID).value = "Papua New Guinea"
    }
    if (country_code == "PY" || country_code == "PRY")
    {
        document.getElementById(countryID).value = "Paraguay"
    }
    if (country_code == "PE" || country_code == "PER")
    {
        document.getElementById(countryID).value = "Peru"
    }
    if (country_code == "PH" || country_code == "PHL")
    {
        document.getElementById(countryID).value = "Philippines"
    }
    if (country_code == "PN" || country_code == "PCN")
    {
        document.getElementById(countryID).value = "Pitcairn"
    }
    if (country_code == "PL" || country_code == "POL")
    {
        document.getElementById(countryID).value = "Poland"
    }
    if (country_code == "PT" || country_code == "PRT")
    {
        document.getElementById(countryID).value = "Portugal"
    }
    if (country_code == "PR" || country_code == "PRI")
    {
        document.getElementById(countryID).value = "Puerto Rico"
    }
    if (country_code == "QA" || country_code == "QAT")
    {
        document.getElementById(countryID).value = "Qatar"
    }
    if (country_code == "RE" || country_code == "REU")
    {
        document.getElementById(countryID).value = "Reunion"
    }
    if (country_code == "RO" || country_code == "ROU")
    {
        document.getElementById(countryID).value = "Romania"
    }
    if (country_code == "RU" || country_code == "RUS")
    {
        document.getElementById(countryID).value = "Russia"
    }
    if (country_code == "RW" || country_code == "RWA")
    {
        document.getElementById(countryID).value = "Rwanda"
    }
    if (country_code == "BL" || country_code == "BLM")
    {
        document.getElementById(countryID).value = "Saint Barthélemy"
    }
    if (country_code == "SH" || country_code == "SHN")
    {
        document.getElementById(countryID).value = "Saint Helena"
    }
    if (country_code == "KN" || country_code == "KNA")
    {
        document.getElementById(countryID).value = "Saint Kitts and Nevis"
    }
    if (country_code == "LC" || country_code == "LCA")
    {
        document.getElementById(countryID).value = "Saint Lucia"
    }
    if (country_code == "MF" || country_code == "MAF")
    {
        document.getElementById(countryID).value = "Saint Martin"
    }
    if (country_code == "PM" || country_code == "SPM")
    {
        document.getElementById(countryID).value = "Saint Pierre and Miquelon"
    }
    if (country_code == "VC" || country_code == "VCT")
    {
        document.getElementById(countryID).value = "Saint Vincent and the Grenadines"
    }
    if (country_code == "WS" || country_code == "WSM")
    {
        document.getElementById(countryID).value = "Samoa"
    }
    if (country_code == "SM" || country_code == "SMR")
    {
        document.getElementById(countryID).value = "San Marino"
    }
    if (country_code == "ST" || country_code == "STP")
    {
        document.getElementById(countryID).value = "Sao Tome and Principe"
    }
    if (country_code == "SA" || country_code == "SAU")
    {
        document.getElementById(countryID).value = "Saudi Arabia"
    }
    if (country_code == "SN" || country_code == "SEN")
    {
        document.getElementById(countryID).value = "Senegal"
    }
    if (country_code == "RS" || country_code == "SRB")
    {
        document.getElementById(countryID).value = "Serbia"
    }
    if (country_code == "SC" || country_code == "SYC")
    {
        document.getElementById(countryID).value = "Seychelles"
    }
    if (country_code == "SL" || country_code == "SLE")
    {
        document.getElementById(countryID).value = "Sierra Leone"
    }
    if (country_code == "SG" || country_code == "SGP")
    {
        document.getElementById(countryID).value = "Singapore"
    }
    if (country_code == "SX" || country_code == "SXM")
    {
        document.getElementById(countryID).value = "Sint Maarten (Dutch Part)"
    }
    if (country_code == "SK" || country_code == "SVK")
    {
        document.getElementById(countryID).value = "Slovakia"
    }
    if (country_code == "SI" || country_code == "SVN")
    {
        document.getElementById(countryID).value = "Slovenia"
    }
    if (country_code == "SB" || country_code == "SLB")
    {
        document.getElementById(countryID).value = "Solomon Islands"
    }
    if (country_code == "SO" || country_code == "SOM")
    {
        document.getElementById(countryID).value = "Somalia"
    }
    if (country_code == "ZA" || country_code == "ZAF")
    {
        document.getElementById(countryID).value = "South Africa"
    }
    if (country_code == "GS" || country_code == "SGS")
    {
        document.getElementById(countryID).value = "South Georgia & South Sandwich Islands"
    }
    if (country_code == "SS" || country_code == "SSD")
    {
        document.getElementById(countryID).value = "South Sudan"
    }
    if (country_code == "ES" || country_code == "ESP")
    {
        document.getElementById(countryID).value = "Spain"
    }
    if (country_code == "LK" || country_code == "LKA")
    {
        document.getElementById(countryID).value = "Sri Lanka"
    }
    if (country_code == "SD" || country_code == "SDN")
    {
        document.getElementById(countryID).value = "Sudan"
    }
    if (country_code == "SR" || country_code == "SUR")
    {
        document.getElementById(countryID).value = "Suriname"
    }
    if (country_code == "SJ" || country_code == "SJM")
    {
        document.getElementById(countryID).value = "Svalbard and Jan Mayen"
    }
    if (country_code == "SZ" || country_code == "SWZ")
    {
        document.getElementById(countryID).value = "Swaziland"
    }
    if (country_code == "SE" || country_code == "SWE")
    {
        document.getElementById(countryID).value = "Sweden"
    }
    if (country_code == "CH" || country_code == "CHE")
    {
        document.getElementById(countryID).value = "Switzerland"
    }
    if (country_code == "SY" || country_code == "SYR")
    {
        document.getElementById(countryID).value = "Syria"
    }
    if (country_code == "TW" || country_code == "TWN")
    {
        document.getElementById(countryID).value = "Taiwan"
    }
    if (country_code == "TJ" || country_code == "TJK")
    {
        document.getElementById(countryID).value = "Tajikistan"
    }
    if (country_code == "TZ" || country_code == "TZA")
    {
        document.getElementById(countryID).value = "Tanzania"
    }
    if (country_code == "TH" || country_code == "THA")
    {
        document.getElementById(countryID).value = "Thailand"
    }
    if (country_code == "TL" || country_code == "TLS")
    {
        document.getElementById(countryID).value = "Timor-Leste"
    }
    if (country_code == "TG" || country_code == "TGO")
    {
        document.getElementById(countryID).value = "Togo"
    }
    if (country_code == "TK" || country_code == "TKL")
    {
        document.getElementById(countryID).value = "Tokelau"
    }
    if (country_code == "TO" || country_code == "TON")
    {
        document.getElementById(countryID).value = "Tonga"
    }
    if (country_code == "TT" || country_code == "TTO")
    {
        document.getElementById(countryID).value = "Trinidad and Tobago"
    }
    if (country_code == "TN" || country_code == "TUN")
    {
        document.getElementById(countryID).value = "Tunisia"
    }
    if (country_code == "TR" || country_code == "TUR")
    {
        document.getElementById(countryID).value = "Turkey"
    }
    if (country_code == "TM" || country_code == "TKM")
    {
        document.getElementById(countryID).value = "Turkmenistan"
    }
    if (country_code == "TC" || country_code == "TCA")
    {
        document.getElementById(countryID).value = "Turks and Caicos Islands"
    }
    if (country_code == "TV" || country_code == "TUV")
    {
        document.getElementById(countryID).value = "Tuvalu"
    }
    if (country_code == "UG" || country_code == "UGA")
    {
        document.getElementById(countryID).value = "Uganda"
    }
    if (country_code == "UA" || country_code == "UKR")
    {
        document.getElementById(countryID).value = "Ukraine"
    }
    if (country_code == "AE" || country_code == "ARE")
    {
        document.getElementById(countryID).value = "United Arab Emirates"
    }
    if (country_code == "GB" || country_code == "GBR")
    {
        document.getElementById(countryID).value = "United Kingdom"
    }
    if (country_code == "US" || country_code == "USA")
    {
        document.getElementById(countryID).value = "United States"
    }
    if (country_code == "UM" || country_code == "UMI")
    {
        document.getElementById(countryID).value = "United States Minor Outlying Islands"
    }
    if (country_code == "UY" || country_code == "URY")
    {
        document.getElementById(countryID).value = "Uruguay"
    }
    if (country_code == "UZ" || country_code == "UZB")
    {
        document.getElementById(countryID).value = "Uzbekistan"
    }
    if (country_code == "VU" || country_code == "VUT")
    {
        document.getElementById(countryID).value = "Vanuatu"
    }
    if (country_code == "VE" || country_code == "VEN")
    {
        document.getElementById(countryID).value = "Venezuela"
    }
    if (country_code == "VN" || country_code == "VNM")
    {
        document.getElementById(countryID).value = "Viet Nam"
    }
    if (country_code == "VG" || country_code == "VGB")
    {
        document.getElementById(countryID).value = "British Virgin Islands"
    }
    if (country_code == "VI" || country_code == "VIR")
    {
        document.getElementById(countryID).value = "United States Virgin Islands"
    }
    if (country_code == "WF" || country_code == "WLF")
    {
        document.getElementById(countryID).value = "Wallis and Futuna"
    }
    if (country_code == "EH" || country_code == "ESH")
    {
        document.getElementById(countryID).value = "Western Sahara"
    }
    if (country_code == "YE" || country_code == "YEM")
    {
        document.getElementById(countryID).value = "Yemen"
    }
    if (country_code == "ZM" || country_code == "ZMB")
    {
        document.getElementById(countryID).value = "Zambia"
    }
    if (country_code == "ZW" || country_code == "ZWE")
    {
        document.getElementById(countryID).value = "Zimbabwe"
    }

}



function setPhoneCC(phoneCCid, hiddenid)
{
    if (document.getElementById(phoneCCid).value)
    {
        document.getElementById(hiddenid).value = document.getElementById(phoneCCid).value;
    }
    else
    {
        document.getElementById(hiddenid).value = "";
    }
}

function optionalCountry(optionalId, countryId, hiddenId, StateId, labels)
{
    if (document.getElementById(optionalId).value)
    {
        document.getElementById(countryId).value = document.getElementById(optionalId).value;
        StateLabel(countryId, hiddenId, StateId);
        if(labels)
        {
            if (labels.city) {
                cityLabel(hiddenId, labels.city);
            }
            if (labels.zip) {
                zipCodeLabel(hiddenId, labels.zip);
            }
        }
    }
    else
    {
        document.getElementById(countryId).value = "";
    }
}

// country Start
function pincodecc(countryvalue, hiddencountryvalue, pincode, hiddenpincode, countryId, hiddenId, StateId, labels)
{
    console.log("Args = ", arguments);
    var country_val = document.getElementById([countryvalue]).value;
    if (countryId, hiddenId, StateId)
    {
        optionalCountry(countryvalue, countryId, hiddenId, StateId, labels);
    }

    if (country_val == "")
    {
        document.getElementById([hiddencountryvalue]).value = '';
        if (pincode)
        {
            document.getElementById([pincode]).value = '';
            document.getElementById([hiddenpincode]).value = '';
        }
    }
    else if (country_val == "Afghanistan")
    {
        document.getElementById([hiddencountryvalue]).value = 'AF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '093';
            document.getElementById([hiddenpincode]).value = '093';
        }
    }
    else if (country_val == "Aland Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'AX';
        if (pincode)
        {
            document.getElementById([pincode]).value = '358';
            document.getElementById([hiddenpincode]).value = '358';
        }
    }
    else if (country_val == "Albania")
    {
        document.getElementById([hiddencountryvalue]).value = 'AL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '355';
            document.getElementById([hiddenpincode]).value = '355';
        }
    }
    else if (country_val == "Algeria")
    {
        document.getElementById([hiddencountryvalue]).value = 'DZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '231';
            document.getElementById([hiddenpincode]).value = '231';
        }
    }
    else if (country_val == "American Samoa")
    {
        document.getElementById([hiddencountryvalue]).value = 'AS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '684';
            document.getElementById([hiddenpincode]).value = '684';
        }
    }
    else if (country_val == "Andorra")
    {
        document.getElementById([hiddencountryvalue]).value = 'AD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '376';
            document.getElementById([hiddenpincode]).value = '376';
        }
    }
    else if (country_val == "Angola")
    {
        document.getElementById([hiddencountryvalue]).value = 'AO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '244';
            document.getElementById([hiddenpincode]).value = '244';
        }
    }
    else if (country_val == "Anguilla")
    {
        document.getElementById([hiddencountryvalue]).value = 'AI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Antarctica")
    {
        document.getElementById([hiddencountryvalue]).value = 'AQ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '000';
            document.getElementById([hiddenpincode]).value = '000';
        }
    }
    else if (country_val == "Antigua and Barbuda")
    {
        document.getElementById([hiddencountryvalue]).value = 'AG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Argentina")
    {
        document.getElementById([hiddencountryvalue]).value = 'AR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '054';
            document.getElementById([hiddenpincode]).value = '054';
        }
    }
    else if (country_val == "Armenia")
    {
        document.getElementById([hiddencountryvalue]).value = 'AM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '374';
            document.getElementById([hiddenpincode]).value = '374';
        }
    }
    else if (country_val == "Aruba")
    {
        document.getElementById([hiddencountryvalue]).value = 'AW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '297';
            document.getElementById([hiddenpincode]).value = '297';
        }
    }
    else if (country_val == "Australia")
    {
        document.getElementById([hiddencountryvalue]).value = 'AU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '061';
            document.getElementById([hiddenpincode]).value = '061';
        }
    }
    else if (country_val == "Austria")
    {
        document.getElementById([hiddencountryvalue]).value = 'AT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '043';
            document.getElementById([hiddenpincode]).value = '043';
        }
    }
    else if (country_val == "Azerbaijan")
    {
        document.getElementById([hiddencountryvalue]).value = 'AZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '994';
            document.getElementById([hiddenpincode]).value = '994';
        }
    }
    else if (country_val == "Bahamas")
    {
        document.getElementById([hiddencountryvalue]).value = 'BS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Bahrain")
    {
        document.getElementById([hiddencountryvalue]).value = 'BH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '973';
            document.getElementById([hiddenpincode]).value = '973';
        }
    }
    else if (country_val == "Bangladesh")
    {
        document.getElementById([hiddencountryvalue]).value = 'BD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '880';
            document.getElementById([hiddenpincode]).value = '880';
        }
    }
    else if (country_val == "Barbados")
    {
        document.getElementById([hiddencountryvalue]).value = 'BB';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Belarus")
    {
        document.getElementById([hiddencountryvalue]).value = 'BY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '375';
            document.getElementById([hiddenpincode]).value = '375';
        }
    }
    else if (country_val == "Belgium")
    {
        document.getElementById([hiddencountryvalue]).value = 'BE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '032';
            document.getElementById([hiddenpincode]).value = '032';
        }
    }
    else if (country_val == "Belize")
    {
        document.getElementById([hiddencountryvalue]).value = 'BZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '501';
            document.getElementById([hiddenpincode]).value = '501';
        }
    }
    else if (country_val == "Benin")
    {
        document.getElementById([hiddencountryvalue]).value = 'BJ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '229';
            document.getElementById([hiddenpincode]).value = '229';
        }
    }
    else if (country_val == "Bermuda")
    {
        document.getElementById([hiddencountryvalue]).value = 'BM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Bhutan")
    {
        document.getElementById([hiddencountryvalue]).value = 'BT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '975';
            document.getElementById([hiddenpincode]).value = '975';
        }
    }
    else if (country_val == "Bolivia")
    {
        document.getElementById([hiddencountryvalue]).value = 'BO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '591';
            document.getElementById([hiddenpincode]).value = '591';
        }
    }
    else if (country_val == "Bosnia and Herzegovina")
    {
        document.getElementById([hiddencountryvalue]).value = 'BA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '387';
            document.getElementById([hiddenpincode]).value = '387';
        }
    }
    else if (country_val == "Botswana")
    {
        document.getElementById([hiddencountryvalue]).value = 'BW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '267';
            document.getElementById([hiddenpincode]).value = '267';
        }
    }
    else if (country_val == "Bouvet Island")
    {
        document.getElementById([hiddencountryvalue]).value = 'BV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '000';
            document.getElementById([hiddenpincode]).value = '000';
        }
    }
    else if (country_val == "Brazil")
    {
        document.getElementById([hiddencountryvalue]).value = 'BR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '055';
            document.getElementById([hiddenpincode]).value = '055';
        }
    }
    else if (country_val == "British Indian Ocean Territory")
    {
        document.getElementById([hiddencountryvalue]).value = 'IO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '246';
            document.getElementById([hiddenpincode]).value = '246';
        }
    }
    else if (country_val == "British Virgin Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'VG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Brunei")
    {
        document.getElementById([hiddencountryvalue]).value = 'BN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '673';
            document.getElementById([hiddenpincode]).value = '673';
        }
    }
    else if (country_val == "Bulgaria")
    {
        document.getElementById([hiddencountryvalue]).value = 'BG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '359';
            document.getElementById([hiddenpincode]).value = '359';
        }
    }
    else if (country_val == "Burkina Faso")
    {
        document.getElementById([hiddencountryvalue]).value = 'BF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '226';
            document.getElementById([hiddenpincode]).value = '226';
        }
    }
    else if (country_val == "Burundi")
    {
        document.getElementById([hiddencountryvalue]).value = 'BI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '257';
            document.getElementById([hiddenpincode]).value = '257';
        }
    }
    else if (country_val == "Cambodia")
    {
        document.getElementById([hiddencountryvalue]).value = 'KH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '855';
            document.getElementById([hiddenpincode]).value = '855';
        }
    }
    else if (country_val == "Cameroon")
    {
        document.getElementById([hiddencountryvalue]).value = 'CM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '237';
            document.getElementById([hiddenpincode]).value = '237';
        }
    }
    else if (country_val == "Canada")
    {
        document.getElementById([hiddencountryvalue]).value = 'CA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Cape Verde")
    {
        document.getElementById([hiddencountryvalue]).value = 'CV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '238';
            document.getElementById([hiddenpincode]).value = '238';
        }
    }
    else if (country_val == "Cayman Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'KY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Central African Republic")
    {
        document.getElementById([hiddencountryvalue]).value = 'CF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '236';
            document.getElementById([hiddenpincode]).value = '236';
        }
    }
    else if (country_val == "Chad")
    {
        document.getElementById([hiddencountryvalue]).value = 'TD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '235';
            document.getElementById([hiddenpincode]).value = '235';
        }
    }
    else if (country_val == "Chile")
    {
        document.getElementById([hiddencountryvalue]).value = 'CL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '056';
            document.getElementById([hiddenpincode]).value = '056';
        }
    }
    else if (country_val == "China")
    {
        document.getElementById([hiddencountryvalue]).value = 'CN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '086';
            document.getElementById([hiddenpincode]).value = '086';
        }
    }
    else if (country_val == "Christmas Island")
    {
        document.getElementById([hiddencountryvalue]).value = 'CX';
        if (pincode)
        {
            document.getElementById([pincode]).value = '061';
            document.getElementById([hiddenpincode]).value = '061';
        }
    }
    else if (country_val == "Cocos (Keeling Islands)")
    {
        document.getElementById([hiddencountryvalue]).value = 'CC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '061';
            document.getElementById([hiddenpincode]).value = '061';
        }
    }
    else if (country_val == "Colombia")
    {
        document.getElementById([hiddencountryvalue]).value = 'CO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '057';
            document.getElementById([hiddenpincode]).value = '057';
        }
    }
    else if (country_val == "Comoros")
    {
        document.getElementById([hiddencountryvalue]).value = 'KM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '269';
            document.getElementById([hiddenpincode]).value = '269';
        }
    }
    else if (country_val == "Cook Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'CK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '682';
            document.getElementById([hiddenpincode]).value = '682';
        }
    }
    else if (country_val == "Costa Rica")
    {
        document.getElementById([hiddencountryvalue]).value = 'CR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '506';
            document.getElementById([hiddenpincode]).value = '506';
        }
    }
    else if (country_val == "Cote d` Ivoire")
    {
        document.getElementById([hiddencountryvalue]).value = 'CI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '225';
            document.getElementById([hiddenpincode]).value = '225';
        }
    }
    else if (country_val == "Croatia")
    {
        document.getElementById([hiddencountryvalue]).value = 'HR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '385';
            document.getElementById([hiddenpincode]).value = '385';
        }
    }
    else if (country_val == "Cuba")
    {
        document.getElementById([hiddencountryvalue]).value = 'CU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '053';
            document.getElementById([hiddenpincode]).value = '053';
        }
    }
    else if (country_val == "Cyprus")
    {
        document.getElementById([hiddencountryvalue]).value = 'CY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '357';
            document.getElementById([hiddenpincode]).value = '357';
        }
    }
    else if (country_val == "Czech Republic")
    {
        document.getElementById([hiddencountryvalue]).value = 'CZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '420';
            document.getElementById([hiddenpincode]).value = '420';
        }
    }
    else if (country_val == "Democratic Republic of the Congo")
    {
        document.getElementById([hiddencountryvalue]).value = 'CD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '243';
            document.getElementById([hiddenpincode]).value = '243';
        }
    }
    else if (country_val == "Denmark")
    {
        document.getElementById([hiddencountryvalue]).value = 'DK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '045';
            document.getElementById([hiddenpincode]).value = '045';
        }
    }
    else if (country_val == "Djibouti")
    {
        document.getElementById([hiddencountryvalue]).value = 'DJ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '253';
            document.getElementById([hiddenpincode]).value = '253';
        }
    }
    else if (country_val == "Dominica")
    {
        document.getElementById([hiddencountryvalue]).value = 'DM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Dominican Republic")
    {
        document.getElementById([hiddencountryvalue]).value = 'DO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Ecuador")
    {
        document.getElementById([hiddencountryvalue]).value = 'EC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '593';
            document.getElementById([hiddenpincode]).value = '593';
        }

    }
    else if (country_val == "Egypt")
    {
        document.getElementById([hiddencountryvalue]).value = 'EG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '020';
            document.getElementById([hiddenpincode]).value = '020';
        }
    }
    else if (country_val == "El Salvador")
    {
        document.getElementById([hiddencountryvalue]).value = 'SV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '503';
            document.getElementById([hiddenpincode]).value = '503';
        }
    }
    else if (country_val == "Equatorial Guinea")
    {
        document.getElementById([hiddencountryvalue]).value = 'GQ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '240';
            document.getElementById([hiddenpincode]).value = '240';
        }
    }
    else if (country_val == "Eritrea")
    {
        document.getElementById([hiddencountryvalue]).value = 'ER';
        if (pincode)
        {
            document.getElementById([pincode]).value = '291';
            document.getElementById([hiddenpincode]).value = '291';
        }
    }
    else if (country_val == "Estonia")
    {
        document.getElementById([hiddencountryvalue]).value = 'EE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '372';
            document.getElementById([hiddenpincode]).value = '372';
        }
    }
    else if (country_val == "Ethiopia")
    {
        document.getElementById([hiddencountryvalue]).value = 'ET';
        if (pincode)
        {
            document.getElementById([pincode]).value = '251';
            document.getElementById([hiddenpincode]).value = '251';
        }
    }
    else if (country_val == "Falkland Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'FK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '500';
            document.getElementById([hiddenpincode]).value = '500';
        }
    }
    else if (country_val == "Faroe Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'FO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '298';
            document.getElementById([hiddenpincode]).value = '298';
        }
    }
    else if (country_val == "Federated States of Micronesia")
    {
        document.getElementById([hiddencountryvalue]).value = 'FM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '691';
            document.getElementById([hiddenpincode]).value = '691';
        }
    }
    else if (country_val == "Fiji")
    {
        document.getElementById([hiddencountryvalue]).value = 'FJ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '679';
            document.getElementById([hiddenpincode]).value = '679';
        }
    }
    else if (country_val == "Finland")
    {
        document.getElementById([hiddencountryvalue]).value = 'FI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '358';
            document.getElementById([hiddenpincode]).value = '358';
        }
    }
    else if (country_val == "France")
    {
        document.getElementById([hiddencountryvalue]).value = 'FR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '033';
            document.getElementById([hiddenpincode]).value = '033';
        }
    }
    else if (country_val == "French Guiana")
    {
        document.getElementById([hiddencountryvalue]).value = 'GF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '594';
            document.getElementById([hiddenpincode]).value = '594';
        }
    }
    else if (country_val == "French Polynesia")
    {
        document.getElementById([hiddencountryvalue]).value = 'PF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '689';
            document.getElementById([hiddenpincode]).value = '689';
        }
    }
    else if (country_val == "French Southern and Antarctic Lands")
    {
        document.getElementById([hiddencountryvalue]).value = 'TF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '000';
            document.getElementById([hiddenpincode]).value = '000';
        }
    }
    else if (country_val == "Gabon")
    {
        document.getElementById([hiddencountryvalue]).value = 'GA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '241';
            document.getElementById([hiddenpincode]).value = '241';
        }
    }
    else if (country_val == "Gambia")
    {
        document.getElementById([hiddencountryvalue]).value = 'GM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '220';
            document.getElementById([hiddenpincode]).value = '220';
        }
    }
    else if (country_val == "Georgia")
    {
        document.getElementById([hiddencountryvalue]).value = 'GE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '995';
            document.getElementById([hiddenpincode]).value = '995';
        }
    }
    else if (country_val == "Germany")
    {
        document.getElementById([hiddencountryvalue]).value = 'DE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '049';
            document.getElementById([hiddenpincode]).value = '049';
        }
    }
    else if (country_val == "Ghana")
    {
        document.getElementById([hiddencountryvalue]).value = 'GH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '233';
            document.getElementById([hiddenpincode]).value = '233';
        }
    }
    else if (country_val == "Gibraltar")
    {
        document.getElementById([hiddencountryvalue]).value = 'GI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '350';
            document.getElementById([hiddenpincode]).value = '350';
        }
    }
    else if (country_val == "Greece")
    {
        document.getElementById([hiddencountryvalue]).value = 'GR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '030';
            document.getElementById([hiddenpincode]).value = '030';
        }
    }
    else if (country_val == "Greenland")
    {
        document.getElementById([hiddencountryvalue]).value = 'GL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '299';
            document.getElementById([hiddenpincode]).value = '030';
        }
    }
    else if (country_val == "Grenada")
    {
        document.getElementById([hiddencountryvalue]).value = 'GD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Guadeloupe")
    {
        document.getElementById([hiddencountryvalue]).value = 'GP';
        if (pincode)
        {
            document.getElementById([pincode]).value = '590';
            document.getElementById([hiddenpincode]).value = '590';
        }
    }
    else if (country_val == "Guam")
    {
        document.getElementById([hiddencountryvalue]).value = 'GU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Guatemala")
    {
        document.getElementById([hiddencountryvalue]).value = 'GT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '502';
            document.getElementById([hiddenpincode]).value = '590';
        }
    }
    else if (country_val == "Guernsey")
    {
        document.getElementById([hiddencountryvalue]).value = 'GG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '000';
            document.getElementById([hiddenpincode]).value = '000';
        }
    }
    else if (country_val == "Guinea")
    {
        document.getElementById([hiddencountryvalue]).value = 'GN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '224';
            document.getElementById([hiddenpincode]).value = '224';
        }
    }
    else if (country_val == "Guinea-Bissau")
    {
        document.getElementById([hiddencountryvalue]).value = 'GW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '245';
            document.getElementById([hiddenpincode]).value = '245';
        }
    }
    else if (country_val == "Guyana")
    {
        document.getElementById([hiddencountryvalue]).value = 'GY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '592';
            document.getElementById([hiddenpincode]).value = '592';
        }
    }
    else if (country_val == "Haiti")
    {
        document.getElementById([hiddencountryvalue]).value = 'HT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '509';
            document.getElementById([hiddenpincode]).value = '509';
        }
    }
    else if (country_val == "Heard Island & McDonald Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'HM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '672';
            document.getElementById([hiddenpincode]).value = '672';
        }
    }
    else if (country_val == "Honduras")
    {
        document.getElementById([hiddencountryvalue]).value = 'HN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '504';
            document.getElementById([hiddenpincode]).value = '504';
        }
    }
    else if (country_val == "Hong Kong")
    {
        document.getElementById([hiddencountryvalue]).value = 'HK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '852';
            document.getElementById([hiddenpincode]).value = '852';
        }
    }
    else if (country_val == "Hungary")
    {
        document.getElementById([hiddencountryvalue]).value = 'HU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '036';
            document.getElementById([hiddenpincode]).value = '036';
        }
    }
    else if (country_val == "Iceland")
    {
        document.getElementById([hiddencountryvalue]).value = 'IS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '354';
            document.getElementById([hiddenpincode]).value = '354';
        }
    }
    else if (country_val == "India")
    {
        document.getElementById([hiddencountryvalue]).value = 'IN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '091';
            document.getElementById([hiddenpincode]).value = '091';
        }
    }
    else if (country_val == "Indonesia")
    {
        document.getElementById([hiddencountryvalue]).value = 'ID';
        if (pincode)
        {
            document.getElementById([pincode]).value = '062';
            document.getElementById([hiddenpincode]).value = '062';
        }
    }
    else if (country_val == "Iran")
    {
        document.getElementById([hiddencountryvalue]).value = 'IR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '098';
            document.getElementById([hiddenpincode]).value = '098';
        }
    }
    else if (country_val == "Iraq")
    {
        document.getElementById([hiddencountryvalue]).value = 'IQ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '964';
            document.getElementById([hiddenpincode]).value = '964';
        }
    }
    else if (country_val == "Ireland")
    {
        document.getElementById([hiddencountryvalue]).value = 'IE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '353';
            document.getElementById([hiddenpincode]).value = '353';
        }
    }
    else if (country_val == "Israel")
    {
        document.getElementById([hiddencountryvalue]).value = 'IL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '972';
            document.getElementById([hiddenpincode]).value = '972';
        }
    }
    else if (country_val == "Italy")
    {
        document.getElementById([hiddencountryvalue]).value = 'IT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '039';
            document.getElementById([hiddenpincode]).value = '039';
        }
    }
    else if (country_val == "Jamaica")
    {
        document.getElementById([hiddencountryvalue]).value = 'JM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Japan")
    {
        document.getElementById([hiddencountryvalue]).value = 'JP';
        if (pincode)
        {
            document.getElementById([pincode]).value = '081';
            document.getElementById([hiddenpincode]).value = '081';
        }
    }
    else if (country_val == "Jersey")
    {
        document.getElementById([hiddencountryvalue]).value = 'JE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '044';
            document.getElementById([hiddenpincode]).value = '044';
        }
    }
    else if (country_val == "Jordan")
    {
        document.getElementById([hiddencountryvalue]).value = 'JO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '962';
            document.getElementById([hiddenpincode]).value = '962';
        }
    }
    else if (country_val == "Kazakhstan")
    {
        document.getElementById([hiddencountryvalue]).value = 'KZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '007';
            document.getElementById([hiddenpincode]).value = '007';
        }
    }
    else if (country_val == "Kenya")
    {
        document.getElementById([hiddencountryvalue]).value = 'KE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '254';
            document.getElementById([hiddenpincode]).value = '254';
        }
    }
    else if (country_val == "Kiribati")
    {
        document.getElementById([hiddencountryvalue]).value = 'KI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '686';
            document.getElementById([hiddenpincode]).value = '686';
        }
    }
    else if (country_val == "Kuwait")
    {
        document.getElementById([hiddencountryvalue]).value = 'KW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '965';
            document.getElementById([hiddenpincode]).value = '965';
        }
    }
    else if (country_val == "Kyrgystan")
    {
        document.getElementById([hiddencountryvalue]).value = 'KG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '996';
            document.getElementById([hiddenpincode]).value = '996';
        }
    }
    else if (country_val == "Laos")
    {
        document.getElementById([hiddencountryvalue]).value = 'LA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '856';
            document.getElementById([hiddenpincode]).value = '856';
        }
    }
    else if (country_val == "Latvia")
    {
        document.getElementById([hiddencountryvalue]).value = 'LV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '371';
            document.getElementById([hiddenpincode]).value = '371';
        }
    }
    else if (country_val == "Lebanon")
    {
        document.getElementById([hiddencountryvalue]).value = 'LB';
        if (pincode)
        {
            document.getElementById([pincode]).value = '961';
            document.getElementById([hiddenpincode]).value = '961';
        }
    }
    else if (country_val == "Lesotho")
    {
        document.getElementById([hiddencountryvalue]).value = 'LS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '266';
            document.getElementById([hiddenpincode]).value = '266';
        }
    }
    else if (country_val == "Liberia")
    {
        document.getElementById([hiddencountryvalue]).value = 'LR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '231';
            document.getElementById([hiddenpincode]).value = '231';
        }
    }
    else if (country_val == "Libya")
    {
        document.getElementById([hiddencountryvalue]).value = 'LY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '218';
            document.getElementById([hiddenpincode]).value = '218';
        }
    }
    else if (country_val == "Liechtenstein")
    {
        document.getElementById([hiddencountryvalue]).value = 'LI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '423';
            document.getElementById([hiddenpincode]).value = '423';
        }
    }
    else if (country_val == "Lithuania")
    {
        document.getElementById([hiddencountryvalue]).value = 'LT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '370';
            document.getElementById([hiddenpincode]).value = '370';
        }
    }
    else if (country_val == "Luxembourg")
    {
        document.getElementById([hiddencountryvalue]).value = 'LU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '352';
            document.getElementById([hiddenpincode]).value = '352';
        }
    }
    else if (country_val == "Macau, China")
    {
        document.getElementById([hiddencountryvalue]).value = 'MO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '853';
            document.getElementById([hiddenpincode]).value = '853';
        }
    }
    else if (country_val == "Macedonia")
    {
        document.getElementById([hiddencountryvalue]).value = 'MK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '389';
            document.getElementById([hiddenpincode]).value = '389';
        }
    }
    else if (country_val == "Madagascar")
    {
        document.getElementById([hiddencountryvalue]).value = 'MG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '261';
            document.getElementById([hiddenpincode]).value = '261';
        }
    }
    else if (country_val == "Malawi")
    {
        document.getElementById([hiddencountryvalue]).value = 'MW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '265';
            document.getElementById([hiddenpincode]).value = '265';
        }
    }
    else if (country_val == "Malaysia")
    {
        document.getElementById([hiddencountryvalue]).value = 'MY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '060';
            document.getElementById([hiddenpincode]).value = '060';
        }
    }
    else if (country_val == "Maldives")
    {
        document.getElementById([hiddencountryvalue]).value = 'MV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '960';
            document.getElementById([hiddenpincode]).value = '960';
        }
    }
    else if (country_val == "Mali")
    {
        document.getElementById([hiddencountryvalue]).value = 'ML';
        if (pincode)
        {
            document.getElementById([pincode]).value = '223';
            document.getElementById([hiddenpincode]).value = '223';
        }
    }
    else if (country_val == "Malta")
    {
        document.getElementById([hiddencountryvalue]).value = 'MT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '356';
            document.getElementById([hiddenpincode]).value = '356';
        }
    }
    else if (country_val == "Marshall Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'MH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '692';
            document.getElementById([hiddenpincode]).value = '692';
        }
    }
    else if (country_val == "Martinique")
    {
        document.getElementById([hiddencountryvalue]).value = 'MQ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '596';
            document.getElementById([hiddenpincode]).value = '596';
        }
    }
    else if (country_val == "Mauritania")
    {
        document.getElementById([hiddencountryvalue]).value = 'MR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '222';
            document.getElementById([hiddenpincode]).value = '222';
        }
    }
    else if (country_val == "Mauritius")
    {
        document.getElementById([hiddencountryvalue]).value = 'MU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '230';
            document.getElementById([hiddenpincode]).value = '230';
        }
    }
    else if (country_val == "Mayotte")
    {
        document.getElementById([hiddencountryvalue]).value = 'YT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '269';
            document.getElementById([hiddenpincode]).value = '269';
        }
    }
    else if (country_val == "Mexico")
    {
        document.getElementById([hiddencountryvalue]).value = 'MX';
        if (pincode)
        {
            document.getElementById([pincode]).value = '052';
            document.getElementById([hiddenpincode]).value = '052';
        }
    }
    else if (country_val == "Moldova")
    {
        document.getElementById([hiddencountryvalue]).value = 'MD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '373';
            document.getElementById([hiddenpincode]).value = '373';
        }
    }
    else if (country_val == "Monaco")
    {
        document.getElementById([hiddencountryvalue]).value = 'MC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '377';
            document.getElementById([hiddenpincode]).value = '377';
        }
    }
    else if (country_val == "Mongolia")
    {
        document.getElementById([hiddencountryvalue]).value = 'MN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '976';
            document.getElementById([hiddenpincode]).value = '976';
        }
    }
    else if (country_val == "Montenegro")
    {
        document.getElementById([hiddencountryvalue]).value = 'ME';
        if (pincode)
        {
            document.getElementById([pincode]).value = '382';
            document.getElementById([hiddenpincode]).value = '382';
        }
    }
    else if (country_val == "Montserrat")
    {
        document.getElementById([hiddencountryvalue]).value = 'MS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Morocco")
    {
        document.getElementById([hiddencountryvalue]).value = 'MA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '212';
            document.getElementById([hiddenpincode]).value = '212';
        }
    }
    else if (country_val == "Mozambique")
    {
        document.getElementById([hiddencountryvalue]).value = 'MZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '258';
            document.getElementById([hiddenpincode]).value = '258';
        }
    }
    else if (country_val == "Myanmar")
    {
        document.getElementById([hiddencountryvalue]).value = 'MM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '095';
            document.getElementById([hiddenpincode]).value = '095';
        }
    }
    else if (country_val == "Namibia")
    {
        document.getElementById([hiddencountryvalue]).value = 'NA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '264';
            document.getElementById([hiddenpincode]).value = '264';
        }
    }
    else if (country_val == "Nauru")
    {
        document.getElementById([hiddencountryvalue]).value = 'NR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '674';
            document.getElementById([hiddenpincode]).value = '674';
        }
    }
    else if (country_val == "Nepal")
    {
        document.getElementById([hiddencountryvalue]).value = 'NP';
        if (pincode)
        {
            document.getElementById([pincode]).value = '977';
            document.getElementById([hiddenpincode]).value = '977';
        }
    }
    else if (country_val == "Netherlands Antilles")
    {
        document.getElementById([hiddencountryvalue]).value = 'AN';
        document.getElementById([pincode]).value = '599';
        document.getElementById([hiddenpincode]).value = '599';
    }
    else if (country_val == "Netherlands")
    {
        document.getElementById([hiddencountryvalue]).value = 'NL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '031';
            document.getElementById([hiddenpincode]).value = '031';
        }
    }
    else if (country_val == "New Caledonia")
    {
        document.getElementById([hiddencountryvalue]).value = 'NC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '687';
            document.getElementById([hiddenpincode]).value = '687';
        }
    }
    else if (country_val == "New Zealand")
    {
        document.getElementById([hiddencountryvalue]).value = 'NZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '064';
            document.getElementById([hiddenpincode]).value = '064';
        }
    }
    else if (country_val == "Nicaragua")
    {
        document.getElementById([hiddencountryvalue]).value = 'NI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '505';
            document.getElementById([hiddenpincode]).value = '505';
        }
    }
    else if (country_val == "Niger")
    {
        document.getElementById([hiddencountryvalue]).value = 'NE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '227';
            document.getElementById([hiddenpincode]).value = '227';
        }
    }
    else if (country_val == "Nigeria")
    {
        document.getElementById([hiddencountryvalue]).value = 'NG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '234';
            document.getElementById([hiddenpincode]).value = '234';
        }
    }
    else if (country_val == "Niue")
    {
        document.getElementById([hiddencountryvalue]).value = 'NU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '683';
            document.getElementById([hiddenpincode]).value = '683';
        }
    }
    else if (country_val == "Norfolk Island")
    {
        document.getElementById([hiddencountryvalue]).value = 'NF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '672';
            document.getElementById([hiddenpincode]).value = '672';
        }
    }
    else if (country_val == "North Korea")
    {
        document.getElementById([hiddencountryvalue]).value = 'KP';
        if (pincode)
        {
            document.getElementById([pincode]).value = '850';
            document.getElementById([hiddenpincode]).value = '850';
        }
    }
    else if (country_val == "Northern Mariana Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'MP';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Norway")
    {
        document.getElementById([hiddencountryvalue]).value = 'NO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '047';
            document.getElementById([hiddenpincode]).value = '047';
        }
    }
    else if (country_val == "Oman")
    {
        document.getElementById([hiddencountryvalue]).value = 'OM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '968';
            document.getElementById([hiddenpincode]).value = '968';
        }
    }
    else if (country_val == "Pakistan")
    {
        document.getElementById([hiddencountryvalue]).value = 'PK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '092';
            document.getElementById([hiddenpincode]).value = '092';
        }
    }
    else if (country_val == "Palau")
    {
        document.getElementById([hiddencountryvalue]).value = 'PW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '680';
            document.getElementById([hiddenpincode]).value = '680';
        }
    }
    else if (country_val == "Palestinian Authority")
    {
        document.getElementById([hiddencountryvalue]).value = 'PS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '970';
            document.getElementById([hiddenpincode]).value = '970';
        }
    }
    else if (country_val == "Panama")
    {
        document.getElementById([hiddencountryvalue]).value = 'PA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '507';
            document.getElementById([hiddenpincode]).value = '507';
        }
    }
    else if (country_val == "Papua New Guinea")
    {
        document.getElementById([hiddencountryvalue]).value = 'PG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '675';
            document.getElementById([hiddenpincode]).value = '675';
        }
    }
    else if (country_val == "Paraguay")
    {
        document.getElementById([hiddencountryvalue]).value = 'PY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '595';
            document.getElementById([hiddenpincode]).value = '595';
        }
    }
    else if (country_val == "Peru")
    {
        document.getElementById([hiddencountryvalue]).value = 'PE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '051';
            document.getElementById([hiddenpincode]).value = '051';
        }
    }
    else if (country_val == "Philippines")
    {
        document.getElementById([hiddencountryvalue]).value = 'PH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '063';
            document.getElementById([hiddenpincode]).value = '063';
        }
    }
    else if (country_val == "Pitcairn Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'PN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '064';
            document.getElementById([hiddenpincode]).value = '064';
        }
    }
    else if (country_val == "Poland")
    {
        document.getElementById([hiddencountryvalue]).value = 'PL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '048';
            document.getElementById([hiddenpincode]).value = '048';
        }
    }
    else if (country_val == "Portugal")
    {
        document.getElementById([hiddencountryvalue]).value = 'PT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '351';
            document.getElementById([hiddenpincode]).value = '351';
        }
    }
    else if (country_val == "Puerto Rico")
    {
        document.getElementById([hiddencountryvalue]).value = 'PR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Qatar")
    {
        document.getElementById([hiddencountryvalue]).value = 'QA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '974';
            document.getElementById([hiddenpincode]).value = '974';
        }
    }
    else if (country_val == "Republic of the Congo")
    {
        document.getElementById([hiddencountryvalue]).value = 'CG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '242';
            document.getElementById([hiddenpincode]).value = '242';
        }
    }
    else if (country_val == "Reunion")
    {
        document.getElementById([hiddencountryvalue]).value = 'RE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '262';
            document.getElementById([hiddenpincode]).value = '262';
        }
    }
    else if (country_val == "Romania")
    {
        document.getElementById([hiddencountryvalue]).value = 'RO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '040';
            document.getElementById([hiddenpincode]).value = '040';
        }
    }
    else if (country_val == "Russia")
    {
        document.getElementById([hiddencountryvalue]).value = 'RU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '007';
            document.getElementById([hiddenpincode]).value = '007';
        }
    }
    else if (country_val == "Rwanda")
    {
        document.getElementById([hiddencountryvalue]).value = 'RW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '250';
            document.getElementById([hiddenpincode]).value = '250';
        }
    }
    else if (country_val == "Saint Barthelemy")
    {
        document.getElementById([hiddencountryvalue]).value = 'BL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '590';
            document.getElementById([hiddenpincode]).value = '590';
        }
    }
    else if (country_val == "Saint Helena, Ascension & Tristan daCunha")
    {
        document.getElementById([hiddencountryvalue]).value = 'SH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '290';
            document.getElementById([hiddenpincode]).value = '290';
        }
    }
    else if (country_val == "Saint Kitts and Nevis")
    {
        document.getElementById([hiddencountryvalue]).value = 'KN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Saint Lucia")
    {
        document.getElementById([hiddencountryvalue]).value = 'LC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Saint Martin")
    {
        document.getElementById([hiddencountryvalue]).value = 'MF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '590';
            document.getElementById([hiddenpincode]).value = '590';
        }
    }
    else if (country_val == "Saint Pierre and Miquelon")
    {
        document.getElementById([hiddencountryvalue]).value = 'PM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '508';
            document.getElementById([hiddenpincode]).value = '508';
        }
    }
    else if (country_val == "Saint Vincent and Grenadines")
    {
        document.getElementById([hiddencountryvalue]).value = 'VC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Samoa")
    {
        document.getElementById([hiddencountryvalue]).value = 'WS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '685';
            document.getElementById([hiddenpincode]).value = '685';
        }
    }
    else if (country_val == "San Marino")
    {
        document.getElementById([hiddencountryvalue]).value = 'SM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '378';
            document.getElementById([hiddenpincode]).value = '378';
        }
    }
    else if (country_val == "Sao Tome and Principe")
    {
        document.getElementById([hiddencountryvalue]).value = 'ST';
        if (pincode)
        {
            document.getElementById([pincode]).value = '239';
            document.getElementById([hiddenpincode]).value = '239';
        }
    }
    else if (country_val == "Saudi Arabia")
    {
        document.getElementById([hiddencountryvalue]).value = 'SA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '966';
            document.getElementById([hiddenpincode]).value = '966';
        }
    }
    else if (country_val == "Senegal")
    {
        document.getElementById([hiddencountryvalue]).value = 'SN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '221';
            document.getElementById([hiddenpincode]).value = '221';
        }
    }
    else if (country_val == "Serbia")
    {
        document.getElementById([hiddencountryvalue]).value = 'RS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '381';
            document.getElementById([hiddenpincode]).value = '381';
        }
    }
    else if (country_val == "Seychelles")
    {
        document.getElementById([hiddencountryvalue]).value = 'SC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '248';
            document.getElementById([hiddenpincode]).value = '248';
        }
    }
    else if (country_val == "Sierra Leone")
    {
        document.getElementById([hiddencountryvalue]).value = 'SL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '232';
            document.getElementById([hiddenpincode]).value = '232';
        }
    }
    else if (country_val == "Singapore")
    {
        document.getElementById([hiddencountryvalue]).value = 'SG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '065';
            document.getElementById([hiddenpincode]).value = '065';
        }
    }
    else if (country_val == "Slovakia")
    {
        document.getElementById([hiddencountryvalue]).value = 'SK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '421';
            document.getElementById([hiddenpincode]).value = '421';
        }
    }
    else if (country_val == "Slovenia")
    {
        document.getElementById([hiddencountryvalue]).value = 'SI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '386';
            document.getElementById([hiddenpincode]).value = '386';
        }
    }
    else if (country_val == "Solomon Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'SB';
        if (pincode)
        {
            document.getElementById([pincode]).value = '677';
            document.getElementById([hiddenpincode]).value = '677';
        }
    }
    else if (country_val == "Somalia")
    {
        document.getElementById([hiddencountryvalue]).value = 'SO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '252';
            document.getElementById([hiddenpincode]).value = '252';
        }
    }
    else if (country_val == "South Africa")
    {
        document.getElementById([hiddencountryvalue]).value = 'ZA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '027';
            document.getElementById([hiddenpincode]).value = '027';
        }
    }
    else if (country_val == "South Georgia & South Sandwich Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'GS';
        if (pincode)
        {
            document.getElementById([pincode]).value = '000';
            document.getElementById([hiddenpincode]).value = '000';
        }
    }
    else if (country_val == "South Korea")
    {
        document.getElementById([hiddencountryvalue]).value = 'KR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '082';
            document.getElementById([hiddenpincode]).value = '082';
        }
    }
    else if (country_val == "Spain")
    {
        document.getElementById([hiddencountryvalue]).value = 'ES';
        if (pincode)
        {
            document.getElementById([pincode]).value = '034';
            document.getElementById([hiddenpincode]).value = '034';
        }
    }
    else if (country_val == "Sri Lanka")
    {
        document.getElementById([hiddencountryvalue]).value = 'LK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '094';
            document.getElementById([hiddenpincode]).value = '094';
        }
    }
    else if (country_val == "Sudan")
    {
        document.getElementById([hiddencountryvalue]).value = 'SD';
        if (pincode)
        {
            document.getElementById([pincode]).value = '249';
            document.getElementById([hiddenpincode]).value = '249';
        }
    }
    else if (country_val == "Suriname")
    {
        document.getElementById([hiddencountryvalue]).value = 'SR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '597';
            document.getElementById([hiddenpincode]).value = '597';
        }
    }
    else if (country_val == "Svalbard and Jan Mayen")
    {
        document.getElementById([hiddencountryvalue]).value = 'SJ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '047';
            document.getElementById([hiddenpincode]).value = '047';
        }
    }
    else if (country_val == "Swaziland")
    {
        document.getElementById([hiddencountryvalue]).value = 'SZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '268';
            document.getElementById([hiddenpincode]).value = '268';
        }
    }
    else if (country_val == "Sweden")
    {
        document.getElementById([hiddencountryvalue]).value = 'SE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '046';
            document.getElementById([hiddenpincode]).value = '046';
        }
    }
    else if (country_val == "Switzerland")
    {
        document.getElementById([hiddencountryvalue]).value = 'CH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '041';
            document.getElementById([hiddenpincode]).value = '041';
        }
    }
    else if (country_val == "Syria")
    {
        document.getElementById([hiddencountryvalue]).value = 'SY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '963';
            document.getElementById([hiddenpincode]).value = '963';
        }
    }
    else if (country_val == "Taiwan")
    {
        document.getElementById([hiddencountryvalue]).value = 'TW';
        if (pincode)
        {
            document.getElementById([pincode]).value = '886';
            document.getElementById([hiddenpincode]).value = '886';
        }
    }
    else if (country_val == "Tajikistan")
    {
        document.getElementById([hiddencountryvalue]).value = 'TJ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '992';
            document.getElementById([hiddenpincode]).value = '992';
        }
    }
    else if (country_val == "Tanzania")
    {
        document.getElementById([hiddencountryvalue]).value = 'TZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '255';
            document.getElementById([hiddenpincode]).value = '255';
        }
    }
    else if (country_val == "Thailand")
    {
        document.getElementById([hiddencountryvalue]).value = 'TH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '066';
            document.getElementById([hiddenpincode]).value = '066';
        }
    }
    else if (country_val == "Timor-Leste")
    {
        document.getElementById([hiddencountryvalue]).value = 'TL';
        if (pincode)
        {
            document.getElementById([pincode]).value = '670';
            document.getElementById([hiddenpincode]).value = '670';
        }
    }
    else if (country_val == "Togo")
    {
        document.getElementById([hiddencountryvalue]).value = 'TG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '228';
            document.getElementById([hiddenpincode]).value = '228';
        }
    }
    else if (country_val == "Tokelau")
    {
        document.getElementById([hiddencountryvalue]).value = 'TK';
        if (pincode)
        {
            document.getElementById([pincode]).value = '690';
            document.getElementById([hiddenpincode]).value = '690';
        }
    }
    else if (country_val == "Tonga")
    {
        document.getElementById([hiddencountryvalue]).value = 'TO';
        if (pincode)
        {
            document.getElementById([pincode]).value = '676';
            document.getElementById([hiddenpincode]).value = '676';
        }
    }
    else if (country_val == "Trinidad and Tobago")
    {
        document.getElementById([hiddencountryvalue]).value = 'TT';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Tunisia")
    {
        document.getElementById([hiddencountryvalue]).value = 'TN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '216';
            document.getElementById([hiddenpincode]).value = '216';
        }
    }
    else if (country_val == "Turkey")
    {
        document.getElementById([hiddencountryvalue]).value = 'TR';
        if (pincode)
        {
            document.getElementById([pincode]).value = '090';
            document.getElementById([hiddenpincode]).value = '090';
        }
    }
    else if (country_val == "Turkmenistan")
    {
        document.getElementById([hiddencountryvalue]).value = 'TM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '993';
            document.getElementById([hiddenpincode]).value = '993';
        }
    }
    else if (country_val == "Turks and Caicos Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'TC';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Tuvalu")
    {
        document.getElementById([hiddencountryvalue]).value = 'TV';
        if (pincode)
        {
            document.getElementById([pincode]).value = '688';
            document.getElementById([hiddenpincode]).value = '688';
        }
    }
    else if (country_val == "Uganda")
    {
        document.getElementById([hiddencountryvalue]).value = 'UG';
        if (pincode)
        {
            document.getElementById([pincode]).value = '296';
            document.getElementById([hiddenpincode]).value = '296';
        }
    }
    else if (country_val == "Ukraine")
    {
        document.getElementById([hiddencountryvalue]).value = 'UA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '380';
            document.getElementById([hiddenpincode]).value = '380';
        }
    }
    else if (country_val == "United Arab Emirates")
    {
        document.getElementById([hiddencountryvalue]).value = 'AE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '971';
            document.getElementById([hiddenpincode]).value = '971';
        }
    }
    else if (country_val == "United Kingdom")
    {
        document.getElementById([hiddencountryvalue]).value = 'GB';
        if (pincode)
        {
            document.getElementById([pincode]).value = '044';
            document.getElementById([hiddenpincode]).value = '044';
        }
    }
    else if (country_val == "United States")
    {
        document.getElementById([hiddencountryvalue]).value = 'US';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "United States Virgin Islands")
    {
        document.getElementById([hiddencountryvalue]).value = 'VI';
        if (pincode)
        {
            document.getElementById([pincode]).value = '001';
            document.getElementById([hiddenpincode]).value = '001';
        }
    }
    else if (country_val == "Uruguay")
    {
        document.getElementById([hiddencountryvalue]).value = 'UY';
        if (pincode)
        {
            document.getElementById([pincode]).value = '598';
            document.getElementById([hiddenpincode]).value = '598';
        }

    }
    else if (country_val == "Uzbekistan")
    {
        document.getElementById([hiddencountryvalue]).value = 'UZ';
        if (pincode)
        {
            document.getElementById([pincode]).value = '998';
            document.getElementById([hiddenpincode]).value = '998';
        }
    }
    else if (country_val == "Vanuatu")
    {
        document.getElementById([hiddencountryvalue]).value = 'VU';
        if (pincode)
        {
            document.getElementById([pincode]).value = '678';
            document.getElementById([hiddenpincode]).value = '678';
        }
    }
    else if (country_val == "Vatican City")
    {
        document.getElementById([hiddencountryvalue]).value = 'VA';
        if (pincode)
        {
            document.getElementById([pincode]).value = '379';
            document.getElementById([hiddenpincode]).value = '379';
        }
    }
    else if (country_val == "Venezuela")
    {
        document.getElementById([hiddencountryvalue]).value = 'VE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '058';
            document.getElementById([hiddenpincode]).value = '058';
        }
    }
    else if (country_val == "Vietnam")
    {
        document.getElementById([hiddencountryvalue]).value = 'VN';
        if (pincode)
        {
            document.getElementById([pincode]).value = '084';
            document.getElementById([hiddenpincode]).value = '084';
        }
    }
    else if (country_val == "Wallis and Futuna")
    {
        document.getElementById([hiddencountryvalue]).value = 'WF';
        if (pincode)
        {
            document.getElementById([pincode]).value = '681';
            document.getElementById([hiddenpincode]).value = '681';
        }
    }
    else if (country_val == "Western Sahara")
    {
        document.getElementById([hiddencountryvalue]).value = 'EH';
        if (pincode)
        {
            document.getElementById([pincode]).value = '212';
            document.getElementById([hiddenpincode]).value = '212';
        }
    }
    else if (country_val == "Yemen")
    {
        document.getElementById([hiddencountryvalue]).value = 'YE';
        if (pincode)
        {
            document.getElementById([pincode]).value = '967';
            document.getElementById([hiddenpincode]).value = '967';
        }
    }
    else if (country_val == "Zambia")
    {
        document.getElementById([hiddencountryvalue]).value = 'ZM';
        if (pincode)
        {
            document.getElementById([pincode]).value = '260';
            document.getElementById([hiddenpincode]).value = '260';
        }
    }
    else if (country_val == "Zimbabwe")
        {
            document.getElementById([hiddencountryvalue]).value = 'ZW';
            if (pincode)
            {
                document.getElementById([pincode]).value = '263';
                document.getElementById([hiddenpincode]).value = '263';
            }
        }
        else
        {
            document.getElementById([hiddencountryvalue]).value = 'IN';
            if (pincode)
            {
                document.getElementById([pincode]).value = '091';
                document.getElementById([hiddenpincode]).value = '091';
            }
        }

}
// country end


function cityLabel(countryId, cityId)
{
    var country = document.getElementById(countryId).value;
    switch (country)
    {
        case "GB":
            return document.getElementById(cityId).innerHTML = "Town";
        default:
            return document.getElementById(cityId).innerHTML = "City";
    }
}

function zipCodeLabel(countryId, zipId)
{
    var country = document.getElementById(countryId).value;
    switch (country)
    {
        case "GB":
            return document.getElementById(zipId).innerHTML = "Postcode";
        default:
            return document.getElementById(zipId).innerHTML = "Zip";
    }
}



function TIGERPAYHideShow(method, displayLabel,isAutoSubmit)
{

    typeLabel   = displayLabel;
    method      = method;
    $("#TIGERPAYOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    if(isAutoSubmit){
        if (method == "TigerPay_Wallet")
        {
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_TigerPay_Wallet').val($('#email_nbi').val());
                $('#firstname_TigerPay_Wallet').val($('#firstname_nbi').val());
                $('#lastname_TigerPay_Wallet').val($('#lastname_nbi').val());
                $('#phone-CC_TigerPay_Wallet').val($('#phonecc_id_nbi').val());
                $('#telno_TigerPay_Wallet').val($('#INphoneNum_nbi').val());
            }
            document.TigerPay_WalletForm.submit();
            show_loader();
        }else if(method == "JPBANK"){
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_JPBANK').val($('#email_nbi').val());
                $('#firstname_JPBANK').val($('#firstname_nbi').val());
                $('#lastname_JPBANK').val($('#lastname_nbi').val());
                $('#phone-CC_JPBANK').val($('#phonecc_id_nbi').val());
                $('#telno_JPBANK').val($('#INphoneNum_nbi').val());
            }
            document.JPBANKForm.submit();
            show_loader();
        }
    }
    showTab(currentTab, method);
    backButtonConfig();

}

function TIGERPAYHideShow2(method, displayLabel)
{
    typeLabel   = displayLabel;
    method      = method;
    if (method == "TigerPay_Wallet")
    {
        document.TigerPay_WalletForm.submit();
        show_loader();
    }else if(method == "JPBANK"){
        document.JPBANKForm.submit();
        show_loader();
    }else{

        $("#TIGERPAYOption").hide();
        $("#" + method).addClass('active');
        document.getElementById('payMethodDisplay').innerText = typeLabel;
        document.getElementById('payMethod').innerText = method;
        showTab(currentTab, method);
        backButtonConfig();
    }
}

function CARDHideShow(method, displayLabel)
{
    typeLabel = displayLabel;
    $("#CARDOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;
    if (method == "CARD")
    {
        document.CARDForm.submit();
        show_loader();
    }
    showTab(currentTab, method);
    backButtonConfig();
}

function MobileBankingBanglaHideShow(method, displayLabel,isAutoSubmit)
{
    typeLabel = displayLabel;
    $("#MobileBankingBanglaOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;

    if(isAutoSubmit){
        console.log("personalInfoDisplay ---> "+personalInfoDisplay)
        /*if(personalInfoDisplay == 'Y'){
         $('#emailaddr_BDE').val($('#email_nbi').val());
         $('#firstname_BDE').val($('#firstname_nbi').val());
         $('#lastname_BDE').val($('#lastname_nbi').val());
         $('#phone-CC_BDE').val($('#phonecc_id_nbi').val());
         $('#telno_BDE').val($('#INphoneNum_nbi').val());
         }*/

        if (method == "Mobile_Banking")
        {
            document.Mobile_BankingForm.submit();
            show_loader();
        }
        else if(method == "TAP"){
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_TAP').val($('#email_nbi').val());
                $('#firstname_TAP').val($('#firstname_nbi').val());
                $('#lastname_TAP').val($('#lastname_nbi').val());
                $('#phone-CC_TAP').val($('#phonecc_id_nbi').val());
                $('#telno_TAP').val($('#INphoneNum_nbi').val());
            }
            document.TAPForm.submit();
            show_loader();
        }else if(method == "NAGAD"){
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_NAGAD').val($('#email_nbi').val());
                $('#firstname_NAGAD').val($('#firstname_nbi').val());
                $('#lastname_NAGAD').val($('#lastname_nbi').val());
                $('#phone-CC_NAGAD').val($('#phonecc_id_nbi').val());
                $('#telno_NAGAD').val($('#INphoneNum_nbi').val());
            }
            document.NAGADForm.submit();
            show_loader();
        }else if(method == "UPAY"){
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_UPAY').val($('#email_nbi').val());
                $('#firstname_UPAY').val($('#firstname_nbi').val());
                $('#lastname_UPAY').val($('#lastname_nbi').val());
                $('#phone-CC_UPAY').val($('#phonecc_id_nbi').val());
                $('#telno_UPAY').val($('#INphoneNum_nbi').val());
            }
            document.UPAYForm.submit();
            show_loader();
        }else if(method == "DBBL_MobileBanking"){
            if(personalInfoDisplay == 'Y'){
                $('#emailaddr_DBBL_MobileBanking').val($('#email_nbi').val());
                $('#firstname_DBBL_MobileBanking').val($('#firstname_nbi').val());
                $('#lastname_DBBL_MobileBanking').val($('#lastname_nbi').val());
                $('#phone-CC_DBBL_MobileBanking').val($('#phonecc_id_nbi').val());
                $('#telno_DBBL_MobileBanking').val($('#INphoneNum_nbi').val());
            }
            document.DBBL_MobileBankingForm.submit();
            show_loader();
        }
    }
    showTab(currentTab, method);
    backButtonConfig();
}

//Send OTP functionality
function validateEmailOtp(personalDisplayFlag)   // < arrow to open Cancel Modal
{
    sendOTP(personalDisplayFlag);
    $('#cancleOtpPopup').css('pointer-events', 'none');
    $('#cancleOtpPopup').css('cursor', 'auto');
    $('#cancleOtpPopup').prop('disabled', 'true');
    timer(120);
    console.log("personalDisplayFlag+++" + personalDisplayFlag);
    /*$("#CancelModalCreditcard").addClass("show").css("display", "inline-block");*/
    var emailValue = $("input[name=emailaddr]").val();
    var PhoneValue = $("input[name=telno]").val();
    console.log("inside OTPInfo email values" + emailValue)
    console.log("inside OTPInfo phone values" + PhoneValue)
    $("#copyOTPPhoneNumber").text(PhoneValue);
    $("#copyOTPEmail").text(emailValue);
    $('#regenerateOTPText').css('pointer-events', 'none');
    $('#regenerateOTPText').css('cursor', 'auto');


}
var timerOn = true;

function closeCancelEmailModal()  // Close the modal
{
    $("#CancelModalCreditcard").removeClass("show").css("display", "none");
    $('#regenerateOTPText').css('pointer-events', 'none');
    $('#regenerateOTPText').css('cursor', 'auto');
    //document.getElementById('regenerateOTPText').innerHTML = "";
    document.getElementById('mainTimer').innerHTML = "";

}

function disableResend(type)
{
    $('#cancleOtpPopup').css('pointer-events', 'none');
    $('#cancleOtpPopup').css('cursor', 'auto');
    $('#cancleOtpPopup').prop('disabled', 'true');
    timer(120);
}

//Function to set timer for resend
function timer(remaining) {
    //$("#cancleOtpPopup").css("display","none");
    var m = Math.floor(remaining / 60);
    var s = remaining % 60;

    m = m < 10 ? '0' + m : m;
    s = s < 10 ? '0' + s : s;
    document.getElementById('mainTimer').innerHTML = "Resend OTP In: <b>" +m + ':' + s+"</b>";
    $('#regenerateOTPText').css('pointer-events', 'none');
    $('#regenerateOTPText').css('cursor', 'auto');
    $('#regenerateOTPText').prop('disabled', 'true');

    //document.getElementById('regenerateOTPText').innerHTML = "";
    remaining -= 1;

    if(remaining >= 0 && timerOn) {
        setTimeout(function() {
            timer(remaining);
        }, 1000);
        return;
    }

    if(!timerOn) {
        // Do validate stuff here
        return;
    }

    // Do timeout stuff here
    console.log('Timeout for otp');
    //$("#cancleOtpPopup").css("display","block");
    document.getElementById('mainTimer').innerHTML="";
    $('#regenerateOTPText').css('pointer-events', 'inherit');
    $('#regenerateOTPText').css('cursor', 'pointer');
    $('#regenerateOTPText').removeAttr("disabled");
    $('#cancleOtpPopup').css('pointer-events', 'inherit');
    $('#cancleOtpPopup').css('cursor', 'pointer');
    $('#cancleOtpPopup').removeAttr("disabled");
    //document.getElementById('regenerateOTPText').innerHTML = "Resend OTP"


}

//Function to resend OTP
function regenerateOTP(personalDetailDisplay)
{
    console.log("personalDetailDisplay+++",personalDetailDisplay);
    disableResend(personalDetailDisplay);
    sendOTP(personalDetailDisplay);
}

function verifyOTP(personalDetailFlag)
{
    console.log("personalDetailDisplay+++"+personalDetailFlag);
    var sendOTPData= "";
    var authToken ="";
    //AuthToken Generate
    var userName = $("#userName").val();
    var partnerId = $("#partId").val();
    var skey = $("#clkey").val();
    var mobileNo = $("#phonenophonecc").val();
    var ccValue = $("#phonecc-id").val();
    ccValue = ccValue.replace(/^0+/, '');
    console.log("ccValue++++"+ccValue);
    var emailId =  $("#email").val();
    var orderID = $("#transactionOrder_id").val();
    var merchantID = $("#memberId").val();
    var mobileOtp = $("#fisrtM").val()+$("#secondM").val()+$("#thirdM").val()+$("#fourthM").val()+$("#fifthM").val()+$("#sixthM").val();
    var emailOtp = $("#fisrtE").val()+$("#secondE").val()+$("#thirdE").val()+$("#fourthE").val()+$("#fifthE").val()+$("#sixthE").val();

    if(personalDetailFlag == "Y")
    {
        sendOTPData = {
            "customer.mobile":ccValue+mobileNo,
            "customer.email":emailId,
            "merchantTransactionId":orderID,
            "customer.smsOtp":mobileOtp,
            "customer.emailOtp":emailOtp
        }
    }
    else if(personalDetailFlag == "M")
    {
        sendOTPData = {
            "customer.mobile":ccValue+mobileNo,
            "merchantTransactionId": orderID,
            "customer.smsOtp":mobileOtp
        }
    }
    else if(personalDetailFlag == "E")
    {
        sendOTPData = {
            "customer.email": emailId,
            "merchantTransactionId": orderID,
            "customer.emailOtp":emailOtp
        }
    }
    else
    {
        sendOTPData = {
            "customer.mobile":ccValue+mobileNo,
            "customer.email": emailId,
            "merchantTransactionId": orderID,
            "customer.smsOtp":mobileOtp,
            "customer.emailOtp":emailOtp
        }
    }
    console.log("before send otp ajax+++"+authToken)
    $.ajax({
        url: "/transactionServices/REST/v1/authToken",
        type: 'post',
        async: true,
        data: {
            "merchant.username": userName,
            "authentication.partnerId": partnerId,
            "authentication.sKey": skey
        },
        success: function (data) {
            console.log("inside success otp",data);
            console.log("inside success otp authToken",data.AuthToken);
            authToken = data.AuthToken;
            if(authToken != "")
            {
                $.ajax({
                    url: "/transactionServices/REST/v1/verifyTransactionOTP",
                    headers: {
                        "AuthToken": authToken
                    },
                    type: 'post',
                    async: true,
                    data: sendOTPData,
                    success: function (data)
                    {
                        console.log("inside success verifyyy otp", data.result.description);
                        var description = data.result.description;
                        $("#messageOTP").text(description);
                        if(data.result.description == "Successful OTP verification")
                        {
                            $("#SuccessOTP").val("Y");
                            document.getElementById('mainTimer').innerHTML="";
                            $('#regenerateOTPText').css('pointer-events', 'none');
                            $('#regenerateOTPText').css('cursor', 'auto');
                            //document.getElementById('regenerateOTPText').innerHTML = "";
                            closeCancelEmailModal();
                            setTimeout(nextPrev(1), 3000);

                        }
                    },
                    error: function ()
                    {
                        console.log("inside error send otp")
                        // return false;
                    }
                });
            }
        },
        error: function () {
            console.log("inside error verify otp");
            //return false;
        }
    });

}

//Send OTP
function sendOTP(personalDetailDisplay)
{
    console.log("personalDetailDisplay+++"+personalDetailDisplay);
    var sendOTPData = "";

    var authToken ="";
    //AuthToken Generate
    var userName = $("#userName").val();
    var partnerId = $("#partId").val();
    var skey = $("#clkey").val();
    var mobileNo = $("#phonenophonecc").val();
    var ccValue = $("#phonecc-id").val();
    ccValue = ccValue.replace(/^0+/, '');
    console.log("ccValue++++"+ccValue);
    var emailId =  $("#email").val();
    var orderID = $("#transactionOrder_id").val();
    var merchantID = $("#memberId").val();
    if(personalDetailDisplay == "Y")
    {
        sendOTPData = {
            "customer.mobile": ccValue+mobileNo,
            "customer.email": emailId,
            "merchantTransactionId": orderID,
            "merchant_id": merchantID
        }
    }
    else if(personalDetailDisplay == "M")
    {
        sendOTPData = {
            "customer.mobile": ccValue+mobileNo,
            "merchantTransactionId": orderID,
            "merchant_id": merchantID
        }
    }
    else if(personalDetailDisplay == "E")
    {
        sendOTPData = {
            "customer.email": emailId,
            "merchantTransactionId": orderID,
            "merchant_id": merchantID
        }
    }
    else
    {
        sendOTPData = {
            "customer.mobile": ccValue+mobileNo,
            "customer.email": emailId,
            "merchantTransactionId": orderID,
            "merchant_id": merchantID
        }
    }
    console.log("before send otp ajax+++"+authToken)
    $.ajax({
        url: "/transactionServices/REST/v1/authToken",
        type: 'post',
        async: true,
        data: {
            "merchant.username": userName,
            "authentication.partnerId": partnerId,
            "authentication.sKey": skey
        },
        success: function (data) {
            console.log("inside success otp",data);
            console.log("inside success otp authToken",data.AuthToken);
            authToken = data.AuthToken;
            if(authToken != "")
            {
                $.ajax({
                    url: "/transactionServices/REST/v1/generateTransactionOTP",
                    headers: {
                        "AuthToken": authToken
                    },
                    type: 'post',
                    async: true,
                    data: sendOTPData,
                    success: function (data)
                    {
                        console.log("inside success send otp", data);
                        var description = data.result.description;
                        $("#messageOTP").text(description);
                        if(description =="Exceed OTP Generation LIMIT, Please Initiate New Transaction")
                        {
                            $("#mainTimer").hide()
                            document.getElementById('mainTimer').innerHTML="";
                            $('#cancleOtpPopup').css('pointer-events', 'inherit');
                            $('#cancleOtpPopup').css('cursor', 'pointer');
                            $('#cancleOtpPopup').removeAttr("disabled");
                        }
                        $("#CancelModalCreditcard").addClass("show").css("display", "inline-block");
                        var isMobileNoVerified = data.customer.isMobileNoVerified;
                        var isEmailVerified = data.customer.isEmailVerified;
                        if(isMobileNoVerified == "Y" && isEmailVerified =="Y")
                        {
                            $("#CancelModalCreditcard").hide();
                            $("#SuccessOTP").val("Y");
                            $("#nextBtn").click();
                        }
                        if(isMobileNoVerified == "Y" && isEmailVerified!="Y")
                        {
                            $("#CancelModalCreditcard").addClass("show").css("display", "inline-block");
                            $("#mobileIdVerify").hide();
                        }
                        if(isEmailVerified == "Y" && isMobileNoVerified != "Y")
                        {
                            $("#CancelModalCreditcard").addClass("show").css("display", "inline-block");
                            $("#emailIdVerify").hide();
                        }
                        else if(isMobileNoVerified != "Y" && isEmailVerified !="Y")
                        {
                            $("#CancelModalCreditcard").addClass("show").css("display", "inline-block");
                        }

                    },
                    error: function ()
                    {
                        console.log("inside error send otp")
                        // return false;
                    }
                });
            }
        },
        error: function () {
            console.log("inside error otp");
            //return false;
        }
    });
}
function inputInsideOtpInput(el) {
    var value = el.value;
    var numbers = value.replace(/[^0-9]/g, "");
    el.value = numbers;
    if (el.value.length > 1){
        el.value = el.value[el.value.length - 1];
    }
    try {
        if(el.value == null || el.value == ""){
            this.foucusOnInput(el.previousElementSibling);
        }else {
            this.foucusOnInput(el.nextElementSibling);
        }
    }catch (e) {
        console.log(e);
    }
}

function foucusOnInput(ele){
    ele.focus();
    var val = ele.value;
    ele.value = "";
    // ele.value = val;
    setTimeout( function(){
        ele.value = val;
    })
}

function pincodeccAF(countryvalue, hiddencountryvalue, pincode, hiddenpincode, countryId, hiddenId, StateId, labels){
    var country         = $('select[id=merchantcountry] option').filter(':selected').text();
    $('#hiddenCountry').val(country);
    pincodecc(countryvalue, hiddencountryvalue, pincode, hiddenpincode, countryId, hiddenId, StateId, labels)
}

function MobileMoneyHideShow(method, displayLabel)
{

    typeLabel   = displayLabel;
    method      = method;
    $("#MobileMoneyAfricaOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText = typeLabel;
    document.getElementById('payMethod').innerText = method;

    /* if (method == "Airtel_Uganda")
     {
     if(personalInfoDisplay == 'Y'){
     $('#emailaddr_TigerPay_Wallet').val($('#email_nbi').val());
     $('#phone-CC_TigerPay_Wallet').val($('#phonecc_id_nbi').val());
     $('#telno_TigerPay_Wallet').val($('#AFphoneNum_nbi').val());
     }
     }

     }*/
    showTab(currentTab, method);
    backButtonConfig();
}

function validatePhoneAfrica(value, id, length){
    $('#emailaddr_Airtel_Uganda').val($('#email_nbi').val());
    $('#phone-CC_Airtel_Uganda').val($('#phonecc_id_nbi').val());
    $('#telno_Airtel_Uganda').val($('#AFphoneNum_nbi').val());
    $('#emailaddr_MTN_Uganda').val($('#email_nbi').val());
    $('#phone-CC_MTN_Uganda').val($('#phonecc_id_nbi').val());
    $('#telno_MTN_Uganda').val($('#AFphoneNum_nbi').val());
    var error = "";
    var valid;
    var regex = new RegExp('^[0-9]+$');
    value = value.toString();
    try {
        if (value == "")
        {
            error = EmptyPhoneNo;
            valid = false;
        }
        else if (!regex.test(value)){
            console.log("in fail regex test")
            error = InvalidPhoneNo;
            valid = false;
        }
        else if (value.length < length || value.length > length )
        {
            error = InvalidPhoneNo;
            valid = false;
        }
        else
        {
            valid = true;
        }
    }
    catch(err) {
        error = InvalidPhoneNo;
        valid = false;
    }
    error_msg(error, id);
    return valid;
}

function valiDateData(text,id)
{
    console.log("valiDateData "+text)
    var pattern = /[a-zA-Z0-9!@#\$%\^\&*\)\(._-]+$/;
    var valid   = true;
    if (text == "")
    {
        if(pay_methods == "GiftCardAfrica"){
            error = "Invalid RedBoxx Gift Card Code";
        }else if(pay_methods == "MobileMoneyAfrica"){
            error = EmptyPhoneNo;
        }else{
            error = EmptyPhoneNo +" OR "+EmptyEmail;
        }

        valid = false;
        error_msg(error, id);
    }else if(!pattern.test(text)){
        console.log("valiDateData else ")
        if(pay_methods == "GiftCardAfrica"){
            error = "Invalid RedBoxx Gift Card Code";
        }else if(pay_methods == "MobileMoneyAfrica"){
            error = InvalidPhoneNo;
        }else{
            error = InvalidPhoneNo + " OR "+InvalidEmail;
        }
        valid   = false;
        error_msg(error, id);
    }
    console.log("error if"+error);
    return valid;
}


function validateName(input, divID)
{
    console.log("input >>> ",input);
    console.log("divID >>> ",divID);
    var val     = document.getElementById(input).value;
    val         = val.trim();
    var error   = "";
    var valid;
    var regex = new RegExp('[0-9!@#$%^&*()_+?><:;,./{}]');
    if (divID)
    {
        if (regex.test(val) || val == "" || val == null || val == " ")
        {

            if(input =='firstname_nbi'){
                error = "Invalid First Name";
            }
            if(input =='lastname_nbi'){
                error = "Invalid Last Name";
            }
            valid = false;
        }
        else
        {
            valid = true;
        }

        error_msg(error, divID);
        return valid;
    }
}

//Copy OTP
document.addEventListener("DOMContentLoaded", function(event) {

    function OTPInput() {
        const editor = document.getElementById('fisrtM');
        editor.onpaste = pasteOTP;

        const inputs = document.querySelectorAll('#otpM > *[id]');
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].addEventListener('input', function(event) {
                if(!event.target.value || event.target.value == '' ){
                    if(event.target.previousSibling.previousSibling){
                        event.target.previousSibling.previousSibling.focus();
                    }

                }else{
                    if(event.target.nextSibling.nextSibling){
                        event.target.nextSibling.nextSibling.focus();
                    }
                }
            });
        }
    }
    function OTPInput2() {
        const editor = document.getElementById('fisrtE');
        editor.onpaste = pasteOTP;

        const inputs = document.querySelectorAll('#otpE > *[id]');
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].addEventListener('input', function(event) {
                if(!event.target.value || event.target.value == '' ){
                    if(event.target.previousSibling.previousSibling){
                        event.target.previousSibling.previousSibling.focus();
                    }

                }else{
                    if(event.target.nextSibling.nextSibling){
                        event.target.nextSibling.nextSibling.focus();
                    }
                }
            });
        }
    }
    OTPInput();
    OTPInput2();
});

function pasteOTP(event){
    event.preventDefault();
    var elm = event.target;
    var pasteVal = event.clipboardData.getData('text').split("");
    if(pasteVal.length > 0){
        while(elm){
            elm.value = pasteVal.shift();
            elm = elm.nextSibling.nextSibling;
        }
    }
}

function UPIHideShow(method, displayLabel)
{
    method      = method+"_U";
    typeLabel   = displayLabel;
    $("#UPIOption").hide();
    $("#UPIQR_detailsForm").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText   = typeLabel;
    document.getElementById('payMethod').innerText          = method;

    console.log("currentTab___UPI++"+currentTab);
    var formname    = $("#" + method).find("form")[0].id;
    var x           = $("#" + formname).find(".tab");
    if(x[0] != undefined && (x[0].id =="UPIQR_U_detailsForm"))
    {
        $('#UPIQR_UForm').attr('action', '/transaction/SingleCallCheckout');
        $('#UPIQR_UForm').submit();
        show_loader();
    }

    showTab(currentTab, method);
    backButtonConfig();
}
function validateDropDown(text,id)
{
    console.log("valiDateData "+text)
    console.log("valiDateData id "+id)

    var valid   = true;
    if (text == "")
    {

        error = "Please Select At Least One Bank";
        valid = false;
        error_msg(error, id);
    }
    console.log("error if"+error);
    return valid;
}

function CASHHideShow(method, displayLabel)
{

    typeLabel = displayLabel;
    method    = method;
    $("#CASHOption").hide();
    $("#" + method).addClass('active');
    document.getElementById('payMethodDisplay').innerText   = typeLabel;
    document.getElementById('payMethod').innerText          = method;
    showTab(currentTab, method);
    backButtonConfig();
}
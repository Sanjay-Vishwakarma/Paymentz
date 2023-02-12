/**
 * Created by Rihen on 2/26/2019.
 */
var options

var PZConstructor = window.PZCheckout = function (opt) {
    console.log(opt);
    options = opt
    //options.description = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
}

var container, backdrop, iframe ,load;

var Container = function () {
    container = document.createElement("div");
    container.className = "checkout-container", container.innerHTML = "<style>.lds-spinner{left: 50%;top: 50%;position: fixed;margin-left: -16px;margin-top: -16px;display:inline-block;width:64px;height:64px}.lds-spinner div{transform-origin:32px 32px;animation:lds-spinner 1.2s linear infinite}.lds-spinner div:after{content:' ';display:block;position:absolute;top:3px;left:29px;width:5px;height:14px;border-radius:20%;background:#fff}.lds-spinner div:nth-child(1){transform:rotate(0);animation-delay:-1.1s}.lds-spinner div:nth-child(2){transform:rotate(30deg);animation-delay:-1s}.lds-spinner div:nth-child(3){transform:rotate(60deg);animation-delay:-.9s}.lds-spinner div:nth-child(4){transform:rotate(90deg);animation-delay:-.8s}.lds-spinner div:nth-child(5){transform:rotate(120deg);animation-delay:-.7s}.lds-spinner div:nth-child(6){transform:rotate(150deg);animation-delay:-.6s}.lds-spinner div:nth-child(7){transform:rotate(180deg);animation-delay:-.5s}.lds-spinner div:nth-child(8){transform:rotate(210deg);animation-delay:-.4s}.lds-spinner div:nth-child(9){transform:rotate(240deg);animation-delay:-.3s}.lds-spinner div:nth-child(10){transform:rotate(270deg);animation-delay:-.2s}.lds-spinner div:nth-child(11){transform:rotate(300deg);animation-delay:-.1s}.lds-spinner div:nth-child(12){transform:rotate(330deg);animation-delay:0s}@keyframes lds-spinner{0%{opacity:1}100%{opacity:0}}</style>";
    container.style = "z-index:9999;position: fixed;top: 0;display:none;left: 0;height:100%;width:100%;-webkit-overflow-scrolling: touch;-webkit-backface-visibility: hidden;overflow-y:visible;transition: 0.3s ease-out;";
    var containerAttributes = {
        style :"z-index:9999;position: fixed;top: 0;display:none;left: 0;height:100%;width:100%;-webkit-overflow-scrolling: touch;-webkit-backface-visibility: hidden;overflow-y:visible;transition: 0.3s ease-out;"
    }
    for (var obj in containerAttributes) {
        for (var prop in containerAttributes[obj]) {
            if (containerAttributes[obj].hasOwnProperty(prop)) {
                container.setAttribute(obj, containerAttributes[obj])
            }
        }
    }
    document.body.appendChild(container);
}

function myFunction() {

    Container();

    container.style.display = 'block';
    backdrop = document.createElement("div");
    var backdropAttributes = {
        style:"min-height: 100%;transition:0.3s ease-out;-webkit-transition:0.3s ease-out;-moz-transition:0.3s ease-out;position:fixed;top: 0;left: 0;width:100%;height:100%;filter:progid:DXImageTransform.Microsoft.gradient(startColorstr=#96000000, endColorstr=#96000000);background: rgba(0, 0, 0, 0.6);"
    }

    for (var obj in backdropAttributes) {
        for (var prop in backdropAttributes[obj]) {
            if (backdropAttributes[obj].hasOwnProperty(prop)) {
                backdrop.setAttribute(obj, backdropAttributes[obj])
            }
        }
    }

    container.appendChild(backdrop)

    load = document.createElement("div");
    load.innerHTML ="<div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div>";
    var loadAttributes = {
        class: "lds-spinner"
    }
    for (var obj in loadAttributes) {
        for (var prop in loadAttributes[obj]) {
            if (loadAttributes[obj].hasOwnProperty(prop)) {
                load.setAttribute(obj, loadAttributes[obj])
            }
        }
    }

    backdrop.appendChild(load);

    // iframe style = left:36%;top:10%;height:550px;width:400px;
    iframe = document.createElement("iframe");
    iframe.className = "embed-responsive-item";
    var iframeAttributes = {
        style: "opacity: 1; height: 100%; position: relative; background: none; display: block; border: 0 none transparent; margin: 0px; padding: 0px;transition: 0.3s ease-out;",
        allowtransparency: !0,
        frameborder: 0,
        width: "100%",
        height: "100%",
        id: "f",
        name: "myFrame"

    }

    for (var obj in iframeAttributes) {
        for (var prop in iframeAttributes[obj]) {
            if (iframeAttributes[obj].hasOwnProperty(prop)) {
                iframe.setAttribute(obj, iframeAttributes[obj])
            }
        }
    }

    backdrop.appendChild(iframe);


    backdrop.addEventListener('click', function () {
        document.body.removeChild(container)
    })

    // timer();

    console.log(iframe)
}

var alphaNumeric_val = /^[a-z0-9]+$/i;
var URLRegex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;

function check_object_values() {
    console.log(options)
    options.amount = parseFloat(Math.round(Number(options.amount) * 100) / 100).toFixed(2);
    options.TMPL_AMOUNT = parseFloat(Math.round(options.TMPL_AMOUNT * 100) / 100).toFixed(2);

    if (isNaN(options.toid)) {
        alert('Toid should be number');
        return false;
    }
    else if (!options.toid || 0 === options.toid.length) {
        alert('Toid should not be empty');
        return false;
    }
    else if (options.toid.toString().length > 10) {
        alert('Max length for toid is 10');
        return false;
    }
    else if (typeof (options.totype) !== 'string') {
        alert('Totype should be of string type');
        return false;
    }
    else if (!options.totype || 0 === options.totype.length) {
        alert('Totype should not be empty');
        return false;
    }
    else if (options.totype.toString().length > 30) {
        alert('Max Length of totype should be less then 30');
        return false;
    }
    else if (alphaNumeric_val.test(options.totype) == false) {
        alert('Please enter alpha numeric values only');
    }
    else if (!options.mkey || 0 === options.mkey.length) {
        alert('mKey should not be empty');
        return false;
    }
    else if (!options.merchantTransactionId || 0 === options.merchantTransactionId.length) {
        alert('Description should not be empty');
        return false;
    }
    else if (options.merchantTransactionId.toString().length >= 100) {
        alert('Description length should be less than or equal to 100');
        return false;
    }
    else if (alphaNumeric_val.test(options.merchantTransactionId) == false) {
        alert('Description should be alpha numeric only, No special characters are allowed');
        return false;
    }
    else if (!(/^[0-9]{1,8}\.[0-9]{2}$/).test(options.amount)) {
        alert('Amount upto 8 digits only');
        return false;
    }
    else if (!(/^[0-9]{1,8}\.[0-9]{2}$/).test(options.TMPL_AMOUNT)) {
        alert('Amount upto 8 digits only');
        return false;
    }
    else if (!options.merchantRedirectUrl || 0 === options.merchantRedirectUrl.length) {
        alert('Redirect url should not be empty');
        return false;
    }
    else if (options.TMPL_CURRENCY.toString().length > 3) {
        alert('Currency length should be less than or equal to 3');
        return false;
    }
    else if(!URLRegex.test(options.hostURL)) {
        alert("Please enter valid URL.");
        return false;
    }
    else {
        console.log("Successful");
        var createForm = document.createElement("form");
        myFunction();

        createForm.setAttribute("action", options.hostURL),
            //createForm.setAttribute("action", "https://<domainname>.com/transaction/Checkout"),
            //createForm.setAttribute("action", "https://<domainname>.com/transaction/Checkout"),
            //createForm.setAttribute("action", "http://localhost:8081/transaction/Checkout"),
                createForm.setAttribute("method", "POST"),
                createForm.setAttribute("target", "myFrame"),
                createForm.innerHTML = objToParams(options),
                document.body.appendChild(createForm),
                createForm.submit();
        document.body.removeChild(createForm);
        console.log(createForm);


        checkFrame()
        return true;
    }

}

function checkFrame() {

    document.getElementById('f').onload = function () {
        //alert("coooolllll");
        load.className+="hide";
        iframe.contentWindow.postMessage({ name: "Message from MyFirstJs to ContentJs", redirecturl: options.merchantRedirectUrl }, "*");
    }
}

var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];

// Now...
// if
//    "attachEvent", then we need to select "onmessage" as the event.
// if
//    "addEventListener", then we need to select "message" as the event

var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

// Listen to message from child IFrame window
eventer(messageEvent, function (e) {
    console.log("Message from ContentJs to MyFirstJs",e.data);
    // Do whatever you want to do with the data got from IFrame in Parent form.

    if(container){
        document.body.removeChild(container);
        options.handler(e.data);
        if (e.data.status) {
            //options.handler(e.data);
            console.log(e.data.status);
        }
        // if(e.data.state == 'close'){
        //     document.body.removeChild(container);
        //     options.handler(e.data);
        // }
        else {
            console.log("WASSUUPPPPP");
        }
    }

}, false);


var objToParams = function (obj) {
    var paramString = '';
    var abc = '';
    for (var key in obj) {
        var value = obj[key];
        if (obj[key] instanceof Array || obj[key] instanceof Object) {
            value = encodeURIComponent(JSON.stringify(value));
        }
        if (paramString != "") paramString += "&";
        paramString += key + "=" + encodeURIComponent(value);

        abc = abc + '<input type="hidden" name="' + key + '" value="' + value + '">'
    }
    //console.log(abc);
    return abc;
}

//  var i =0;
function timer() {
    i++;
    setInterval(function () {
        alert("Hello");
        document.body.removeChild(container);
        console.log(i)
    }, 360000);
}
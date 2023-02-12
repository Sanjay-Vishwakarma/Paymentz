/**
 * Created by Nikhil Poojari on 25-May-18.
 */
var cookiesurl =$('#cookiesurl').val();
$(document).ready(function() {
    $('body').ihavecookies({
        title: '&#x1F36A; Accept Cookies Policy?',
        message: 'By continuing to use this website, or closing this message, you agree to use our cookies in accordance with our '+ "<a target=\"_blank\" href="+cookiesurl+">Cookies Policy</a>" +'.',
        delay: 600,
        expires: 30,
        //link: '',
        /*onAccept: function(){
         alert('Yay! You clicked the accept button. This is an optional callback function that can be set on accept.');
         },*/
        uncheckBoxes: true
    });
});
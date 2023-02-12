function clevertap_init() {
  if(!window.clevertap) {
    var request = new XMLHttpRequest();
    request.open("webfronts/%23/index/GET.html", window.location.origin + "/config.json", false);
    request.send(null);
    var CONFIG_DATA = JSON.parse(request.responseText);

    window.clevertap = {event: [], profile: [], account: [], onUserLogin: [], notifications: []};
    clevertap.account.push({"id": CONFIG_DATA.clevertap_id});
    (function () {
      var wzrk = document.createElement('script');
      wzrk.type = 'text/javascript';
      wzrk.async = true;
      wzrk.src = ('https:' == document.location.protocol ? 'https://d2r1yp2w7bby2u.cloudfront.net' : 'http://static.clevertap.com') + '/js/a.js';
      var s = document.getElementsByTagName('script')[0];
      s.parentNode.insertBefore(wzrk, s);
    })();
  }
}
clevertap_init();

function pushLogout() {
  window.clevertap.event.push('Logout',{
    Source: window.location.href
  });
  clevertap.logout();
  return true;
};

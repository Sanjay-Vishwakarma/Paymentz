function poptuk(message) {
		$('#poptuk').fadeIn();


    $('#poptuk #poptuk_content .poptuk_body').html(message);
}

function poptuk_a(what) {
	if (what == 'close') {
		$('#poptuk').fadeOut();
	}
}


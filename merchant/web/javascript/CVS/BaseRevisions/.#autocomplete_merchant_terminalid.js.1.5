/**
 * Created by Admin on 4/12/2018.
 */
$(function () {

    $("#curr").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/merchant/servlet/GetDetails",
                dataType: "json",
                data: {
                    merchantid: $('#merchantid').val(),
                    ctoken: $('#ctoken').val(),
                    role: $('#role').val(),
                    method:"currencyList",
                    term: request.term



                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0


    });


    $("#tid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/merchant/servlet/GetDetails",
                dataType: "json",
                data: {
                    merchantid: $('#merchantid').val(),
                    ctoken: $('#ctoken').val(),
                    role: $('#role').val(),
                    method:"terminalList",
                    currency: $('#curr').val(),
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));

                }
            } );
        },
        minLength: 0

    });
    $("#accountid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/merchant/servlet/GetDetails",
                dataType: "json",
                data: {
                    merchantid: $('#merchantid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"accountList",
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));

                }
            } );
        },
        minLength: 0

    });

    $("#ibank").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/merchant/servlet/GetDetails",
                dataType: "json",
                data: {
                    merchantid: $('#merchantid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"issuingbankList",
                    term: request.term


                },
                success: function( data ) {
                    //response( data );
                    const ordered = {};
                    Object.keys(data).sort().forEach(function(key) {
                        ordered[key] = data[key];
                    });

                    response($.map(ordered, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 3

    });
});
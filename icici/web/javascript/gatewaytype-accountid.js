/**
 * Created by admin on 6/15/2018.
 */


$(function () {
    $("#gateway1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"gatewayList1",
                    gatetype:"gatetype",
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
        minLength: 0
    });

    $("#accountid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"accountList",
                    accountid:"accountid",
                    gatewayid1: $('#gateway1').val(),
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

});

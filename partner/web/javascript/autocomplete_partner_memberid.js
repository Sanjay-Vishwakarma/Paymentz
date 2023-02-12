/**
 * Created by Naushad on 4/5/2018.
 */
$(function () {

    $("#mid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"memberid",
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

    $("#mid_search").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid_search').val(),
                    ctoken: $('#ctoken').val(),
                    method:"memberid",
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

    //CREATED FOR AUTOCOMPLETE PARTNER FIELD
    $("#pid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"partneridlist",
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

    //CREATED FOR AUTOCOMPLETE PARTNER FIELD
    $("#PID").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"getPartnersList",
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

    //CREATED NEW AUTOCOMPLETE FOR MEMBER LIST.
    $("#member").autocomplete({
        source: function( request, response ){
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();
            /*var partner;
            if(Partnerid_field== ""){
                partner = partnerid_session;
            }else{
                partner = Partnerid_field;
            }*/
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: Partnerid_field,
                    partnersession: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"memberlistnew",
                    term: request.term
                },
                success: function( data ){
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

    $("#member2").autocomplete({
        source: function( request, response ){
            var Partnerid_field = $('#pid2').val();
            var partnerid_session = $('#session_partner').val();
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: Partnerid_field,
                    partnersession: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"memberlistnew",
                    term: request.term
                },
                success: function( data ){
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

    $("#pid2").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#session_partner').val(),
                    ctoken: $('#ctoken').val(),
                    method:"partneridlist",
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


    //CREATED NEW AUTOCOMPLETE FOR MEMBER LIST.
    $("#MID").autocomplete({
        source: function( request, response ){
            var Partnerid_field = $('#PID').val();
            var partnerid_session = $('#partnerid').val();
             $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: Partnerid_field,
                    partnersession: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"memberlistnew",
                    term: request.term
                },
                success: function( data ){
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


    //CREATED NEW FOR AUTOCOMPLETEBOX FOR TERMINAL LIST.
    $("#terminal").autocomplete({
        source: function( request, response ) {
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();
            var partner;
            if(Partnerid_field== ""){
                partner = partnerid_session;
            }else{
                partner = Partnerid_field;
            }
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: partner,
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"Active",
                    memberid: $('#member').val(),
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

    $("#tidAll").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"All",
                    memberid: $('#mid').val(),
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

    //AUTOCOMPLETETEXTBOX FOR TERMINAL ID all new
    $("#terminalALL").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"All",
                    memberid: $('#member').val(),
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

    $("#terminal1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"All",
                    memberid: $('#member').val(),
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

    $("#tid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"Active",
                    memberid: $('#mid').val(),
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

    $("#rrn").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"riskrulename",
                    memberid: $('#mid').val(),
                    terminalid: $('#tid').val(),
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

    $("#rn").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"riskrulename",
                    memberid: $('#mid').val(),
                    terminalid: $('#tidAll').val(),
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

    $("#curr").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    companyname: $('#companyname').val(),
                    ctoken: $('#ctoken').val(),
                    method:"currencyList",
                    memberid: $('#mid').val(),
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


    $("#gateway").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"gatewayList",
                    term: request.term
                },
                success: function( data ) {

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

    $("#bank_name").autocomplete({
        source: function( request, response ) {
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();
            var partner;
            if(Partnerid_field== ""){
                partner = partnerid_session;
            }else{
                partner = Partnerid_field;
            }
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: partner,
                    ctoken: $('#ctoken').val(),
                    method:"gatewayList",
                    term: request.term
                },
                success: function( data ) {

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


    $("#accountid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"accountList",
                    gateway: $('#gateway').val(),
                    memberid: $('#mid').val(),
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

    $("#account_id").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"accountList",
                    gateway: $('#gateway').val(),
                    memberid: $('#member').val(),
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

    $("#acc_id").autocomplete({
        source: function( request, response ) {
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();
            var partner;
            if(Partnerid_field== ""){
                partner = partnerid_session;
            }else{
                partner = Partnerid_field;
            }
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: partner,
                    ctoken: $('#ctoken').val(),
                    method:"accountList_new",
                    gateway: $('#bank_name').val(),
                    memberid: $('#member').val(),
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

    $("#memberid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"memberList",
                    gateway: $('#gateway').val(),
                    accountid: $('#accountid').val(),
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

    $("#terminalid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    isActive:"All",
                    accountid: $('#accountid').val(),
                    memberid: $('#memberid').val(),
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

    $("#member_id").autocomplete({
        source: function( request, response ) {
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();

            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    pid : Partnerid_field,
                    partnerid: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"memberList_new",
                    gateway: $('#bank_name').val(),
                    accountid: $('#acc_id').val(),
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
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnername: $('#partnername').val(),
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

    $("#agnt").autocomplete({

        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentList",
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

    $("#agnt-mid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentMemberList",
                    agentid: $('#agnt').val(),
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
    $("#agntpartner").autocomplete({
        source: function( request, response ){
            var Partnerid_field = $('#pid').val();
            var partnerid_session = $('#partnerid').val();
            console.log(partnerid_session)
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: Partnerid_field,
                    partnersession: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"agentListPartner",
                    term: request.term
                },
                success: function( data ){
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

    $("#agnt").autocomplete({

        source: function( request, response ) {
            var Partnerid_field= $('#pid').val();
            var partnerid_session= $('#partnerid').val();
            $.ajax( {
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: Partnerid_field,
                    partnersession: partnerid_session,
                    ctoken: $('#ctoken').val(),
                    method:"agentList",
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

    $("#paymode1").autocomplete({
        source: function( request, response) {
            $.ajax({
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "paymodeData",
                    term: request.term
                },
                success: function(data){

                    response($.map(data, function(value, key){
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            });
        },
        minLength: 0
    });

    $("#ctype1").autocomplete({
        source: function( request, response) {
            $.ajax({
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "cardtypeData",
                    term: request.term
                },
                success: function(data){

                    response($.map(data, function(value, key){
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            });
        },
        minLength: 0
    });

});

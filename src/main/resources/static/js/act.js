
var form;


function updateTableByData(data) {
    console.log("updateTableByData");
    ctx.datatableApi.clear().rows.add(data).draw();
}

var userAjaxUrl = "/api/users";

var ctx = {
    ajaxUrl: userAjaxUrl,
    updateTable: function () {
        console.log("ctx start");
        $.get(userAjaxUrl, updateTableByData);
        console.log("ctx end");
    }
}

function makeEditable(datatableOpts) {
    console.log("makeEditable start");
    console.log(ctx.datatableApi);
    ctx.datatableApi = $("#datatable").DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN
        $.extend(true, datatableOpts,
            {
                "ajax": {
                    "url": ctx.ajaxUrl,
                    "dataSrc": ""
                },
                "paging": false,
                "info": true
            }
        ));
    console.log(ctx.datatableApi);
    console.log("makeEditable end");

    // form = $('#detailsForm');
    // $(document).ajaxError(function (event, jqXHR, options, jsExc) {
    //     failNoty(jqXHR);
    // });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    // $.ajaxSetup({cache: false});
}

// function renderEditBtn(data, type, row) {
//     console.log("renderEditBtn")
//     if (type === "display") {
//         return "<a onclick='updateRow(" + row.id + ");'><span class='fa fa-pencil'></span></a>";
//     }
// }
//
// function renderDeleteBtn(data, type, row) {
//     console.log("renderDeleteBtn")
//     if (type === "display") {
//         return "<a onclick='deleteRow(" + row.id + ");'><span class='fa fa-remove'></span></a>";
//     }
// }

$(function () {
    console.log("call makeEditable");
    console.log(document.getElementById("datatable"));
    makeEditable({
        "columns": [
            {
                "data": "phoneNumber",
                "render": function (data, type, row) {
                    console.log("1")
                }
            },
            {
                "data": "email",
                "render": function (data, type, row) {
                    console.log("1")
                }
            },
            {
                "data": "firstName",
                "render": function (data, type, row) {
                    console.log("1")
                }
            },
            {
                "data": "lastName",
                "render": function (data, type, row) {
                    console.log("1")
                }
            }
            // ,
            // {
            //     "render": renderEditBtn,
            //     "defaultContent": "",
            //     "orderable": false
            // },
            // {
            //     "render": renderDeleteBtn,
            //     "defaultContent": "",
            //     "orderable": false
            // }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ]
        ,
        "createdRow": function (row, data, dataIndex) {
            if (!data.enabled) {
                $(row).attr("data-userEnabled", false);
            }
        }
    });
});

// console.log('Hi')
// $(document).ready(function () {
//     $.ajax({
//         url: "/api/users/1"
//     }).then(function (data) {
//         console.log(data)
//         $('.phoneNumber1').append(data.phoneNumber);
//         $('.email1').append(data.email);
//         $('.firstName1').append(data.firstName);
//         $('.lastName1').append(data.lastName);
//     });
// });
//
// $(document).ready(function () {
//     $.ajax({
//         url: "/api/users"
//     }).then(function (data) {
//         console.log(data)
//         $('.phoneNumber').append(data._embedded.users[1].phoneNumber);
//         $('.email').append(data._embedded.users[1].email);
//         $('.firstName').append(data._embedded.users[1].firstName);
//         $('.lastName').append(data._embedded.users[1].lastName);
//     });
// });

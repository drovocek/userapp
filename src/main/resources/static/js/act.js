var form;

function updateTableByData(data) {
    console.log("updateTableByData");
    console.log(data);
    console.log(data._embedded.users);
    ctx.datatableApi.clear().rows.add(data._embedded.users).draw();
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
    ctx.datatableApi = $("#datatable").DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN
        $.extend(true, datatableOpts,
            {
                "ajax": {
                    "url": ctx.ajaxUrl,
                    "dataSrc": "_embedded.users"
                },
                "paging": false,
                "info": true
            }
        ));
    console.log(ctx.datatableApi)
    console.log("makeEditable end");

    // form = $('#detailsForm');
    // $(document).ajaxError(function (event, jqXHR, options, jsExc) {
    //     failNoty(jqXHR);
    // });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    // $.ajaxSetup({cache: false});
}

function renderEditBtn(data, type, row) {
    console.log("renderEditBtn")
    if (type === "display") {
        return "<a onclick='updateRow(" + row.id + ");'><span class='fa fa-pencil'></span></a>";
    }
}

function renderDeleteBtn(data, type) {
    console.log("renderDeleteBtn")
    console.log("data " + data)
    console.log(data.toString());
    if (type === "display") {
        return "<a onclick='deleteRow(\"" + data + "\")'><span class='fa fa-remove'></span></a>";
    }
}

// function deleteRow(link) {
//     console.log("!!!!!!!!!!" + link);
//     if (confirm("Are you sure?")) {
//         $.ajax({
//             url: link,
//             type: "DELETE"
//         })
//             .done(function () {
//                 ctx.updateTable();
//                 successNoty("Record deleted");
//             });
//     }
// }

$(function () {
    console.log("call makeEditable");
    console.log(document.getElementById("datatable"));
    makeEditable({
        "columns": [
            {
                "data": "phoneNumber",
            },
            {
                "data": "email",
            },
            {
                "data": "firstName",
            },
            {
                "data": "lastName",
            }
            ,
            {
                "render": renderEditBtn,
                "defaultContent": "",
                "orderable": false
            },
            {
                "data": "_links.self.href",
                "render": renderDeleteBtn,
                "defaultContent": "",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ]
        ,
        "columnDefs": [
            {
                "targets": [0],
                "visible": false,
                "searchable": false
            }
        ]
    });
});

//Noty

var failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + key,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

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

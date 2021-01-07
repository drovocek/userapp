const userAjaxUrl = "/api/users";
var failedNote;

const restApi = {
    createOrUpdate(dataTable) {
        console.log("createOrUpdate start()");
        const dataSet = dataTable.rows('.selected').data()[0];
        const isHasId = (typeof dataSet !== "undefined");

        $.ajax({
            type: isHasId ? "PUT" : "POST",
            url: userAjaxUrl + ((isHasId) ? "/" + dataSet.id : ""),
            data: viewApi.buildRequestBody(),
            contentType: "application/json",
            cache: false,
            success: function (data) {
                console.log("createOrUpdate success: " + data);
                viewApi.addRow(dataTable, data);
                if (isHasId) viewApi.removeRow(dataTable);
                viewApi.clearForm();
                isHasId ? viewApi.successNoty("Record updated") : viewApi.successNoty("Record created");
            },
            error: function (jqXHR) {
                viewApi.failNoty(jqXHR);
            }
        })
    },
    delete(dataTable) {
        console.log("delete()");
        const dataSet = dataTable.rows('.selected').data()[0];

        if (typeof dataSet !== "undefined") {
            if (confirm("Are you sure?")) {
                $.ajax({
                    url: userAjaxUrl + "/" + dataSet.id,
                    type: "DELETE",
                    cache: false,
                    success: function () {
                        viewApi.removeRow(dataTable);
                        viewApi.clearForm();
                        viewApi.successNoty("Record deleted");
                    },
                    error: function (jqXHR) {
                        viewApi.failNoty(jqXHR);
                    }
                })
            }
        }
    }
}

var stompClient = null;
const greetPanel = $("#greetings");

const socketApi = {
    setConnected(connected) {
        console.log("setConnected(connected): " + connected);
        $("#connect").prop("disabled", connected);
        $("#disconnect").prop("disabled", !connected);
        if (connected) {
            $("#conversation").show();
        } else {
            $("#conversation").hide();
        }
        $("#greetings").html("");
    },
    connect() {
        console.log("connect()");
        var socket = new SockJS('/gs-guide-websocket');
        console.log("socket: " + socket);
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            socketApi.setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/greetings', function (greeting) {
                console.log("RESPONSE");
                console.log(greeting.body);
                // showGreeting(JSON.parse(greeting.body).email);
                const greetBody = JSON.parse(greeting.body);
                switch (greetBody.packageType) {
                    case 'GET':
                        socketApi.showGet(greetBody);
                        break;
                    case 'GET_ALL':
                        socketApi.showGetAll(greetBody);
                        break;
                    case 'UPDATE':
                        socketApi.showUpdate(greetBody);
                        break;
                    case 'CREATE':
                        socketApi.showCreate(greetBody);
                        break;
                    case 'DELETE':
                        socketApi.showDelete(greetBody);
                        break;
                    default:
                        alert('NO RESPONSE TYPE');
                }
            });
        });
    },
    disconnect() {
        console.log("disconnect()");

        if (stompClient !== null) {
            stompClient.disconnect();
        }
        socketApi.setConnected(false);
        console.log("Disconnected");
    },
    showUpdate(greetBody) {
        console.log("showUpdate()");
        greetPanel.append("<tr><td>" + greetBody.id + "</td></tr>");
        greetPanel.append("<tr><td>" + greetBody.firstName + "</td></tr>");
        greetPanel.append("<tr><td>" + greetBody.lastName + "</td></tr>");
        greetPanel.append("<tr><td>" + greetBody.phoneNumber + "</td></tr>");
        greetPanel.append("<tr><td>" + greetBody.email + "</td></tr>");
    },
    sendName() {
        console.log("sendName()");

        console.log("stompClient: " + stompClient);
        // stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
        stompClient.send("/app/users/update", {},
            // JSON.stringify({'id': "1", 'firstName':"",'lastName':"",'phoneNumber':"",'email':""}));
            JSON.stringify({
                'id': "1",
                'firstName': "newFirstName",
                'lastName': "newLastName",
                'phoneNumber': "9 (999) 999-99-99",
                'email': "andrey@gmail.com"
            }));
    },
    showGreeting(message) {
        console.log("showGreeting()");

        $("#greetings").append("<tr><td>" + message + "</td></tr>");
    }
}
$(function () {
    console.log("START");
    $("form").on('submit', function (e) {
        console.log("START preventDefault()");
        e.preventDefault();
    });

    $("#connect").click(function () {
        console.log("START connect()");
        socketApi.connect();
    });

    $("#disconnect").click(function () {
        console.log("START disconnect()");
        socketApi.disconnect();
    });

    $("#send").click(function () {
        console.log("START sendName()");
        socketApi.sendName();
    });
});

const viewApi = {
    initTableView() {
        console.log("start()");

        const dataTable = $('#realtime').DataTable({
            ajax: {
                url: userAjaxUrl,
                dataSrc: ""
            }
            ,
            paging: true,
            info: false,
            columns: [
                {
                    title: "First Name",
                    data: "firstName"
                },
                {
                    title: "Last Name",
                    data: "lastName"
                },
                {
                    title: "Phone Number",
                    data: "phoneNumber"
                },
                {
                    title: "Email",
                    data: "email"
                }
            ],
            order: [
                [
                    3,
                    "asc"
                ]
            ],
            responsive: true
        });

        $('#formButton').on("click", restApi.createOrUpdate.bind(this, dataTable));
        $('#delete').on("click", restApi.delete.bind(this, dataTable));
        $('#clear').on("click", this.clearForm);

        const self = this;
        $('#realtime tbody').on("click", "tr", function () {
            self.selectRow.bind(this, dataTable)();
        });

        const sellLayout = '<input class="form-control py-2 border-right-0 border" type="search" placeholder="Search">';

        $("#realtime thead tr").clone(true).appendTo("#realtime thead");
        $("#realtime thead tr:eq(1) th").each(function (i) {

            $(this).html(sellLayout);

            $("input", this).on("keyup change", function () {
                if (dataTable.column(i).search() !== this.value) {
                    dataTable
                        .column(i)
                        .search(this.value)
                        .draw();
                }
            });
        });
    },
    addRow(dataTable, data) {
        console.log("addRow()");
        console.log("data: " + data);
        const addedRow = dataTable.row.add(data).draw();

        const addedRowNode = addedRow.node();
        console.log(addedRowNode);
        $(addedRowNode).addClass("highlight");
    },
    selectRow(dataTable) {
        console.log("selectRow()");

        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            this.clearForm();
        } else {
            dataTable.$("tr.selected").removeClass("selected");
            $(this).addClass("selected");
            viewApi.fillForm(dataTable);
        }
    },
    removeRow(dataTable) {
        console.log("removeRow()");

        dataTable.row(".selected").remove().draw(false);
    },
    clearForm() {
        console.log("clearForm()");
        $(".selected").removeClass("selected");
        $("#detailsForm").find(":input").val("");
        viewApi.drawFormDetails(false);
    },
    fillForm(dataTable) {
        console.log("fillForm()");
        const dataSet = dataTable.rows(".selected").data()[0];

        $('#id').val(dataSet.id);
        console.log(dataSet.id);
        $('#firstName').val(dataSet.firstName);
        $('#lastName').val(dataSet.lastName);
        $('#phoneNumber').val(dataSet.phoneNumber);
        $('#email').val(dataSet.email);

        this.drawFormDetails(true);
    },
    drawFormDetails(isForUpdate) {
        console.log("drawFormDetails()");

        const formBtn = $("#formButton");
        const deleteBtn = $("#delete");
        const formTitle = $("#formTitle");

        formBtn.removeClass((isForUpdate) ? "btn btn-success" : "btn btn-warning");
        formBtn.addClass((isForUpdate) ? "btn btn-warning" : "btn btn-success");
        formBtn.html((isForUpdate) ? "Update" : "Create");
        formTitle.html((isForUpdate) ? "Update User" : "Create User");

        (isForUpdate) ? deleteBtn.show() : deleteBtn.hide();
    },
    buildRequestBody() {
        const formData = JSON.stringify($("#detailsForm").serializeJSON());
        console.log("buildRequestBody: " + formData);
        return formData;
    },
    closeNoty() {
        if (failedNote) {
            failedNote.close();
            failedNote = undefined;
        }
    },
    successNoty(key) {
        console.log("successNoty()");

        this.closeNoty();
        new Noty({
            text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + key,
            type: "success",
            // mint, sunset, relax, nest, metroui, semanticui, light, bootstrap-v3, bootstrap-v4
            theme: "relax",
            layout: "bottomRight",
            timeout: 1000
        }).show();
    },
    failNoty(jqXHR) {
        console.log("failNoty()");
        const serverErrMsg = {
            url: "",
            type: "SERVER_ERROR",
            typeMessage: "Server error",
            details: ["Server disconnect"]
        };
        const errorInfo = (typeof jqXHR.responseText === "undefined") ? serverErrMsg : $.parseJSON(jqXHR.responseText);
        console.log(jqXHR);
        console.log("type: " + errorInfo.type);
        console.log("details: " + errorInfo.details);
        failedNote = new Noty({
            text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.typeMessage + "<br>" + errorInfo.details.join("<br>"),
            type: "error",
            // mint, sunset, relax, nest, metroui, semanticui, light, bootstrap-v3, bootstrap-v4
            theme: "relax",
            layout: "bottomRight",
            timeout: 2000
        }).show();
    }
}


$(document).ready(() => viewApi.initTableView());





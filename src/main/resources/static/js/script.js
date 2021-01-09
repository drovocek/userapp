var stompClient = null;
var dataTable = null;

const socketApi = {
    createOrUpdate() {
        console.log("<< createOrUpdate() >>");//LOG
        const data = $.parseJSON(viewApi.buildRequestBody());

        console.log("data: " + data);//LOG
        console.log("data.id: " + data.id);//LOG
        if (data.id === "") {
            stompClient.send("/app/users/create", {}, viewApi.buildRequestBody());
        } else {
            stompClient.send("/app/users/update/" + data.id, {}, viewApi.buildRequestBody());
        }
    },
    delete() {
        console.log("<< delete() >>");//LOG
        stompClient.send("/app/users/delete/" + $("#id").val(), {}, "");
    },
    connect(callback) {
        console.log("<< connect() >>"); //LOG
        var socket = new SockJS('/websocket');
        console.log("socket: " + socket);//LOG
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);//LOG

            stompClient.subscribe('/topic/users', function (userPackage) {
                const packageBody = JSON.parse(userPackage.body);
                socketApi.doActionByPackageType(packageBody);
            });

            stompClient.subscribe('/user/queue/users', function (userPackage) {
                const packageBody = JSON.parse(userPackage.body);
                socketApi.doActionByPackageType(packageBody);
            });

            stompClient.send("/app/users/getAll", {}, "");

            callback();
        });
    },
    doActionByPackageType(packageBody) {
        switch (packageBody.packageType) {
            case 'GET_ALL':
                console.log('packageType: GET_ALL');//LOG
                viewApi.printTable(packageBody.users);
                break;
            case 'DELETE':
                console.log('packageType: DELETE');//LOG
                viewApi.removeRow(dataTable, packageBody.id);
                break;
            case 'UPDATE':
                console.log('packageType: UPDATE');//LOG
                viewApi.addRow(dataTable, packageBody.users[0]);
                viewApi.removeRow(dataTable, packageBody.id);
                break;
            case 'CREATE':
                console.log('packageType: CREATE');//LOG
                viewApi.addRow(dataTable, packageBody.users[0]);
                break;
            case 'GET':
                console.log('packageType: GET');//LOG
                viewApi.addRow(dataTable, packageBody.users[0]);
                break;
            case 'ERROR':
                console.log('packageType: ERROR');//LOG
                viewApi.failNoty(packageBody.errorMessage);
                break;
            default:
                console.log('packageType: none');//LOG
                alert('NO RESPONSE TYPE');
        }
        viewApi.clearForm();
    }
}

const viewApi = {
    initTableView() {
        console.log("<< initTableView() >>");//LOG

        dataTable = $('#realtime').DataTable({
            paging: true,
            info: false,
            responsive: true,
            columns: [
                {
                    title: "Id",
                    data: "id"
                },
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
            columnDefs: [
                {
                    targets: [0],
                    visible: false,
                    searchable: true
                }
            ]
        });

        $('#formButton').on("click", socketApi.createOrUpdate.bind(this, dataTable));
        $('#delete').on("click", socketApi.delete.bind(this, dataTable));
        $('#clear').on("click", viewApi.clearForm);

        const self = viewApi;
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
        // stompClient.send("/app/users/getAll", {}, "");
    },
    printTable(usersArray) {
        console.log("<< printTable() >>");//LOG
        dataTable.rows.add(usersArray).draw();
    },
    addRow(dataTable, data) {
        console.log("<< addRow() >>");//LOG
        const addedRow = dataTable.row.add(data).draw(false);

        const addedRowNode = addedRow.node();
        console.log(addedRowNode);
        $(addedRowNode).addClass("highlight");
    },
    removeRow(dataTable, id) {
        console.log("<< removeRow() >>");//LOG

        var rowIndexes = [];
        dataTable.rows(function (idx, data, node) {
            if (data.id === parseInt(id)) {
                rowIndexes.push(idx);
            }
            return false;
        });

        dataTable.row(rowIndexes[0]).remove().draw(false);
    },
    selectRow(dataTable) {
        console.log("<< selectRow() >>");//LOG

        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            this.clearForm();
        } else {
            dataTable.$("tr.selected").removeClass("selected");
            $(this).addClass("selected");
            viewApi.fillForm(dataTable);
        }
    },
    clearForm() {
        console.log("<< clearForm() >>");//LOG
        $(".selected").removeClass("selected");
        $("#detailsForm").find(":input").val("");
        viewApi.drawFormDetails(false);
    },
    fillForm(dataTable) {
        console.log("<< fillForm() >>");//LOG
        const dataSet = dataTable.rows(".selected").data()[0];

        $('#id').val(dataSet.id);
        $('#firstName').val(dataSet.firstName);
        $('#lastName').val(dataSet.lastName);
        $('#phoneNumber').val(dataSet.phoneNumber);
        $('#email').val(dataSet.email);

        this.drawFormDetails(true);
    },
    drawFormDetails(isForUpdate) {
        console.log("<< drawFormDetails() >>");//LOG

        const formBtn = $("#formButton");
        const deleteBtn = $("#delete");
        const formTitle = $("#formTitle");

        formBtn.removeClass((isForUpdate) ? "btn btn-success" : "btn btn-warning")
            .addClass((isForUpdate) ? "btn btn-warning" : "btn btn-success")
            .html((isForUpdate) ? "Update" : "Create");

        formTitle.html((isForUpdate) ? "Update User" : "Create User");

        (isForUpdate) ? deleteBtn.show() : deleteBtn.hide();
    },
    buildRequestBody() {
        console.log("<< buildRequestBody() >> ");//LOG
        const formData = JSON.stringify($("#detailsForm").serializeJSON());
        console.log("requestBody: " + formData);//LOG
        return formData;
    },
    closeNoty() {
        console.log("<< closeNoty() >>");//LOG
        if (failedNote) {
            failedNote.close();
            failedNote = undefined;
        }
    },
    successNoty(key) {
        console.log("<< successNoty() >>");//LOG

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
        console.log("<< failNoty() >>");//LOG
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

$(document).ready(() => socketApi.connect(viewApi.initTableView));
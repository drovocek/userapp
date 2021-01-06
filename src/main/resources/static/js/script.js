const userAjaxUrl = "/api/users";
var failedNote;

const app = {
    start() {
        console.log("start()");

        const dataTable = $('#realtime').DataTable({
            // data: dataSet,
            ajax: {
                url: userAjaxUrl,
                dataSrc: '_embedded.users'
            }
            ,
            paging: true,
            info: false,
            columns: [
                {
                    title: 'First Name',
                    data: 'firstName'
                },
                {
                    title: 'Last Name',
                    data: 'lastName'
                },
                {
                    title: 'Phone Number',
                    data: 'phoneNumber'
                },
                {
                    title: 'Email',
                    data: 'email'
                }
            ],
            order: [
                [
                    3,
                    'asc'
                ]
            ],
            responsive: true
        });

        $('#formButton').on('click', this.createOrUpdate.bind(this, dataTable));
        $('#delete').on('click', this.delete.bind(this, dataTable));
        $('#clear').on('click', this.clearForm);

        const self = this;
        $('#realtime tbody').on('click', 'tr', function () {
            self.selectRow.bind(this, dataTable)();
        });

        const sellLayout = '<input class="form-control py-2 border-right-0 border" type="search" placeholder="Search">';


        $('#realtime thead tr').clone(true).appendTo('#realtime thead');
        $('#realtime thead tr:eq(1) th').each(function (i) {

            $(this).html(sellLayout);

            $('input', this).on('keyup change', function () {
                if (dataTable.column(i).search() !== this.value) {
                    dataTable
                        .column(i)
                        .search(this.value)
                        .draw();
                }
            });
        });

        // var table = $('#realtime').DataTable({
        //     orderCellsTop: true,
        //     fixedHeader: true
        // });


        // // Pusher
        // var pusher = new Pusher('App Key', {
        //     cluster: 'CLUSTER',
        //     encrypted: true
        // });
        //
        // var channel = pusher.subscribe('records');
        // channel.bind('new-record', (data) => {
        //     this.addRow(dataTable, data);
        // });
    },
    createOrUpdate(dataTable) {
        console.log("createOrUpdate start()");
        const dataSet = dataTable.rows('.selected').data()[0];
        console.log(dataSet);
        const isEntityHref = (typeof dataSet !== "undefined");
        // const isEntityHref = (typeof dataSet === "undefined") ? false : app.checkHrefContainsAndValid(dataSet);

        $.ajax({
            type: isEntityHref ? "PUT" : "POST",
            url: isEntityHref ? dataSet._links.user.href : userAjaxUrl,
            data: app.buildRequestBody(),
            contentType: "application/json",
            cache: false,
            success: function (data) {
                console.log("createOrUpdate success");
                isEntityHref ? app.rewriteRow(dataTable, data) : app.addRow(dataTable, data);
                app.clearForm();
                isEntityHref ? app.successNoty("Record updated") : app.successNoty("Record created");
            },
            error: function (jqXHR) {
                app.failNoty(jqXHR);
            }
        })
    },
    // checkHrefContainsAndValid(dataSet) {
    //     console.log("checkHrefContainsAndValid");
    //
    //     if (typeof dataSet === "undefined" ||
    //         typeof dataSet._links === "undefined" ||
    //         typeof dataSet._links.user === "undefined" ||
    //         dataSet._links.user.href === "undefined"
    //     ) return false;
    //
    //     const splitData = dataSet._links.user.href.split('/');
    //     const id = splitData[splitData.length - 1];
    //
    //     return (app.getContextPath() + userAjaxUrl + id) === (dataSet._links.user.href);
    // },
    delete(dataTable) {
        console.log("delete()");
        const dataSet = dataTable.rows('.selected').data()[0];

        if (typeof dataSet !== "undefined") {
            const link = dataSet._links.user.href;
            if (confirm("Are you sure?")) {
                $.ajax({
                    url: link,
                    type: "DELETE",
                    cache: false,
                    success: function () {
                        app.removeRow(dataTable);
                        app.clearForm();
                        app.successNoty("Record deleted");
                    },
                    error: function (jqXHR) {
                        app.failNoty(jqXHR);
                    }
                })
            }
        }
    },
    addRow(dataTable, data) {
        console.log("addRow()");

        const addedRow = dataTable.row.add(data).draw();
        // addedRow.show().draw(false);

        const addedRowNode = addedRow.node();
        console.log(addedRowNode);
        $(addedRowNode).addClass('highlight');
    },
    rewriteRow(dataTable, data) {
        console.log("rewriteRow()");

        app.removeRow(dataTable);
        app.addRow(dataTable, data);
    },
    selectRow(dataTable) {
        console.log("selectRow()");

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            app.clearForm();
        } else {
            dataTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            app.fillForm(dataTable);
        }
    },
    removeRow(dataTable) {
        console.log("removeRow()");

        dataTable.row('.selected').remove().draw(false);
    },
    buildRequestBody() {
        const formData = JSON.stringify($('#detailsForm').serializeJSON());
        console.log("buildRequestBody: " + formData);
        return formData;
    },
    clearForm() {
        console.log("clearForm()");
        $('.selected').removeClass('selected');
        $('#detailsForm').find(":input").val("");
        app.drawFormDetails(false);
    },
    fillForm(dataTable) {
        console.log("fillForm()");
        const dataSet = dataTable.rows('.selected').data()[0];

        $('#firstName').val(dataSet.firstName);
        $('#lastName').val(dataSet.lastName);
        $('#phoneNumber').val(dataSet.phoneNumber);
        $('#email').val(dataSet.email);

        app.drawFormDetails(true);
    },
    drawFormDetails(isForUpdate) {
        console.log("drawFormDetails()");

        const formBtn = $('#formButton');
        const deleteBtn = $('#delete');
        const formTitle = $('#formTitle');

        formBtn.removeClass((isForUpdate) ? "btn btn-success" : "btn btn-warning");
        formBtn.addClass((isForUpdate) ? "btn btn-warning" : "btn btn-success");
        formBtn.html((isForUpdate) ? "Update" : "Create");
        formTitle.html((isForUpdate) ? "Update User" : "Create User");

        (isForUpdate) ? deleteBtn.show() : deleteBtn.hide();
    },
    closeNoty() {
        if (failedNote) {
            failedNote.close();
            failedNote = undefined;
        }
    },
    successNoty(key) {
        console.log("successNoty()");

        app.closeNoty();
        new Noty({
            text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + key,
            type: 'success',
            // mint, sunset, relax, nest, metroui, semanticui, light, bootstrap-v3, bootstrap-v4
            theme: 'relax',
            layout: "bottomRight",
            timeout: 1000
        }).show();
    },
    failNoty(jqXHR) {
        console.log("failNoty()");
        const errorInfo = $.parseJSON(jqXHR.responseText);
        console.log(errorInfo);
        console.log(jqXHR);
        console.log("type: " + errorInfo.type);
        console.log("details: " + errorInfo.details);
        failedNote = new Noty({
            text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.typeMessage + "<br>" + errorInfo.details.join("<br>"),
            type: "error",
            // mint, sunset, relax, nest, metroui, semanticui, light, bootstrap-v3, bootstrap-v4
            theme: 'relax',
            layout: "bottomRight",
            timeout: 2000
        }).show();
    },
    getContextPath() {
        return window.location.pathname;
    }
};

$(document).ready(() => app.start());





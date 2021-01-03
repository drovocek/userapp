const userAjaxUrl = "http://localhost:8080/api/users";

const app = {
    start() {
        const dataTable = $('#realtime').DataTable({
            // data: dataSet,
            ajax: {
                url: userAjaxUrl,
                dataSrc: '_embedded.users'
            }
            ,
            paging: true,
            info: true,
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
                // {
                //     title: '',
                //     render: function renderDeleteBtn(data, type) {
                //         if (type === "display") {
                //             console.log("<a class=\"btn btn-danger\" id=\"del\"><span class=\"fa fa-remove\"></span></a>");
                //             return "<a class=\"btn btn-danger\" id=\"del\"><span class=\"fa fa-remove\"></span></a>";
                //         }
                //     },
                //     defaultContent: "",
                //     orderable: false
                // }
            ],
            order: [
                [
                    0,
                    'desc'
                ]
            ]
        });

        $('#save').on('click', this.create.bind(this, dataTable));
        $('#delete').on('click', this.delete.bind(this, dataTable));

        const self = this;
        $('#realtime tbody').on('click', 'tr', function () {
            self.selectRow.bind(this, dataTable)();
        });


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
    create(dataTable) {
        console.log("createNew start");
        $.ajax({
            type: "POST",
            url: userAjaxUrl,
            data: app.buildRequestBody(),
            contentType: "application/json",
            success: function (data) {
                console.log("createNew success");
                app.addRow(dataTable, data);
                app.clearForm();
            }
        });
    },
    delete(dataTable) {
        console.log("delete");
        const dataSet = dataTable.rows('.selected').data()[0];

        if (typeof dataSet !== "undefined") {
            const link = dataSet._links.user.href;
            if (confirm("Are you sure?")) {
                $.ajax({
                    url: link,
                    type: "DELETE",
                    success: function () {
                        app.removeRow(dataTable);
                        // successNoty("Record deleted");
                    }
                });
            }
        }
    },
    addRow(dataTable, data) {
        console.log("addRow")

        const addedRow = dataTable.row.add(data).draw();
        addedRow.show().draw(false);

        const addedRowNode = addedRow.node();
        console.log(addedRowNode);
        $(addedRowNode).addClass('highlight');
    },
    selectRow(dataTable) {
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
        dataTable.row('.selected').remove().draw(false);
    },
    buildRequestBody() {
        const formData = JSON.stringify($('#detailsForm').serializeJSON());
        console.log("buildRequestBody: " + formData);
        return formData;
    },
    clearForm() {
        console.log("clearForm");
        $('#detailsForm').find("input[type=text],input[type=email],textarea").val("");
    },
    fillForm(dataTable) {
        console.log("fillForm");
        const dataSet = dataTable.rows('.selected').data()[0];

        $('#firstName').val(dataSet.firstName);
        $('#lastName').val(dataSet.lastName);
        $('#phoneNumber').val(dataSet.phoneNumber);
        $('#email').val(dataSet.email);
    }
};

$(document).ready(() => app.start());





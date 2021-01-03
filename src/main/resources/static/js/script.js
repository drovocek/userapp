const app = {
    buildForm() {
        return JSON.stringify($('#detailsForm').serializeJSON());
    },
    sendToServer() {
        const formData = this.buildForm();
        console.log(formData);
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/users",
            data: formData,
            contentType: "application/json"
        }).then(response => console.log(response));
    },
    addRow(dataTable, data) {
        const addedRow = dataTable.row.add(data).draw();
        addedRow.show().draw(false);

        const addedRowNode = addedRow.node();
        console.log(addedRowNode);
        $(addedRowNode).addClass('highlight');
    },
    selectRow(dataTable) {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            dataTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    },
    removeRow(dataTable) {
        dataTable.row('.selected').remove().draw(false);
    },
    start() {
        const dataTable = $('#realtime').DataTable({
            // data: dataSet,
            ajax: {
                url: 'http://localhost:8080/api/users',
                dataSrc: '_embedded.users'
            }
            ,
            paging: false,
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

                // ,
                // {
                //     data: "_links.self.href",
                //     render: renderDeleteBtn,
                //     defaultContent: "",
                //     orderable: false
                // }
            ]
            ,
            order: [
                [
                    0,
                    'desc'
                ]
            ]
            // ,
            // columnDefs: [
            //     {
            //         targets: [0],
            //         visible: false,
            //         searchable: false
            //     }
            // ]
        });

        $('#add').on('click', this.sendToServer.bind(this));
        const self = this;
        $('#realtime tbody').on('click', 'tr', function () {
            self.selectRow.bind(this, dataTable)();
        });
        $('#remove').on('click', this.removeRow.bind(this, dataTable));

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
    }
};

$(document).ready(() => app.start());
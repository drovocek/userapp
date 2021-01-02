$(function () {
    console.log("start")
    $.extend($.fn.bootstrapTable.defaults, {
        // method: 'post',
        // contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        pagination: true,
        sidePagination: 'server',
        showRefresh: true,
        search: true
    })
    $.extend($.fn.bootstrapTable.columnDefaults, {
        align: 'center',
        valign: 'middle'
    })

    $('#table').bootstrapTable()
    console.log("end")
})

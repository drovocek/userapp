console.log('Hi')
$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/api/users/1"
    }).then(function(data) {
        console.log(data)
        $('.phoneNumber').append(data.phoneNumber);
        $('.email').append(data.email);
        $('.firstName').append(data.firstName);
        $('.lastName').append(data.lastName);
    });
});
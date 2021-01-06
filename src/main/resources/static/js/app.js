var stompClient = null;

function setConnected(connected) {
    console.log("setConnected(connected): " + connected);
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    console.log("connect()");
    var socket = new SockJS('/gs-guide-websocket');
    console.log("socket: " + socket);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    console.log("disconnect()");

    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    console.log("sendName()");

    console.log("stompClient: " + stompClient);
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    console.log("showGreeting()");

    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    console.log("START");
    $("form").on('submit', function (e) {
        console.log("START preventDefault()");
        e.preventDefault();
    });

    $( "#connect" ).click(function() {
        console.log("START connect()");
        connect(); });

    $( "#disconnect" ).click(function() {
        console.log("START disconnect()");
        disconnect(); });

    $( "#send" ).click(function() {
        console.log("START sendName()");
        sendName(); });
});
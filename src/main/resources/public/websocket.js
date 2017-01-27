//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) {
    handleMessage(msg);
};
webSocket.onclose = function () {
    alert("WebSocket connection closed")
};
webSocket.onopen = login();

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

id("logout").addEventListener("click", function () {
    webSocket.send("logout_");
})

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        sendMessage(e.target.value);
    }
});

id("addchannel").addEventListener("click", function () {
    webSocket.send("addChannel_");
});

id("exitchannel").addEventListener("click", function () {
    webSocket.send("channelExit_");
});

function channelEnter(channel) {
    webSocket.send("channelEnter_" + channel);
}

function sendWait(message) {
    this.waitForConnection(function () {
        webSocket.send(message);
    }, 1000);
}

function waitForConnection(callback, interval) {
    if (webSocket.readyState === 1) {
        callback();
    } else {
        var that = this;
        setTimeout(function () {
            that.waitForConnection(callback, interval);
        }, interval);
    }
}


function login() {
    var username = getCookie("username");
    if (username != "") {
        alert("Logging as " + username + "...");
        sendWait("name_" + username);
    }
    else
        setUsername();
}

function setUsername() {

    var username = prompt("Type your username: ");

    if (username == null) {
        alert("Set username first!");
        setUsername();
        return;
    }


    if (username == "") {
        alert("Set username first!");
        setUsername();
        return;
    }

    setCookie("username", username);

    sendWait("name_" + username);
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send("msg_" + message);
        id("message").value = "";
    }
}

//Update the chat-panel
function handleMessage(msg) {

    var data = JSON.parse(msg.data);
    if (data.reason == "taken") {
        alert("Nazwa jest juz zajeta");
        setUsername();
        return;
    }

    if(data.reason == "login")
        setUsername();

    if (data.reason == "message")
        insert("chat", data.userMessage);

    if(data.reason == "refresh") {
        id("channellist").innerHTML = "";

        data.channellist.forEach(function (channel) {

            var marker = document.createElement('button');
            marker.onclick = function () {
                channelEnter(channel);
            };;
            var t = document.createTextNode(channel);
            marker.appendChild(t);

            var container = id("channellist");
            container.appendChild(marker);
        });
    }

}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(name, value) {
    var now = new Date();
    var time = now.getTime();
    time += 60 * 1000;
    now.setTime(time);
    document.cookie = name + "=" + value + "; expires=" + now.toUTCString();
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
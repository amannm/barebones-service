/**
 * Created by amann.malik on 7/12/2015.
 *
 * encapsulates Websocket communication logic and
 * isolates the native Javascript Websocket library from the rest of the application.
 *
 */

'use strict';

function Endpoint() {

    var socket = null;

    var openHandler = function() {
        console.log('socket opened');
    };
    var closeHandler = function() {
        console.log('socket closed');
    };
    var errorHandler = function() {
        console.log('socket error');
    };
    var messageHandler = function(obj) {
        console.log('socket message: ' + obj);
    };

    this.open = function (url) {
        socket = new WebSocket(url);
        socket.onerror = function (event) {
            errorHandler();
        };
        socket.onclose = function (event) {
            closeHandler();
        };
        socket.onopen = function (event) {
            openHandler();
        };
        socket.onmessage = function (event) {
            if (event.data) {
                var obj = JSON.parse(event.data);
                console.log(obj);
                messageHandler(obj);
            }
        };
    };

    this.close = function () {
        socket.onclose = function () {
        };
        socket.close();
        socket = null;
    };

    this.send = function (obj) {
        if (socket.readyState === 1) {
            console.log(obj);
            var serialized = JSON.stringify(obj);
            socket.send(serialized);
        } else {
            throw "socket not ready";
        }
    };

    this.setOpenHandler = function(handler) {
        openHandler = handler;
    }
    this.setCloseHandler = function(handler) {
        closeHandler = handler;
    }
    this.setErrorHandler = function(handler) {
        errorHandler = handler;
    }
    this.setMessageHandler = function(handler) {
        messageHandler = handler;
    }

}

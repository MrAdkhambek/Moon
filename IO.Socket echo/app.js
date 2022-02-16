const http = require('http')
const app = require('express')()
const server = http.createServer(app)

const { Server } = require("socket.io")
const io = new Server(server)

io.sockets.on('connection', function (socket) {
    console.log("Someone just connected!")

    socket.on('message', function (message) {
        console.log("Got message: " + message)
        socket.emit('message', message)
    })

    setInterval(() => {
        socket.emit('ping', {
            'message': 'pong'
        })
    }, 1000)


    console.log("Someone just connected!")

    // Echo back messages from the client
    socket.on('test', (arg, ack) => {
        console.log("Got message: " + arg)
        ack([
            {
                'message': 'pong'
            },
            {
                'message': 'pong'
            }
        ])
    })
})


server.listen(3000, async () => {
    console.log('listening on *:3000')
})
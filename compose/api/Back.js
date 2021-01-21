const http = require('http');
const express = require('express');
const mongoose = require('mongoose');

const app = express();
app.use(require('cors')());

const PORT = 8080;
const message = `Servidor API está rodando na porta ${PORT} e tem id de processo ${process.pid}.`;

// MongoDB Connection //
mongoose.connect('mongodb://mongo/dust', {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useFindAndModify: false,
    useCreateIndex: true
});

// Routes //
const Permissions = require('./routes/Permissions');
app.use('/api/permissions', Permissions);

// Server //
const server = http.createServer(app);
server.listen(PORT);

console.log(message);
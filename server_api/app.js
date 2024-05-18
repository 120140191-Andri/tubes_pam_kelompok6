const app = require('express')();
const http = require('http').Server(app);
const io = require('socket.io')(http);
const mysql = require('mysql');
const bodyParser = require('body-parser');
const Binance = require('binance-api-node').default;
const router = require("./rt.js");

const axios = require('axios');

app.use(bodyParser({limit: '50mb'}));
app.use(bodyParser.urlencoded({ extended: false, parameterLimit: 1000000 }));
app.use(bodyParser.json());

// app.use(cors());

app.use(router);

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'carb3554_caridosen',
  password: '73bJYDnU@q73d@A',
  database: 'carb3554_caridosen'
});

connection.connect();

http.listen(3000, function() {
   console.log('listening on *:3000');
});

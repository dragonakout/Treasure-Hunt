const express = require('express');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const indexRouter = require('./routes/index');
const tresorsRouter = require('./routes/tresors');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());

app.use('/', indexRouter);
app.use('/tresor', tresorsRouter);

//const generateur = require('./Generateur');
//generateur.demarrer();

module.exports = app;

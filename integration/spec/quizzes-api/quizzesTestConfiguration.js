
var quizzesTestConfiguration = {
    quizzesApiUrl : (process.env['QUIZZES_SERVER_URL'] || 'http://localhost:8080') + '/quizzes/api'
};

module.exports = quizzesTestConfiguration;
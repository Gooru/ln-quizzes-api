
var quizzesTestConfiguration = {
    quizzesApiUrl : (process.env['QUIZZES_SERVER_URL'] || 'http://localhost:8080') + '/quizzes/api',
    nileApiUrl : (process.env['NILE_SERVER_URL'] || 'http://www.gooru.org') + '/api/nucleus-auth',
};

module.exports = quizzesTestConfiguration;
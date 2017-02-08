
var quizzesTestConfiguration = {
    quizzesApiUrl : (process.env['QUIZZES_SERVER_URL'] || 'http://localhost:8080') + '/quizzes/api',
    contentProviderApiUrl : (process.env['QUIZZES_CONTENT_PROVIDER_SERVER_URL'] || 'http://nucleus-qa.gooru.org') + '/api/nucleus-auth',
};

module.exports = quizzesTestConfiguration;
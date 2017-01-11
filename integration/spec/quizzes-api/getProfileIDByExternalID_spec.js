const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');


frisby.create('Get the assignee id in Quizzes')
    .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .inspectJSON()
    .expectJSONTypes({
        "id": String
    })
    .toss();


frisby.create('Get the assignee id in Quizzes')
    .get(QuizzesApiUrl + '/v1/profile-by-external-id/random-nonexisting-id')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(404)
    .inspectJSON()
    .expectJSON({
        "status": 404
    })
    .expectJSONTypes({
        message: String,
        status: Number,
        exception: String
    })
    .toss();
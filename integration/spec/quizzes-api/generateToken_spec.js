const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test Session Creation')
    .post(QuizzesApiUrl + '/v1/session/authorization', {
        "clientApiKey": 	"e1bafe95-dc2c-4c72-9dde-6a0d1bc085fa",
        "clientApiSecret": "GBVYr4rdgcv8Ys@=^?*eRfKgK5j2X@Yj",
        "user": {
            "externalId": "student-session-1",
            "firstName": "SessionFirstName1",
            "lastName": "SessionLastName1",
            "username": "studentSession1",
            "email": "session1@quizzes.com"
        }
    }, {json: true})
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function () {
        frisby.create('Verify that Profile was created in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-session-1')
            .addHeader('client-id', 'gooru')
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .inspectRequest()

            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (json) {
                frisby.create('Verify that Profile has the correct data')
                    .get(QuizzesApiUrl + '/v1/profile/' + json.id)
                    .addHeader('profile-id', json.id)
                    .addHeader('client-id', 'gooru')
                    .inspectRequest()

                    .expectStatus(200)
                    .inspectJSON()
                    .expectJSON({
                        "id": json.id,
                        "externalId": "student-session-1",
                        "firstName": "SessionFirstName1",
                        "lastName": "SessionLastName1",
                        "username": "studentSession1",
                        "email": "session1@quizzes.com"
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

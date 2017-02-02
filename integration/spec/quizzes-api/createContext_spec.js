const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Create Context", function () {
    QuizzesCommon.getAuthorizationToken("teacherqa01", function (authResponse) {
        QuizzesCommon.createContext(function () {
            QuizzesCommon.getProfileByExternalId('student-id-1', function (json) {
                frisby.create('Verify that Assignee Profile has the correct data')
                    .get(QuizzesApiUrl + '/v1/profile/' + json.id)
                    .addHeader('Authorization', 'Token ' + authResponse.access_data)
                    .inspectRequest()

                    .expectStatus(200)
                    .inspectJSON()
                    .expectJSON({
                        'id': json.id,
                        'externalId': 'student-id-1',
                        'firstName': 'StudentFirstName1',
                        'lastName': 'StudentLastName1',
                        'username': 'student1',
                        'email': 'student1@quizzes.com'
                    })
                    .toss();
            });
        });
    });
});

QuizzesCommon.getAuthorizationToken("teacherqa01", function (authResponse) {
    frisby.create('Test wrong collection ID')
        .post(QuizzesApiUrl + '/v1/contexts', {
            'collectionId': 'wrong-assessment-id',
            'contextData': {
                'contextMap': {
                    'classId': 'class-id-1'
                },
                'metadata': {}
            }
        }, {json: true})
        .addHeader('Authorization', 'Token ' + authResponse.access_token)
        .inspectRequest()
        .expectStatus(500)
        .expectHeaderContains('content-type', 'application/json')
        .inspectJSON()
        .toss()
});

//TODO: once we remove the conditional in AuthorizationTokenInterceptor class to ALWAYS check for a token
//TODO: this error will be a controlled 401 instead of 500
frisby.create('Test context without owner (no authorization token)')
    .post(QuizzesApiUrl + '/v1/contexts', {
        'collectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'contextData': {
            'contextMap': {
                'classId': 'class-id-1'
            },
            'metadata': {}
        }
    }, {json: true})
    .inspectRequest()
    .expectStatus(500)
//    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
//    .expectJSON({"exception":"Missing request attribute \u0027profileId\u0027 of type String"})
    .toss();

QuizzesCommon.getAuthorizationToken("teacherqa01", function (authResponse) {
    frisby.create('Test context without contextData')
        .post(QuizzesApiUrl + '/v1/contexts', {
            'collectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        }, {json: true})
        .addHeader('Authorization', 'Token ' + authResponse.access_token)
        .inspectRequest()
        .expectStatus(406)
        .expectHeaderContains('content-type', 'application/json')
        .inspectJSON()
        .expectJSON({
            "Errors": [
                "Error in contextData: A ContextData is required"
            ]
        })
        .toss()
})

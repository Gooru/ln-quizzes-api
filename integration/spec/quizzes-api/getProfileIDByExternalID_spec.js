const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test Context creation for one assignee and owner')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
        'owner': {
            'id': 'random-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
        },
        'assignees': [
            {
                'id': 'student-id-1',
                'firstName': 'StudentFirstName1',
                'lastName': 'StudentLastName1',
                'username': 'student1',
                'email': 'student1@quizzes.com'
            }
        ],
        'contextData': {
            'contextMap': {
                'classId': 'class-id-1'
            },
            'metadata': {}
        }
    }, {json: true})
    // This profile-id header for this specific endpoint is fake, it is useless but it is required by the endpoint
    // Both headers will be replaced by the session-token header once we finish with the Session implementation
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function() {
        frisby.create('Verify that Assignee was created in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/random-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .expectJSONTypes({
                "id": String
            })
            .toss();
    })
    .toss();


frisby.create('Get the assignee id in Quizzes with a random nonexisting id')
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
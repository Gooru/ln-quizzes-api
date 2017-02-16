const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test Context creation for one assignee and owner')
    .post(QuizzesApiUrl + '/v1/contexts', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
        'owner': {
            'id': 'teacher-id-1',
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
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function(json) {
                frisby.create('Verify that Assignee Profile has the correct data')
                    .get(QuizzesApiUrl + '/v1/profile/' + json.id)
                    .addHeader('profile-id', json.id)
                    .addHeader('client-id', 'quizzes')
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
            })
            .toss();
    })
    .toss();

frisby.create('Get the profile id in Quizzes on a non existent ID')
    .get(QuizzesApiUrl + '/v1/profile/0e4b4050-813a-41be-aef0-41fda6a41765')
    .addHeader('client-id', 'quizzes')
    .addHeader('profile-id', '6b75be01-960e-4ead-8c44-36243d11e33d')
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


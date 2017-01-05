const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');


frisby.create('Test Context creation to test assignee data')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
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
        },
        'owner': {
            'id': 'teacher-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
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
                    .afterJSON(function(json) {
                        frisby.create('Verify Assignee Profile with fields')
                            .get(QuizzesApiUrl + '/v1/profile/' + json.id + "?fields=email,externalId")
                            .addHeader('profile-id', json.id)
                            .addHeader('client-id', 'quizzes')
                            .inspectRequest()

                            .expectStatus(200)
                            .inspectJSON()
                            .expectJSONLength(2)
                            .expectJSON({
                                'email': 'student1@quizzes.com',
                                'externalId': 'student-id-1'
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();
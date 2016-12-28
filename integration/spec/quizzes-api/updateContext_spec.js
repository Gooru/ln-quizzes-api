const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Gets the owner profileId')
    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
    .addHeader('lms-id', 'quizzes')
    .inspectJSON()
    .afterJSON(function(profileJson) {
        frisby.create('Test Context update for one assignee')
            .post(QuizzesApiUrl + '/v1/context', {
                'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
                'assignees': [
                    {
                        'id': 'student-id-1',
                        'firstName': 'StudentFirstName1',
                        'lastName': 'StudentLastName1',
                        'username': 'student1',
                        'email': 'student1@quizzes.com'
                    },
                    {
                        'id': 'student-id-2',
                        'firstName': 'StudentFirstName2',
                        'lastName': 'StudentLastName2',
                        'username': 'student2',
                        'email': 'student2@quizzes.com'
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
            .addHeader('profile-id', profileJson.id)
            .addHeader('lms-id', 'quizzes')
            .inspectRequest()

            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function(contextJson) {
                frisby.create('Verify that the context exists as expected')
                    .get(QuizzesApiUrl + '/v1/context/created/' + contextJson.id)
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', profileJson.id)
                    .inspectRequest()

                    .expectStatus(200)
                    .inspectJSON()
                    .expectJSON({
                        "assignees": function(val) {expect(val.length).toBe(2); var assignees1 = val;},
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-1'
                            },
                            'metadata': {}
                        },
                        "id": contextJson.id
                    })
                    .toss();
            })
            .toss();
    })
    .toss();
const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context creation for one assignee and owner for start context ')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
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
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function (context) {
        frisby.create('Verify that Assignee was created in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()

            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                frisby.create('Verify that Assignee Profile has the correct data')
                    .get(QuizzesApiUrl + '/v1/profile/' + profile.id)
                    .addHeader('profile-id', profile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()

                    .expectStatus(200)
                    .inspectJSON()
                    .expectJSON({
                        'id': profile.id,
                        'externalId': 'student-id-1',
                        'firstName': 'StudentFirstName1',
                        'lastName': 'StudentLastName1',
                        'username': 'student1',
                        'email': 'student1@quizzes.com'
                    })
                    .afterJSON(function () {
                        frisby.create('Get context assigned information')
                            .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
                            .addHeader('profile-id', profile.id)
                            .addHeader('client-id', 'quizzes')
                            .inspectRequest()

                            .expectStatus(200)
                            .inspectJSON()
                            .afterJSON(function (contextAssigned) {
                                frisby.create('Get the collection information')
                                    .get(QuizzesApiUrl + '/v1/collection/' + contextAssigned.collection.id)
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()

                                    .expectStatus(200)
                                    .inspectJSON()
                                    .afterJSON(function (collection) {
                                        frisby.create('Start Context and verify the data')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('client-id', 'quizzes')
                                            .inspectRequest()

                                            .expectStatus(200)
                                            .inspectJSON()
                                            .expectJSON({
                                                'id': context.id,
                                                'collection': {
                                                    'id': collection.id
                                                },
                                                'currentResourceId': collection.resources[0].id,
                                                'events': []
                                            })
                                            .afterJSON(function (startResponse) {
                                                frisby.create('Answer the first and current question')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id +'/on-resource/' + startResponse.currentResourceId , {
                                                        "previousResource": {
                                                            "answer": [
                                                                {
                                                                    "value": "Any answer"
                                                                }
                                                            ],
                                                            "reaction": 0,
                                                            "resourceId": collection.resources[1].id,
                                                            "timeSpent": 4525
                                                        }
                                                    })
                                                    .addHeader('profile-id', profile.id)
                                                    .addHeader('lms-id', 'quizzes')
                                                    .inspectRequest()

                                                    .expectStatus(200)
                                                    .inspectJSON()
                                                    /*.expectJSON({
                                                        'id': context.id,
                                                        'collection': {
                                                            'id': collection.id
                                                        },
                                                        'hasStarted': true
                                                    })*/
                                                    .toss();
                                            })
                                            .toss();
                                    })
                                    .toss();
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();
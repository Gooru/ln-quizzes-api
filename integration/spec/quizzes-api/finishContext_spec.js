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
        frisby.create('Get the assignee id in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                frisby.create('Get assigned context information')
                    .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
                    .addHeader('profile-id', profile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function (contextCreated) {
                        frisby.create('Get the collection information')
                            .get(QuizzesApiUrl + '/v1/collection/' + contextCreated.collection.id)
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
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                                "previousResource": {
                                                    "answer": [
                                                        {
                                                            "value": "4"
                                                        }
                                                    ],
                                                    "reaction": 3,
                                                    "resourceId": startResponse.currentResourceId,
                                                    "timeSpent": 4525
                                                }
                                            }, {json: true})
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function() {
                                                frisby.create('Finish Context and verify the status')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/finish')
                                                    .addHeader('profile-id', profile.id)
                                                    .addHeader('client-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(204)
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
        frisby.create('Get the assignee id in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                frisby.create('Finish an unstarted, unanswered Context and verify the status')
                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/finish')
                    .addHeader('profile-id', profile.id)
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
            })
            .toss();
    })
    .toss();

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
        frisby.create('Get the assignee id in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                frisby.create('Get assigned context information')
                    .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
                    .addHeader('profile-id', profile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function (contextCreated) {
                        frisby.create('Get the collection information')
                            .get(QuizzesApiUrl + '/v1/collection/' + contextCreated.collection.id)
                            .addHeader('profile-id', profile.id)
                            .addHeader('client-id', 'quizzes')
                            .inspectRequest()
                            .expectStatus(200)
                            .inspectJSON()
                            .afterJSON(function (collection) {
                                frisby.create('Start Context')
                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                                "previousResource": {
                                                    "answer": [
                                                        {
                                                            "value": "4"
                                                        }
                                                    ],
                                                    "reaction": 3,
                                                    "resourceId": startResponse.currentResourceId,
                                                    "timeSpent": 4525
                                                }
                                            }, {json: true})
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function() {
                                                frisby.create('Finish Context using an incorrect profileID that is NOT an assignee and verify the status')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/finish')
                                                    .addHeader('profile-id', '38ecc42b-827f-4822-8061-350ac1ca9187')
                                                    .addHeader('client-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectJSON({
                                                        "status": 404
                                                    })
                                                    .expectJSONTypes({
                                                        message: String,
                                                        status: Number,
                                                        exception: String
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
    })
    .toss();
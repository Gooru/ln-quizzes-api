const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context creation for one assignee and owner for answering a started context using onResource endpoint.')
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
                                frisby.create('Start Context to be able to answer')
                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/'
                                                + context.id + '/event/on-resource/' + collection.resources[1].id , {
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


frisby.create('Test context creation for one assignee and owner for answering a non-started context using onResource endpoint.')
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
                                frisby.create('Answer the first and current question without starting the context')
                                    .post(QuizzesApiUrl + '/v1/context/'
                                        + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                        "previousResource": {
                                            "answer": [
                                                {
                                                    "value": "4"
                                                }
                                            ],
                                            "reaction": 3,
                                            "resourceId": collection.resources[0].id,
                                            "timeSpent": 4525
                                        }
                                    }, {json: true})
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('lms-id', 'quizzes')
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
            })
            .toss();
    })
    .toss();


frisby.create('Test context creation for one assignee and owner for answering a started context using onResource endpoint and try to answer as owner')
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
                                frisby.create('Start Context to be able to answer')
                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Get the owner id in Quizzes')
                                            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                                            .addHeader('client-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(200)
                                            .inspectJSON()
                                            .afterJSON(function (ownerProfile) {
                                                frisby.create('Answer the first and current question as the owner who is not an assignee')
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
                                                    .addHeader('profile-id', ownerProfile.id)
                                                    .addHeader('lms-id', 'quizzes')
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
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

frisby.create('Test context creation for one assignee and owner for answering a started context using onResource endpoint with a wrong contextId or ResourceId')
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
                                frisby.create('Start Context to be able to answer')
                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Answer the first and current question with a wrong contextID')
                                            .post(QuizzesApiUrl + '/v1/context/'
                                                + collection.resources[0].id + '/event/on-resource/' + collection.resources[1].id , {
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
                                        frisby.create('Answer the first and current question with a wrong resourceID')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id
                                                + '/event/on-resource/74b62b82-6e77-48b8-b5bc-aac34148b77a'  , {
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
                    })
                    .toss();
            })
            .toss();
    })
    .toss();
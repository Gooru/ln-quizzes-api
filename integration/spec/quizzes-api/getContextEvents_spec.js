const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
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
    .afterJSON(function (contextCreated) {
        frisby.create('Get the assignee id in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (assigneeProfile) {
                frisby.create('Get assigned context information')
                    .get(QuizzesApiUrl + '/v1/contexts/' + contextCreated.id + '/assigned')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function (contextAssigned) {
                        frisby.create('Get the collection information')
                            .get(QuizzesApiUrl + '/v1/collection/' + contextAssigned.collection.id)
                            .addHeader('profile-id', assigneeProfile.id)
                            .addHeader('client-id', 'quizzes')
                            .inspectRequest()
                            .expectStatus(200)
                            .inspectJSON()
                            .afterJSON(function (collection) {
                                frisby.create('Start Context and verify the data')
                                    .post(QuizzesApiUrl + '/v1/contexts/' + contextAssigned.id + '/start')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .inspectJSON()
                                    .afterJSON(function (startResponse) {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/contexts/' + contextAssigned.id
                                                + '/onResource/' + collection.resources[1].id, {
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
                                            .addHeader('profile-id', assigneeProfile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function () {
                                                frisby.create('Get the owner id in Quizzes')
                                                    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                                                    .addHeader('client-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(200)
                                                    .inspectJSON()
                                                    .afterJSON(function (ownerProfile) {
                                                        frisby.create('Get the context Events as an owner')
                                                            .get(QuizzesApiUrl + '/v1/context/'
                                                                + contextAssigned.id + '/events')
                                                            .addHeader('profile-id', ownerProfile.id)
                                                            .addHeader('lms-id', 'quizzes')
                                                            .inspectRequest()
                                                            .expectStatus(200)
                                                            .inspectJSON()
                                                            .expectJSON({
                                                                "collection": {
                                                                    "id": collection.id
                                                                },
                                                                "contextId": contextAssigned.id,
                                                                "profileEvents": [
                                                                    {
                                                                        "currentResourceId": collection.resources[1].id,
                                                                        "events": [
                                                                            {
                                                                                "answer": [
                                                                                    {
                                                                                        "value": "4"
                                                                                    }
                                                                                ],
                                                                                "isSkipped": false,
                                                                                "reaction": 3,
                                                                                "resourceId": collection.resources[0].id,
                                                                                "timeSpent": 4525
                                                                            }
                                                                        ],
                                                                        "profileId": assigneeProfile.id,
                                                                        "contextProfileSummary": {
                                                                            "totalTimeSpent": 4525,
                                                                            "averageReaction": 3,
                                                                            "averageScore": 0,
                                                                            "totalCorrect": 0,
                                                                            "totalAnswered": 1
                                                                        }
                                                                    }
                                                                ]
                                                            })
                                                            .toss();
                                                        frisby.create('Get the context Events as an assignee')
                                                            .get(QuizzesApiUrl + '/v1/context/' + contextAssigned.id + '/events')
                                                            .addHeader('profile-id', assigneeProfile.id)
                                                            .addHeader('lms-id', 'quizzes')
                                                            .inspectRequest()
                                                            .expectStatus(403)
                                                            .inspectJSON()
                                                            .expectJSON({
                                                                "status": 403
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
    })
    .toss();

frisby.create('Test context events for one assignee and owner for a not started context ')
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
        frisby.create('Get the owner id in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                frisby.create('Get assigned context information')
                    .get(QuizzesApiUrl + '/v1/contexts/' + context.id + "/created")
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
                                frisby.create('Get the context Events as an owner without the context having started')
                                    .get(QuizzesApiUrl + '/v1/context/' + context.id + '/events')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('lms-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .inspectJSON()
                                    .expectJSON({
                                        "contextId": context.id,
                                        "collection": {
                                            "id": collection.id
                                        },
                                        "profileEvents": []
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

frisby.create('Test context events for one assignee and owner for started context without events ')
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
                    .get(QuizzesApiUrl + '/v1/contexts/' + context.id + '/assigned')
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
                                    .post(QuizzesApiUrl + '/v1/contexts/' + context.id + '/start')
                                    .addHeader('profile-id', profile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .inspectJSON()
                                    .afterJSON(function () {
                                        frisby.create('Get the owner id in Quizzes')
                                            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                                            .addHeader('client-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(200)
                                            .inspectJSON()
                                            .afterJSON(function (ownerProfile) {
                                                frisby.create('Get the context Events as an unkown profile')
                                                    .get(QuizzesApiUrl + '/v1/context/' + context.id + '/events')
                                                    .addHeader('profile-id', ownerProfile.id)
                                                    .addHeader('lms-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(200)
                                                    .expectJSON({
                                                        "collection": {
                                                            "id": collection.id
                                                        },
                                                        "contextId": context.id,
                                                        "profileEvents": [
                                                            {
                                                                "currentResourceId": collection.resources[0].id,
                                                                "events": [],
                                                                "profileId": profile.id
                                                            }
                                                        ]
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

QuizzesCommon.startTest("Support Multiple Attempts", function() {
    QuizzesCommon.createContext(function (contextCreated) {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (assigneeProfile) {
            QuizzesCommon.getAssignedContextByContextId(contextCreated.id, assigneeProfile.id, function (contextAssigned) {
                QuizzesCommon.getCollectionById(contextAssigned.collection.id, assigneeProfile.id, function (collection) {
                    QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function (startResponse) {
                        QuizzesCommon.onResourceEvent(contextAssigned.id, assigneeProfile.id, collection.resources[1].id,
                            {
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
                            }, function () {
                                QuizzesCommon.getProfileByExternalId('teacher-id-1', function (ownerProfile) {
                                    QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                        {
                                            "collection": {
                                                "id": collection.id
                                            },
                                            "contextId": contextAssigned.id,
                                            "profileEvents": [
                                                {
                                                    "currentResourceId": collection.resources[1].id,
                                                    "events": [
                                                        {
                                                            "answer": [
                                                                {
                                                                    "value": "4"
                                                                }
                                                            ],
                                                            "isSkipped": false,
                                                            "reaction": 3,
                                                            "resourceId": collection.resources[0].id,
                                                            "timeSpent": 4525
                                                        }
                                                    ],
                                                    "profileId": assigneeProfile.id,
                                                    "contextProfileSummary": {
                                                        "totalTimeSpent": 4525,
                                                        "averageReaction": 3,
                                                        "averageScore": 0,
                                                        "totalCorrect": 0,
                                                        "totalAnswered": 1
                                                    }
                                                }
                                            ]
                                        }, function () {
                                            QuizzesCommon.finishContext(contextAssigned.id, assigneeProfile.id, function () {
                                                QuizzesCommon.startContext(contextAssigned.id, assigneeProfile.id, function () {
                                                    QuizzesCommon.verifyGetContextEvents(contextAssigned.id, ownerProfile.id,
                                                        {
                                                            "collection": {
                                                                "id": collection.id
                                                            },
                                                            "contextId": contextAssigned.id,
                                                            "profileEvents": [
                                                                {
                                                                    "currentResourceId": collection.resources[0].id,
                                                                    "events": [],
                                                                    "isComplete": false,
                                                                    "profileId": assigneeProfile.id,
                                                                    "contextProfileSummary": {
                                                                        "totalTimeSpent": 0,
                                                                        "averageReaction": 0,
                                                                        "averageScore": 0,
                                                                        "totalCorrect": 0,
                                                                        "totalAnswered": 0
                                                                    }
                                                                }
                                                            ]
                                                        }, function () {
                                                        });
                                                });
                                            });
                                        });
                                });
                            });
                    });
                });
            });
        });
    });
});


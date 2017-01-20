const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context summary for 10 correctly answered questions ')
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
                                    .afterJSON(function () {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                                "previousResource": {
                                                    "answer": [ { value: 'D' } ],
                                                    "reaction": 3,
                                                    "resourceId": collection.resources[0].id,
                                                    "timeSpent": 4525
                                                }
                                            }, {json: true})
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function() {
                                                frisby.create('Answer the second and current question')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[2].id, {
                                                        "previousResource": {
                                                            "answer": [ { value: 'B' } ],
                                                            "reaction": 3,
                                                            "resourceId": collection.resources[1].id,
                                                            "timeSpent": 4525
                                                        }
                                                    }, {json: true})
                                                    .addHeader('profile-id', profile.id)
                                                    .addHeader('lms-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(204)
                                                    .after(function () {
                                                        frisby.create('Answer the third and current question')
                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[3].id, {
                                                                "previousResource": {
                                                                    "answer": [ { value: 'Smaller amounts of water evaporate in the cool morning.' } ],
                                                                    "reaction": 3,
                                                                    "resourceId": collection.resources[2].id,
                                                                    "timeSpent": 4525
                                                                }
                                                            }, {json: true})
                                                            .addHeader('profile-id', profile.id)
                                                            .addHeader('lms-id', 'quizzes')
                                                            .inspectRequest()
                                                            .expectStatus(204)
                                                            .after(function () {
                                                                frisby.create('Answer the fourth question')
                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[3].id, {
                                                                        "previousResource": {
                                                                            "answer": [ { value: 'The water in the sponge went into the air.' } ],
                                                                            "reaction": 3,
                                                                            "resourceId": collection.resources[3].id,
                                                                            "timeSpent": 4525
                                                                        }
                                                                    }, {json: true})
                                                                    .addHeader('profile-id', profile.id)
                                                                    .addHeader('lms-id', 'quizzes')
                                                                    .inspectRequest()
                                                                    .expectStatus(204)
                                                                    .after(function () {
                                                                        frisby.create('Answer the fifth question')
                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[4].id, {
                                                                                "previousResource": {
                                                                                    "answer": [ { value: 'They are formed from water vapor in the air.' } ],
                                                                                    "reaction": 3,
                                                                                    "resourceId": collection.resources[4].id,
                                                                                    "timeSpent": 4525
                                                                                }
                                                                            }, {json: true})
                                                                            .addHeader('profile-id', profile.id)
                                                                            .addHeader('lms-id', 'quizzes')
                                                                            .inspectRequest()
                                                                            .expectStatus(204)
                                                                            .after(function () {
                                                                                frisby.create('Answer the sixth question')
                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[5].id, {
                                                                                        "previousResource": {
                                                                                            "answer": [ { value: 'more water vapor will be in the atmosphere.' } ],
                                                                                            "reaction": 3,
                                                                                            "resourceId": collection.resources[5].id,
                                                                                            "timeSpent": 4525
                                                                                        }
                                                                                    }, {json: true})
                                                                                    .addHeader('profile-id', profile.id)
                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                    .inspectRequest()
                                                                                    .expectStatus(204)
                                                                                    .after(function () {
                                                                                        frisby.create('Answer the seventh question')
                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[6].id, {
                                                                                                "previousResource": {
                                                                                                    "answer": [ { value: 'snow' } ],
                                                                                                    "reaction": 3,
                                                                                                    "resourceId": collection.resources[6].id,
                                                                                                    "timeSpent": 4525
                                                                                                }
                                                                                            }, {json: true})
                                                                                            .addHeader('profile-id', profile.id)
                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                            .inspectRequest()
                                                                                            .expectStatus(204)
                                                                                            .after(function () {
                                                                                                frisby.create('Answer the eight question')
                                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[7].id, {
                                                                                                        "previousResource": {
                                                                                                            "answer": [ { value: 'Dew forming on plants during a cold night' } ],
                                                                                                            "reaction": 3,
                                                                                                            "resourceId": collection.resources[7].id,
                                                                                                            "timeSpent": 4525
                                                                                                        }
                                                                                                    }, {json: true})
                                                                                                    .addHeader('profile-id', profile.id)
                                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                                    .inspectRequest()
                                                                                                    .expectStatus(204)
                                                                                                    .after(function () {
                                                                                                        frisby.create('Answer the ninth question')
                                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[8].id, {
                                                                                                                "previousResource": {
                                                                                                                    "answer":[ { value: 'The sun heating the lake' } ],
                                                                                                                    "reaction": 3,
                                                                                                                    "resourceId": collection.resources[8].id,
                                                                                                                    "timeSpent": 4525
                                                                                                                }
                                                                                                            }, {json: true})
                                                                                                            .addHeader('profile-id', profile.id)
                                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                                            .inspectRequest()
                                                                                                            .expectStatus(204)
                                                                                                            .after(function () {
                                                                                                                frisby.create('Answer the tenth question')
                                                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[9].id, {
                                                                                                                        "previousResource": {
                                                                                                                            "answer": [ { value: 'water vapor cooling down to become a liquid' } ],
                                                                                                                            "reaction": 3,
                                                                                                                            "resourceId": collection.resources[9].id,
                                                                                                                            "timeSpent": 4525
                                                                                                                        }
                                                                                                                    }, {json: true})
                                                                                                                    .addHeader('profile-id', profile.id)
                                                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                                                    .inspectRequest()
                                                                                                                    .expectStatus(204)
                                                                                                                    .after(function () {
                                                                                                                        frisby.create('Finish Context and verify the status')
                                                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/finish')
                                                                                                                            .addHeader('profile-id', profile.id)
                                                                                                                            .addHeader('client-id', 'quizzes')
                                                                                                                            .inspectRequest()
                                                                                                                            .expectStatus(204)
                                                                                                                            .after(function() {
                                                                                                                                frisby.create('Get the owner id in Quizzes')
                                                                                                                                    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                                                                                                                                    .addHeader('client-id', 'quizzes')
                                                                                                                                    .inspectRequest()
                                                                                                                                    .expectStatus(200)
                                                                                                                                    .inspectJSON()
                                                                                                                                    .afterJSON(function (ownerProfile) {
                                                                                                                                        frisby.create('Get the context Events as an owner')
                                                                                                                                            .get(QuizzesApiUrl + '/v1/context/'
                                                                                                                                                + context.id + '/events')
                                                                                                                                            .addHeader('profile-id', ownerProfile.id)
                                                                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                                                                            .inspectRequest()
                                                                                                                                            .expectStatus(200)
                                                                                                                                            .inspectJSON()
                                                                                                                                            .expectJSON({
                                                                                                                                                "collection": {
                                                                                                                                                    "id": collection.id
                                                                                                                                                },
                                                                                                                                                "contextId": context.id,
                                                                                                                                                "profileEvents": [
                                                                                                                                                    {
                                                                                                                                                        "currentResourceId": collection.resources[9].id,
                                                                                                                                                        "profileId": profile.id,
                                                                                                                                                        "contextProfileSummary": {
                                                                                                                                                            "totalTimeSpent": 45250,
                                                                                                                                                            "averageReaction": 3,
                                                                                                                                                            "averageScore": 100,
                                                                                                                                                            "totalCorrect": 10,
                                                                                                                                                            "totalAnswered": 10
                                                                                                                                                        }
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
                    })
                    .toss();
            })
            .toss();
    })
    .toss();


frisby.create('Test context summary for 10 incorrectly answered questions ')
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
                                    .afterJSON(function () {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                                "previousResource": {
                                                    "answer": [ { value: 'A' } ],
                                                    "reaction": 3,
                                                    "resourceId": collection.resources[0].id,
                                                    "timeSpent": 4525
                                                }
                                            }, {json: true})
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function() {
                                                frisby.create('Answer the second and current question')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[2].id, {
                                                        "previousResource": {
                                                            "answer": [ { value: 'D' } ],
                                                            "reaction": 3,
                                                            "resourceId": collection.resources[1].id,
                                                            "timeSpent": 4525
                                                        }
                                                    }, {json: true})
                                                    .addHeader('profile-id', profile.id)
                                                    .addHeader('lms-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(204)
                                                    .after(function () {
                                                        frisby.create('Answer the third and current question')
                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[3].id, {
                                                                "previousResource": {
                                                                    "answer": [ { value: 'D' } ],
                                                                    "reaction": 3,
                                                                    "resourceId": collection.resources[2].id,
                                                                    "timeSpent": 4525
                                                                }
                                                            }, {json: true})
                                                            .addHeader('profile-id', profile.id)
                                                            .addHeader('lms-id', 'quizzes')
                                                            .inspectRequest()
                                                            .expectStatus(204)
                                                            .after(function () {
                                                                frisby.create('Answer the fourth question')
                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[3].id, {
                                                                        "previousResource": {
                                                                            "answer": [ { value: 'D' } ],
                                                                            "reaction": 3,
                                                                            "resourceId": collection.resources[3].id,
                                                                            "timeSpent": 4525
                                                                        }
                                                                    }, {json: true})
                                                                    .addHeader('profile-id', profile.id)
                                                                    .addHeader('lms-id', 'quizzes')
                                                                    .inspectRequest()
                                                                    .expectStatus(204)
                                                                    .after(function () {
                                                                        frisby.create('Answer the fifth question')
                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[4].id, {
                                                                                "previousResource": {
                                                                                    "answer": [ { value: 'D' } ],
                                                                                    "reaction": 3,
                                                                                    "resourceId": collection.resources[4].id,
                                                                                    "timeSpent": 4525
                                                                                }
                                                                            }, {json: true})
                                                                            .addHeader('profile-id', profile.id)
                                                                            .addHeader('lms-id', 'quizzes')
                                                                            .inspectRequest()
                                                                            .expectStatus(204)
                                                                            .after(function () {
                                                                                frisby.create('Answer the sixth question')
                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[5].id, {
                                                                                        "previousResource": {
                                                                                            "answer": [ { value: 'D' } ],
                                                                                            "reaction": 3,
                                                                                            "resourceId": collection.resources[5].id,
                                                                                            "timeSpent": 4525
                                                                                        }
                                                                                    }, {json: true})
                                                                                    .addHeader('profile-id', profile.id)
                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                    .inspectRequest()
                                                                                    .expectStatus(204)
                                                                                    .after(function () {
                                                                                        frisby.create('Answer the seventh question')
                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[6].id, {
                                                                                                "previousResource": {
                                                                                                    "answer": [ { value: 'D' } ],
                                                                                                    "reaction": 3,
                                                                                                    "resourceId": collection.resources[6].id,
                                                                                                    "timeSpent": 4525
                                                                                                }
                                                                                            }, {json: true})
                                                                                            .addHeader('profile-id', profile.id)
                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                            .inspectRequest()
                                                                                            .expectStatus(204)
                                                                                            .after(function () {
                                                                                                frisby.create('Answer the eight question')
                                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[7].id, {
                                                                                                        "previousResource": {
                                                                                                            "answer": [ { value: 'D' } ],
                                                                                                            "reaction": 3,
                                                                                                            "resourceId": collection.resources[7].id,
                                                                                                            "timeSpent": 4525
                                                                                                        }
                                                                                                    }, {json: true})
                                                                                                    .addHeader('profile-id', profile.id)
                                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                                    .inspectRequest()
                                                                                                    .expectStatus(204)
                                                                                                    .after(function () {
                                                                                                        frisby.create('Answer the ninth question')
                                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[8].id, {
                                                                                                                "previousResource": {
                                                                                                                    "answer":[ { value: 'D' } ],
                                                                                                                    "reaction": 3,
                                                                                                                    "resourceId": collection.resources[8].id,
                                                                                                                    "timeSpent": 4525
                                                                                                                }
                                                                                                            }, {json: true})
                                                                                                            .addHeader('profile-id', profile.id)
                                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                                            .inspectRequest()
                                                                                                            .expectStatus(204)
                                                                                                            .after(function () {
                                                                                                                frisby.create('Answer the tenth question')
                                                                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[9].id, {
                                                                                                                        "previousResource": {
                                                                                                                            "answer": [ { value: 'D' } ],
                                                                                                                            "reaction": 3,
                                                                                                                            "resourceId": collection.resources[9].id,
                                                                                                                            "timeSpent": 4525
                                                                                                                        }
                                                                                                                    }, {json: true})
                                                                                                                    .addHeader('profile-id', profile.id)
                                                                                                                    .addHeader('lms-id', 'quizzes')
                                                                                                                    .inspectRequest()
                                                                                                                    .expectStatus(204)
                                                                                                                    .after(function () {
                                                                                                                        frisby.create('Finish Context and verify the status')
                                                                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/finish')
                                                                                                                            .addHeader('profile-id', profile.id)
                                                                                                                            .addHeader('client-id', 'quizzes')
                                                                                                                            .inspectRequest()
                                                                                                                            .expectStatus(204)
                                                                                                                            .after(function() {
                                                                                                                                frisby.create('Get the owner id in Quizzes')
                                                                                                                                    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                                                                                                                                    .addHeader('client-id', 'quizzes')
                                                                                                                                    .inspectRequest()
                                                                                                                                    .expectStatus(200)
                                                                                                                                    .inspectJSON()
                                                                                                                                    .afterJSON(function (ownerProfile) {
                                                                                                                                        frisby.create('Get the context Events as an owner')
                                                                                                                                            .get(QuizzesApiUrl + '/v1/context/'
                                                                                                                                                + context.id + '/events')
                                                                                                                                            .addHeader('profile-id', ownerProfile.id)
                                                                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                                                                            .inspectRequest()
                                                                                                                                            .expectStatus(200)
                                                                                                                                            .inspectJSON()
                                                                                                                                            .expectJSON({
                                                                                                                                                "collection": {
                                                                                                                                                    "id": collection.id
                                                                                                                                                },
                                                                                                                                                "contextId": context.id,
                                                                                                                                                "profileEvents": [
                                                                                                                                                    {
                                                                                                                                                        "currentResourceId": collection.resources[9].id,
                                                                                                                                                        "profileId": profile.id,
                                                                                                                                                        "contextProfileSummary": {
                                                                                                                                                            "totalTimeSpent": 45250,
                                                                                                                                                            "averageReaction": 3,
                                                                                                                                                            "averageScore": 0,
                                                                                                                                                            "totalCorrect": 0,
                                                                                                                                                            "totalAnswered": 10
                                                                                                                                                        }
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
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

frisby.create('Test context summary for 10 correctly answered questions ')
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
                                    .afterJSON(function () {
                                        frisby.create('Answer the first and current question')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[1].id , {
                                                "previousResource": {
                                                    "answer": [ { value: 'D' } ],
                                                    "reaction": 3,
                                                    "resourceId": collection.resources[0].id,
                                                    "timeSpent": 4525
                                                }
                                            }, {json: true})
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('lms-id', 'quizzes')
                                            .inspectRequest()
                                            .expectStatus(204)
                                            .after(function() {
                                                frisby.create('Answer the second and current question')
                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[2].id, {
                                                        "previousResource": {
                                                            "answer": [ { value: 'B' } ],
                                                            "reaction": 3,
                                                            "resourceId": collection.resources[1].id,
                                                            "timeSpent": 4525
                                                        }
                                                    }, {json: true})
                                                    .addHeader('profile-id', profile.id)
                                                    .addHeader('lms-id', 'quizzes')
                                                    .inspectRequest()
                                                    .expectStatus(204)
                                                    .after(function () {
                                                        frisby.create('Answer the third and current question')
                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[3].id, {
                                                                "previousResource": {
                                                                    "answer": [ { value: 'Smaller amounts of water evaporate in the cool morning.' } ],
                                                                    "reaction": 3,
                                                                    "resourceId": collection.resources[2].id,
                                                                    "timeSpent": 4525
                                                                }
                                                            }, {json: true})
                                                            .addHeader('profile-id', profile.id)
                                                            .addHeader('lms-id', 'quizzes')
                                                            .inspectRequest()
                                                            .expectStatus(204)
                                                            .after(function () {
                                                                frisby.create('Answer the fourth question')
                                                                    .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[4].id, {
                                                                        "previousResource": {
                                                                            "answer": [ { value: 'The water in the sponge went into the air.' } ],
                                                                            "reaction": 3,
                                                                            "resourceId": collection.resources[3].id,
                                                                            "timeSpent": 4525
                                                                        }
                                                                    }, {json: true})
                                                                    .addHeader('profile-id', profile.id)
                                                                    .addHeader('lms-id', 'quizzes')
                                                                    .inspectRequest()
                                                                    .expectStatus(204)
                                                                    .after(function () {
                                                                        frisby.create('Answer the fifth question')
                                                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/on-resource/' + collection.resources[5].id, {
                                                                                "previousResource": {
                                                                                    "answer": [ { value: 'They are formed from water vapor in the air.' } ],
                                                                                    "reaction": 3,
                                                                                    "resourceId": collection.resources[4].id,
                                                                                    "timeSpent": 4525
                                                                                }
                                                                            }, {json: true})
                                                                            .addHeader('profile-id', profile.id)
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
                                                                                                + context.id + '/events')
                                                                                            .addHeader('profile-id', ownerProfile.id)
                                                                                            .addHeader('lms-id', 'quizzes')
                                                                                            .inspectRequest()
                                                                                            .expectStatus(200)
                                                                                            .inspectJSON()
                                                                                            .expectJSON({
                                                                                                "collection": {
                                                                                                    "id": collection.id
                                                                                                },
                                                                                                "contextId": context.id,
                                                                                                "profileEvents": [
                                                                                                    {
                                                                                                        "currentResourceId": collection.resources[5].id,
                                                                                                        "profileId": profile.id,
                                                                                                        "contextProfileSummary": {
                                                                                                            "totalTimeSpent": 22625,
                                                                                                            "averageReaction": 3,
                                                                                                            "averageScore": 100,
                                                                                                            "totalCorrect": 5,
                                                                                                            "totalAnswered": 5
                                                                                                        }
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



























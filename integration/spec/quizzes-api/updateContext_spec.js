const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test Context create with two assignees')
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
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function(contextJson) {
        frisby.create('Gets the owner profileId of the created context')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function(ownerProfileJson) {
                frisby.create('Updating the context to add 3 more assignees but 1 is already assigned')
                    .put(QuizzesApiUrl + '/v1/context/' + contextJson.id, {
                        'assignees': [
                            {
                                'id': 'student-id-1',
                                'firstName': 'StudentFirstName1',
                                'lastName': 'StudentLastName1',
                                'username': 'student1',
                                'email': 'student1@quizzes.com'
                            },
                            {
                                'id': 'student-id-3',
                                'firstName': 'StudentFirstName3',
                                'lastName': 'StudentLastName3',
                                'username': 'student3',
                                'email': 'student3@quizzes.com'
                            },
                            {
                                'id': 'student-id-4',
                                'firstName': 'StudentFirstName4',
                                'lastName': 'StudentLastName4',
                                'username': 'student4',
                                'email': 'student4@quizzes.com'
                            }

                        ],
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-2'
                            },
                            'metadata': {
                                'title': 'Updated title'
                            }
                        }
                    }, {json: true})
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', ownerProfileJson.id)
                    .inspectRequest()

                    .expectStatus(200)
                    .afterJSON(function(updatedContextJson) {
                        frisby.create('Verify that the context was updated ' +
                            'with 2 more assignees and data updated')
                            .get(QuizzesApiUrl + '/v1/context/created/' + updatedContextJson.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', ownerProfileJson.id)
                            .inspectRequest()

                            .expectStatus(200)
                            .inspectJSON()
                            .expectJSON({
                                "assignees": function(val) {expect(val.length).toBe(4)},
                                'contextData': {
                                    'contextMap': {
                                        'classId': 'class-id-1'
                                    },
                                    'metadata': {
                                        'title': 'Updated title',
                                        'startDate': 0,
                                        'dueDate': 0
                                    }
                                },
                                "id": updatedContextJson.id
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

//Testing the update context with NO assignees
// first we create a context with 2 assignees
// then we update the context without assignees');
frisby.create('Test Context create with two assignees')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
        'assignees': [
            {
                'id': 'student-id-6',
                'firstName': 'StudentFirstName6',
                'lastName': 'StudentLastName6',
                'username': 'student6',
                'email': 'student6@quizzes.com'
            },
            {
                'id': 'student-id-7',
                'firstName': 'StudentFirstName7',
                'lastName': 'StudentLastName7',
                'username': 'student7',
                'email': 'student7@quizzes.com'
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
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function (contextJson) {
        frisby.create('Gets the owner profileId of the created context')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function (ownerProfileJson) {
                frisby.create('Updating the context with NO assignees')
                    .put(QuizzesApiUrl + '/v1/context/' + contextJson.id, {
                        'assignees': [],
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-2'
                            },
                            'metadata': {
                                'title': 'Updated title'
                            }
                        }
                    }, {json: true})
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', ownerProfileJson.id)
                    .inspectRequest()

                    .expectStatus(200)
                    .afterJSON(function (updatedContextJson) {
                        frisby.create('Verify that the context data was updated')
                            .get(QuizzesApiUrl + '/v1/context/created/' + updatedContextJson.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', ownerProfileJson.id)
                            .inspectRequest()

                            .expectStatus(200)
                            .inspectJSON()
                            .expectJSON({
                                "assignees": function (val) {
                                    expect(val.length).toBe(2)
                                },
                                'contextData': {
                                    'contextMap': {
                                        'classId': 'class-id-1'
                                    },
                                    'metadata': {
                                        'title': 'Updated title',
                                        'startDate': 0,
                                        'dueDate': 0
                                    }
                                },
                                "id": updatedContextJson.id
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

//Testing the update context whem I'm not the owner
frisby.create('Test Context create with two assignees')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
        'assignees': [
            {
                'id': 'student-id-6',
                'firstName': 'StudentFirstName6',
                'lastName': 'StudentLastName6',
                'username': 'student6',
                'email': 'student6@quizzes.com'
            },
            {
                'id': 'student-id-7',
                'firstName': 'StudentFirstName7',
                'lastName': 'StudentLastName7',
                'username': 'student7',
                'email': 'student7@quizzes.com'
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
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function (contextJson) {
        frisby.create('Updating the context with NO assignees and RANDOM OWNER')
            .put(QuizzesApiUrl + '/v1/context/' + contextJson.id, {
                'assignees': [],
                'contextData': {
                    'contextMap': {
                        'classId': 'class-id-2'
                    },
                    'metadata': {
                        'title': 'Updated title'
                    }
                }
            }, {json: true})
            .addHeader('lms-id', 'quizzes')
            .addHeader('profile-id', '60958549-b0f5-4a3e-a348-521ed7cd45a5')
            .inspectRequest()

            .expectStatus(404)
            .toss();
    })
    .toss();

//Testing the update context with a context that doesn't exist
frisby.create('Gets an existing owner profileId')
    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
    .addHeader('lms-id', 'quizzes')
    .inspectJSON()
    .afterJSON(function (ownerProfileJson) {
        frisby.create('Updating a random context that does not exist')
            .put(QuizzesApiUrl + '/v1/context/9c066b3a-db63-4bb4-a1a7-ff2ee455edba', {
                'assignees': [],
                'contextData': {
                    'contextMap': {
                        'classId': 'class-id-2'
                    },
                    'metadata': {
                        'title': 'Updated title'
                    }
                }
            }, {json: true})
            .addHeader('lms-id', 'quizzes')
            .addHeader('profile-id', ownerProfileJson.id)
            .inspectRequest()

            .expectStatus(404)
            .toss();
    })
    .toss();

//Testing the update context with valid context data dates
frisby.create('Test Context create with two assignees')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': '927ec170-f7b3-46c3-ace3-24fd61dda0c0',
        'assignees': [
            {
                'id': 'student-id-6',
                'firstName': 'StudentFirstName6',
                'lastName': 'StudentLastName6',
                'username': 'student6',
                'email': 'student6@quizzes.com'
            },
            {
                'id': 'student-id-7',
                'firstName': 'StudentFirstName7',
                'lastName': 'StudentLastName7',
                'username': 'student7',
                'email': 'student7@quizzes.com'
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
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()

    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function (contextJson) {
        frisby.create('Gets the owner profileId of the created context')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function (ownerProfileJson) {
                frisby.create('Updating the context with NO assignees but context data dates')
                    .put(QuizzesApiUrl + '/v1/context/' + contextJson.id, {
                        'assignees': [],
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-2'
                            },
                            'metadata': {
                                'title': 'Updated title',
                                'startDate': 1483042847464,
                                'dueDate': 1485053846353
                            }
                        }
                    }, {json: true})
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', ownerProfileJson.id)
                    .inspectRequest()

                    .expectStatus(200)
                    .afterJSON(function (updatedContextJson) {
                        frisby.create('Verify that the context data was updated')
                            .get(QuizzesApiUrl + '/v1/context/created/' + updatedContextJson.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', ownerProfileJson.id)
                            .inspectRequest()

                            .expectStatus(200)
                            .inspectJSON()
                            .expectJSON({
                                "assignees": function (val) {
                                    expect(val.length).toBe(2)
                                },
                                'contextData': {
                                    'contextMap': {
                                        'classId': 'class-id-1'
                                    },
                                    'metadata': {
                                        'title': 'Updated title',
                                        'startDate': 1483042847464,
                                        'dueDate': 1485053846353
                                    }
                                },
                                "id": updatedContextJson.id
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();
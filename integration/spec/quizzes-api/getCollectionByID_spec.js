const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test getCollectionByID endpoint')
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
    .addHeader('profile-id', '427ec170-f7b3-46c3-ace6-24fd21dda0c0')
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function(contextJson) {
        frisby.create('Gets the owner profileId')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function(profileJson) {
                frisby.create('Verify that the context exists as expected')
                    .get(QuizzesApiUrl + '/v1/context/created/' + contextJson.id)
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', profileJson.id)
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function(createdContextJson) {
                        frisby.create('Verify that the context exists as expected')
                            .get(QuizzesApiUrl + '/v1/collection/' + createdContextJson.collection.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', profileJson.id)
                            .inspectRequest()
                            .expectStatus(200)
                            .expectHeaderContains('content-type', 'application/json')
                            .inspectJSON()
                            .expectJSON({
                                "id": createdContextJson.collection.id,
                                "isCollection": false
                            })
                            .expectJSONTypes({
                                "id": String,
                                "isCollection": Boolean,
                                "resources": Array
                            })
                            .toss();
                    })
                    .toss();
            })
            .toss();
    })
    .toss();


frisby.create('Test getCollectionByID on wrong Collection ID')
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
    .addHeader('profile-id', '427ec170-f7b3-46c3-ace6-24fd21dda0c0')
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function(contextJson) {
        frisby.create('Gets the owner profileId to test and check if incorrect id works.')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function(profileJson) {
                frisby.create('Get the context')
                    .get(QuizzesApiUrl + '/v1/context/created/' + contextJson.id)
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', profileJson.id)
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function(profileJson) {
                        frisby.create('Get the collection using an incorrect id')
                            .get(QuizzesApiUrl + '/v1/collection/' + contextJson.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', profileJson.id)
                            .inspectRequest()
                            .expectStatus(404)
                            .expectHeaderContains('content-type', 'application/json')
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

frisby.create('Test getCollectionID on wrong profileID')
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
    .addHeader('profile-id', '427ec170-f7b3-46c3-ace6-24fd21dda0c0')
    .addHeader('lms-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function(contextJson) {
        frisby.create('Gets the owner profileId')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('lms-id', 'quizzes')
            .inspectJSON()
            .afterJSON(function(profileJson) {
                frisby.create('Get created context to get collection id')
                    .get(QuizzesApiUrl + '/v1/context/created/' + contextJson.id)
                    .addHeader('lms-id', 'quizzes')
                    .addHeader('profile-id', profileJson.id)
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function(createdContextJson) {
                        frisby.create('Verify that the collection cant be retrieved with a wrong profile-id')
                            .get(QuizzesApiUrl + '/v1/collection/' + createdContextJson.id)
                            .addHeader('lms-id', 'quizzes')
                            .addHeader('profile-id', contextJson.id)
                            .inspectRequest()
                            .expectStatus(404)
                            .expectHeaderContains('content-type', 'application/json')
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
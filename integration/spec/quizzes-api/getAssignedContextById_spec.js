const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context creation for one assignee and owner for get assigned context by id')
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
            .afterJSON(function (assigneeProfile) {
                frisby.create('Get context assigned information')
                    .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .expectJSON({
                        'id': context.id,
                        'collection': {
                            'id': function (val) {
                                expect(val).toBeType(String)
                            }
                        },
                        'hasStarted': false,
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-1'
                            },
                            'metadata': {}
                        },
                        'owner': {
                            'id': function (val) {
                                expect(val).toBeType(String)
                            }
                        },
                        "createdDate": function (val) {
                            expect(val).toBeType(Number)
                        }
                    })
                    .toss();
            })
            .toss();
    })
    .toss();

frisby.create('Test context creation for one assignee and owner for get assigned context by id')
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
            .afterJSON(function (assigneeProfile) {
                frisby.create('Verify that owner profile was created')
                    .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()

                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function (ownerProfile) {
                        frisby.create('Get context assigned information')
                            .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
                            .addHeader('profile-id', context.id)
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
    })
    .toss();

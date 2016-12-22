const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context creation for one assignee and owner for start context ')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'assignees': [
            {
                'id': 'student-start-1',
                'firstName': 'StartFirstName1',
                'lastName': 'StartLastName1',
                'username': 'startContext1',
                'email': 'start1@quizzes.com'
            },
            {
                'id': 'student-start-2',
                'firstName': 'StartFirstName2',
                'lastName': 'StartLastName2',
                'username': 'startContext2',
                'email': 'start2@quizzes.com'
            }
        ],
        'contextData': {
            'contextMap': {
                'classId': 'class-id-1'
            },
            'metadata': {}
        },
        'owner': {
            'id': 'teacher-start-1',
            'firstName': 'TeacherStartFirstName1',
            'lastName': 'TeacherStartLastName1',
            'username': 'teacherStart1',
            'email': 'teacherstart1@quizzes.com'
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
    .afterJSON(function (context) {
        frisby.create('Verify that Assignee was created in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-start-1')
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
                        'externalId': 'student-start-1',
                        'firstName': 'StartFirstName1',
                        'lastName': 'StartLastName1',
                        'username': 'startContext1',
                        'email': 'start1@quizzes.com'
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
                                            .afterJSON(function () {
                                                frisby.create('Verify context started')
                                                    .get(QuizzesApiUrl + '/v1/context/assigned/' + context.id)
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
                                                        'hasStarted': true
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
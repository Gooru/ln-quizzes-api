const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Test context creation for one assignee and owner for start context ')
    .post(QuizzesApiUrl + '/v1/contexts', {
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
                            .get(QuizzesApiUrl + '/v1/contexts/' + context.id + '/assigned')
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
                                                    .get(QuizzesApiUrl + '/v1/contexts/' + context.id + '/assigned')
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

                                        frisby.create('Start Context with a not assigned profile')
                                            .post(QuizzesApiUrl + '/v1/context/' + context.id + '/event/start')
                                            .addHeader('profile-id', 'fd5fea6a-3ce8-4b8c-9be8-bb1b1330700a')
                                            .addHeader('client-id', 'quizzes')
                                            .inspectRequest()

                                            .expectStatus(403)
                                            .toss();

                                        frisby.create('Start a non existent Context')
                                            .post(QuizzesApiUrl + '/v1/context/78856489-1501-4eb6-bc13-d73382dfd7be/event/start')
                                            .addHeader('profile-id', profile.id)
                                            .addHeader('client-id', 'quizzes')
                                            .inspectRequest()

                                            .expectStatus(404)
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
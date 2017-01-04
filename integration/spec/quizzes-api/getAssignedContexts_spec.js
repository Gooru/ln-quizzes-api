const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'owner': {
            'id': 'teacher-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
        },
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
        }
    }, {json: true})
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function (context) {
        frisby.create('Retrieves an Assignee Profile')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (assigneeProfile) {
                frisby.create('Verifies Context was correctly assigned and that it has not started')
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .expectJSON('?',

                        {
                            id: context.id,
                            collection: { id: 'be389274-eb52-485f-a451-5ce94e68313f' }
                        }

                    ).toss();
            }).toss();
    }).toss();

frisby.create('Creates a Context and assigns it to an Assignee, then tries to retrieve the assigned Context using ' +
    'the Owner Profile ID')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'assignees': [
            {
                'id': 'student-id-1',
                'firstName': 'StudentFirstName1',
                'lastName': 'StudentLastName1',
                'username': 'student1',
                'email': 'student1@quizzes.com'
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
        frisby.create('Retrieve Owner Profile')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/teacher-id-1')
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (ownerProfile) {
                frisby.create('Tries to retrieve an assigned Context using Owner ID')
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/')
                    .addHeader('profile-id', ownerProfile.id)
                    .addHeader('client-id', 'quizzes')
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
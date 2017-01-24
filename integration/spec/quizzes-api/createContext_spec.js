const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Create Context", function () {
    QuizzesCommon.createContext(function () {
        QuizzesCommon.getProfileByExternalId('student-id-1', function (json) {
            frisby.create('Verify that Assignee Profile has the correct data')
                .get(QuizzesApiUrl + '/v1/profile/' + json.id)
                .addHeader('profile-id', json.id)
                .addHeader('client-id', 'quizzes')
                .inspectRequest()

                .expectStatus(200)
                .inspectJSON()
                .expectJSON({
                    'id': json.id,
                    'externalId': 'student-id-1',
                    'firstName': 'StudentFirstName1',
                    'lastName': 'StudentLastName1',
                    'username': 'student1',
                    'email': 'student1@quizzes.com'
                })
                .toss();
        });
    });
});

QuizzesCommon.startTest("Create a Context with same assignee as owner", function () {
    QuizzesCommon.createContextWithParams(
        {
            'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
            'assignees': [
                {
                    'id': 'profile-id-111',
                    'firstName': 'ProfileFirstName111',
                    'lastName': 'ProfileLastName111',
                    'username': 'profile111',
                    'email': 'profile111@quizzes.com'
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
                'id': 'profile-id-111',
                'firstName': 'ProfileFirstName111',
                'lastName': 'ProfileLastName111',
                'username': 'profile111',
                'email': 'profile111@quizzes.com'
            }
        }
        ,function (createdContext) {
        QuizzesCommon.getProfileByExternalId('profile-id-111', function (profileInfo) {
            frisby.create('Verifies the profile is assigned to the context')
                .get(QuizzesApiUrl + '/v1/context/assigned/' + createdContext.id)
                .addHeader('profile-id', profileInfo.id)
                .addHeader('client-id', 'quizzes')
                .inspectRequest()
                .expectStatus(200)
                .inspectJSON()
                .toss();

            frisby.create('Verifies the profile is the owner of the context')
                .get(QuizzesApiUrl + '/v1/context/created/' + createdContext.id)
                .addHeader('profile-id', profileInfo.id)
                .addHeader('client-id', 'quizzes')
                .inspectRequest()
                .expectStatus(200)
                .inspectJSON()
                .toss();
        });
    });
});

frisby.create('Test wrong external collection ID')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'wrong-assessment-id',
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
    .expectStatus(500)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .toss();

frisby.create('Test context without assignees')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'assignees': [
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
    .expectStatus(406)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSONTypes("Errors", [
        "Error in assignees: {assignment.assignees.size}"
    ])
    .toss();

frisby.create('Test context without assignee fields')
    .post(QuizzesApiUrl + '/v1/context', {
        'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
        'assignees': [
            {
                'firstName': 'StudentFirstName1',
                'lastName': 'StudentLastName1',
                'username': 'student1',
                'email': 'student1@quizzes.com'
            },
            {
                'id': 'student-id-2',
                'lastName': 'StudentLastName2',
                'username': 'student2',
                'email': 'student2@quizzes.com'
            },
            {
                'id': 'student-id-3',
                'firstName': 'StudentFirstName3',
                'username': 'student3',
                'email': 'student3@quizzes.com'
            },
            {
                'id': 'student-id-4',
                'firstName': 'StudentFirstName4',
                'lastName': 'StudentLastName4',
                'email': 'student4@quizzes.com'
            },
            {
                'id': 'student-id-5',
                'firstName': 'StudentFirstName5',
                'lastName': 'StudentLastName5',
                'username': 'student5',
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
    .expectStatus(406)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSONTypes("Errors", [
        "Error in assignees[4].email: Email is required",
        "Error in assignees[3].username: Username is required",
        "Error in assignees[1].firstName: Firstname is required",
        "Error in assignees[2].lastName: Lastname is required",
        "Error in assignees[0].id: ID is required"
    ])
    .toss();

frisby.create('Test context without owner')
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
        }
    }, {json: true})
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(406)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSON({"Errors": [
        "Error in owner: A Owner is required"
    ]})
    .toss();

frisby.create('Test context with owner fields errors')
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
        }

    }, {json: true})
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(406)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSON({"Errors": [
        "Error in owner\.firstName: Firstname is required",
        "Error in owner\.id: ID is required",
        "Error in owner\.lastName: Lastname is required",
        "Error in owner\.email: Email is required",
        "Error in owner\.username: Username is required"
    ]})
    .toss();

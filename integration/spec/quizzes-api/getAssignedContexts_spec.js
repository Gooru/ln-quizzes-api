const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
    .post(QuizzesApiUrl + '/v1/contexts', {
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
                            'hasStarted': false
                        }

                    ).toss();
            }).toss();
    }).toss();

frisby.create('Creates a Context and assigns it to an Assignee, then tries to retrieve the assigned Context using ' +
    'the Owner Profile ID')
    .post(QuizzesApiUrl + '/v1/contexts', {
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
                    .expectStatus(200)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .expectJSON([])
                    .toss();
            })
            .toss();
    })
    .toss();

//Tests for filter parameters combinations "isActive", "startDate" and "dueDate"
frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
    .post(QuizzesApiUrl + '/v1/contexts', {
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
            'metadata': {
                "startDate" : 1483358400000, // Monday, January 2, 2017 12:00:00 PM GMT
                "dueDate" : 1485777600000 // Monday, January 30, 2017 12:00:00 PM GMT
            }
        }
    }, {json: true})
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function () {
        frisby.create('Retrieves an Assignee Profile')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/student-id-1')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (assigneeProfile) {
                frisby.create('Verifies error when combination isActive + startDate + dueDate')
                    // startDate = Sunday, January 1, 2017 12:00:00 PM GMT
                    // dueDate = Tuesday, January 31, 2017 12:00:00 PM GMT
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=true&startDate=1483272000000&dueDate=1485864000000')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(400)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .expectJSON(
                        {
                            'status': 400,
                            'exception': 'isActive parameter can\'t be combined with startDate or dueDate',
                            'message': 'Invalid request'
                        }

                    ).toss();

                frisby.create('Verifies error when combination isActive + startDate')
                // startDate = Sunday, January 1, 2017 12:00:00 PM GMT
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=true&startDate=1483272000000')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(400)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .expectJSON(
                        {
                            'status': 400,
                            'exception': 'isActive parameter can\'t be combined with startDate or dueDate',
                            'message': 'Invalid request'
                        }

                    ).toss();

                frisby.create('Verifies error when combination isActive + dueDate')
                // dueDate = Tuesday, January 31, 2017 12:00:00 PM GMT
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=false&dueDate=1485864000000')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(400)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .expectJSON(
                        {
                            'status': 400,
                            'exception': 'isActive parameter can\'t be combined with startDate or dueDate',
                            'message': 'Invalid request'
                        }

                    ).toss();
                
                frisby.create('Verifies that isActive parameter with no value is valid')
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .toss();

                frisby.create('Verifies that isActive parameter with value is valid')
                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=false')
                    .addHeader('profile-id', assigneeProfile.id)
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .toss();

            }).toss();
    }).toss();

var randomAssigneeID = "student_id_" + QuizzesCommon.generateUUID();

//Tests for filter parameters combinations "isActive", "startDate" and "dueDate"
frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
    .post(QuizzesApiUrl + '/v1/contexts', {
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
                'id': randomAssigneeID,
                'firstName': 'StudentFirstName1',
                'lastName': 'StudentLastName1',
                'username': 'student1',
                'email': 'student1@quizzes.com'
            }
        ],
        'contextData': {
            'contextMap': {
                'classId': 'class-id-2'
            },
            'metadata': {
                "startDate" : 1483358400000, // Monday, January 2, 2017 12:00:00 GMT
                "dueDate" : 1485777600000 // Monday, January 30, 2017 12:00:00 GMT
            }
        }
    }, {json: true})
    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
    .addHeader('client-id', 'quizzes')
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .afterJSON(function () {
        frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
            .post(QuizzesApiUrl + '/v1/contexts', {
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
                        'id': randomAssigneeID,
                        'firstName': 'StudentFirstName1',
                        'lastName': 'StudentLastName1',
                        'username': 'student1',
                        'email': 'student1@quizzes.com'
                    }
                ],
                'contextData': {
                    'contextMap': {
                        'classId': 'class-id-3'
                    },
                    'metadata': {
                        "startDate" : 1483531200000, // Wed, 04 Jan 2017 12:00:00 GMT
                        "dueDate" : 1485604800000 // Sat, 28 Jan 2017 12:00:00 GMT
                    }
                }
            }, {json: true})
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function () {
                frisby.create('Creates a Context and assigns it to two Assignees and verifies it was correctly assigned')
                    .post(QuizzesApiUrl + '/v1/contexts', {
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
                                'id': randomAssigneeID,
                                'firstName': 'StudentFirstName1',
                                'lastName': 'StudentLastName1',
                                'username': 'student1',
                                'email': 'student1@quizzes.com'
                            }
                        ],
                        'contextData': {
                            'contextMap': {
                                'classId': 'class-id-4'
                            },
                            'metadata': {
                                "startDate" : 1483704000000, // Fri, 06 Jan 2017 12:00:00 GMT
                                "dueDate" : 1485432000000 // Thu, 26 Jan 2017 12:00:00 GMT
                            }
                        }
                    }, {json: true})
                    .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
                    .addHeader('client-id', 'quizzes')
                    .inspectRequest()
                    .expectStatus(200)
                    .expectHeaderContains('content-type', 'application/json')
                    .inspectJSON()
                    .afterJSON(function () {
                        frisby.create('Retrieves an Assignee Profile')
                            .get(QuizzesApiUrl + '/v1/profile-by-external-id/' + randomAssigneeID)
                            .addHeader('client-id', 'quizzes')
                            .inspectRequest()
                            .expectStatus(200)
                            .expectHeaderContains('content-type', 'application/json')
                            .inspectJSON()
                            .afterJSON(function (assigneeProfile) {
                                frisby.create('Verifies isActive=true')
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=true')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(3)
                                    .toss();

                                frisby.create('Verifies isActive=false')
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?isActive=false')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(0)
                                    .toss();

                                frisby.create('Verifies startDate with a date in the range')
                                //startDate = Tue, 03 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?startDate=1483444800000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(2)
                                    .toss();

                                frisby.create('Verifies startDate with a date out of the range')
                                //startDte = Sat, 07 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?startDate=1483790400000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(0)
                                    .toss();

                                frisby.create('Verifies dueDate with a date in the range')
                                //dueDate = Sun, 29 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?dueDate=1485691200000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(2)
                                    .toss();

                                frisby.create('Verifies dueDate with a date out of the range')
                                //dueDate = Wed, 25 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?dueDate=1485345600000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(0)
                                    .toss();

                                frisby.create('Verifies startDate and dueDate with a date in the range')
                                //startDate = Tue, 03 Jan 2017 12:00:00 GMT
                                //dueDate = Sun, 29 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?' +
                                        'startDate=1483444800000&dueDate=1485691200000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(2)
                                    .toss();

                                frisby.create('Verifies startDate and dueDate with a date in the range')
                                //startDate = Tue, 03 Jan 2017 12:00:00 GMT
                                //dueDate = Fri, 27 Jan 2017 12:00:00 GMT
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?' +
                                        'startDate=1483444800000&dueDate=1485518400000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(1)
                                    .toss();

                                frisby.create('Verifies startDate and dueDate with a date out of the range')
                                //startDate = Sat, 07 Jan 2017 12:00:00 GMT - this is out
                                //dueDate = Fri, 27 Jan 2017 12:00:00 GMT - this is in
                                    .get(QuizzesApiUrl + '/v1/contexts/assigned/?' +
                                        'startDate=1483790400000&dueDate=1485518400000')
                                    .addHeader('profile-id', assigneeProfile.id)
                                    .addHeader('client-id', 'quizzes')
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectHeaderContains('content-type', 'application/json')
                                    .inspectJSON()
                                    .expectJSONLength(0)
                                    .toss();
                            }).toss();
                    }).toss();
            }).toss();
    }).toss();

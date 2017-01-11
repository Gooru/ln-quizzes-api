const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');


frisby.create('Token creation')
    .post(QuizzesApiUrl + '/v1/session/authorization', {
        "clientApiKey": "f6985f53-3607-40ae-8404-0a565dfd329c",
        "clientApiSecret": "ypCpfRt=qmnWX&C#Z#Xn*V#2mk_MGjd@",
        "user": {
            'externalId': 'teacher-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
        }
    }, {json: true})
    // This profile-id header for this specific endpoint is fake, it is useless but it is required by the endpoint
    // Both headers will be replaced by the session-token header once we finish with the Session implementation
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .toss();



frisby.create('Token creation with invalid api key')
    .post(QuizzesApiUrl + '/v1/session/authorization', {
        "clientApiKey": "f6985f53-3607-40ae-8404-0a5bad00329c",
        "clientApiSecret": "ypCpfRt=qmnWX&C#Z#Xn*V#2mk_MGjd@",
        "user": {
            'externalId': 'teacher-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
        }
    }, {json: true})
    // This profile-id header for this specific endpoint is fake, it is useless but it is required by the endpoint
    // Both headers will be replaced by the session-token header once we finish with the Session implementation
    .inspectRequest()
    .expectStatus(404)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSON({
        "status": 401
    })
    .expectJSONTypes({
        message: String,
        status: Number,
        exception: String
    })
    .toss();

frisby.create('Token creation with invalid api secret')
    .post(QuizzesApiUrl + '/v1/session/authorization', {
        "clientApiKey": "f6985f53-3607-40ae-8404-0a565dfd329c",
        "clientApiSecret": "ypCpfRt=qminvalidZ#Xn*V#2mk_MGjd@",
        "user": {
            'externalId': 'teacher-id-1',
            'firstName': 'TeacherFirstName1',
            'lastName': 'TeacherLastName1',
            'username': 'teacher1',
            'email': 'teacher1@quizzes.com'
        }
    }, {json: true})
    // This profile-id header for this specific endpoint is fake, it is useless but it is required by the endpoint
    // Both headers will be replaced by the session-token header once we finish with the Session implementation
    .inspectRequest()
    .expectStatus(200)
    .expectHeaderContains('content-type', 'application/json')
    .inspectJSON()
    .expectJSON({
        "status": 401
    })
    .expectJSONTypes({
        message: String,
        status: Number,
        exception: String
    })
    .toss();
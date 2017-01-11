const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
var frisby = require('frisby');

frisby.create('Get the profile by profile id in Quizzes')
    .get(QuizzesApiUrl + '/v1/profile/66c3d5aa-1c4b-4589-80fb-080b7dcc21b1')
    .addHeader('client-id', 'quizzes')
    .addHeader('profile-id', '6b75be01-960e-4ead-8c44-36243d11e33d')
    .inspectRequest()
    .expectStatus(200)
    .inspectJSON()
    .expectJSON({
        'id': '66c3d5aa-1c4b-4589-80fb-080b7dcc21b1',
        'firstName': 'StudentFirstName1',
        'lastName': 'StudentLastName1',
        'username': 'student1',
        'email': 'student1@quizzes.com'
    })
    .toss();


frisby.create('Get the profile id in Quizzes on a non existent ID')
    .get(QuizzesApiUrl + '/v1/profile/0e4b4050-813a-41be-aef0-41fda6a41765')
    .addHeader('client-id', 'quizzes')
    .addHeader('profile-id', '6b75be01-960e-4ead-8c44-36243d11e33d')
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


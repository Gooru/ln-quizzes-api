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

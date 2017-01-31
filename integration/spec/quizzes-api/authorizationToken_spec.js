const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Create a Context with same assignee as owner", function () {
    QuizzesCommon.getAuthorizationToken("teacherqa01", function (authorizationResponse) {
        frisby.create('Testing Create Context with Authorization token')
            .post(QuizzesApiUrl + '/v1/context', {
                "externalCollectionId": "b7af52ce-7afc-4301-959c-4342a6f941cb",
                "owner":	{
                    "id":"1c2f6635-cefd-4e24-822c-6384377984e3",
                    "firstName":"teacherqa01",
                    "lastName":"teacherqa01",
                    "username":"teacherqa01",
                    "email" : "teacherqa01@edify.cr"
                },
                "assignees": [
                    {
                        "id":"123455444",
                        "firstName":"firstname",
                        "lastName":"lastname",
                        "username":"student1",
                        "email" : "student1@gooru.org"
                    }
                ],
                "contextData": {
                    "classId":"123"
                }
            }, {json: true})
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .addHeader('client-id', 'quizzes')
            .addHeader('Authorization', 'Token ' + authorizationResponse.access_token)
            .inspectRequest()
            .expectStatus(200)
            //.expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .toss()
    });
});

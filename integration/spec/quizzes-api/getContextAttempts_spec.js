const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Context Profile Attempts", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        var profileId = QuizzesCommon.getProfileIdFromToken(authToken.access_token);
        frisby.create("Test context attempts with no attempts started")
            .get(QuizzesApiUrl + '/v1/contexts/' + contextResponse.id + "/profiles/" + profileId + "/attempts")
            .addHeader('Authorization', 'Token ' + authToken.access_token)
            .inspectRequest()
            .expectStatus(200)
            .expectJSONLength( 'attempts', 0 )
            .inspectJSON()
            .toss();

        //In this test an authorized user looks for an existent contextId but with a wrong profile(assignee)
        // and the list is empty
        frisby.create("Test context attempts with random profile")
            .get(QuizzesApiUrl + '/v1/contexts/' + contextResponse.id + "/profiles/" + QuizzesCommon.generateUUID()
                + "/attempts")
            .addHeader('Authorization', 'Token ' + authToken.access_token)
            .inspectRequest()
            .expectStatus(200)
            .expectJSONLength( 'attempts', 0 )
            .inspectJSON()
            .toss();

        frisby.create("Test context attempts with random context")
            .get(QuizzesApiUrl + '/v1/contexts/' + QuizzesCommon.generateUUID() + "/profiles/" + profileId + "/attempts")
            .addHeader('Authorization', 'Token ' + authToken.access_token)
            .inspectRequest()

            .expectStatus(404)
            .inspectJSON()
            .toss();
    });
});
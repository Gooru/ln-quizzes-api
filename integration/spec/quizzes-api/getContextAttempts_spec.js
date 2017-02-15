const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Context Profile Attempts", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        var profileId = QuizzesCommon.getProfileIdFromToken(authToken.access_token);
        frisby.create("Test context attempts with no attempts started")
            .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + profileId)
            .addHeader('Authorization', 'Token ' + authToken.access_token)
            .inspectRequest()
            .expectStatus(200)
            .expectJSONLength( 'attempts', 0 )
            .inspectJSON()
            .toss();
    });
});
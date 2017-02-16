const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Attempts with context not started", function () {
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

QuizzesCommon.startTest("Get Attempts started but no finished", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        var profileId = QuizzesCommon.getProfileIdFromToken(authToken.access_token);
        ContentProviderUtils.getAuthorizationToken("TestAcc02", function (assigneeAuthToken) {
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                frisby.create("Test context attempts with started context by owner (wrong profile ID)")
                // The owner is ok but the owner doesn't have started contexts,
                // so this is an empty attempts for a wrong profileId
                    .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + profileId)
                    .addHeader('Authorization', 'Token ' + authToken.access_token)
                    .inspectRequest()
                    .expectStatus(200)
                    .expectJSONLength('attempts', 0)
                    .inspectJSON()
                    .toss();

                var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
                frisby.create("Test context attempts with started context by assigneeId")
                // The owner is ok and the profileId is the correct id of the assignee
                    .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + assigneeProfileId)
                    .addHeader('Authorization', 'Token ' + authToken.access_token)
                    .inspectRequest()
                    .expectStatus(200)
                    .expectJSONLength('attempts', 0)
                    .inspectJSON()
                    .toss()

                frisby.create("Test context attempts with started context with assignee token (wrong owner)")
                // With a wrong token this is a content not found error
                    .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + profileId)
                    .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                    .inspectRequest()
                    .expectStatus(404)
                    .toss()

            })
        })
    });
});

QuizzesCommon.startTest("Get Attempts started and finished", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        var profileId = QuizzesCommon.getProfileIdFromToken(authToken.access_token);
        ContentProviderUtils.getAuthorizationToken("TestAcc02", function (assigneeAuthToken) {
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                QuizzesCommon.finishContext(contextResponse.id, assigneeAuthToken.access_token, function () {
                    frisby.create("Test context attempts with finished context by owner (wrong profileId)")
                    // The owner is ok but the owner doesn't have started contexts,
                    // so this is an empty attempts for a wrong profileId
                        .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + profileId)
                        .addHeader('Authorization', 'Token ' + authToken.access_token)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSONLength('attempts', 0)
                        .inspectJSON()
                        .toss();

                    var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
                    frisby.create("Test context attempts with finished context by assigneeId")
                    // The owner is ok and the profileId is the correct id of the assignee
                        .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + assigneeProfileId)
                        .addHeader('Authorization', 'Token ' + authToken.access_token)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSONLength('attempts', 1)
                        .inspectJSON()
                        .toss()

                    frisby.create("Test context attempts with finished context with assignee token (wrong owner)")
                    // With a wrong token this is a content not found error
                        .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id + "/profiles/" + profileId)
                        .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss()

                })
            })
        })
    });
});
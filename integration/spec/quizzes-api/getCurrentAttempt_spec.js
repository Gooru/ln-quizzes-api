const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Current Attempt with context not started", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assigneeAuthToken) {
            frisby.create("Test current attempt with no attempts started by context owner")
                .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                .addHeader('Authorization', 'Token ' + authToken.access_token)
                .inspectRequest()
                .expectStatus(200)
                .expectJSONLength('profileAttempts', 0)
                .inspectJSON()
                .toss();

            frisby.create("Test current attempt with no attempts started by assignee")
                .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                .inspectRequest()
                .expectStatus(404)
                .inspectJSON()
                .toss();
        })
    });
});

QuizzesCommon.startTest("Get Current attempts, context started but not finished", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assigneeAuthToken) {
            var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                frisby.create("Test current attempt by owner")
                    .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                    .addHeader('Authorization', 'Token ' + authToken.access_token)
                    .inspectRequest()
                    .expectStatus(200)
                    .expectJSONLength('profileAttempts', 1)
                    .expectJSON('profileAttempts', [{
                        'profileId': assigneeProfileId
                    }])
                    .inspectJSON()
                    .toss();

                frisby.create("Test current attempt with started context with assignee token (wrong owner)")
                    .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                    .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                    .inspectRequest()
                    .expectStatus(404)
                    .toss()

            })
        })
    });
});

QuizzesCommon.startTest("Get Current Attempt started and finished", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assigneeAuthToken) {
            var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                QuizzesCommon.finishContext(contextResponse.id, assigneeAuthToken.access_token, function () {
                    frisby.create("Test current attempt with finished context by owner")
                        .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                        .addHeader('Authorization', 'Token ' + authToken.access_token)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSONLength('profileAttempts', 1)
                        .expectJSON('profileAttempts', [{
                            'profileId': assigneeProfileId
                        }])
                        .inspectJSON()
                        .toss();

                    frisby.create("Test context attempts with finished context with assignee token (wrong owner)")
                        .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                        .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss()

                })
            })
        })
    });
});

QuizzesCommon.startTest("Get Current Attempts for two assignees, 1 complete, 1 not finished", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assignee1AuthToken) {
            QuizzesCommon.startContext(contextResponse.id, assignee1AuthToken.access_token, function (startResponse1) {
                QuizzesCommon.finishContext(contextResponse.id, assignee1AuthToken.access_token, function () {
                    ContentProviderUtils.getAuthorizationToken("Student02", function (assignee2AuthToken) {
                        QuizzesCommon.startContext(contextResponse.id, assignee2AuthToken.access_token, function (startResponse2) {
                            var assignee1ProfileId = QuizzesCommon.getProfileIdFromToken(assignee1AuthToken.access_token);
                            var assignee2ProfileId = QuizzesCommon.getProfileIdFromToken(assignee2AuthToken.access_token);

                            frisby.create("Test current context attempts with two assignees by owner")
                                .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                                .addHeader('Authorization', 'Token ' + authToken.access_token)
                                .inspectRequest()
                                .expectStatus(200)
                                .expectJSONLength('profileAttempts', 2)
                                .expectJSON('profileAttempts', [{
                                    'profileId': assignee1ProfileId,
                                    'isComplete': true
                                },
                                    {
                                    'profileId': assignee2ProfileId,
                                    'isComplete': false
                                }])
                                .inspectJSON()
                                .toss();

                            frisby.create("Test context attempts with two assignees by assignee number 1")
                                .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                                .addHeader('Authorization', 'Token ' + assignee1AuthToken.access_token)
                                .inspectRequest()
                                .expectStatus(404)
                                .inspectJSON()
                                .toss();

                            frisby.create("Test context attempts with two assignees by assignee number 2")
                                .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextResponse.id)
                                .addHeader('Authorization', 'Token ' + assignee2AuthToken.access_token)
                                .inspectRequest()
                                .expectStatus(404)
                                .toss();
                        })
                    })
                })
            })
        })
    });
});

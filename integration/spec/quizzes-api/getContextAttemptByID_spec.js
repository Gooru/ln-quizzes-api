const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
var frisby = require('frisby');

QuizzesCommon.startTest("Get Attempt started and finished info", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assigneeAuthToken) {
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                QuizzesCommon.finishContext(contextResponse.id, assigneeAuthToken.access_token, function () {
                    var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
                    QuizzesCommon.getAttempts(contextResponse.id, assigneeProfileId, authToken.access_token, function (attemptsResponse) {

                        frisby.create("Test context attempt by ID by owner")
                            .get(QuizzesApiUrl + '/v1/attempts/' + attemptsResponse.attempts[0])
                            .addHeader('Authorization', 'Token ' + authToken.access_token)
                            .inspectRequest()
                            .expectStatus(200)
                            .expectJSON({
                                'profileId': assigneeProfileId,
                                'contextId': contextResponse.id,
                                'eventSummary': function(summary) {expect(summary.totalAnswered).toBe(10)},
                                'events': function(eventList) {expect(eventList.length).toBe(10)}
                                })
                            .inspectJSON()
                            .toss();

                        frisby.create("Test context attempt by ID by assignee")
                            .get(QuizzesApiUrl + '/v1/attempts/' + attemptsResponse.attempts[0])
                            .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                            .inspectRequest()
                            .expectStatus(200)
                            .expectJSON({
                                'profileId': assigneeProfileId,
                                'contextId': contextResponse.id,
                                'eventSummary': function(summary) {expect(summary.totalAnswered).toBe(10)},
                                'events': function(eventList) {expect(eventList.length).toBe(10)}
                            })
                            .inspectJSON()
                            .toss();

                        frisby.create("Test context attempt by ID with valid owner but random attempt (wrong attemp)")
                            .get(QuizzesApiUrl + '/v1/attempts/' + QuizzesCommon.generateUUID())
                            .addHeader('Authorization', 'Token ' + authToken.access_token)
                            .inspectRequest()
                            .expectStatus(404)
                            .toss();

                        frisby.create("Test context attempt by ID with valid assignee but random attempt (wrong attemp)")
                            .get(QuizzesApiUrl + '/v1/attempts/' + QuizzesCommon.generateUUID())
                            .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                            .inspectRequest()
                            .expectStatus(404)
                            .toss()
                    })
                })
            })
        })
    });
});

QuizzesCommon.startTest("Get Attempt started and finished info with a wrong user", function () {
    QuizzesCommon.createContext(function (contextResponse, authToken) {
        ContentProviderUtils.getAuthorizationToken("Student01", function (assigneeAuthToken) {
            QuizzesCommon.startContext(contextResponse.id, assigneeAuthToken.access_token, function (startResponse) {
                QuizzesCommon.finishContext(contextResponse.id, assigneeAuthToken.access_token, function () {
                    var assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken.access_token);
                    QuizzesCommon.getAttempts(contextResponse.id, assigneeProfileId, authToken.access_token, function (attemptsResponse) {
                        ContentProviderUtils.getAuthorizationToken("Student02", function (assignee2AuthToken) {

                            frisby.create("Test context attempt by ID by owner")
                                .get(QuizzesApiUrl + '/v1/attempts/' + attemptsResponse.attempts[0])
                                .addHeader('Authorization', 'Token ' + authToken.access_token)
                                .inspectRequest()
                                .expectStatus(200)
                                .expectJSON({
                                    'profileId': assigneeProfileId,
                                    'contextId': contextResponse.id,
                                    'eventSummary': function (summary) {
                                        expect(summary.totalAnswered).toBe(10)
                                    },
                                    'events': function (eventList) {
                                        expect(eventList.length).toBe(10)
                                    }
                                })
                                .inspectJSON()
                                .toss();

                            frisby.create("Test context attempt by ID by assignee")
                                .get(QuizzesApiUrl + '/v1/attempts/' + attemptsResponse.attempts[0])
                                .addHeader('Authorization', 'Token ' + assigneeAuthToken.access_token)
                                .inspectRequest()
                                .expectStatus(200)
                                .expectJSON({
                                    'profileId': assigneeProfileId,
                                    'contextId': contextResponse.id,
                                    'eventSummary': function (summary) {
                                        expect(summary.totalAnswered).toBe(10)
                                    },
                                    'events': function (eventList) {
                                        expect(eventList.length).toBe(10)
                                    }
                                })
                                .inspectJSON()
                                .toss();

                            frisby.create("Test context attempt with wrong assignee")
                                .get(QuizzesApiUrl + '/v1/attempts/' + attemptsResponse.attempts[0])
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

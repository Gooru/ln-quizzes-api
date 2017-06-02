const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');

QuizzesCommon.startTest('Get Attempt started and finished info', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                        let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                        QuizzesCommon.getAttemptsByProfileId(contextId, assigneeProfileId, authToken, function (attemptsResponse) {
                            let firstAttemptId = attemptsResponse.attempts[0];
                            Frisby.create('Test context attempt by owner')
                                .get(QuizzesApiUrl + `/v1/attempts/${firstAttemptId}`)
                                .addHeader('Authorization', `Token ${authToken}`)
                                .inspectRequest()
                                .expectStatus(200)
                                .expectJSONSchema({
                                    "type": "object",
                                    "properties": {
                                        "profileId": {
                                            "type": "string"
                                        },
                                        "contextId": {
                                            "type": "string"
                                        },
                                        "collectionId": {
                                            "type": "string"
                                        },
                                        "taxonomySummary": {
                                            "type": "array",
                                            "items": {
                                                "type": "object",
                                                "properties": {
                                                    "taxonomyId": {
                                                        "type": "string"
                                                    },
                                                    "averageScore": {
                                                        "type": "number"
                                                    },
                                                    "averageReaction": {
                                                        "type": "number"
                                                    },
                                                    "totalAnswered": {
                                                        "type": "number"
                                                    },
                                                    "totalCorrect": {
                                                        "type": "number"
                                                    },
                                                    "totalTimeSpent": {
                                                        "type": "number"
                                                    },
                                                    "resources": {
                                                        "type": "array"
                                                    }
                                                },
                                                "required": ["taxonomyId", "averageScore", "averageReaction",
                                                    "totalAnswered", "totalCorrect", "totalTimeSpent", "resources"]
                                            },
                                            "minItems": 10,
                                            "maxItems": 10,
                                            "uniqueItems": false
                                        }
                                    },
                                    "required": ["profileId", "contextId", "eventSummary", "taxonomySummary"]
                                })
                                .inspectJSON()
                                .toss();

                            Frisby.create('Test context attempt by assignee')
                                .get(QuizzesApiUrl + `/v1/attempts/${firstAttemptId}`)
                                .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                                .inspectRequest()
                                .expectStatus(200)
                                .expectJSON({
                                    'profileId': assigneeProfileId,
                                    'contextId': contextResponse.id
                                })
                                .inspectJSON()
                                .toss();

                            let randomAttemptId = QuizzesCommon.generateUUID();
                            Frisby.create('Test context attempt with valid owner but random attempt (wrong attemp)')
                                .get(QuizzesApiUrl + `/v1/attempts/${randomAttemptId}`)
                                .addHeader('Authorization', `Token ${authToken}`)
                                .inspectRequest()
                                .expectStatus(404)
                                .toss();

                            Frisby.create('Test context attempt with valid assignee but random attempt (wrong attemp)')
                                .get(QuizzesApiUrl + `/v1/attempts/${randomAttemptId}`)
                                .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                                .inspectRequest()
                                .expectStatus(404)
                                .toss();
                        });
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('Get Attempt started and finished info with a wrong user', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        let classId = Config.getClass('TestClass01').id;
        QuizzesCommon.createContext(collectionId, classId, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                        let assigneeProfileId = QuizzesCommon.getProfileIdFromToken(assigneeAuthToken);
                        QuizzesCommon.getAttemptsByProfileId(contextId, assigneeProfileId, authToken, function (attemptsResponse) {
                            QuizzesCommon.getAuthorizationToken('Student02', function (assignee2AuthToken) {
                                let firstAttemptId = attemptsResponse.attempts[0];
                                Frisby.create('Test context attempt by owner')
                                    .get(QuizzesApiUrl + `/v1/attempts/${firstAttemptId}`)
                                    .addHeader('Authorization', `Token ${authToken}`)
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectJSON({
                                        'profileId': assigneeProfileId,
                                        'contextId': contextResponse.id,
                                        'eventSummary': function (summary) {
                                            expect(summary.totalAnswered).toBe(0)
                                        },
                                        'events': function (eventList) {
                                            expect(eventList.length).toBe(10)
                                        }
                                    })
                                    .inspectJSON()
                                    .toss();

                                Frisby.create('Test context attempt by assignee')
                                    .get(QuizzesApiUrl + `/v1/attempts/${firstAttemptId}`)
                                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                                    .inspectRequest()
                                    .expectStatus(200)
                                    .expectJSON({
                                        'profileId': assigneeProfileId,
                                        'contextId': contextResponse.id,
                                        'eventSummary': function (summary) {
                                            expect(summary.totalAnswered).toBe(0)
                                        },
                                        'events': function (eventList) {
                                            expect(eventList.length).toBe(10)
                                        }
                                    })
                                    .inspectJSON()
                                    .toss();

                                Frisby.create('Test context attempt with wrong assignee')
                                    .get(QuizzesApiUrl + `/v1/attempts/${firstAttemptId}`)
                                    .addHeader('Authorization', `Token ${assignee2AuthToken}`)
                                    .inspectRequest()
                                    .expectStatus(404)
                                    .toss();
                            });
                        });
                    });
                });
            });
        });
    });
});

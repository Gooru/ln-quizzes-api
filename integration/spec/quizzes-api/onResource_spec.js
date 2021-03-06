const Config = require('./quizzesTestConfiguration.js');
const QuizzesCommon = require('./quizzesCommon.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const Frisby = require('frisby');

Frisby.globalSetup({
    timeout: 30000

});

let eventContext = {
    'eventSource': 'dailyclassactivity',
    'sourceUrl': 'http://nile-qa.gooru.org/',
    'pathId': 1,
    'timezone': 'America/Costa_Rica'
};

QuizzesCommon.startTest('OnResource on started context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collection.id, classId, true, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    let previousResource = collection.resources[0];
                    let resourceId = collection.resources[1].id;

                    Frisby.create('Test valid onResource call')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .toss();

                    Frisby.create('onResource call with resourceId === previous resource ID')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${previousResource.id}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .toss();

                    Frisby.create('onResource call without previous resource')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(500)
                        .toss();

                    Frisby.create('onResource call as the teacher, the context is not found for the teacher')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${authToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss();

                    let randomUUID = QuizzesCommon.generateUUID();
                    Frisby.create('onResource call on a wrong previous resource, previous resource not found')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': randomUUID,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss();

                    Frisby.create('onResource call with context not found')
                        .post(QuizzesApiUrl + `/v1/contexts/${randomUUID}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss();

                    Frisby.create('onResource call with resource not found')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${randomUUID}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(404)
                        .toss();

                });
            });
        });
    });
});

QuizzesCommon.startTest('OnResource with anonymous user', function () {
    QuizzesCommon.getAnonymousToken(function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        QuizzesCommon.createContext(collection.id, null, true, {}, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.startContext(contextId, authToken, function () {
                let previousResource = collection.resources[0];
                let resourceId = collection.resources[1].id;

                Frisby.create('onResource call with anonymous user')
                    .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                        'eventContext': eventContext,
                        'previousResource': {
                            'answer': previousResource.correctAnswer,
                            'reaction': 2,
                            'resourceId': previousResource.id,
                            'timeSpent': 1500
                        }
                    }, {json: true})
                    .addHeader('Authorization', `Token ${authToken}`)
                    .inspectRequest()
                    .expectStatus(200)
                    .toss();
            });
        });
    });
});

QuizzesCommon.startTest('OnResource on a context not started', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collection.id, classId, true, contextMap, authToken, function (contextResponse) {
            let contextResponseId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                let previousResource = collection.resources[0];
                let resourceId = collection.resources[1].id;

                Frisby.create('onResource call, context not started')
                    .post(QuizzesApiUrl + `/v1/contexts/${contextResponseId}/onResource/${resourceId}`, {
                        'eventContext': eventContext,
                        'previousResource': {
                            'answer': previousResource.correctAnswer,
                            'reaction': 2,
                            'resourceId': previousResource.id,
                            'timeSpent': 1500
                        }
                    }, {json: true})
                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                    .inspectRequest()
                    .expectStatus(404)
                    .toss();

            });
        });
    });
});

QuizzesCommon.startTest('OnResource on started and finished context', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collection.id, classId, true, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    QuizzesCommon.finishContext(contextId, assigneeAuthToken, function () {
                        let previousResource = collection.resources[0];
                        let resourceId = collection.resources[1].id;

                        Frisby.create('onResource call with started and finished context')
                            .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                                'eventContext': eventContext,
                                'previousResource': {
                                    'answer': previousResource.correctAnswer,
                                    'reaction': 2,
                                    'resourceId': previousResource.id,
                                    'timeSpent': 1500
                                }
                            }, {json: true})
                            .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                            .inspectRequest()
                            .expectStatus(200)
                            .toss();
                    });
                });
            });
        });
    });
});

QuizzesCommon.startTest('OnResource with no feedback because of no setting', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collection = Config.getCollection('TestCollection01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(collection.id, classId, true, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    let previousResource = collection.resources[0];
                    let resourceId = collection.resources[1].id;

                    Frisby.create('OnResource with no feedback because of no setting')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSON({})
                        .toss();
                });
            });
        });
    });
});

QuizzesCommon.startTest('OnResource with feedback by setting', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessment = Config.getAssessment('TestAssessment01');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(assessment.id, classId, false, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    let previousResource = assessment.resources[0];
                    let resourceId = assessment.resources[1].id;

                    Frisby.create('OnResource with showFeedback immediate')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSON({ score: 100 })
                        .toss();
                });
            });
        });
    });
});

QuizzesCommon.startTest('OnResource with no feedback by setting', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessment = Config.getAssessment('TestAssessment02');
        let classId = Config.getClass('TestClass01').id;
        let contextMap = QuizzesCommon.generateRandomContextMap();
        QuizzesCommon.createContext(assessment.id, classId, false, contextMap, authToken, function (contextResponse) {
            let contextId = contextResponse.id;
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                QuizzesCommon.startContext(contextId, assigneeAuthToken, function () {
                    let previousResource = assessment.resources[0];
                    let resourceId = assessment.resources[1].id;
                    Frisby.create('OnResource with showFeedback never')
                        .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, {
                            'eventContext': eventContext,
                            'previousResource': {
                                'answer': previousResource.correctAnswer,
                                'reaction': 2,
                                'resourceId': previousResource.id,
                                'timeSpent': 1500
                            }
                        }, {json: true})
                        .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                        .inspectRequest()
                        .expectStatus(200)
                        .expectJSON({})
                        .toss();
                });
            });
        });
    });
});
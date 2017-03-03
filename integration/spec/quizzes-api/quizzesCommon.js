const Config = require('./quizzesTestConfiguration.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const ContentProviderApiUrl = Config.contentProviderApiUrl;
const Frisby = require('frisby');

var quizzesCommon = {

    /**
     * copied from https://jsfiddle.net/briguy37/2MVFd/
     */
    generateUUID: function generateUUID() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
            });
        return uuid;
    },

    getProfileIdFromToken : function(token) {
        return Buffer(token, 'base64').toString().split(":")[2];
    },

    startTest: function (title, functionalTest) {
        console.log("\n ****** Executing Functional Test: " + title + " ****** \n");
        functionalTest();
    },

    getAuthorizationToken: function (userId, afterJsonFunction) {
        let authorizationUser = Config.getUser(userId);
        console.log('Autorization user ' + authorizationUser.identityId);
        Frisby.create('Gets the authorization token for ' + userId)
            .post(ContentProviderApiUrl + '/v2/authorize', {
                'client_key': 'c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==',
                'client_id': 'ba956a97-ae15-11e5-a302-f8a963065976',
                'grant_type': 'google',
                'user': {
                    'firstname': authorizationUser.firstname,
                    'lastname': authorizationUser.lastname,
                    'identity_id': authorizationUser.identityId
                }
            }, {json: true})
            .inspectRequest()
            .expectStatus(201)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (authorizationResponse) {
                afterJsonFunction(authorizationResponse.access_token);
            })
            .toss();
    },

    getAnonymousToken: function (afterJsonFunction) {
        console.log('Autorization anonymous user');
        Frisby.create('Gets the authorization token for an anonymous user')
            .post(ContentProviderApiUrl + '/v2/signin', {
                'client_key': 'c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==',
                'client_id': 'ba956a97-ae15-11e5-a302-f8a963065976',
                'grant_type': 'anonymous'
            }, {json: true})
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (authorizationResponse) {
                afterJsonFunction(authorizationResponse.access_token);
            })
            .toss();
    },

    createContext: function (collectionId, classId, isCollection, contextMap, authToken, afterJsonFunction) {
        Frisby.create(`Create Context for collectionId ${collectionId} and classId ${classId}`)
            .post(QuizzesApiUrl + '/v1/contexts', {
                'collectionId': collectionId,
                'classId': classId,
                'isCollection': isCollection,
                'contextData': {
                    'metadata': {
                        'title': `Context for collectionId ${collectionId} and classId ${classId}`
                    },
                    'contextMap': contextMap
                }
            }, {json: true})
            .addHeader('Authorization', `Token ${authToken}`)
            .inspectRequest()
            .expectStatus(200)
            .timeout(60000) //waits 60sec for response
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(afterJsonFunction)
            .toss();
    },

    /**
     * Creates a context passing the JSON body and the expected response as a parameter
     * @param body JSON with context information
     * @param expectedBody JSON with expected result
     * @param afterJsonFunction function to call on afterJSON
     */
    createContextWithParams: function (body, afterJsonFunction) {
        var contextClass = Config.getClass("TestClass01");
        this.getAuthorizationToken(contextClass.owner, function (authResponse) {
            Frisby.create('Test context creation using body ' + body)
                .post(QuizzesApiUrl + '/v1/contexts', body, {json: true})
                .addHeader('Authorization', 'Token ' + authResponse.access_token)
                .inspectRequest()
                .expectStatus(200)
                .expectHeaderContains('content-type', 'application/json')
                .inspectJSON()
                .afterJSON(function (context) {
                    afterJsonFunction(context);
                })
                .toss();
        })
    },

    getProfileByExternalId: function (externalId, afterJsonFunction) {
        Frisby.create('Get the profile information in Quizzes')
            .get(QuizzesApiUrl + '/v1/profile-by-external-id/' + externalId)
            .addHeader('client-id', 'quizzes')
            .inspectRequest()

            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (profile) {
                afterJsonFunction(profile);
            })
            .toss()
    },

    getAssignedContextById: function (contextId, assigneeAuthToken, expectedJson, afterJsonFunction) {
        Frisby.create('Get assigned context information')
            .get(QuizzesApiUrl + `/v1/contexts/${contextId}/assigned`)
            .addHeader('Authorization', `Token ${assigneeAuthToken}`)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .expectJSON(expectedJson)
            .afterJSON(function (context) {
                afterJsonFunction(context);
            })
            .toss()
    },

    getCreatedContextById: function (contextId, authToken, expectedJson, afterJsonFunction) {
        Frisby.create('Get created context information')
            .get(QuizzesApiUrl + `/v1/contexts/${contextId}/created`)
            .addHeader('Authorization', `Token ${authToken}`)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .expectJSON(expectedJson)
            .afterJSON(function (context) {
                afterJsonFunction(context);
            })
            .toss();
    },

    getCreatedContexts: function (authToken, afterJsonFunction) {
        this.doGet('Get created contexts list', '/v1/contexts/created', 200, authToken,
            function(contexts) {
                afterJsonFunction(contexts);
            }
        );
    },

    getCollectionById: function (collectionId, authToken, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "collection", authToken, afterJsonFunction)
    },

    getAssessmentById: function (collectionId, authToken, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "assessment", authToken, afterJsonFunction)
    },

    getCollectionByIdAndType: function (collectionId, type, authToken, afterJsonFunction) {
        this.doGet(`Get the ${type} information`,
            `/v1/collections/${collectionId}?type=${type}`, 200, authToken, afterJsonFunction);
    },

    startContext: function (contextId, authToken, afterJsonFunction) {
        Frisby.create('Start Context')
            .post(QuizzesApiUrl + `/v1/contexts/${contextId}/start`)
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (startContextResult) {
                afterJsonFunction(startContextResult);
            })
            .toss()
    },

    onResourceEvent: function (contextId, resourceId, previousResource, authToken, afterJsonFunction) {
        Frisby.create('On Resource Event')
            .post(QuizzesApiUrl + `/v1/contexts/${contextId}/onResource/${resourceId}`, previousResource, {json: true})
            .addHeader('Authorization', `Token ${authToken}`)
            .inspectRequest()
            .expectStatus(204)
            .after(function () {
                afterJsonFunction();
            })
            .toss()
    },

    finishContext: function (contextId, authToken, afterJsonFunction) {
        Frisby.create('Finish Context')
            .post(QuizzesApiUrl + '/v1/contexts/' + contextId + '/finish')
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(204)
            .after(function () {
                afterJsonFunction();
            })
            .toss()
    },

    verifyGetContextEvents: function (contextId, ownerProfileId, result, afterJsonFunction) {
        Frisby.create('Get the context Events as an owner')
            .get(QuizzesApiUrl + '/v1/context/' + contextId + '/events')
            .addHeader('profile-id', ownerProfileId)
            .addHeader('lms-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .expectJSON(result)
            .afterJSON(function () {
                afterJsonFunction(afterJsonFunction);
            })
            .toss()
    },

    verifyContentNotFound: function(url, title, authToken) {
        Frisby.create(title + ' throws ContentNotFoundException')
            .get(QuizzesApiUrl + url)
            .addHeader('Authorization', 'Token ' + authToken)
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
    },

    verifyBadRequest: function(url, title, authToken) {
        Frisby.create(title + ' throws InvalidRequestException')
            .get(QuizzesApiUrl + url)
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(400)
            .inspectJSON()
            .expectJSON({
                "status": 400
            })
            .expectJSONTypes({
                message: String,
                status: Number,
                exception: String
            }).toss();
    },

    verifyInternalServerError: function(url, title){
        Frisby.create(title + ' throws Internal Server Error')
            .get(QuizzesApiUrl + url)
            .inspectRequest()
            .expectStatus(500)
            .inspectJSON()
            .expectJSON({
                "status": 500
            })
            .expectJSONTypes({
                message: String,
                status: Number,
                exception: String
            })
            .toss();
    },

    getAttempts : function(contextId, profileId, authToken, afterJsonFunction) {
        this.doGet(`Get Attempts for context ${contextId} and profile ${profileId}`,
            `/v1/attempts/contexts/${contextId}/profiles/${profileId}`, 200, authToken, afterJsonFunction);
    },

    doGet: function(description, url, expectedStatus, authToken, afterJsonFunction) {
        Frisby.create(description)
            .get(QuizzesApiUrl + url)
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(expectedStatus)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(afterJsonFunction)
            .toss()
    },

    doPost: function(description, url, body, expectedStatus, authToken, afterJsonFunction) {
        Frisby.create(description)
            .post(QuizzesApiUrl + url, body, { json: true })
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(expectedStatus)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(afterJsonFunction)
            .toss()
    },

    verifyHttpError: function (description, url, expectedStatus, authToken) {
        this.doGet(description, url, expectedStatus, authToken, function(error) {
            expect(typeof error.message).toBe('string');
            expect(typeof error.status).toBe('number');
            expect(typeof error.exception).toBe('string');
        });
    },

    verifyHttpErrorPost: function (description, url, body, expectedStatus, authToken) {
        this.doPost(`${description} returns ${expectedStatus} code`, url, body, expectedStatus, authToken, function(error) {
            expect(typeof error.message).toBe('string');
            expect(typeof error.status).toBe('number');
            expect(typeof error.exception).toBe('string');
        });
    },

    httpErrorCodes: {
        BAD_REQUEST: 400,
        UNAUTHORIZED: 401,
        FORBIDDEN: 403,
        NOT_FOUND: 404
    }
};

module.exports = quizzesCommon;

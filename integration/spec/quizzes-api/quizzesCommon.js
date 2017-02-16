const Config = require('./quizzesTestConfiguration.js');
const QuizzesApiUrl = Config.quizzesApiUrl;
const ContentProviderApiUrl = Config.contentProviderApiUrl;
const Frisby = require('Frisby');

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

    getTestUser: function (userId) {
        return testUsers[userId];
    },

    getAuthorizationToken : function(userId, afterJsonFunction) {
        var authorizationUser = Config.getUser(userId);
        console.log("Autorization user " + authorizationUser.identityId);
        Frisby.create('Gets the authorization token for ' + userId)
            .post(ContentProviderApiUrl + '/v2/authorize', {
                "client_key": "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==",
                "client_id": "ba956a97-ae15-11e5-a302-f8a963065976",
                "grant_type": "google",
                "user": {
                    "firstname": authorizationUser.firstname,
                    "lastname": authorizationUser.lastname,
                    "identity_id": authorizationUser.identityId
                }
            }, {json: true})
            .inspectRequest()
            .expectStatus(201)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(afterJsonFunction)
            .toss();
    },

    getProfileIdFromToken : function(token) {
        return Buffer(token, 'base64').toString().split(":")[1];
    },

    resolveAccessToken: function(authorizationResponse) {
        return authorizationResponse.access_token;
    },

    startTest: function (title, functionalTest) {
        console.log("\n ****** Executing Functional Test: " + title + " ****** \n");
        functionalTest();
    },

    createContext: function (afterJsonFunction) {
        this.getAuthorizationToken("TestAcc01", function (authResponse) {
            Frisby.create('Test context creation for TestAcc01')
                .post(QuizzesApiUrl + '/v1/contexts', {
                    'collectionId': '3c843308-8864-4ecd-a1c8-75ab423336f2',
                    'classId': '5d22f953-121c-485b-8043-9a96ff3ec89c',
                    'isCollection': false,
                    'contextData': {
                        'contextMap': {
                            'classId': 'class-id-1'
                        },
                        'metadata': {}
                    }
                }, {json: true})
                .addHeader('Authorization', 'Token ' + authResponse.access_token)
                .inspectRequest()
                .expectStatus(200)
                //TODO: createContext is not fully working at this point, it is returning null
                //TODO: once it is complete uncomment this two lines
//                .expectHeaderContains('content-type', 'application/json')
                .inspectJSON()
                .afterJSON(function (context) {
                    afterJsonFunction(context, authResponse);
                })
                .toss();
        })
    },

    /**
     * Creates a context passing the JSON body and the expected response as a parameter
     * @param body JSON with context information
     * @param expectedBody JSON with expected result
     * @param afterJsonFunction function to call on afterJSON
     */
    createContextWithParams: function (body, afterJsonFunction) {
        this.getAuthorizationToken("TestAcc", function (authResponse) {
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

    getAssignedContextByContextId: function (contextId, assigneeProfileId, afterJsonFunction) {
        Frisby.create('Get assigned context information')
            .get(QuizzesApiUrl + '/v1/contexts/' + contextId + '/assigned')
            .addHeader('profile-id', assigneeProfileId)
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (context) {
                afterJsonFunction(context);
            })
            .toss()
    },

    getCollectionById: function (collectionId, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "collection", afterJsonFunction)
    },

    getAssessmentById: function (collectionId, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "assessment", afterJsonFunction)
    },

    getCollectionByIdAndType: function (collectionId, type, afterJsonFunction) {
        this.getAuthorizationToken("TestAcc01", function (authResponse) {
            Frisby.create('Get the ' + type + ' information')
                .get(QuizzesApiUrl + '/v1/collections/' + collectionId + '?type=' + type)
                .addHeader('Authorization', 'Token ' + authResponse.access_token)
                .inspectRequest()
                .expectStatus(200)
                .inspectJSON()
                .afterJSON(function (collection) {
                    afterJsonFunction(collection);
                })
                .toss()
        })
    },

    startContext: function (contextId, assigneeProfileId, afterJsonFunction) {
        Frisby.create('Start Context')
            .post(QuizzesApiUrl + '/v1/contexts/' + contextId + '/start')
            .addHeader('profile-id', assigneeProfileId)
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (startContextResult) {
                afterJsonFunction(startContextResult);
            })
            .toss()
    },

    onResourceEvent: function (contextId, assigneeProfileId, resourceId, previousResource, afterJsonFunction) {
        Frisby.create('On Resource Event')
            .post(QuizzesApiUrl + '/v1/contexts/' + contextId + '/onResource/' +
                resourceId, previousResource, {json: true})
            .addHeader('profile-id', assigneeProfileId)
            .addHeader('lms-id', 'quizzes')
            .inspectRequest()
            .expectStatus(204)
            .after(function () {
                afterJsonFunction();
            })
            .toss()
    },

    finishContext: function (contextId, assigneeProfileId, afterJsonFunction) {
        Frisby.create('Finish Context')
            .post(QuizzesApiUrl + '/v1/contexts/' + contextId + '/finish')
            .addHeader('profile-id', assigneeProfileId)
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(204)
            .after(function () {
                afterJsonFunction(afterJsonFunction);
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

    verifyContentNotFound: function(url, title){
        Frisby.create(title + ' throws ContentNotFoundException')
            .get(QuizzesApiUrl + url)
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

    verifyInvalidRequest: function(url, title){
        Frisby.create(title + ' throws InvalidRequestException')
            .get(QuizzesApiUrl + url)
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
            })
            .toss();
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

    doGet: function(description, url, sessionToken, afterJsonFunction) {
        Frisby.create(description)
            .get(url)
            .addHeader('Authorization', 'Token ' + sessionToken)
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(afterJsonFunction)
            .toss()
    },

    questionTypeDemoCollection: 'ca13e08c-6e2d-4c10-93cf-7b8111f3b705'
};

module.exports = quizzesCommon;

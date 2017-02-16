const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const config = require('./quizzesTestConfiguration.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
const frisby = require('frisby');

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

    createContext: function (afterJsonFunction) {
        var contextClass = config.getClass("TestClass01");
        var collection = config.getCollection("TestCollection01")
        ContentProviderUtils.getAuthorizationToken(contextClass.owner, function (authResponse) {
            frisby.create('Test context creation for Teacher01')
                .post(QuizzesApiUrl + '/v1/contexts', {
                    'collectionId': collection.id,
                    'classId': contextClass.id,
                    'isCollection': true,
                    'contextData': {
                        'contextMap': {
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
        var contextClass = config.getClass("TestClass01");
        ContentProviderUtils.getAuthorizationToken(contextClass.owner, function (authResponse) {
            frisby.create('Test context creation using body ' + body)
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
        frisby.create('Get the profile information in Quizzes')
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
        frisby.create('Get assigned context information')
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

    getCollectionById: function (collectionId, authToken, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "collection", authToken, afterJsonFunction)
    },

    getAssessmentById: function (collectionId, authToken, afterJsonFunction) {
        this.getCollectionByIdAndType(collectionId, "assessment", authToken, afterJsonFunction)
    },

    getCollectionByIdAndType: function (collectionId, type, authToken, afterJsonFunction) {
        frisby.create('Get the ' + type + ' information')
            .get(QuizzesApiUrl + '/v1/collections/' + collectionId + '?type=' + type)
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (collection) {
                afterJsonFunction(collection);
            })
            .toss();
    },

    startContext: function (contextId, authToken, afterJsonFunction) {
        frisby.create('Start Context')
            .post(QuizzesApiUrl + '/v1/contexts/' + contextId + '/start')
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (startContextResult) {
                afterJsonFunction(startContextResult);
            })
            .toss()
    },

    onResourceEvent: function (contextId, assigneeProfileId, resourceId, previousResource, afterJsonFunction) {
        frisby.create('On Resource Event')
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

    finishContext: function (contextId, authToken, afterJsonFunction) {
        frisby.create('Finish Context')
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
        frisby.create('Get the context Events as an owner')
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
        frisby.create(title + ' throws ContentNotFoundException')
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
        frisby.create(title + ' throws InvalidRequestException')
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
        frisby.create(title + ' throws Internal Server Error')
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
        frisby.create("Get Attempts for context " + contextId + " and profile " + profileId)
            .get(QuizzesApiUrl + '/v1/attempts/contexts/' + contextId + "/profiles/" + profileId)
            .addHeader('Authorization', 'Token ' + authToken)
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function(attemptsResponse) {
                afterJsonFunction(attemptsResponse)
            })
            .toss()

    }
};

module.exports = quizzesCommon;

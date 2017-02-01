const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const NileApiUrl = require('./quizzesTestConfiguration.js').nileApiUrl;
const frisby = require('frisby');

var testUsers = {};
    testUsers["teacherqa01"] = {"firstname": "teacherqa01", "lastname": "teacherqa01", "identityId": "teacherqa01@edify.cr"};
    testUsers["teacherqa02"] = {"firstname": "teacherqa02", "lastname": "teacherqa02", "identityId": "teacherqa02@edify.cr"};
    testUsers["teacherqa03"] = {"firstname": "teacherqa03", "lastname": "teacherqa03", "identityId": "teacherqa03@edify.cr"};
    testUsers["studentqa01"] = {"firstname": "studentqa01", "lastname": "studentqa01", "identityId": "studentqa01@edify.cr"};
    testUsers["studentqa02"] = {"firstname": "studentqa02", "lastname": "studentqa02", "identityId": "studentqa02@edify.cr"};
    testUsers["studentqa03"] = {"firstname": "studentqa03", "lastname": "studentqa03", "identityId": "studentqa03@edify.cr"};
    testUsers["studentqa04"] = {"firstname": "studentqa04", "lastname": "studentqa04", "identityId": "studentqa04@edify.cr"};
    testUsers["studentqa05"] = {"firstname": "studentqa05", "lastname": "studentqa05", "identityId": "studentqa05@edify.cr"};

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
        var authorizationUser = this.getTestUser(userId);
        console.log("Autorization user " + authorizationUser.identityId);
        frisby.create('Gets the authorization token for ' + userId)
            .post(NileApiUrl + '/v1/authorize', {
                "client_key": "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==",
                "client_id": "ba956a97-ae15-11e5-a302-f8a963065976",
                "grant_type": "google",
                "return_url": "http://www.gooru.org",
                "user": {
                    "firstname": authorizationUser.firstname,
                    "lastname": authorizationUser.lastname,
                    "identity_id": authorizationUser.identityId
                }
            }, {json: true})
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (authorizationResponse) {
                afterJsonFunction(authorizationResponse);
            })
            .toss();
    },

    startTest: function (title, functionalTest) {
        console.log("\n ****** Executing Functional Test: " + title + " ****** \n");
        functionalTest();
    },

    createContext: function (afterJsonFunction) {
        frisby.create('Test context creation for one assignee and owner for start context')
            .post(QuizzesApiUrl + '/v1/contexts', {
                'externalCollectionId': 'b7af52ce-7afc-4301-959c-4342a6f941cb',
                'assignees': [
                    {
                        'id': 'student-id-1',
                        'firstName': 'StudentFirstName1',
                        'lastName': 'StudentLastName1',
                        'username': 'student1',
                        'email': 'student1@quizzes.com'
                    },
                    {
                        'id': 'student-id-2',
                        'firstName': 'StudentFirstName2',
                        'lastName': 'StudentLastName2',
                        'username': 'student2',
                        'email': 'student2@quizzes.com'
                    }
                ],
                'contextData': {
                    'contextMap': {
                        'classId': 'class-id-1'
                    },
                    'metadata': {}
                },
                'owner': {
                    'id': 'teacher-id-1',
                    'firstName': 'TeacherFirstName1',
                    'lastName': 'TeacherLastName1',
                    'username': 'teacher1',
                    'email': 'teacher1@quizzes.com'
                }
            }, {json: true})
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (context) {
                afterJsonFunction(context);
            })
            .toss();
    },

    /**
     * Creates a context passing the JSON body and the expected response as a parameter
     * @param body JSON with context information
     * @param expectedBody JSON with expected result
     * @param afterJsonFunction function to call on afterJSON
     */
    createContextWithParams: function (body, afterJsonFunction) {
        frisby.create('Test context creation using body ' + body)
            .post(QuizzesApiUrl + '/v1/contexts', body , {json: true})
            .addHeader('profile-id', '1fd8b1bc-65de-41ee-849c-9b6f339349c9')
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .expectHeaderContains('content-type', 'application/json')
            .inspectJSON()
            .afterJSON(function (context) {
                afterJsonFunction(context);
            })
            .toss();
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

    getCollectionById: function (collectionId, assigneeProfileId, afterJsonFunction) {
        frisby.create('Get the collection information')
            .get(QuizzesApiUrl + '/v1/collection/' + collectionId)
            .addHeader('profile-id', assigneeProfileId)
            .addHeader('client-id', 'quizzes')
            .inspectRequest()
            .expectStatus(200)
            .inspectJSON()
            .afterJSON(function (collection) {
                afterJsonFunction(collection);
            })
            .toss()
    },

    startContext: function (contextId, assigneeProfileId, afterJsonFunction) {
        frisby.create('Start Context')
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

    finishContext: function (contextId, assigneeProfileId, afterJsonFunction) {
        frisby.create('Finish Context')
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

    verifyContentNotFound: function(url, title){
        frisby.create(title + ' throws ContentNotFoundException')
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
        frisby.create(title + ' throws InvalidRequestException')
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
    }
};

module.exports = quizzesCommon;

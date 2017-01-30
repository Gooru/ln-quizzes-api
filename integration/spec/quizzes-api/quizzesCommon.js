const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
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

    startTest: function (title, functionalTest) {
        console.log("\n ****** Executing Functional Test: " + title + " ****** \n");
        functionalTest();
    },

    createContext: function (afterJsonFunction) {
        frisby.create('Test context creation for one assignee and owner for start context')
            .post(QuizzesApiUrl + '/v1/context', {
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
            .post(QuizzesApiUrl + '/v1/context', body , {json: true})
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
            .get(QuizzesApiUrl + '/v1/context/assigned/' + contextId)
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
    }
};

module.exports = quizzesCommon;

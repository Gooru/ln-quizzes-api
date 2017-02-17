var testUsers = {};
testUsers["Teacher01"] = {"firstname": "testuser", "lastname": "eigth", "identityId": "testuser08@edify.cr"};
testUsers["Student01"] = {"firstname": "testuser", "lastname": "nine", "identityId": "testuser09@edify.cr"};
testUsers["Student02"] = {"firstname": "testuser", "lastname": "ten", "identityId": "testuser10@edify.cr"};

var testClasses = {};
testClasses["TestClass01"] = {"id": "04805631-f43f-4364-9d40-7bdc7f1b7178", "owner": "Teacher01", "assignees": ["Student01"]};

var testCollections = {};
testCollections["TestCollection01"] = {"id": "ca13e08c-6e2d-4c10-93cf-7b8111f3b705", "owner": "Teacher01"};

var testAssessments = {};
testAssessments["TestAssessment01"] = {"id": "95e0c189-e62a-4d32-9738-eac1fae6cf3e", "owner": "Teacher01"};

var quizzesTestConfiguration = {
    quizzesApiUrl : (process.env['QUIZZES_SERVER_URL'] || 'http://localhost:8080') + '/quizzes/api',
    contentProviderApiUrl : (process.env['QUIZZES_CONTENT_PROVIDER_SERVER_URL'] || 'http://nile-qa.gooru.org')
                            + '/api/nucleus-auth',
    getUser : function(userId) {return testUsers[userId]},
    getClass : function(classId) {return testClasses[classId]},
    getCollection : function(collectionId) {return testCollections[collectionId]},
    getAssessment : function(assessmentId) {return testAssessments[assessmentId]}

};

module.exports = quizzesTestConfiguration;
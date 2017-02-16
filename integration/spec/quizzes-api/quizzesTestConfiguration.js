var testUsers = {};
testUsers["Teacher01"] = {"firstname": "Test", "lastname": "Acc", "identityId": "acc01@test.com"};
testUsers["Teacher02"] = {"firstname": "Acc", "lastname": "Test", "identityId": "acc04@test.com"};
testUsers["Student01"] = {"firstname": "Test", "lastname": "Acc", "identityId": "acc02@test.com"};
testUsers["Student02"] = {"firstname": "Acc Three", "lastname": "Test", "identityId": "acc03@test.com"};

var testClasses = {};
testClasses["TestClass01"] = {"id": "5d22f953-121c-485b-8043-9a96ff3ec89c", "owner": "Teacher01", "assignees": ["Student01"]};

var testCollections = {};
testCollections["TestCollection01"] = {"id": "b3d71a3a-e3e2-480e-96a6-da3a5097063a", "owner": "Teacher01"};

var testAssessments = {};
testAssessments["TestAssessment01"] = {"id": "13d2de39-581b-4222-8db7-d858a9ff54e3", "owner": "Teacher01"};

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
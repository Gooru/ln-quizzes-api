const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const ContentProviderUtils = require('./contentProviderUtils.js');
var frisby = require('frisby');

// "Get assessment without type parameter" and "Get assessment with invalid parameter type"
// are already covered on the getCollection_spec.js

QuizzesCommon.startTest("Get not existing assessment", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        QuizzesCommon.verifyContentNotFound('/v1/collections/' + QuizzesCommon.generateUUID() + '?type=assessment', "Get not existing assessment", authResponse.access_token);
    });
});

QuizzesCommon.startTest("Get assessment endpoint", function () {
    ContentProviderUtils.getAuthorizationToken("Teacher01", function (authResponse) {
        var assessment = Config.getAssessment("TestAssessment01");
        QuizzesCommon.getAssessmentById(assessment.id, authResponse.access_token, function(json) {
            expect(json.id).toEqual(assessment.id);
            expect(json.metadata.title).toEqual("Assessment Test # 1");
            expect(json.isCollection).toEqual(false);
            expect(json.resources.length).toEqual(9);
        });
    });
});
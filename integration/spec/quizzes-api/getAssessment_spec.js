const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');

// 'Get assessment without type parameter' and 'Get assessment with invalid parameter type'
// are already covered on the getCollection_spec.js

QuizzesCommon.startTest('Get not existing assessment', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = QuizzesCommon.generateUUID();
        QuizzesCommon.verifyContentNotFound(`/v1/collections/${assessmentId}?type=assessment`,
            'Get not existing assessment', authToken);
    });
});

QuizzesCommon.startTest('Get assessment endpoint', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment01').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken, function(json) {
            expect(json.id).toEqual(assessmentId);
            expect(json.metadata.title).toEqual('Assessment Test # 1');
            expect(json.isCollection).toEqual(false);
            expect(json.resources.length).toEqual(9);
        });
    });
});
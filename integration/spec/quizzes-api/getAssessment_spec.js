const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const HttpErrorCodes = QuizzesCommon.httpCodes;

// 'Get assessment without type parameter' and 'Get assessment with invalid parameter type'
// are already covered on the getCollection_spec.js

QuizzesCommon.startTest('Get not existing assessment', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = QuizzesCommon.generateUUID();
        QuizzesCommon.verifyHttpError('Get not existing assessment',
            `/v1/collections/${assessmentId}?type=assessment`, HttpErrorCodes.NOT_FOUND, authToken);
    });
});

QuizzesCommon.startTest('Get assessment endpoint', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let assessmentId = Config.getAssessment('TestAssessment01').id;
        QuizzesCommon.getAssessmentById(assessmentId, authToken, function(assessment) {
            expect(assessment.id).toEqual(assessmentId);
            expect(assessment.metadata.title).toEqual('Assessment Test # 1');
            expect(assessment.metadata.setting).toBeDefined();
            expect(assessment.metadata.taxonomy).toBeDefined();
            expect(assessment.isCollection).toEqual(false);
            expect(assessment.resources.length).toEqual(9);
        });
    });
});
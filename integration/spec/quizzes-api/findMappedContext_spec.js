const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const Frisby = require('frisby');
const QuizzesApiUrl = Config.quizzesApiUrl;

QuizzesCommon.startTest('Finds mapped context', function () {
    let collectionId = Config.getCollection('TestCollection01').id;
    let classId = Config.getClass('TestClass01').id;
    let courseId = QuizzesCommon.generateUUID();
    let unitId = Config.getUnit('Unit01').id;
    let lessonId = Config.getLesson('Lesson01').id;
    let contextMap = {
        courseId: courseId,
        unitId: unitId,
        lessonId: lessonId
    };
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        QuizzesCommon.createContext(collectionId, classId, true, contextMap, authToken, function (contextResponse) {
            QuizzesCommon.getAuthorizationToken('Student01', function (assigneeAuthToken) {
                Frisby.create('Finds mapped contexts')
                    .get(QuizzesApiUrl + `/v1/contexts/mapped/classes/${classId}/collections/${collectionId}` +
                        `?courseId=${courseId}&unitId=${unitId}&lessonId=${lessonId}`)
                    .addHeader('Authorization', `Token ${assigneeAuthToken}`)
                    .inspectRequest()
                    .expectStatus(200)
                    .inspectJSON()
                    .afterJSON(function (mappedContext) {
                        expect(mappedContext[0].contextId).toEqual(contextResponse.id);
                    })
                    .toss();

                Frisby.create('Verifies response error when endpoint is called by invalid Assignee')
                    .get(QuizzesApiUrl + `/v1/contexts/mapped/classes/${classId}/collections/${collectionId}` +
                        `?courseId=${courseId}&unitId=${unitId}&lessonId=${lessonId}`)
                    .addHeader('Authorization', `Token ${authToken}`)
                    .inspectRequest()
                    .expectStatus(403)
                    .inspectJSON()
                    .toss();
            });
        });
    });

});



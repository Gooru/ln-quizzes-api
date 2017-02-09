const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

var getCollectionById = function (collectionId, type) {
    return frisby.create('Get the ' + type + ' information')
        .get(QuizzesApiUrl + '/v1/collections/' + collectionId + '?type=' + type)
        .inspectRequest()
        .expectStatus(200)
        .inspectJSON();
};

var getResourceByQuestionType = function(resources, questionType) {
    return resources.filter(x => x.metadata.type == questionType)
};

var checkTrueFalseQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("True False Question");
    expect(resources[0].metadata.body).toEqual("True False Question");
    expect(resources[0].metadata.correctAnswer).toEqual([{"value": "VHJ1ZQ=="}]);
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "True",
        "isFixed": true,
        "value": "VHJ1ZQ==",
        "sequence": 1
    });
    expect(resource[0].metadata.interaction.choices).toContain({
        "text": "False",
        "isFixed": true,
        "value": "RmFsc2U=",
        "sequence": 2
    });
};


QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    getCollectionById(QuizzesCommon.questionTypeDemoAssessment, "assessment").afterJSON(function(json) {
        var resources = json.resources;

        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));

    }).toss();
});
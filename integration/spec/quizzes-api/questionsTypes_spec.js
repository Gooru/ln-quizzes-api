const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

var getResourceByQuestionType = function(resources, questionType) {
    return resources.filter(x => x.metadata.type == questionType)
};

var checkSingleChoiceQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Choice Question");
    expect(resources[0].metadata.body).toEqual("Multiple Choice Question");
    expect(resources[0].metadata.correctAnswer).toEqual([{"value": "QQ=="}]);
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(4);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "A",
        "isFixed": true,
        "value": "QQ==",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "B",
        "isFixed": true,
        "value": "Qg==",
        "sequence": 2
    });
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
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "False",
        "isFixed": true,
        "value": "RmFsc2U=",
        "sequence": 2
    });
};


QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getAssessmentById(QuizzesCommon.questionTypeDemoAssessment, function(json) {
        var resources = json.resources;

        checkSingleChoiceQuestion(getResourceByQuestionType(resources, "single_choice"));
        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));
    });
});
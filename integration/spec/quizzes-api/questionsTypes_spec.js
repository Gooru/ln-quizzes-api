const QuizzesApiUrl = require('./quizzesTestConfiguration.js').quizzesApiUrl;
const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

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
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "False",
        "isFixed": true,
        "value": "RmFsc2U=",
        "sequence": 2
    });
};

var checkDragAndDropQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Drag and Drop Question");
    expect(resources[0].metadata.body).toEqual("Drag and Drop Question");
    expect(resources[0].metadata.correctAnswer[0]).toEqual({"value": "T25l"});
    expect(resources[0].metadata.correctAnswer[1]).toEqual({"value": "VHdv"});
    expect(resources[0].metadata.correctAnswer[2]).toEqual({"value": "VGhyZWU="});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(3);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "One",
        "isFixed": true,
        "value": "T25l",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "Two",
        "isFixed": true,
        "value": "VHdv",
        "sequence": 2
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "Three",
        "isFixed": true,
        "value": "VGhyZWU=",
        "sequence": 3
    });
};


QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getAssessmentById(QuizzesCommon.questionTypeDemoAssessment, function(json) {
        var resources = json.resources;

        checkDragAndDropQuestion(getResourceByQuestionType(resources, "drag_and_drop"));
        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));
    });
});
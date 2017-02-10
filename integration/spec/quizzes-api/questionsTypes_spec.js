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

var checkTextEntryQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Fill in the Blank Question");
    expect(resources[0].metadata.body).toEqual("<span style=\"background-color: rgb(255, 255, 255);\">FIll with \"wolf\" and \"house\". The big bad [] blew down the [].</span><br>");
    expect(resources[0].metadata.correctAnswer.length).toEqual(2);
    expect(resources[0].metadata.correctAnswer[0]).toEqual({"value": "wolf"});
    expect(resources[0].metadata.correctAnswer[1]).toEqual({"value": "house"});
    expect(resources[0].metadata.interaction).not.toBeDefined();
};


QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getAssessmentById(QuizzesCommon.questionTypeDemoAssessment, function(json) {
        var resources = json.resources;

        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));
        checkTextEntryQuestion(getResourceByQuestionType(resources, "text_entry"));
    });
});
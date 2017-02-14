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

var checkMultipleChoiceQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Answer Question");
    expect(resources[0].metadata.body).toEqual("Multiple Answer Question");
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

var checkHotTextWordQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Word Question");
    expect(resources[0].metadata.body).toEqual("The big bad wolf blew down the house.");
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkHotTextSentenceQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Sentence Question");
    expect(resources[0].metadata.body).toEqual("The first little pig built his house of straw. The big bad wolf blew down the house. The second pig built his house of wood.");
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getAssessmentById(QuizzesCommon.questionTypeDemoAssessment, function(json) {
        var resources = json.resources;

        checkSingleChoiceQuestion(getResourceByQuestionType(resources, "single_choice"));
        checkMultipleChoiceQuestion(getResourceByQuestionType(resources, "multiple_choice"));
        checkDragAndDropQuestion(getResourceByQuestionType(resources, "drag_and_drop"));
        checkHotTextWordQuestion(getResourceByQuestionType(resources, "hot_text_word"));
        checkHotTextSentenceQuestion(getResourceByQuestionType(resources, "hot_text_sentence"));
        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));
    });
});
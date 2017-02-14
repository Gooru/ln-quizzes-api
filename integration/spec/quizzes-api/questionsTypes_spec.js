const QuizzesCommon = require('./quizzesCommon.js');
var frisby = require('frisby');

var SINGLE_CHOICE = "single_choice",
    MULTIPLE_CHOICE = "multiple_choice",
    DRAG_AND_DROP = "drag_and_drop",
    HOT_TEXT_WORD = "hot_text_word",
    HOT_TEXT_SENTENCE = "hot_text_sentence",
    TRUE_FALSE = "true_false",
    TEXT_ENTRY = "text_entry",
    MULTIPLE_SELECT_IMAGE = "multiple_choice_image",
    MULTIPLE_SELECT_TEXT = "multiple_choice_text";

var getResourcesByQuestionType = function(resources, questionType) {
    return resources.filter(x => x.metadata.type == questionType)
};

var checkSingleChoiceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, SINGLE_CHOICE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Choice Question");
    expect(resources[0].metadata.body).toEqual("Multiple Choice Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
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
    resources = getResourcesByQuestionType(resources, MULTIPLE_CHOICE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Answer Question");
    expect(resources[0].metadata.body).toEqual("Multiple Answer Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
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

var checkDragAndDropQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, DRAG_AND_DROP);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Drag and Drop Question");
    expect(resources[0].metadata.body).toEqual("Drag and Drop Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
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
    resources = getResourcesByQuestionType(resources, HOT_TEXT_WORD);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Word Question");
    expect(resources[0].metadata.body).toEqual("The big bad wolf blew down the house.");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkHotTextSentenceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, HOT_TEXT_SENTENCE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Sentence Question");
    expect(resources[0].metadata.body).toEqual("The first little pig built his house of straw. The big bad wolf blew down the house. The second pig built his house of wood.");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkTrueFalseQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, TRUE_FALSE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("True False Question");
    expect(resources[0].metadata.body).toEqual("True False Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
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
    resources = getResourcesByQuestionType(resources, TEXT_ENTRY);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Fill in the Blank Question");
    expect(resources[0].metadata.body).toEqual("<span style=\"background-color: rgb(255, 255, 255);\">The big bad [] blew down the [].</span><br>");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkMultipleSelectImageQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_IMAGE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Select Image Question");
    expect(resources[0].metadata.body).toEqual("Multiple Select Image Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(4);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "6504c43d-952d-476f-bc83-a3633ee3827e.png",
        "isFixed": true,
        "value": "NjUwNGM0M2QtOTUyZC00NzZmLWJjODMtYTM2MzNlZTM4MjdlLnBuZw==",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "eb324b9b-1c11-4c48-99fa-969b6e571ddf.jpg",
        "isFixed": true,
        "value": "ZWIzMjRiOWItMWMxMS00YzQ4LTk5ZmEtOTY5YjZlNTcxZGRmLmpwZw==",
        "sequence": 2
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "beb9e145-e0aa-4d7c-8169-1c3cd113b61f.png",
        "isFixed": true,
        "value": "YmViOWUxNDUtZTBhYS00ZDdjLTgxNjktMWMzY2QxMTNiNjFmLnBuZw==",
        "sequence": 3
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "372028c8-b58b-4f0b-a609-fabfb02628dc.jpg",
        "isFixed": true,
        "value": "MzcyMDI4YzgtYjU4Yi00ZjBiLWE2MDktZmFiZmIwMjYyOGRjLmpwZw==",
        "sequence": 4
    });
};

var checkMultipleSelectTextQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_TEXT);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Multiple Select Text Question");
    expect(resources[0].metadata.body).toEqual("Multiple Select Text Question");
    expect(resources[0].metadata.correctAnswer).not.toBeDefined();
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

        checkSingleChoiceQuestion(json.resources);
        checkMultipleChoiceQuestion(json.resources);
        checkDragAndDropQuestion(json.resources);
        checkHotTextWordQuestion(json.resources);
        checkHotTextSentenceQuestion(json.resources);
        checkTrueFalseQuestion(json.resources);
        checkTextEntryQuestion(json.resources);
        checkMultipleSelectImageQuestion(json.resources);
        checkMultipleSelectTextQuestion(json.resources);
    });
});
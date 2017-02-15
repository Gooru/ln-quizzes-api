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
    expect(resources[0].metadata.title).toEqual("MC - Select the correct operation result");
    expect(resources[0].metadata.body).toEqual("MC - Select the correct operation result");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "MTAwIC0gMTAgPSDCoDkwwqA="});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "100 - 10 =  90 ",
        "isFixed": true,
        "value": "MTAwIC0gMTAgPSDCoDkwwqA=",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "50 + 40 = 80 ",
        "isFixed": true,
        "value": "NTAgKyA0MCA9IDgwwqA=",
        "sequence": 2
    });
};

var checkMultipleChoiceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_CHOICE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("MA - Select all the correct results");
    expect(resources[0].metadata.body).toEqual("MA - Select all the correct results");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "NTAwICogMiA9IDEwMDAw"});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "100 * 2 = 200 ",
        "isFixed": true,
        "value": "MTAwICogMiA9IDIwMMKg",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "500 * 2 = 10000",
        "isFixed": true,
        "value": "NTAwICogMiA9IDEwMDAw",
        "sequence": 2
    });
};

var checkDragAndDropQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, DRAG_AND_DROP);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("DD - Order the number from lowest to highest");
    expect(resources[0].metadata.body).toEqual("DD - Order the number from lowest to highest");
    expect(resources[0].metadata.correctAnswer.length).toEqual(2);
    expect(resources[0].metadata.correctAnswer[0]).toEqual({"value": "MTAw"});
    expect(resources[0].metadata.correctAnswer[1]).toEqual({"value": "NTAw"});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "100",
        "isFixed": true,
        "value": "MTAw",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "500",
        "isFixed": true,
        "value": "NTAw",
        "sequence": 2
    });
};

var checkHotTextWordQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, HOT_TEXT_WORD);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("HT - Highlight Text sample (Word)");
    expect(resources[0].metadata.body).toEqual("The big bad wolf blew down the house.");
    expect(resources[0].metadata.correctAnswer.length).toEqual(2);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "big,4"});
    expect(resources[0].metadata.correctAnswer).toContain({"value": "down,22"});
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkHotTextSentenceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, HOT_TEXT_SENTENCE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("HT - Highlight Text sample (Sentence)");
    expect(resources[0].metadata.body).toEqual("The first little pig built his house of straw. The big bad wolf blew down the house.");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "The big bad wolf blew down the house.,47"});
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkTrueFalseQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, TRUE_FALSE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("TF -  Select the correct value");
    expect(resources[0].metadata.body).toEqual("TF -  Select the correct value");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "VHJ1ZQ=="});
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
    expect(resources[0].metadata.title).toEqual("FIB - Complete the sentence with the correct words");
    expect(resources[0].metadata.body).toEqual("<span style=\"background-color: rgb(255, 255, 255);\">The big bad [] blew down the [].</span><br>");
    expect(resources[0].metadata.correctAnswer.length).toEqual(2);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "wolf"});
    expect(resources[0].metadata.correctAnswer).toContain({"value": "house"});
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkMultipleSelectImageQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_IMAGE);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("MSI - Select the animals");
    expect(resources[0].metadata.body).toEqual("MSI - Select the animals");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "NThiMTcwNzYtMTRiNC00MmU3LTk3NzMtZmIzMTQ5MWMyZTRkLmpwZWc="});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "01921eb6-a4fa-4754-876e-e014ef3bba8c.png",
        "isFixed": true,
        "value": "MDE5MjFlYjYtYTRmYS00NzU0LTg3NmUtZTAxNGVmM2JiYThjLnBuZw==",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "58b17076-14b4-42e7-9773-fb31491c2e4d.jpeg",
        "isFixed": true,
        "value": "NThiMTcwNzYtMTRiNC00MmU3LTk3NzMtZmIzMTQ5MWMyZTRkLmpwZWc=",
        "sequence": 2
    });
};

var checkMultipleSelectTextQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_TEXT);

    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("MST - Select the correct definition");
    expect(resources[0].metadata.body).toEqual("MST - Select the correct definition");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toContain({"value": "PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPlJ1bjwvc3Bhbj4="});
    expect(resources[0].metadata.interaction.shuffle).toBeDefined();
    expect(resources[0].metadata.interaction.maxChoices).toBeDefined();
    expect(resources[0].metadata.interaction.prompt).toBeDefined();
    expect(resources[0].metadata.interaction.choices.length).toEqual(2);
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "<span style=\"font-size: 15px;\">Run</span>",
        "isFixed": true,
        "value": "PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPlJ1bjwvc3Bhbj4=",
        "sequence": 1
    });
    expect(resources[0].metadata.interaction.choices).toContain({
        "text": "<span style=\"font-size: 15px;\">Dog</span>",
        "isFixed": true,
        "value": "PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPkRvZzwvc3Bhbj4=",
        "sequence": 2
    });
};

QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getCollectionById(QuizzesCommon.questionTypeDemoCollection, function(json) {

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
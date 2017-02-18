const QuizzesCommon = require('./quizzesCommon.js');
const Config = require('./quizzesTestConfiguration.js');
const SINGLE_CHOICE = 'single_choice',
    MULTIPLE_CHOICE = 'multiple_choice',
    DRAG_AND_DROP = 'drag_and_drop',
    HOT_TEXT_WORD = 'hot_text_word',
    HOT_TEXT_SENTENCE = 'hot_text_sentence',
    TRUE_FALSE = 'true_false',
    TEXT_ENTRY = 'text_entry',
    MULTIPLE_SELECT_IMAGE = 'multiple_choice_image',
    MULTIPLE_SELECT_TEXT = 'multiple_choice_text',
    EXTENDED_TEXT = 'extended_text';

let getResourcesByQuestionType = function (resources, questionType) {
    return resources.filter(x => x.metadata.type == questionType)
};

let checkSingleChoiceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, SINGLE_CHOICE);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('MC - Select the correct operation result');
    expect(metadata.body).toEqual('MC - Select the correct operation result');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'MTAwIC0gMTAgPSDCoDkwwqA='});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': '100 - 10 =  90 ',
        'isFixed': true,
        'value': 'MTAwIC0gMTAgPSDCoDkwwqA=',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': '50 + 40 = 80 ',
        'isFixed': true,
        'value': 'NTAgKyA0MCA9IDgwwqA=',
        'sequence': 2
    });
};

let checkMultipleChoiceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_CHOICE);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('MA - Select all the correct results');
    expect(metadata.body).toEqual('MA - Select all the correct results');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'NTAwICogMiA9IDEwMDAw'});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': '100 * 2 = 200 ',
        'isFixed': true,
        'value': 'MTAwICogMiA9IDIwMMKg',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': '500 * 2 = 10000',
        'isFixed': true,
        'value': 'NTAwICogMiA9IDEwMDAw',
        'sequence': 2
    });
};

let checkDragAndDropQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, DRAG_AND_DROP);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('DD - Order the number from lowest to highest');
    expect(metadata.body).toEqual('DD - Order the number from lowest to highest');
    expect(metadata.correctAnswer.length).toEqual(2);
    expect(metadata.correctAnswer[0]).toEqual({'value': 'MTAw'});
    expect(metadata.correctAnswer[1]).toEqual({'value': 'NTAw'});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': '100',
        'isFixed': true,
        'value': 'MTAw',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': '500',
        'isFixed': true,
        'value': 'NTAw',
        'sequence': 2
    });
};

let checkHotTextWordQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, HOT_TEXT_WORD);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('HT - Highlight Text sample (Word)');
    expect(metadata.body).toEqual('The big bad wolf blew down the house.');
    expect(metadata.correctAnswer.length).toEqual(2);
    expect(metadata.correctAnswer).toContain({'value': 'big,4'});
    expect(metadata.correctAnswer).toContain({'value': 'down,22'});
    expect(metadata.interaction).not.toBeDefined();
};

let checkHotTextSentenceQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, HOT_TEXT_SENTENCE);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('HT - Highlight Text sample (Sentence)');
    expect(metadata.body).toEqual('The first little pig built his house of straw. The big bad wolf blew down the house.');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'The big bad wolf blew down the house.,47'});
    expect(metadata.interaction).not.toBeDefined();
};

let checkTrueFalseQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, TRUE_FALSE);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('TF -  Select the correct value');
    expect(metadata.body).toEqual('TF -  Select the correct value');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'VHJ1ZQ=='});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': 'True',
        'isFixed': true,
        'value': 'VHJ1ZQ==',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': 'False',
        'isFixed': true,
        'value': 'RmFsc2U=',
        'sequence': 2
    });
};

let checkTextEntryQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, TEXT_ENTRY);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('FIB - Complete the sentence with the correct words');
    expect(metadata.body).toEqual('<span style="background-color: rgb(255, 255, 255);">' +
        'The big bad [] blew down the [].</span><br>');
    expect(metadata.correctAnswer.length).toEqual(2);
    expect(metadata.correctAnswer).toContain({'value': 'wolf'});
    expect(metadata.correctAnswer).toContain({'value': 'house'});
    expect(metadata.interaction).not.toBeDefined();
};

let checkMultipleSelectImageQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_IMAGE);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('MSI - Select the animals');
    expect(metadata.body).toEqual('MSI - Select the animals');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'NThiMTcwNzYtMTRiNC00MmU3LTk3NzMtZmIzMTQ5MWMyZTRkLmpwZWc='});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': '01921eb6-a4fa-4754-876e-e014ef3bba8c.png',
        'isFixed': true,
        'value': 'MDE5MjFlYjYtYTRmYS00NzU0LTg3NmUtZTAxNGVmM2JiYThjLnBuZw==',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': '58b17076-14b4-42e7-9773-fb31491c2e4d.jpeg',
        'isFixed': true,
        'value': 'NThiMTcwNzYtMTRiNC00MmU3LTk3NzMtZmIzMTQ5MWMyZTRkLmpwZWc=',
        'sequence': 2
    });
};

let checkMultipleSelectTextQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, MULTIPLE_SELECT_TEXT);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('MST - Select the correct definition');
    expect(metadata.body).toEqual('MST - Select the correct definition');
    expect(metadata.correctAnswer.length).toEqual(1);
    expect(metadata.correctAnswer).toContain({'value': 'PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPlJ1bjwvc3Bhbj4='});
    expect(metadata.interaction.shuffle).toBeDefined();
    expect(metadata.interaction.maxChoices).toBeDefined();
    expect(metadata.interaction.prompt).toBeDefined();
    expect(metadata.interaction.choices.length).toEqual(2);
    expect(metadata.interaction.choices).toContain({
        'text': '<span style="font-size: 15px;">Run</span>',
        'isFixed': true,
        'value': 'PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPlJ1bjwvc3Bhbj4=',
        'sequence': 1
    });
    expect(metadata.interaction.choices).toContain({
        'text': '<span style="font-size: 15px;">Dog</span>',
        'isFixed': true,
        'value': 'PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZTogMTVweDsiPkRvZzwvc3Bhbj4=',
        'sequence': 2
    });
};

let checkExtendedTextQuestion = function (resources) {
    resources = getResourcesByQuestionType(resources, EXTENDED_TEXT);
    let metadata = resources[0].metadata;
    expect(resources.length).toEqual(1);
    expect(metadata.title).toEqual('FR- Please describe your learning objectives');
    expect(metadata.body).toEqual('FR- Please describe your learning objectives');
    expect(metadata.correctAnswer).not.toBeDefined();
    expect(metadata.interaction).not.toBeDefined();
};

QuizzesCommon.startTest('Get an assessment and check all the question types', function () {
    QuizzesCommon.getAuthorizationToken('Teacher01', function (authToken) {
        let collectionId = Config.getCollection('TestCollection01').id;
        QuizzesCommon.getCollectionById(collectionId, authToken, function (collection) {
            let resources = collection.resources;
            checkSingleChoiceQuestion(resources);
            checkMultipleChoiceQuestion(resources);
            checkDragAndDropQuestion(resources);
            checkHotTextWordQuestion(resources);
            checkHotTextSentenceQuestion(resources);
            checkTrueFalseQuestion(resources);
            checkTextEntryQuestion(resources);
            checkMultipleSelectImageQuestion(resources);
            checkMultipleSelectTextQuestion(resources);
            checkExtendedTextQuestion(resources);
        });
    });
});
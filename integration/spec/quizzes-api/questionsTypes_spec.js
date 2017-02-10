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

var checkHotTextWordQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Word Question");
    expect(resources[0].metadata.body).toEqual("The big bad wolf blew down the house.");
    expect(resources[0].metadata.correctAnswer.length).toEqual(2);
    expect(resources[0].metadata.correctAnswer[0]).toEqual({"value": "big,4"});
    expect(resources[0].metadata.correctAnswer[1]).toEqual({"value": "down,22"});
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

var checkHotTextSentenceQuestion = function (resources) {
    expect(resources.length).toEqual(1);
    expect(resources[0].metadata.title).toEqual("Highlight Sentence Question");
    expect(resources[0].metadata.body).toEqual("The first little pig built his house of straw. The big bad wolf blew down the house. The second pig built his house of wood.");
    expect(resources[0].metadata.correctAnswer.length).toEqual(1);
    expect(resources[0].metadata.correctAnswer).toEqual([{"value": "The big bad wolf blew down the house.,47"}]);
    expect(resources[0].metadata.interaction).not.toBeDefined();
};

QuizzesCommon.startTest("Get an assessment and check all the question types", function () {
    QuizzesCommon.getAssessmentById(QuizzesCommon.questionTypeDemoAssessment, function(json) {
        var resources = json.resources;

        checkHotTextWordQuestion(getResourceByQuestionType(resources, "hot_text_word"));
        checkHotTextSentenceQuestion(getResourceByQuestionType(resources, "hot_text_sentence"));
        checkTrueFalseQuestion(getResourceByQuestionType(resources, "true_false"));
    });
});
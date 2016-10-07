package com.quizzes.api.common.model;

public enum Lms {

        Gooru("gooru"),
        Quizzes("quizzes"),
        ItsLearning("its-learning");

        private String name;

        Lms(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

}

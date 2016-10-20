package com.quizzes.api.realtime.controller;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractRealTimeControllerTest {
    @Test
    public void buildCollectionUniqueId() throws Exception {
        AbstractRealTimeController controller = new AbstractRealTimeController() {
            @Override
            protected String buildCollectionUniqueId(String classId, String collectionId) {
                return super.buildCollectionUniqueId(classId, collectionId);
            }
        };
        assertEquals("cla-id_col-id", controller.buildCollectionUniqueId("cla-id", "col-id"));
    }

}
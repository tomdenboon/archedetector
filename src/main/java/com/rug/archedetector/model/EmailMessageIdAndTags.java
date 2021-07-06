package com.rug.archedetector.model;

import java.util.List;

public interface EmailMessageIdAndTags {
    String getMessageId();
    List<Tag> getTags();
}

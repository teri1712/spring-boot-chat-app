package com.decade.practice.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class EventUtils {

    public static final int EVENT_LIMIT = 20;
    public static final Sort VERSION_SORT_ASC = Sort.by(Sort.Direction.ASC, "eventVersion");
    public static final Sort VERSION_SORT_DESC = Sort.by(Sort.Direction.DESC, "eventVersion");
    public static final Sort CURRENT_SORT_DESC = Sort.by(Sort.Direction.DESC, "currentVersion");

    public static final PageRequest EVENT_VERSION_LESS_THAN_EQUAL = PageRequest.of(0, EVENT_LIMIT, VERSION_SORT_DESC);
    public static final PageRequest CURRENT_VERSION_LESS_THAN = PageRequest.of(0, 1, VERSION_SORT_DESC);

}
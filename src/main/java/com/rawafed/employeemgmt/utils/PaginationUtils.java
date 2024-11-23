package com.rawafed.employeemgmt.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public abstract class PaginationUtils {
    public final static Integer PAGE_SIZE = 10;

    public static Pageable createPageRequest(Integer page) {
        if (0 > page) {
            throw new IllegalArgumentException("Page number cannot be less than 0");
        }
        return PageRequest.of(page, PAGE_SIZE);
    }
}

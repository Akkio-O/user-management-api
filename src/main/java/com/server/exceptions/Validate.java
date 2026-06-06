package com.server.exceptions;

import org.springframework.data.domain.Pageable;

public class Validate {
    public static void validatePageable(Pageable pageable) {
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
        }
        if (pageable.getPageSize() <= 0 || pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Размер страницы должен быть от 1 до 100");
        }
    }
}

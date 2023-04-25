package com.y2gcoder.auth.auth.domain;

import java.util.UUID;

public class CodeGenerator {
    public static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

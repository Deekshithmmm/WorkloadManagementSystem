package com.wms;

import lombok.Data;

@Data
public class Test {
    private String name;

    void setName(String working) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    boolean getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

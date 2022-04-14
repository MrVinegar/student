package com.hcl.exception;

public class AgeLimitExceptiopn extends Exception {
    public AgeLimitExceptiopn() {
    }

    @Override
    public String toString() {
        return "Age cannot be less than 7 and more than 60";
    }
}

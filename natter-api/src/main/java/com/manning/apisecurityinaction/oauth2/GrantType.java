package com.manning.apisecurityinaction.oauth2;

import spark.Request;

import java.util.Set;

public interface GrantType {
    AccessDecision validate(Request request, Set<String> scope);
}

package com.sorta.service.workflow;

public interface Workflow<TRequest, TInput, TOutput> {
    TOutput run(TRequest request, TInput input);
    Boolean shouldRun(TRequest request);
}
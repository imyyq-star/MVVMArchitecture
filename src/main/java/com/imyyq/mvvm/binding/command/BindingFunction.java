package com.imyyq.mvvm.binding.command;

public interface BindingFunction<T, R> {

    R apply(T t);
}
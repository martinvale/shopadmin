package com.ibiscus.shopnchek.application;

public interface Command<T> {

	T execute();

}

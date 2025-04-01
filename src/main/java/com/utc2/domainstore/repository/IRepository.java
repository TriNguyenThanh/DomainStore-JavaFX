package com.utc2.domainstore.repository;

import java.util.ArrayList;

public interface IRepository<T> {
    public int insert(T t);

    public int update(T t);

    public int delete(T t);

    public ArrayList<T> selectAll();

    public T selectById(T t);

    public ArrayList<T> selectByCondition(String condition);
}

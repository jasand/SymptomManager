/* 
**
** Copyright 2014, Jules White
**
** 
*/
package org.coursera.capstone.syman.util;

public interface TaskCallback<T> {

    public void success(T result);

    public void error(Exception e);

}

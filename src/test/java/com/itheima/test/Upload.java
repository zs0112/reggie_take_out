package com.itheima.test;

import org.junit.jupiter.api.Test;

public class Upload {
    @Test
    public void t1(){
String fileName = "ererer.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(substring);
    }
}

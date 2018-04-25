package com.yibo.security;

import com.yibo.security.utils.SHA256Util;
import org.junit.Test;

public class SHA256Test {
    @Test
    public void test(){
        String password=SHA256Util.getSHA256("111111");
        System.out.println(password);
    }
}

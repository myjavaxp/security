package com.yibo.security;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderTests {
    @Test
    public void test(){
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String admin = encoder.encode("admin");
        System.out.println(admin);
    }
}

package com.theja.jsonsearch.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class JsonUtilsTest {

    @Test
    public void getJsonStreamFromFileOnValidFile() {
        String validFIle = "users";
        InputStream stramedJson = JsonUtils.getJsonStreamFromFile(validFIle);
        Assert.assertNotNull(stramedJson);
    }

    @Test(expected = IllegalStateException.class)
    public void getJsonStreamFromFileOnInValidFile() {
        String inValidFileName = "someR@ndoMnaM3";
        JsonUtils.getJsonStreamFromFile(inValidFileName);

    }

}
package com.comcast.xfinity.sirius.refapplication;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class BaseTest  {

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files =  file.listFiles();
            for (int i = 0; i < files.length; i++) {
                recursiveDelete(files[i]);
            }
        }
        file.delete();
    }

    @Test
    public void runningSiriusTests(){
        assertTrue("Reference Application Test Starts Here!",true);
    }
}

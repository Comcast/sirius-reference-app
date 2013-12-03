package com.comcast.xfinity.sirius.refapplication;

import com.comcast.xfinity.sirius.refapplication.sirius.SiriusImplementation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;


@RunWith(JUnit4.class)
public class ClusterTest {

    @Test
    public void testCreateConfig(){
        new SiriusImplementation().createClusterConfig("config",8080);
        new SiriusImplementation().createClusterConfig("config",8081);
        new SiriusImplementation().createClusterConfig("config",8082);
        new SiriusImplementation().createClusterConfig("config",8083);

    }

}

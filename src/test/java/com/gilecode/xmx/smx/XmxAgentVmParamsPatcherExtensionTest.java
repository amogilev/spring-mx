package com.gilecode.xmx.smx;

import org.junit.Test;

import static org.junit.Assert.*;

public class XmxAgentVmParamsPatcherExtensionTest {

    XmxAgentVmParamsPatcherExtension uut = new XmxAgentVmParamsPatcherExtension();

    @Test
    public void isSpringJar() {
        assertTrue(uut.isSpringJar("spring.jar"));
        assertTrue(uut.isSpringJar("spring.1.2.3.jar"));
        assertTrue(uut.isSpringJar("spring-core.jar"));
        assertTrue(uut.isSpringJar("spring-core.1.2.3-SNAPSHOT.jar"));
        assertTrue(uut.isSpringJar("/some/path/spring-core.jar"));
        assertTrue(uut.isSpringJar("C:\\path\\spring-core.jar"));

        assertFalse(uut.isSpringJar("C:\\path\\notspring-core.jar"));
    }
}
package com.chemaxon.bkovacs.util;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class InputDownloaderTest {


    @Test
    void testInputDownload() throws Exception {
        assertFalse(PageConnector.downloadInputLine(2022,5).isEmpty());
    }
}
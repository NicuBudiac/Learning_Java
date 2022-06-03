package com.Listener;

import com.gilat.automation.common.utils.config.ConfigReader;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class ConfigListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        ConfigReader.readSuite(suite);
    }
}
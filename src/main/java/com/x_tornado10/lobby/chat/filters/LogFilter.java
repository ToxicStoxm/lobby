package com.x_tornado10.lobby.chat.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;

public class LogFilter extends CustomFilter {
    public static List<String> blockedStrings;

    public void registerFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(this);
    }

    @Override
    protected Result logResult(String string) {

        for (String str : blockedStrings) {

            if (string.contains(str)) {
                return Result.DENY;
            }

        }

        return Result.NEUTRAL;
    }


    @Override
    public String getName() {
        return "LogFilter";
    }
}
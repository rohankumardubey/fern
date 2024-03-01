package com.fern.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class Logger implements ILogger {
    private static final Map<Class<?>, ILogger> LOGGERS = new HashMap<>();
    private static final Level DEFAULT_LEVEL = Level.INFO;
    private static final Set<String> IGNORE_METHODS = new HashSet<>();

    private static final ThreadLocal<StringBuilder> THREAD_LOCAL = new ThreadLocal<>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(255);
        }

        @Override
        public StringBuilder get() {
            StringBuilder sb = super.get();
            sb.setLength(0);
            return sb;
        }
    };


    static {
        IGNORE_METHODS.add("java.lang.Thread.getStackTrace");
        StringBuilder sb = new StringBuilder();
        sb.append(Logger.class.getName()).append(".");
        int sbRootLen = sb.length();
        for (Method method : ILogger.class.getDeclaredMethods()) {
            sb.setLength(sbRootLen);
            sb.append(method.getName());
            IGNORE_METHODS.add(sb.toString());
        }
    }

    public static final ILogger loggerFor(Class<?> clazz) {
        ILogger logger;
        synchronized (LOGGERS) {
            logger = LOGGERS.get(clazz);
            if (logger == null) {
                LOGGERS.put(clazz, logger = new Logger());
            }
        }
        return logger;
    }

    private final AtomicReference<Level> level;

    private Logger() {
        level = new AtomicReference<>(DEFAULT_LEVEL);
    }

    @Override
    public void setLevel(Level level) {
        this.level.set(level);
    }

    @Override
    public Level getLevel() {
        return level.get();
    }

    @Override
    public void log(Level level, String format, Object... args) {
        if (args == null) {
            throw new IllegalArgumentException("args");
        }
        if (getLevel().order() >= level.order()) {
            String location = null;
            StringBuilder method = THREAD_LOCAL.get();
            for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
                method.setLength(0);
                method.append(e.getClassName()).append(".").append(e.getMethodName());
                if (IGNORE_METHODS.contains(method.toString())) {
                    continue;
                }
                location = method.append("(l:").append(e.getLineNumber()).append(")").toString();
                break;
            }
            System.out.printf("%-5s <%d> Thr(%s) %s -> %s\n",
                    level,
                    TimeUnit.NANOSECONDS.toMicros(System.nanoTime()),
                    Thread.currentThread().getName(),
                    location,
                    String.format(format, args));
        }
    }

    @Override
    public final void debug(String format, Object... args) {
        log(Level.DEBUG, format, args);
    }

    @Override
    public final void info(String format, Object... args) {
        log(Level.INFO, format, args);
    }

    @Override
    public final void warn(String format, Object... args) {
        log(Level.WARN, format, args);
    }

    @Override
    public final void error(String format, Object... args) {
        log(Level.ERROR, format, args);
    }
}
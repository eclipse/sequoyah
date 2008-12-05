/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 * Fabio Fantato (Eldorado)
 *
 * Contributors:
 * Fabio Fantato (Eldorado) - do not force to show console view
 ********************************************************************************/

package org.eclipse.tml.common.utilities.logger;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * @author   Fabio Fantato
 */
public final class SimpleLogger implements ILogger
{
    private static SimpleLogger logLogger;
    /**
	 * @uml.property  name="levels"
	 * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.String"
	 */
    private static final Map<String, String> levelsMap;
    private static final String STR_SEPARATOR = "-"; //$NON-NLS-1$

    
    
    static {
    	levelsMap = new HashMap<String, String>();
        levelsMap.put(LoggerConstants.DEBUG, LoggerConstants.TXT_DEBUG);
        levelsMap.put(LoggerConstants.INFO, LoggerConstants.TXT_INFO);
        levelsMap.put(LoggerConstants.WARNING, LoggerConstants.TXT_WARNING);
        levelsMap.put(LoggerConstants.ERROR, LoggerConstants.TXT_ERROR);
        levelsMap.put(LoggerConstants.FATAL, LoggerConstants.TXT_FATAL);
        levelsMap.put(LoggerConstants.LOG_ON, LoggerConstants.TXT_ALL);
    }
    
    
    private SimpleLogger()    
    {
    	MessageConsole console = new MessageConsole("console", null); //$NON-NLS-1$
    	ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
    	MessageConsoleStream stream = console.newMessageStream();

    	System.setOut(new PrintStream(stream));
    	System.setErr(new PrintStream(stream));

    }

    /**
     * Configure the Log view
     *
     * @param logView Log view ID
     */
    private void configureLogView(int logView)
    {
        switch (logView)
        {
            case LoggerConstants.LOG_CONSOLE:
                setAppenderConsole();
                break;
            default:
                String errMsg = Integer.toString(logView);
                throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * Configure log in a specific class
     * @param classType The class object
     * @see ILogger#configureLogger(java.lang.Object)
     */
    public void configureLogger(Object classType)
    {
    	configureLogView(LoggerConstants.LOG_CONSOLE);   	
    }

    /**
     * Debug a message into logger
     * @param message a message object
     * @see ILogger#info(java.lang.Object)
     */
    public void debug(Object message)
    {
        System.out.println(LoggerConstants.TXT_DEBUG+message);
    }

    /**
     * Register an error message into logger
     * @param message a message object
     * @see ILogger#error(java.lang.Object)
     */
    public void error(Object message)
    {
    	System.out.println(LoggerConstants.TXT_ERROR+message);
    }

    /**
     * Register an error message into logger includding the throwable
     * @param message a message object
     * @param throwable a throwable object
     * @see ILogger#error(java.lang.Object,
     *      java.lang.Object)
     */
    public void error(Object message, Object throwable)
    {
    	System.out.println(LoggerConstants.TXT_ERROR+message+STR_SEPARATOR+((Throwable)throwable).getMessage());
    }

    /**
     * (non-Javadoc)
     *
     * @see ILogger#fatal(java.lang.Object)
     */
    public void fatal(Object message)
    {
    	System.out.println(LoggerConstants.TXT_FATAL+message);
    }

    /**
     * Get the current log level
     *
     * @return the current log level
     */
    public Object getCurrentLevel()
    {
        return LoggerConstants.TXT_ALL;
    }

    /**
     * Get instance of logger
     *
     * @return logger
     */
    public static SimpleLogger getInstance()
    {
        if (logLogger == null)
        {
            logLogger = new SimpleLogger();
        }

        return logLogger;
    }

    /**
     * get Message info
     * @param message a message object
     * @see ILogger#info(java.lang.Object)
     */
    public void info(Object message)
    {
    	System.out.println(LoggerConstants.TXT_INFO+message);
    }

    /**
     * Set a message log with priority and throwable
     * @param priority priority object defined in levelsMap
     * @param message a message object
     * @param throwable a throwable object
     * @see ILogger#log(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void log(Object priority, Object message, Object throwable)
    {
    	System.out.println(levelsMap.get(priority)+message+STR_SEPARATOR+((Throwable)throwable).getMessage());
    }

    /**
     * @param throwable
     * @see ILogger#log(java.lang.Object)
     */
    public void log(Object throwable)
    {
    	System.out.println(LoggerConstants.TXT_ALL+((Throwable)throwable).getMessage());
    }

    /**
     * Set a message with priority
     * @param priority priority object defined in levelsMap
     * @param message a message object 
     * @see ILogger#log(java.lang.Object,
     *      java.lang.Object)
     */
    public void log(Object priority, Object message)
    {
    	System.out.println(levelsMap.get(priority)+message);
    }

    /**
     * Sets a Appender Console
     */
    private void setAppenderConsole()
    {
    }

    /**
     * Sets a Appender File
     */
	private void setAppenderFile()
    {
    }

    /**
     * Sets a Appender HTML File
     */
   
	private void setAppenderHTML()
    {
    }

    /**
     * Sets the level of log
     * @param level Level object
     * @see ILogger#setLevel(java.lang.Object)
     */
    public void setLevel(Object level)
    {
    }

    /**
     * Sets the log view in console
     */
    public void setLogToConsole()
    {
    }

    /**
     * Sets the log in a file
     *
     * @param filename the file name
     */
    public void setLogToFile(String filename)
    {
    }

    /**
     * Write the log into a file
     *
     * @param filename the file name
     * @param threshold level of message
     */
    public void setLogToFile(String filename, String threshold)
    {
       
    }

    /**
     * Sets the log in a HTML file
     *
     * @param filename the HTML file name
     */
    public void setLogToHTMLFile(String htmlFilename)
    {
    }

    /**
     * Sets a message in log with warn level
     * @param message message object
     * @see ILogger#warn(java.lang.Object)
     */
    public void warn(Object message)
    {
    	System.out.println(LoggerConstants.TXT_WARNING+message);
    }
}

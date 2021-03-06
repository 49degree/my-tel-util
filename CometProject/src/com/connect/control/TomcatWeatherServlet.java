package com.connect.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class TomcatWeatherServlet extends HttpServlet implements CometProcessor {
	static Logger logger = Logger.getLogger(TomcatWeatherServlet.class);
    private MessageSender messageSender = null;
    private static final Integer TIMEOUT = 60 * 1000;

    @Override
    public void destroy() {
        messageSender.stop();
        messageSender = null;

    }

    @Override
    public void init() throws ServletException {
    	 logger.debug("init ++++++++++++++++++++++++++++++");
//        messageSender = new MessageSender();
//        Thread messageSenderThread =
//                new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
//        messageSenderThread.setDaemon(true);
//        messageSenderThread.start();
        logger.debug("init ++++++++++++++++++++++++++++++");

    }
    
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
	}
	SendMessage sendMessage = null;
	ReadMessage readMessage = null;
    public void event(final CometEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
        	logger.debug("BEGIN for session: " + request.getSession(true).getId());
            request.setAttribute("org.apache.tomcat.comet.timeout", TIMEOUT);
//            messageSender.setConnection(response);
//            Weatherman weatherman = new Weatherman(95118, 32408);
//            weatherman.start();
            sendMessage = new SendMessage(response);
            sendMessage.setDaemon(true);
            sendMessage.start();
            
            
            readMessage = new ReadMessage(request);
            readMessage.setDaemon(true);
            readMessage.start();
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
            logger.debug("Error for session");
            sendMessage.stopFlag = true;
            sendMessage.interrupt();
            
            readMessage.stopFlag = true;
            readMessage.interrupt();
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.END) {
            logger.debug("End for session: " + request.getSession(true).getId());
            sendMessage.stopFlag = true;
            sendMessage.interrupt();
            
            readMessage.stopFlag = true;
            readMessage.interrupt();
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.READ) {
            sendMessage.stopFlag = true;
            sendMessage.interrupt();
            
            readMessage.stopFlag = true;
            readMessage.interrupt();
            throw new UnsupportedOperationException("This servlet does not accept data");
        }

    }
    
    
    private class ReadMessage extends Thread{
    	HttpServletRequest request=null;
    	BufferedReader reader = null;
    	boolean stopFlag = false;
    	char[] cBuffer = new char[1024];
    	public ReadMessage(HttpServletRequest request){
    		this.request = request;
    		try{
    			reader = request.getReader();
    		}catch(Exception e){
    			
    		}
    	}
    	public void run(){
    		int len = 0;
    		while(!stopFlag){
        		try{
        			logger.debug("begin:"+System.currentTimeMillis());
        			len = reader.read(cBuffer);
        			logger.debug("end:"+System.currentTimeMillis());
        			
        			sleep(1000);
        		}catch(Exception e){
        			
        		}
        		if(len>0)
        			logger.debug(new String(cBuffer,0,len));
        		
    		}
    	}
    }
    
    private class SendMessage  extends Thread{
    	HttpServletResponse response=null;
    	PrintWriter writer = null;
    	boolean stopFlag = false;
    	public SendMessage (HttpServletResponse response){
    		this.response = response;
    		try{
    			writer = response.getWriter();
    		}catch(Exception e){
    			
    		}
    	}
    	public void run(){
    		while(!stopFlag){
    			writer.write("test");
    			writer.flush();
        		try{
        			sleep(1000);
        		}catch(Exception e){
        			
        		}
    		}
    	}
    }    
    
    

private class Weatherman {

    private final List<URL> zipCodes;
    private final String YAHOO_WEATHER = "http://weather.yahooapis.com/forecastrss?p=";

    public Weatherman(Integer... zips) {
        zipCodes = new ArrayList<URL>(zips.length);
        for (Integer zip : zips) {
            try {
                zipCodes.add(new URL(YAHOO_WEATHER + zip));
            } catch (Exception e) {
                // dont add it if it sucks
            }
        }
    }

    public void start() {
        Runnable r = new Runnable() {

            public void run() {
                int i = 0;
                while (i >= 0) {
                    int j = i % zipCodes.size();
                    SyndFeedInput input = new SyndFeedInput();
                    try {
                        SyndFeed feed = input.build(new InputStreamReader(zipCodes.get(j).openStream()));
                        SyndEntry entry = (SyndEntry) feed.getEntries().get(0);
                        messageSender.send(entryToHtml(entry));
                        Thread.sleep(30000L);
                    } catch (Exception e) {
                        // just eat it, eat it
                    }
                    i++;
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
    private String entryToHtml(SyndEntry entry){
        StringBuilder html = new StringBuilder("<h2>");
        html.append(entry.getTitle());
        html.append("</h2>");
        html.append(entry.getDescription().getValue());
        return html.toString();
    }
}



   private class MessageSender implements Runnable {

        protected boolean running = true;
        protected final ArrayList<String> messages = new ArrayList<String>();
        private ServletResponse connection;

        private synchronized void setConnection(ServletResponse connection){
            this.connection = connection;
            notify();
        }

        public void stop() {
            running = false;
        }

        /**
         * Add message for sending.
         */
        public void send(String message) {
            synchronized (messages) {
                messages.add(message);
                logger.debug("Message added #messages=" + messages.size());
                messages.notify();
            }
        }

        public void run() {
            while (running) {
                if (messages.size() == 0) {
                    try {
                        synchronized (messages) {
                            messages.wait();
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
                String[] pendingMessages = null;
                synchronized (messages) {
                    pendingMessages = messages.toArray(new String[0]);
                    messages.clear();
                }
                try {
                    if (connection == null){
                        try{
                            synchronized(this){
                                wait();
                            }
                        } catch (InterruptedException e){
                            // Ignore
                        }
                    }
                    PrintWriter writer = connection.getWriter();
                    for (int j = 0; j < pendingMessages.length; j++) {
                        final String forecast = pendingMessages[j] + "<br>";
                        writer.println(forecast);
                        logger.debug("Writing:" + forecast);
                    }
                    writer.flush();
                    //writer.close();
                    connection = null;
                    logger.debug("Closing connection");
                } catch (IOException e) {
                    logger.debug("IOExeption sending message", e);
                }
            }
        }
    }
}

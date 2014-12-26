package com.yangxp.rtsp;

import java.io.IOException;
import java.io.Serializable;

public interface RtspSession extends Serializable{
		public String getSessionId(); 
		/** 
		 * Returns a Session Description that can be stored in a file or sent to a client with RTSP.
		 * @return The Session Description
		 * @throws IllegalStateException
		 * @throws IOException
		 */
		public String getSessionDescription() throws IllegalStateException, IOException;
}

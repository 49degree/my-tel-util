/***********************************************************************
 * Module:  Client.java
 * Author:  Administrator
 * Purpose: Defines the Interface Client
 ***********************************************************************/

package com.a3650.posserver.core.init;

import java.io.IOException;

/** @pdOid 5a5d3dcd-fc24-49c8-9322-81c0ae85c355 */
public interface Client {
   /**
    * 接收数据
    * @throws IOException 
    **/
   byte[] receiveData() throws IOException;
   
   /** @param buffer
    *返回数据
    * @throws IOException 
    **/
   boolean returnData(byte[] buffer) throws IOException;
   
   /**
    * 关闭连接
    * @throws IOException 
    **/
   public boolean close();
   
	public interface CloseListener{
		public void close();
	}
}
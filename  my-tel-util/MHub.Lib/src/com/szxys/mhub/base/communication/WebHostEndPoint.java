package com.szxys.mhub.base.communication;

/**
 * 服务器终结点。
 */
public class WebHostEndPoint {
	/**
	 * 服务器终结点的 网际协议 (IP) 地址或 Webservice 地址。
	 */
	public String Adress;

	/**
	 * 服务器终结点的 端口号。
	 */
	public int Port;

	/**
	 * 用指定的地址初始化 WebHostEndPoint 类的新实例。
	 * 
	 * @param adress
	 *            ：网际协议 (IP + Port) 地址或 Webservice 地址。
	 */
	public WebHostEndPoint(String adress) {
		if (adress.toLowerCase().startsWith("http", 0)) {
			this.Adress = adress;
			this.Port = 0;
		} else {
			int index = adress.lastIndexOf(':');
			if (index > -1) {
				try {
					this.Adress = adress.substring(0, index);
					this.Port = Integer.parseInt(adress.substring(index + 1));
				} catch (Exception e) {
					throw new IllegalArgumentException(
							"The parameter is invalid.", e);
				}
			} else {
				throw new IllegalArgumentException("The parameter is invalid.");
			}
		}
	}

	/**
	 * 用指定的地址和端口号初始化 WebHostEndPoint 类的新实例。
	 * 
	 * @param adress
	 *            ：网际协议 (IP) 地址或 Webservice 地址。
	 * @param port
	 *            ：与网际协议 (IP) 关联的端口号。
	 */
	public WebHostEndPoint(String adress, int port) {
		this.Adress = adress;
		this.Port = port;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof WebHostEndPoint)) {
			return false;
		}

		WebHostEndPoint ep = (WebHostEndPoint) o;
		return ep.Adress.equals(this.Adress) && ep.Port == this.Port;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + this.Port;
		hashCode = 31 * hashCode
				+ (this.Adress != null ? this.Adress.hashCode() : 0);
		return hashCode;
	}

	@Override
	public String toString() {
		return this.Port == 0 ? this.Adress : (this.Adress + ":" + this.Port);
	}
}
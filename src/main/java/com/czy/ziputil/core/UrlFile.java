package com.czy.ziputil.core;

import java.net.URL;

public class UrlFile{
	private String path;
	private URL url;
	public UrlFile(String path) throws Exception{
		this.path=path;
		this.url=new URL(this.path);
	}
	public String getPath() {
		return path;
	}
	
	public URL getURL() {
		return url;
	}
	
}

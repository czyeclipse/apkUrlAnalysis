package com.czy.ziputil.core;

import java.net.HttpURLConnection;

public class Utils{

	public static String getFileNameFromHeader(HttpURLConnection conn) {
		String content=conn.getHeaderField("Content-Disposition");
		if(content==null||"".equals(content)) {
			return null;
		}
		String[] splits=content.split(";");
		for(String split:splits) {
			split=split.trim();
			int index=split.indexOf("=");
			if(index!=-1) {
				String key=split.substring(0, index);
				if("filename".equals(key)) {
					return split.substring(index+1);
				}
			}
		}
		return null;
	}
	
	public static void printBytes(byte[] bs,int offset,int size) {
		int end=offset+size;
		if(end>bs.length) {
			end=bs.length;
		}
		for(int i=offset;i<end;i++) {
			System.out.print(bs[i]+"\t");
			if(i%10==9) {
				System.out.println();
			}
		}
		System.out.println();
	}
}

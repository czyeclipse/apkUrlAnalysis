package com.czy.ziputil.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TestRandomAccessFile extends RandomAccessFile{

	private long currentPos;
	public TestRandomAccessFile(File file,String mode) throws FileNotFoundException{
		super(file, mode);
	}
	public void seek(long pos) throws IOException{
		if(count<100) {
			System.out.println("pos:"+pos);
		}
		this.currentPos=pos;
		super.seek(pos);
	}
	int count=0;
	@Override
	public int read(byte[] b,int off,int len) throws IOException{
//		if(count<1000) {
//			System.out.println("read access file,off:"+off+",len:"+len+",pos:"+this.currentPos+",bslist:0,count:"+count++);
//		}
		int result=super.read(b, off, len);
		count++;
		if(count<100) {
			System.out.println("count:"+count);
			Utils.printBytes(b, off, len);
		}
		return result;
	}
}

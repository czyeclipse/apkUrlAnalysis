package com.czy.ziputil.core;

import java.io.ByteArrayOutputStream;

public class ByteBlockEntry{

	public static final int TYPE_NO=1;
	public static final int TYPE_YES=2;
	public static final int TYPE_NEED=3;
	
	private long start;
	private long end;
	private int type=TYPE_NO;
	private ByteArrayOutputStream bs;
	
	public ByteBlockEntry(long start,long end,int type) {
		this.start=start;
		this.end=end;
		this.type=type;
		bs=new ByteArrayOutputStream();
	}
	
	public ByteBlockEntry(long start,byte[] bytes) {
		this.start=start;
		this.end=this.start+bytes.length;
		bs=new ByteArrayOutputStream();
		bs.write(bytes, 0, bytes.length);
		this.type=TYPE_YES;
	}
	public boolean isOk() {
		return this.type==TYPE_YES;
	}
	public long getStart(){
		return start;
	}
	public void setStart(long start){
		this.start=start;
	}
	public long getEnd(){
		return end;
	}
	public void setEnd(long end){
		this.end=end;
	}
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type=type;
	}

	public ByteArrayOutputStream getBs(){
		return bs;
	}
	public byte[] getBsArray() {
		return this.bs.toByteArray();
	}
	public void setBs(ByteArrayOutputStream bs){
		this.bs=bs;
	}
	public int getLength() {
		return (int)(end-start);
	}
	public void appendBS(byte[] bytes) {
		bs.write(bytes,0,bytes.length);
		this.end+=bytes.length;
	}
	public void initBS(byte[] bytes) {
		bs.write(bytes,0,bytes.length);
		this.type=TYPE_YES;
	}
	public void beforeBS(byte[] bytes) throws Exception{
		this.start-=bytes.length;
		ByteArrayOutputStream newBS=new ByteArrayOutputStream();
		newBS.write(bytes);
		newBS.write(bs.toByteArray());
		bs=newBS;
	}
	
	
	
	@Override
	public String toString(){
		return "start:"+start+",end:"+end+",len:"+this.getLength();
	}
	
}

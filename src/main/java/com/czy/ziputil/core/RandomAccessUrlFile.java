package com.czy.ziputil.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RandomAccessUrlFile extends RandomAccessFile{

	private static final int READ_BLOCK_SIZE=8192*2;
	
	private UrlFile urlFile;
	private long currentPos=-1;
	private HttpURLConnection urlConn=null;
	private long totalLength=-1;
	private List<ByteBlockEntry> bsList=null;
	private ByteBlockEntry currentBlock=null;
	
	public RandomAccessUrlFile(UrlFile file) throws Exception{
		super(File.createTempFile("zip", ".temp"), "r");
		this.urlFile=file;
		this.bsList=new ArrayList<ByteBlockEntry>();
		this.initFileInfo();
		this.bsList.add(new ByteBlockEntry(0,this.totalLength,ByteBlockEntry.TYPE_NO));
		
	}
	private void initFileInfo() throws Exception{
		this.urlConn=(HttpURLConnection)this.urlFile.getURL().openConnection();
		this.totalLength=this.urlConn.getContentLengthLong();
//		this.fileName=Utils.getFileNameFromHeader(this.urlConn);
		this.urlConn.disconnect();
	}
	
	@Override
	public void seek(long pos) throws IOException{
		currentPos=pos;
	}
	
	@Override
	public long length() throws IOException{
		return this.totalLength;
	}
	
	@Override
	public long getFilePointer() throws IOException{
		return this.currentPos;
	}
	
	@Override
	public int read(byte[] b) throws IOException{
		return read(b,0,b.length);
	}
	
	private RandomAccessFile tempRaf=null;
	private int readForTest(byte[] b,int off,int len) throws IOException{
		if(tempRaf==null) {
			tempRaf=new RandomAccessFile("","r");
		}
		tempRaf.seek(this.currentPos);
		int n=tempRaf.read(b,off,len);
		this.currentPos+=n;
		return n;
	}
	private int readForTest(long range,byte[] b,int off,int len) throws IOException{
		if(tempRaf==null) {
			tempRaf=new RandomAccessFile("","r");
		}
		tempRaf.seek(range);
		int n=tempRaf.read(b,off,len);
		return n;
	}
	@Override
	public int read(byte[] b,int off,int len) throws IOException{
//		return readForTest(b, off, len);
		try{
			long l=System.currentTimeMillis();
			ByteBlockEntry entry=getBlockEntry(this.currentPos, len);
			int start=(int)(this.currentPos-entry.getStart());
			byte[] tempBS=entry.getBsArray();
			int end=off+len;
			if(end>b.length) {
				end=b.length;
			}
			int llen=end-off;
			if(this.currentPos+llen>entry.getEnd()) {
				llen=(int)(entry.getEnd()-this.currentPos);
			}
			end=off+llen;
			for(int i=off;i<end;i++) {
				b[i]=tempBS[start++];
			}
			this.currentPos+=llen;
//			long tempT=System.currentTimeMillis()-l;
//			if(tempT>10) {
//				System.out.println("usedTime:"+tempT);
//			}
			return llen;
		}catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException();
		}
	}
	private long t=0;
	private ByteBlockEntry getBlockEntry(long pos,int len) throws Exception{
		long end=pos+len;
		int startIndex=-1;
		int endIndex=-1;
		if(t==0) {
			t=System.currentTimeMillis();
		}
		for(int i=0;i<this.bsList.size();i++) {
			ByteBlockEntry entry=this.bsList.get(i);
			if(entry.getStart()<=pos&&entry.getEnd()>pos) {
				startIndex=i;
			}
			if(entry.getStart()<end&&entry.getEnd()>=end) {
				endIndex=i;
			}
			if(startIndex!=-1&&endIndex!=-1) {
				break;
			}
		}
		if(startIndex==endIndex) {
			ByteBlockEntry entry=this.bsList.get(startIndex);
			if(entry.isOk()) {
//				long tempT=System.currentTimeMillis()-t;
//				if(tempT>0)System.out.println("bsListSize:"+this.bsList.size()+",usedTime:"+tempT);
//				t=0;
				return entry;
			}else {
				long blockStart=entry.getStart();
				long blockEnd=entry.getEnd();
				int insertIndex=startIndex+1;
				if(blockStart==pos) {
					entry.setEnd(end);
					entry.setType(ByteBlockEntry.TYPE_NEED);
				}else {
					entry.setEnd(pos);
					entry=new ByteBlockEntry(pos,end,ByteBlockEntry.TYPE_NEED);
					this.bsList.add(insertIndex++, entry);
					endIndex++;
				}
				if(end<blockEnd) {
					entry=new ByteBlockEntry(end,blockEnd,ByteBlockEntry.TYPE_NO);
					this.bsList.add(insertIndex, entry);
				}
			}
		}else {
			for(int i=startIndex+1;i<endIndex;i++) {
				ByteBlockEntry entry=this.bsList.get(i);
				if(!entry.isOk()) {
					this.bsList.get(i).setType(ByteBlockEntry.TYPE_NEED);
				}
			}
			ByteBlockEntry entry=this.bsList.get(startIndex);
			if(!entry.isOk()) {
				long blockEnd=entry.getEnd();
				if(entry.getStart()==pos) {
					this.bsList.remove(startIndex);
					startIndex--;
					endIndex--;
				}else {
					entry.setEnd(pos);
				}
				entry=new ByteBlockEntry(pos,blockEnd,ByteBlockEntry.TYPE_NEED);
				this.bsList.add(startIndex+1, entry);
				endIndex++;
			}
			entry=this.bsList.get(endIndex);
			if(!entry.isOk()) {
				long blockEnd=entry.getEnd();
				entry.setEnd(end);
				entry.setType(ByteBlockEntry.TYPE_NEED);
				if(blockEnd!=end) {
					entry=new ByteBlockEntry(end,blockEnd,ByteBlockEntry.TYPE_NO);
					this.bsList.add(endIndex+1, entry);
				}
			}
		}
		
		for(int i=startIndex;i<=endIndex;i++) {
			ByteBlockEntry entry=this.bsList.get(i);
			if(entry.getType()==ByteBlockEntry.TYPE_NEED) {
				int blockLen=entry.getLength();
				if(blockLen<READ_BLOCK_SIZE) {
					int remainLen=READ_BLOCK_SIZE-blockLen;
					long leftStart=entry.getStart()-remainLen;
					for(int j=i-1;j>=0;j--) {
						ByteBlockEntry leftBlock=this.bsList.get(j);
						if(leftBlock.getType()!=ByteBlockEntry.TYPE_NO) {
							break;
						}
						if(leftBlock.getStart()==leftStart) {
							entry.setStart(leftStart);
							this.bsList.remove(j);
							remainLen=0;
							endIndex--;
							i--;
							break;
						}else if(leftBlock.getStart()<leftStart) {
							entry.setStart(leftStart);
							leftBlock.setEnd(leftStart);
							remainLen=0;
							break;
						}else {
							leftBlock.setType(ByteBlockEntry.TYPE_NEED);
							remainLen-=leftBlock.getLength();
						}
					}
					if(remainLen>0) {
						long rightEnd=entry.getEnd()+remainLen;
						for(int j=i+1;j<this.bsList.size();j++) {
							ByteBlockEntry rightBlock=this.bsList.get(j);
							if(rightBlock.getType()!=ByteBlockEntry.TYPE_NO) {
								break;
							}
							if(rightBlock.getEnd()==rightEnd) {
								entry.setEnd(rightEnd);
								this.bsList.remove(j);
								endIndex--;
								break;
							}else if(rightBlock.getEnd()>rightEnd) {
								entry.setEnd(rightEnd);
								rightBlock.setStart(rightEnd);
								break;
							}else {
								rightBlock.setType(ByteBlockEntry.TYPE_NEED);
								remainLen-=rightBlock.getLength();
							}
						}
					}
				}
			}
		}
		int i=0;
		while(i<this.bsList.size()) {
			ByteBlockEntry block1=this.bsList.get(i);
			if(i+1<this.bsList.size()) {
				ByteBlockEntry block2=this.bsList.get(i+1);
				if(block1.getType()==block2.getType()) {
					if(block1.isOk()) {
						block1.appendBS(block2.getBsArray());
					}else {
						block1.setEnd(block2.getEnd());
					}
					this.bsList.remove(i+1);
				}else {
					i++;
				}
			}else {
				i++;
			}
			if(block1.getType()==ByteBlockEntry.TYPE_NEED) {
				byte[] bs=readFromUrl(block1.getStart(), block1.getLength());
				block1.initBS(bs);
				i-=2;
				if(i<0) {
					i=0;
				}
			}
		}
		return getBlockEntry(pos,len);
	}
	
	private byte[] readFromUrl(long range,int len) throws Exception{
//		byte[] bs=new byte[len];
//		readForTest(range, bs, 0, len);
//		return bs;
		InputStream is=null;
//		long l=System.currentTimeMillis();
		try {
//			System.out.println("readFromUrl,range:"+range+",len:"+len);
			this.urlConn=(HttpURLConnection)this.urlFile.getURL().openConnection();
			this.urlConn.setRequestProperty("Range", "bytes="+range+"-");
			this.urlConn.connect();
			int responseCode=this.urlConn.getResponseCode();
			if(responseCode==200||responseCode==206) {
				is=this.urlConn.getInputStream();
				if(len<=READ_BLOCK_SIZE) {
					byte[] b=new byte[len];
					int n=0;
					int m=0;
					int leftlen=len;
					while(leftlen>0&&(n=is.read(b, m, leftlen))!=-1) {
						m+=n;
						leftlen=len-m;
					}
					return b;
				}else {
					ByteArrayOutputStream bs=new ByteArrayOutputStream();
					byte[] b=new byte[READ_BLOCK_SIZE];
					while(len>0) {
						int n=is.read(b);
						if(n==-1) {
							break;
						}
						bs.write(b, 0, n);
						len-=n;
					}
					return bs.toByteArray();
				}
			}else {
				return null;
			}
		}catch(Exception ex) {
			throw ex;
		}finally {
			if(is!=null) {
				is.close();
			}
			if(this.urlConn!=null) {
				this.urlConn.disconnect();
			}
//			System.out.println("readFromUrl,range:"+range+",len:"+len+",usedTime:"+(System.currentTimeMillis()-l));
		}
		
	}
	public static void main(String[] args)throws Exception{
		String apkUrl="";
		File file=new File("");
		if(file.exists()) {
			file.delete();
		}
		URL url=new URL(apkUrl);
		HttpURLConnection conn=null;
		InputStream is=null;
		RandomAccessFile raf=null;
		try {
			raf=new RandomAccessFile(file,"rw");
			conn=(HttpURLConnection)url.openConnection();
			is=conn.getInputStream();
			long size=conn.getContentLengthLong();
			String fileName=Utils.getFileNameFromHeader(conn);
			System.out.println("size:"+size+",filename="+fileName);
			byte[] bs=new byte[4096];
			int n=0;
			long readedSize=0;
			while((n=is.read(bs))!=-1) {
				raf.write(bs, 0, n);
				readedSize+=n;
				if(readedSize>20455890) {
					break;
				}
			}
			is.close();
			conn.disconnect();
			conn=(HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Range", "bytes="+readedSize+"-");
			is=conn.getInputStream();
			while((n=is.read(bs))!=-1) {
				raf.write(bs, 0, n);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(is!=null) {
				is.close();
			}
			if(raf!=null) {
				raf.close();
			}
		}
	}
	
}

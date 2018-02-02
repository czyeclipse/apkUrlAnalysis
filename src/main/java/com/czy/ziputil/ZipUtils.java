package com.czy.ziputil;

import java.io.InputStream;

import com.czy.ziputil.core.AXMLPrinter;
import com.czy.ziputil.core.ApkInfo;
import com.czy.ziputil.core.ZipUrlFile;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * Hello world!
 *
 */
public class ZipUtils{
	public static ApkInfo getApkInfo(String apkUrl) throws Exception{
		ZipUrlFile zipUrlFile=new ZipUrlFile(apkUrl);
		FileHeader headerManifest=zipUrlFile.getFileHeader("AndroidManifest.xml");
		InputStream is=zipUrlFile.getInputStream(headerManifest);
		ApkInfo apkInfo=AXMLPrinter.parserStreamToApkInfo(is);
		return apkInfo;
	}
	
	
	
	public static void main(String[] args)throws Exception{
//		String apkPath="/Users/chenzhiyong/Downloads/2017_com.tencent.tmgp.sgame_h100_1.32.1.10.apk";
//		String apkPath="http://res.yeshen.com/appstore/apk/2018/01/06/QQfeiche1515221340592.apk";
		
//		String toFile="/Users/chenzhiyong/Desktop/xiaoxiaoxunlongshi_yueyoufenbao.apk";
		
		

		
		
//		String toFile="/Users/chenzhiyong/Desktop/"+header.getFileName();
//		ZipInputStream is=zipFile.getInputStream(header);
//		OutputStream output=new FileOutputStream(new File(toFile));
		
//		funLocal(toFile);
		funUrl();
//		funApkTool();
	}
	public static void funUrl()throws Exception{
		long l=System.currentTimeMillis();
//		String apkPath="http://bignoxtest.oss-cn-beijing.aliyuncs.com/gamers/gamerapps/NoxGamer_v2.0.1.apk";
//		String apkPath="http://10.8.1.212:10093/group1/M00/83/65/CggB01poDbSAMdFzKV9iNY5kUT4205.apk?filename=wangzherongyao.apk";
		String apkPath="http://blcdn.nikkigames.cn/sdk/poster/apk/papegames-1.0.1211-16-dist-release.apk";
		ZipUrlFile zipUrlFile=new ZipUrlFile(apkPath);
//		List<?> headerList=zipUrlFile.getFileHeaders();
//		for(int i=0;i<headerList.size();i++) {
//			FileHeader header=(FileHeader)headerList.get(i);
//			System.out.println(header.getFileName());
//			
//		}
		
//		
//		FileHeader headerResource=zipUrlFile.getFileHeader("resources.arsc");
//		BufferedInputStream bfi=new BufferedInputStream(zipUrlFile.getInputStream(headerResource));
//		ResTable resTable = new ResTable();
//		ResPackage[] resPackage=ARSCDecoder.decode(bfi, false, false, resTable).getPackages();
		FileHeader headerManifest=zipUrlFile.getFileHeader("AndroidManifest.xml");
//		
//		
		System.out.println("usedTime:"+(System.currentTimeMillis()-l));
		InputStream is=zipUrlFile.getInputStream(headerManifest);
		ApkInfo apkInfo=AXMLPrinter.parserStreamToApkInfo(is);
		if(apkInfo!=null) {
			System.out.println(apkInfo.toString());
		}else {
			System.out.println("error");
		}
		
//		AXmlResourceParser parser=new AXmlResourceParser();
//		parser.open(is);
//		System.out.println(AXMLPrinter.parserStream(is));
	}
	public static void funLocal(String apkPath) throws Exception{
		ZipFile zipFile=new ZipFile(apkPath);
		FileHeader header=zipFile.getFileHeader("AndroidManifest.xml");
		ApkInfo apkInfo=AXMLPrinter.parserStreamToApkInfo(zipFile.getInputStream(header));
		System.out.println(apkInfo);
	}
	
	public static void funApkTool()throws Exception{
		String apkPath="/Users/chenzhiyong/Downloads/NoxGamer_v2.0.1.apk";
		String toPath="/Users/chenzhiyong/Desktop/tmp/NoxGamer";
//		String apkPath="/Users/chenzhiyong/Downloads/QQ.apk";
//		String toPath="/Users/chenzhiyong/Desktop/tmp/QQ";
//		File toFile=new File(toPath);
//		FileUtils.deleteQuietly(toFile);
//		ApkDecoder decoder=new ApkDecoder();
//		decoder.setOutDir(toFile);
//		decoder.setApkFile(new File(apkPath));
//		decoder.decode();
	}
}

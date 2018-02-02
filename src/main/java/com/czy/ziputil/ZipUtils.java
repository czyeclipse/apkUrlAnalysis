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
//		String apkPath="";
//		String apkPath="";
		
//		String toFile="";
		
		

		
		
//		String toFile=""+header.getFileName();
//		ZipInputStream is=zipFile.getInputStream(header);
//		OutputStream output=new FileOutputStream(new File(toFile));
		
//		funLocal(toFile);
		funUrl();
//		funApkTool();
	}
	public static void funUrl()throws Exception{
		long l=System.currentTimeMillis();
//		String apkPath="";
//		String apkPath="k";
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
		String apkPath="";
		String toPath="";
//		String apkPath="";
//		String toPath="";
//		File toFile=new File(toPath);
//		FileUtils.deleteQuietly(toFile);
//		ApkDecoder decoder=new ApkDecoder();
//		decoder.setOutDir(toFile);
//		decoder.setApkFile(new File(apkPath));
//		decoder.decode();
	}
}

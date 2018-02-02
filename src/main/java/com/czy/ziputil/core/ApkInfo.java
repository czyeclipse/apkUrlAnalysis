package com.czy.ziputil.core;

import java.util.List;

public class ApkInfo{

	private String verName;
	private int verCode;
	private String packageName;
	private String appName;
	private String iconBase64;
	private List<String> permissionList;
	private List<String> activityList;
	private List<String[]> metaDataList;
	public String getVerName(){
		return verName;
	}
	public void setVerName(String verName){
		this.verName=verName;
	}
	public int getVerCode(){
		return verCode;
	}
	public void setVerCode(int verCode){
		this.verCode=verCode;
	}
	public String getPackageName(){
		return packageName;
	}
	public void setPackageName(String packageName){
		this.packageName=packageName;
	}
	public String getAppName(){
		return appName;
	}
	public void setAppName(String appName){
		this.appName=appName;
	}
	public String getIconBase64(){
		return iconBase64;
	}
	public void setIconBase64(String iconBase64){
		this.iconBase64=iconBase64;
	}
	public List<String> getPermissionList(){
		return permissionList;
	}
	public void setPermissionList(List<String> permissionList){
		this.permissionList=permissionList;
	}
	public List<String> getActivityList(){
		return activityList;
	}
	public void setActivityList(List<String> activityList){
		this.activityList=activityList;
	}
	public List<String[]> getMetaDataList(){
		return metaDataList;
	}
	public void setMetaDataList(List<String[]> metaDataList){
		this.metaDataList=metaDataList;
	}
	@Override
	public String toString(){
		return "verName:"+verName+",verCode:"+verCode+",packageName:"+packageName+",appName:"+appName;
	}
	
}

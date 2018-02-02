package com.czy.ziputil.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xmlpull.v1.XmlPullParser;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

public class AXMLPrinter{
	public static String parserStream(InputStream is){
		try{
			AXmlResourceParser parser=new AXmlResourceParser();
			parser.open(is);
			StringBuilder indent=new StringBuilder(10);
			final String indentStep="	";
			while(true){
				int type=parser.next();
				if(type==XmlPullParser.END_DOCUMENT){
					break;
				}
				switch(type){
				case XmlPullParser.START_DOCUMENT:{
					log("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					break;
				}
				case XmlPullParser.START_TAG:{
					log("%s<%s%s", indent, getNamespacePrefix(parser.getPrefix()), parser.getName());
					indent.append(indentStep);

					int namespaceCountBefore=parser.getNamespaceCount(parser.getDepth()-1);
					int namespaceCount=parser.getNamespaceCount(parser.getDepth());
					for(int i=namespaceCountBefore;i!=namespaceCount;++i){
						log("%sxmlns:%s=\"%s\"", indent, parser.getNamespacePrefix(i), parser.getNamespaceUri(i));
					}

					for(int i=0;i!=parser.getAttributeCount();++i){
						log("%s%s%s=\"%s\"", indent, getNamespacePrefix(parser.getAttributePrefix(i)),
								parser.getAttributeName(i), getAttributeValue(parser, i));
					}
					log("%s>", indent);
					break;
				}
				case XmlPullParser.END_TAG:{
					indent.setLength(indent.length()-indentStep.length());
					log("%s</%s%s>", indent, getNamespacePrefix(parser.getPrefix()), parser.getName());
					break;
				}
				case XmlPullParser.TEXT:{
					log("%s%s", indent, parser.getText());
					break;
				}
				}
			}
			return indent.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private static String parserStreamToString(InputStream is){
		try{
			AXmlResourceParser parser=new AXmlResourceParser();
			parser.open(is);
			StringBuffer content=new StringBuffer();
			while(true){
				int type=parser.next();
				if(type==XmlPullParser.END_DOCUMENT){
					break;
				}
				switch(type){
					case XmlPullParser.START_DOCUMENT:{
						content.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
						break;
					}
					case XmlPullParser.START_TAG:{
						content.append("<").append(getNamespacePrefix(parser.getPrefix())).append(parser.getName());
	
						int namespaceCountBefore=parser.getNamespaceCount(parser.getDepth()-1);
						int namespaceCount=parser.getNamespaceCount(parser.getDepth());
						for(int i=namespaceCountBefore;i!=namespaceCount;++i){
							content.append(" xmlns:").append(parser.getNamespacePrefix(i)).append("=\"").append(parser.getNamespaceUri(i)).append("\"");
						}
	
						for(int i=0;i!=parser.getAttributeCount();++i){
							content.append(" ").append(getNamespacePrefix(parser.getAttributePrefix(i))).append(parser.getAttributeName(i)).append("=\"").append(getAttributeValue(parser, i)).append("\"");
						}
						content.append(">");
						break;
					}
					case XmlPullParser.END_TAG:{
						content.append("</").append(getNamespacePrefix(parser.getPrefix())).append(parser.getName()).append(">");
						break;
					}
					case XmlPullParser.TEXT:{
						content.append(parser.getText());
						break;
					}
				}
			}
			return content.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static ApkInfo parserStreamToApkInfo(InputStream is){
		try{
			ApkInfo apkInfo=new ApkInfo();
			String content=parserStreamToString(is);
			Document doc=DocumentHelper.parseText(content);
			Element rootElement=doc.getRootElement();
			Attribute attr=rootElement.attribute("versionCode");
			apkInfo.setVerCode(Integer.parseInt(attr.getValue()));
			attr=rootElement.attribute("versionName");
			apkInfo.setVerName(attr.getValue());
			attr=rootElement.attribute("package");
			apkInfo.setPackageName(attr.getValue());
			List<?> subElements=rootElement.elements("uses-permission");
			List<String> permission=new ArrayList<String>();
			for(int i=0;i<subElements.size();i++) {
				Element element=(Element)subElements.get(i);
				permission.add(element.attributeValue("name"));
			}
			apkInfo.setPermissionList(permission);
			
			Element appElement=rootElement.element("application");
			System.out.println(appElement.asXML());
			
			
			return apkInfo;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	private static String getNamespacePrefix(String prefix){
		if(prefix==null||prefix.length()==0){
			return "";
		}
		return prefix+":";
	}

	private static String getAttributeValue(AXmlResourceParser parser,int index){
		int type=parser.getAttributeValueType(index);
		int data=parser.getAttributeValueData(index);
		if(type==TypedValue.TYPE_STRING){
			return parser.getAttributeValue(index);
		}
		if(type==TypedValue.TYPE_ATTRIBUTE){
			return String.format("?%s%08X", getPackage(data), data);
		}
		if(type==TypedValue.TYPE_REFERENCE){
			return String.format("@%s%08X", getPackage(data), data);
		}
		if(type==TypedValue.TYPE_FLOAT){
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if(type==TypedValue.TYPE_INT_HEX){
			return String.format("0x%08X", data);
		}
		if(type==TypedValue.TYPE_INT_BOOLEAN){
			return data!=0?"true":"false";
		}
		if(type==TypedValue.TYPE_DIMENSION){
			return Float.toString(complexToFloat(data))+DIMENSION_UNITS[data&TypedValue.COMPLEX_UNIT_MASK];
		}
		if(type==TypedValue.TYPE_FRACTION){
			return Float.toString(complexToFloat(data))+FRACTION_UNITS[data&TypedValue.COMPLEX_UNIT_MASK];
		}
		if(type>=TypedValue.TYPE_FIRST_COLOR_INT&&type<=TypedValue.TYPE_LAST_COLOR_INT){
			return String.format("#%08X", data);
		}
		if(type>=TypedValue.TYPE_FIRST_INT&&type<=TypedValue.TYPE_LAST_INT){
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>", data, type);
	}

	private static String getPackage(int id){
		if(id>>>24==1){
			return "android:";
		}
		return "";
	}

	private static void log(String format,Object...arguments){
		System.out.printf(format, arguments);
		System.out.println();
	}

	/////////////////////////////////// ILLEGAL STUFF, DONT LOOK :)

	public static float complexToFloat(int complex){
		return (float)(complex&0xFFFFFF00)*RADIX_MULTS[(complex>>4)&3];
	}

	private static final float RADIX_MULTS[]={0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F};
	private static final String DIMENSION_UNITS[]={"px","dip","sp","pt","in","mm","",""};
	private static final String FRACTION_UNITS[]={"%","%p","","","","","",""};
}

package com.timedebris.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class NetCore {
	public static String postResultToNet(String url,List<NameValuePair> params) throws Exception{
		//创建连接地址
		HttpPost httpRequest = new HttpPost(url);
		//发�?�的信息以键值对的形式发�?					
			//编码成字符串,取出响应内容的消息对�?
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "utf-8");
			httpRequest.setEntity(httpEntity);
			//发�?�请�?
			HttpClient httpClient = new DefaultHttpClient();
			//设置请求超时和连接超�?
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);			
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			
			String result = "";
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				BufferedReader bin = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			    result = bin.readLine();
			    System.out.println(result);
			}
		return result;
	}
}

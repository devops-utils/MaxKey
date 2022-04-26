/*
 * Copyright [2022] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.maxkey.web.ipregion;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.maxkey.util.JsonUtils;

public class IpRegionIp138 extends AbstractIpRegion implements IpRegion{
	
	public static final String REGION_URL = "https://www.ip138.com/iplookup.asp?ip=%s&action=2";
	
	public static final String BEGIN 	= "\"ip_c_list\":[";
	public static final String END 		= "], \"zg\":1};";
	
	@Override
	public String region(String ipAddress) {
		try {
			Document doc;
			doc = Jsoup.connect(String.format(REGION_URL, ipAddress))
					.timeout(TIMEOUT)
					.userAgent(USERAGENT)
					.header("Host", "www.ip138.com")
					.header("Referer", "https://www.ip138.com/")
					.header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"")
					.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
					.get();
			String htmlData = doc.toString();
			String jsonData = htmlData.substring(htmlData.indexOf(BEGIN) + BEGIN.length() , htmlData.indexOf(END));
			IpRegionIp138Response responseJson = JsonUtils.json2Object(jsonData, IpRegionIp138Response.class);
			return responseJson == null ? null : responseJson.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

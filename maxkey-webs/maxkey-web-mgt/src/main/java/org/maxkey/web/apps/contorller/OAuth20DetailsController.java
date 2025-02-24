/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
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
 

package org.maxkey.web.apps.contorller;

import org.maxkey.authn.annotation.CurrentUser;
import org.maxkey.authz.oauth2.common.OAuth2Constants;
import org.maxkey.authz.oauth2.provider.client.JdbcClientDetailsService;
import org.maxkey.constants.ConstsProtocols;
import org.maxkey.crypto.ReciprocalUtils;
import org.maxkey.entity.Message;
import org.maxkey.entity.UserInfo;
import org.maxkey.entity.apps.Apps;
import org.maxkey.entity.apps.AppsOAuth20Details;
import org.maxkey.entity.apps.oauth2.provider.client.BaseClientDetails;
import org.maxkey.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value={"/apps/oauth20"})
public class OAuth20DetailsController  extends BaseAppContorller {
	final static Logger _logger = LoggerFactory.getLogger(OAuth20DetailsController.class);
	
	@Autowired
	JdbcClientDetailsService oauth20JdbcClientDetailsService;

	@RequestMapping(value = { "/init" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> init() {
		AppsOAuth20Details oauth20Details=new AppsOAuth20Details();
		oauth20Details.setId(oauth20Details.generateId());
		oauth20Details.setSecret(ReciprocalUtils.generateKey(""));
		oauth20Details.setClientId(oauth20Details.getId());
		oauth20Details.setClientSecret(oauth20Details.getSecret());
		oauth20Details.setProtocol(ConstsProtocols.OAUTH20);
		return new Message<AppsOAuth20Details>(oauth20Details).buildResponse();
	}
	
	@RequestMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		BaseClientDetails baseClientDetails=(BaseClientDetails)oauth20JdbcClientDetailsService.loadClientByClientId(id,false);
		Apps application=appsService.get(id);//
		decoderSecret(application);
		AppsOAuth20Details oauth20Details=new AppsOAuth20Details(application,baseClientDetails);
		oauth20Details.setSecret(application.getSecret());
		oauth20Details.setClientSecret(application.getSecret());
		_logger.debug("forwardUpdate "+oauth20Details);
		oauth20Details.transIconBase64();
		return new Message<AppsOAuth20Details>(oauth20Details).buildResponse();
	}
	
	@ResponseBody
	@RequestMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> add(
			@RequestBody  AppsOAuth20Details oauth20Details,
			@CurrentUser UserInfo currentUser) {
		_logger.debug("-Add  :" + oauth20Details);
		
		if(oauth20Details.getProtocol().equalsIgnoreCase(ConstsProtocols.OAUTH21)) {
		    oauth20Details.setPkce(OAuth2Constants.PKCE_TYPE.PKCE_TYPE_YES);
		}
		transform(oauth20Details);

		oauth20Details.setClientSecret(oauth20Details.getSecret());
		oauth20Details.setInstId(currentUser.getInstId());
		
		oauth20JdbcClientDetailsService.addClientDetails(oauth20Details.clientDetailsRowMapper());
		if (appsService.insertApp(oauth20Details)) {
			return new Message<AppsOAuth20Details>(Message.SUCCESS).buildResponse();
		} else {
			return new Message<AppsOAuth20Details>(Message.FAIL).buildResponse();
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"/update"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> update(
			@RequestBody  AppsOAuth20Details oauth20Details,
			@CurrentUser UserInfo currentUser) {
		_logger.debug("-update  :" + oauth20Details);
		_logger.debug("-update  application :" + oauth20Details);
		_logger.debug("-update  oauth20Details use oauth20JdbcClientDetails" );
		if(oauth20Details.getProtocol().equalsIgnoreCase(ConstsProtocols.OAUTH21)) {
            oauth20Details.setPkce(OAuth2Constants.PKCE_TYPE.PKCE_TYPE_YES);
        }
		oauth20Details.setClientSecret(oauth20Details.getSecret());
		oauth20Details.setInstId(currentUser.getInstId());
        oauth20JdbcClientDetailsService.updateClientDetails(oauth20Details.clientDetailsRowMapper());
        oauth20JdbcClientDetailsService.updateClientSecret(oauth20Details.getClientId(), oauth20Details.getClientSecret());
        
		transform(oauth20Details);
		
		if (appsService.updateApp(oauth20Details)) {
		    return new Message<AppsOAuth20Details>(Message.SUCCESS).buildResponse();
		} else {
			return new Message<AppsOAuth20Details>(Message.FAIL).buildResponse();
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> delete(
			@RequestParam("ids") String ids,
			@CurrentUser UserInfo currentUser) {
		_logger.debug("-delete  ids : {} " , ids);
		for (String id : StringUtils.split(ids, ",")){
			oauth20JdbcClientDetailsService.removeClientDetails(id);
		}
		if (appsService.deleteBatch(ids)) {
			 return new Message<AppsOAuth20Details>(Message.SUCCESS).buildResponse();
		} else {
			return new Message<AppsOAuth20Details>(Message.FAIL).buildResponse();
		}
	}
	
}

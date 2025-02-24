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
 

package org.maxkey.authn.provider;

import java.text.ParseException;
import org.maxkey.authn.AbstractAuthenticationProvider;
import org.maxkey.authn.LoginCredential;
import org.maxkey.authn.jwt.AuthJwtService;
import org.maxkey.authn.online.OnlineTicketService;
import org.maxkey.authn.realm.AbstractAuthenticationRealm;
import org.maxkey.configuration.ApplicationConfig;
import org.maxkey.constants.ConstsLoginType;
import org.maxkey.entity.Institutions;
import org.maxkey.entity.UserInfo;
import org.maxkey.web.WebConstants;
import org.maxkey.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * database Authentication provider.
 * @author Crystal.Sea
 *
 */
public class NormalAuthenticationProvider extends AbstractAuthenticationProvider {
    private static final Logger _logger =
            LoggerFactory.getLogger(NormalAuthenticationProvider.class);

    public String getProviderName() {
        return "normal" + PROVIDER_SUFFIX;
    }
    

    public NormalAuthenticationProvider() {
		super();
	}

    public NormalAuthenticationProvider(
    		AbstractAuthenticationRealm authenticationRealm,
    		ApplicationConfig applicationConfig,
    	    OnlineTicketService onlineTicketServices,
    	    AuthJwtService authJwtService) {
		this.authenticationRealm = authenticationRealm;
		this.applicationConfig = applicationConfig;
		this.onlineTicketServices = onlineTicketServices;
		this.authJwtService = authJwtService;
	}

    @Override
	public Authentication doAuthenticate(LoginCredential loginCredential) {
		UsernamePasswordAuthenticationToken authenticationToken = null;
		_logger.debug("Trying to authenticate user '{}' via {}", 
                loginCredential.getPrincipal(), getProviderName());
        try {
        	
	        _logger.debug("authentication " + loginCredential);
	        
	        Institutions inst = (Institutions)WebContext.getAttribute(WebConstants.CURRENT_INST);
	        
	        if(this.applicationConfig.getLoginConfig().isCaptcha()) {
	        	captchaValid(loginCredential.getState(),loginCredential.getCaptcha());
	        }
	        else if(inst.getCaptchaSupport().equalsIgnoreCase("YES")) {
	        	captchaValid(loginCredential.getState(),loginCredential.getCaptcha());
	        }
	
	        emptyPasswordValid(loginCredential.getPassword());
	
	        emptyUsernameValid(loginCredential.getUsername());
	
	        UserInfo userInfo =  loadUserInfo(loginCredential.getUsername(),loginCredential.getPassword());
	
	        statusValid(loginCredential , userInfo);
	        
	        //Validate PasswordPolicy
	        authenticationRealm.getPasswordPolicyValidator().passwordPolicyValid(userInfo);
	             
	        //Match password 
	        authenticationRealm.passwordMatches(userInfo, loginCredential.getPassword());

	        //apply PasswordSetType and resetBadPasswordCount
	        authenticationRealm.getPasswordPolicyValidator().applyPasswordPolicy(userInfo);
	        
	        authenticationToken = createOnlineTicket(loginCredential,userInfo);
	        // user authenticated
	        _logger.debug("'{}' authenticated successfully by {}.", 
	        		loginCredential.getPrincipal(), getProviderName());
	        
	        authenticationRealm.insertLoginHistory(userInfo, 
							        				ConstsLoginType.LOCAL, 
									                "", 
									                "xe00000004", 
									                WebConstants.LOGIN_RESULT.SUCCESS);
        } catch (AuthenticationException e) {
            _logger.error("Failed to authenticate user {} via {}: {}",
                    new Object[] {  loginCredential.getPrincipal(),
                                    getProviderName(),
                                    e.getMessage() });
            WebContext.setAttribute(
                    WebConstants.LOGIN_ERROR_SESSION_MESSAGE, e.getMessage());
        } catch (Exception e) {
            _logger.error("Login error Unexpected exception in {} authentication:\n{}" ,
                            getProviderName(), e.getMessage());
        }
       
        return  authenticationToken;
    }
    
    /**
     * captcha validate .
     * 
     * @param authType String
     * @param captcha String
     * @throws ParseException 
     */
    protected void captchaValid(String state ,String captcha) throws ParseException {
        // for basic
    	if(!authJwtService.validateCaptcha(state,captcha)) {
    		throw new BadCredentialsException(WebContext.getI18nValue("login.error.captcha"));
    	}        
    }
}

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
 

package org.maxkey.authz.cas.endpoint;

import org.maxkey.authn.online.OnlineTicketService;
import org.maxkey.authz.cas.endpoint.ticket.TicketServices;
import org.maxkey.authz.endpoint.AuthorizeBaseEndpoint;
import org.maxkey.persistence.service.AppsCasDetailsService;
import org.maxkey.persistence.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.maxkey.web.HttpResponseAdapter;
import org.maxkey.web.HttpRequestAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CasBaseAuthorizeEndpoint  extends AuthorizeBaseEndpoint{
    final static Logger _logger = LoggerFactory.getLogger(CasBaseAuthorizeEndpoint.class);
    
    @Autowired
    @Qualifier("appsCasDetailsService")
    protected AppsCasDetailsService casDetailsService;
    
    @Autowired
    @Qualifier("userInfoService")
    protected UserInfoService userInfoService;
    
    @Autowired
    @Qualifier("casTicketServices")
    protected TicketServices ticketServices;
    
    @Autowired
    @Qualifier("casTicketGrantingTicketServices")
    protected TicketServices casTicketGrantingTicketServices;
    
    @Autowired
    protected OnlineTicketService onlineTicketService;
    
    @Autowired
    @Qualifier("casProxyGrantingTicketServices")
    protected TicketServices casProxyGrantingTicketServices;
    
    @Autowired
    protected HttpResponseAdapter httpResponseAdapter;
    
    @Autowired
    protected HttpRequestAdapter httpRequestAdapter; 
    
}

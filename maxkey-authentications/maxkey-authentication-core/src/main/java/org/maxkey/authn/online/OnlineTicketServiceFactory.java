/*
 * Copyright [2021] [MaxKey of copyright http://www.maxkey.top]
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
 

package org.maxkey.authn.online;

import org.maxkey.constants.ConstsPersistence;
import org.maxkey.persistence.redis.RedisConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class OnlineTicketServiceFactory {
	private static final  Logger _logger = 
            LoggerFactory.getLogger(OnlineTicketServiceFactory.class);
	
	 public OnlineTicketService getService(
			 	int persistence,
			 	JdbcTemplate jdbcTemplate,
	            RedisConnectionFactory redisConnFactory){
		 
		 OnlineTicketService onlineTicketServices = null;
		if (persistence == ConstsPersistence.INMEMORY) {
		    onlineTicketServices = new InMemoryOnlineTicketService(jdbcTemplate);
		    _logger.debug("InMemoryOnlineTicketServices");
		} else if (persistence == ConstsPersistence.JDBC) {
		    _logger.debug("OnlineTicketServices not support "); 
		} else if (persistence == ConstsPersistence.REDIS) {
		    onlineTicketServices = new RedisOnlineTicketService(redisConnFactory,jdbcTemplate);
		    _logger.debug("RedisOnlineTicketServices");
		}
		
		return onlineTicketServices;
	}
}

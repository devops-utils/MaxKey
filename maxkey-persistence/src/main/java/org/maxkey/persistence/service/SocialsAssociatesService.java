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
 

package org.maxkey.persistence.service;

import java.util.List;

import org.apache.mybatis.jpa.persistence.JpaBaseService;
import org.maxkey.entity.SocialsAssociate;
import org.maxkey.entity.UserInfo;
import org.maxkey.persistence.mapper.SocialsAssociateMapper;
import org.springframework.stereotype.Repository;


@Repository
public class SocialsAssociatesService  extends JpaBaseService<SocialsAssociate>{
	
	public SocialsAssociatesService() {
		super(SocialsAssociateMapper.class);
	}

	/* (non-Javadoc)
	 * @see com.connsec.db.service.BaseService#getMapper()
	 */
	@Override
	public SocialsAssociateMapper getMapper() {
		return (SocialsAssociateMapper)super.getMapper();
	}
 
	
	public List<SocialsAssociate>  queryByUser(UserInfo user) {
		return getMapper().queryByUser(user);
	}
	 
}

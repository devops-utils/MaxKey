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
 

package org.maxkey.persistence.service;

import java.util.List;

import org.apache.mybatis.jpa.persistence.JpaBaseService;
import org.maxkey.constants.ConstsStatus;
import org.maxkey.crypto.password.PasswordReciprocal;
import org.maxkey.entity.Accounts;
import org.maxkey.entity.AccountsStrategy;
import org.maxkey.entity.OrganizationsCast;
import org.maxkey.entity.UserInfo;
import org.maxkey.persistence.mapper.AccountsMapper;
import org.maxkey.persistence.mq.MqIdentityAction;
import org.maxkey.persistence.mq.MqIdentityTopic;
import org.maxkey.persistence.mq.MessageQueueService;
import org.maxkey.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

@Repository
public class AccountsService  extends JpaBaseService<Accounts>{

    @Autowired
    MessageQueueService mqPersistService;
    
    @Autowired
    UserInfoService  userInfoService;
    
    @Autowired
    AccountsStrategyService accountsStrategyService;
    
    @Autowired
    OrganizationsCastService organizationsCastService;
    
	public AccountsService() {
		super(AccountsMapper.class);
	}

	/* (non-Javadoc)
	 * @see com.connsec.db.service.BaseService#getMapper()
	 */
	@Override
	public AccountsMapper getMapper() {
		return (AccountsMapper)super.getMapper();
	}
	
	
	 public boolean insert(Accounts account) {
	     if (super.insert(account)) {
	            if(mqPersistService.getApplicationConfig().isMessageQueueSupport()) {
	                UserInfo loadUserInfo = userInfoService.findUserRelated(account.getUserId());
	                account.setUserInfo(loadUserInfo);
	                OrganizationsCast cast = new OrganizationsCast();
                    cast.setProvider(account.getAppId());
                    cast.setOrgId(loadUserInfo.getDepartmentId());
                    account.setOrgCast(organizationsCastService.query(cast));
                    mqPersistService.send(
	                        MqIdentityTopic.ACCOUNT_TOPIC, 
	                        account,
	                        MqIdentityAction.CREATE_ACTION);
	            }
	            
	            return true;
	        }
	     return false;
	 }
	 
   public boolean update(Accounts account) {
         if (super.update(account)) {
        	 if(mqPersistService.getApplicationConfig().isMessageQueueSupport()) {
                    UserInfo loadUserInfo = userInfoService.findUserRelated(account.getUserId());
                    account.setUserInfo(loadUserInfo);
                    OrganizationsCast cast = new OrganizationsCast();
                    cast.setProvider(account.getAppId());
                    cast.setOrgId(loadUserInfo.getDepartmentId());
                    account.setOrgCast(organizationsCastService.query(cast));
                    mqPersistService.send(
                            MqIdentityTopic.ACCOUNT_TOPIC, 
                            account,
                            MqIdentityAction.UPDATE_ACTION);
                }
                
                return true;
            }
         return false;
     }
   
   public boolean remove(String id) {
       Accounts account = this.get(id);
       if (super.remove(id)) {
              UserInfo loadUserInfo = null;
              if(mqPersistService.getApplicationConfig().isMessageQueueSupport()) {
                  loadUserInfo = userInfoService.findUserRelated(account.getUserId());
                  account.setUserInfo(loadUserInfo);
                  mqPersistService.send(
                          MqIdentityTopic.ACCOUNT_TOPIC, 
                          account,
                          MqIdentityAction.DELETE_ACTION);
              }
              
              return true;
          }
       return false;
   }
   
   public void refreshByStrategy(AccountsStrategy strategy) {
       if(StringUtils.isNotBlank(strategy.getOrgIdsList())) {
           strategy.setOrgIdsList("'"+strategy.getOrgIdsList().replace(",", "','")+"'");
       }
       List<UserInfo>  userList = queryUserNotInStrategy(strategy);
       for(UserInfo user : userList) {
           Accounts account = new Accounts();
           account.setAppId(strategy.getAppId());
           account.setAppName(strategy.getAppName());
           
           account.setUserId(user.getId());
           account.setUsername(user.getUsername());
           account.setDisplayName(user.getDisplayName());
           account.setRelatedUsername(generateAccount(user,strategy));
           account.setRelatedPassword(PasswordReciprocal.getInstance().encode(userInfoService.randomPassword()));
           
           account.setCreateType("automatic");
           account.setStatus(ConstsStatus.ACTIVE);
           account.setStrategyId(strategy.getId());
           
           insert(account);
       }
       deleteByStrategy(strategy);
   }
   public void refreshAllByStrategy() {
	   AccountsStrategy queryStrategy = new AccountsStrategy();
	   queryStrategy.setCreateType("automatic");
       for( AccountsStrategy strategy : accountsStrategyService.query(queryStrategy)) {
           refreshByStrategy(strategy);
       }
   }
   
   
   public List<UserInfo> queryUserNotInStrategy(AccountsStrategy strategy){
       return getMapper().queryUserNotInStrategy(strategy);
   }
   
   public long deleteByStrategy(AccountsStrategy strategy) {
       return getMapper().deleteByStrategy(strategy);
   }
	
	
   public List<Accounts> queryByAppIdAndDate(Accounts account) {
       return getMapper().queryByAppIdAndDate(account);
   }
   
   public List<Accounts> queryByAppIdAndAccount(String appId,String relatedUsername){
	   return getMapper().queryByAppIdAndAccount(appId,relatedUsername);
   }
   
   
   public String generateAccount(UserInfo  userInfo,AccountsStrategy accountsStrategy) {
   	String shortAccount = generateAccount(userInfo,accountsStrategy,true);
   	String account = generateAccount(userInfo,accountsStrategy,false);
   	String accountResult = shortAccount;
   	List<Accounts> AccountsList =getMapper().queryByAppIdAndAccount(accountsStrategy.getAppId(),shortAccount +accountsStrategy.getSuffixes());
   	if(!AccountsList.isEmpty()) {
   		if(accountsStrategy.getMapping().equalsIgnoreCase("email")) {
   			accountResult = account;
   			AccountsList =getMapper().queryByAppIdAndAccount(accountsStrategy.getAppId(),account + accountsStrategy.getSuffixes());
   		}
   		if(!AccountsList.isEmpty()) {
	    		for(int i =1 ;i < 100 ;i++) {
	    			accountResult = account + i;
	    			AccountsList =getMapper().queryByAppIdAndAccount(accountsStrategy.getAppId(),accountResult + accountsStrategy.getSuffixes());
	    			if(AccountsList.isEmpty())break;
	    		}
   		}
   	}
   	if(StringUtils.isNotBlank(accountsStrategy.getSuffixes())){
   		accountResult = accountResult + accountsStrategy.getSuffixes();
   	}
       return accountResult;
   }
   
   
	private String generateAccount(UserInfo  userInfo,AccountsStrategy strategy,boolean isShort) {
		String account = "";
    	if(strategy.getMapping().equalsIgnoreCase("username")) {
    		account = userInfo.getUsername();
    	}else if(strategy.getMapping().equalsIgnoreCase("mobile")) {
    		account = userInfo.getMobile();
    	}else if(strategy.getMapping().equalsIgnoreCase("email")) {
    		try {
	    		if(isShort) {
	    			account = getPinYinShortName(userInfo.getDisplayName());
	    		}else {
	    			account = getPinYinName(userInfo.getDisplayName());
	    		}
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    	}else if(strategy.getMapping().equalsIgnoreCase("employeeNumber")) {
    		account = userInfo.getEmployeeNumber();
    	}else if(strategy.getMapping().equalsIgnoreCase("windowsAccount")) {
    		account = userInfo.getWindowsAccount();
    	}else if(strategy.getMapping().equalsIgnoreCase("idCardNo")) {
    		account = userInfo.getIdCardNo();
    	}else {
    		account = userInfo.getUsername();
    	}
    	
        return account;
	}
	
	public static String getPinYinName(String name) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat pinyinFormat = new        HanyuPinyinOutputFormat();
        pinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        pinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinyinFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        return PinyinHelper.toHanYuPinyinString(name, pinyinFormat, "",false);
    }
	
	public static String getPinYinShortName(String name) throws BadHanyuPinyinOutputFormatCombination {
		char[] strs = name.toCharArray();
		String pinyinName = "";
		for(int i=0;i<strs.length;i++) {
			if(i == 0) {
				pinyinName += getPinYinName(strs[i]+"");
			}else {
				pinyinName += getPinYinName(strs[i]+"").charAt(0);
			}
		}
		return pinyinName;
    }
	
}

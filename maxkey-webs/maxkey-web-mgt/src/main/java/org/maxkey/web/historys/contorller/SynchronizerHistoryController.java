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
 

package org.maxkey.web.historys.contorller;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.mybatis.jpa.persistence.JpaPageResults;
import org.maxkey.authn.annotation.CurrentUser;
import org.maxkey.entity.HistorySynchronizer;
import org.maxkey.entity.Message;
import org.maxkey.entity.UserInfo;
import org.maxkey.persistence.service.HistorySynchronizerService;
import org.maxkey.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 同步器日志查询
 * 
 * @author Crystal.sea
 *
 */

@Controller
@RequestMapping(value={"/historys"})
public class SynchronizerHistoryController {
final static Logger _logger = LoggerFactory.getLogger(SynchronizerHistoryController.class);

	@Autowired
    @Qualifier("historySynchronizerService")
	HistorySynchronizerService historySynchronizerService;
	
	/**
     * @param historySynchronizer
     * @return
     */
    @RequestMapping(value={"/synchronizerHistory/fetch"})
    @ResponseBody
    public ResponseEntity<?> fetch(
    			@ModelAttribute("historySynchronizer") HistorySynchronizer historySynchronizer,
    			@CurrentUser UserInfo currentUser){
        _logger.debug("historys/synchronizerHistory/fetch/ {}",historySynchronizer);
        historySynchronizer.setInstId(currentUser.getInstId());
        return new Message<JpaPageResults<HistorySynchronizer>>(
        		historySynchronizerService.queryPageResults(historySynchronizer)
        	).buildResponse();
    }

	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_HH_MM_SS);
        dateFormat.setLenient(false);  
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}

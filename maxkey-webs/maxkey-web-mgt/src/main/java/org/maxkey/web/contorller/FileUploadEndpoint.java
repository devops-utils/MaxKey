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
 

package org.maxkey.web.contorller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.maxkey.authn.annotation.CurrentUser;
import org.maxkey.entity.FileUpload;
import org.maxkey.entity.Message;
import org.maxkey.entity.UserInfo;
import org.maxkey.persistence.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileUploadEndpoint {
	
	private static Logger _logger = LoggerFactory.getLogger(FileUploadEndpoint.class);
	
	@Autowired
	FileUploadService fileUploadService;
	
 	@RequestMapping(value={"/file/upload/"})
 	@ResponseBody
 	public ResponseEntity<?> upload( HttpServletRequest request, 
 	                            HttpServletResponse response,
 	                           @ModelAttribute FileUpload fileUpload,
 	                           @CurrentUser UserInfo currentUser){
 		_logger.debug("FileUpload");
 		fileUpload.setId(fileUpload.generateId());
 		fileUpload.setContentType(fileUpload.getUploadFile().getContentType());
 		fileUpload.setFileName(fileUpload.getUploadFile().getOriginalFilename());
 		fileUpload.setContentSize(fileUpload.getUploadFile().getSize());
 		fileUpload.setCreatedBy(currentUser.getUsername());
 		/*
		 * upload UploadFile MultipartFile  to Uploaded Bytes
		 */
		if(null!=fileUpload.getUploadFile()&&!fileUpload.getUploadFile().isEmpty()){
			try {
				fileUpload.setUploaded(fileUpload.getUploadFile().getBytes());
				fileUploadService.insert(fileUpload);
				_logger.trace("FileUpload SUCCESS");
			} catch (IOException e) {
				_logger.error("FileUpload IOException",e);
			}
		}
 		return new Message<Object>(Message.SUCCESS,(Object)fileUpload.getId()).buildResponse();
 	}
 	
}

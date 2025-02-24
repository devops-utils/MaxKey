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
 

package org.maxkey.web.contorller;

import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.maxkey.authn.jwt.AuthJwtService;
import org.maxkey.entity.Message;
import org.maxkey.persistence.MomentaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * ImageCaptchaEndpoint  Producer captcha.
 * @author Crystal.Sea
 *
 */
@Controller
public class ImageCaptchaEndpoint {
    private static final Logger _logger = LoggerFactory.getLogger(ImageCaptchaEndpoint.class);
    
    @Autowired
    private Producer captchaProducer;
    
    @Autowired 
	protected MomentaryService momentaryService;
    
    @Autowired
	AuthJwtService authJwtService;

    /**
     * captcha image Producer.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value={"/captcha"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public  ResponseEntity<?> captchaHandleRequest( 
    			@RequestParam(value="captcha",required=false,defaultValue="text") String captchaType,
    			@RequestParam(value="state",required=false,defaultValue="state") String state) {
        try {
            String kaptchaText = captchaProducer.createText();
            String kaptchaValue = kaptchaText;
            if (captchaType.equalsIgnoreCase("Arithmetic")) {
                Integer minuend = Integer.valueOf(kaptchaText.substring(0, 1));
                Integer subtrahend = Integer.valueOf(kaptchaText.substring(1, 2));
                if (minuend - subtrahend > 0) {
                	kaptchaValue = (minuend - subtrahend ) + "";
                    kaptchaText = minuend + "-" + subtrahend + "=?";
                } else {
                	kaptchaValue = (minuend + subtrahend) + "";
                    kaptchaText = minuend + "+" + subtrahend + "=?";
                }
            }
            String kaptchaKey = "";
            if(StringUtils.isNotBlank(state) 
            		&& !state.equalsIgnoreCase("state")
            		&& authJwtService.validateJwtToken(state)) {
            	//just validate state Token
            }else {
            	state = authJwtService.genJwt();
            }
            kaptchaKey = authJwtService.resolveJWTID(state);
            _logger.trace("kaptchaKey {} , Captcha Text is {}" ,kaptchaKey, kaptchaValue);
           
            momentaryService.put("", kaptchaKey, kaptchaValue);
            // create the image with the text
            BufferedImage bufferedImage = captchaProducer.createImage(kaptchaText);
            // write the data out
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", stream);
			
			String b64Image = "data:image/png;base64," + 
					Base64.getEncoder().encodeToString(stream.toByteArray());
           
            _logger.trace("b64Image {}" , b64Image);
            
            stream.close();
            return new Message<ImageCaptcha>(
            			new ImageCaptcha(state,b64Image)
            		).buildResponse();
        } catch (Exception e) {
            _logger.error("captcha Producer Error " + e.getMessage());
        }
        return new Message< Object>(Message.FAIL).buildResponse();
    }

	public void setCaptchaProducer(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }


}

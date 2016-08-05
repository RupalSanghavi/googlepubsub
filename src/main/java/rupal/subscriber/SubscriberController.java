package rupal.subscriber;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.common.base.Preconditions;



@Controller
@RequestMapping("/Demo")
public class SubscriberController {

private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);
	
	/**
	 * @return
	 * will return the feature switch values cache map as a json whenever they hit this endpoint
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiveMessage", method=RequestMethod.GET)
	public String receiveMessage() {
		
		logger.info("Received message");
		return "Hello World!!";
	}
		
	
}
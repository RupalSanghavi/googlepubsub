
import java.util.ConcurrentModificationException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


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


import java.util.ConcurrentModificationException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

import java.io.IOException;
//import java.util.logging.Logger;



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
		
	
	public class RetryHttpInitializerWrapper implements HttpRequestInitializer {

//	    private static final Logger LOG =
//	        Logger.getLogger(RetryHttpInitializerWrapper.class.getName());

	    // Intercepts the request for filling in the "Authorization"
	    // header field, as well as recovering from certain unsuccessful
	    // error codes wherein the Credential must refresh its token for a
	    // retry.
	    private final Credential wrappedCredential;

	    // A sleeper; you can replace it with a mock in your test.
	    private final Sleeper sleeper;

	    public RetryHttpInitializerWrapper(Credential wrappedCredential) {
	        this(wrappedCredential, Sleeper.DEFAULT);
	    }

	    // Use only for testing.
	    RetryHttpInitializerWrapper(
	            Credential wrappedCredential, Sleeper sleeper) {
	        this.wrappedCredential = Preconditions.checkNotNull(wrappedCredential);
	        this.sleeper = sleeper;
	    }

	    @Override
	    public void initialize(HttpRequest request) {
	        request.setReadTimeout(2 * 60000); // 2 minutes read timeout
	        final HttpUnsuccessfulResponseHandler backoffHandler =
	            new HttpBackOffUnsuccessfulResponseHandler(
	                new ExponentialBackOff())
	                    .setSleeper(sleeper);
	        request.setInterceptor(wrappedCredential);
	        request.setUnsuccessfulResponseHandler(
	                new HttpUnsuccessfulResponseHandler() {
	                    @Override
	                    public boolean handleResponse(
	                            HttpRequest request,
	                            HttpResponse response,
	                            boolean supportsRetry) throws IOException {
	                        if (wrappedCredential.handleResponse(
	                                request, response, supportsRetry)) {
	                            // If credential decides it can handle it,
	                            // the return code or message indicated
	                            // something specific to authentication,
	                            // and no backoff is desired.
	                            return true;
	                        } else if (backoffHandler.handleResponse(
	                                request, response, supportsRetry)) {
	                            // Otherwise, we defer to the judgement of
	                            // our internal backoff handler.
	                          //LOG.info("Retrying " + request.getUrl());
	                          return true;
	                        } else {
	                            return false;
	                        }
	                    }
	                });
	        request.setIOExceptionHandler(
	            new HttpBackOffIOExceptionHandler(new ExponentialBackOff())
	                .setSleeper(sleeper));
	    }
	}
	
}

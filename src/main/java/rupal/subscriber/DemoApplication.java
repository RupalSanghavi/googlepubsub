package rupal.subscriber;



import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.PushConfig;
import com.google.api.services.pubsub.model.Subscription;



@SpringBootApplication
public class DemoApplication{
	
	private Pubsub pubsub;
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		// Only needed if you are using push delivery
		String pushEndpoint = "https:/162.222.183.65:8443/Demo/receiveMessage";
		PushConfig pushConfig = new PushConfig().setPushEndpoint(pushEndpoint);
		Pubsub pubsub = null;
		try {
			pubsub = createPubsubClient();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Subscription subscription = new Subscription()
		        // The name of the topic from which this subscription
		        // receives messages
		        .setTopic("projects/someproject/topics/sometopic")
		        // Ackowledgement deadline in second
		        .setAckDeadlineSeconds(10)
		        // Only needed if you are using push delivery
		        .setPushConfig(pushConfig);
		Subscription newSubscription = null;
		try {
			newSubscription = pubsub.projects().subscriptions().create(
			        "projects/myproject/subscriptions/mysubscription", subscription)
			        .execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Created: " + newSubscription.getName());
	}
	
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public Pubsub createPubsubClient()
		    throws IOException, GeneralSecurityException {
		    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
		    GoogleCredential credential = GoogleCredential.getApplicationDefault();
		    HttpRequestInitializer initializer =
		        new RetryHttpInitializerWrapper(credential);
		    return new Pubsub.Builder(transport, JSON_FACTORY, initializer).build();
		  }
	
	public class RetryHttpInitializerWrapper implements HttpRequestInitializer{
//		private final Logger LOG =
//		        Logger.getLogger(RetryHttpInitializerWrapper.class.getName());

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


import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes incoming messages and sends them to the client web app.
 */
public class ReceiveMessageServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    public final void doPost(final HttpServletRequest req,
                             final HttpServletResponse resp)
            throws IOException {
        // Validating unique subscription token before processing the message
        String subscriptionToken = System.getProperty(
                Constants.BASE_PACKAGE + ".subscriptionUniqueToken");
        if (!subscriptionToken.equals(req.getParameter("token"))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().close();
            return;
        }

        ServletInputStream inputStream = req.getInputStream();

        // Parse the JSON message to the POJO model class
        JsonParser parser = JacksonFactory.getDefaultInstance()
                .createJsonParser(inputStream);
        parser.skipToKey("message");
        PubsubMessage message = parser.parseAndClose(PubsubMessage.class);

        // Store the message in the datastore
        Entity messageToStore = new Entity("PubsubMessage");
        messageToStore.setProperty("message",
                new String(message.decodeData(), "UTF-8"));
        messageToStore.setProperty("receipt-time", System.currentTimeMillis());
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(messageToStore);

        // Invalidate the cache
        MemcacheService memcacheService =
                MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete(Constants.MESSAGE_CACHE_KEY);

        // Acknowledge the message by returning a success code
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().close();
    }
}

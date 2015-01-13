package com.chicagof1.auth;

import com.chicagof1.model.UserInfo;
import com.chicagof1.persistence.PersistenceManager;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.j2e.configuration.ClientsConfiguration;
import org.pac4j.j2e.filter.CallbackFilter;
import org.pac4j.j2e.filter.ClientsConfigFilter;
import org.pac4j.j2e.filter.RequiresAuthenticationFilter;
import org.pac4j.j2e.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Patching CallbackFilter to allow handling the login event
 */
public class MyCallbackFilter extends ClientsConfigFilter {
    PersistenceManager persistenceManager;

    public MyCallbackFilter(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    private static final Logger logger = LoggerFactory.getLogger(CallbackFilter.class);

    private String defaultUrl = "/";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.defaultUrl = filterConfig.getInitParameter("defaultUrl");
        CommonHelper.assertNotBlank("defaultUrl", this.defaultUrl);
    }

    @Override
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                  final HttpSession session, final FilterChain chain) throws IOException,
            ServletException {

        final WebContext context = new J2EContext(request, response);
        final Client client = ClientsConfiguration.getClients().findClient(context);
        logger.debug("client : {}", client);

        final Credentials credentials;
        try {
            credentials = client.getCredentials(context);
        } catch (final RequiresHttpAction e) {
            logger.debug("extra HTTP action required : {}", e.getCode());
            return;
        }
        logger.debug("credentials : {}", credentials);

        // get user profile
        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context);
        logger.debug("profile : {}", profile);

        if (profile != null) {
            // only save profile when it's not null
            UserUtils.setProfile(session, profile);
            String email = profile.getEmail();
            String fullName = AuthUtils.fullName(profile);
            UserInfo ui = new UserInfo(email, fullName);
            //persistenceManager.loggedIn(ui);
        }

        final String requestedUrl = (String) session.getAttribute(RequiresAuthenticationFilter.ORIGINAL_REQUESTED_URL);
        logger.debug("requestedUrl : {}", requestedUrl);
        if (CommonHelper.isNotBlank(requestedUrl)) {
            response.sendRedirect(requestedUrl);
        } else {
            response.sendRedirect(this.defaultUrl);
        }
    }
}

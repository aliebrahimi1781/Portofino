/*
* Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
* http://www.manydesigns.com/
*
* Unless you have purchased a commercial license agreement from ManyDesigns srl,
* the following license terms apply:
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License version 3 as published by
* the Free Software Foundation.
*
* There are special exceptions to the terms and conditions of the GPL
* as it is applied to this software. View the full text of the
* exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
* software distribution.
*
* This program is distributed WITHOUT ANY WARRANTY; and without the
* implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
* or write to:
* Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330,
* Boston, MA  02111-1307  USA
*
*/

package com.manydesigns.portofino.oauth;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.manydesigns.elements.ElementsThreadLocals;
import com.manydesigns.elements.ognl.OgnlUtils;
import com.manydesigns.portofino.shiro.ShiroUtils;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.util.UrlBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Utility class for generic OAuth authorization
 *
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public class OAuthHelper {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    public static final Logger logger = LoggerFactory.getLogger(OAuthHelper.class);

    protected CredentialStore credentialStore;
    protected String authorizeMethod = "authorize";
    protected HttpTransport httpTransport = new NetHttpTransport();
    protected Credential.AccessMethod accessMethod = BearerToken.authorizationHeaderAccessMethod();

    protected static final JsonFactory JSON_FACTORY = new JacksonFactory();

    protected final ActionBeanContext actionBeanContext;
    protected final String tokenServerUrl;
    protected final String authorizationServerUrl;
    protected final Collection<String> scopes;
    protected final String clientId;
    protected final String clientSecret;

    protected String error;

    public OAuthHelper(ActionBeanContext actionBeanContext, String tokenServerUrl, String authorizationServerUrl,
                       Collection<String> scopes, String clientId, String clientSecret) {
        this.actionBeanContext = actionBeanContext;
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerUrl = authorizationServerUrl;
        this.scopes = scopes;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public OAuthHelper(ActionBeanContext actionBeanContext, String tokenServerUrl, String authorizationServerUrl,
                       String scope, String clientId, String clientSecret) {
        this(actionBeanContext, tokenServerUrl, authorizationServerUrl,
             Collections.singleton(scope), clientId, clientSecret);
    }

    /**
     * Returns a URL where the user should be redirected in order to authorize access to the selected resources.
     * @return the authorization URL.
     */
    public String computeAuthorizationUrl() {
        String redirectUri = getRedirectUrl();

        String authorizationUrl = new AuthorizationCodeRequestUrl(authorizationServerUrl, clientId)
                .setRedirectUri(redirectUri)
                .setScopes(scopes).build();

        return authorizationUrl;
    }

    public String getRedirectUrl() {
        return new UrlBuilder(actionBeanContext.getLocale(),
                       actionBeanContext.getRequest().getRequestURL().toString(),
                       false).setEvent(authorizeMethod).toString();
    }

    /**
     * Handles the callback from the OAuth provider, returning a valid Credential if successful.
     * @param code
     * @param userId
     * @return
     * @throws IOException
     */
    public Credential authorize(String code, @Nullable String userId) throws IOException {
        String redirectUri = getRedirectUrl();

        AuthorizationCodeFlow codeFlow = createCodeFlow();

        TokenResponse response = codeFlow
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .setScopes(scopes)
                .execute();

        return codeFlow.createAndStoreCredential(response, userId);
    }

    /**
     * Handles the callback from the OAuth provider, returning a valid Credential if successful.
     * @param request
     * @param userId
     * @return
     * @throws IOException
     */
    public Credential authorize(HttpServletRequest request, String userId) throws IOException {
        error = request.getParameter("error");
        if(!StringUtils.isBlank(error)) {
            return null;
        }

        String code = request.getParameter("code");
        if(StringUtils.isBlank(code)) {
            throw new RuntimeException("No authorization code found in request");
        }
        return authorize(code, userId);
    }

    /**
     * Handles the callback from the OAuth provider, returning a valid Credential if successful. Automatically uses
     * the current request and the logged in user.
     * @return
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        HttpServletRequest request = ElementsThreadLocals.getHttpServletRequest();
        Subject subject = SecurityUtils.getSubject();
        String userId;
        if(subject.isAuthenticated()) {
            userId = OgnlUtils.convertValueToString(ShiroUtils.getPrimaryPrincipal(subject));
        } else {
            throw new IllegalStateException("User is not logged in, can not determine the user id");
        }
        return authorize(request, userId);
    }

    /**
     * Executes an action if the user's credential is known, otherwise redirects to the authorization page.
     * @param userId
     * @param action
     * @return
     */
    public Resolution doWithCredential(String userId, Callable<Resolution> action) {
        Credential credential = loadCredential(userId);
        if (credential != null) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new RedirectResolution(computeAuthorizationUrl());
    }

    /**
     * Executes an action if the current logged in user's credential is known,
     * otherwise redirects to the authorization page.
     * @param action
     * @return
     */
    public Resolution doWithCredential(Callable<Resolution> action) {
        Subject subject = SecurityUtils.getSubject();
        String userId;
        if(subject.isAuthenticated()) {
            userId = OgnlUtils.convertValueToString(ShiroUtils.getPrimaryPrincipal(subject));
        } else {
            throw new IllegalStateException("User is not logged in, can not determine the user id");
        }
        return doWithCredential(userId, action);
    }

    protected AuthorizationCodeFlow createCodeFlow() {
        return new AuthorizationCodeFlow.Builder(
                accessMethod,
                httpTransport,
                JSON_FACTORY,
                new GenericUrl(tokenServerUrl),
                getHttpExecuteInterceptor(),
                clientId,
                authorizationServerUrl)
                .setScopes(scopes)
                .setCredentialStore(credentialStore)
                .build();
    }

    protected HttpExecuteInterceptor getHttpExecuteInterceptor() {
        return new ClientParametersAuthentication(clientId, clientSecret);
    }

    public Credential loadCredential(String userId) {
        try {
            return createCodeFlow().loadCredential(userId);
        } catch (IOException e) {
            logger.error("Could not load credential", e);
            return null;
        }
    }

    public CredentialStore getCredentialStore() {
        return credentialStore;
    }

    public void setCredentialStore(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
    }

    public String getAuthorizeMethod() {
        return authorizeMethod;
    }

    public void setAuthorizeMethod(String authorizeMethod) {
        this.authorizeMethod = authorizeMethod;
    }

    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    public void setHttpTransport(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    public Credential.AccessMethod getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(Credential.AccessMethod accessMethod) {
        this.accessMethod = accessMethod;
    }

    public String getError() {
        return error;
    }

    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
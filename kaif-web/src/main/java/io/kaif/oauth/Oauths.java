/**
 * Copyright 2010 Newcastle University
 * <p>
 * http://research.ncl.ac.uk/smart/
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kaif.oauth;

public class Oauths {

  public static final class HeaderType {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String AUTHORIZATION = "Authorization";
  }

  public static final class WWWAuthHeader {
    public static final String REALM = "realm";
  }

  public static final class ContentType {
    public static final String URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String JSON = "application/json";
  }

  public static final String OAUTH_RESPONSE_TYPE = "response_type";
  public static final String OAUTH_CLIENT_ID = "client_id";
  public static final String OAUTH_CLIENT_SECRET = "client_secret";
  public static final String OAUTH_REDIRECT_URI = "redirect_uri";
  public static final String OAUTH_USERNAME = "username";
  public static final String OAUTH_PASSWORD = "password";
  public static final String OAUTH_ASSERTION_TYPE = "assertion_type";
  public static final String OAUTH_ASSERTION = "assertion";
  public static final String OAUTH_SCOPE = "scope";
  public static final String OAUTH_STATE = "state";
  public static final String OAUTH_GRANT_TYPE = "grant_type";

  public static final String OAUTH_HEADER_NAME = "Bearer";

  //Authorization response params
  public static final String OAUTH_CODE = "code";
  public static final String OAUTH_ACCESS_TOKEN = "access_token";
  public static final String OAUTH_EXPIRES_IN = "expires_in";
  public static final String OAUTH_REFRESH_TOKEN = "refresh_token";

  public static final String OAUTH_TOKEN_TYPE = "token_type";

  public static final String OAUTH_TOKEN = "oauth_token";

  public static final String OAUTH_TOKEN_DRAFT_0 = "access_token";
  public static final String OAUTH_BEARER_TOKEN = "access_token";

  public static final String DEFAULT_PARAMETER_STYLE = "header";
  public static final String DEFAULT_TOKEN_TYPE = "Bearer";

  public static final String OAUTH_VERSION_DIFFER = "oauth_signature_method";

  private Oauths() {
  }
}

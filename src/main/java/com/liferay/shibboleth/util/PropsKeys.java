/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * Copyright (c) 2014 Georg-August-Universität Göttingen
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.shibboleth.util;

/**
 * @author Eric Chin
 */
public class PropsKeys {

	public static final String SHIBBOLETH_AUTH_ENABLED =
		"shibboleth.auth.enabled";

	public static final String SHIBBOLETH_USER_AUTO_CREATE = 
	  	"shibboleth.user.auto.create";

	public static final String SHIBBOLETH_IMPORT_FROM_LDAP =
		"shibboleth.import.from.ldap";

	public static final String SHIBBOLETH_LOGOUT_URL = 
	 	 "shibboleth.logout.url";

	public static final String SHIBBOLETH_USERNAME_HEADER = 
		"shibboleth.user.username.header";

	public static final String SHIBBOLETH_EMAIL_HEADER = 
		"shibboleth.user.email.header";

	public static final String SHIBBOLETH_FIRSTNAME_HEADER = 
		"shibboleth.user.firstName.header";

	public static final String SHIBBOLETH_LASTNAME_HEADER = 
		"shibboleth.user.lastName.header";

	public static final String SHIBBOLETH_GROUPS_ENABLEMAPPING = 
		"shibboleth.groups.enable.mapping";

	public static final String SHIBBOLETH_GROUPS_HEADER = 
		"shibboleth.groups.header";

	public static final String SHIBBOLETH_GROUPS_HEADER_SPLIT = 
		"shibboleth.groups.header.split";

	public static final String SHIBBOLETH_HEADERS_ENABLED = 
		"shibboleth.headers.enabled";

	public static final String SHIBBOLETH_REENCODE_ENABLED =
		"shibboleth.reencode.enabled";
}

package impl.clt;

import java.net.URI;

import microgram.impl.clt.java._TODO_RetryPostsClient;
import microgram.impl.clt.java._TODO_RetryProfilesClient;
import microgram.impl.clt.rest.*;
import microgram.impl.clt.soap._TODO_SoapPostsClient;
import microgram.impl.clt.soap._TODO_SoapProfilesClient;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;

public class ClientFactory {

	private static final String REST = "/rest";
	private static final String SOAP = "/soap";
	
	public static Profiles createProfilesClient(URI uri) {
		String uriString = uri.toString();
		if (uriString.endsWith(REST))
			return new _TODO_RetryProfilesClient(new _TODO_RestProfilesClient(uri));
		else if (uriString.endsWith(SOAP))
			return new _TODO_RetryProfilesClient(new _TODO_SoapProfilesClient(uri));

		throw new RuntimeException("Unknown service type..." + uri);
	}
	
	public static Posts createPostsClient(URI uri) {
		String uriString = uri.toString();
		if (uriString.endsWith(REST))
			return new _TODO_RetryPostsClient(new _TODO_RestPostsClient(uri));
		else if (uriString.endsWith(SOAP))
			return new _TODO_RetryPostsClient(new _TODO_SoapPostsClient(uri));

		throw new RuntimeException("Unknown service type..." + uri);
	}
}

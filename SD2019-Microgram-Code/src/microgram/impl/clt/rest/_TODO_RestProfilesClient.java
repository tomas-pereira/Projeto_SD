package microgram.impl.clt.rest;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.rest.RestProfiles;

public class _TODO_RestProfilesClient extends RestClient implements Profiles {

	public _TODO_RestProfilesClient(URI serverUri) {
		super(serverUri, RestProfiles.PATH);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		Response r = target.path(userId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<Profile>() {});
	}
	
	public Result<Void> createProfile( Profile profile ) {
		Response r = target
				.request()
				.post(Entity.entity(profile, MediaType.APPLICATION_JSON));
		return super.verifyResponse(r, Status.NO_CONTENT);
	}
	
	public Result<Void> deleteProfile( String userId ) {
		Response r = target.path(userId)
				.request()
				.delete();
		return super.verifyResponse(r, Status.NO_CONTENT);
	}
	
	public Result<List<Profile>> search( String prefix ) {
		Response r = target.queryParam("query", prefix)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.responseContents(r, Status.OK, new GenericType<List<Profile>>() {});
	}
	
	public Result<Void> follow( String userId1, String userId2, boolean isFollowing) {
		Response r = target.path(userId1).path("following").path(userId2)
				.request()
				.put(Entity.entity(isFollowing, MediaType.APPLICATION_JSON));
		return super.verifyResponse(r, Status.OK);
		
	}
	
	public Result<Boolean> isFollowing( String userId1, String userId2) {
		Response r = target.path(userId1).path("following").path(userId2)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {});
	}

	@Override
	public Result<Set<String>> getFollwed(String userId) {
		Response r = target.path(userId).path("following")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.responseContents(r, Status.OK, new GenericType<Set<String>>() {});
	}
}

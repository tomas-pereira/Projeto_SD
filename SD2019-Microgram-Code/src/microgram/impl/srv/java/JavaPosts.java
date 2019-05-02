package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import impl.clt.ClientFactory;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import utils.Hash;

import microgram.api.java.Profiles;

public class JavaPosts implements Posts {

	protected Map<String, Post> posts = new HashMap<>();
	protected Map<String, Set<String>> likes = new HashMap<>();
	protected Map<String, Set<String>> userPosts = new HashMap<>();
	private Profiles profiles;

	public JavaPosts(URI serverUri) {
		// user = null;
		profiles = ClientFactory.createProfilesClient(serverUri);
	}

	/*
	 * private void createProfilesClient() { if(user == null) { URI[] mediaURIs =
	 * Discovery.findUrisOf( "Microgram-Profiles", 1); user =
	 * ClientFactory.createProfilesClient(mediaURIs[0]); } }
	 */

	@Override
	public Result<Post> getPost(String postId) {
		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> deletePost(String postId) {
		Post post = posts.remove(postId);
		if (post == null)
			return error(NOT_FOUND);
		likes.remove(postId);
		userPosts.get(post.getOwnerId()).remove(postId);

		
		return ok();
	}

	@Override
	public Result<String> createPost(Post post) {
		String userId = post.getOwnerId();
		
		String postId = Hash.of(userId, post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, new HashSet<>());

			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = new LinkedHashSet<>());

			posts.add(postId);
		}
		return ok(postId);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Set<String> res = likes.get(postId);
		if (res == null)
			return error(NOT_FOUND);

		if (isLiked) {
			if (!res.add(userId))
				return error(CONFLICT);
		} else {
			if (!res.remove(userId))
				return error(NOT_FOUND);
		}

		getPost(postId).value().setLikes(res.size());
		return ok();
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);

		if (res != null)
			return ok(res.contains(userId));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		Set<String> result = new HashSet<String>();
		Result<Set<String>> getFollowed = profiles.getFollwed(userId);

		if(!getFollowed.isOK())
			return error(NOT_FOUND);
		
		Set<String> followed = getFollowed.value();
		
		for (String followedId : followed) {
			result.addAll(getPosts(followedId).value());
		}

		return ok(new ArrayList<String>(result));
	}

	public void deleteAllPosts(String userId) {

		Set<String> userposts = userPosts.get(userId);

		if (userposts != null) {

			for (String postId : userposts) {
				deletePost(postId);
				/*posts.remove(postId);
				userPosts.remove(postId);
				likes.remove(postId);*/
			}
			userPosts.remove(userId);
		}
	}
}

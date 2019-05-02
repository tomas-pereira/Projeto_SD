package impl.clt;

import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;

public class PostsClient {
	private static Logger Log = Logger.getLogger(PostsClient.class.getName());

	public static final String SERVICE = "Microgram-Posts";
	
	private static final Post post1 = new Post("post1", "joaomm", "link", "location", 0, 0);
	private static final String post1Id = "114BA4288D4D0C6DB3C224657FB109C4009F5FEE";
	private static final String userId = "olaola";
	
	public static void main(String[] args) throws Exception {

		URI[] mediaURIs = Discovery.findUrisOf(SERVICE, 1);
		if (mediaURIs.length > 0) {
			Posts post = ClientFactory.createPostsClient(mediaURIs[0]);

			System.out.println("Command:");
			Scanner in = new Scanner(System.in);
			String cmd = in.next().toUpperCase();
			
			while (!cmd.equals("EXIT")) {
				switch (cmd) {
				
				case "CREATEPOST":
					
					Result<String> uri = post.createPost(post1);
					
					if (uri.isOK())
						Log.info("Post created: " + uri.value());
					else
						Log.info("Creation failed, reason: " + uri.error());
					break;
				
				case "GETPOST":
					Result<Post> getPost = post.getPost(post1Id);
					
					if (getPost.isOK())
						Log.info("Post download: " + getPost.value().getPostId());
					else
						Log.info("Download failed, reason: " + getPost.error());
					break;
				
				case "DELETEPOST":
					Result<Void> delete = post.deletePost(post1Id);
					if (delete.isOK())
						Log.info("Post deleted: " + delete);
					else
						Log.info("Delete failed, reason: " + delete.error());
					break;
				
				case "LIKE":
					Boolean isLiked = true;
					Result<Void> like = post.like(post1Id, userId, isLiked);
					if (like.isOK())
						Log.info("Operation sucessful");
					else
						Log.info("Operation failed, reason: " + like.error());
					break;
	
				case "ISLIKED":
					Result<Boolean> bool = post.isLiked(post1Id, userId);
					if (bool.isOK())
						Log.info("Search success: " + bool);
					else
						Log.info("Search failed, reason: " + bool.error());
					break;
	
				case "GETPOSTS":
					Result<List<String>> getPosts = post.getPosts(post1.getOwnerId());
					if (getPosts.isOK())
						Log.info("Search success: " + getPosts);
					else
						Log.info("Search failed, reason: " + getPosts.error());
					break;
	
				case "GETFEED":
		
					break;
	
				}
				cmd = in.next().toUpperCase();
			}
		}
	}
}

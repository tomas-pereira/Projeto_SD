package impl.clt;

import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class ProfilesClient {
	private static Logger Log = Logger.getLogger(ProfilesClient.class.getName());

	public static final String SERVICE = "Microgram-Profiles";

	private static final Profile profile1 = new Profile("joaomm", "João Miguel", "link", 0, 0, 0);
	private static final Profile profile2 = new Profile("rogerios", "Rogerio S", "link", 0, 0, 0);
	private static final String prefix = "jo";
	
	public static void main(String[] args) throws Exception {

		URI[] mediaURIs = Discovery.findUrisOf(SERVICE, 1);
		if (mediaURIs.length > 0) {
			Profiles profile = ClientFactory.createProfilesClient(mediaURIs[0]);
			
			System.out.println("Command:");
			Scanner in = new Scanner(System.in);
			String cmd = in.next().toUpperCase();	
			
			while (!cmd.equals("EXIT")) {
				switch (cmd) {
				
				case "GETPROFILE":
					
					Result<Profile> getProfile = profile.getProfile(profile1.getUserId());
					if (getProfile.isOK())
						Log.info("Profile download: " + getProfile.value().getUserId());
					else
						Log.info("Download failed, reason: " + getProfile.error());
					break;
				
				case "CREATEPROFILE":
					
					Result<Void> uri = profile.createProfile(profile1);
					if (uri.isOK())
						Log.info("Profile created: " + uri);
					else
						Log.info("Creation failed, reason: " + uri.error());
					
					break;
					
				case "CREATEPROFILE2":
					
					Result<Void> uri2 = profile.createProfile(profile2);
					if (uri2.isOK())
						Log.info("Profile created: " + uri2);
					else
						Log.info("Creation failed, reason: " + uri2.error());
					
					break;
				
				case "DELETEPROFILE":
					Result<Void> delete = profile.deleteProfile(profile1.getUserId());
					if (delete.isOK())
						Log.info("Profile deleted: " + delete);
					else
						Log.info("Delete failed, reason: " + delete.error());
					break;
					
				case "SEARCH":
					
					Result<List<Profile>> list = profile.search(prefix);
					if (list.isOK())
						Log.info("Search success: " + list);
					else
						Log.info("Search failed, reason: " + list.error());
					
					break;
					
				case "FOLLOW":
					Boolean isFollowing = false;
					Result<Void> follow = profile.follow(profile1.getUserId(), profile2.getUserId(), isFollowing);
					if (follow.isOK())
						Log.info("Operation sucessful");
					else
						Log.info("Operation failed, reason: " + follow.error());
					break;
					
				case "ISFOLLOWING":
					Result<Boolean> bool = profile.isFollowing(profile1.getUserId(), profile2.getUserId());
					if (bool.isOK())
						Log.info("Search success: " + bool);
					else
						Log.info("Search failed, reason: " + bool.error());
					break;
				}
				cmd = in.next().toUpperCase();
			}
		}
	}
}

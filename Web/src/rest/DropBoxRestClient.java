package rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import shared.File;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static java.lang.System.in;
import static java.lang.System.setOut;


// Step 1: Create Dropbox Account

// Step 2: Create Application (https://www.dropbox.com/developers)

public class DropBoxRestClient {

	// Access codes #1: per application used to get access codes #2
	private static final String API_APP_KEY = "8lggmrv5xk31uew";
	private static final String API_APP_SECRET = "2dlze1yyj18nmkr";

	private OAuthService service;


	public DropBoxRestClient(String url){
		this.service = new ServiceBuilder()
				.provider(DropBoxApi.class)
				.apiKey(API_APP_KEY)
				.apiSecret(API_APP_SECRET)
				.callback(url)
				.build();
	}

	public String getUrl(){
		return service.getAuthorizationUrl(null);
	}

	public String getToken(String code){
		Verifier verifier = new Verifier(code);
		Token accessToken = service.getAccessToken(null, verifier);
		return accessToken.getToken();
	}

	public String getClientId(String token){
		OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_current_account", service);
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("Content-Type",  "");
		Response response = request.send();
		JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
		return (String) rj.get("account_id");
	}

	public ArrayList<File> listFiles(String token) {
		boolean keepGoing = false;
		String cursor = "";
		OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/files/list_folder", service);
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("Content-Type",  "application/json");
		request.addPayload("{\n" +
				"    \"path\": \"\",\n" +
				"    \"recursive\": true,\n" +
				"    \"include_media_info\": false,\n" +
				"    \"include_deleted\": false,\n" +
				"    \"include_has_explicit_shared_members\": false,\n" +
				"    \"include_mounted_folders\": true\n" +
				"}");
		ArrayList<File> files = new ArrayList<>();
		do {
			if(keepGoing){
				request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/files/list_folder/continue", service);
				request.addHeader("Authorization", "Bearer " + token);
				request.addHeader("Content-Type",  "application/json");
				request.addPayload("{\n" +
						"    \"cursor\": \""+ cursor + "\"\n" +
						"}");
			}
			Response response = request.send();
			System.out.println(response.getBody());
			keepGoing = false;
			JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
			JSONArray contents = (JSONArray) rj.get("entries");
			for (int i = 0; i < contents.size(); i++) {
				JSONObject item = (JSONObject) contents.get(i);
				if (((String) item.get(".tag")).equalsIgnoreCase("file")) {
					files.add(new File((String) item.get("name"), (String) item.get("id")));
				}
			}
			if(((boolean)rj.get("has_more"))){
				cursor = (String)rj.get("cursor");
				System.out.println(cursor);
				keepGoing = true;
			}
		}while(keepGoing);

		return files;
	}

	public String playFile(String clientId, String token) {
		OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/files/get_temporary_link", service);
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("Content-Type", "application/json");
		request.addPayload("{\n" +
				"    \"path\": \""+ clientId + "\"\n" +
				"}");
		Response response = request.send();
		System.out.println(clientId);
		System.out.println(response.getBody());

		JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
		return (String) rj.get("link");
	}

	public void shareFile(String fileID, String userId, String token){
		OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/sharing/add_file_member", service);
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("Content-Type",  "application/json");
		request.addPayload("{\n" +
				"    \"file\": \""+ fileID +"\",\n" +
				"    \"members\": [" +
				"    				{" +
				"   					\".tag\": \"dropbox_id\",\n" +
				"    					\"dropbox_id\": \"" + userId + "\"\n" +
				"    				}" +
				"	 			]\n" +
				"}");
		request.send();

	}

	private static void addFile(String path, OAuthService service, Token accessToken) {
      // TODO
	}

	private static void deleteFile(String path, OAuthService service, Token accessToken) {
      // TODO
	}


}

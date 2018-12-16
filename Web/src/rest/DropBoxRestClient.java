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

	public void downloadFile(String clientId, String token) {
		OAuthRequest request = new OAuthRequest(Verb.POST, "https://content.dropboxapi.com/2/files/download", service);
		request.addHeader("Authorization", "Bearer " + token);
		request.addHeader("Content-Type", "");
		request.addHeader("Dropbox-API-Arg", "{" +
				"    \"path\": \"" + clientId + "\"" +
				"}");
		Response response = request.send();
		System.out.println(response.getCode());
		InputStream in = new ByteArrayInputStream(response.getBody().getBytes(StandardCharsets.UTF_8));

		java.io.File file = new java.io.File("teste.pdf");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			int read = 0;
			byte[] buffer = new byte[1024];
			while ((read = in.read(buffer)) != -1) {
				fileOut.write(buffer, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		int ok = 16 << 10;
//		byte[] copyBuffer = new byte[ok];
//		try {
//
//			while (true) {
//				int count;
//				count = in.read(copyBuffer);
//				if (count == -1) break;
//				fileOut.write(copyBuffer, 0, count);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("wtf");
	}

	private static void addFile(String path, OAuthService service, Token accessToken) {
      // TODO
	}

	private static void deleteFile(String path, OAuthService service, Token accessToken) {
      // TODO
	}


}

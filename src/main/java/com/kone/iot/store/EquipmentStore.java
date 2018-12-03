package com.kone.iot.store;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.kone.iot.model.Equipment;

public class EquipmentStore {

	private Database db = null;
	private static final String databaseName = "mydb";

	public EquipmentStore() {
		CloudantClient cloudant = createClient();
		if (cloudant != null) {
			db = cloudant.database(databaseName, true);
		}
	}

	public Database getDB() {
		return db;
	}

	private static CloudantClient createClient() {

		String url;

		if (System.getenv("VCAP_SERVICES") != null) {
			JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
			if (cloudantCredentials == null) {
				System.out.println("No cloudant database service bound to this application");
				return null;
			}
			url = cloudantCredentials.get("url").getAsString();
		}else {
			System.out.println("Running locally. Looking for credentials in cloudant.properties");
			url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
		}

		try {
			System.out.println("Connecting to Cloudant");
			CloudantClient client = ClientBuilder.url(new URL(url)).build();
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			// e.printStackTrace();
			return null;
		}
	}

	public Collection<Equipment> get(int limit) {
		List<Equipment> docs;
		try {
			docs = db.getAllDocsRequestBuilder().limit(limit).includeDocs(true).build().getResponse()
					.getDocsAs(Equipment.class);
		} catch (IOException e) {
			return null;
		}
		return docs;
	}

	public Equipment get(String id) throws IOException {
		return db.find(Equipment.class, id);
	}

	public Equipment persist(Equipment equipment) {
		String id = db.save(equipment).getId();
		return db.find(Equipment.class, id);
	}

}

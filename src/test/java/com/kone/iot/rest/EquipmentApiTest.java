package com.kone.iot.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.kone.iot.model.Equipment;
import com.kone.iot.model.Status;
import com.kone.iot.store.EquipmentStore;

@RunWith(MockitoJUnitRunner.class)
public class EquipmentApiTest {
	@Mock
	private EquipmentStore equipmentStore;

	@InjectMocks
	private EquipmentApi equipmentApi;

	@Test
	public void returnsListOfEquipmentsIfEquipmentsArePresent() {
		when(equipmentStore.get(5)).thenReturn(Arrays.asList(new Equipment()));
		Response response = equipmentApi.getEquipments(5);
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity()).isNotNull();
		assertThat((List<Equipment>) response.getEntity()).hasSize(1);
	}

	@Test
	public void returnsNotFoundIfEquipmentsAreNotPresent() {
		when(equipmentStore.get(5)).thenReturn(new ArrayList<Equipment>());
		Response response = equipmentApi.getEquipments(5);
		assertThat(response.getStatus()).isEqualTo(404);
		assertThat((String) response.getEntity()).isEqualTo("No Data Available");

	}

	@Test
	public void returnsEquipmentIfEquipmentForIdPresent() throws IOException {
		Equipment equipment = new Equipment();
		when(equipmentStore.get("5")).thenReturn(equipment);
		Response response = equipmentApi.getEquipment("5");
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity()).isNotNull();
		assertThat((Equipment) response.getEntity()).isEqualTo(equipment);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void returnsNotFoundIfEquipmentIdNotPresent() throws IOException {
		when(equipmentStore.get("5")).thenThrow(NoDocumentException.class);
		Response response = equipmentApi.getEquipments(5);
		assertThat(response.getStatus()).isEqualTo(404);
		assertThat((String) response.getEntity()).isEqualTo("No Data Available");
	}

	@Test
	public void createEquipmentsReturnsBadRequestIfDateIsNotParseable() {
		Equipment equipment = new Equipment();
		equipment.set_id("100");
		equipment.setContractStartDate("7889");
		Response response = equipmentApi.createEquipment(equipment);
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat((String) response.getEntity()).isEqualTo("Provide proper date format dd/mm/yyyy");
	}

	@Test
	public void createEquipmentsReturnsBadRequestIfEquipmentNumberisNull() {
		Response response = equipmentApi.createEquipment(new Equipment());
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat((String) response.getEntity()).isEqualTo("Equipment Number is empty");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void createEquipmentsReturnsBadRequestIfDocumentAlreadyPresent() {
		when(equipmentStore.persist(Matchers.any(Equipment.class))).thenThrow(DocumentConflictException.class);
		Equipment equipment = new Equipment();
		equipment.set_id("101");
		equipment.setContractStartDate("12/12/2017");
		equipment.setContractEndDate("12/12/2018");
		equipment.setStatus(Status.Running);
		Response response = equipmentApi.createEquipment(equipment);
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat((String) response.getEntity()).isEqualTo("Equipment Already exists");
	}

	@Test
	public void createEquipmentsReturnsBadRequestIfEndDateIsBeforeStartDate() {
		Equipment equipment = new Equipment();
		equipment.set_id("101");
		equipment.setContractStartDate("12/12/2018");
		equipment.setContractEndDate("12/12/2017");
		Response response = equipmentApi.createEquipment(equipment);
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat((String) response.getEntity()).isEqualTo("End Date is before Start Date");
	}

}

package com.kone.iot.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.kone.iot.model.Equipment;
import com.kone.iot.store.EquipmentStore;

@ApplicationPath("EquipmentManager")
@Path("/equipment")
public class EquipmentApi extends Application {
	private EquipmentStore store;

	public EquipmentApi() {
		store = new EquipmentStore();
	}

	@GET
	@Path("/search")
	@Produces("application/json")
	public Response getEquipments(@QueryParam("limit") int limit) {
		if (store.get(limit).size() > 0) {
			return Response.status(Response.Status.OK).entity(store.get(limit)).build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("No Data Available").build();
	}

	@GET
	@Path("/{equipmentNumber}")
	@Produces("application/json")
	public Response getEquipment(@PathParam("equipmentNumber") String equipmentNumber) throws IOException {
		try {
			return Response.status(Response.Status.OK).entity(store.get(equipmentNumber)).build();
		} catch (NoDocumentException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("No Data Available").build();
		}
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response createEquipment(Equipment equipment) {
		try {
			if (equipment.get_id() == null || "".equals(equipment.get_id())) {
				return Response.status(Response.Status.BAD_REQUEST).entity("Equipment Number is empty").build();
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date startDate = formatter.parse(equipment.getContractStartDate());
			Date endDate = formatter.parse(equipment.getContractEndDate());
			if (endDate.before(startDate)) {
				return Response.status(Response.Status.BAD_REQUEST).entity("End Date is before Start Date").build();
			}

			return Response.status(Response.Status.OK).entity(store.persist(equipment)).build();
		} catch (ParseException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Provide proper date format dd/mm/yyyy").build();
		} catch (DocumentConflictException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Equipment Already exists").build();
		}
	}

}

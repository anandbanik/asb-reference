package com.asb.reference.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.asb.reference.model.StoreScanModel;



@Path("/scan")
@Produces({ "application/json"})
public interface StoreScanService {
	
	@POST
	@Path("/queue")
	public boolean queueStoreScan(StoreScanModel storeScanModel);

}

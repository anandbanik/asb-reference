package com.asb.reference.model;

import java.io.Serializable;


public class StoreScanModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private String storeId;
	

	private String lotNo;
	

	private String gtin;


	public String getStoreId() {
		return storeId;
	}


	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}


	public String getLotNo() {
		return lotNo;
	}


	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}


	public String getGtin() {
		return gtin;
	}


	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
	
	
	
}

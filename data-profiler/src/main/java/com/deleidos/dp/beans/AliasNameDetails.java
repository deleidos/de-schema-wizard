package com.deleidos.dp.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AliasNameDetails {
	private String aliasName;
	private String dsGuid;
	@JsonProperty("alias-name")
	public String getAliasName() {
		return aliasName;
	}	
	@JsonProperty("dsId")
	public String getDsGuid() {
		return dsGuid;
	}
	@JsonProperty("dsId")
	public void setDsGuid(String dsGuid) {
		this.dsGuid = dsGuid;
	}
	@JsonProperty("alias-name")
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AliasNameDetails) {
			AliasNameDetails otherAlias = (AliasNameDetails) obj;
			return otherAlias.getAliasName().equals(aliasName) && otherAlias.getDsGuid().equals(dsGuid);
		}
		return false;
	}
	
}

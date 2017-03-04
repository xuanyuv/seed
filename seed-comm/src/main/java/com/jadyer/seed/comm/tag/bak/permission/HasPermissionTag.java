package com.jadyer.seed.comm.tag.bak.permission;

public class HasPermissionTag extends PermissionTag {
	private static final long serialVersionUID = -5186397989078973832L;

	@Override
	protected boolean showTagBody(String permission){
		return this.isPermitted(permission);
	}
}
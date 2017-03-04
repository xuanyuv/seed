package com.jadyer.seed.comm.tag.bak.permission;

public class LacksPermissionTag extends PermissionTag {
	private static final long serialVersionUID = -6702087928102010950L;

	@Override
	protected boolean showTagBody(String permission){
		return !this.isPermitted(permission);
	}
}
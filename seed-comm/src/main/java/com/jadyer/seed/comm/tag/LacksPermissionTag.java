package com.jadyer.seed.comm.tag;

public class LacksPermissionTag extends PermissionTag {
	private static final long serialVersionUID = -1524099749494968922L;

	@Override
	protected boolean showTagBody(){
		return !this.isPermitted();
	}
}
package com.jadyer.seed.comm.tag;

public class HasPermissionTag extends PermissionTag {
	private static final long serialVersionUID = -2837917246768664260L;

	@Override
	protected boolean showTagBody(){
		return this.isPermitted();
	}
}
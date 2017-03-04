package com.jadyer.seed.comm.constant;

/**
 * 封装接口应答报文
 * Created by 玄玉<https://jadyer.github.io/> on 2015/6/3 21:57.
 */
public class CommonResult {
	/**
	 * 应答码（默认值为操作成功）
	 */
	private int code = CodeEnum.SUCCESS.getCode();
	/**
	 * 应答码描述（默认值为操作成功）
	 */
	private String msg = CodeEnum.SUCCESS.getMsg();
	/**
	 * 应答数据体（可以为空）
	 */
	private Object data = "";

	public CommonResult() {}

	/**
	 * 默认返回的操作码为CodeEnum.SUCCESS
	 */
	public CommonResult(Object data) {
		this(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), data);
	}

	/**
	 * 默认返回的data=""
	 */
	public CommonResult(int code, String msg) {
		this(code, msg, "");
	}
	
	public CommonResult(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
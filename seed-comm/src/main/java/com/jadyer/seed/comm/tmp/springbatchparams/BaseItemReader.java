package com.jadyer.seed.comm.tmp.springbatchparams;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;

import java.util.List;

public abstract class BaseItemReader<T> implements ItemReader<T> {
	private static Log log = LogFactory.getLog(BaseItemReader.class);

	protected StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, String Value) {
		this.stepExecution.getJobExecution().getExecutionContext().putString(key, Value);
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, long Value) {
		this.stepExecution.getJobExecution().getExecutionContext().putLong(key, Value);
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, int Value) {
		this.stepExecution.getJobExecution().getExecutionContext().putInt(key, Value);
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, List<Object> value) {
		this.stepExecution.getJobExecution().getExecutionContext().put(key, value);
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, Object value) {
		this.stepExecution.getJobExecution().getExecutionContext().put(key, value);
	}

	/**
	 * STEP参数取得
	 */
	public Object getStepParameter(String key) {
		return this.stepExecution.getJobExecution().getExecutionContext().get(key);
	}

	/**
	 * STEP参数取得
	 */
	public String getStringStepParameter(String key) {
		return this.stepExecution.getJobExecution().getExecutionContext().getString(key);
	}

	/**
	 * STEP参数取得
	 */
	public long getLongStepParameter(String key) {
		return this.stepExecution.getJobExecution().getExecutionContext().getLong(key);
	}

	/**
	 * STEP参数取得
	 */
	public int getIntStepParameter(String key) {
		return this.stepExecution.getJobExecution().getExecutionContext().getInt(key);
	}

	/**
	 * STEP参数递增(+1)
	 */
	public void addLongStepParameter(String key) {
		this.addLongStepParameter(key, 1);
	}

	/**
	 * STEP参数递增
	 */
	public void addLongStepParameter(String key, long cnt) {
		long value = this.getLongStepParameter(key);
		value = value + cnt;
		this.saveStepParameter(key, value);
	}

	/**
	 * STEP参数递增(+1)
	 */
	public void addIntStepParameter(String key) {
		this.addIntStepParameter(key, 1);
	}

	/**
	 * STEP参数递增
	 */
	public void addIntStepParameter(String key, int cnt) {
		int value = this.getIntStepParameter(key);
		value = value + cnt;
		this.saveStepParameter(key, value);
	}
}
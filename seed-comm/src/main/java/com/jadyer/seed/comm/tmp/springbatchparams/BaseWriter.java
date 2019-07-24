package com.jadyer.seed.comm.tmp.springbatchparams;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public abstract class BaseWriter<T> implements ItemWriter<T> {
	protected StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void write(List<? extends T> items) throws Exception {
		JobParameters params = stepExecution.getJobParameters();
		ExecutionContext stepContext = stepExecution.getExecutionContext();
		for (T item : items) {
			doWrite(item, params, stepContext);
		}
	}

	public abstract void doWrite(T item, JobParameters params, ExecutionContext stepContext) throws Exception;

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
	public void saveStepParameter(String key, Object Value) {
		this.stepExecution.getJobExecution().getExecutionContext().put(key, Value);
	}

	/**
	 * STEP参数保存
	 */
	public void saveStepParameter(String key, List<Object> value) {
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
		return this.stepExecution.getJobExecution().getExecutionContext().getLong(key, 0);
	}

	/**
	 * STEP参数取得
	 */
	public int getIntStepParameter(String key) {
		return this.stepExecution.getJobExecution().getExecutionContext().getInt(key, 0);
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
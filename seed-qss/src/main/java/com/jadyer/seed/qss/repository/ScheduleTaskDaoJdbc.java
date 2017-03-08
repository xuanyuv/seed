package com.jadyer.seed.qss.repository;

import com.jadyer.seed.qss.module.ScheduleTask;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ScheduleTaskDaoJdbc {
	@Resource
	private JdbcTemplate jdbcTemplate;
	private static final String SQL_GETBYID = "SELECT * FROM t_schedule_task WHERE id=?";

	public ScheduleTask getById(long id){
		try{
			return this.jdbcTemplate.queryForObject(SQL_GETBYID, new Object[]{id}, new UserRowMapper());
		}catch(EmptyResultDataAccessException e){
			return new ScheduleTask();
		}
	}

	private static class UserRowMapper implements RowMapper<ScheduleTask> {
		@Override
		public ScheduleTask mapRow(ResultSet rs, int index) throws SQLException {
			ScheduleTask task = new ScheduleTask();
			task.setId(rs.getLong("id"));
			task.setName(rs.getString("name"));
			task.setCron(rs.getString("cron"));
			task.setStatus(rs.getInt("status"));
			task.setConcurrent(rs.getInt("concurrent"));
			task.setUrl(rs.getString("url"));
			task.setComment(rs.getString("comment"));
			task.setCreateTime(rs.getDate("create_time"));
			task.setUpdateTime(rs.getDate("update_time"));
			return task;
		}
	}
}
package com.jztey.demo.dao;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.springframework.util.CollectionUtils;

import com.jztey.demo.entity.ExamArea;
import com.jztey.demo.entity.User;
import com.jztey.framework.mvc.BaseDao;

@Named
public class ExamAreaDao extends BaseDao<ExamArea> {

	public List<ExamArea> getAll() throws Exception {

		List<ExamArea> result = this.em.createQuery("select d   from ExamArea d where 1=1", ExamArea.class)
				.getResultList();

		return CollectionUtils.isEmpty(result) ? null : result;

	}

}

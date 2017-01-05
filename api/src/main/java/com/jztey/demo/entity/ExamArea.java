package com.jztey.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.jztey.framework.mvc.Id;

/**
 * Created by yushi on 2016/8/19.
 */
@Entity
@Table(name = "exam_org_center")
public class ExamArea implements Id, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 191375791970697574L;
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "exam_center_id")
	private String examCenterId;// '体检中心编码'
	@Column(name = "exam_center_name")
	private String examCenterName;// '检体中心名称'
	@Column(name = "province")
	private String province;// '省'
	@Column(name = "city")
	private String city;// '市'
	@Column(name = "area")
	private String area;// 区
	@Column(name = "tcids")
	private String tcids;
	@Column(name = "org_id")
	private String orgId;// '检体中心所属公司'

	public String getExamCenterId() {
		return examCenterId;
	}

	public void setExamCenterId(String examCenterId) {
		this.examCenterId = examCenterId;
	}

	public String getExamCenterName() {
		return examCenterName;
	}

	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTcids() {
		return tcids;
	}

	public void setTcids(String tcids) {
		this.tcids = tcids;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}

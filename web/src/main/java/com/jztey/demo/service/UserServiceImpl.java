package com.jztey.demo.service;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.jztey.demo.dao.ExamAreaDao;
import com.jztey.demo.dao.UserDao;
import com.jztey.demo.entity.Demo;
import com.jztey.demo.entity.DemoGetReponse;
import com.jztey.demo.entity.DemoReponse;
import com.jztey.demo.entity.ExamArea;
import com.jztey.demo.entity.User;
import com.jztey.demo.entity.UserVo;
import com.jztey.framework.cache.SpelCacheNameCacheResolver;
import com.jztey.framework.mvc.BaseDao;
import com.jztey.framework.mvc.BaseService;
import com.jztey.framework.mvc.Paging;
import com.jztey.framework.mvc.RestfulPagingResult;
import com.jztey.framework.mvc.RestfulResult;
import com.jztey.framework.mvc.RestfulResultException;
import com.jztey.framework.util.HttpClient;

@Service
@CacheConfig(cacheNames = SpelCacheNameCacheResolver.SPEL_CACHE_NAME)
@com.alibaba.dubbo.config.annotation.Service
@ConfigurationProperties(prefix = "demoN", locations = { "classpath:test.properties" }) // 双重配置文件读取，自定义
public class UserServiceImpl extends BaseService<User> implements UserService {
	private final static String DEMO_KEY = "DEMO: com.jztey.demo.service.UserServiceImpl";
	@Autowired
	private UserDao userDao;

	private String userNameN;// 自定义读取配置文件的变量

	public String getUserNameN() {
		return userNameN;
	}

	public void setUserNameN(String userNameN) {
		this.userNameN = userNameN;
	}

	@Value("${demo.account}")
	private String configaccount;
	@Value("${demo.remark}")
	private String remark;

	@Value("${test.url}")
	private String testUrl;
	@Value("${test.url.get}")
	private String testUrlGet;

	@Inject
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valueOperations;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private ExamAreaDao examAreaDao;

	@Override
	public BaseDao<User> getDao() {
		return userDao;
	}

	@Override
	@Transactional
	public RestfulResult<User> insertOrUpdate(User user) {
		User result = userDao.findByUniqueKey(user.getName());
		if (null == result) {
			userDao.persist(user);// 没有就增加
		} else {
			result.setId(user.getId());// 有就更新
			userDao.merge(result);
		}
		return new RestfulResult<>(user);
	}

	@Override
	@Transactional
	public RestfulResult<User> delete(int age, int sex) {
		List<User> result = userDao.findByKeys(age, sex);
		if (null != result) {
			for (User user : result) {
				this.remove(user.getId());// 进行批量删除
			}
			return new RestfulResult<>(result);
		}
		return new RestfulResult<>();
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulPagingResult<User> findPage(int sex, int page, int pageSize, int total) {
		System.out.println("get");

		User query = new User();
		query.setSex(sex);
		// 统一使用查询接口
		Paging<User> paging = new Paging<>(page, pageSize, query);
		paging.setTotal(total);
		return this.search(paging);
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulPagingResult<User> search(Paging<User> paging) {
		System.out.println("search");
		List<User> userList = new ArrayList<>();

		if (-1 == paging.getTotal()) { // total没有传上来
			// 查询total
			paging.setTotal(this.countByExample(paging.getQuery()).intValue());
		}

		userList = this.findByExample(paging);
		return new RestfulPagingResult<User>(userList, paging.getTotal());
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulPagingResult<User> findPageByEntity(UserVo user) {
		System.out.println("findPageByEntity");
		User query = new User();
		query.setSex(user.getUser().getSex());
		// 统一使用查询接口
		Paging<User> paging = new Paging<>(user.getPaging().getPage(), user.getPaging().getPageSize(), query);
		paging.setTotal(user.getPaging().getTotal());
		return this.search(paging);

	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<User> findBySex(int sex) {
		System.out.println("findPageBySex");
		List<User> result = userDao.findByKey(sex);
		return new RestfulResult<>(result);
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<User> findBySexSimple(int sex) {
		return findBySex(sex);
	}

	@Override
	public RestfulPagingResult<User> paramHeader(String headParam) {
		System.out.println("headParam:" + headParam);
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<Map<String, Map<String, List<ExamArea>>>> getAll() throws Exception {
		RestfulResult<Map<String, Map<String, List<ExamArea>>>> restfulResult = new RestfulResult<>();
		boolean success = false;
		String message = "查询失败，传入参数错误";

		try {
			List<ExamArea> list = examAreaDao.getAll();

			// 省-市, 市-中心列表
			Map<String, Map<String, List<ExamArea>>> treeResult = new HashMap<>();
			for (ExamArea examArea : list) {
				Map<String, List<ExamArea>> provinceMap = treeResult.get(examArea.getProvince());// 先进行取，这样可以避免重复的数据产生
				if (null == provinceMap) {// 没有才进行加入
					provinceMap = new HashMap<>();
					treeResult.put(examArea.getProvince(), provinceMap);
				}
				List<ExamArea> cityList = provinceMap.get(examArea.getCity());// 得到市下面的中心
				if (null == cityList) {
					cityList = new ArrayList<>();
					provinceMap.put(examArea.getCity(), cityList);
				}
				cityList.add(examArea);
			}
			restfulResult.addData(treeResult);
			success = true;
			message = "查询成功";

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			message = "查询失败";
		}

		restfulResult.setMessage(message);
		restfulResult.setSuccess(success);
		return restfulResult;

	}

	@Override
	public RestfulResult<String> getConfig() throws Exception {
		String configvalue = configaccount + "----" + remark;
		String configvalueN = configvalue + "$$$$$$$" + userNameN;
		return new RestfulResult<>(configvalueN);
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<User> findBySexPathParam(int sex) {
		return findBySex(sex);
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<User> findBySexQueryParam(int sex) {
		return findBySex(sex);
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<User> getException(int id) {

		if (id < 0) { // 意外异常
			Integer i = null;
			i.toString();
		}
		if (id < 1) { // 故意抛出普通异常
			throw new RuntimeException("发生RuntimeException错误啦");
		}
		if (id < 2) { // 自定义异常, 快速方式
			throw new RestfulResultException("环信服务器连接失败");
		}
		if (id < 3) { // 自定义异常, 指定http code
			RestfulResult<String> rr = new RestfulResult<>();
			rr.setCode(401);
			rr.setMessage("权鉴失败");
			rr.setSuccess(false);
			throw new RestfulResultException(rr);
		}
		if (id < 4) { // 自定义异常, 完全自定义
			RestfulResult<String> rr = new RestfulResult<>();
			rr.setCode(10001);
			rr.setMessage("咨询的医生不在线");
			rr.setSuccess(false);
			ResponseEntity<RestfulResult> re = new ResponseEntity<RestfulResult>(rr, HttpStatus.OK);
			throw new RestfulResultException(re);
		}
		return new RestfulResult<>();
	}

	@Override
	@Transactional(readOnly = true)
	public RestfulResult<Demo> getConfigHttpClient(int healthAccount, String httpType) throws Exception {

		HttpClient httpClient = HttpClient.instance();
		String url = testUrl + healthAccount;
		String urlGet = testUrlGet + healthAccount;
		DemoReponse demoReponse = null;
		Map<String, Integer> para = new HashMap<>();
		List<Demo> list = new ArrayList<>();
		DemoGetReponse demoGetReponse = null;
		try {
			if ("post".equalsIgnoreCase(httpType)) {
				demoReponse = httpClient.invoke(url, para, true, DemoReponse.class);// 目前仅支持post，没有封装其他的
				list = demoReponse.getData();
			} else if ("get".equalsIgnoreCase(httpType)) {
				RestTemplate restTemplate = new RestTemplate();
				MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
				// headers.add("Authorization", "Bearer " +
				// this.getAdminToken());
				headers.add("Content-Type", "application/json");

				RequestEntity<HashMap> requestEntity = new RequestEntity("", headers, HttpMethod.GET, new URI(urlGet));
				// HashMap response = restTemplate.exchange(requestEntity,
				// HashMap.class).getBody();
				demoGetReponse = restTemplate.exchange(requestEntity, DemoGetReponse.class).getBody();
				// List<Map<String, Object>> data = (List<Map<String, Object>>)
				// response.get("data");
				list = demoGetReponse.getData();

			} else {// 调用framework中的http的get方法

				demoGetReponse = httpClient.invoke(urlGet, para, false, true, DemoGetReponse.class);
				list = demoGetReponse.getData();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RestfulResult<>(list);
	}

	@Override
	public String getRedis(int healthAccount) throws Exception {

		User user = new User();
		user.setId(Long.parseLong(healthAccount + ""));

		Object result = null;
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		result = operations.get(DEMO_KEY);
		if (null != result) {
			System.out.println("缓存中的数据：" + ((User) result).getId());
		} else {
			System.out.println("设置缓存");
			operations.set(DEMO_KEY, user);

			redisTemplate.expire(DEMO_KEY, 60l, TimeUnit.SECONDS);// 设置过期时间

		}

		return healthAccount + "";
	}

	
 
}

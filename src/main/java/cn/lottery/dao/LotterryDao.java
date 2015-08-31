package cn.lottery.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.lottery.model.LotteryModel;

@Repository
public class LotterryDao {
	// @Autowired
	// private JdbcTemplate jdbcTemplate;
	@Autowired
	private SessionFactory sessionFactory;

	public List<LotteryModel> query(LotteryModel params) {
		String sql = "from LotteryModel t where 1=1";
		
		Object value = params.getCdCode();
		if(StringUtils.isNoneBlank((String)value)){
			sql += " and t.cdCode = :cdCode";
		}
		
		if(StringUtils.isNoneBlank(params.getName())){
			sql += " and t.name like :name";
			params.setName("%" + params.getName()+"%");
		}
		if(StringUtils.isNoneBlank(params.getCode())){
			sql += " and t.code = :code";
		}
		
		sql += " order by t.createTime desc,t.id asc";
		Query query = sessionFactory.getCurrentSession().createQuery(sql);
		query.setProperties(params);
		return query.list();
	}

	public int deleteById(Long id) {
		String sql = "delete from t_lottery where id=:id";
		
		Query query= sessionFactory.getCurrentSession().createSQLQuery(sql);
		query.setParameter("id", id);
		return query.executeUpdate();
	}
	
	public void save(LotteryModel model){
		sessionFactory.getCurrentSession().save(model);
	}

	public LotteryModel findById(Long id) {
		return (LotteryModel) this.sessionFactory.getCurrentSession().get(LotteryModel.class, id);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> tj(String type) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) as c,sum(money) as money from t_lottery where 1=1");
		Map<String,Object> params= new HashMap<String, Object>();
		if(StringUtils.isNoneBlank(type)){
			sql.append(" and type=:type");
			params.put("type", type);
		}
		Query query = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setProperties(params);
		return (Map<String, Object>) query.uniqueResult();
	}
}

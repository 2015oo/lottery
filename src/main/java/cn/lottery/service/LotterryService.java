package cn.lottery.service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lottery.dao.LotterryDao;
import cn.lottery.model.LotteryModel;

@Service
@Transactional
public class LotterryService {
	@Autowired
	private LotterryDao dao;

	@Value("${app.startStr}")
	private String stratStr;
	@Value("${app.endStr}")
	private String endStr;

	FastDateFormat dataFormat = FastDateFormat.getInstance("yyyy-MM-dd");
	/**
	 * 1 保存成功 其他失败，失败原因
	 * 
	 * @param lotteryModel
	 * @return
	 * @throws ParseException 
	 */
	public String save(LotteryModel lotteryModel) throws ParseException {
		// 检查 身份证号
		// 1、检查时间
		Date createTime = new Date();
		Date start = dataFormat.parse(stratStr);
		Date end = dataFormat.parse(endStr);

		if (createTime.before(start) || createTime.after(end)) {
			return "2";// "不在活动时间范围内";
		}

		// 检查传单号是否已经使用
		if ("1".equals(lotteryModel.getType())) {
			LotteryModel qu2 = new LotteryModel();
			qu2.setCdCode(lotteryModel.getCdCode());
			if (this.query(qu2).size() > 0) {
				return "3";// "该传单号已经使用!";
			}
		}

		// 检查同一个身份证号用了几次
		LotteryModel qu = new LotteryModel();
		qu.setCode(lotteryModel.getCode());
		List<LotteryModel> list = this.query(qu);
		if (list.size() >= 3) {
			return "4";// "该身份证号码已经用过三次了";
		}

		//检查当天该身份证是否用过一次
		if(list!=null && list.size()>0){
			LotteryModel l = list.get(0);
			Date d = l.getCreateTime();
			if(dataFormat.format(d).equals(dataFormat.format(createTime))){
				return "5";//一天只能用一次
			}
		}
		
		
		lotteryModel.setCreateTime(createTime);
		this.dao.save(lotteryModel);
		return "1";
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public int delete(Long id) {
		return this.dao.deleteById(id);
	}

	/**
	 * 查询
	 * 
	 * @param params
	 * @return
	 */
	public List<LotteryModel> query(LotteryModel params) {
		return this.dao.query(params);
	}

	public LotteryModel findById(Long id) {
		return this.dao.findById(id);
	}

	public Map<String, Object> tj() {
		
		Map<String,Object> result = new HashMap<String, Object>();
		//统计满堂彩
		String strForm = "人数：%s，金额：%s";
		String type = "2";
		Map<String,Object> mtctj = this.dao.tj(type);
		Object c = mtctj.get("C");
		Object summoney = mtctj.get("MONEY");
		result.put("mtctj", String.format(strForm, String.valueOf(c),String.valueOf(summoney)));
		
		//统计彩中彩
		type = "1";
		mtctj = this.dao.tj(type);
		c = mtctj.get("C");
		summoney = mtctj.get("MONEY");
		result.put("csctj", String.format(strForm, String.valueOf(c),String.valueOf(summoney)));
		//总统计
		//统计彩中彩
		type = "";
		mtctj = this.dao.tj(type);
		c = mtctj.get("C");
		summoney = mtctj.get("MONEY");
		result.put("ztj", String.format(strForm, String.valueOf(c),String.valueOf(summoney)));
		return result;
	}
}

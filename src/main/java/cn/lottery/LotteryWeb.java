package cn.lottery;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lottery.model.LotteryModel;
import cn.lottery.service.LotterryService;

@Controller
@RequestMapping("/lottery")
public class LotteryWeb {
	@Autowired
	private LotterryService service;

	@ModelAttribute
	public void init(Model model,HttpServletRequest request){
		model.addAttribute("ctx", request.getContextPath());
	}
	
	@RequestMapping("index")
	public String index() {
		return "index";
	}
	
	@RequestMapping("query")
	@ResponseBody
	public Object query(LotteryModel lotteryModel,Model model) {
		return this.service.query(lotteryModel);
	}

	@RequestMapping("view")
	public String view(Long id,Model model) {
		LotteryModel lottery  = this.service.findById(id);
		model.addAttribute("lottery", lottery);
		return "view";
	}

	@RequestMapping("save")
	@ResponseBody
	public Object save(LotteryModel lotteryModel) throws ParseException{
		String result = this.service.save(lotteryModel);
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("msg", result);
		if(lotteryModel.getId()!=null){
			resultMap.put("success", true);
		}else{
			resultMap.put("success", false);
		}
		return resultMap;
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Object delete(Long id){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		int result = this.service.delete(id);
		resultMap.put("success", result==1);
		return resultMap;
	}
	@RequestMapping("tj")
	@ResponseBody
	public Object tj(){
		Map<String,Object> resultMap  = this.service.tj();
		return resultMap;
	}
	
}

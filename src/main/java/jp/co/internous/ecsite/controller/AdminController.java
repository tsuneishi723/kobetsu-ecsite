package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.ecsite.model.domain.MstGoods;
import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.form.GoodsForm;
import jp.co.internous.ecsite.model.form.LoginForm;
import jp.co.internous.ecsite.model.mapper.MstGoodsMapper;
import jp.co.internous.ecsite.model.mapper.MstUserMapper;

@Controller
@RequestMapping("/ecsite/admin")
public class AdminController {

	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private MstGoodsMapper goodsMapper;
	
	@RequestMapping("/")
	public String index() {
		return "admintop";
	}
	
	@PostMapping("/welcome")
	public String welcome(LoginForm form,Model model) {
		
		MstUser user = userMapper.findByUserNameAndPassword(form);
		
		if(user == null) {
			model.addAttribute("errMessage" ,"ユーザー名またはパスワードが違います。");
			return "forward:/ecsite/admin/";
		}
		
		if(user.getIsAdmin() == 0) {
			model.addAttribute("errMessage","管理者ではありません。");
			return "forward:/ecsite/admin/";
		}
		//商品のリスト並べているmst_goodsテーブルから商品情報をすべて取得
		List<MstGoods> goods = goodsMapper.findAll();
		model.addAttribute("userName", user.getUserName());
		model.addAttribute("password",user.getPassword());
		model.addAttribute("goods",goods);
		
		return "welcome";
	}
	
	@PostMapping("/goodsMst")
	public String goodsMst(LoginForm f, Model m) {
		m.addAttribute("userName", f.getUserName());
		m.addAttribute("password", f.getPassword());
		
		return "goodsmst";
	}
	
	@PostMapping("/addGoods")
	public String addGoods(GoodsForm goodsForm, LoginForm loginForm, Model m) {
		m.addAttribute("userName", loginForm.getUserName());
		m.addAttribute("password", loginForm.getPassword());
		
		//インスタンス生成
		MstGoods goods = new MstGoods();
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		
		goodsMapper.insert(goods);
		
		return "forward:/ecsite/admin/welcome";
	}
	
	@ResponseBody
	@PostMapping("/api/deleteGoods")
	public String deleteApi(@RequestBody GoodsForm f,Model m) {
		try {
			goodsMapper.deleteById(f.getId());
		} catch(IllegalArgumentException e) {
			return "-1";
		}
		
		return "1";
	}
	
	@PostMapping("/goodsEdit")
	public String goodsEdit(GoodsForm goodsForm, LoginForm loginForm,  Model m) {
		m.addAttribute("userName",loginForm.getUserName());
		m.addAttribute("password",loginForm.getPassword());
		m.addAttribute("id",goodsForm.getId());
		m.addAttribute("goodsName",goodsForm.getGoodsName());
		m.addAttribute("price",goodsForm.getPrice());
		
		
		return "goodsedit";
	}
	
	@PostMapping("/fixedGoods")
	public String fixedGoods(GoodsForm goodsForm, LoginForm loginForm, Model m) {
		m.addAttribute("userName", loginForm.getUserName());
		m.addAttribute("password", loginForm.getPassword());
		
		MstGoods goods = new MstGoods();
		goods.setId(goodsForm.getId());
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		
		goodsMapper.updateById(goods);
		
		System.out.println("userName:"+loginForm.getUserName());
		System.out.println("password:"+loginForm.getPassword());
		return "forward:/ecsite/admin/welcome";
	}
}

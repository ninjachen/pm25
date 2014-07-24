package me.ninjachen;

public abstract interface API {
	public static final String BaiduGeocoderAPI = "http://api.map.baidu.com/geocoder/v2/?ak=1919b4b56ae680106f8d1dfa5f7244d9&location=%s5&output=json";
	//http://api.map.baidu.com/geocoder/v2/?ak=1919b4b56ae680106f8d1dfa5f7244d9&location=22.5329294,114.019329&output=json
	public static final String GoogleGeocoderAPI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s&sensor=true";
	//http://maps.googleapis.com/maps/api/geocode/json?latlng=22.5329294,114.019329&sensor=true
	public static final String PM25API = "http://www.pm25.in/api/querys/aqi_details.json?token=5j1znBVAsnSf5xQyNQyq&city=%s&stations=yes";
	//http://www.pm25.in/api/querys/aqi_details.json?token=4esfG6UEhGzNkbszfjAp&city=shenzhen&stations=yes
	public static final String PM25FeedbackAPI = "http://www.pm25.in/api/update/feedback.json?token=5j1znBVAsnSf5xQyNQyq&city";
	public static final String[] cities = { "change city", "auto", "baoding",
			"beijing", "cangzhou", "changzhou", "chengde", "chengdu", "dalian",
			"dongguan", "foshan", "fuzhou", "guangzhou", "guiyang", "haerbin",
			"haikou", "handan", "hangzhou", "hefei", "hengshui", "huaian",
			"huhehaote", "huizhou", "huzhou", "jiangmen", "jiaxing", "jinan",
			"jinhua", "kunming", "langfang", "lanzhou", "lasa", "lianyungang",
			"lishui", "nanchang", "nanjing", "nanning", "nantong", "ningbo",
			"qingdao", "qinhuangdao", "quzhou", "shamen", "shanghai",
			"shaoxing", "shenyang", "shenzhen", "shijiazhuang", "suqian",
			"suzhou", "taiyuan", "taizhou", "taizhoushi", "tangshan",
			"tianjin", "wenzhou", "wuhan", "wulumuqi", "wuxi", "xian",
			"xiangtan", "xingtai", "xining", "xuzhou", "yancheng", "yangzhou",
			"yinchuan", "zhangchun", "zhangjiakou", "zhangsha", "zhaoqing",
			"zhengzhou", "zhenjiang", "zhongqing", "zhongshan", "zhoushan",
			"zhuhai", "zhuzhou" };
}

package cn.oomall.function.weixin.config;


public class OOWeixinConfig {
    private final String  interfaceURI;
    
	/**
	 * 图片server地址 
	 */
    private final String  imageServerURL;
    
    private final String ovcmServerURI;
    
    
    /**
     * 二维码logo地址
     */
    private final String qrcodeLogPath;
    
    private final String controllerURI;
    public OOWeixinConfig(String interfaceURI,String imageServerURL,String ovcmServerURI,String qrcodeLogPath,String controllerURI) {
    	this.interfaceURI=interfaceURI;
    	this.imageServerURL=imageServerURL;
    	this.ovcmServerURI = ovcmServerURI;
    	this.qrcodeLogPath = qrcodeLogPath;
    	this.controllerURI = controllerURI;
    }
    
	public String getInterfaceURI() {
		return interfaceURI;
	}
	public String getImageServerURL() {
		return imageServerURL;
	}
	public String getOvcmServerURI() {
		return ovcmServerURI;
	}
	public String getQrcodeLogPath() {
		return qrcodeLogPath;
	}

	public String getControllerURI() {
		return controllerURI;
	}
}

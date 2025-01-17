/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author n00b
 */
public class BurpExtender implements IBurpExtender, ITab, IHttpListener, IProxyListener {
    
    public String ExtensionName =  "XES Killer";
    public String TabName =  "XES Killer";
    public String _Header = "XES Killer";
    XES_Killer _xes_killer;
    
    public IBurpExtenderCallbacks callbacks;
    public IExtensionHelpers helpers;
    public PrintWriter stdout;
    public PrintWriter stderr;
    public Boolean isDebug = true;
    public Boolean isRunning = false;
    
    public Cipher cipher;
    public SecretKeySpec sec_key;
    public IvParameterSpec iv_param;
   
    public String _host;
    public String _enc_type;
    public String _algorithm;
    public String _secret_key;
    public String _iv_param;
    public String[] _req_param;
    public String[] _res_param;
    
    public String[] _obffusicatedChar;
    public String[] _replaceWithChar;
    
    public Boolean _exclude_iv = false;
    public Boolean _hex_format = false;
    public Boolean _ignore_response = false;
    public Boolean _do_off = false;
    public Boolean _url_enc_dec = false;
    public Boolean _is_req_body = false;
    public Boolean _is_res_body = false;
    public Boolean _is_req_param = false;
    public Boolean _is_res_param = false;
    public Boolean _is_ovrr_req_body = false;
    public Boolean _is_ovrr_res_body = false;
    public Boolean _is_ovrr_req_body_form = false;
    public Boolean _is_ovrr_res_body_form = false;
    public Boolean _is_ovrr_req_body_json = false;
    public Boolean _is_ovrr_res_body_json = false;
    

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        this.callbacks.setExtensionName(this.ExtensionName);
        this.callbacks.registerContextMenuFactory(new CryptoMenuFactory(this));
        
        _xes_killer = new XES_Killer(this);
        this.callbacks.addSuiteTab(this);
        this.stdout.println("XES_Killer Installed !!!");
    }

    @Override
    public String getTabCaption() {
        return this.TabName;
    }

    @Override
    public Component getUiComponent() {
        return this._xes_killer;
    }
//    @Override
//    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
//        ArrayList<JMenuItem> menu_item_list = new ArrayList<JMenuItem>();
//        }
//
    public void start_aes_killer(){
        this.callbacks.registerHttpListener(this);
        this.callbacks.registerProxyListener(this);
        this.isRunning = true;
    }
    
    public void stop_aes_killer(){
        this.callbacks.removeHttpListener(this);
        this.callbacks.removeProxyListener(this);
        this.isRunning = false;
    }

    public void print_output(String _src, String str){
        if(! isDebug){ return; }
        this.stdout.println();
        this.stdout.println("------------------------"+_src+"-----------------------------------");
        this.stdout.println(str);
        this.stdout.println("-------------------------------------------------------------------");
        this.stdout.println();
    }

    public void print_error(String _src, String str){
        if(! isDebug){ return; }
        this.stdout.println();
        this.stdout.println("------------------------"+_src+"-----------------------------------");
        this.stderr.println(str);
        this.stdout.println("-------------------------------------------------------------------");
        this.stdout.println();
    }
    
    public String get_host(String _url){
        try{
            URL abc = new URL(_url);
            return abc.getHost().toString();
        }catch (Exception ex){
            print_error("get_endpoint", _url);
            return _url;
        }
    }

    public String remove_0bff(String _paramString) {
        if (_paramString != null) {
          for(int i =0; i< this._obffusicatedChar.length; i++){
              _paramString = _paramString.replace(this._replaceWithChar[i], this._obffusicatedChar[i]);
          }
          return _paramString;
        }
        return _paramString;
    }
    
    public String do_0bff(String _paramString) {
        if (_paramString != null) {
          for(int i =0; i< this._obffusicatedChar.length; i++){
              _paramString = _paramString.replace(this._obffusicatedChar[i], this._replaceWithChar[i]);
          }
          return _paramString;
        }
        return _paramString;
    }

    public String do_decrypt(String _enc_str){

        try{
            this._algorithm=this._enc_type.substring(0,3);
            cipher = Cipher.getInstance(this._enc_type);
            sec_key = new SecretKeySpec(this._secret_key.getBytes(StandardCharsets.UTF_8),this._algorithm);
//            if (this._hex_format){
//
//                sec_key = new SecretKeySpec(this._secret_key.getBytes(StandardCharsets.UTF_8),this._algorithm);
//            }
//            else {
//                sec_key = new SecretKeySpec(this.helpers.base64Decode(this._secret_key), this._algorithm);
//            }
            if (this._enc_type.indexOf("ECB")>0){
                cipher.init(Cipher.DECRYPT_MODE, sec_key);
            }
            else {
                iv_param = new IvParameterSpec(this._iv_param.getBytes(StandardCharsets.UTF_8));
                cipher.init(Cipher.DECRYPT_MODE, sec_key, iv_param);
            }
            
            if (this._url_enc_dec) { _enc_str = this.helpers.urlDecode(_enc_str); }
            if (this._do_off) { _enc_str = this.remove_0bff(_enc_str); }

            if (this._hex_format){
                _enc_str = new String(cipher.doFinal(this.hex2str(_enc_str)));
            }
            else {
                _enc_str = new String(cipher.doFinal(this.helpers.base64Decode(_enc_str)));
            }

            return _enc_str;
        }catch(Exception ex){
            print_error("do_decrypt", ex.getMessage());
            return _enc_str;
        }
    }



    public String do_encrypt(String _dec_str){
        try{
            this._algorithm=this._enc_type.substring(0,3);
            cipher = Cipher.getInstance(this._enc_type);
            if(this._algorithm.equals("DES")){
                    this._secret_key = this._secret_key + "\0\0\0\0\0\0\0\0";
                    this._secret_key = this._secret_key.substring(0, 8);
                    this._iv_param = this._iv_param + "\0\0\0\0\0\0\0\0";
                    this._iv_param = this._iv_param.substring(0, 8);
            }
            if(this._algorithm.equals("AES")){
                    this._secret_key = this._secret_key + "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
                    this._secret_key = this._secret_key.substring(0, 16);
                    this._iv_param = this._iv_param + "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
                    this._iv_param = this._iv_param.substring(0, 16);
            }
            sec_key = new SecretKeySpec(this._secret_key.getBytes(StandardCharsets.UTF_8),this._algorithm);
//            if (this._hex_format){
//                sec_key = new SecretKeySpec(this._secret_key.getBytes(StandardCharsets.UTF_8),this._algorithm);
//            }
//            else {
//                sec_key = new SecretKeySpec(this.helpers.base64Decode(this._secret_key), this._algorithm);
//            }

            if (this._enc_type.indexOf("ECB")>0){
                cipher.init(Cipher.ENCRYPT_MODE, sec_key);
            }
            else {
                iv_param = new IvParameterSpec(this._iv_param.getBytes(StandardCharsets.UTF_8));
                cipher.init(Cipher.ENCRYPT_MODE, sec_key, iv_param);
            }
            if (this._hex_format){
                byte[] enc = cipher.doFinal(_dec_str.getBytes());
                _dec_str = new BigInteger(1,enc).toString(16);
            }
            else {
                _dec_str =this.helpers.base64Encode(cipher.doFinal(_dec_str.getBytes()));
            }
            if (this._do_off) { _dec_str = this.do_0bff(_dec_str); }
            if (this._url_enc_dec) { _dec_str = this.helpers.urlEncode(_dec_str); }
            return _dec_str;
        }catch(Exception ex){
            print_error("do_decrypt", ex.getMessage());
            return _dec_str;
        }
    }

    public  byte[] hex2str(String str) throws Exception {
        byte[] bytes = str.getBytes();
        int length = bytes.length;
        byte[] bArr = new byte[length / 2];
        for (int i2 = 0; i2 < length; i2 += 2) {
            bArr[i2 / 2] = (byte) Integer.parseInt(new String(bytes, i2, 2), 16);
        }
//        String ret = new String(bArr); //返回字符串

        return bArr; //返回数组
}
    public  String str2hex(String str) throws Exception {
        byte[] bArr = str.getBytes();
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i2 : bArr) {
            while (i2 < 0) {
                i2 += 256;
            }
            if (i2 < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Integer.toString(i2, 16));
        }
        return new String(stringBuffer);
    }



    public byte[] update_req_params (byte[] _request, List<String> headers, String[] _params, Boolean _do_enc){
        for(int i = 0 ; i < _params.length; i++){
            IParameter _p = this.helpers.getRequestParameter(_request, _params[i]);
            if (_p == null || _p.getName().toString().length() == 0){ continue; }
            
            String _str = "";
            if(_do_enc) {
                _str = this.do_encrypt(_p.getValue().toString().trim());
            }
            else {
                _str = this.do_decrypt(_p.getValue().toString().trim());
            }
            
            if(this._is_ovrr_req_body){
                if (!headers.contains(this._Header)) { headers.add(this._Header); }
                _request = this.helpers.buildHttpMessage(headers, _str.getBytes());
                return _request;
            }
            
            if(this._is_ovrr_res_body){
                if (!headers.contains(this._Header)) { headers.add(this._Header); }
                _request = this.helpers.buildHttpMessage(headers, _str.getBytes());
                return _request;
            }

            
            IParameter _newP = this.helpers.buildParameter(_params[i], _str, _p.getType());
            _request = this.helpers.removeParameter(_request, _p);
            _request = this.helpers.addParameter(_request, _newP);
            if (!headers.contains(this._Header)) { headers.add(this._Header); }
            IRequestInfo reqInfo2 = helpers.analyzeRequest(_request);
            String tmpreq = new String(_request);
            String messageBody = new String(tmpreq.substring(reqInfo2.getBodyOffset())).trim();
            _request = this.helpers.buildHttpMessage(headers, messageBody.getBytes());
        }
        return _request;
    }
    
    public byte[] update_req_params_json(byte[] _request, List<String> headers, String[] _params, Boolean _do_enc){
        for(int i=0; i< _params.length; i++){
            IParameter _p = this.helpers.getRequestParameter(_request, _params[i]);
            if (_p == null || _p.getName().toString().length() == 0){ continue; }
            
            String _str = "";
            if(_do_enc) {
                _str = this.do_encrypt(_p.getValue().toString().trim());
            }
            else {
                _str = this.do_decrypt(_p.getValue().toString().trim());
            }
            
            
            if(this._is_ovrr_req_body){
                if (!headers.contains(this._Header)) { headers.add(this._Header); }
                _request = this.helpers.buildHttpMessage(headers, _str.getBytes());
                return _request;
            }
            
            if(this._is_ovrr_res_body){
                if (!headers.contains(this._Header)) { headers.add(this._Header); }
                _request = this.helpers.buildHttpMessage(headers, _str.getBytes());
                return _request;
            }
            
            
            IRequestInfo reqInfo = helpers.analyzeRequest(_request);
            String tmpreq = new String(_request);
            String messageBody = new String(tmpreq.substring(reqInfo.getBodyOffset())).trim();

            int _fi = messageBody.indexOf(_params[i]);
            if(_fi < 0) { continue; }

            _fi = _fi + _params[i].length() + 3;
            int _si = messageBody.indexOf("\"", _fi);
            print_output("update_req_params_json", _str);
            print_output("update_req_params_json", messageBody.substring(0, _fi));
            print_output("update_req_params_json", messageBody.substring(_si, messageBody.length()));
            if (!headers.contains(this._Header)) { headers.add(this._Header); }
            messageBody = messageBody.substring(0, _fi) + _str + messageBody.substring(_si, messageBody.length());
            _request = this.helpers.buildHttpMessage(headers, messageBody.getBytes());
            
        }
        return _request;
    }
    
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (messageIsRequest) {
            IHttpRequestResponse messageInfo = message.getMessageInfo();
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            String URL = new String(reqInfo.getUrl().toString());
            List headers = reqInfo.getHeaders();
            
            if(this._host.contains(get_host(URL))) {
                
                if(this._is_req_body) {
                    // decrypting request body
                    String tmpreq = new String(messageInfo.getRequest());
                    String messageBody = new String(tmpreq.substring(reqInfo.getBodyOffset())).trim();
                    String decValue = this.do_decrypt(messageBody);
                    headers.add(new String(this._Header));
                    byte[] updateMessage = helpers.buildHttpMessage(headers, decValue.getBytes());
                    messageInfo.setRequest(updateMessage);
                    print_output("PPM-req", "Final Decrypted Request\n" + new String(updateMessage));
                }
                else if(this._is_req_param){
                    
                    byte[] _request = messageInfo.getRequest();
                    
                    if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                        _request = update_req_params_json(_request, headers, this._req_param ,false);
                    }
                    else{
                        _request = update_req_params(_request, headers, this._req_param, false);                        
                    }
                    print_output("PPM-req", "Final Decrypted Request\n" + new String(_request));
                    messageInfo.setRequest(_request);
                    
                }
                else {
                    return;
                }
                
            }
        }
        else {
            if(this._ignore_response) { return; }
            // PPM Response
            
            IHttpRequestResponse messageInfo = message.getMessageInfo();
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
            String URL = new String(reqInfo.getUrl().toString());
            List headers = resInfo.getHeaders();
            
            if(this._host.contains(this.get_host(URL))){
                
                if(!headers.contains(this._Header)){ return; }
                
                if(this._is_res_body){
                    // Complete Response Body encryption
                    String tmpreq = new String(messageInfo.getResponse());
                    String messageBody = new String(tmpreq.substring(resInfo.getBodyOffset())).trim();
                    messageBody = do_encrypt(messageBody);
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody.getBytes());
                    messageInfo.setResponse(updateMessage);
                    print_output("PPM-res", "Final Encrypted Response\n" + new String(updateMessage));
                }
                else if(this._is_ovrr_res_body){
                    String tmpreq = new String(messageInfo.getResponse());
                    String messageBody = new String(tmpreq.substring(resInfo.getBodyOffset())).trim();
                    messageBody = do_encrypt(messageBody);
                    
                    if(this._is_ovrr_res_body_form){
                        messageBody = this._req_param[0] + "=" + messageBody;
                    }
                    else if(this._is_ovrr_res_body_json){
                        messageBody = "{\"" + this._req_param[0] + "\":\"" + messageBody + "\"}";
                    }
                    
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody.getBytes());
                    messageInfo.setResponse(updateMessage);
                    print_output("PPM-res", "Final Encrypted Response\n" + new String(updateMessage));
                }
                else if(this._is_res_param){
                    // implement left --------------------------
                    byte[] _response = messageInfo.getResponse();
                    
                    _response = this.update_req_params_json(_response, headers, this._res_param, true);
                    messageInfo.setResponse(_response);
                    print_output("PHTM-res", "Final Decrypted Response\n" + new String(_response));
                    
                }
                else{
                    return;
                }
            
            }
        }
    }

    
    
    
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (messageIsRequest) {
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            String URL = new String(reqInfo.getUrl().toString());
            List headers = reqInfo.getHeaders();
            
            if(!headers.contains(this._Header)){ return; }
            
            if(this._host.contains(get_host(URL))){
                if(this._is_req_body) {
                    String tmpreq = new String(messageInfo.getRequest());
                    String messageBody = new String(tmpreq.substring(reqInfo.getBodyOffset())).trim();
                    messageBody = this.do_encrypt(messageBody);
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody.getBytes());
                    messageInfo.setRequest(updateMessage);
                    print_output("PHTM-req", "Final Encrypted Request\n" + new String(updateMessage));
                }
                else if(this._is_ovrr_req_body){
                    String tmpreq = new String(messageInfo.getRequest());
                    String messageBody = new String(tmpreq.substring(reqInfo.getBodyOffset())).trim();
                    messageBody = this.do_encrypt(messageBody);
                    
                    if(this._is_ovrr_req_body_form){
                        messageBody = this._req_param[0] + "=" + messageBody;
                    }
                    else if(this._is_ovrr_req_body_json){
                        messageBody = "{\"" + this._req_param[0] + "\":\"" + messageBody + "\"}";
                    }
                    
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody.getBytes());
                    messageInfo.setRequest(updateMessage);
                    print_output("PHTM-req", "Final Encrypted Request\n" + new String(updateMessage));
                }
                else if(this._is_req_param){
                    
                    byte[] _request = messageInfo.getRequest();
                    
                    if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                        _request = update_req_params_json(_request, headers, this._req_param, true);
                    }
                    else{
                        _request = update_req_params(_request, headers, this._req_param, true);                        
                    }
                    print_output("PHTM-req", "Final Encrypted Request\n" + new String(_request));
                    messageInfo.setRequest(_request);
                }
                else {
                    return;
                }
            }
            
            
        }
        else {
            if(this._ignore_response) { return; }
            
            // PHTM Response
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
            String URL = new String(reqInfo.getUrl().toString());
            List headers = resInfo.getHeaders();
            
            
            if(this._host.contains(this.get_host(URL))){
                
                if(this._is_res_body){
                    // Complete Response Body decryption
                    String tmpreq = new String(messageInfo.getResponse());
                    String messageBody = new String(tmpreq.substring(resInfo.getBodyOffset())).trim();
                    messageBody = do_decrypt(messageBody);
                    headers.add(this._Header);
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody.getBytes());
                    messageInfo.setResponse(updateMessage);
                    print_output("PHTM-res", "Final Decrypted Response\n" + new String(updateMessage));
                }
                else if(this._is_res_param){
                    // implement left --------------------------
                    byte[] _response = messageInfo.getResponse();
                    
                    _response = this.update_req_params_json(_response, headers, this._res_param, false);
                    messageInfo.setResponse(_response);
                    print_output("PHTM-res", "Final Decrypted Response\n" + new String(_response));
                }
                else{
                    return;
                }
                
            }
            
            
        }
    }

    
    
    
    
    
    
    
    
    
}

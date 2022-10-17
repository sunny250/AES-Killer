package burp;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CryptoMenuFactory implements IContextMenuFactory {
    private BurpExtender _burpObj;

    public CryptoMenuFactory(BurpExtender parent) {
        this._burpObj = parent;
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menus = new ArrayList<>();
        //if (invocation.getToolFlag() == parent.callbacks.TOOL_INTRUDER) {
        JMenuItem Decrypt = new JMenuItem("reqDecrypt");
        try {
            Decrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];
                    byte[] request = req.getRequest();
//                    byte[] selectedBytes = getSelectedBytes(request, invocation.getSelectionBounds());
                    String selectedText = getSelectedText(request, invocation.getSelectionBounds());
                    this._burpObj.print_output("request", new String(request));
                    this._burpObj.print_output("selectedText", selectedText);

                    //返回包添加
//                    byte[] response = req.getResponse();
//                    selectedText = selectedText + getSelectedText(response, invocation.getSelectionBounds());
//                    this._burpObj.print_output("response", new String(response));
//                    this._burpObj.print_output("selectedText-------", selectedText);


                    if (selectedText != null && selectedText != "") {
                        String plainText = this._burpObj.do_decrypt(selectedText);
                        byte[] plainTextBytes = plainText.getBytes();
                        this._burpObj.print_output("plainText", plainText);

                        if (plainText != null && plainText != "") {
                            if (invocation.getToolFlag() == _burpObj.callbacks.TOOL_INTRUDER || invocation.getToolFlag() == _burpObj.callbacks.TOOL_REPEATER|| invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
                                    invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ) {
                                req.setRequest(Replace(request, invocation.getSelectionBounds(), plainTextBytes));


                                //返回包添加
//                            req.setResponse(Replace(response, invocation.getSelectionBounds(),plainTextBytes));
//                            ShowCopiableMessage(plainText, "This message plaintext is: ");

                            } else {
                                ShowCopiableMessage(plainText, "This message plaintext is: ");
                                req.setComment(plainText);
                            }
                        } else {
                            JOptionPane.showMessageDialog(Decrypt, "Not found!");
                        }
                    }
                    //返回包处理
//                    byte[] response = req.getResponse();
////                    byte[] reqSelectedBytes = getSelectedBytes(response, invocation.getSelectionBounds());
//                    String respSelectedText = getSelectedText(response, invocation.getSelectionBounds());
////                    selectedText = getSelectedText(response, invocation.getSelectionBounds());
//                    this._burpObj.print_output("response", new String(response));
//                    this._burpObj.print_output("respSelectedText-------", respSelectedText);
//
//                    if (respSelectedText != null && respSelectedText != "") {
//                        String respPlainText = this._burpObj.do_decrypt(respSelectedText);
//                        byte[] respPlainTextBytes = respPlainText.getBytes();
//                        this._burpObj.print_output("respPlainText", respPlainText);
//
//                        if (respPlainText != null && respPlainText != "") {
//                            if (invocation.getToolFlag() == _burpObj.callbacks.TOOL_PROXY ) {
////                                req.setResponse(Replace(response, invocation.getSelectionBounds(), respPlainTextBytes));
//
//
//                                //返回包添加
//                                req.setResponse(Replace(response, invocation.getSelectionBounds(), respPlainTextBytes));
//                                ShowCopiableMessage(respPlainText, "This message respPlainText is: ");
//
//
//                            } else {
//                                ShowCopiableMessage(respPlainText, "This message respPlainText is: ");
//                                req.setComment(respPlainText);
//                            }
//                        } else {
//                            JOptionPane.showMessageDialog(Decrypt, "Not found!");
//                        }
//                    }
                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Decrypt error", ex.getMessage());
        }
        menus.add(Decrypt);
        //}
//

        //if (invocation.getToolFlag() == parent.callbacks.TOOL_INTRUDER) {
        JMenuItem Encrypt = new JMenuItem("reqEncrypt");
        try {
            Encrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];

//                    请求包处理
                    byte[] request = req.getRequest();
//                    byte[] selectedBytes = getSelectedBytes(request, invocation.getSelectionBounds());
                    String selectedText = getSelectedText(request, invocation.getSelectionBounds());
                    this._burpObj.print_output("Encrypt request", new String(request));
                    this._burpObj.print_output("Encrypt selectedText", selectedText);



                    if (selectedText != null && selectedText != "") {
                        String cryptoText = this._burpObj.do_encrypt(selectedText);
                        byte[] cryptoTextBytes = cryptoText.getBytes();
                        this._burpObj.print_output("Encrypt cryptoText", cryptoText);

                        if (cryptoText != null && cryptoText != "") {
                            if (invocation.getToolFlag() == _burpObj.callbacks.TOOL_INTRUDER || invocation.getToolFlag() == _burpObj.callbacks.TOOL_REPEATER|| invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
                                    invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ) {
                                req.setRequest(Replace(request, invocation.getSelectionBounds(), cryptoTextBytes));


//                                //返回包添加
//                            req.setResponse(Replace(response, invocation.getSelectionBounds(),cryptoTextBytes));
//                            ShowCopiableMessage(cryptoText, "This message cryptoText is: ");


                            } else {
                                ShowCopiableMessage(cryptoText, "This message cryptoText is: ");
                                req.setComment(cryptoText);
                            }
                        } else {
                            JOptionPane.showMessageDialog(Encrypt, "Not found!");
                        }
                    }

                    // 返回包处理
                    //返回包添加

//                    byte[] response = req.getResponse();
//                    this._burpObj.print_output("Encrypt response", new String(response));
////                    byte[] respSelectedBytes = getSelectedBytes(response, invocation.getSelectionBounds());
//                    String respSelectedText = getSelectedText(response, invocation.getSelectionBounds());
////                    selectedText = selectedText + getSelectedText(response, invocation.getSelectionBounds());
//                    this._burpObj.print_output("Encrypt respSelectedText--------", respSelectedText);
//                    if (respSelectedText != null && respSelectedText != "") {
//                        String respCryptoText = this._burpObj.do_encrypt(respSelectedText);
//                        byte[] respCryptoTextBytes = respCryptoText.getBytes();
//                        this._burpObj.print_output("Encrypt respCryptoText", respCryptoText);
//
//                        if (respCryptoText != null && respCryptoText != "") {
//                            if (invocation.getToolFlag() == _burpObj.callbacks.TOOL_PROXY ) {
//
//                                //返回包添加
//                                req.setResponse(Replace(response, invocation.getSelectionBounds(),respCryptoTextBytes));
//                                ShowCopiableMessage(respCryptoText, "This message respCryptoText is: ");
//
//
//                            } else {
//                                ShowCopiableMessage(respCryptoText, "This message respCryptoText is: ");
//                                req.setComment(respCryptoText);
//                            }
//                        } else {
//                            JOptionPane.showMessageDialog(Encrypt, "Not found!");
//                        }
//                    }
                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Encrypt error", ex.getMessage());
        }
        menus.add(Encrypt);

        JMenuItem rspDecrypt = new JMenuItem("rspDecrypt");
        try {
            rspDecrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];
                    byte[] response = req.getResponse();
//                    byte[] selectedBytes = getSelectedBytes(request, invocation.getSelectionBounds());
                    String rspselectedText = getSelectedText(response, invocation.getSelectionBounds());
                    this._burpObj.print_output("response", new String(response));
                    this._burpObj.print_output("selectedText", rspselectedText);

                    if (rspselectedText != null && rspselectedText != "") {
                        String rspplainText = this._burpObj.do_decrypt(rspselectedText);
                        byte[] rspplainTextBytes = rspplainText.getBytes();
                        this._burpObj.print_output("rspplainText", rspplainText);

                        if (rspplainText != null && rspplainText != "") {
                            if (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
                                    invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ) {
                                req.setResponse(Replace(response, invocation.getSelectionBounds(), rspplainTextBytes));

                            } else {
                                ShowCopiableMessage(rspplainText, "This message rspplainText is: ");
                                req.setComment(rspplainText);
                            }
                        } else {
                            JOptionPane.showMessageDialog(rspDecrypt, "Not found!");
                        }
                    }

                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Decrypt error", ex.getMessage());
        }
        menus.add(rspDecrypt);
        //}
//

        //if (invocation.getToolFlag() == parent.callbacks.TOOL_INTRUDER) {
        JMenuItem rspEncrypt = new JMenuItem("rspEncrypt");
        try {
            rspEncrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];

                    // 返回包处理
                    //返回包添加

                    byte[] response = req.getResponse();
                    this._burpObj.print_output("Encrypt response", new String(response));
//                    byte[] respSelectedBytes = getSelectedBytes(response, invocation.getSelectionBounds());
                    String respSelectedText = getSelectedText(response, invocation.getSelectionBounds());
//                    selectedText = selectedText + getSelectedText(response, invocation.getSelectionBounds());
                    this._burpObj.print_output("Encrypt respSelectedText--------", respSelectedText);
                    if (respSelectedText != null && respSelectedText != "") {
                        String respCryptoText = this._burpObj.do_encrypt(respSelectedText);
                        byte[] respCryptoTextBytes = respCryptoText.getBytes();
                        this._burpObj.print_output("Encrypt respCryptoText", respCryptoText);

                        if (respCryptoText != null && respCryptoText != "") {
                            if (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
                                    invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ) {

                                //返回包添加
                                req.setResponse(Replace(response, invocation.getSelectionBounds(),respCryptoTextBytes));
//                                ShowCopiableMessage(respCryptoText, "This message respCryptoText is: ");


                            } else {
                                ShowCopiableMessage(respCryptoText, "This message respCryptoText is: ");
                                req.setComment(respCryptoText);
                            }
                        } else {
                            JOptionPane.showMessageDialog(Encrypt, "Not found!");
                        }
                    }
                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Encrypt error", ex.getMessage());
        }
        menus.add(rspEncrypt);


        return menus;
    }


    public void ShowCopiableMessage(String message, String title) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea ta = new JTextArea(20, 60);
                ta.setText(message);
                ta.setWrapStyleWord(true);
                ta.setLineWrap(true);
                ta.setCaretPosition(0);
                ta.setEditable(false);
                JOptionPane.showMessageDialog(null, new JScrollPane(ta), title, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

//    private String searchKey(String key) {
//        String value = _burpObj.dict.Search(key);
//        if (value == null) {
//            value = _burpObj.dict.Search(_burpObj.helpers.urlDecode(key));
//        }
//        return value;
//    }

    private String getSelectedText(byte[] request, int[] selectedIndexRange) {
        String strArray[] = Arrays.stream(selectedIndexRange)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        this._burpObj.print_output("getSelectedText paras", new String(request)+"   "+Arrays.toString(strArray));
        try {
            return new String(getSelectedBytes(request, selectedIndexRange));
        } catch (Exception ex) {
            this._burpObj.print_error("getSelectedText", ex.getMessage());
            return null;
        }
    }

    private byte[] getSelectedBytes(byte[] request, int[] selectedIndexRange) {
        String strArray[] = Arrays.stream(selectedIndexRange)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        this._burpObj.print_output("getSelectedBytes paras", new String(request)+"   "+ Arrays.toString(strArray));
        try {

            byte[] selectedText = new byte[selectedIndexRange[1] - selectedIndexRange[0]];
            System.arraycopy(request, selectedIndexRange[0], selectedText, 0, selectedText.length);

            return selectedText;
        } catch (Exception ex) {
            this._burpObj.print_error("getSelectedBytes", ex.getMessage());
            return null;
        }
    }

    public static byte[] Replace(byte[] request, int[] selectedIndexRange, byte[] targetBytes) {
        byte[] result = new byte[request.length - (selectedIndexRange[1] - selectedIndexRange[0]) + targetBytes.length];
        System.arraycopy(request, 0, result, 0, selectedIndexRange[0]);
        System.arraycopy(targetBytes, 0, result, selectedIndexRange[0], targetBytes.length);
        System.arraycopy(request, selectedIndexRange[1], result, selectedIndexRange[0] + targetBytes.length, request.length - selectedIndexRange[1]);
        return result;
    }

}

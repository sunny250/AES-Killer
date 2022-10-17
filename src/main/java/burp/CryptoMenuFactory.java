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
        JMenuItem Decrypt = new JMenuItem("Decrypt");
        try {
            Decrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];
                    if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST  ||
                            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST )
                    {
                        byte[] request = req.getRequest();
                        String selectedText = getSelectedText(request, invocation.getSelectionBounds());

                        this._burpObj.print_output("reqselectedText", selectedText);
                        if (selectedText != null && selectedText != "") {
                            String plainText = this._burpObj.do_decrypt(selectedText);
                            byte[] plainTextBytes = plainText.getBytes();
                            this._burpObj.print_output("plainText", plainText);
                            if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST) {
                                req.setRequest(Replace(request, invocation.getSelectionBounds(), plainTextBytes));
                            }else {
                                ShowCopiableMessage(plainText, "This message plaintext is: ");
                                req.setComment(plainText);
                            }
                        }
                    }

                    if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ||
                            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_RESPONSE)
                    {
                        byte[] response = req.getResponse();
                        String   selectedText =getSelectedText(response, invocation.getSelectionBounds());
//                        this._burpObj.print_output("response", new String(response));
                        this._burpObj.print_output("rspselectedText", selectedText);
                        if (selectedText != null && selectedText != "") {
                            String plainText = this._burpObj.do_decrypt(selectedText);
                            byte[] plainTextBytes = plainText.getBytes();
                            this._burpObj.print_output("plainText", plainText);
                            if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE) {
                                req.setResponse(Replace(response, invocation.getSelectionBounds(), plainTextBytes));
                             }else {
                                ShowCopiableMessage(plainText, "This message plaintext is: ");
                                req.setComment(plainText);
                            }
                        }

                    }

                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Decrypt error", ex.getMessage());
        }
        menus.add(Decrypt);


        JMenuItem Encrypt = new JMenuItem("Encrypt");
        try {
            Encrypt.addActionListener(e -> {
                IHttpRequestResponse[] resps = invocation.getSelectedMessages();
                if (resps.length > 0) {
                    IHttpRequestResponse req = resps[0];
                    if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST  ||
                            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST )
                    {
                        byte[] request = req.getRequest();
                        String selectedText = getSelectedText(request, invocation.getSelectionBounds());
                        this._burpObj.print_output("reqselectedText", selectedText);
                        if (selectedText != null && selectedText != "") {
                            String plainText = this._burpObj.do_encrypt(selectedText);
                            byte[] plainTextBytes = plainText.getBytes();
                            this._burpObj.print_output("cryptoText", plainText);
                            if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST) {
                                req.setRequest(Replace(request, invocation.getSelectionBounds(), plainTextBytes));
                            }else {
                                ShowCopiableMessage(plainText, "This message cryptoText is: ");
                                req.setComment(plainText);
                            }
                        }
                    }

                    if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE ||
                            invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_RESPONSE)
                    {
                        byte[] response = req.getResponse();
                        String   selectedText =getSelectedText(response, invocation.getSelectionBounds());
                        this._burpObj.print_output("rspselectedText", selectedText);
                        if (selectedText != null && selectedText != "") {
                            String plainText = this._burpObj.do_encrypt(selectedText);
                            byte[] plainTextBytes = plainText.getBytes();
                            this._burpObj.print_output("cryptoText", plainText);
                            if(invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE) {
                                req.setResponse(Replace(response, invocation.getSelectionBounds(), plainTextBytes));
                            }else {
                                ShowCopiableMessage(plainText, "This message cryptoText is: ");
                                req.setComment(plainText);
                            }
                        }

                    }

                }
            });
        }catch (Exception ex){
            this._burpObj.print_error("Encrypt error", ex.getMessage());
        }
        menus.add(Encrypt);

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

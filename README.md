# XES Killer (Burpsuite Plugin)
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=102)](https://github.com/ellerbrock/open-source-badge/)
[![GitHub version](https://d25lcipzij17d.cloudfront.net/badge.svg?id=gh&type=0.3&v=0.1.3&x2=0)](http://badge.fury.io/gh/boennemann%2Fbadges)
[![Open Source Love](https://badges.frapsoft.com/os/mit/mit.svg?v=102)](https://github.com/ellerbrock/open-source-badge/)

**Burpsuite Plugin to decrypt AES、DES、Blowfish Encrypted traffic on the fly**

### Requirements
- Burpsuite



### 说明
源码改自AES-KILLER，由于AES-KILLER只支持AES/CBC/PKCS5PADDING、AES/EBC/PKCS5PADDING，同时没有合适的手动加解密功能。
XES-KILLER插件支持三种算法（AES,DES,BLOWFISH），六大加密模式（CBC、CFB、CTR、CTS、ECB、OFB），3种填充方式（NOPADDING、IOS10126PADDING、PKCS5PADDING）。BASE64与HEX两种数据格式。

### 安装使用
1、使用burp加载插件即可。
2、选择加密模式、填入key，填写iv（ecb模式下可填可不填，不会影响到加解密结果）。
**Tips：des模式中，key、iv长度不够八字节将使用"\0"补齐8字节，超过将截取前8位。
aes模式中key、iv长度不够16字节将使用"\0"补齐16字节，超过将截取前16字节**
3、默认数据格式为base64，勾选HEX_format后可随意切换格式。
4、除了右键菜单中的手动加解密，在xes_killer中还可手动加解密，将数据输入到左边的编辑框中，点击加密或解密即可。
5、在任何可以选中文本的地方单击右键即可加密或解密，在可编辑的区域内将自动替换，不可编辑的区域内将出现弹窗显示加解密内容。


### 目前存在的问题
1、加密中文存在问题，burp传入的中文选中大小计数为1，但是插件处理时，1个中文的计数为2。
2、切换加密算法，key、iv、数据格式后，先在插件页面随意输入数据点击一下加密或者解密。
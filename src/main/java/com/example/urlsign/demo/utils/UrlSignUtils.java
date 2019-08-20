package com.example.urlsign.demo.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UrlSignUtils {

    private static Logger logger = LoggerFactory.getLogger(UrlSignUtils.class);

    public static String getUrlStr(Map<String, String> params, String payApiKey) {
        StringBuffer xmlStr = new StringBuffer();
        xmlStr.append(sortToString(params) + "&");
        xmlStr.append("code=" + uniSign(params, payApiKey));
        return xmlStr.toString();
    }

    /**
     * <h3>签名算法</h3>
     * <P>
     * 签名生成的通用步骤如下：
     * </p>
     * <p>
     * <p>
     * 第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（
     * 即key1=value1&key2=value2…）拼接成字符串stringA。 特别注意以下重要规则：
     * <ol>
     * <li>◆ 参数名ASCII码从小到大排序（字典序）；</li>
     * <li>◆ 如果参数的值为空不参与签名；</li>
     * <li>◆ 参数名区分大小写；</li>
     * <li>◆ 传送的sign参数不参与签名，将生成的签名与该sign值作校验。</li>
     * <li>◆ 接口可能增加字段，验证签名时必须支持增加的扩展字段</li>
     * </ol>
     * </p>
     * <p>
     * 第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5(或者其他摘要算法)运算，
     * 再将得到的字符串所有字符转换为大写，得到sign值signValue。
     * key 为双方系统约定的私钥字符串 不超过32位
     * </p>
     *
     * @return 参数签名：sign
     */
    public static String uniSign(Map<String, String> params, String payApiKey) {
        String sign = "";
        String strTmp = "";
        if (StringUtils.isNotBlank(payApiKey)) {
            strTmp = sortToString(params) + "&key=" + payApiKey;
        } else {
            strTmp = sortToString(params);
        }


        logger.info("#1.生成字符串：#2.连接商户key：" + strTmp);
        sign = DigestUtils.md5Hex(strTmp);
        sign = sign.toUpperCase();
        logger.info("#3.md5编码并转成大写：" + sign);

        return sign;
    }

    /**
     * 去除空值和sign参数，进行字典序排序，然后用&拼接
     *
     * @param params
     * @return
     */
    private static String sortToString(Map<String, String> params) {
        // 移除空值 和 sign参数
        removeBlankValue(params);
        // key排序
        Set<String> keys = params.keySet();
        String[] sortedKeys = toArray(keys);
        Arrays.sort(sortedKeys);
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < sortedKeys.length; i++) {
            String key = sortedKeys[i];
            String value = params.get(key);
            str.append(key + "=" + value + "&");
        }

        //replace返回this对象 所以不用替换返回变量
        str.replace(str.lastIndexOf("&"), str.length(), "");

        return str.toString();
    }

    private static String[] toArray(Set<String> keys) {
        String[] sortedKeys = new String[keys.size()];

        int i = 0;
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            sortedKeys[i] = key;
            i++;
        }
        return sortedKeys;
    }

    /**
     * 移除空值 和 sign参数
     *
     * @param params
     */
    private static void removeBlankValue(Map<String, String> params) {
        //
        if (params.containsKey("code")) {
            params.remove("code");
        }

        Set<String> keys = params.keySet();
        for (Iterator<String> keyIter = keys.iterator(); keyIter.hasNext(); ) {
            String key = keyIter.next();
            String value = params.get(key);
            if (StringUtils.isBlank(value)) {
//				params.remove(key);
                keyIter.remove();
            }
        }
    }

}

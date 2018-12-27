
package com.huawei.bridge.common.config;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.PBEParametersGenerator;

import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.util.LogUtils;





/**
 * 
 * <p>Title: 不可逆的密码加密 </p>
 * <p>Description:  </p>
 * <pre>  </pre>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Huawei Technologies Co.</p>
 * @since
 */
public abstract class EncryptUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(EncryptUtils.class);
    
  
    
    /**
     *  导出密钥的字节长度
     */
    private static int DK_LENGTH = 256;
    
    /**
     * 编码格式
     */
    private static final String UTF_8 = "utf-8";

    

    
  
    
    /**
     * 使用SHA1的PBKDF2进行加密
     * 在StandardEncryptor方法中用于密钥生成
     * @param plaintext 明文
     * @param salt      盐值
     * @return          密文
     */
    public static String encryptWithPBKDF2(String plaintext, String salt, int count)
    {
        // 1. 校验参数
        plaintext = (plaintext != null) ? plaintext : "";
        salt = (salt != null) ? salt : "";
        
        // 2. 加密
        String ciphertext = "";
       
        try 
        {
            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(plaintext.toCharArray()), 
                    salt.getBytes(UTF_8), count);
            KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(getContentLength());
            ciphertext = new String(Base64.encode(key.getKey()), UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.error("encryptWithPBKDF2 failed, {}", LogUtils.encodeForLog(e.getMessage()));
        }
        return ciphertext;
    }
    
    private static int getContentLength()
    {
        int contentLength = 0;
        try
        {
            //获取密钥的字节长度
            contentLength = Integer.valueOf(RootKeyManager
                    .getValueFromKeysMap("CRYPT_PKBDF2_ENCRYPT_LENGTH"));
        }
        catch (NumberFormatException e)
        {
            contentLength = DK_LENGTH;
            LOG.error("CRYPT_PKBDF2_ENCRYPT_LENGTH is invalid");
        }
        return contentLength;
    }
   
}

package io.github.forezp.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.github.forezp.common.constant.JWTConstants.SALT_KEY;

/**
 * Created by forezp on 2018/8/7.
 * 登录认证工具类
 */
public class JWTUtils {


    /**
     * 生产Token
     * @param id JWT ID：是JWT的唯一标识
     * @param subject JWT的主体，即它的所有人
     * @param ttlMillis 失效哦时间
     * @return 生产的token信息
     * @throws Exception
     */
    public static String createJWT(String id, String subject, long ttlMillis) throws Exception {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String,Object> claims = new HashMap<String,Object>();
//        claims.put("uid", "DSSFAWDWADAS...");
//        claims.put("user_name", "admin");
//        claims.put("nick_name","DASDA121");
        SecretKey key = generalKey();//生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        //下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder() //这里其实就是new一个JwtBuilder，设置jwt的body
                .setClaims(claims)          //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setId(id)                  //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setIssuedAt(now)           //iat: jwt的签发时间
                .setSubject(subject)        //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .signWith(signatureAlgorithm, key);//设置签名使用的签名算法和签名使用的秘钥
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);     //设置过期时间
        }
        return builder.compact();           //就开始压缩为xxxxxxxxxxxxxx.xxxxxxxxxxxxxxx.xxxxxxxxxxxxx这样的jwt
        //打印了一哈哈确实是下面的这个样子
        //eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiJEU1NGQVdEV0FEQVMuLi4iLCJzdWIiOiIiLCJ1c2VyX25hbWUiOiJhZG1pbiIsIm5pY2tfbmFtZSI6IkRBU0RBMTIxIiwiZXhwIjoxNTE3ODI4MDE4LCJpYXQiOjE1MTc4Mjc5NTgsImp0aSI6Imp3dCJ9.xjIvBbdPbEMBMurmwW6IzBkS3MPwicbqQa2Y5hjHSyo
    }

    public static Claims parseJWT(String jwt) throws Exception{
        SecretKey key = generalKey();  //签名秘钥，和生成的签名的秘钥一模一样
        Claims claims = Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey(key)         //设置签名的秘钥
                .parseClaimsJws(jwt).getBody();//设置需要解析的jwt
        return claims;
    }

    public static SecretKey generalKey(){
        String stringKey =SALT_KEY;//本地配置文件中加密的密文7786df7fc3a34e26a61c034d5ec8245d
        byte[] encodedKey = Base64.decodeBase64(stringKey);//本地的密码解码[B@152f6e2
        //System.out.println(encodedKey);//[B@152f6e2
       // System.out.println(Base64.encodeBase64URLSafeString(encodedKey));//7786df7fc3a34e26a61c034d5ec8245d
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");// 根据给定的字节数组使用AES加密算法构造一个密钥，使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。（后面的文章中马上回推出讲解Java加密和解密的一些算法）
        return key;
    }

    /**
     * main用于测试生产Token与Token解析
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // JWTUtils util = new JWTUtils();
        // 创建token
        String token = createJWT("jwt001", "{id:100,name:wangwenyuan}", 600000);
        System.out.println("生成token: "+token);

        // 根据jwt解析用户信息
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7aWQ6MTAwLG5hbWU6d2FuZ3dlbnl1YW59IiwiaXNzIjoidGVzdCIsImV4cCI6MTY1NTk5OTAzMCwiaWF0IjoxNjU1OTk4NDMwLCJqdGkiOiJqd3QwMDEifQ.8leXZtYBLOCtvM3ohDGtVkFtcWzird6bsOG_IR1AaEo";
        Claims c = parseJWT(jwt);//注意：如果jwt已经过期了，这里会抛出jwt过期异常。
        System.out.println(c.getId());//jwt001
        // jwt的签发时间
        System.out.println(c.getIssuedAt());
        System.out.println(c.getSubject());//{id:100,name:wangwenyuan}
        // jwt发行人,createJWT中没设置故为null
        System.out.println(c.getIssuer());
        System.out.println(c.get("uid", String.class));//DSSFAWDWADAS...
    }
}

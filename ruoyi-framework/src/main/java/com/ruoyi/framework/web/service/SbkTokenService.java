package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.SbkLoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Component
public class SbkTokenService
{
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public SbkLoginUser getSbkLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.SBK_LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                SbkLoginUser user = redisCache.getCacheObject(userKey);
                return user;
            }
            catch (Exception e)
            {
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setSbkLoginUser(SbkLoginUser sbkLoginUser)
    {
        if (StringUtils.isNotNull(sbkLoginUser) && StringUtils.isNotEmpty(sbkLoginUser.getToken()))
        {
            refreshToken(sbkLoginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delSbkLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param sbkLoginUser 用户信息
     * @return 令牌
     */
    public String createToken(SbkLoginUser sbkLoginUser)
    {
        String token = IdUtils.fastUUID();
        sbkLoginUser.setToken(token);
        setUserAgent(sbkLoginUser);
        refreshToken(sbkLoginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.SBK_LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param sbkLoginUser
     * @return 令牌
     */
    public void verifyToken(SbkLoginUser sbkLoginUser)
    {
        long expireTime = sbkLoginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
        {
            refreshToken(sbkLoginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param sbkLoginUser 登录信息
     */
    public void refreshToken(SbkLoginUser sbkLoginUser)
    {
        sbkLoginUser.setLoginTime(System.currentTimeMillis());
        sbkLoginUser.setExpireTime(sbkLoginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将SbkLoginUser缓存
        String userKey = getTokenKey(sbkLoginUser.getToken());
        if (expireTime > 0) {
            redisCache.setCacheObject(userKey, sbkLoginUser, expireTime, TimeUnit.MINUTES);
        } else {
            redisCache.setCacheObject(userKey, sbkLoginUser);
        }
    }

    /**
     * 设置用户代理信息
     *
     * @param sbkLoginUser 登录信息
     */
    public void setUserAgent(SbkLoginUser sbkLoginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        sbkLoginUser.setIpaddr(ip);
        sbkLoginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        sbkLoginUser.setBrowser(userAgent.getBrowser().getName());
        sbkLoginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    private String getTokenKey(String uuid)
    {
        return Constants.SBK_LOGIN_TOKEN_KEY + uuid;
    }
}

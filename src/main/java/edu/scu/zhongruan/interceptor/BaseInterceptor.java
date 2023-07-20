package edu.scu.zhongruan.interceptor;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Objects;

@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Resource
    RedisTemplate<String, String> template;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //检查token
        String token = request.getParameter("token");
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        if(Objects.isNull(token)){
            JSONObject object = new JSONObject();
            object.put("code", 500);
            object.put("msg", "缺少token");
            response.getWriter().write(object.toString());
            return false;
        }
        Boolean hasKey = template.hasKey(token);
        if(Objects.isNull(hasKey) || !hasKey){
            JSONObject object = new JSONObject();
            object.put("code", 500);
            object.put("msg", "无效token");
            response.getWriter().write(object.toString());
            return false;
        }
        //token延时
        ValueOperations<String, String> ops = template.opsForValue();
        String name = ops.get(token);
        if(Objects.isNull(name)){
            JSONObject object = new JSONObject();
            object.put("code", 500);
            object.put("msg", "用户信息异常");
            response.getWriter().write(object.toString());
            return false;
        }
        ops.set(token, name, Duration.ofMinutes(30));
        ops.set(name, token, Duration.ofMinutes(30));
        return true;
    }
}

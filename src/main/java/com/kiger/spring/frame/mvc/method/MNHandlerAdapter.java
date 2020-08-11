package com.kiger.spring.frame.mvc.method;

import com.kiger.spring.frame.annotation.MNRequestParam;
import com.kiger.spring.frame.mvc.hanlder.MNHandlerMapping;
import com.kiger.spring.frame.mvc.servlet.HandlerAdapter;
import com.kiger.spring.frame.mvc.servlet.HandlerMapping;
import com.kiger.spring.frame.mvc.view.MNModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMapping);
    }

    @Override
    public MNModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MNHandlerMapping handlerMapping = (MNHandlerMapping) handler;

        // 每个方法有一个参数列表，这里保存的是形参列表
        Map<String, Integer> paramMapping = new HashMap<>();

        // 获取方法中命名的参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof MNRequestParam) {
                    String paramName = ((MNRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }

        // 根据用户请求的参数信息，与 Method 中的参数信息进行动态匹配

        // 1.准备方法的形参列表
        // 只处理 Request 和 Response
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramMapping.put(type.getName(), i);
            }
        }

        // 2.得到自定义命名参数所在的位置
        Map<String, String[]> requestParameterMap = request.getParameterMap();

        // 3.构造实参列表
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : requestParameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramMapping.get(param.getKey());

            // 因为页面传过来的值都是String类型，而在方法中定义的类型多变
            // 需要进行类型转换
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        // 4.从 handler 中取出 Controller、Method，使用反射调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);

        if (result == null) { return null; }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MNModelAndView.class;
        if (isModelAndView) {
            return (MNModelAndView) result;
        } else {
            return null;
        }
    }

    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }
}

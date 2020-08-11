package com.kiger.spring.frame.mvc.servlet;

import com.kiger.spring.frame.annotation.MNController;
import com.kiger.spring.frame.annotation.MNRequestMapping;
import com.kiger.spring.frame.context.support.MNApplicationContext;
import com.kiger.spring.frame.mvc.method.MNHandlerAdapter;
import com.kiger.spring.frame.mvc.hanlder.MNHandlerMapping;
import com.kiger.spring.frame.mvc.view.MNModelAndView;
import com.kiger.spring.frame.mvc.view.MNView;
import com.kiger.spring.frame.mvc.view.MNViewResolver;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zk_kiger
 * @date 2020/8/1
 */

@Slf4j
public class MNDispatherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<MNHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<MNHandlerMapping, MNHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<MNViewResolver> viewResolvers = new ArrayList<>();

    private MNApplicationContext webApplicationContext;

    /**
     * 初始化IOC容器和SpringMVC九大组件
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化IOC容器
        if (this.webApplicationContext == null) {
            this.webApplicationContext = new MNApplicationContext(config.getInitParameter(LOCATION));
        }
        initStrategies(this.webApplicationContext);
    }

    /**
     * 初始化SpringMVC九大组件
     * @param context
     */
    protected void initStrategies(MNApplicationContext context) {
        // 文件上传解析，如果请求类型是multipart，将通过MultipartResolver进行文件上传解析
        initMultipartResolver(context);
        // 本地化解析
        initLocaleResolver(context);
        // 主题解析
        initThemeResolver(context);
        // 实现：HandlerMapping 用来保存 Controller 中配置的 RequestMapping 和 Method 的对应关系
        // 通过 HandlerMapping 将请求映射到处理器
        initHandlerMappings(context);
        // 实现：HandlerAdapters 用来动态匹配 Method 参数，包括类型转换、动态赋值
        initHandlerAdapters(context);
        // 执行过程中遇到异常，将交给 HandlerExceptionResolvers 解决
        initHandlerExceptionResolvers(context);
        // 直接将请求解析到视图名
        initRequestToViewNameTranslator(context);
        // 实现：通过 ViewResolver 实现动态模板的解析
        initViewResolvers(context);
        // Flash 映射管理器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(MNApplicationContext context) {}
    private void initLocaleResolver(MNApplicationContext context) {}
    private void initThemeResolver(MNApplicationContext context) {}
    private void initHandlerExceptionResolvers(MNApplicationContext context) {}
    private void initRequestToViewNameTranslator(MNApplicationContext context) {}
    private void initFlashMapManager(MNApplicationContext context) {}

    /**
     * 将 Controller 中配置的 RequestMapping 和 Method 进行一一对应
     *  Map<url, Method>
     * @param context
     */
    private void initHandlerMappings(MNApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(MNController.class)) {
                    continue;
                }

                String baseUrl = "";
                if (clazz.isAnnotationPresent(MNRequestMapping.class)) {
                    MNRequestMapping requestMapping = clazz.getAnnotation(MNRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                // 扫描所有 public 方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(MNRequestMapping.class)) {
                        continue;
                    }

                    MNRequestMapping requestMapping = method.getAnnotation(MNRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*"))
                            .replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new MNHandlerMapping(controller, method, pattern));
                    log.info("Mapping " + regex + " , " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 针对不同的方法由于参数类型顺序不同，针对每一个方法分配一个适配器
     * 用于处理反射调用时，传入的形参数组，根据方法中对应参数 index，
     * 逐个从数组中取值
     * @param context
     */
    private void initHandlerAdapters(MNApplicationContext context) {
        for (MNHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MNHandlerAdapter());
        }
    }

    /**
     * 解决页面名字和模板文件关联问题
     * 我们只配置了一个视图解析器 MNViewResolver，用于处理返回 HTML view
     * SpringMVC 中会加入不同功能的 ViewResolver
     * @param context
     */
    private void initViewResolvers(MNApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String webappRoot = this.getClass().getClassLoader().getResource("/").getPath().replaceFirst("/", "").replaceAll("WEB-INF/classes/", "");
        String templateRootPath = (webappRoot + templateRoot).replaceAll("/+", "/");

        this.viewResolvers.add(new MNViewResolver(templateRootPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispath(req, resp);
        } catch (Exception e) {
            resp.getWriter().write(500);
            e.printStackTrace();
        }
    }

    private void doDispath(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 根据用户请求 url，获取一个 handler
        MNHandlerMapping handler = getHandler(req);
        if (handler == null) {
            // 未找到对应的 handler，向页面输入 404
            processDispathResult(req, resp, new MNModelAndView("404"));
            return;
        }

        HandlerAdapter ha = getHandlerAdapter(handler);

        // HandlerAdapter 将请求参数与方法参数进行适配，调用方法，得到返回值
        MNModelAndView mv = ha.handle(req, resp, handler);

        // 将结果输出到页面
        processDispathResult(req, resp, mv);
    }

    private HandlerAdapter getHandlerAdapter(MNHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) { return null; }
        MNHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private void processDispathResult(HttpServletRequest req, HttpServletResponse resp, MNModelAndView mv) throws Exception {
        if (mv == null) { return; }
        if (this.viewResolvers.isEmpty()) { return; }

        if (this.viewResolvers != null) {
            for (MNViewResolver viewResolver : this.viewResolvers) {
                MNView view = viewResolver.resolveViewName(mv.getViewName(), null);
                if (view != null) {
                    view.render(mv.getModel(), req, resp);
                    return;
                }
            }
        }

    }

    private MNHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "".replaceAll("/+", "/"));

        for (MNHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }

        return null;
    }

}

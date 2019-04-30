package com.thejonsey.cyber.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class MyErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public MyErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public ModelAndView handleError(HttpServletRequest request, WebRequest webRequest, ModelMap map) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        Map<String, Object> Error = this.errorAttributes.getErrorAttributes(webRequest, true);
         Error.forEach(map::addAttribute);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ModelAndView("error/404");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new ModelAndView("error/500");
            }
        }
        return new ModelAndView("error");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }
}
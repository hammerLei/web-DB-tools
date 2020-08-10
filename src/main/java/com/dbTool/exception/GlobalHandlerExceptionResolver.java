package com.dbTool.exception;

import com.dbTool.constant.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
* 类说明 控制器异常捕获类
*/
@ControllerAdvice
public class GlobalHandlerExceptionResolver implements HandlerExceptionResolver {

	private static final Log LOG = LogFactory.getLog(GlobalHandlerExceptionResolver.class);
	
    public GlobalHandlerExceptionResolver(){
        LOG.info("Init Global Error Ok!");
    }
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (ex == null)
			return null;
		
		Throwable cause = ex.getCause();
		if(cause!= null && cause.getClass() != null && 
				cause.getClass().getSimpleName().equals("ClientAbortException")){
			LOG.error("ClientAbortException Caught");
			return null;
		}
		
    LOG.error("[GlobalHandlerExceptionResolver::ResolveException] Exception:", ex);
    Map<String, Object> status = new HashMap<String, Object>();
    Map<String, Object> result = new HashMap<String, Object>();
    MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

    status.put(Constant.RESULT_CODE, Constant.SERRVER_ERROR);
    status.put(Constant.RESULT_MSG, Constant.SERRVER_ERROR);
    result.put(Constant.RESULT_DATA,ex.toString());
    result.put(Constant.RESULT_STATUS,status);

    return new ModelAndView(jsonView,result);
	}

}

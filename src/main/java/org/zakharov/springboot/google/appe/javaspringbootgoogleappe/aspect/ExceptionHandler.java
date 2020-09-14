package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.utils.ErrorsGetter;

import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@Aspect
public class ExceptionHandler {

    protected static Logger logger;

    public ExceptionHandler(){
        logger =
                Logger.getLogger(ExceptionHandler.class.getName());
    }

    @Around("execution(* org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.*.*(..))")
    public Object daoExceptionLog(ProceedingJoinPoint pjp) throws Throwable {
        Object output = null;
        try {
            output = pjp.proceed();
        } catch (Exception ex) {
            // TODO
            if (ErrorsGetter.get(ex).contains("ConstraintViolationException")){
                throw new Exception("ConstraintViolationException");
            }
            throw ex;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return output;
    }

    @Around("execution(* org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.*.*(..))")
    public Object serviceExceptionLog(ProceedingJoinPoint pjp) throws Throwable {
        Object output = null;
        try {
            output = pjp.proceed();
        } catch (Exception ex) {
            /*if (ex.getMessage().equals("ConstraintViolationException")){
                output =
                    ResponseModel.builder()
                        .status(ResponseModel.FAIL_STATUS)
                        .message("This name is already taken")
                        .build();
            } else {*/
            System.err.println("Google DataStore Error");
            ex.printStackTrace();
            output =
                    ResponseModel.builder()
                            .status(ResponseModel.FAIL_STATUS)
                            .message("Unknown storage error")
                            .build();
            //}
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return output;
    }
}
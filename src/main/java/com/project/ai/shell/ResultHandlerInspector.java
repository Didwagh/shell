package com.project.ai.shell;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ResultHandlerInspector implements ApplicationRunner {

    private final ApplicationContext ctx;

    public ResultHandlerInspector(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<String, org.springframework.shell.ResultHandler> beans =
                ctx.getBeansOfType(org.springframework.shell.ResultHandler.class);
        System.out.println("=== ResultHandler beans (name -> class) ===");
        beans.forEach((name, bean) -> System.out.println(name + " -> " + bean.getClass().getName()));
        System.out.println("===========================================");
    }
}

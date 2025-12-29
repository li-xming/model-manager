package com.example.datamodel.core;

import com.alibaba.fastjson2.JSON;
import com.example.datamodel.entity.Function;
import com.example.datamodel.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * 函数执行引擎
 *
 * 设计约定：
 * - 函数代码使用 JavaScript（兼容 Nashorn），并定义一个全局函数：
 *   function <functionName>(input) { ... return result; }
 *   例如：
 *   function calcDiscount(input) { var amount = input.amount || 0; return { discount: amount * 0.1 }; }
 *
 * - 函数执行时：
 *   1. 将请求体作为 input（Map）传入脚本引擎；
 *   2. 调用上面的函数名，并返回执行结果（可以是基本类型、Map、JSON字符串等）。
 *
 * 说明：
 * - 基于 Java 8 自带的 Nashorn 引擎实现，不引入额外依赖；
 * - 仅用于演示和轻量级计算，不适合作为高负载的通用脚本执行环境。
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class FunctionExecutor {

    private final ScriptEngine engine;

    public FunctionExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("nashorn");
        if (this.engine == null) {
            log.warn("未找到Nashorn脚本引擎，函数执行将不可用");
        }
    }

    /**
     * 执行函数
     *
     * @param function 函数定义
     * @param input    输入参数（通常是请求体）
     * @return 函数执行结果
     */
    public Object execute(Function function, Map<String, Object> input) {
        if (engine == null) {
            throw new BusinessException("运行环境不支持Nashorn脚本引擎，无法执行函数");
        }
        if (function == null) {
            throw new BusinessException("函数定义不能为空");
        }
        if (function.getCode() == null || function.getCode().trim().isEmpty()) {
            throw new BusinessException("函数代码为空：" + function.getName());
        }

        String funcName = function.getName();
        try {
            // 1. 先在引擎中评估函数代码
            engine.eval(function.getCode());

            // 2. 将输入参数作为 JSON 字符串注入，并在脚本中解析为对象
            //    也可以直接将 Map 传入 Invocable 调用
            Invocable invocable = (Invocable) engine;

            // 3. 调用 JS 函数：functionName(input)
            Object result = invocable.invokeFunction(funcName, input);

            // 4. 尝试将结果转换为更友好的结构（例如 JSON 字符串 -> Map）
            if (result instanceof String) {
                String str = (String) result;
                try {
                    return JSON.parse(str);
                } catch (Exception e) {
                    // 普通字符串，直接返回
                    return str;
                }
            }

            return result;
        } catch (ScriptException e) {
            log.error("执行函数脚本异常，name={}, code={}", function.getName(), function.getCode(), e);
            throw new BusinessException("执行函数脚本异常：" + e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error("在脚本中未找到函数：{}，请确认函数名与 Function.name 保持一致", funcName, e);
            throw new BusinessException("在脚本中未找到函数：" + funcName);
        } catch (Exception e) {
            log.error("执行函数发生未知异常，name={}", function.getName(), e);
            throw new BusinessException("执行函数发生未知异常：" + e.getMessage());
        }
    }
}



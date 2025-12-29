package com.example.datamodel.controller.v1;

import com.alibaba.fastjson2.JSON;
import com.example.datamodel.entity.ActionType;
import com.example.datamodel.core.FunctionExecutor;
import com.example.datamodel.entity.Function;
import com.example.datamodel.entity.Interface;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.service.ActionTypeService;
import com.example.datamodel.service.FunctionService;
import com.example.datamodel.service.InterfaceService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 外部接口统一入口控制器
 *
 * 说明：
 * - 外部系统通过 HTTP 方法 + URL 路径 调用本控制器
 * - 本控制器根据 method + path 在接口表中查找对应的 Interface 定义
 * - 校验请求体是否满足 Interface.requiredProperties 中定义的必需字段
 * - 后续可以在此处根据 Interface.actionTypeId 调用对应的操作类型实现
 *
 * 当前版本只做「路由 + 校验 + 回显」，便于调试和后续扩展。
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "外部接口网关", description = "统一的外部接口调用入口")
@RestController
@RequestMapping("/external")
public class ExternalInterfaceController {

    @Autowired
    private InterfaceService interfaceService;

    @Autowired
    private ActionTypeService actionTypeService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private FunctionExecutor functionExecutor;

    /**
     * 统一处理所有外部请求
     *
     * 实际匹配逻辑：
     * - method: 来自 HttpServletRequest.getMethod()
     * - path:   来自 HttpServletRequest.getRequestURI()
     *   因此在配置 Interface.path 时，请使用与实际请求完全一致的路径，例如 "/external/toll/charge"
     */
    @Operation(summary = "外部接口统一入口")
    @RequestMapping("/**")
    public ResponseVO<?> handle(HttpServletRequest request,
                                @RequestBody(required = false) Map<String, Object> body) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        log.info("收到外部请求，method={}, path={}, body={}", method, path, body != null ? JSON.toJSONString(body) : "null");

        Interface iface = interfaceService.getByMethodAndPath(method, path);
        if (iface == null) {
            log.warn("未找到匹配的接口定义，method={}, path={}", method, path);
            return ResponseVO.error(404, "未找到匹配的接口定义");
        }

        // 校验必需字段：Interface.requiredProperties 目前是一个 JSON 对象，key 视为必需字段名
        validateRequiredProperties(iface, body);

        // 根据 iface.getActionTypeId() 调用具体的操作类型 / 函数引擎（当前版本仅做骨架与回显）
        Object actionResult = null;
        if (iface.getActionTypeId() != null) {
            ActionType actionType = actionTypeService.getById(iface.getActionTypeId());
            if (actionType == null) {
                throw new BusinessException("接口绑定的操作类型不存在：" + iface.getActionTypeId());
            }
            actionResult = executeAction(actionType, body);
        }

        // 回显接口元数据、请求体及动作执行结果，便于调试和后续扩展
        Map<String, Object> result = new HashMap<>();
        result.put("interfaceId", iface.getId());
        result.put("interfaceName", iface.getName());
        result.put("interfaceDisplayName", iface.getDisplayName());
        result.put("method", iface.getMethod());
        result.put("path", iface.getPath());
        result.put("actionTypeId", iface.getActionTypeId());
        result.put("actionResult", actionResult);
        result.put("requestBody", body);

        return ResponseVO.success(result);
    }

    /**
     * 校验请求体是否包含接口定义的必需字段
     */
    private void validateRequiredProperties(Interface iface, Map<String, Object> body) {
        if (body == null) {
            body = new HashMap<>();
        }

        if (iface.getRequiredProperties() == null || iface.getRequiredProperties().trim().isEmpty()) {
            return;
        }

        Map<String, Object> requiredProps;
        try {
            requiredProps = JSON.parseObject(iface.getRequiredProperties());
        } catch (Exception e) {
            log.warn("解析接口必需属性JSON失败，ifaceId={}, requiredProperties={}", iface.getId(), iface.getRequiredProperties(), e);
            return;
        }

        if (requiredProps == null || requiredProps.isEmpty()) {
            return;
        }

        for (String key : requiredProps.keySet()) {
            Object value = body.get(key);
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                throw new BusinessException("请求体缺少必需字段：" + key);
            }
        }
    }

    /**
     * 执行动作类型的骨架方法
     *
     * 当前版本：
     * - 不真正执行业务逻辑，只是回显动作及请求体信息
     * - 预留扩展点：后续可在此处根据 actionType.getHandlerFunction() 调用函数引擎
     */
    private Object executeAction(ActionType actionType, Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        result.put("actionTypeId", actionType.getId());
        result.put("actionTypeName", actionType.getName());
        result.put("actionTypeDisplayName", actionType.getDisplayName());
        result.put("targetObjectTypeId", actionType.getTargetObjectTypeId());
        result.put("handlerFunction", actionType.getHandlerFunction());
        result.put("requestBody", body);

        // 如果配置了 handlerFunction，则尝试执行对应的函数
        if (actionType.getHandlerFunction() != null && !actionType.getHandlerFunction().trim().isEmpty()) {
            Function function = functionService.getByName(actionType.getHandlerFunction());
            if (function == null) {
                throw new BusinessException("未找到名称为 " + actionType.getHandlerFunction() + " 的函数定义");
            }
            Object fnResult = functionExecutor.execute(function, body);
            result.put("functionId", function.getId());
            result.put("functionName", function.getName());
            result.put("functionDisplayName", function.getDisplayName());
            result.put("functionReturnType", function.getReturnType());
            result.put("functionResult", fnResult);
        }

        return result;
    }
}



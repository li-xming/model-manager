package com.example.datamodel.core.datasource;

import com.example.datamodel.exception.BusinessException;
import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据源连接器注册表
 * 自动注册所有实现了DataSourceConnector接口的Bean
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class DataSourceConnectorRegistry {

    private final Map<String, DataSourceConnector> connectors = new ConcurrentHashMap<>();

    /**
     * 注册连接器（Spring自动注入所有实现类）
     *
     * @param connectorList 连接器列表
     */
    @Autowired(required = false)
    public void registerConnectors(List<DataSourceConnector> connectorList) {
        if (connectorList == null || connectorList.isEmpty()) {
            log.warn("未找到任何数据源连接器实现");
            return;
        }

        for (DataSourceConnector connector : connectorList) {
            String typeCode = connector.getTypeCode();
            if (connectors.containsKey(typeCode)) {
                log.warn("数据源类型 {} 的连接器已存在，将覆盖：{}", typeCode, connector.getClass().getName());
            }
            connectors.put(typeCode, connector);
            log.info("注册数据源连接器：{} -> {}", typeCode, connector.getDisplayName());
        }

        log.info("数据源连接器注册完成，共{}种类型：{}",
            connectors.size(),
            connectors.keySet().stream().collect(Collectors.joining(", ")));
    }

    /**
     * 获取连接器
     *
     * @param typeCode 类型代码
     * @return 连接器
     */
    public DataSourceConnector getConnector(String typeCode) {
        DataSourceConnector connector = connectors.get(typeCode);
        if (connector == null) {
            throw new BusinessException("不支持的数据源类型：" + typeCode +
                "。支持的类型：" + connectors.keySet());
        }
        return connector;
    }

    /**
     * 获取所有支持的数据源类型信息
     *
     * @return 类型信息列表
     */
    public List<DataSourceTypeInfo> getSupportedTypes() {
        return connectors.values().stream()
            .map(connector -> DataSourceTypeInfo.builder()
                .code(connector.getTypeCode())
                .displayName(connector.getDisplayName())
                .defaultPort(connector.getDefaultPort())
                .requiresDatabase(connector.requiresDatabase())
                .requiresSchema(connector.requiresSchema())
                .description(connector.getDescription())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * 检查类型是否支持
     *
     * @param typeCode 类型代码
     * @return 是否支持
     */
    public boolean isSupported(String typeCode) {
        return connectors.containsKey(typeCode);
    }
}

/**
 * 数据源类型信息
 */
@Data
@Builder
class DataSourceTypeInfo {
    private String code;
    private String displayName;
    private Integer defaultPort;
    private Boolean requiresDatabase;
    private Boolean requiresSchema;
    private String description;
}



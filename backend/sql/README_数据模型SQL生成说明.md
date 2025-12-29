# 省中心联网收费业务数据模型SQL脚本说明

## 文件说明

根据 `省中心联网收费业务实体关系图.puml` 文件生成的数据模型SQL脚本包含以下文件：

1. **省中心联网收费业务数据模型-完整版.sql**
   - 包含业务域创建
   - 包含17个对象类型的创建
   - 包含部分属性（User, Vehicle, Medium）的定义
   - 包含19个链接类型的完整定义

2. **省中心联网收费业务数据模型-属性补充.sql**
   - 包含所有17个实体的完整属性定义
   - 需要在主SQL脚本执行后运行

## 执行顺序

1. **第一步：执行主SQL脚本**
   ```sql
   -- 执行前，请修改脚本中的 v_datasource_id 为实际的数据源ID
   \i 省中心联网收费业务数据模型-完整版.sql
   ```

2. **第二步：执行属性补充SQL**
   ```sql
   \i 省中心联网收费业务数据模型-属性补充.sql
   ```

## 数据结构概览

### 业务域
- **编码**: TOLL_COLLECTION
- **名称**: 省中心联网收费业务

### 对象类型（17个）

1. **User** - 车主（12个属性）
2. **Vehicle** - 车辆（30个属性）
3. **Medium** - 通行介质（8个属性）
4. **TollRoad** - 收费公路（15个属性）
5. **SectionOwner** - 路段业主（13个属性）
6. **Section** - 收费路段（24个属性）
7. **TollStation** - 收费站（29个属性）
8. **TollPlaza** - 收费广场（17个属性）
9. **TollGantry** - 收费门架（75+个属性）
10. **TollInterval** - 收费单元（18个属性）
11. **TollLane** - 收费车道（17个属性）
12. **Transaction** - 交易流水（19个属性）
13. **Path** - 车辆通行路径（9个属性）
14. **PathDetail** - 车辆通行路径明细（12个属性）
15. **RestorePath** - 车辆通行拟合路径（9个属性）
16. **RestorePathDetail** - 车辆通行拟合路径明细（12个属性）
17. **SplitDetail** - 拆分明细（6个属性）

### 链接类型（19个）

1. User 1--n Vehicle (拥有)
2. Vehicle 1--1n Medium (持有)
3. Vehicle 1--n Transaction (关联)
4. TollRoad 1--n Section (包含)
5. SectionOwner 1--n Section (管理)
6. Section 1--n TollStation (包含)
7. Section 1--n TollInterval (包含)
8. TollStation 1--n TollPlaza (包含)
9. TollPlaza 1--n TollLane (包含)
10. TollGantry 1--n TollInterval (所在收费单元是)
11. TollGantry 1--n TollInterval (代收)
12. TollLane 1--n Transaction (生成)
13. TollGantry 1--n Transaction (生成)
14. Transaction n--1 Path (汇聚为)
15. Path 1--n PathDetail (持有)
16. Path 1--1 RestorePath (拟合为)
17. RestorePath 1--n RestorePathDetail (持有)
18. RestorePath 1--n SplitDetail (拆分为)
19. TollInterval 1--1 SplitDetail (关联)

## 注意事项

1. **数据源ID**: 执行前必须修改 `省中心联网收费业务数据模型-完整版.sql` 中的 `v_datasource_id` 变量为实际的数据源ID

2. **执行环境**: 确保数据库连接正常，且具有创建表和插入数据的权限

3. **外键约束**: 由于使用了DO块和变量，脚本会按顺序创建依赖关系，无需担心外键约束问题

4. **重复执行**: 如果已经存在相同编码的业务域或对象类型，执行会失败，需要先清理或修改编码

## 重新生成SQL

如果需要重新生成SQL脚本（例如修改了PlantUML文件），可以运行：

```bash
cd backend
python sql/generate_complete_sql.py
```

这将会重新生成 `省中心联网收费业务数据模型-属性补充.sql` 文件。


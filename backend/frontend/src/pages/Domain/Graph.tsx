import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, App, Descriptions, Modal, Table } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { objectTypeApi, linkTypeApi, businessDomainApi, propertyApi } from '../../api';
import type { ObjectType, LinkType, BusinessDomain, Property } from '../../types/api';
import type { ColumnsType } from 'antd/es/table';

interface GraphNode {
  id: string;
  label: string;
  type: 'objectType';
  x: number;
  y: number;
}

interface GraphLink {
  source: string;
  target: string;
  label?: string;
}

const DomainGraph = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [domain, setDomain] = useState<BusinessDomain | null>(null);
  const [graphData, setGraphData] = useState<{ nodes: GraphNode[]; links: GraphLink[] }>({
    nodes: [],
    links: [],
  });
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
  const [isPanning, setIsPanning] = useState(false);
  const [lastPanPosition, setLastPanPosition] = useState<{ x: number; y: number } | null>(null);
  const [draggingNodeId, setDraggingNodeId] = useState<string | null>(null);
  const [lastDragPosition, setLastDragPosition] = useState<{ x: number; y: number } | null>(null);
  const [selectedObjectType, setSelectedObjectType] = useState<ObjectType | null>(null);
  const [selectedProperties, setSelectedProperties] = useState<Property[]>([]);
  const [detailModalVisible, setDetailModalVisible] = useState(false);

  useEffect(() => {
    if (id) {
      loadDomainAndGraph();
    }
  }, [id]);

  const loadDomainAndGraph = async () => {
    setLoading(true);
    try {
      // 加载业务域信息
      const domainRes = await businessDomainApi.getById(id!);
      if (domainRes.code === 200 && domainRes.data) {
        setDomain(domainRes.data);
      }

      // 加载该业务域下的对象类型
      const otRes = await objectTypeApi.list({ current: 1, size: 1000, domainId: id });
      if (otRes.code !== 200 || !otRes.data) {
        setGraphData({ nodes: [], links: [] });
        return;
      }
      const objectTypes: ObjectType[] = otRes.data.records || [];
      if (objectTypes.length === 0) {
        setGraphData({ nodes: [], links: [] });
        return;
      }

      const typeIdSet = new Set(objectTypes.map(o => o.id));

      // 为这些对象类型加载相关的链接类型，并限定在当前业务域
      const linkResults = await Promise.all(
        objectTypes.map(async (ot) => {
          try {
            const res = await linkTypeApi.getByObjectTypeId(ot.id);
            if (res.code === 200 && res.data) {
              return (res.data as LinkType[]).filter(lt => !lt.domainId || lt.domainId === id);
            }
          } catch (e) {
            // 单个失败不影响整体
          }
          return [] as LinkType[];
        }),
      );

      const allLinkTypes: LinkType[] = ([] as LinkType[]).concat(...linkResults);

      const edgeKeySet = new Set<string>();
      const links: GraphLink[] = [];

      allLinkTypes.forEach((lt) => {
        const sid = lt.sourceObjectTypeId;
        const tid = lt.targetObjectTypeId;
        if (!typeIdSet.has(sid) || !typeIdSet.has(tid)) {
          return;
        }
        const key = [sid, tid, lt.id].sort().join('|');
        if (edgeKeySet.has(key)) {
          return;
        }
        edgeKeySet.add(key);
        links.push({
          source: sid,
          target: tid,
          label: lt.displayName || lt.name,
        });
      });

      // 初始采用环形布局，为每个实体计算起始坐标，后续可通过拖拽调整
      const count = Math.max(objectTypes.length, 1);
      const rectWidth = 140;
      const baseRadius = 180;
      const minRadius = (rectWidth * count * 1.6) / (2 * Math.PI);
      const radius = Math.max(baseRadius, minRadius);

      const nodes: GraphNode[] = objectTypes.map((ot, index) => {
        const angle = (2 * Math.PI * index) / count - Math.PI / 2;
        const x = radius * Math.cos(angle);
        const y = radius * Math.sin(angle);
        return {
          id: ot.id,
          label: ot.displayName || ot.name,
          type: 'objectType',
          x,
          y,
        };
      });

      setGraphData({ nodes, links });
      // 默认不选中具体对象类型
      setSelectedObjectType(null);
      setSelectedProperties([]);
      setDetailModalVisible(false);
    } catch (error: any) {
      message.error(error.friendlyMessage || error.message || '加载业务域对象视图失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate('/domains')}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <h2 style={{ margin: 0, display: 'inline' }}>
            业务域对象视图{domain ? ` - ${domain.displayName} (${domain.code})` : ''}
          </h2>
        </div>
      </div>
      <Card
        style={{ borderRadius: 8 }}
        loading={loading}
      >
        <div
          style={{
            height: 700,
            position: 'relative',
            cursor: draggingNodeId ? 'grabbing' : isPanning ? 'grabbing' : 'grab',
            overflow: 'hidden',
          }}
          onWheel={(e) => {
            e.preventDefault();
            const delta = e.deltaY;
            setZoom((prev) => {
              const factor = delta > 0 ? 0.9 : 1.1;
              const next = Math.min(3, Math.max(0.5, prev * factor));
              return next;
            });
          }}
          onMouseDown={(e) => {
            e.preventDefault();
            // 仅在未拖拽节点时启用平移
            if (!draggingNodeId) {
              setIsPanning(true);
              setLastPanPosition({ x: e.clientX, y: e.clientY });
            }
          }}
          onMouseMove={(e) => {
            // 节点拖拽
            if (draggingNodeId && lastDragPosition) {
              const dx = (e.clientX - lastDragPosition.x) / zoom;
              const dy = (e.clientY - lastDragPosition.y) / zoom;
              setGraphData(prev => ({
                ...prev,
                nodes: prev.nodes.map(n =>
                  n.id === draggingNodeId
                    ? { ...n, x: n.x + dx, y: n.y + dy }
                    : n
                ),
              }));
              setLastDragPosition({ x: e.clientX, y: e.clientY });
              return;
            }

            // 画布平移
            if (!isPanning || !lastPanPosition) return;
            const dx = e.clientX - lastPanPosition.x;
            const dy = e.clientY - lastPanPosition.y;
            setPan((prev) => ({
              x: prev.x + dx / zoom,
              y: prev.y + dy / zoom,
            }));
            setLastPanPosition({ x: e.clientX, y: e.clientY });
          }}
          onMouseUp={() => {
            setIsPanning(false);
            setLastPanPosition(null);
            setDraggingNodeId(null);
            setLastDragPosition(null);
          }}
          onMouseLeave={() => {
            setIsPanning(false);
            setLastPanPosition(null);
            setDraggingNodeId(null);
            setLastDragPosition(null);
          }}
        >
          <svg width="100%" height="100%" viewBox="-250 -250 600 600">
            <defs>
              <marker
                id="arrow"
                viewBox="0 0 10 10"
                refX="10"
                refY="5"
                markerWidth="6"
                markerHeight="6"
                orient="auto-start-reverse"
              >
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#999" />
              </marker>
            </defs>
            <g transform={`translate(${pan.x} ${pan.y}) scale(${zoom})`}>
              {/* 边 */}
              {graphData.links.map((link, idx) => {
                const sourceIndex = graphData.nodes.findIndex(n => n.id === link.source);
                const targetIndex = graphData.nodes.findIndex(n => n.id === link.target);
                if (sourceIndex === -1 || targetIndex === -1) return null;

                const rectWidth = 140;
                const rectHeight = 60;

                const sourceNode = graphData.nodes[sourceIndex];
                const targetNode = graphData.nodes[targetIndex];
                const sourceCenterX = sourceNode.x;
                const sourceCenterY = sourceNode.y;
                const targetCenterX = targetNode.x;
                const targetCenterY = targetNode.y;

                // 计算从源实体矩形边缘出发的点（不穿过实体）
                const computeEdgePoint = (
                  cx: number,
                  cy: number,
                  tx: number,
                  ty: number,
                  w: number,
                  h: number,
                  reverse = false,
                ) => {
                  let dx = tx - cx;
                  let dy = ty - cy;
                  if (reverse) {
                    dx = cx - tx;
                    dy = cy - ty;
                  }
                  if (dx === 0 && dy === 0) {
                    return { x: cx, y: cy };
                  }
                  const halfW = w / 2;
                  const halfH = h / 2;
                  const absDx = Math.abs(dx);
                  const absDy = Math.abs(dy);

                  let scale: number;
                  if (absDx * halfH > absDy * halfW) {
                    // 与左右边相交
                    scale = halfW / absDx;
                  } else {
                    // 与上下边相交
                    scale = halfH / absDy;
                  }

                  const ex = cx + dx * scale;
                  const ey = cy + dy * scale;
                  return { x: ex, y: ey };
                };

                const { x: sx, y: sy } = computeEdgePoint(
                  sourceCenterX,
                  sourceCenterY,
                  targetCenterX,
                  targetCenterY,
                  rectWidth,
                  rectHeight,
                  false,
                );

                const { x: tx, y: ty } = computeEdgePoint(
                  targetCenterX,
                  targetCenterY,
                  sourceCenterX,
                  sourceCenterY,
                  rectWidth,
                  rectHeight,
                  false,
                );

                const mx = (sx + tx) / 2;
                const my = (sy + ty) / 2;

                return (
                  <g key={idx}>
                    <line
                      x1={sx}
                      y1={sy}
                      x2={tx}
                      y2={ty}
                      stroke="#999"
                      strokeWidth={1.2}
                      markerEnd="url(#arrow)"
                    />
                    {link.label && (
                      <text
                        x={mx}
                        y={my - 8}
                        fill="#666"
                        fontSize={20}
                        textAnchor="middle"
                      >
                        {link.label}
                      </text>
                    )}
                  </g>
                );
              })}
              {/* 实体（对象类型）矩形 */}
              {graphData.nodes.map((node) => {
                const rectWidth = 140;
                const rectHeight = 60;
                const centerX = node.x;
                const centerY = node.y;
                const x = centerX - rectWidth / 2;
                const y = centerY - rectHeight / 2;
                const color = '#1677ff';

                return (
                  <g
                    key={node.id}
                    onMouseDown={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      setDraggingNodeId(node.id);
                      setLastDragPosition({ x: e.clientX, y: e.clientY });
                    }}
                    onClick={async (e) => {
                      e.stopPropagation();
                      try {
                        const [otRes, propRes] = await Promise.all([
                          objectTypeApi.getById(node.id),
                          propertyApi.getByObjectTypeId(node.id),
                        ]);
                        if (otRes.code === 200 && otRes.data) {
                          setSelectedObjectType(otRes.data);
                        }
                        if (propRes.code === 200 && propRes.data) {
                          setSelectedProperties(propRes.data || []);
                        } else {
                          setSelectedProperties([]);
                        }
                        setDetailModalVisible(true);
                      } catch (err: any) {
                        message.error(err.friendlyMessage || err.message || '加载对象类型详情失败');
                      }
                    }}
                  >
                    {/* 实体框体 */}
                    <rect
                      x={x}
                      y={y}
                      width={rectWidth}
                      height={rectHeight}
                      fill="#fff"
                      stroke={color}
                      strokeWidth={1.6}
                      rx={4}
                      ry={4}
                    />
                    {/* 实体名称 */}
                    <text
                      x={centerX}
                      y={centerY}
                      fill="#333"
                      fontSize={20}
                      textAnchor="middle"
                      dominantBaseline="middle"
                    >
                      {node.label || node.id}
                    </text>
                  </g>
                );
              })}
            </g>
          </svg>
        </div>
      </Card>

      {/* 对象类型详情弹窗（含属性列表） */}
      <Modal
        title={selectedObjectType ? `对象类型详情 - ${selectedObjectType.displayName}` : '对象类型详情'}
        open={detailModalVisible}
        width={800}
        footer={null}
        onCancel={() => {
          setDetailModalVisible(false);
        }}
      >
        {selectedObjectType && (
          <>
            <Descriptions column={2} bordered size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="名称">{selectedObjectType.name}</Descriptions.Item>
              <Descriptions.Item label="显示名称">{selectedObjectType.displayName}</Descriptions.Item>
              <Descriptions.Item label="描述" span={2}>
                {selectedObjectType.description || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="主键字段">{selectedObjectType.primaryKey}</Descriptions.Item>
            </Descriptions>

            <Card
              title="属性列表"
              size="small"
              style={{ borderRadius: 8 }}
              bodyStyle={{ padding: 0 }}
            >
              <Table<Property>
                size="small"
                columns={[
                  { title: '名称', dataIndex: 'name', key: 'name', width: 140 },
                  { title: '数据类型', dataIndex: 'dataType', key: 'dataType', width: 100 },
                  {
                    title: '必填',
                    dataIndex: 'required',
                    key: 'required',
                    width: 80,
                    render: (value: boolean) => (value ? '是' : '否'),
                  },
                  { title: '默认值', dataIndex: 'defaultValue', key: 'defaultValue', width: 120 },
                  {
                    title: '排序',
                    dataIndex: 'sortOrder',
                    key: 'sortOrder',
                    width: 80,
                  },
                  {
                    title: '描述',
                    dataIndex: 'description',
                    key: 'description',
                    ellipsis: true,
                  },
                ] as ColumnsType<Property>}
                dataSource={selectedProperties}
                rowKey="id"
                pagination={false}
              />
            </Card>
          </>
        )}
      </Modal>
    </div>
  );
};

export default DomainGraph;



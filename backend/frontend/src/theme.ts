/**
 * Ant Design 主题配置
 * 参考 Palantir Foundry 的设计风格
 */
import type { ThemeConfig } from 'antd';

export const themeConfig: ThemeConfig = {
  token: {
    // 主色调 - 使用专业的蓝色系
    colorPrimary: '#1890ff',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#ff4d4f',
    colorInfo: '#1890ff',
    
    // 字体
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif',
    fontSize: 14,
    
    // 圆角
    borderRadius: 4,
    
    // 间距
    padding: 16,
    margin: 16,
  },
  components: {
    Layout: {
      bodyBg: '#f0f2f5',
      headerBg: '#ffffff',
      siderBg: '#001529',
    },
    Menu: {
      darkItemBg: '#001529',
      darkSubMenuItemBg: '#000c17',
      darkItemSelectedBg: '#1890ff',
      darkItemHoverBg: 'rgba(255, 255, 255, 0.08)',
    },
    Card: {
      borderRadius: 8,
      paddingLG: 24,
    },
    Table: {
      borderRadius: 4,
    },
    Button: {
      borderRadius: 4,
    },
  },
};


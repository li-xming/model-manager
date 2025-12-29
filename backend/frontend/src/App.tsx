import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ConfigProvider, App as AntdApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';

import { themeConfig } from './theme';
import Layout from './layouts/BasicLayout';
import Home from './pages/Home';
import ObjectTypeList from './pages/ObjectType/List';
import ObjectTypeForm from './pages/ObjectType/Form';
import ObjectTypeDetail from './pages/ObjectType/Detail';
import LinkTypeList from './pages/LinkType/List';
import LinkTypeForm from './pages/LinkType/Form';
import LinkTypeDetail from './pages/LinkType/Detail';
import LinkInstanceForm from './pages/LinkInstance/Form';
import ActionTypeList from './pages/ActionType/List';
import ActionTypeForm from './pages/ActionType/Form';
import InterfaceList from './pages/Interface/List';
import InterfaceForm from './pages/Interface/Form';
import FunctionList from './pages/Function/List';
import FunctionForm from './pages/Function/Form';
import InstanceIndex from './pages/Instance/Index';
import InstanceList from './pages/Instance/List';
import InstanceForm from './pages/Instance/Form';
import QueryBuilder from './pages/Query/Builder';
import DomainList from './pages/Domain/List';
import DomainForm from './pages/Domain/Form';
import DomainGraph from './pages/Domain/Graph';
import MetaModelList from './pages/MetaModel/List';
import DataSourceList from './pages/DataSource/List';
import DataSourceForm from './pages/DataSource/Form';

// 设置dayjs中文
dayjs.locale('zh-cn');

function App() {
  return (
    <ConfigProvider locale={zhCN} theme={themeConfig}>
      <AntdApp>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<Home />} />
              <Route path="object-types" element={<ObjectTypeList />} />
              <Route path="object-types/new" element={<ObjectTypeForm />} />
              <Route path="object-types/:id/edit" element={<ObjectTypeForm />} />
              <Route path="object-types/:id" element={<ObjectTypeDetail />} />
              <Route path="link-types" element={<LinkTypeList />} />
              <Route path="link-types/new" element={<LinkTypeForm />} />
              <Route path="link-types/:id/edit" element={<LinkTypeForm />} />
              <Route path="link-types/:id" element={<LinkTypeDetail />} />
              <Route path="link-instances/new" element={<LinkInstanceForm />} />
              <Route path="link-instances/:id/edit" element={<LinkInstanceForm />} />
              <Route path="action-types" element={<ActionTypeList />} />
              <Route path="action-types/new" element={<ActionTypeForm />} />
              <Route path="action-types/:id/edit" element={<ActionTypeForm />} />
              <Route path="interfaces" element={<InterfaceList />} />
              <Route path="interfaces/new" element={<InterfaceForm />} />
              <Route path="interfaces/:id/edit" element={<InterfaceForm />} />
              <Route path="functions" element={<FunctionList />} />
              <Route path="functions/new" element={<FunctionForm />} />
              <Route path="functions/:id/edit" element={<FunctionForm />} />
              <Route path="domains" element={<DomainList />} />
              <Route path="domains/new" element={<DomainForm />} />
              <Route path="domains/:id/edit" element={<DomainForm />} />
              <Route path="domains/:id/graph" element={<DomainGraph />} />
              <Route path="datasources" element={<DataSourceList />} />
              <Route path="datasources/new" element={<DataSourceForm />} />
              <Route path="datasources/:id/edit" element={<DataSourceForm />} />
              <Route path="instances" element={<InstanceIndex />} />
              <Route path="instances/:objectTypeName" element={<InstanceList />} />
              <Route path="instances/:objectTypeName/new" element={<InstanceForm />} />
              <Route path="instances/:objectTypeName/:id/edit" element={<InstanceForm />} />
              <Route path="query" element={<QueryBuilder />} />
              <Route path="meta-models" element={<MetaModelList />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AntdApp>
    </ConfigProvider>
  );
}

export default App;

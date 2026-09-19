<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  getWorkOrderPage,
  startWorkOrder,
  addWorkOrder,
  completeWorkOrder,
} from '../api/workOrder'

const workOrderList = ref<any[]>([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const title = ref('')
const status = ref('')
const dialogVisible = ref(false)

const newWorkOrder = ref({
  equipmentId: 1,
  title: '',
  description: '',
  assignee: '',
})

async function loadWorkOrders() {
const response = await getWorkOrderPage({
  current: current.value,
  size: size.value,
  title: title.value,
  status: status.value,
})

  workOrderList.value = response.data.data.records
  total.value = response.data.data.total
}

function resetSearch() {
  title.value = ''
  status.value = ''
  current.value = 1
  loadWorkOrders()
}

async function handleStart(id: number) {
  await startWorkOrder(id)
  loadWorkOrders()
}
async function handleComplete(id: number) {
  const result = prompt('请输入处理结果')

  if (!result) {
    return
  }

  await completeWorkOrder(id, result)
  loadWorkOrders()
}

async function handleAdd() {
  await addWorkOrder(newWorkOrder.value)

  dialogVisible.value = false

  newWorkOrder.value = {
    equipmentId: 1,
    title: '',
    description: '',
    assignee: '',
  }

  loadWorkOrders()
}

onMounted(() => {
  loadWorkOrders()
})
</script>

<template>
  <div class="work-order-page">

    <!-- 页面标题 -->
    <div class="page-heading">
      <div>
        <h2>工单管理</h2>
        <p>创建维修任务，跟踪处理进度与完成结果</p>
      </div>

      <div class="heading-actions">
        <div class="heading-badge">
          共 {{ total }} 条工单
        </div>

        <el-button
          class="add-button"
          type="primary"
          @click="dialogVisible = true"
        >
          新增工单
        </el-button>
      </div>
    </div>

    <!-- 查询工具栏 -->
    <section class="toolbar glass-card">
      <div class="toolbar-fields">

        <el-input
          v-model="title"
          placeholder="搜索工单标题"
          clearable
          class="title-input"
          @keyup.enter="loadWorkOrders"
        />

        <el-select
          v-model="status"
          placeholder="工单状态"
          clearable
          class="status-select"
        >
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="已完成" value="已完成" />
        </el-select>

      </div>

      <div class="toolbar-actions">
        <el-button
          class="query-button"
          type="primary"
          @click="loadWorkOrders"
        >
          查询
        </el-button>

        <el-button
          class="reset-button"
          @click="resetSearch"
        >
          重置
        </el-button>
      </div>
    </section>

    <!-- 工单列表 -->
    <section class="table-card glass-card">

      <div class="table-header">
        <div>
          <div class="table-title">维修工单</div>
          <div class="table-description">
            按待处理、处理中、已完成进行状态流转
          </div>
        </div>

        <div class="flow-hint">
          待处理
          <span>→</span>
          处理中
          <span>→</span>
          已完成
        </div>
      </div>

      <el-table
        :data="workOrderList"
        class="work-order-table"
        style="width: 100%"
      >
        <el-table-column
          prop="id"
          label="工单ID"
          width="90"
        />

        <el-table-column
          prop="equipmentId"
          label="设备ID"
          width="100"
        />

        <el-table-column
          prop="title"
          label="工单标题"
          min-width="180"
        />

        <el-table-column
          label="状态"
          width="130"
        >
          <template #default="scope">
            <span
              class="status-pill"
              :class="{
                'status-pending': scope.row.status === '待处理',
                'status-processing': scope.row.status === '处理中',
                'status-completed': scope.row.status === '已完成'
              }"
            >
              <span class="status-dot"></span>
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
          prop="assignee"
          label="处理人"
          min-width="120"
        />

        <el-table-column
          prop="result"
          label="处理结果"
          min-width="200"
          show-overflow-tooltip
        />

        <el-table-column
          label="操作"
          width="170"
          fixed="right"
        >
          <template #default="scope">

            <el-button
              v-if="scope.row.status === '待处理'"
              class="start-action"
              link
              @click="handleStart(scope.row.id)"
            >
              开始维修
            </el-button>

            <el-button
              v-if="scope.row.status === '处理中'"
              class="complete-action"
              link
              @click="handleComplete(scope.row.id)"
            >
              完成维修
            </el-button>

            <span
              v-if="scope.row.status === '已完成'"
              class="finished-text"
            >
              已结束
            </span>

          </template>
        </el-table-column>

        <template #empty>
          <div class="empty-table">
            暂无工单数据
          </div>
        </template>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadWorkOrders"
        />
      </div>

    </section>

    <!-- 新增工单 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增维修工单"
      width="520px"
      class="work-order-dialog"
    >
      <div class="dialog-tip">
        填写设备及故障信息后创建维修任务
      </div>

      <el-form
        label-position="top"
        class="work-order-form"
      >
        <el-form-item label="设备 ID">
          <el-input-number
            v-model="newWorkOrder.equipmentId"
            :min="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="工单标题">
          <el-input
            v-model="newWorkOrder.title"
            placeholder="例如：设备异常噪音排查"
          />
        </el-form-item>

        <el-form-item label="故障描述">
          <el-input
            v-model="newWorkOrder.description"
            type="textarea"
            :rows="4"
            placeholder="请描述设备出现的故障现象"
          />
        </el-form-item>

        <el-form-item label="处理人">
          <el-input
            v-model="newWorkOrder.assignee"
            placeholder="请输入处理人"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button
            class="cancel-button"
            @click="dialogVisible = false"
          >
            取消
          </el-button>

          <el-button
            class="confirm-button"
            type="primary"
            @click="handleAdd"
          >
            确认新增
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.work-order-page {
  padding: 8px 2px 28px;
}

/* 标题 */

.page-heading {
  min-height: 68px;

  display: flex;
  justify-content: space-between;
  align-items: flex-start;

  margin-bottom: 18px;
  padding: 4px 4px 0;
}

.page-heading h2 {
  margin: 0;

  color: #202832;

  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.4px;
}

.page-heading p {
  margin: 8px 0 0;

  color: #929ca7;
  font-size: 12px;
}

.heading-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.heading-badge {
  padding: 8px 13px;

  border: 1px solid rgba(52, 65, 78, 0.07);
  border-radius: 12px;

  background: rgba(255, 255, 255, 0.52);

  color: #7d8791;
  font-size: 11px;

  backdrop-filter: blur(16px);
}

.add-button {
  height: 36px;

  padding: 0 16px;

  border: none;
  border-radius: 11px;

  background: #29343f;

  font-size: 12px;

  box-shadow:
    0 8px 18px rgba(41, 52, 63, 0.13);
}

.add-button:hover {
  background: #202a33;
}

/* 玻璃卡片 */

.glass-card {
  border: 1px solid rgba(255, 255, 255, 0.84);

  background: rgba(255, 255, 255, 0.62);

  box-shadow:
    0 12px 34px rgba(45, 61, 78, 0.055),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);

  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* 查询栏 */

.toolbar {
  min-height: 76px;

  padding: 15px 18px;

  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;

  border-radius: 18px;

  margin-bottom: 16px;
}

.toolbar-fields {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-input {
  width: 270px;
}

.status-select {
  width: 170px;
}

.toolbar :deep(.el-input__wrapper),
.toolbar :deep(.el-select__wrapper) {
  min-height: 40px;

  border-radius: 11px;

  background: rgba(255, 255, 255, 0.78);

  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.07) inset !important;
}

.toolbar-actions {
  display: flex;
  align-items: center;
}

.query-button,
.reset-button {
  height: 40px;

  padding: 0 19px;

  border-radius: 11px;

  font-size: 13px;
}

.query-button {
  border: none;

  background: #29343f;

  box-shadow:
    0 7px 16px rgba(41, 52, 63, 0.13);
}

.query-button:hover {
  background: #202a33;
}

.reset-button {
  border-color: rgba(52, 65, 78, 0.09);

  background: rgba(255, 255, 255, 0.6);

  color: #66717c;
}

/* 工单列表 */

.table-card {
  border-radius: 20px;

  padding: 20px 20px 16px;
}

.table-header {
  min-height: 48px;

  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  margin-bottom: 15px;
}

.table-title {
  color: #313b45;

  font-size: 15px;
  font-weight: 650;
}

.table-description {
  margin-top: 5px;

  color: #a0a8b1;

  font-size: 11px;
}

.flow-hint {
  display: flex;
  align-items: center;
  gap: 7px;

  padding: 7px 10px;

  border-radius: 10px;

  background: rgba(42, 52, 62, 0.05);

  color: #8c969f;

  font-size: 10px;
}

.flow-hint span {
  color: #bcc2c8;
}

/* 表格 */

.work-order-table {
  --el-table-border-color: rgba(62, 74, 86, 0.06);
  --el-table-header-bg-color: rgba(246, 248, 250, 0.7);
  --el-table-row-hover-bg-color: rgba(246, 248, 250, 0.65);
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;

  border-radius: 14px;
  overflow: hidden;
}

.work-order-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.work-order-table :deep(th.el-table__cell) {
  height: 46px;

  background: rgba(245, 247, 249, 0.74);

  color: #7a848e;

  font-size: 12px;
  font-weight: 600;
}

.work-order-table :deep(td.el-table__cell) {
  height: 54px;

  color: #4c5761;

  font-size: 13px;
}

.work-order-table :deep(.cell) {
  padding-left: 14px;
  padding-right: 14px;
}

/* 状态 */

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 7px;

  padding: 6px 10px;

  border-radius: 9px;

  font-size: 11px;
  font-weight: 500;
}

.status-dot {
  width: 6px;
  height: 6px;

  border-radius: 50%;
}

.status-pending {
  background: rgba(193, 143, 69, 0.10);
  color: #a77942;
}

.status-pending .status-dot {
  background: #c38e50;
}

.status-processing {
  background: rgba(83, 115, 149, 0.10);
  color: #587692;
}

.status-processing .status-dot {
  background: #6b89a7;
}

.status-completed {
  background: rgba(89, 151, 120, 0.10);
  color: #56866d;
}

.status-completed .status-dot {
  background: #6e9f82;
}

/* 操作 */

.start-action {
  color: #576f86 !important;
  font-weight: 500;
}

.start-action:hover {
  color: #2f465b !important;
}

.complete-action {
  color: #58856c !important;
  font-weight: 500;
}

.complete-action:hover {
  color: #35634c !important;
}

.finished-text {
  color: #a1a9b1;
  font-size: 11px;
}

/* 分页 */

.pagination-area {
  display: flex;
  justify-content: flex-end;

  padding-top: 17px;
}

.pagination-area :deep(.el-pagination) {
  --el-pagination-button-bg-color: rgba(255, 255, 255, 0.55);
  --el-pagination-hover-color: #303b46;
}

.pagination-area :deep(.btn-prev),
.pagination-area :deep(.btn-next),
.pagination-area :deep(.number) {
  border-radius: 9px !important;
}

/* 空表 */

.empty-table {
  padding: 38px;

  color: #a2abb4;

  font-size: 12px;
}

/* 新增工单弹窗 */

:deep(.work-order-dialog) {
  overflow: hidden;

  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 20px;

  background: rgba(250, 251, 252, 0.96);

  box-shadow:
    0 26px 70px rgba(39, 54, 69, 0.16);

  backdrop-filter: blur(22px);
}

:deep(.work-order-dialog .el-dialog__header) {
  margin-right: 0;

  padding: 21px 24px 14px;
}

:deep(.work-order-dialog .el-dialog__title) {
  color: #313b45;

  font-size: 16px;
  font-weight: 650;
}

:deep(.work-order-dialog .el-dialog__body) {
  padding: 4px 24px 8px;
}

:deep(.work-order-dialog .el-dialog__footer) {
  padding: 14px 24px 22px;
}

.dialog-tip {
  margin-bottom: 22px;

  color: #9ba4ad;

  font-size: 11px;
}

.work-order-form :deep(.el-form-item__label) {
  margin-bottom: 6px;

  color: #606b76;

  font-size: 12px;
  font-weight: 600;
}

.work-order-form :deep(.el-input__wrapper),
.work-order-form :deep(.el-textarea__inner),
.work-order-form :deep(.el-input-number) {
  border-radius: 11px;
}

.work-order-form :deep(.el-input__wrapper),
.work-order-form :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.72);

  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.08) inset !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.cancel-button,
.confirm-button {
  height: 38px;

  padding: 0 18px;

  border-radius: 10px;
}

.confirm-button {
  border: none;

  background: #29343f;
}

.confirm-button:hover {
  background: #202a33;
}

@media (max-width: 900px) {
  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .toolbar-fields {
    width: 100%;
  }

  .title-input,
  .status-select {
    flex: 1;
    width: auto;
  }

  .toolbar-actions {
    justify-content: flex-end;
  }

  .flow-hint {
    display: none;
  }
}
</style>

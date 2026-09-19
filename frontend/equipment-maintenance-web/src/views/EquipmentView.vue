<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipmentPage, deleteEquipment } from '../api/equipment'
import { getWorkOrderHistory } from '../api/workOrder'
const equipmentList = ref<any[]>([])

const current = ref(1)
const size = ref(10)
const total = ref(0)
const name = ref('')
const status = ref('')
const historyDialogVisible = ref(false)
const historyList = ref<any[]>([])
const historyEquipmentName = ref('')
const role = localStorage.getItem('role')

async function handleHistory(row: any) {
  historyEquipmentName.value = row.equipmentName

  const response = await getWorkOrderHistory(row.id)

  historyList.value = response.data.data
  historyDialogVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除设备“${row.equipmentName}”吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteEquipment(row.id)

    ElMessage.success('删除成功')

    loadEquipment()
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') {
      console.error(error)
    }
  }
}

async function loadEquipment() {
  const response = await getEquipmentPage({
    current: current.value,
    size: size.value,
    name: name.value,
    status: status.value,
  })

  equipmentList.value = response.data.data.records
  total.value = response.data.data.total
}

function resetSearch() {
  name.value = ''
  status.value = ''
  current.value = 1
  loadEquipment()
}

onMounted(() => {
  loadEquipment()
})
</script>

<template>
  <div class="equipment-page">

    <!-- 页面标题 -->
    <div class="page-heading">
      <div>
        <h2>设备管理</h2>
        <p>维护设备基础信息，查看状态及维修历史</p>
      </div>

      <div class="heading-badge">
        共 {{ total }} 台设备
      </div>
    </div>

    <!-- 查询工具栏 -->
    <section class="toolbar glass-card">

      <div class="toolbar-fields">
        <el-input
          v-model="name"
          placeholder="搜索设备名称"
          clearable
          class="name-input"
          @keyup.enter="loadEquipment"
        />

        <el-select
          v-model="status"
          placeholder="设备状态"
          clearable
          class="status-select"
        >
          <el-option label="正常" value="正常" />
          <el-option label="维修中" value="维修中" />
        </el-select>
      </div>

      <div class="toolbar-actions">
        <el-button
          class="query-button"
          type="primary"
          @click="loadEquipment"
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

    <!-- 设备列表 -->
    <section class="table-card glass-card">

      <div class="table-header">
        <div>
          <div class="table-title">设备列表</div>
          <div class="table-description">
            当前设备基础信息与运行状态
          </div>
        </div>

        <div class="role-tip">
          {{ role === 'ADMIN' ? '管理员权限' : '维修人员权限' }}
        </div>
      </div>

      <el-table
        :data="equipmentList"
        class="equipment-table"
        style="width: 100%"
      >
        <el-table-column
          prop="equipmentNo"
          label="设备编号"
          min-width="130"
        />

        <el-table-column
          prop="equipmentName"
          label="设备名称"
          min-width="160"
        />

        <el-table-column
          prop="equipmentType"
          label="设备类型"
          min-width="140"
        />

        <el-table-column
          label="状态"
          min-width="120"
        >
          <template #default="scope">
            <span
              class="status-pill"
              :class="
                scope.row.status === '正常'
                  ? 'status-normal'
                  : 'status-repair'
              "
            >
              <span class="status-indicator"></span>
              {{ scope.row.status }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
          prop="location"
          label="位置"
          min-width="150"
        />

        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="scope">

            <el-button
              class="history-action"
              link
              @click="handleHistory(scope.row)"
            >
              维修历史
            </el-button>

            <el-button
              v-if="role === 'ADMIN'"
              class="delete-action"
              link
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>

          </template>
        </el-table-column>

        <template #empty>
          <div class="empty-table">
            暂无设备数据
          </div>
        </template>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadEquipment"
        />
      </div>

    </section>

    <!-- 维修历史 -->
    <el-dialog
      v-model="historyDialogVisible"
      :title="`${historyEquipmentName} · 维修历史`"
      width="760px"
      class="history-dialog"
    >
      <div
        v-if="historyList.length > 0"
        class="history-content"
      >
        <el-table
          :data="historyList"
          class="history-table"
          style="width: 100%"
        >
          <el-table-column
            prop="title"
            label="工单标题"
            min-width="180"
          />

          <el-table-column
            prop="assignee"
            label="处理人"
            width="110"
          />

          <el-table-column
            prop="result"
            label="处理结果"
            min-width="220"
          />

          <el-table-column
            prop="updateTime"
            label="完成时间"
            width="180"
          />
        </el-table>
      </div>

      <el-empty
        v-else
        description="暂无维修历史"
      />
    </el-dialog>

  </div>
</template>

<style scoped>
.equipment-page {
  padding: 8px 2px 28px;
}

/* 页面标题 */

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

.heading-badge {
  padding: 8px 13px;

  border: 1px solid rgba(52, 65, 78, 0.07);
  border-radius: 12px;

  background: rgba(255, 255, 255, 0.52);

  color: #7d8791;

  font-size: 11px;

  backdrop-filter: blur(16px);
}

/* 通用玻璃卡片 */

.glass-card {
  border: 1px solid rgba(255, 255, 255, 0.84);

  background: rgba(255, 255, 255, 0.62);

  box-shadow:
    0 12px 34px rgba(45, 61, 78, 0.055),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);

  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* 搜索栏 */

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

.name-input {
  width: 260px;
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

.toolbar :deep(.el-input__wrapper.is-focus),
.toolbar :deep(.el-select__wrapper.is-focused) {
  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.24) inset !important;
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

/* 表格卡片 */

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

.role-tip {
  padding: 6px 10px;

  border-radius: 9px;

  background: rgba(42, 52, 62, 0.055);

  color: #8d969f;

  font-size: 10px;
}

/* Element Plus table */

.equipment-table {
  --el-table-border-color: rgba(62, 74, 86, 0.06);
  --el-table-header-bg-color: rgba(246, 248, 250, 0.7);
  --el-table-row-hover-bg-color: rgba(246, 248, 250, 0.65);
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;

  border-radius: 14px;
  overflow: hidden;
}

.equipment-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.equipment-table :deep(th.el-table__cell) {
  height: 46px;

  background: rgba(245, 247, 249, 0.74);

  color: #7a848e;

  font-size: 12px;
  font-weight: 600;
}

.equipment-table :deep(td.el-table__cell) {
  height: 54px;

  color: #4c5761;

  font-size: 13px;
}

.equipment-table :deep(.cell) {
  padding-left: 16px;
  padding-right: 16px;
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

.status-indicator {
  width: 6px;
  height: 6px;

  border-radius: 50%;
}

.status-normal {
  background: rgba(91, 153, 123, 0.10);

  color: #56886e;
}

.status-normal .status-indicator {
  background: #6fa184;
}

.status-repair {
  background: rgba(196, 142, 72, 0.11);

  color: #ac7940;
}

.status-repair .status-indicator {
  background: #c08b50;
}

/* 操作按钮 */

.history-action {
  color: #536b82 !important;

  font-weight: 500;
}

.history-action:hover {
  color: #263c50 !important;
}

.delete-action {
  margin-left: 8px !important;

  color: #b06c6c !important;
}

.delete-action:hover {
  color: #934f4f !important;
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

/* 空数据 */

.empty-table {
  padding: 38px;

  color: #a2abb4;

  font-size: 12px;
}

/* 维修历史弹窗 */

:deep(.history-dialog) {
  overflow: hidden;

  border: 1px solid rgba(255, 255, 255, 0.8);

  border-radius: 20px;

  background: rgba(250, 251, 252, 0.94);

  box-shadow:
    0 26px 70px rgba(39, 54, 69, 0.16);

  backdrop-filter: blur(22px);
}

:deep(.history-dialog .el-dialog__header) {
  margin-right: 0;

  padding: 21px 24px 17px;

  border-bottom: 1px solid rgba(67, 78, 89, 0.06);
}

:deep(.history-dialog .el-dialog__title) {
  color: #313b45;

  font-size: 15px;
  font-weight: 650;
}

:deep(.history-dialog .el-dialog__body) {
  padding: 20px 24px 24px;
}

.history-table {
  --el-table-border-color: rgba(62, 74, 86, 0.06);
  --el-table-header-bg-color: rgba(245, 247, 249, 0.8);
}

@media (max-width: 900px) {
  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .toolbar-fields {
    width: 100%;
  }

  .name-input,
  .status-select {
    flex: 1;
    width: auto;
  }

  .toolbar-actions {
    justify-content: flex-end;
  }
}
</style>

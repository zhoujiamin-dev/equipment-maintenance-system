<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  analyzeFaultWithRag,
  createWorkOrderFromAnalysis,
} from '../api/ai'
import { getEquipmentPage } from '../api/equipment'

const description = ref('')
const analysisResult = ref('')
const loading = ref(false)
const equipmentId = ref<number | null>(null)
const equipmentList = ref<any[]>([])
const analysisId = ref<number | null>(null)
const sources = ref<string[]>([])

async function loadEquipment() {
  const response = await getEquipmentPage({
    current: 1,
    size: 100,
  })

  equipmentList.value = response.data.data.records
}

onMounted(() => {
  loadEquipment()
})

async function handleAnalyze() {
  if (!equipmentId.value || !description.value.trim()) {
    return
  }

  loading.value = true
  analysisResult.value = ''
  sources.value = []

  try {
    const response = await analyzeFaultWithRag(
      equipmentId.value,
      description.value,
    )

    analysisId.value = response.data.analysisId
    analysisResult.value = response.data.analysisResult
    sources.value = response.data.sources || []
  } catch (error) {
    analysisResult.value = 'AI 分析暂时失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function handleCreateWorkOrder() {
  if (!analysisId.value) {
    return
  }

  try {
    const response = await createWorkOrderFromAnalysis(analysisId.value)

    if (response.data.code === 200) {
      alert('维修工单创建成功')
    } else {
      alert(response.data.message || '维修工单创建失败')
    }
  } catch (error) {
    alert('维修工单创建失败')
  }
}

</script>

<template>
  <div class="analysis-page">

    <!-- 页面标题 -->
    <div class="page-heading">
      <div>
        <h2>故障辅助分析</h2>
        <p>结合设备维修知识库，对故障现象进行检索增强分析</p>
      </div>

      <div class="rag-badge">
        <span class="rag-dot"></span>
        RAG 知识检索
      </div>
    </div>

    <!-- 主体区域 -->
    <div class="analysis-layout">

      <!-- 左侧：输入 -->
      <section class="input-card glass-card">

        <div class="section-header">
          <div>
            <div class="section-title">故障信息</div>
            <div class="section-description">
              选择设备并描述实际故障现象
            </div>
          </div>

          <div class="step-number">01</div>
        </div>

        <div class="form-area">

          <div class="field-group">
            <div class="field-label">分析设备</div>

            <el-select
              v-model="equipmentId"
              placeholder="请选择需要分析的设备"
              class="equipment-select"
            >
              <el-option
                v-for="item in equipmentList"
                :key="item.id"
                :label="`${item.equipmentName} · ${item.equipmentNo}`"
                :value="item.id"
              />
            </el-select>
          </div>

          <div class="field-group description-group">
            <div class="field-label">
              故障描述
              <span>尽量描述具体现象</span>
            </div>

            <el-input
              v-model="description"
              type="textarea"
              :rows="8"
              resize="none"
              placeholder="例如：设备运行一段时间后温度明显升高，同时伴随异常噪音和振动。"
            />
          </div>

          <div class="analysis-tip">
            <div class="tip-icon">i</div>

            <div>
              <div class="tip-title">描述建议</div>
              <div class="tip-text">
                可补充噪音、振动、温度、流量、压力等现象，信息越完整越便于检索相关维修资料。
              </div>
            </div>
          </div>

          <el-button
            class="analyze-button"
            type="primary"
            :loading="loading"
            :disabled="!equipmentId || !description.trim()"
            @click="handleAnalyze"
          >
            {{ loading ? '正在分析...' : '开始故障分析' }}
          </el-button>

        </div>

      </section>

      <!-- 右侧：分析结果 -->
      <section class="result-card glass-card">

        <div class="section-header">
          <div>
            <div class="section-title">分析结果</div>
            <div class="section-description">
              基于检索到的维修资料生成辅助诊断建议
            </div>
          </div>

          <div class="step-number">02</div>
        </div>

        <!-- 暂无分析 -->
        <div
          v-if="!analysisResult && !loading"
          class="empty-result"
        >
          <div class="empty-mark">
            <span></span>
            <span></span>
            <span></span>
          </div>

          <div class="empty-title">等待故障分析</div>

          <div class="empty-description">
            请先在左侧选择设备并填写故障描述
          </div>
        </div>

        <!-- 加载中 -->
        <div
          v-else-if="loading"
          class="loading-result"
        >
          <div class="loading-ring"></div>
          <div class="loading-title">正在分析故障信息</div>
          <div class="loading-description">
            正在检索维修知识并生成辅助诊断结果
          </div>
        </div>

        <!-- 有结果 -->
        <div
          v-else
          class="result-content"
        >

          <div class="result-toolbar">
            <div class="result-status">
              <span class="success-dot"></span>
              分析完成
            </div>

            <div
              v-if="analysisId"
              class="analysis-id"
            >
              记录 #{{ analysisId }}
            </div>
          </div>

          <div class="answer-box">
            {{ analysisResult }}
          </div>

          <!-- 来源 -->
          <div
            v-if="sources.length > 0"
            class="sources-section"
          >
            <div class="sources-heading">
              <div>
                <div class="sources-title">参考维修资料</div>
                <div class="sources-subtitle">
                  本次回答检索到 {{ sources.length }} 条相关知识
                </div>
              </div>

              <span class="source-count">
                {{ sources.length }}
              </span>
            </div>

            <div class="source-list">
              <div
                v-for="(source, index) in sources"
                :key="index"
                class="source-item"
              >
                <div class="source-index">
                  {{ String(index + 1).padStart(2, '0') }}
                </div>

                <div class="source-text">
                  {{ source }}
                </div>
              </div>
            </div>
          </div>

          <!-- 没有资料 -->
          <div
            v-else
            class="no-source-box"
          >
            <span class="no-source-dot"></span>

            <div>
              <div class="no-source-title">未检索到直接相关资料</div>
              <div class="no-source-text">
                当前知识库支持范围有限，分析结果请结合现场情况判断。
              </div>
            </div>
          </div>

          <!-- 一键工单 -->
          <div
            v-if="analysisResult && analysisId"
            class="result-actions"
          >
            <div class="action-description">
              确认需要维修处理后，可将本次分析直接转为维修工单。
            </div>

            <el-button
              class="create-order-button"
              type="primary"
              @click="handleCreateWorkOrder"
            >
              一键创建维修工单
            </el-button>
          </div>

        </div>

      </section>

    </div>

    <!-- 底部流程说明 -->
    <section class="process-card glass-card">

      <div class="process-title">
        故障辅助处理流程
      </div>

      <div class="process-flow">

        <div class="process-step">
          <span class="process-index">01</span>
          <div>
            <div class="process-name">描述故障</div>
            <div class="process-text">录入设备实际故障现象</div>
          </div>
        </div>

        <div class="process-line"></div>

        <div class="process-step">
          <span class="process-index">02</span>
          <div>
            <div class="process-name">知识检索</div>
            <div class="process-text">检索相关维修资料</div>
          </div>
        </div>

        <div class="process-line"></div>

        <div class="process-step">
          <span class="process-index">03</span>
          <div>
            <div class="process-name">辅助分析</div>
            <div class="process-text">生成原因与检查建议</div>
          </div>
        </div>

        <div class="process-line"></div>

        <div class="process-step">
          <span class="process-index">04</span>
          <div>
            <div class="process-name">维修工单</div>
            <div class="process-text">确认后进入维修流程</div>
          </div>
        </div>

      </div>

    </section>

  </div>
</template>

<style scoped>
.analysis-page {
  padding: 8px 2px 28px;
}

/* 标题 */

.page-heading {
  min-height: 68px;

  display: flex;
  align-items: flex-start;
  justify-content: space-between;

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

.rag-badge {
  display: flex;
  align-items: center;
  gap: 7px;

  padding: 8px 12px;

  border: 1px solid rgba(52, 65, 78, 0.07);
  border-radius: 12px;

  background: rgba(255, 255, 255, 0.52);

  color: #74808b;

  font-size: 11px;

  backdrop-filter: blur(16px);
}

.rag-dot {
  width: 7px;
  height: 7px;

  border-radius: 50%;

  background: #7495ae;

  box-shadow:
    0 0 0 4px rgba(116, 149, 174, 0.10);
}

/* 卡片 */

.glass-card {
  border: 1px solid rgba(255, 255, 255, 0.84);

  background: rgba(255, 255, 255, 0.62);

  box-shadow:
    0 12px 34px rgba(45, 61, 78, 0.055),
    inset 0 1px 0 rgba(255, 255, 255, 0.82);

  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* 两栏 */

.analysis-layout {
  display: grid;
  grid-template-columns: minmax(360px, 0.78fr) minmax(520px, 1.22fr);

  gap: 16px;
}

.input-card,
.result-card {
  min-height: 560px;

  border-radius: 20px;

  padding: 22px;
}

/* 卡片标题 */

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  padding-bottom: 18px;

  border-bottom:
    1px solid rgba(58, 70, 82, 0.055);
}

.section-title {
  color: #313b45;

  font-size: 16px;
  font-weight: 650;
}

.section-description {
  margin-top: 6px;

  color: #9aa3ac;

  font-size: 11px;
}

.step-number {
  color: #c4cad0;

  font-size: 11px;
  font-weight: 600;
}

/* 左侧表单 */

.form-area {
  padding-top: 22px;
}

.field-group + .field-group {
  margin-top: 22px;
}

.field-label {
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 9px;

  color: #59646f;

  font-size: 12px;
  font-weight: 600;
}

.field-label span {
  color: #a8afb6;

  font-size: 10px;
  font-weight: 400;
}

.equipment-select {
  width: 100%;
}

.input-card :deep(.el-select__wrapper),
.input-card :deep(.el-input__wrapper) {
  min-height: 45px;

  border-radius: 12px;

  background: rgba(255, 255, 255, 0.74);

  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.07) inset !important;
}

.input-card :deep(.el-textarea__inner) {
  padding: 14px 15px;

  border: none;
  border-radius: 13px;

  background: rgba(255, 255, 255, 0.72);

  color: #4c5761;

  font-size: 13px;
  line-height: 1.75;

  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.07) inset !important;
}

.input-card :deep(.el-select__wrapper.is-focused),
.input-card :deep(.el-input__wrapper.is-focus),
.input-card :deep(.el-textarea__inner:focus) {
  box-shadow:
    0 0 0 1px rgba(52, 65, 78, 0.25) inset !important;
}

/* 提示 */

.analysis-tip {
  margin-top: 18px;

  display: flex;
  gap: 11px;

  padding: 13px 14px;

  border-radius: 13px;

  background: rgba(78, 105, 127, 0.045);
}

.tip-icon {
  flex: none;

  width: 22px;
  height: 22px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 7px;

  background: rgba(79, 105, 126, 0.08);

  color: #758795;

  font-size: 11px;
  font-weight: 700;
}

.tip-title {
  color: #6d7883;

  font-size: 11px;
  font-weight: 600;
}

.tip-text {
  margin-top: 4px;

  color: #9ca4ac;

  font-size: 10px;
  line-height: 1.65;
}

/* 分析按钮 */

.analyze-button {
  width: 100%;
  height: 44px;

  margin-top: 20px;

  border: none;
  border-radius: 12px;

  background: #29343f;

  font-size: 13px;
  font-weight: 600;

  box-shadow:
    0 8px 18px rgba(41, 52, 63, 0.13);
}

.analyze-button:hover,
.analyze-button:focus {
  background: #202a33;
}

.analyze-button.is-disabled {
  background: #d7dce1;
}

/* 空状态 */

.empty-result,
.loading-result {
  min-height: 430px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-mark {
  width: 50px;
  height: 50px;

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  border-radius: 16px;

  background: rgba(49, 60, 71, 0.045);
}

.empty-mark span {
  width: 5px;
  height: 5px;

  border-radius: 50%;

  background: #aeb6be;
}

.empty-title,
.loading-title {
  margin-top: 18px;

  color: #5e6974;

  font-size: 13px;
  font-weight: 600;
}

.empty-description,
.loading-description {
  margin-top: 7px;

  color: #a3abb3;

  font-size: 11px;
}

/* 加载 */

.loading-ring {
  width: 33px;
  height: 33px;

  border: 3px solid rgba(64, 77, 90, 0.08);
  border-top-color: #697a89;

  border-radius: 50%;

  animation: loading-spin 0.85s linear infinite;
}

@keyframes loading-spin {
  to {
    transform: rotate(360deg);
  }
}

/* 分析结果 */

.result-content {
  padding-top: 20px;
}

.result-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 14px;
}

.result-status {
  display: flex;
  align-items: center;
  gap: 8px;

  color: #668273;

  font-size: 11px;
  font-weight: 600;
}

.success-dot {
  width: 7px;
  height: 7px;

  border-radius: 50%;

  background: #6f9e82;

  box-shadow:
    0 0 0 4px rgba(111, 158, 130, 0.10);
}

.analysis-id {
  color: #a3abb3;

  font-size: 10px;
}

.answer-box {
  max-height: 370px;

  overflow: auto;

  padding: 18px;

  border-radius: 15px;

  background: rgba(247, 249, 251, 0.70);

  color: #48535e;

  white-space: pre-wrap;

  font-size: 12px;
  line-height: 1.9;
}

/* 来源 */

.sources-section {
  margin-top: 20px;

  padding-top: 19px;

  border-top:
    1px solid rgba(58, 70, 82, 0.055);
}

.sources-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 12px;
}

.sources-title {
  color: #59646f;

  font-size: 12px;
  font-weight: 650;
}

.sources-subtitle {
  margin-top: 4px;

  color: #a3abb3;

  font-size: 10px;
}

.source-count {
  min-width: 24px;
  height: 24px;

  padding: 0 7px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 8px;

  background: rgba(59, 74, 88, 0.055);

  color: #818b95;

  font-size: 10px;
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.source-item {
  display: grid;
  grid-template-columns: 34px 1fr;

  gap: 10px;

  padding: 12px;

  border-radius: 12px;

  background: rgba(248, 249, 250, 0.66);
}

.source-index {
  width: 29px;
  height: 29px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 9px;

  background: rgba(57, 70, 82, 0.055);

  color: #929ca5;

  font-size: 9px;
}

.source-text {
  max-height: 105px;

  overflow: auto;

  color: #68737e;

  white-space: pre-wrap;

  font-size: 10px;
  line-height: 1.7;
}

/* 无来源 */

.no-source-box {
  margin-top: 18px;

  display: flex;
  align-items: flex-start;
  gap: 10px;

  padding: 13px 14px;

  border-radius: 12px;

  background: rgba(177, 135, 77, 0.06);
}

.no-source-dot {
  flex: none;

  width: 7px;
  height: 7px;

  margin-top: 4px;

  border-radius: 50%;

  background: #c19a67;
}

.no-source-title {
  color: #8d785d;

  font-size: 11px;
  font-weight: 600;
}

.no-source-text {
  margin-top: 4px;

  color: #a89c8c;

  font-size: 10px;
  line-height: 1.6;
}

/* 创建工单 */

.result-actions {
  margin-top: 19px;

  padding-top: 17px;

  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;

  border-top:
    1px solid rgba(58, 70, 82, 0.055);
}

.action-description {
  color: #9ca4ac;

  font-size: 10px;
  line-height: 1.6;
}

.create-order-button {
  flex: none;

  height: 38px;

  padding: 0 16px;

  border: none;
  border-radius: 10px;

  background: #29343f;

  font-size: 11px;
}

.create-order-button:hover {
  background: #202a33;
}

/* 底部流程 */

.process-card {
  margin-top: 16px;

  padding: 18px 22px;

  border-radius: 18px;
}

.process-title {
  margin-bottom: 16px;

  color: #626d78;

  font-size: 11px;
  font-weight: 600;
}

.process-flow {
  display: flex;
  align-items: center;
}

.process-step {
  display: flex;
  align-items: center;
  gap: 9px;

  flex: none;
}

.process-index {
  width: 28px;
  height: 28px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 9px;

  background: rgba(47, 59, 71, 0.055);

  color: #89939c;

  font-size: 9px;
  font-weight: 600;
}

.process-name {
  color: #606b75;

  font-size: 11px;
  font-weight: 600;
}

.process-text {
  margin-top: 3px;

  color: #a4acb3;

  font-size: 9px;
}

.process-line {
  flex: 1;

  height: 1px;

  margin: 0 18px;

  background: rgba(80, 91, 102, 0.09);
}

/* 响应式 */

@media (max-width: 1100px) {
  .analysis-layout {
    grid-template-columns: 1fr;
  }

  .input-card,
  .result-card {
    min-height: auto;
  }
}

@media (max-width: 760px) {
  .process-flow {
    align-items: flex-start;
    flex-direction: column;
    gap: 14px;
  }

  .process-line {
    display: none;
  }

  .result-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .create-order-button {
    width: 100%;
  }
}
</style>

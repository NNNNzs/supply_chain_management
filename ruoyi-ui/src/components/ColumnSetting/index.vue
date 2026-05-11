<template>
  <el-popover placement="bottom" :width="300" trigger="click">
    <template #reference>
      <el-button :icon="Setting" circle />
    </template>
    <div class="column-setting">
      <div class="column-setting__header">
        <span>表头设置</span>
        <el-button link type="primary" size="small" @click="handleReset">重置</el-button>
      </div>
      <div class="column-setting__list">
        <draggable
          v-model="localColumns"
          item-key="key"
          handle=".column-setting__drag"
          animation="200"
          ghost-class="column-setting__ghost"
        >
          <template #item="{ element }">
            <div class="column-setting__item" :class="{ 'is-disabled': element.fixed }">
              <el-icon v-if="!element.fixed" class="column-setting__drag"><Rank /></el-icon>
              <span v-else class="column-setting__drag-placeholder" />
              <el-checkbox v-model="element.visible" :disabled="element.fixed" @change="handleChange">
                {{ element.label }}
              </el-checkbox>
            </div>
          </template>
        </draggable>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { computed } from 'vue'
import { Setting, Rank } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'

const props = defineProps({
  columns: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['reset', 'update:columns'])

const localColumns = computed({
  get: () => props.columns,
  set: (val) => emit('update:columns', val),
})

function handleReset() {
  emit('reset')
}

function handleChange() {
  emit('change')
}
</script>

<style scoped>
.column-setting__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 14px;
}

.column-setting__list {
  max-height: 360px;
  overflow-y: auto;
}

.column-setting__item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
  border-radius: 4px;
}

.column-setting__item.is-disabled {
  opacity: 0.6;
}

.column-setting__drag {
  cursor: grab;
  color: #909399;
  flex-shrink: 0;
}

.column-setting__drag-placeholder {
  width: 16px;
  flex-shrink: 0;
}

.column-setting__ghost {
  opacity: 0.5;
  background: #e6f0ff;
}
</style>

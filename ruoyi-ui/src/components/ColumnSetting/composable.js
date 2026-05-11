import { ref, watch } from 'vue'

/**
 * 通用表头设置 composable
 * @param {string} storageKey - localStorage 存储键
 * @param {Array<{key: string, label: string, visible?: boolean, fixed?: boolean}>} defaultColumns - 默认列配置
 * @returns {{ columns, visibleColumns, resetColumns }}
 */
export function useColumnSetting(storageKey, defaultColumns) {
  const STORAGE_PREFIX = 'column_setting:'

  function loadColumns() {
    try {
      const stored = localStorage.getItem(STORAGE_PREFIX + storageKey)
      if (stored) {
        const savedKeys = JSON.parse(stored)
        // savedKeys 格式: ['orderNo', 'customerName', ...] 按顺序排列的可见列 key
        const savedSet = new Set(savedKeys)
        // 按 savedKeys 的顺序排列可见列，再追加默认可见但未保存的列
        const ordered = []
        for (const key of savedKeys) {
          const col = defaultColumns.find(c => c.key === key)
          if (col) ordered.push({ ...col, visible: true })
        }
        for (const col of defaultColumns) {
          if (!savedSet.has(col.key)) {
            ordered.push({ ...col, visible: false })
          }
        }
        return ordered
      }
    } catch (e) {
      // ignore
    }
    return defaultColumns.map(c => ({ ...c, visible: c.visible !== false }))
  }

  const columns = ref(loadColumns())

  function persistColumns() {
    const visibleKeys = columns.value.filter(c => c.visible).map(c => c.key)
    localStorage.setItem(STORAGE_PREFIX + storageKey, JSON.stringify(visibleKeys))
  }

  watch(columns, persistColumns, { deep: true })

  function isVisible(key) {
    const col = columns.value.find(c => c.key === key)
    return col ? col.visible : true
  }

  function resetColumns() {
    columns.value = defaultColumns.map(c => ({ ...c, visible: c.visible !== false }))
  }

  return { columns, isVisible, resetColumns }
}

import { ref, watch, onMounted } from 'vue'

/**
 * 表格列宽持久化 composable
 * @param {string} storageKey - localStorage 存储键
 * @returns {{ saveColumnWidth, loadColumnWidth, resetColumnWidths, handleHeaderDragend, getColumnWidth }}
 */
export function useTableColumnWidth(storageKey) {
  const STORAGE_PREFIX = 'table_column_width:'
  const columnWidths = ref({})

  // 加载所有列宽
  function loadAllColumnWidths() {
    try {
      const stored = localStorage.getItem(STORAGE_PREFIX + storageKey)
      return stored ? JSON.parse(stored) : {}
    } catch (e) {
      console.warn('Failed to load column widths:', e)
      return {}
    }
  }

  /**
   * 获取列宽
   * @param {string} columnKey - 列标识
   * @returns {number|undefined} 列宽
   */
  function getColumnWidth(columnKey) {
    if (!columnWidths.value[columnKey]) {
      const allWidths = loadAllColumnWidths()
      columnWidths.value = { ...allWidths }
    }
    return columnWidths.value[columnKey]
  }

  /**
   * 保存列宽
   * @param {object} params - Element Plus table header dragend 事件参数
   */
  function handleHeaderDragend(params) {
    const { column, width } = params
    if (column && column.property && width) {
      try {
        const allWidths = loadAllColumnWidths()
        allWidths[column.property] = width
        localStorage.setItem(STORAGE_PREFIX + storageKey, JSON.stringify(allWidths))
        columnWidths.value = allWidths
      } catch (e) {
        console.warn('Failed to save column width:', e)
      }
    }
  }

  /**
   * 重置所有列宽
   */
  function resetColumnWidths() {
    try {
      localStorage.removeItem(STORAGE_PREFIX + storageKey)
      columnWidths.value = {}
    } catch (e) {
      console.warn('Failed to reset column widths:', e)
    }
  }

  return {
    getColumnWidth,
    handleHeaderDragend,
    resetColumnWidths
  }
}

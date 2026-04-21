// 全局属性类型声明
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

// 声明 window 上挂载的全局变量
declare global {
  interface Window {
    // 若依框架全局变量
    $store: any
    $config: any
  }

  // Vue 原型上的全局方法（若依框架）
  const $modal: {
    msg: (message: string) => void
    msgError: (message: string) => void
    msgSuccess: (message: string) => void
    msgWarning: (message: string) => void
    alert: (message: string) => void
    alertError: (message: string) => void
    alertSuccess: (message: string) => void
    alertWarning: (message: string) => void
    confirm: (message: string) => Promise<boolean>
    prompt: (message: string) => Promise<string | null>
    notify: (message: string) => void
    notifyError: (message: string) => void
    notifySuccess: (message: string) => void
    notifyWarning: (message: string) => void
    showLoading: (message?: string) => void
    closeLoading: () => void
  }

  const $tab: {
    openPage: (url: string, title?: string) => void
    refreshPage: () => void
    closePage: (obj?: any) => void
  }

  const $auth: {
    hasPermi: (permissions: string | string[]) => boolean
    hasRole: (roles: string | string[]) => boolean
  }

  const $cache: {
    session: {
      set: (key: string, value: any) => void
      get: (key: string) => any
      remove: (key: string) => void
      clear: () => void
    }
    local: {
      set: (key: string, value: any) => void
      get: (key: string) => any
      remove: (key: string) => void
      clear: () => void
    }
  }
}

export {}

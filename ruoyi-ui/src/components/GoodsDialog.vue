<template>
  <el-dialog
    :title="title"
    v-model="dialogVisible"
    width="600px"
    append-to-body
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="goodsFormRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="货物名称" prop="goodsName">
        <el-input v-model="form.goodsName" placeholder="请输入货物名称" />
      </el-form-item>
      <el-form-item label="货物型号" prop="goodsModel">
        <el-input v-model="form.goodsModel" placeholder="请输入货物型号" />
      </el-form-item>
      <el-form-item label="货物分类" prop="goodsCategory">
        <el-select v-model="form.goodsCategory" placeholder="请选择货物分类" style="width: 100%">
          <el-option label="普通货物" value="普通货物" />
          <el-option label="危险品" value="危险品" />
          <el-option label="易碎品" value="易碎品" />
          <el-option label="冷链品" value="冷链品" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="计量单位" prop="goodsUnit">
        <el-select v-model="form.goodsUnit" placeholder="请选择计量单位" style="width: 100%">
          <el-option label="吨" value="吨" />
          <el-option label="千克" value="千克" />
          <el-option label="立方米" value="立方米" />
          <el-option label="件" value="件" />
          <el-option label="箱" value="箱" />
        </el-select>
      </el-form-item>
      <el-form-item label="参考单价" prop="unitPrice">
        <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="请输入参考单价" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">确 定</el-button>
        <el-button @click="handleClose">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, toRefs, watch } from 'vue'
import { addGoods } from '@/api/logistics/goods'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '新增货物'
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const goodsFormRef = ref(null)

const data = reactive({
  form: {
    goodsName: null,
    goodsModel: null,
    goodsCategory: '其他',
    goodsUnit: '吨',
    unitPrice: 0,
    status: '0'
  },
  rules: {
    goodsName: [
      { required: true, message: "货物名称不能为空", trigger: "blur" }
    ],
    goodsModel: [
      { required: true, message: "货物型号不能为空", trigger: "blur" }
    ],
    goodsCategory: [
      { required: true, message: "货物分类不能为空", trigger: "change" }
    ],
    goodsUnit: [
      { required: true, message: "计量单位不能为空", trigger: "change" }
    ]
  }
})

const { form, rules } = toRefs(data)

const dialogVisible = ref(false)

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    resetForm()
  }
})

function resetForm() {
  form.value = {
    goodsName: null,
    goodsModel: null,
    goodsCategory: '其他',
    goodsUnit: '吨',
    unitPrice: 0,
    status: '0'
  }
  if (goodsFormRef.value) {
    goodsFormRef.value.resetFields()
  }
}

function handleConfirm() {
  goodsFormRef.value.validate((valid) => {
    if (valid) {
      addGoods(form.value).then(response => {
        emit('success', response.data)
        handleClose()
      }).catch(error => {
        console.error('新增货物失败:', error)
      })
    }
  })
}

function handleClose() {
  dialogVisible.value = false
  resetForm()
}

defineExpose({
  resetForm
})
</script>

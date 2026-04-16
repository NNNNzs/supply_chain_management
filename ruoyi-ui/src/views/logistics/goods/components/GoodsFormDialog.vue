<template>
  <el-dialog
    :title="title"
    v-model="visible"
    width="600px"
    append-to-body
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="货物编码" prop="goodsCode">
            <el-input v-model="form.goodsCode" placeholder="请输入货物编码" maxlength="50" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="货物名称" prop="goodsName">
            <el-input v-model="form.goodsName" placeholder="请输入货物名称" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="货物型号" prop="goodsModel">
            <el-input v-model="form.goodsModel" placeholder="请输入货物型号" maxlength="100" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="货物分类" prop="goodsCategory">
            <el-input v-model="form.goodsCategory" placeholder="请输入货物分类" maxlength="50" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="计量单位" prop="goodsUnit">
            <el-select v-model="form.goodsUnit" placeholder="请选择计量单位" style="width: 100%">
              <el-option label="吨" value="吨" />
              <el-option label="千克" value="千克" />
              <el-option label="立方米" value="立方米" />
              <el-option label="件" value="件" />
              <el-option label="箱" value="箱" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="参考单价" prop="unitPrice">
            <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :controls="false" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio value="0">正常</el-radio>
              <el-radio value="1">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { addGoods } from "@/api/logistics/goods"

const { proxy } = getCurrentInstance()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const title = ref("添加货物")
const formRef = ref(null)

const data = reactive({
  form: {
    goodsId: null,
    goodsCode: null,
    goodsName: null,
    goodsModel: null,
    goodsUnit: "吨",
    goodsCategory: null,
    unitPrice: 0,
    status: "0",
    remark: null
  },
  rules: {
    goodsCode: [
      { required: true, message: "货物编码不能为空", trigger: "blur" }
    ],
    goodsName: [
      { required: true, message: "货物名称不能为空", trigger: "blur" }
    ]
  }
})

const { form, rules } = toRefs(data)

function reset() {
  form.value = {
    goodsId: null,
    goodsCode: null,
    goodsName: null,
    goodsModel: null,
    goodsUnit: "吨",
    goodsCategory: null,
    unitPrice: 0,
    status: "0",
    remark: null
  }
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (valid) {
      addGoods(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功")
        visible.value = false
        emit('success', response.data)
      })
    }
  })
}

function cancel() {
  visible.value = false
}

function handleClosed() {
  reset()
  proxy.$refs.formRef?.resetFields()
}
</script>

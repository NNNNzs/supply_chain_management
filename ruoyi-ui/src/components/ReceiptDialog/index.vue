<template>
  <el-dialog :title="title" v-model="visible" width="600px" append-to-body :before-close="handleClose">
    <el-form ref="receiptRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单号" prop="orderId">
        <el-select v-model="form.orderId" placeholder="请选择订单" clearable filterable style="width: 100%">
          <el-option v-for="item in orderOptions" :key="item.orderId" :label="item.orderNo" :value="item.orderId">
            <span>{{ item.orderNo }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">{{ item.customerName }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="回单日期" prop="receiptDate">
        <el-date-picker v-model="form.receiptDate" type="date" placeholder="选择回单日期" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
      <el-form-item label="回单图片" prop="receiptImage">
        <image-upload
          v-model="form.receiptImage"
          :limit="5"
          :file-size="10"
          :file-type="['jpg', 'jpeg', 'png', 'gif']"
        />
      </el-form-item>
      <el-form-item label="回单状态" prop="receiptStatus">
        <el-radio-group v-model="form.receiptStatus">
          <el-radio label="not_received">未收到</el-radio>
          <el-radio label="received">已收到</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接收人" prop="receiver">
        <el-input v-model="form.receiver" placeholder="请输入接收人" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="handleClose">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { addReceipt, updateReceipt } from "@/api/logistics/receipt"
import { listOrder } from "@/api/logistics/order"

const { proxy } = getCurrentInstance()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  receiptId: {
    type: Number,
    default: null
  },
  orderId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const title = computed(() => props.receiptId ? '修改回单信息' : '添加回单信息')

const orderOptions = ref([])

const data = reactive({
  form: {},
  rules: {
    orderId: [
      { required: true, message: "订单不能为空", trigger: "change" }
    ]
  }
})

const { form, rules } = toRefs(data)

function getOrderList() {
  listOrder({ pageNum: 1, pageSize: 1000, orderStatus: "transporting" }).then(response => {
    orderOptions.value = response.rows
  })
}

function reset() {
  form.value = {
    receiptId: props.receiptId || null,
    receiptNo: null,
    orderId: props.orderId || null,
    receiptDate: null,
    receiptImage: null,
    receiptStatus: "not_received",
    receiver: null,
    remark: null
  }
  if (props.orderId) {
    form.value.orderId = props.orderId
  }
}

function handleClose() {
  visible.value = false
  reset()
}

function submitForm() {
  proxy.$refs["receiptRef"].validate(valid => {
    if (valid) {
      const apiCall = form.value.receiptId ? updateReceipt : addReceipt
      apiCall(form.value).then(response => {
        proxy.$modal.msgSuccess(form.value.receiptId ? "修改成功" : "新增成功")
        visible.value = false
        emit('success')
      })
    }
  })
}

watch(() => props.modelValue, (val) => {
  if (val) {
    reset()
    getOrderList()
  }
})
</script>

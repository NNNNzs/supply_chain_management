/**
 * 直接调用后端 API 的客户端，用于测试数据准备和清理
 * 不依赖 Playwright request context，直接用 fetch 请求后端
 */
export class ApiClient {
  private token: string = '';
  private baseURL: string;

  constructor() {
    this.baseURL = process.env.API_BASE_URL || 'http://localhost:8897';
  }

  async login(username: string, password: string): Promise<void> {
    const resp = await fetch(`${this.baseURL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, code: '', uuid: '' }),
    });
    const data = await resp.json();
    if (data.code !== 200) {
      throw new Error(`API登录失败: ${data.msg || JSON.stringify(data)}`);
    }
    this.token = data.token;
  }

  private get headers(): Record<string, string> {
    return {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    };
  }

  private async request(method: string, path: string, body?: any): Promise<any> {
    const opts: RequestInit = {
      method,
      headers: this.headers,
    };
    if (body) {
      opts.body = JSON.stringify(body);
    }
    const resp = await fetch(`${this.baseURL}${path}`, opts);
    return resp.json();
  }

  // ========== 订单 ==========

  async getOrders(params: Record<string, string | number> = {}): Promise<any> {
    const query = new URLSearchParams();
    query.set('pageNum', String(params.pageNum || 1));
    query.set('pageSize', String(params.pageSize || 10));
    Object.entries(params).forEach(([k, v]) => {
      if (k !== 'pageNum' && k !== 'pageSize') query.set(k, String(v));
    });
    return this.request('GET', `/logistics/order/list?${query.toString()}`);
  }

  async getOrder(orderId: number): Promise<any> {
    return this.request('GET', `/logistics/order/${orderId}`);
  }

  async createOrder(data: Record<string, any>): Promise<any> {
    return this.request('POST', '/logistics/order', data);
  }

  async updateOrder(data: Record<string, any>): Promise<any> {
    return this.request('PUT', '/logistics/order', data);
  }

  async deleteOrder(orderId: number): Promise<any> {
    return this.request('DELETE', `/logistics/order/${orderId}`);
  }

  async changeOrderStatus(orderId: number, orderStatus: string): Promise<any> {
    return this.request('PUT', `/logistics/order/changeStatus/${orderId}`, { orderStatus });
  }

  // ========== 回单 ==========

  async getReceipts(params: Record<string, string | number> = {}): Promise<any> {
    const query = new URLSearchParams();
    query.set('pageNum', String(params.pageNum || 1));
    query.set('pageSize', String(params.pageSize || 10));
    Object.entries(params).forEach(([k, v]) => {
      if (k !== 'pageNum' && k !== 'pageSize') query.set(k, String(v));
    });
    return this.request('GET', `/logistics/receipt/list?${query.toString()}`);
  }

  async createReceipt(data: Record<string, any>): Promise<any> {
    return this.request('POST', '/logistics/receipt', data);
  }

  async deleteReceipt(receiptId: number): Promise<any> {
    return this.request('DELETE', `/logistics/receipt/${receiptId}`);
  }

  async confirmReceipt(receiptId: number, receiver: string): Promise<any> {
    return this.request('PUT', `/logistics/receipt/confirm/${receiptId}?receiver=${encodeURIComponent(receiver)}`);
  }

  // ========== 发票 ==========

  async getInvoices(params: Record<string, string | number> = {}): Promise<any> {
    const query = new URLSearchParams();
    query.set('pageNum', String(params.pageNum || 1));
    query.set('pageSize', String(params.pageSize || 10));
    Object.entries(params).forEach(([k, v]) => {
      if (k !== 'pageNum' && k !== 'pageSize') query.set(k, String(v));
    });
    return this.request('GET', `/logistics/invoice/list?${query.toString()}`);
  }

  async mergeInvoice(data: Record<string, any>): Promise<any> {
    return this.request('POST', '/logistics/invoice/merge', data);
  }

  async issueInvoice(batchId: number): Promise<any> {
    return this.request('PUT', `/logistics/invoice/issue/${batchId}`);
  }

  async cancelInvoice(batchId: number): Promise<any> {
    return this.request('PUT', `/logistics/invoice/cancel/${batchId}`);
  }

  async cancelMergeInvoice(batchId: number): Promise<any> {
    return this.request('PUT', `/logistics/invoice/cancelMerge/${batchId}`);
  }

  async deleteInvoice(batchId: number): Promise<any> {
    return this.request('DELETE', `/logistics/invoice/${batchId}`);
  }

  // ========== 基础数据 ==========

  async getCustomers(params: Record<string, string | number> = {}): Promise<any> {
    const query = new URLSearchParams();
    query.set('pageNum', String(params.pageNum || 1));
    query.set('pageSize', String(params.pageSize || 100));
    return this.request('GET', `/logistics/customer/list?${query.toString()}`);
  }

  async getGoods(params: Record<string, string | number> = {}): Promise<any> {
    const query = new URLSearchParams();
    query.set('pageNum', String(params.pageNum || 1));
    query.set('pageSize', String(params.pageSize || 100));
    return this.request('GET', `/logistics/goods/list?${query.toString()}`);
  }

  async getDriverTree(): Promise<any> {
    return this.request('GET', '/logistics/driver/treeList');
  }

  async getDrivers(): Promise<any> {
    return this.request('GET', '/logistics/driver/list');
  }
}

import { Locator, Page, expect } from '@playwright/test';

/**
 * Element Plus 组件交互辅助函数
 * Element Plus 的下拉组件（select、date-picker 等）渲染的浮层挂载到 body，
 * 不在组件 DOM 树内，需要特殊处理。
 */

/**
 * el-select 选择选项
 */
export async function selectOption(
  page: Page,
  testId: string,
  optionText: string,
): Promise<void> {
  const trigger = page.getByTestId(testId).locator('.el-input');
  await trigger.click();

  // 等待下拉选项出现
  const dropdown = page.locator('.el-select-dropdown:visible').last();
  await dropdown.waitFor({ state: 'visible' });

  // 点击目标选项
  const option = dropdown.locator('.el-select-dropdown__item').filter({ hasText: optionText }).first();
  await option.click();

  // 等待下拉关闭
  await dropdown.waitFor({ state: 'hidden' }).catch(() => {});
}

/**
 * el-select 可搜索选择器
 */
export async function filterableSelect(
  page: Page,
  testId: string,
  searchText: string,
  optionText?: string,
): Promise<void> {
  const trigger = page.getByTestId(testId).locator('.el-input');
  await trigger.click();

  // 填写搜索文字
  const input = page.getByTestId(testId).locator('input');
  await input.fill(searchText);

  // 等待过滤后的选项
  await page.waitForTimeout(300);

  const dropdown = page.locator('.el-select-dropdown:visible').last();
  await dropdown.waitFor({ state: 'visible' });

  const targetText = optionText || searchText;
  const option = dropdown.locator('.el-select-dropdown__item').filter({ hasText: targetText }).first();
  await option.click();

  await dropdown.waitFor({ state: 'hidden' }).catch(() => {});
}

/**
 * el-date-picker 选择日期
 */
export async function datePicker(
  page: Page,
  testId: string,
  dateStr: string, // 格式：YYYY-MM-DD
): Promise<void> {
  const trigger = page.getByTestId(testId).locator('input');
  await trigger.click();

  // 等待日期面板出现
  const panel = page.locator('.el-date-picker:visible, .el-date-editor:visible').last();
  await panel.waitFor({ state: 'visible' }).catch(() => {});

  // 直接在输入框中输入日期
  const input = page.getByTestId(testId).locator('input');
  await input.fill(dateStr);
  await input.press('Enter');

  await page.waitForTimeout(200);
}

/**
 * el-date-picker 选择日期范围
 */
export async function dateRangePicker(
  page: Page,
  testId: string,
  startDate: string,
  endDate: string,
): Promise<void> {
  const trigger = page.getByTestId(testId).locator('input').first();
  await trigger.click();

  const inputs = page.getByTestId(testId).locator('input');
  await inputs.nth(0).fill(startDate);
  await inputs.nth(1).fill(endDate);
  await inputs.nth(1).press('Enter');

  await page.waitForTimeout(200);
}

/**
 * el-tree-select 选择节点
 */
export async function treeSelect(
  page: Page,
  testId: string,
  nodeText: string,
): Promise<void> {
  const trigger = page.getByTestId(testId).locator('.el-input');
  await trigger.click();

  // 等待树形下拉出现
  const dropdown = page.locator('.el-tree-select__popper:visible, .el-popper:visible').last();
  await dropdown.waitFor({ state: 'visible' }).catch(() => {});
  await page.waitForTimeout(300);

  // 展开树节点并点击目标
  const treeNode = dropdown.locator('.el-tree-node').filter({ hasText: nodeText }).first();
  await treeNode.locator('.el-tree-node__content').click();

  await page.waitForTimeout(200);
}

/**
 * el-autocomplete 选择建议项
 */
export async function autocomplete(
  page: Page,
  testId: string,
  searchText: string,
): Promise<void> {
  const input = page.getByTestId(testId).locator('input');
  await input.clear();
  await input.fill(searchText);

  // 等待建议列表出现
  await page.waitForTimeout(500);

  const suggestion = page.locator('.el-autocomplete-suggestion:visible li').first();
  await suggestion.waitFor({ state: 'visible' }).catch(() => {});

  // 如果有建议项，点击第一个
  if (await suggestion.isVisible()) {
    await suggestion.click();
  } else {
    // 没有建议项，直接使用输入的文本
    await input.press('Enter');
  }
}

/**
 * 确认弹窗（ElMessageBox）
 */
export async function confirmDialog(page: Page): Promise<void> {
  const dialog = page.locator('.el-message-box');
  await dialog.waitFor({ state: 'visible' });
  await dialog.locator('button').filter({ hasText: '确定' }).click();
  await dialog.waitFor({ state: 'hidden' }).catch(() => {});
}

/**
 * 取消弹窗
 */
export async function cancelDialog(page: Page): Promise<void> {
  const dialog = page.locator('.el-message-box');
  await dialog.waitFor({ state: 'visible' });
  await dialog.locator('button').filter({ hasText: '取消' }).click();
  await dialog.waitFor({ state: 'hidden' }).catch(() => {});
}

/**
 * 等待 ElMessage 提示
 */
export async function waitForElMessage(
  page: Page,
  type: 'success' | 'error' | 'warning' = 'success',
  timeout = 5000,
): Promise<Locator> {
  const message = page.locator(`.el-message--${type}`).last();
  await message.waitFor({ state: 'visible', timeout });
  return message;
}

/**
 * 在表格中按单元格文本查找行
 */
export function tableRowByCellText(
  page: Page,
  tableTestId: string,
  cellText: string,
): Locator {
  const table = page.getByTestId(tableTestId);
  return table.locator('tbody tr').filter({ hasText: cellText }).first();
}

/**
 * 在表格中勾选指定行的复选框
 */
export async function checkTableRow(
  page: Page,
  tableTestId: string,
  cellText: string,
): Promise<void> {
  const row = tableRowByCellText(page, tableTestId, cellText);
  await row.locator('.el-checkbox').click();
}

/**
 * radio-group 选择
 */
export async function radioSelect(
  page: Page,
  testId: string,
  labelText: string,
): Promise<void> {
  const radio = page.getByTestId(testId).locator('.el-radio').filter({ hasText: labelText }).first();
  await radio.click();
}

/**
 * 填写 el-input
 */
export async function fillInput(
  page: Page,
  testId: string,
  value: string,
): Promise<void> {
  const input = page.getByTestId(testId).locator('input');
  await input.clear();
  await input.fill(value);
}

/**
 * 填写 el-input-number
 */
export async function fillInputNumber(
  page: Page,
  testId: string,
  value: number,
): Promise<void> {
  const input = page.getByTestId(testId).locator('input');
  await input.clear();
  await input.fill(String(value));
  await input.press('Tab');
}

/**
 * 填写 textarea
 */
export async function fillTextarea(
  page: Page,
  testId: string,
  value: string,
): Promise<void> {
  const textarea = page.getByTestId(testId).locator('textarea');
  await textarea.clear();
  await textarea.fill(value);
}

/**
 * 等待表格加载完成（至少一行数据或空状态）
 */
export async function waitForTableLoad(
  page: Page,
  tableTestId: string,
  timeout = 5000,
): Promise<void> {
  const table = page.getByTestId(tableTestId);
  await table.waitFor({ state: 'visible', timeout });
  // 等待表格数据渲染
  await page.waitForTimeout(500);
}

/**
 * 点击表格行内的操作按钮（通过 icon 区分）
 */
export async function clickRowAction(
  page: Page,
  tableTestId: string,
  rowText: string,
  buttonText: string,
): Promise<void> {
  const row = tableRowByCellText(page, tableTestId, rowText);
  const btn = row.locator('.el-button').filter({ hasText: buttonText }).first();
  await btn.click();
}

/**
 * 点击表格行内下拉菜单项
 */
export async function clickRowDropdownItem(
  page: Page,
  tableTestId: string,
  rowText: string,
  menuItemText: string,
): Promise<void> {
  const row = tableRowByCellText(page, tableTestId, rowText);
  // 点击下拉触发按钮
  const dropdownBtn = row.locator('.el-dropdown').locator('.el-button').first();
  await dropdownBtn.click();

  // 等待下拉菜单出现
  const dropdown = page.locator('.el-dropdown-menu:visible').last();
  await dropdown.waitFor({ state: 'visible' });

  // 点击目标菜单项
  const item = dropdown.locator('.el-dropdown-menu__item').filter({ hasText: menuItemText }).first();
  await item.click();
}

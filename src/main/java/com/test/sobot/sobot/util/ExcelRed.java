package com.test.sobot.sobot.util;

import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel表格转成json
 */
public class ExcelRed {
    private static final String EXCEL_XLS           = "xls";
    private static final String EXCEL_XLSX          = "xlsx";
    //第一种模板类型
    private static final int    HEADER_VALUE_TYPE_Z = 1;
    //第二种模板类型
    private static final int    HEADER_VALUE_TYPE_S = 2;



    /**
     * 获取一个实例
     */
    private static ExcelRed getExcel2JSONHelper() {

        return new ExcelRed();
    }

    /**
     * 文件过滤
     *
     * @throws
     * @Title: fileNameFileter
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param:
     * @return: void
     */
    private boolean fileNameFileter(File file) {
        boolean endsWith = false;
        if (file != null) {
            String fileName = file.getName();
            endsWith = fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
        }
        return endsWith;
    }

    /**
     * 获取表头行
     *
     * @throws
     * @Title: getHeaderRow
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param sheet
     * @param: @param index
     * @param: @return
     * @return: Row
     */
    private Row getHeaderRow(Sheet sheet, int index) {
        Row headerRow = null;
        if (sheet != null) {
            headerRow = sheet.getRow(index);
        }
        return headerRow;
    }

    /**
     * 获取表格中单元格的value
     *
     * @throws
     * @Title: getCellValue
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param row
     * @param: @param cellIndex
     * @param: @param formula
     * @param: @return
     * @return: Object
     */
    private Object getCellValue(Row row, int cellIndex, FormulaEvaluator formula) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                //String类型
                case Cell.CELL_TYPE_STRING:
                    return cell.getRichStringCellValue().getString();

                //number类型
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().getTime();
                    } else {
                        return cell.getNumericCellValue();
                    }
                    //boolean类型
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
                //公式
                case Cell.CELL_TYPE_FORMULA:
                    return formula.evaluate(cell).getNumberValue();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * 获取表头value
     *
     * @throws
     * @Title: getHeaderCellValue
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param headerRow
     * @param: @param cellIndex 英文表头所在的行，从0开始计算哦
     * @param: @param type 表头的类型第一种 姓名（name）英文于实体类或者数据库中的变量一致
     * @param: @return
     * @return: String
     */
    private String getHeaderCellValue(Row headerRow, int cellIndex, int type) {
        Cell cell = headerRow.getCell(cellIndex);
        String headerValue = null;
        if (cell != null) {
            //第一种模板类型
            if (type == HEADER_VALUE_TYPE_Z) {
                headerValue = cell.getRichStringCellValue().getString();
                int l_bracket = headerValue.indexOf("（");
                int r_bracket = headerValue.indexOf("）");
                if (l_bracket == -1) {
                    l_bracket = headerValue.indexOf("(");
                }
                if (r_bracket == -1) {
                    r_bracket = headerValue.indexOf(")");
                }
                headerValue = headerValue.substring(l_bracket + 1, r_bracket);
            } else if (type == HEADER_VALUE_TYPE_S) {
                //第二种模板类型
                headerValue = cell.getRichStringCellValue().getString();
            }
        }
        return headerValue;
    }

    /**
     * 读取excel表格
     *
     * @throws
     * @Title: readExcle
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param file
     * @param: @param headerIndex
     * @param: @param headType 表头的类型第一种 姓名（name）英文于实体类或者数据库中的变量一致
     */
    public JSONArray readExcle(File file, int headerIndex, int headType) {
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        if (!fileNameFileter(file)) {
            return null;
        } else {
            try {
                //加载excel表格
                WorkbookFactory wbFactory = new WorkbookFactory();
                Workbook wb = wbFactory.create(file);
                //读取第一个sheet页
                Sheet sheet = wb.getSheetAt(0);
                //读取表头行
                Row headerRow = getHeaderRow(sheet, headerIndex);
                //读取数据
                FormulaEvaluator formula = wb.getCreationHelper().createFormulaEvaluator();
                for (int r = headerIndex + 1; r <= sheet.getLastRowNum(); r++) {
                    Row dataRow = sheet.getRow(r);
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (int h = 0; h < dataRow.getLastCellNum(); h++) {
                        //表头为key
                        String key = getHeaderCellValue(headerRow, h, headType);
                        //数据为value
                        Object value = getCellValue(dataRow, h, formula);
                        if (!key.equals("") && !key.equals("null") && key != null) {
                            map.put(key, value);
                        }
                    }
                    lists.add(map);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray jsonArray = JSONArray.fromObject(lists);
        return jsonArray;
    }


}
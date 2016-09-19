package tw.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * POI3.9版本 工具类 版本01
 * 
 * @author 吕鹏
 * @date 2012-12-22
 */
public class ExcelUtil {
	static SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
	static Map<String, CellStyle> styleMap = new HashMap<String, CellStyle>(); // 存储单元格样式的Map

	public static void main(String[] args) {
		/**
		 * 读文件
		 */
		// readExcel("D:/a.xlsx");

		/**
		 * 写文件
		 */
		testWrite("D:/a.xlsx", "D:/b.xlsx");
	}

	/**
	 * 读excel
	 * 
	 * @param filePath
	 *            excel路径
	 */
	// public static void readExcel(String filePath){
	// Workbook book = null;
	// try {
	// book = getXExcelWorkbook(filePath);
	// Sheet sheet = getSheetByNum(book,1);
	// System.out.println("sheet名称是："+sheet.getSheetName());
	//
	// int lastRowNum = sheet.getLastRowNum();
	//
	// Row row = null;
	// for(int i=0;i<=lastRowNum;i++){
	// row = sheet.getRow(i);
	// if(row != null){
	// System.out.println("正在读第"+(i+1)+"行：");
	// int lastCellNum = row.getLastCellNum();
	// Cell cell = null;
	// StringBuilder sb = null;
	// for(int j=0;j<lastCellNum;j++){
	// cell = row.getCell(j);
	// if(cell != null){
	// sb = new StringBuilder("第"+(j+1)+"列的单元格内容是：");
	// String type_cn = null;
	// String type_style =
	// cell.getCellStyle().getDataFormatString().toUpperCase();
	// String type_style_cn = getCellStyleByChinese(type_style);
	// int type = cell.getCellType();
	// String value = "";
	// switch (type) {
	// case 0:
	// if(DateUtil.isCellDateFormatted(cell)){
	// type_cn = "NUMBER-DATE";
	// Date date = cell.getDateCellValue();
	// value = sFormat.format(date);
	// }else {
	// type_cn = "NUMBER";
	// double tempValue = cell.getNumericCellValue();
	// value = String.valueOf(tempValue);
	// }
	// break;
	// case 1:
	// type_cn = "STRING";
	// value = cell.getStringCellValue();
	// break;
	// case 2:
	// type_cn = "FORMULA";
	// value = cell.getCellFormula();
	// break;
	// case 3:
	// type_cn = "BLANK";
	// value = cell.getStringCellValue();
	// break;
	// case 4:
	// type_cn = "BOOLEAN";
	// boolean tempValue = cell.getBooleanCellValue();
	// value = String.valueOf(tempValue);
	// break;
	// case 5:
	// type_cn = "ERROR";
	// byte b = cell.getErrorCellValue();
	// value = String.valueOf(b);
	// default:
	// break;
	// }
	// sb.append(value + ",内容类型是："+type_cn+",单元格的格式是："+type_style_cn);
	// System.out.println(sb.toString());
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 根据单元格的格式 返回单元格的格式中文
	 * 
	 * @param type_style
	 * @return
	 */
	private static String getCellStyleByChinese(String type_style) {
		String cell_style_cn = "";
		if (type_style.contains("GENERAL")) {
			cell_style_cn = "常规";
		} else if (type_style.equals("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ")) {
			cell_style_cn = "会计专用";
		} else if (type_style.equals("0")) {
			cell_style_cn = "整数";
		} else if (type_style.contains("YYYY/MM") || type_style.contains("YYYY\\-MM")) {
			cell_style_cn = "日期";
		} else if (type_style.equals("0.00%")) {
			cell_style_cn = "百分比";
		} else {
			cell_style_cn = "不符合规定格式类型:" + type_style;
			// cell_style_cn = type_style;
		}
		return cell_style_cn;
	}

	/**
	 * 将传入的内容写入到excel中sheet里
	 * 
	 * @param list
	 */
	public static boolean writeToExcel(List<Map<String, String>> list, Sheet sheet, int startRow) {
		boolean result = false;
		try {
			Map<String, String> map = null;
			Row row = null;
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				row = sheet.getRow(startRow - 1);
				if (row == null) {
					row = sheet.createRow(startRow - 1);
				}
				startRow++;
				Cell cell = null;

				BigDecimal db = null;
				for (Map.Entry<String, String> entry : map.entrySet()) {
					String key = entry.getKey();
					int colNum = Integer.parseInt(key) - 1;

					String value_type = entry.getValue();
					String value = value_type.split(",")[0];
					String style = value_type.split(",")[1];

					cell = row.getCell(colNum);
					if (cell == null) {
						cell = row.createCell(colNum);
					}
					if (style.equals("GENERAL")) {
						cell.setCellValue(value);
					} else {
						if (style.equals("DOUBLE") || style.equals("INT")) {
							db = new BigDecimal(value, java.math.MathContext.UNLIMITED);
							cell.setCellValue(db.doubleValue());
						} else if (style.equals("PERCENT")) {
							db = new BigDecimal(value, java.math.MathContext.UNLIMITED);
							cell.setCellValue(db.doubleValue());
						} else if (style.equals("DATE")) {
							Date date = sFormat.parse(value);
							cell.setCellValue(date);
						}
						cell.setCellStyle(styleMap.get(style));
					}
				}
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}

	/**
	 * 获取excel的Workbook
	 * 
	 * @throws IOException
	 */
	public static HSSFWorkbook getHExcelWorkbook(String filePath) throws IOException {

		return getHExcelWorkbook(new File(filePath));
	}

	public static HSSFWorkbook getHExcelWorkbook(File file) throws IOException {
		HSSFWorkbook book = null;
		FileInputStream fis = null;

		if (!file.exists()) {
			throw new RuntimeException("文件不存在");
		} else {
			fis = new FileInputStream(file);
			book = new HSSFWorkbook(fis);
			initStyleMap(book);
		}
		// throw new RuntimeException(e.getMessage());
		if (fis != null) {
			fis.close();
		}
		return book;
	}

	public static XSSFWorkbook getXExcelWorkbook(String filePath) throws IOException {

		return getXExcelWorkbook(new File(filePath));
	}

	public static XSSFWorkbook getXExcelWorkbook(File file) throws IOException {
		XSSFWorkbook book = null;
		FileInputStream fis = null;

		if (!file.exists()) {
			throw new RuntimeException("文件不存在");
		} else {
			fis = new FileInputStream(file);
			book = new XSSFWorkbook(fis);
			initStyleMap(book);
		}
		// throw new RuntimeException(e.getMessage());
		if (fis != null) {
			fis.close();
		}
		return book;
	}

	/**
	 * 根据索引 返回Sheet
	 * 
	 * @param number
	 */
	public static Sheet getSheetByNum(Workbook book, int number) {
		Sheet sheet = null;
		try {
			sheet = book.getSheetAt(number - 1);
			// if(sheet == null){
			// sheet = book.createSheet("Sheet"+number);
			// }
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return sheet;
	}

	/**
	 * 初始化格式Map
	 */

	public static void initStyleMap(Workbook book) {
		DataFormat hssfDF = book.createDataFormat();

		CellStyle doubleStyle = book.createCellStyle(); // 会计专用
		doubleStyle.setDataFormat(hssfDF.getFormat("_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ")); // poi写入后为会计专用
		styleMap.put("DOUBLE", doubleStyle);

		CellStyle intStyle = book.createCellStyle(); // 会计专用
		intStyle.setDataFormat(hssfDF.getFormat("0")); // poi写入后为会计专用
		styleMap.put("INT", intStyle);

		CellStyle yyyyMMddStyle = book.createCellStyle();// 日期yyyyMMdd
		yyyyMMddStyle.setDataFormat(hssfDF.getFormat("yyyy-MM-dd"));
		styleMap.put("DATE", yyyyMMddStyle);

		CellStyle percentStyle = book.createCellStyle();// 百分比
		percentStyle.setDataFormat(hssfDF.getFormat("0.00%"));
		styleMap.put("PERCENT", percentStyle);
	}

	/**
	 * 写内容到excel中
	 * 
	 * @throws IOException
	 */
	public static void testWrite(String srcFilePath, String tarFilePath) {
		FileOutputStream out = null;
		try {
			Workbook book = getXExcelWorkbook(srcFilePath);
			Sheet sheet = getSheetByNum(book, 1);

			Map<String, String> map = new HashMap<String, String>();
			List<Map<String, String>> list = new LinkedList<Map<String, String>>();
			map.put("A", "4,INT");
			map.put("B", "小红,GENERAL");
			map.put("C", "18,INT");
			map.put("D", "1990-03-10,DATE");
			map.put("E", "0.056,PERCENT");
			map.put("F", "4800,DOUBLE");
			list.add(map);

			int startRow = 6;
			boolean result = writeToExcel(list, sheet, startRow);
			if (result) {
				out = new FileOutputStream(tarFilePath);
				book.write(out);
				System.out.println("文件写入完成！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

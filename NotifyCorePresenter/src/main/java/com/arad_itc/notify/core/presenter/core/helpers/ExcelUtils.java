package com.arad_itc.notify.core.presenter.core.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.arad_itc.notify.core.amq.domain.entities.AMQMessage;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtils {
  public static final String TAG = "ExcelUtil";
  private static Cell cell;
  private static Sheet sheet;
  private static Workbook workbook;
  private static CellStyle headerCellStyle;

  /**
   * Export Data into Excel Workbook
   *
   * @param context  - Pass the application context
   * @param fileName - Pass the desired fileName for the output excel Workbook
   * @param dataList - Contains the actual data to be displayed in excel
   */
  public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                               List<AMQMessage> dataList) {
    boolean isWorkbookWrittenIntoStorage;

    // Check if available and not read only
    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
      Log.e(TAG, "Storage not available or read only");
      return false;
    }

    // Creating a New HSSF Workbook (.xls format)
    workbook = new HSSFWorkbook();

    setHeaderCellStyle();

    // Creating a New Sheet and Setting width for each column
    sheet = workbook.createSheet("Seet-Name");
    sheet.setColumnWidth(0, (15 * 400));

    setHeaderRow();
    fillDataIntoExcel(dataList);
    isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

    return isWorkbookWrittenIntoStorage;
  }

  /**
   * Checks if Storage is READ-ONLY
   *
   * @return boolean
   */
  private static boolean isExternalStorageReadOnly() {
    String externalStorageState = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
  }

  /**
   * Checks if Storage is Available
   *
   * @return boolean
   */
  private static boolean isExternalStorageAvailable() {
    String externalStorageState = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(externalStorageState);
  }

  /**
   * Setup header cell style
   */
  private static void setHeaderCellStyle() {
    headerCellStyle = workbook.createCellStyle();
    headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
  }

  /**
   * Setup Header Row
   */
  private static void setHeaderRow() {
    Row headerRow = sheet.createRow(0);

    cell = headerRow.createCell(0);
    cell.setCellValue("   Data    ");
    cell.setCellStyle(headerCellStyle);
  }

  /**
   * Fills Data into Excel Sheet
   * <p>
   * NOTE: Set row index as i+1 since 0th index belongs to header row
   *
   * @param dataList - List containing data to be filled into excel
   */
  private static void fillDataIntoExcel(List<AMQMessage> dataList) {
    for (int i = 0; i < dataList.size(); i++) {
      // Create a New Row for every new entry in list
      Row rowData = sheet.createRow(i + 1);

      // Create Cells for each row
      cell = rowData.createCell(0);
      cell.setCellValue(dataList.get(i).getMessage().getDataForExelExport());
//      cell = rowData.createCell(1);
//      cell.setCellValue(dataList.get(i).getFromCoding().fullName);
//      cell = rowData.createCell(2);
//      cell.setCellValue(dataList.get(i).getToCoding().fullName);
//      cell = rowData.createCell(3);
//      cell.setCellValue(dataList.get(i).date.toString());
//      cell = rowData.createCell(4);
//      cell.setCellValue(UiHelper.formatCurrency(dataList.get(i).amount));

    }
  }

  /**
   * Store Excel Workbook in external storage
   *
   * @param context  - application context
   * @param fileName - name of workbook which will be stored in device
   * @return boolean - returns state whether workbook is written into storage or not
   */
  private static boolean storeExcelInStorage(Context context, String fileName) {
    boolean isSuccess;
    File file = new File(context.getExternalFilesDir(null), fileName);
    FileOutputStream fileOutputStream = null;

    try {
      fileOutputStream = new FileOutputStream(file);
      workbook.write(fileOutputStream);
      Log.e(TAG, "Writing file" + file);
      isSuccess = true;
    } catch (IOException e) {
      Log.e(TAG, "Error writing Exception: ", e);
      isSuccess = false;
    } catch (Exception e) {
      Log.e(TAG, "Failed to save file due to Exception: ", e);
      isSuccess = false;
    } finally {
      try {
        if (null != fileOutputStream) {
          fileOutputStream.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return isSuccess;
  }
}

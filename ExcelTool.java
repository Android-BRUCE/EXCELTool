
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
/**
 * @author Created by BruceZheng
 * @date 2017-11-22 12:01
 **/
    public class ExcelTool {

        public static void main(String[] args){
            //将两个excel合并
            File file = new File("C:\\Users\\Administrator.SKY-20170531RMO\\Desktop\\2018-01-17-5.xls");
            File file2 = new File("C:\\Users\\Administrator.SKY-20170531RMO\\Desktop\\121.xls");
            ArrayList<ArrayList<Object>> result = ExcelTool.readExcel(file);
            ArrayList<ArrayList<Object>> result2 = ExcelTool.readExcel(file2);
            ArrayList<ArrayList<Object>> result3 = new ArrayList<>();
            for(int i = 0 ;i < result.size() ;i++){
                for(int ii = 0 ;ii < result2.size() ;ii++){
                    //判断两个excel表格已什么条件合并为一行
                    if (result.get(i).get(1).toString().equals(result2.get(ii).get(1).toString())){
                        System.out.println(result.get(i).get(1).toString()+"||" + result2.get(ii).get(1).toString());
                        int a  = result2.get(ii).size();
                        ArrayList<Object> colList;
                        colList = new ArrayList<>();
                        for(int j = 0;j<a; j++){
                            //塞入excel表格2的ii行j列的内容
                            colList.add(result2.get(ii).get(j).toString());
                        }
                        int b  = result.get(i).size();
                        for(int j = 0;j<b; j++){
                            //塞入excel表格1的ii行j列的内容
                            colList.add(result2.get(i).get(j).toString());
                        }
                        result3.add(colList);
                    }
                }
            }
            //输出目录
            ExcelTool.writeExcel(result3,"F:/excel/bb.xls");
        }

        //默认单元格内容为数字时格式
        private static DecimalFormat df = new DecimalFormat("0");
        // 默认单元格格式化日期字符串
        private static SimpleDateFormat sdf = new SimpleDateFormat(  "yyyy-MM-dd HH:mm:ss");
        // 格式化数字
        private static DecimalFormat nf = new DecimalFormat("0.00");
        public static ArrayList<ArrayList<Object>> readExcel(File file){
            if(file == null){
                return null;
            }
            if(file.getName().endsWith("xlsx")){
                //处理ecxel2007
                return readExcel2007(file);
            }else{
                //处理ecxel2003
                return readExcel2003(file);
            }
        }
        /*
         * @return 将返回结果存储在ArrayList内，存储结构与二位数组类似
         * lists.get(0).get(0)表示过去Excel中0行0列单元格
         */
        public static ArrayList<ArrayList<Object>> readExcel2003(File file){
            try{
                ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
                ArrayList<Object> colList;
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
                HSSFSheet sheet = wb.getSheetAt(0);
                HSSFRow row;
                HSSFCell cell;
                Object value;
                for(int i = sheet.getFirstRowNum() , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
                    row = sheet.getRow(i);
                    colList = new ArrayList<Object>();
                    if(row == null){
                        //当读取行为空时
                        if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
                            rowList.add(colList);
                        }
                        continue;
                    }else{
                        rowCount++;
                    }
                    for( int j = row.getFirstCellNum() ; j <= row.getLastCellNum() ;j++){
                        cell = row.getCell(j);
                        if(cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK){
                            //当该单元格为空
                            if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
                                colList.add("");
                            }
                            continue;
                        }
                        switch(cell.getCellType()){
                            case XSSFCell.CELL_TYPE_STRING:
                                System.out.println(i + "行" + j + " 列 is String type");
                                value = cell.getStringCellValue();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                    value = df.format(cell.getNumericCellValue());
                                } else if ("General".equals(cell.getCellStyle()
                                        .getDataFormatString())) {
                                    value = nf.format(cell.getNumericCellValue());
                                } else {
                                    value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                            .getNumericCellValue()));
                                }
                                System.out.println(i + "行" + j
                                        + " 列 is Number type ; DateFormt:"
                                        + value.toString());
                                break;
                            case XSSFCell.CELL_TYPE_BOOLEAN:
                                System.out.println(i + "行" + j + " 列 is Boolean type");
                                value = Boolean.valueOf(cell.getBooleanCellValue());
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                System.out.println(i + "行" + j + " 列 is Blank type");
                                value = "";
                                break;
                            default:
                                System.out.println(i + "行" + j + " 列 is default type");
                                value = cell.toString();
                        }// end switch
                        colList.add(value);
                    }//end for j
                    rowList.add(colList);
                }//end for i

                return rowList;
            }catch(Exception e){
                return null;
            }
        }

        public static ArrayList<ArrayList<Object>> readExcel2007(File file){
            try{
                ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
                ArrayList<Object> colList;
                XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet = wb.getSheetAt(0);
                XSSFRow row;
                XSSFCell cell;
                Object value;
                for(int i = sheet.getFirstRowNum() , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
                    row = sheet.getRow(i);
                    colList = new ArrayList<Object>();
                    if(row == null){
                        //当读取行为空时
                        if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
                            rowList.add(colList);
                        }
                        continue;
                    }else{
                        rowCount++;
                    }
                    for( int j = row.getFirstCellNum() ; j <= row.getLastCellNum() ;j++){
                        cell = row.getCell(j);
                        if(cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK){
                            //当该单元格为空
                            if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
                                colList.add("");
                            }
                            continue;
                        }
                        switch(cell.getCellType()){
                            case XSSFCell.CELL_TYPE_STRING:
                                System.out.println(i + "行" + j + " 列 is String type");
                                value = cell.getStringCellValue();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                    value = df.format(cell.getNumericCellValue());
                                } else if ("General".equals(cell.getCellStyle()
                                        .getDataFormatString())) {
                                    value = nf.format(cell.getNumericCellValue());
                                } else {
                                    value = sdf.format(HSSFDateUtil.getJavaDate(cell
                                            .getNumericCellValue()));
                                }
                                System.out.println(i + "行" + j
                                        + " 列 is Number type ; DateFormt:"
                                        + value.toString());
                                break;
                            case XSSFCell.CELL_TYPE_BOOLEAN:
                                System.out.println(i + "行" + j + " 列 is Boolean type");
                                value = Boolean.valueOf(cell.getBooleanCellValue());
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                System.out.println(i + "行" + j + " 列 is Blank type");
                                value = "";
                                break;
                            default:
                                System.out.println(i + "行" + j + " 列 is default type");
                                value = cell.toString();
                        }// end switch
                        colList.add(value);
                    }//end for j
                    rowList.add(colList);
                }//end for i

                return rowList;
            }catch(Exception e){
                System.out.println("exception");
                return null;
            }
        }

        public static void writeExcel(ArrayList<ArrayList<Object>> result,String path){
            if(result == null){
                return;
            }
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet1");
            for(int i = 0 ;i < result.size() ; i++){
                HSSFRow row = sheet.createRow(i);
                if(result.get(i) != null){
                    for(int j = 0; j < result.get(i).size() ; j ++){
                        HSSFCell cell = row.createCell(j);
                        cell.setCellValue(result.get(i).get(j).toString());
                    }
                }
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try
            {
                wb.write(os);
            } catch (IOException e){
                e.printStackTrace();
            }
            byte[] content = os.toByteArray();
            File file = new File(path);//Excel文件生成后存储的位置。
            OutputStream fos  = null;
            try
            {
                fos = new FileOutputStream(file);
                fos.write(content);
                os.close();
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public static DecimalFormat getDf() {
            return df;
        }
        public static void setDf(DecimalFormat df) {
            ExcelTool.df = df;
        }
        public static SimpleDateFormat getSdf() {
            return sdf;
        }
        public static void setSdf(SimpleDateFormat sdf) {
            ExcelTool.sdf = sdf;
        }
        public static DecimalFormat getNf() {
            return nf;
        }
        public static void setNf(DecimalFormat nf) {
            ExcelTool.nf = nf;
        }
}
